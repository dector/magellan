package com.gdxjam.magellan.drones

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.gameobj.IInteractable

/**
 * Created by saibotd on 27.12.15.
 */
open class DroneRoutine(
        @JvmField var drone: Drone) {

    @JvmField
    var powerLevel = 0f

    @JvmField
    var sprite = Sprite()

    @JvmField
    var windowSprite = Sprite()

    @JvmField
    var routine: ROUTINES? = null

    enum class ROUTINES {
        MINING, SCOUTING, ATTACKING, ADVSCOUTING, FOLLOWING, REPAIRING
    }

    init {
        drone.addRoutine(this)
    }

    open fun tick() {}

    fun receiveDamage(damage: Int) {}

    fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        return OrderedMap()
    }

    fun shootAt(target: IDestroyable) {}

    fun render(batch: SpriteBatch, delta: Float) {
        sprite.apply {
            setOriginCenter()
            setPosition(drone.spriteVessel.x, drone.spriteVessel.y)
            rotation = drone.spriteVessel.rotation
            setSize(drone.spriteVessel.width, drone.spriteVessel.height)
            draw(batch)
        }
    }
}
