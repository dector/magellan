@file:Suppress("NOTHING_TO_INLINE")

package com.gdxjam.magellan.utils

import com.badlogic.gdx.graphics.Color


inline fun color(rgb888: Int) = Color((rgb888 shl 8) or 0xff)