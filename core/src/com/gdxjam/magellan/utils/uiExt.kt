package com.gdxjam.magellan.utils

import com.badlogic.gdx.scenes.scene2d.ui.Label


inline fun Label.updateLocalStyle(modifier: (Label.LabelStyle) -> Unit) {
    val newStyle = Label.LabelStyle(style)
    modifier(newStyle)
    style = newStyle
}