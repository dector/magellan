package com.gdxjam.magellan.drones

import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineRepairing(drone: Drone) : DroneRoutine(drone) {

    init {
        routine = DroneRoutine.ROUTINES.REPAIRING
        sprite = MagellanGame.assets.textureSprite("drone_mine.png")
        windowSprite = MagellanGame.assets.textureSprite("sectorview_drone_mine.png")
    }

    override fun tick() {
        if (MagellanGame.instance.universe.playerShip.sector == drone.sector) {
            MagellanGame.instance.universe.playerShip.heal(powerLevel)
        }
    }
}
