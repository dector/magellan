package com.gdxjam.magellan.ships

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.battle.Battle
import com.gdxjam.magellan.gameobj.*
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.utils.texture

/**
 * Created by lolcorner on 19.12.2015.
 */
open class Ship(sector: Sector) : MovingGameObj(sector), IDrawableMap, IDrawableWindow, IDestroyable, IArmed {

    override var shield = 0f
    override var health = 3
    override var attack = 1

    private var battle: Battle? = null

    override val isAlive: Boolean
        get() = health > 0

    override val title = "SHIP"

    override val info: String
        get() = """
            Faction: $faction
            Health: $health
            Attack: $attack
            Shield: ${Math.round(shield * 100)}%
        """.trimIndent()

    override val actor: Actor
        get() = Image(MagellanGame.assets.texture("map_playership.png")).apply {
            userObject = this@Ship
        }

    override fun shootAt(target: IDestroyable) = if (target.receiveDamage(attack)) {
        attack
    } else {
        -1
    }

    override fun receiveDamage(damage: Int): Boolean {
        if (Math.random() < shield)
            return false

        health -= damage

        if (health <= 0) destroy()
        return true
    }

    override fun destroy() {
        dispose()
    }

    override fun dispose() {
        this.sector.gameObjs.removeValue(this, true)
    }

    override fun inBattle() = battle != null

    override fun setBattle(battle: Battle?) {
        this.battle = battle
    }

    override fun prepareRenderingOnMap() {}

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {}
}
