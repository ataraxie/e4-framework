package de.scandio.e4.testpackages.pocketquery

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.WebClient
import org.openqa.selenium.JavascriptExecutor
import java.util.*

open class PocketQuerySeleniumHelper(
        protected val webClient: WebClient
) {

    companion object {
        fun String.trimLines() = replace("\n", "")
        val WIKIPEDIA_ARTICLE_TITLE = "Vancouver"
        val WIKIPEDIA_DATASOURCE_URL = "https://en.wikipedia.org/w/api.php"
        val WIKIPEDIA_QUERY_URL = "?action=parse&page=$WIKIPEDIA_ARTICLE_TITLE&format=json"

        val WIKIPEDIA_CONVERTER_CONTENT = """
function convert(json) {
	var parsedJsonObject = JSON.parse(json);
	return [{wikipediaContent: parsedJsonObject.parse.text['*']}];
}
            """.trimIndent().trimLines()

        val WIKIPEDIA_TEMPLATE_CONTENT = """
<link rel='stylesheet' href='https://en.wikipedia.org/w/load.php?debug=false&lang=en&modules=site.styles&only=styles&skin=vector'>
${'$'}result.get(0).wikipediaContent
            """.trimIndent().trimLines()

        val CONFLUENCE_DB_URL = System.getenv("CONFLUENCE_DB_URL")
        val CONFLUENCE_DB_NAME = "confluence"
        val CONFLUENCE_DB_USER = "confluence"
        val CONFLUENCE_DB_PWD = "confluence"

        val DATASOURCE_TYPE_REST_CUSTOM = "4"
        val DATASOURCE_TYPE_JNDI = "6"
        val SIMPLE_QUERY_SQL = "SELECT id, user_name FROM cwd_user"
    }

    val webConfluence = webClient as WebConfluence
    val dom = webConfluence.dom

    fun createSqlDatasource(name: String, url: String, user: String, pwd: String, description: String = ""): String {
        //"jdbc:mysql://localhost/world"

        var driverString: String = ""
        if (url.contains("mysql")) {
            driverString = "com.mysql.jdbc.Driver"
        }

        openAddEntityForm("database")
        val datasourceName = timestampName(name)
        insertEntityName("database", datasourceName)
        dom.insertText("#database-url", url)
        dom.click("#database-driver")
        dom.awaitHasValue("#database-driver", driverString)
        dom.insertText("#database-user", user)
        dom.insertText("#database-password", pwd)
        dom.insertText("#database-description", description)
        clickTestDatasourceConnectionAwaitSuccess()
        submitForm("database")
        return datasourceName
    }

    fun clickTestDatasourceConnectionAwaitSuccess() {
        dom.click("#pocket-databases .test-connection")
        webConfluence.awaitSuccessFlag()
    }

    fun createRestCustomDatasource(name: String, url: String, description: String = ""): String {
        openAddEntityForm("database")
        val datasourceName = timestampName(name)
        insertEntityName("database", datasourceName)
        dom.setSelectedOptionByValue("#database-type", DATASOURCE_TYPE_REST_CUSTOM)
        dom.insertText("#database-url", url)
        dom.insertText("#database-description", description)
        clickTestDatasourceConnectionAwaitSuccess()
        submitForm("database")
        return datasourceName
    }

    fun createWikipediaDatasource(name: String): String {
        return createRestCustomDatasource(name, WIKIPEDIA_DATASOURCE_URL)
    }

    fun createConfluenceDatasource(name: String): String {
        return createSqlDatasource(name, CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
    }

    fun createRestQuery(datasourceName: String, name: String, url: String, description: String = ""): String {
        openAddEntityForm("query")
        val queryName = createBaseQuery(datasourceName, name, url)
        dom.insertText("#query-description", description)
        submitForm("query")
        return queryName
    }

    fun createSqlQuery(datasourceName: String, name: String, statementOrUrl: String, description: String = ""): String {
        openAddEntityForm("query")
        val queryName = createBaseQuery(datasourceName, name, statementOrUrl)
        dom.insertText("#query-description", description)
        submitForm("query")
        return queryName
    }

    fun createBaseQuery(datasourceName: String, name: String, statementOrUrl: String): String {
        openAddEntityForm("query")
        val queryName = timestampName(name)
        insertEntityName("query", queryName)
        dom.setSelectedOptionByText("#query-database", datasourceName)
        setEditorValue(statementOrUrl)
        return queryName
    }

    fun createJndiDatasource(name: String, resourceName: String, description: String = ""): String {
        openAddEntityForm("database")
        val datasourceName = timestampName(name)
        insertEntityName("database", datasourceName)
        dom.setSelectedOptionByValue("#database-type", DATASOURCE_TYPE_JNDI)
        dom.insertText("#database-resourcename", resourceName)
        dom.insertText("#database-description", description)
        submitForm("database")
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
        dom.click("#pocket-${pluralEntityType(entityType)} li[data-displayname='$entityName']")
        dom.awaitClass("#pocket-${pluralEntityType(entityType)}", "form-visible")
    }

    fun insertEntityName(entityType: String, entityName: String) {
        dom.insertText("#$entityType-displayname", entityName)
    }

    fun pluralEntityType(entityType: String): String {
        var entityTypePlural = "${entityType}s"
        if (entityType == "query") {
            entityTypePlural = "queries"
        }
        return entityTypePlural
    }

    fun submitForm(entityType: String) {
        dom.click("#pocket-${pluralEntityType(entityType)} .nice-right button.submit")
        dom.awaitNoClass("#pocket-${pluralEntityType(entityType)}", "form-visible")
    }

    fun awaitEntityPresent(entityType: String, entityName: String) {
        dom.awaitElementPresent("#${pluralEntityType(entityType)}-list li[data-displayname='$entityName']")
    }

    fun setEditorValue(value: String) {
        val js = webClient.webDriver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue(\"$value\");", dom.findElement(".CodeMirror"))
    }

    private fun createTemplateOrConverter(entityType: String, name: String, content: String, description: String = ""): String {
        openAddEntityForm(entityType)
        val fullEntityName = timestampName(name)
        insertEntityName(entityType, fullEntityName)
        dom.executeScript("$('#$entityType-content').data('codemirror').setValue(\"$content\")")
        dom.insertText("#$entityType-description", description)
        submitForm(entityType)
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
        dom.setSelectedOptionByText("#$sourceEntityType-$targetEntityType", targetEntityName)
        submitForm(sourceEntityType)
        dom.awaitMilliseconds(50)
    }

    fun setTemplateOnQuery(queryName: String, templateName: String) {
        setLinkedEntity("query", queryName, "template", templateName)
    }

    fun setConverterOnQuery(queryName: String, converterName: String) {
        setLinkedEntity("query", queryName, "converter", converterName)
    }

    // FIXME: need to differentiate between query name and displayName here!
    fun createWikipediaSetup(description: String = ""): QuerySetup {
        val datasourceName = createRestCustomDatasource("WikipediaDS", WIKIPEDIA_DATASOURCE_URL)
        val queryName = createRestQuery(datasourceName,
                "WikipediaQuery", WIKIPEDIA_QUERY_URL, description)

        val templateName = createTemplate("WikipediaTemplate", WIKIPEDIA_TEMPLATE_CONTENT)
        val converterName = createConverter("WikipediaConverter", WIKIPEDIA_CONVERTER_CONTENT)

        setTemplateOnQuery(queryName, templateName)
        setConverterOnQuery(queryName, converterName)
        return QuerySetup(datasourceName, queryName, templateName, converterName)
    }

    fun createSqlSetup(dbName: String, dbUrl: String, dbUser: String, dbPassword: String, description: String = ""): String {
        val confluenceDatasourceName = createSqlDatasource(dbName, dbUrl, dbUser, dbPassword)
        return createSqlQuery(
                confluenceDatasourceName,
                "SelectUsers",
                SIMPLE_QUERY_SQL, description)
    }

    fun timestampName(name: String): String {
        return "${name} (${Date().time})"
    }

    inner class QuerySetup (
            val datasourceName: String,
            val queryName: String,
            val templateName: String,
            val converterName: String
    )

}