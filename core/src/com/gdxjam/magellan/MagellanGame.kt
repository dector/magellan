package com.gdxjam.magellan

import aurelienribon.tweenengine.Tween
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.gdxjam.magellan.screen.MapScreen
import com.gdxjam.magellan.screen.StoryScreen
import com.gdxjam.magellan.screen.TitleScreen
import com.gdxjam.magellan.screen.WindowScreen
import com.gdxjam.magellan.tweening.ActorAccessor
import com.gdxjam.magellan.tweening.SpriteAccessor

class MagellanGame : Game() {

    companion object {

        lateinit var instance: MagellanGame

        lateinit var gameState: GameState

        lateinit var assets: AssetManager
        lateinit var soundFx: SoundFx

        @JvmField
        var DEBUG = false
    }
    
    @JvmField
    var universe: Universe

    lateinit var mapScreen: MapScreen
    lateinit var windowScreen: WindowScreen

    private lateinit var storyScreen: StoryScreen
    private lateinit var titleScreen: TitleScreen

    init {
        Companion.instance = this

        universe = Universe(this)
        Companion.gameState = GameState(this)
    }

    override fun create() {
        Tween.registerAccessor(Sprite::class.java, SpriteAccessor())
        Tween.registerAccessor(Actor::class.java, ActorAccessor())

        Companion.assets = AssetManager().apply {
            loadTexture("pixel.png")
            loadTexture("dot.png")
            loadTexture("circle.png")
            loadTexture("title.png")
            loadTexture("map_playership.png")
            loadTexture("map_meteoroids_emptysector.png")
            loadTexture("map_meteoroids_planetsector.png")
            loadTexture("map_planet_1.png")
            loadTexture("map_planet_2.png")
            loadTexture("map_planet_3.png")
            loadTexture("map_planet_4.png")
            loadTexture("map_planet_claimed.png")
            loadTexture("map_sector.png")
            loadTexture("map_sector_notvisited.png")
            loadTexture("map_shop.png")
            loadTexture("drone.png")
            loadTexture("drone_gun.png")
            loadTexture("drone_mine.png")
            loadTexture("drone_thruster.png")
            loadTexture("enemy_fighter.png")
            loadTexture("enemy_transport.png")
            loadTexture("enemy_drone.png")
            loadTexture("pirateship.png")
            loadTexture("shop.png")
            loadTexture("sectorview_asteroids.png")
            loadTexture("sectorview_asteroids_resources.png")
            loadTexture("sectorview_planet_1.png")
            loadTexture("sectorview_planet_2.png")
            loadTexture("sectorview_planet_3.png")
            loadTexture("sectorview_planet_4.png")
            loadTexture("sectorview_ship.png")
            loadTexture("sectorview_ship_shield.png")
            loadTexture("sectorview_drone.png")
            loadTexture("sectorview_drone_gun.png")
            loadTexture("sectorview_drone_mine.png")
            loadTexture("sectorview_drone_thruster.png")
            loadTexture("sectorview_enemy_fighter.png")
            loadTexture("sectorview_enemy_transporter.png")
            loadTexture("bar.png")
            loadTexture("sectorview_enemy_fighter_shield.png")
            loadTexture("sectorview_enemy_transporter_shield.png")
            loadTexture("sectorview_enemy_drone.png")
            loadTexture("sectorview_pirates.png")
            loadTexture("sectorview_shop.png")
            loadTexture("bg.png")
            loadTexture("topbarBg.png")

            loadSkin("skin/uiskin.json")

            loadMusic("bgm0.mp3")
            loadMusic("bgm1.mp3")
            loadMusic("bgm2.mp3")
            loadMusic("bgm3.mp3")
            loadMusic("battle.mp3")

            loadSound("sounds/buy_sell.mp3")
            loadSound("sounds/ship_jump_2.mp3")
            loadSound("sounds/button.mp3")
            loadSound("sounds/mine.mp3")
            loadSound("sounds/population.mp3")
            loadSound("sounds/upgrade.mp3")
            loadSound("sounds/shield_1.mp3")
            loadSound("sounds/shield_2.mp3")
            loadSound("sounds/nope.mp3")
            loadSound("sounds/doomed.wav")
            loadSound("sounds/explosion1.wav")
            loadSound("sounds/explosion2.wav")
            loadSound("sounds/explosion3.wav")
            loadSound("sounds/explosion4.wav")
            loadSound("sounds/weaponfire2.wav")
            loadSound("sounds/weaponfire3.wav")
            loadSound("sounds/weaponfire5.wav")
            loadSound("sounds/weaponfire6.wav")

            finishLoading()
        }

        soundFx = SoundFx()

        titleScreen = TitleScreen(this)
        mapScreen = MapScreen(this)
        windowScreen = WindowScreen(this)
        storyScreen = StoryScreen(this)

        setScreen(titleScreen)

        if (!BuildConstants.Debug) {
            Gdx.graphics.setDisplayMode(Gdx.graphics.desktopDisplayMode.width, Gdx.graphics.desktopDisplayMode.height, true)
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
