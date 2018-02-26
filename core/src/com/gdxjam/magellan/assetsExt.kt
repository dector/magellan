@file:Suppress("NOTHING_TO_INLINE")

package com.gdxjam.magellan

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin


inline fun AssetManager.loadTexture(fileName: String) = load(fileName, Texture::class.java)
inline fun AssetManager.loadSkin(fileName: String) = load(fileName, Skin::class.java)
inline fun AssetManager.loadMusic(fileName: String) = load(fileName, Music::class.java)
inline fun AssetManager.loadSound(fileName: String) = load(fileName, Sound::class.java)