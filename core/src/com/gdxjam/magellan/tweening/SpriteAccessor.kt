package com.gdxjam.magellan.tweening

import aurelienribon.tweenengine.TweenAccessor
import com.badlogic.gdx.graphics.g2d.Sprite

/**
 * Created by Felix on 25.12.2015.
 */
class SpriteAccessor : TweenAccessor<Sprite> {

    companion object {

        const val POSITION_X = 1
        const val POSITION_Y = 2
        const val POSITION_XY = 3
        const val ROTATION = 4
    }

    override fun getValues(target: Sprite, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            POSITION_X -> {
                returnValues[0] = target.x
                return 1
            }
            POSITION_Y -> {
                returnValues[0] = target.y
                return 1
            }
            POSITION_XY -> {
                returnValues[0] = target.x
                returnValues[1] = target.y
                return 2
            }
            ROTATION -> {
                returnValues[0] = target.rotation
                return 1
            }
            else -> {
                assert(false)
                return -1
            }
        }
    }

    override fun setValues(target: Sprite, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            POSITION_X -> target.x = newValues[0]
            POSITION_Y -> target.y = newValues[0]
            POSITION_XY -> target.setPosition(newValues[0], newValues[1])
            ROTATION -> target.rotation = newValues[0]

            else -> assert(false)
        }
    }
}
