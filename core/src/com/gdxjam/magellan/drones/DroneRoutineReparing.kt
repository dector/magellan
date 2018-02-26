package com.gdxjam.magellan.drones

import com.badlogic.gdx.graphics.g2d.Sprite
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.utils.texture

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineReparing(drone: Drone) : DroneRoutine(drone) {

    init {
        routine = DroneRoutine.ROUTINES.REPAIRING
        sprite = Sprite(MagellanGame.assets.texture("drone_mine.png"))
        windowSprite = Sprite(MagellanGame.assets.texture("sectorview_drone_mine.png"))
    }

    override fun tick() {
        if (MagellanGame.instance.universe.playerShip.sector == drone.sector) {
            MagellanGame.instance.universe.playerShip.heal(powerLevel)
        }
    }
}
