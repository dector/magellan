package com.gdxjam.magellan.utils

import com.badlogic.gdx.Gdx


fun displayInFullScreenMode() {
    Gdx.graphics.setDisplayMode(
            Gdx.graphics.desktopDisplayMode.width,
            Gdx.graphics.desktopDisplayMode.height,
            true)
}