@file:JvmName("DesktopLauncher")
package com.gdxjam.magellan.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.gdxjam.magellan.MagellanGame

fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration().apply {
        width = 1280
        height = 720
        backgroundFPS = 60
        foregroundFPS = 60
        samples = 16
        vSyncEnabled = true
        resizable = false
    }

    LwjglApplication(MagellanGame(), config)
}