package com.gdxjam.magellan.drones

import com.badlogic.gdx.utils.Array
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.MeteoroidField
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineMining(drone: Drone) : DroneRoutine(drone) {

    private val resourcesPerTick = 2
    private var noResMessageShown = false

    init {
        routine = DroneRoutine.ROUTINES.MINING
        sprite = MagellanGame.assets.textureSprite("drone_mine.png")
        windowSprite = MagellanGame.assets.textureSprite("sectorview_drone_mine.png")
    }

    override fun tick() {
        val metroidFields = Array<MeteoroidField>()

        drone.sector.gameObjs
                .filterIsInstance<MeteoroidField>()
                .forEach { metroidFields.add(it) }

        var resCounter = 0

        metroidFields.forEach { meteoroidField ->
            when (meteoroidField.resource) {
                1 -> {
                    MagellanGame.gameState.resource1 += meteoroidField.mine(Math.round(resourcesPerTick * powerLevel))
                    resCounter += meteoroidField.resourceAmount
                }
                2 -> {
                    MagellanGame.gameState.resource2 += meteoroidField.mine(Math.round(resourcesPerTick * powerLevel))
                    resCounter += meteoroidField.resourceAmount
                }
                3 -> {
                    MagellanGame.gameState.resource3 += meteoroidField.mine(Math.round(resourcesPerTick * powerLevel))
                    resCounter += meteoroidField.resourceAmount
                }
            }
        }

        if (drone.faction == GameObj.Factions.PLAYER && resCounter <= 0 && !noResMessageShown) {
            noResMessageShown = true
            MagellanGame.instance.mapScreen.log.addEntry("Mining drone has no resources left", drone.sector)
        }
    }
}
