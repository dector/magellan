package com.gdxjam.magellan.gameobj

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.battle.Battle


/**
 * Originally created by lolcorner on 19-20.12.2015.
 */

interface IArmed {
    val attack: Int

    fun shootAt(target: IDestroyable): Int
}

interface IDestroyable : Disposable {
    val isAlive: Boolean
    val health: Int
    val shield: Float

    fun receiveDamage(damage: Int): Boolean
    fun destroy()

    override fun dispose()

    fun inBattle(): Boolean

    fun setBattle(battle: Battle?)
}

interface IDrawableMap {
    fun prepareRenderingOnMap()
    fun renderOnMap(batch: SpriteBatch, delta: Float)
}

interface IDrawableWindow {
    val title: String
    val info: String
    val actor: Actor
}

interface IInteractable : IDrawableWindow {
    fun getInteractions(with: GameObj): OrderedMap<String, Interaction>

    interface Interaction {
        fun interact()
    }

    interface InputInteraction : Interaction

    interface SliderInteraction : Interaction
}