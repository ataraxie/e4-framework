package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.scenarios.CreatePageScenario
import de.scandio.e4.testpackages.vanilla.scenarios.CreateSpaceScenario
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualCreator : VirtualUser {

    override fun getScenarios(): MutableList<Scenario> {
        val list = arrayListOf<Scenario>()
        val spaceKey = "E4${getRandomNumber(1, 999)}"
        val spaceName = "E4 Space ${getRandomNumber(1, 999)}"
        var pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
        list.add(CreateSpaceScenario(spaceKey, spaceName))
        list.add(CreatePageScenario(spaceKey, pageTitle))
        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
        list.add(CreatePageScenario(spaceKey, pageTitle))
        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
        list.add(CreatePageScenario(spaceKey, pageTitle))
        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
        list.add(CreatePageScenario(spaceKey, pageTitle))
        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
        list.add(CreatePageScenario(spaceKey, pageTitle))
        return list
    }

    fun getRandomNumber(min: Int, max: Int): Double {
        return Math.floor(Math.random() * (max - min + 1) + min)
    }

}