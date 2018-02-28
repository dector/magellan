package com.gdxjam.magellan

import aurelienribon.tweenengine.Tween
import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.gdxjam.magellan.models.GameState
import com.gdxjam.magellan.models.Universe
import com.gdxjam.magellan.screen.MapScreen
import com.gdxjam.magellan.screen.StoryScreen
import com.gdxjam.magellan.screen.TitleScreen
import com.gdxjam.magellan.screen.WindowScreen
import com.gdxjam.magellan.sfx.SoundFx
import com.gdxjam.magellan.tweening.ActorAccessor
import com.gdxjam.magellan.tweening.SpriteAccessor
import com.gdxjam.magellan.utils.*

class MagellanGame : Game() {

    companion object {

        lateinit var instance: MagellanGame

        lateinit var gameState: GameState

        lateinit var assets: AssetManager
        lateinit var soundFx: SoundFx

        @JvmField
        var DEBUG = false
    }

    lateinit var universe: Universe

    lateinit var mapScreen: MapScreen
    lateinit var windowScreen: WindowScreen

    private lateinit var storyScreen: StoryScreen
    private lateinit var titleScreen: TitleScreen

    init {
        Companion.instance = this
    }

    override fun create() {
        registerTweenAccessors()

        loadResources()
        soundFx = SoundFx(assets)

        universe = Universe(this)
        Companion.gameState = GameState(this)

        titleScreen = TitleScreen(this)
        mapScreen = MapScreen(this)
        windowScreen = WindowScreen(this)
        storyScreen = StoryScreen(this)

        setScreen(titleScreen)

        if (!BuildConstants.DevMode) {
            displayInFullScreenMode()
        }
    }

    private fun registerTweenAccessors() {
        Tween.registerAccessor(Sprite::class.java, SpriteAccessor())
        Tween.registerAccessor(Actor::class.java, ActorAccessor())
    }

    private fun loadResources() {
        Companion.assets = com.gdxjam.magellan.utils.assets {
            loadTextures(
                    "pixel.png",
                    "dot.png",
                    "circle.png",
                    "title.png",
                    "map_playership.png",
                    "map_meteoroids_emptysector.png",
                    "map_meteoroids_planetsector.png",
                    "map_planet_1.png",
                    "map_planet_2.png",
                    "map_planet_3.png",
                    "map_planet_4.png",
                    "map_planet_claimed.png",
                    "map_sector.png",
                    "map_sector_notvisited.png",
                    "map_shop.png",
                    "drone.png",
                    "drone_gun.png",
                    "drone_mine.png",
                    "drone_thruster.png",
                    "enemy_fighter.png",
                    "enemy_transport.png",
                    "enemy_drone.png",
                    "pirateship.png",
                    "shop.png",
                    "sectorview_asteroids.png",
                    "sectorview_asteroids_resources.png",
                    "sectorview_planet_1.png",
                    "sectorview_planet_2.png",
                    "sectorview_planet_3.png",
                    "sectorview_planet_4.png",
                    "sectorview_ship.png",
                    "sectorview_ship_shield.png",
                    "sectorview_drone.png",
                    "sectorview_drone_gun.png",
                    "sectorview_drone_mine.png",
                    "sectorview_drone_thruster.png",
                    "sectorview_enemy_fighter.png",
                    "sectorview_enemy_transporter.png",
                    "bar.png",
                    "sectorview_enemy_fighter_shield.png",
                    "sectorview_enemy_transporter_shield.png",
                    "sectorview_enemy_drone.png",
                    "sectorview_pirates.png",
                    "sectorview_shop.png",
                    "bg.png",
                    "topbarBg.png"
            )

            loadSkin("skin/uiskin.json")

            loadMusic(
                    "bgm0.mp3",
                    "bgm1.mp3",
                    "bgm2.mp3",
                    "bgm3.mp3",
                    "battle.mp3"
            )

            loadSounds(
                    "sounds/buy_sell.mp3",
                    "sounds/ship_jump_2.mp3",
                    "sounds/button.mp3",
                    "sounds/mine.mp3",
                    "sounds/population.mp3",
                    "sounds/upgrade.mp3",
                    "sounds/shield_1.mp3",
                    "sounds/shield_2.mp3",
                    "sounds/nope.mp3",
                    "sounds/doomed.wav",
                    "sounds/explosion1.wav",
                    "sounds/explosion2.wav",
                    "sounds/explosion3.wav",
                    "sounds/explosion4.wav",
                    "sounds/weaponfire2.wav",
                    "sounds/weaponfire3.wav",
                    "sounds/weaponfire5.wav",
                    "sounds/weaponfire6.wav"
            )

            finishLoading()
        }
    }

    fun showWindowScreen() {
        setScreen(windowScreen)
    }

    fun showMapScreen() {
        setScreen(mapScreen)
    }

    fun showStoryScreen() {
        setScreen(storyScreen)
    }

    fun showTitleScreen() {
        setScreen(titleScreen)
    }

    fun restartGame() {
        mapScreen.dispose()
        windowScreen.dispose()

        universe = Universe(this)
        gameState = GameState(this)

        mapScreen = MapScreen(this)
        windowScreen = WindowScreen(this)
    }
}
