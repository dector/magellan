package com.gdxjam.magellan.utils

import com.badlogic.gdx.utils.Array


inline fun <reified T> gdxArrayOf(vararg items: T) = Array<T>(items.size).apply {
    addAll(*items)
}