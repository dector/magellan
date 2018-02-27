@file:Suppress("NOTHING_TO_INLINE")

package com.gdxjam.magellan.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array


inline fun AssetManager.loadTexture(file: String) {
    load(file, Texture::class.java)
}

inline fun AssetManager.loadSkin(file: String) {
    load(file, Skin::class.java)
}

inline fun AssetManager.loadMusic(file: String) {
    load(file, Music::class.java)
}

inline fun AssetManager.loadSound(file: String) {
    load(file, Sound::class.java)
}

inline fun AssetManager.loadTextures(vararg files: String) = files.forEach(::loadTexture)
inline fun AssetManager.loadMusic(vararg files: String) = files.forEach(::loadMusic)
inline fun AssetManager.loadSounds(vararg files: String) = files.forEach(::loadSound)

inline fun assets(init: AssetManager.() -> Unit) = AssetManager().apply(init)

inline fun AssetManager.sound(file: String): Sound = get(file, Sound::class.java)
inline fun AssetManager.sounds(vararg files: String) = Array<Sound>(files.size).also { array ->
    files.forEach { file ->
        array.add(sound(file))
    }
}
inline fun AssetManager.skin(file: String): Skin = get(file, Skin::class.java)
inline fun AssetManager.texture(file: String): Texture = get(file, Texture::class.java)
inline fun AssetManager.music(file: String): Music = get(file, Music::class.java)