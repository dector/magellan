package com.gdxjam.magellan.models

import com.badlogic.gdx.math.MathUtils
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.drones.DroneRoutine
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.Planet
import com.gdxjam.magellan.utils.gdxArrayOf

/**
 * Created by Felix on 23.12.2015.
 */
data class GameState(private val game: MagellanGame) {

    var drones = 0

    var resource1 = 0
    var resource2 = 0
    var resource3 = 0

    var credits = 0
    var creditsPerTick = 0

    var year = 3056
    var yearsPassed = 0

    var population = 0
    var aiHostility = 0

    var unlockedRoutines = gdxArrayOf(
        DroneRoutine.ROUTINES.MINING,
        DroneRoutine.ROUTINES.SCOUTING,
        DroneRoutine.ROUTINES.REPAIRING
    )

    var mapTutorialShown = false
    var startTutorialShown = false
    var droneTutorialShown = false

    fun progressYear() {
        year++
        yearsPassed++
        updateNumberOfDrones()
    }

    fun updateNumberOfDrones() {
        drones = game.universe.playerShip.drones.size
    }

    fun getPlanetIncome() {
        creditsPerTick = game.universe.getGameObjs(Planet::class.java)
                .map { it as Planet }
                .filter { it.faction == GameObj.Factions.PLAYER }
                .sumBy { it.creditsByTick() }
        credits += creditsPerTick
    }

    fun updatePopulationCount() {
        population = game.universe.getGameObjs(Planet::class.java)
                .map { it as Planet }
                .filter { it.faction == GameObj.Factions.PLAYER }
                .sumBy { it.population }
    }

    // SPEND RESOURCE AND RETURN HOW MANY CAN BE SPENT
    fun spendResource(resourcetype: Int, amount: Int): Int {
        var availableAmount = amount

        when (resourcetype) {
            1 -> {
                availableAmount = MathUtils.clamp(availableAmount, 0, resource1)
                resource1 -= availableAmount
            }
            2 -> {
                availableAmount = MathUtils.clamp(availableAmount, 0, resource2)
                resource2 -= availableAmount
            }
            3 -> {
                availableAmount = MathUtils.clamp(availableAmount, 0, resource3)
                resource3 -= availableAmount
            }
        }

        return availableAmount
    }
}
