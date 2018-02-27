@file:Suppress("NOTHING_TO_INLINE")

package com.gdxjam.magellan.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite


fun displayInFullScreenMode() {
    Gdx.graphics.setDisplayMode(
            Gdx.graphics.desktopDisplayMode.width,
            Gdx.graphics.desktopDisplayMode.height,
            true)
}

inline fun AssetManager.textureSprite(file: String) = Sprite(this.texture(file))