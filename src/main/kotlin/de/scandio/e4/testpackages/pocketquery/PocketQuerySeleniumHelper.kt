package de.scandio.e4.testpackages.pocketquery

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.interfaces.WebClient
import org.openqa.selenium.JavascriptExecutor
import java.util.*

abstract class PocketQuerySeleniumHelper(
        protected val webClient: WebClient,
        protected val dom: DomHelper
) {

    val DATASOURCE_TYPE_REST_CUSTOM = "4"
    val DATASOURCE_TYPE_JNDI = "6"
    val SIMPLE_QUERY_SQL = "SELECT id, user_name FROM cwd_user"
    val WIKIPEDIA_REST_URL = "https://en.wikipedia.org/w/api.php"

    fun createSqlDatasource(name: String, url: String, user: String, pwd: String, description: String = ""): String {
        //"jdbc:mysql://localhost/world"

        var driverString: String = ""
        if (url.contains("mysql")) {
            driverString = "com.mysql.jdbc.Driver"
        }

        openAddEntityForm("database")
        val datasourceName = "${name}_${Date().time}"
        insertEntityName("database", datasourceName)
        dom.insertText("#database-url", url)
        dom.click("#database-driver")
        dom.awaitHasValue("#database-driver", driverString)
        dom.insertText("#database-user", user)
        dom.insertText("#database-password", pwd)
        dom.insertText("#database-description", description)
        dom.click("#pocket-databases a.testconnection")
        dom.awaitHasText("#pocket-databases .nice-right .nice-status", "success")
        submitForm("database", datasourceName)
        return datasourceName
    }


    fun createRestCustomDatasource(name: String, url: String, description: String = ""): String {
        goToAdminSection("database")
        openAddEntityForm("database")
        val datasourceName = "${name}_${Date().time}"
        insertEntityName("database", datasourceName)
        dom.setSelectedOption("#database-type", DATASOURCE_TYPE_REST_CUSTOM)
        dom.insertText("#database-url", url)
        dom.insertText("#database-description", description)
        dom.click("#pocket-databases a.testconnection")
        dom.awaitHasText("#pocket-databases .nice-right .nice-status", "success")
        submitForm("database", datasourceName)
        return datasourceName
    }

    fun createWikipediaDatasource(name: String): String {
        return createRestCustomDatasource(name, WIKIPEDIA_REST_URL)
    }

    fun createRestQuery(datasourceName: String, name: String, url: String, jsonPath: String = "", description: String = ""): String {
        goToAdminSection("query")
        openAddEntityForm("query")
        val queryName = createBaseSqlQuery(datasourceName, name, url)
        dom.insertText("#query-jsonpath", jsonPath)
        dom.insertText("#query-description", description)
        submitForm("query", queryName)
        return queryName
    }

    fun createSqlQuery(datasourceName: String, name: String, statementOrUrl: String, description: String = ""): String {
        goToAdminSection("query")
        openAddEntityForm("query")
        val queryName = createBaseSqlQuery(datasourceName, name, statementOrUrl)
        dom.insertText("#query-description", description)
        submitForm("query", queryName)
        return queryName
    }

    fun createBaseSqlQuery(datasourceName: String, name: String, statementOrUrl: String): String {
        goToAdminSection("query")
        openAddEntityForm("query")
        val queryName = "${name}_${Date().time}"
        insertEntityName("query", queryName)
        dom.setSelectedOption("#query-database", datasourceName)
        setEditorValue(statementOrUrl)
        return queryName
    }

    fun createJndiDatasource(name: String, resourceName: String, description: String = ""): String {
        goToAdminSection("database")
        openAddEntityForm("database")
        val datasourceName = "${name}_${Date().time}"
        insertEntityName("database", datasourceName)
        dom.setSelectedOption("#database-type", DATASOURCE_TYPE_JNDI)
        dom.insertText("#database-resourcename", resourceName)
        dom.insertText("#database-description", description)
        submitForm("database", datasourceName)
        return datasourceName
    }

    fun goToPocketQueryAdmin() {
        webClient.navigateTo("plugins/servlet/pocketquery/admin")
        dom.awaitElementPresent("#pocket-admin")
    }

    fun goToAdminSection(entityType: String) {
        if (!webClient.webDriver.currentUrl.contains("pocketquery/admin")) {
            goToPocketQueryAdmin()
        }
        dom.click("li[data-section='$entityType'] a")
        dom.awaitClass("#pocket-${pluralEntityType(entityType)}", "active")
    }

    fun openAddEntityForm(entityType: String) {
        goToAdminSection(entityType)
        dom.click("#pocket-${pluralEntityType(entityType)} .nice-add-entity")
        dom.awaitClass("#pocket-${pluralEntityType(entityType)}", "form-visible")
    }

    fun openEditEntityForm(entityType: String, entityName: String) {
        goToAdminSection(entityType)
        dom.click("#pocket-${pluralEntityType(entityType)} li[data-entityname='$entityName']")
        dom.awaitClass("#pocket-${pluralEntityType(entityType)}", "form-visible")
    }

    fun insertEntityName(entityType: String, entityName: String) {
        dom.insertText("#$entityType-name", entityName)
    }

    fun pluralEntityType(entityType: String): String {
        var entityTypePlural = "${entityType}s"
        if (entityType == "query") {
            entityTypePlural = "queries"
        }
        return entityTypePlural
    }

    fun submitForm(entityType: String, entityName: String) {
        dom.click("#pocket-${pluralEntityType(entityType)} .nice-right input.submit")
        dom.awaitNoClass("#pocket-${pluralEntityType(entityType)}", "form-visible")
    }

    fun awaitEntityPresent(entityType: String, entityName: String) {
        dom.awaitElementPresent("#${pluralEntityType(entityType)}-list li[data-entityname='$entityName']")
    }

    fun setEditorValue(value: String) {
        val js = webClient.webDriver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue(\"$value\");", dom.findElement(".CodeMirror"))
    }

    private fun createTemplateOrConverter(entityType: String, name: String, content: String, description: String = ""): String {
        goToAdminSection(entityType)
        openAddEntityForm(entityType)
        val fullEntityName = "${name}_${Date().time}"
        insertEntityName(entityType, fullEntityName)
        dom.executeScript("$('#$entityType-content').data('codemirror').setValue(\"$content\")")
        dom.insertText("#$entityType-description", description)
        submitForm(entityType, fullEntityName)
        return fullEntityName
    }

    fun createTemplate(name: String, content: String, description: String = ""): String {
        return createTemplateOrConverter("template", name, content, description)
    }

    fun createConverter(name: String, content: String, description: String = ""): String {
        return createTemplateOrConverter("converter", name, content, description)
    }

    private fun setLinkedEntity(sourceEntityType: String, sourceEntityName: String, targetEntityType: String, targetEntityName: String) {
        goToAdminSection(sourceEntityType)
        openEditEntityForm(sourceEntityType, sourceEntityName)
        dom.setSelectedOption("#$sourceEntityType-$targetEntityType", targetEntityName)
        submitForm(sourceEntityType, sourceEntityName)
        dom.awaitMilliseconds(50)
    }

    fun setTemplateOnQuery(queryName: String, templateName: String) {
        setLinkedEntity("query", queryName, "template", templateName)
    }

    fun setConverterOnQuery(queryName: String, converterName: String) {
        setLinkedEntity("query", queryName, "converter", converterName)
    }

    fun setSelect2Option(selector: String, value: String) {
        dom.executeScript("$('$selector').val('$value').trigger('change')")
        dom.awaitMilliseconds(50)
    }

    fun createWikipediaSetup(description: String = ""): String {
        val wikipediaTitle = "Vancouver"
        val wikipediaQuery = "?action=parse&page=$wikipediaTitle&format=json"
        val wikipediaJsonPath = "$.parse.text['*']"
        val wikipediaConverter = """
            function convert(json) {
                var parsedJsonObject = JSON.parse(json);
                return [{ 'wikipediaContent': parsedJsonObject }];
            }
        """.trimIndent().trimLines()
        val innerTemplate = """
                <link rel='stylesheet' href='https://en.wikipedia.org/w/load.php?debug=false&lang=en&modules=site.styles&only=styles&skin=vector'>
                ${'$'}result.get(0).wikipediaContent
        """.trimIndent().trimLines()

        val wikipediaDatasourceName = createRestCustomDatasource("WikipediaDS", WIKIPEDIA_REST_URL)
        val wikipediaQueryName = createRestQuery(wikipediaDatasourceName,
                "WikipediaQuery", wikipediaQuery, wikipediaJsonPath)

        val wikipediaTemplateName = createTemplate("WikipediaTemplate", innerTemplate)
        val wikipediaConverterName = createConverter("WikipediaConverter", wikipediaConverter)

        setTemplateOnQuery(wikipediaQueryName, wikipediaTemplateName)
        setConverterOnQuery(wikipediaQueryName, wikipediaConverterName)
        return wikipediaQueryName
    }

    fun createSqlSetup(dbName: String, dbUrl: String, dbUser: String, dbPassword: String, description: String = ""): String {
        val confluenceDatasourceName = createSqlDatasource(dbName, dbUrl, dbUser, dbPassword)
        return createSqlQuery(
                confluenceDatasourceName,
                "SelectUsers",
                SIMPLE_QUERY_SQL, description)
    }

    fun String.trimLines() = replace("\n", "")

}