package com.gdxjam.magellan.drones

import com.badlogic.gdx.graphics.g2d.Sprite
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.utils.texture

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineScouting(drone: Drone) : DroneRoutine(drone) {

    private var wait = 5

    init {
        routine = DroneRoutine.ROUTINES.SCOUTING
        sprite = Sprite(MagellanGame.assets.texture("drone_thruster.png"))
        windowSprite = Sprite(MagellanGame.assets.texture("sectorview_drone_thruster.png"))
    }

    override fun tick() {
        wait -= powerLevel.toInt()

        if (wait <= 0) {
            val sector = drone.sector.connectedSectors.random()

            sector.discovered = true
            sector.visited = true

            for (_sector in sector.connectedSectors) {
                for (__sector in _sector.connectedSectors) {
                    __sector.discovered = true
                }

                _sector.discovered = true
                _sector.visited = true
            }

            drone.moveTo(sector)
            wait = 5
        }
    }
}
