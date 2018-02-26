package com.gdxjam.magellan.drones

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.gdxjam.magellan.Battle
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.ships.AiShip
import com.gdxjam.magellan.utils.texture

/**
 * Created by saibotd on 27.12.15.
 */
class DroneRoutineFighting(
        drone: Drone) : DroneRoutine(drone) {

    private val attack = 1

    init {
        routine = DroneRoutine.ROUTINES.ATTACKING
        sprite = Sprite(MagellanGame.assets.texture("drone_gun.png"))
        windowSprite = Sprite(MagellanGame.assets.texture("sectorview_drone_gun.png"))
    }

    override fun tick() {
        for (gameObj in drone.sector.gameObjs) {
            if (gameObj is AiShip) {
                Battle(drone, gameObj as IDestroyable)

                if (drone.faction === GameObj.Factions.PLAYER) {
                    MagellanGame.instance.mapScreen.log.addEntry("Attack drone is engaging an enemy!", drone.sector)
                }
            }
        }
    }

    fun getAttack(): Int
            = MathUtils.clamp(attack * powerLevel, 1f, 5f).toInt()
}
