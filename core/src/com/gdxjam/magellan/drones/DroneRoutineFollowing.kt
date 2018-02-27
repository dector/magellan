package com.gdxjam.magellan.drones

import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineFollowing(drone: Drone) : DroneRoutine(drone) {

    init {
        routine = DroneRoutine.ROUTINES.FOLLOWING
        sprite = MagellanGame.assets.textureSprite("drone_thruster.png")
        windowSprite = MagellanGame.assets.textureSprite("sectorview_drone_thruster.png")
    }

    override fun tick() {
        if (MagellanGame.instance.universe.playerShip.sector != drone.sector) {
            drone.moveTo(MagellanGame.instance.universe.playerShip.sector)
        }
    }
}
