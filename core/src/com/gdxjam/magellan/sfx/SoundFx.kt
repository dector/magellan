package com.gdxjam.magellan.sfx

import com.badlogic.gdx.assets.AssetManager
import com.gdxjam.magellan.utils.sound
import com.gdxjam.magellan.utils.sounds

/**
 * Created by Felix on 02.01.2016.
 */
class SoundFx(assets: AssetManager) {

    @JvmField
    val shipJump = assets.sound("sounds/ship_jump_2.mp3")

    @JvmField
    val buy = assets.sound("sounds/buy_sell.mp3")

    @JvmField
    val button = assets.sound("sounds/button.mp3")

    @JvmField
    val mine = assets.sound("sounds/mine.mp3")

    @JvmField
    val population = assets.sound("sounds/population.mp3")

    @JvmField
    val upgrade = assets.sound("sounds/upgrade.mp3")

    @JvmField
    val nope = assets.sound("sounds/nope.mp3")

    @JvmField
    val doomed = assets.sound("sounds/doomed.wav")

    @JvmField
    val explosions = assets.sounds(
            "sounds/explosion1.wav",
            "sounds/explosion2.wav",
            "sounds/explosion3.wav",
            "sounds/explosion4.wav"
    )

    @JvmField
    val weaponFire = assets.sounds(
            "sounds/weaponfire2.wav",
            "sounds/weaponfire3.wav"
    )

    @JvmField
    val weaponFireSmall = assets.sounds(
            "sounds/weaponfire5.wav",
            "sounds/weaponfire6.wav"
    )

    @JvmField
    val shield = assets.sounds(
            "sounds/shield_1.mp3",
            "sounds/shield_2.mp3"
    )
}
