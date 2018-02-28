package com.gdxjam.magellan

object BuildConfig {

    const val DevMode = true

    val DontDisplayTutorial = on(false)
}

@Suppress("NOTHING_TO_INLINE")
private inline fun on(value: Boolean) = BuildConfig.DevMode && value