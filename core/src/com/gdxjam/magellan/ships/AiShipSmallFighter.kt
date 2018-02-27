package com.gdxjam.magellan.ships

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.gdxjam.magellan.Battle
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.Sector
import com.gdxjam.magellan.drones.Drone
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.gameobj.Planet
import com.gdxjam.magellan.utils.texture

/**
 * Created by lolcorner on 20.12.2015.
 */
class AiShipSmallFighter(sector: Sector) : AiShip(sector) {

    override val actor: Actor
        get() {
            val image = Image(MagellanGame.assets.texture("sectorview_enemy_drone.png"))
            val imageShield = Image(MagellanGame.assets.texture("sectorview_enemy_fighter_shield.png"))
            imageShield.setColor(1f, 1f, 1f, 0f)

            val stack = Stack()
            stack.setSize(150f, 124f)
            stack.userObject = this
            stack.addActor(image)
            stack.addActor(imageShield)

            return stack
        }

    init {
        health = 6
        attack = 1
        shield = 0.0f
        faction = GameObj.Factions.SAATOO
    }

    override fun prepareRenderingOnMap() {
        super.prepareRenderingOnMap()

        spriteVessel = Sprite(MagellanGame.assets.texture("enemy_drone.png"))
        spriteVessel.setSize(6f, 8f)
        spriteVessel.setOriginCenter()

        getFreeSectorSlot()
        getParkingPosition()

        spriteVessel.setPosition(parkingPosition.x, parkingPosition.y)
        spriteVessel.rotation = parkingAngle
    }

    private fun decideState() {
        target = null

        for (gameObj in sector.gameObjs) {
            if (gameObj is IDestroyable && gameObj.faction === GameObj.Factions.PLAYER) {
                if (gameObj is PlayerShip) {
                    if (Math.random() < .7) {
                        target = gameObj
                    }
                    break
                } else {
                    if (Math.random() < .7) {
                        target = gameObj
                    }
                }
            }
        }

        if (target != null && target!!.isAlive && MagellanGame.gameState.aiHostility >= 5) {
            state = States.HOSTILE
            return
        }

        state = States.IDLE
    }

    override fun passiveTick() {
        decideState()

        when (state) {
            States.IDLE -> if (Math.random() < .5) super.passiveTick()
            States.FLEEING -> super.passiveTick()
        }
    }

    override fun activeTick() {
        super.activeTick()

        decideState()

        if (state == States.HOSTILE) {
            if (target is Drone && (target as Drone).faction === GameObj.Factions.PLAYER) {
                MagellanGame.instance.mapScreen.log.addEntry("Your drone is under attack!", sector)
            }

            if (target is Planet && (target as Planet).faction === GameObj.Factions.PLAYER) {
                MagellanGame.instance.mapScreen.log.addEntry("Your planet is under attack!", sector)
            }

            Battle(this, target)
        }
    }
}
