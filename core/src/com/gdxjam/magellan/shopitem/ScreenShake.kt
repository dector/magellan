package com.gdxjam.magellan.shopitem

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import java.util.*

object ScreenShake {

    private var shakeScreen = false

    private val rad = Random()

    private var shakes = 0
    private var nShakes = 0
    private var camPos: Vector3? = null
    private var velocity = 1f

    private var shake2 = false

    @JvmStatic
    fun update(camera: Camera) {
        if (!shakeScreen) return

        val n1 = if (rad.nextInt(2) == 0) rad.nextFloat() * velocity else rad.nextFloat() * velocity * -1f
        val n2 = if (rad.nextInt(2) == 0) rad.nextFloat() * velocity else rad.nextFloat() * velocity * -1f

        camera.translate(n2, n1, 0f)

        if (shake2) {
            shake2 = false
            camera.position.set(camPos)
        } else
            shake2 = true

        shakes++

        if (shakes > nShakes) {
            shakeScreen = false
            nShakes = 0
            shakes = 0
            camera.position.set(camPos)
        }
    }

    @JvmStatic
    fun shakeScreen(nshakes: Int, camPoss: Vector3, f: Float) {
        nShakes = if (shakeScreen && nshakes < nShakes) nShakes else nshakes
        velocity = if (shakeScreen && f < velocity) velocity else f
        camPos = camPoss
        shakeScreen = true
    }
}