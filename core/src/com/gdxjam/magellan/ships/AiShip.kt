package com.gdxjam.magellan.ships

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.models.Sector

/**
 * Created by lolcorner on 20.12.2015.
 */
open class AiShip(sector: Sector) : Ship(sector) {

    var target: IDestroyable? = null

    override val title: String
        get() = if (MagellanGame.gameState.aiHostility < 3) {
            "STRANGE SHIP"
        } else "ENEMY SHIP"

    @JvmField
    var state = States.IDLE

    init {
        faction = GameObj.Factions.SAATOO
    }

    override fun passiveTick() {
        moveTo(sector.connectedSectors.random())
    }

    override fun activeTick() {
        if (sector == MagellanGame.instance.universe.playerShip.sector) {
            when (MagellanGame.gameState.aiHostility) {
                0 -> {
                    MagellanGame.instance.showWindowScreen()
                    MagellanGame.instance.windowScreen.getWindow("Communication", "Who are you?\nAre you one of us?")
                    MagellanGame.gameState.aiHostility++
                }
                1 -> {
                    MagellanGame.instance.showWindowScreen()
                    MagellanGame.instance.windowScreen.getWindow("Communication", "Saatoo knows about you and your plans.\nWe don't like it.\nSTAY AWAY FROM SAATOO!")
                    MagellanGame.gameState.aiHostility++
                }
                2 -> {
                    MagellanGame.instance.showWindowScreen()
                    MagellanGame.instance.windowScreen.getWindow("Communication", "This is your final warning!\nHumanity doesn't deserve a second chance.\nRETREAT OR GET CRUSHED BY SAATOO!")
                    MagellanGame.gameState.aiHostility = 5
                }
            }
        }
    }

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        super.render(delta)
        spriteVessel.draw(batch)
    }
}

enum class States {
    IDLE, HOSTILE, FLEEING
}
