package com.gdxjam.magellan.ships

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.battle.Battle
import com.gdxjam.magellan.drones.Drone
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.gameobj.Planet
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.utils.texture
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by lolcorner on 20.12.2015.
 */
class AiShipSettler(sector: Sector) : AiShip(sector) {

    override val actor: Actor
        get() {
            val image = Image(MagellanGame.assets.texture("sectorview_enemy_transporter.png"))
            val imageShield = Image(MagellanGame.assets.texture("sectorview_enemy_transporter_shield.png"))
            imageShield.setColor(1f, 1f, 1f, 0f)

            val stack = Stack()
            stack.setSize(500f, 370f)
            stack.userObject = this
            stack.addActor(image)
            stack.addActor(imageShield)

            return stack
        }

    init {
        health = 10
        attack = 2
        shield = 0.3f
        faction = GameObj.Factions.SAATOO
    }

    override fun prepareRenderingOnMap() {
        super.prepareRenderingOnMap()

        spriteVessel = MagellanGame.assets.textureSprite("enemy_transport.png")
        spriteVessel.setSize(20f, 20f)
        spriteVessel.setOriginCenter()

        getFreeSectorSlot()
        getParkingPosition()

        spriteVessel.setPosition(parkingPosition.x, parkingPosition.y)
        spriteVessel.rotation = parkingAngle
    }

    private fun decideState() {
        target = null

        for (i in 0 until sector.gameObjs.size) {
            val gameObj = sector.gameObjs.get(i)
            if (gameObj is IDestroyable && gameObj.faction === GameObj.Factions.PLAYER) {
                if (gameObj is PlayerShip) {
                    if (Math.random() < .3) {
                        target = gameObj
                    }
                    break
                } else {
                    if (Math.random() < .3) {
                        target = gameObj
                    }
                }
            }

            if (gameObj is Planet && gameObj.faction == GameObj.Factions.NEUTRAL) {
                gameObj.claim(this)
                gameObj.resource1 += MathUtils.random(100, 200)
                gameObj.resource2 += MathUtils.random(100, 200)
                gameObj.resource3 += MathUtils.random(100, 200)
                gameObj.populate(this, MathUtils.random(500, 1500))
            }
        }

        if (target != null && target!!.isAlive && MagellanGame.gameState.aiHostility >= 5) {
            state = States.FLEEING

            if (Math.random() < .2) {
                state = States.HOSTILE
            }
            return
        }

        state = States.IDLE
    }

    override fun passiveTick() {
        decideState()

        when (state) {
            States.IDLE -> if (Math.random() < .5) super.passiveTick()
            States.FLEEING -> super.passiveTick()
            else -> {}
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
