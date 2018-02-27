package com.gdxjam.magellan.gameobj

import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.models.Sector

/**
 * Created by lolcorner on 19.12.2015.
 */
abstract class GameObj(@JvmField var sector: Sector) {

    @JvmField
    var faction = Factions.NEUTRAL
    @JvmField
    var submenuOpen = ""

    enum class Factions {
        NEUTRAL, PLAYER, SAATOO, PIRATE
    }

    init {
        sector.gameObjs.add(this)
    }

    open fun passiveTick() {}
    open fun activeTick() {}
    open fun render(deltaTime: Float) {}

    fun closeWindow() {
        MagellanGame.instance.windowScreen.closeWindow()
    }

    fun showInteractionWindow() {
        closeWindow()
        MagellanGame.instance.windowScreen.showInteractionWindow(this as IDrawableWindow)
    }
}
