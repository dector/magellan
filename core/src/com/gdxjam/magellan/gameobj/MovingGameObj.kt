package com.gdxjam.magellan.gameobj

import aurelienribon.tweenengine.Timeline
import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenEquations
import aurelienribon.tweenengine.TweenManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.tweening.SpriteAccessor


/**
 * Created by lolcorner on 19.12.2015.
 */
open class MovingGameObj(sector: Sector) : GameObj(sector) {

    private var lastSector: Sector? = null

    private val tweenManager = TweenManager()

    lateinit var spriteVessel: Sprite

    @JvmField
    protected var sectorSlot = -1

    @JvmField
    protected var parkingPosition = Vector2()

    protected lateinit var lastParkingPosition: Vector2

    @JvmField
    protected var parkingAngle = 0f

    @JvmField
    protected var flightAngle = 0f

    private val notMoving = false

    open fun moveTo(sector: Sector) {
        if (notMoving) return

        if (this is IDestroyable) {
            val iDestroyable = this as IDestroyable
            if (iDestroyable.inBattle()) {
                iDestroyable.setBattle(null)
            }
        }

        lastSector = this.sector
        lastParkingPosition = parkingPosition.cpy()

        this.sector.gameObjs.removeValue(this, true)
        this.sector = sector

        sector.gameObjs.add(this)

        getFreeSectorSlot()
        getParkingPosition()

        flightAngle = Math.atan2((parkingPosition.y - lastParkingPosition.y).toDouble(), (parkingPosition.x - lastParkingPosition.x).toDouble()).toFloat() * 180f / Math.PI.toFloat() - 90f
        while (flightAngle < -180) flightAngle += 360f
        while (flightAngle > 180) flightAngle -= 360f

        tweenManager.killAll()
        Timeline.createSequence()
                .push(Tween.to(this.spriteVessel, SpriteAccessor.ROTATION, 0.2f).target(flightAngle))
                .push(Tween.to(this.spriteVessel, SpriteAccessor.POSITION_XY, 0.5f).target(parkingPosition.x, parkingPosition.y).ease(TweenEquations.easeInOutQuint))
                .push(Tween.to(this.spriteVessel, SpriteAccessor.ROTATION, 1f).target(parkingAngle).ease(TweenEquations.easeInOutCubic))
                .start(tweenManager)
    }

    override fun render(deltaTime: Float) {
        super.render(deltaTime)
        tweenManager.update(deltaTime)
    }

    protected fun getFreeSectorSlot() {
        slots@ for (i in 0 until sector.gameObjs.size) {
            for (gameObj in sector.gameObjs) {
                if (gameObj is MovingGameObj
                        && gameObj.sectorSlot == i
                        && gameObj.toString() !== this.toString()) {
                    continue@slots
                }
            }
            sectorSlot = i
            return
        }
        sectorSlot = sector.gameObjs.size
    }

    protected fun getParkingPosition() {
        val shipsPerRow = 8
        var angle = (360 / shipsPerRow * sectorSlot).toFloat()
        val row = MathUtils.floor((sectorSlot / shipsPerRow).toFloat()).toFloat()
        if (row % 2 == 0f) angle += (360 / shipsPerRow / 2).toFloat()
        val distance = 30 + row * 15

        while (angle < -180) angle += 360f
        while (angle > 180) angle -= 360f


        val dx = distance * MathUtils.cosDeg(angle)
        val dy = distance * MathUtils.sinDeg(angle)

        parkingPosition = sector.position.cpy().sub(spriteVessel.width / 2, spriteVessel.height / 2).add(dx, dy)
        parkingAngle = (angle + 90) % 360
    }
}