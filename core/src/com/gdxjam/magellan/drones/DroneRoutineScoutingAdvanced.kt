package com.gdxjam.magellan.drones

import com.badlogic.gdx.utils.Array
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineScoutingAdvanced(drone: Drone) : DroneRoutine(drone) {

    private var wait = 5

    init {
        routine = DroneRoutine.ROUTINES.ADVSCOUTING
        sprite = MagellanGame.assets.textureSprite("drone_thruster.png")
        windowSprite = MagellanGame.assets.textureSprite("sectorview_drone_thruster.png")
    }

    override fun tick() {
        wait -= powerLevel.toInt()

        if (wait <= 0) {
            val sector: Sector
            val preferredSectors = Array<Sector>()

            drone.sector.connectedSectors
                    .filter { it.position.x > drone.sector.position.x || it.position.y > drone.sector.position.y }
                    .forEach { preferredSectors.add(it) }

            sector = if (preferredSectors.size == 0) {
                drone.sector.connectedSectors.random()
            } else {
                preferredSectors.random()
            }
            sector.discovered = true
            sector.visited = true

            sector.connectedSectors.forEach { _sector ->
                _sector.connectedSectors.forEach { __sector -> __sector.discovered = true }
                _sector.discovered = true
                _sector.visited = true
            }

            drone.moveTo(sector)
            wait = 5
        }
    }
}