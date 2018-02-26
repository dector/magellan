package com.gdxjam.magellan.drones

import com.badlogic.gdx.graphics.g2d.Sprite
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.utils.texture

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineFollowing(drone: Drone) : DroneRoutine(drone) {

    init {
        routine = DroneRoutine.ROUTINES.FOLLOWING
        sprite = Sprite(MagellanGame.assets.texture("drone_thruster.png"))
        windowSprite = Sprite(MagellanGame.assets.texture("sectorview_drone_thruster.png"))
    }

    override fun tick() {
        if (MagellanGame.instance.universe.playerShip.sector != drone.sector) {
            drone.moveTo(MagellanGame.instance.universe.playerShip.sector)
        }
    }
}
