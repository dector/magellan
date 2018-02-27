package com.gdxjam.magellan.screen

import aurelienribon.tweenengine.TweenManager
import com.badlogic.gdx.*
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.gdxjam.magellan.Colors
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.UiTopbar
import com.gdxjam.magellan.utils.music
import com.gdxjam.magellan.utils.skin
import com.gdxjam.magellan.utils.texture

/**
 * Created by lolcorner on 20.12.2015.
 */
open class BaseScreen(@JvmField var game: MagellanGame) : Screen, InputProcessor {

    @JvmField
    var btnWindow: TextButton

    @JvmField
    var btnMap: TextButton

    @JvmField
    var btnWait: TextButton

    @JvmField
    var batch: SpriteBatch

    @JvmField
    var viewport: FitViewport

    @JvmField
    var skin: Skin

    @JvmField
    var stage: Stage

    @JvmField
    var topbar: UiTopbar

    private val bgTexture: Texture

    @JvmField
    var windowContainer: Container<Window>

    @JvmField
    var mainContainer: Table

    @JvmField
    var sectorContainer: Table

    @JvmField
    var tweenManager: TweenManager

    lateinit var starfield: Sprite

    companion object {
        private var bgm: Music? = null
    }

    init {
        skin = MagellanGame.assets.skin("skin/uiskin.json")
        batch = SpriteBatch()
        viewport = FitViewport(1280f, 720f)
        stage = Stage(viewport)

        tweenManager = TweenManager()

        sectorContainer = Table()
        sectorContainer.setSize(1280f, (720 - 60).toFloat())
        sectorContainer.clip = true
        stage.addActor(sectorContainer)

        mainContainer = Table()
        mainContainer.setSize(1280f, 720f)
        stage.addActor(mainContainer)

        windowContainer = Container()
        windowContainer.setSize(1280f, 720f)
        stage.addActor(windowContainer)

        val menu = HorizontalGroup()
        menu.setPosition(20f, 40f)
        menu.space(10f)
        btnWindow = TextButton("Show Sector", skin)
        btnMap = TextButton("Star Map", skin)
        btnWait = TextButton("Skip turn", skin)
        menu.addActor(btnWait)
        menu.addActor(btnWindow)
        menu.addActor(btnMap)
        stage.addActor(menu)

        bgTexture = MagellanGame.assets.texture("bg.png")
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        topbar = UiTopbar(game, mainContainer)

        btnWindow.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                game.showWindowScreen()
            }
        })
        btnMap.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (!game.universe.playerShip.inBattle())
                    game.showMapScreen()
            }
        })
        btnWait.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (!game.universe.playerShip.inBattle())
                    game.universe.tick()
            }
        })

        bgm = MagellanGame.assets.music("bgm0.mp3")

    }

    @JvmOverloads
    fun startBGM(song: Music = MagellanGame.assets.music("bgm" + MathUtils.random(0, 3) + ".mp3")) {
        if (bgm?.isPlaying == true) bgm?.stop()
        bgm = song
        bgm?.volume = .2f
        bgm?.play()
    }

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(this, stage)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.Z -> MagellanGame.DEBUG = !MagellanGame.DEBUG
            Input.Keys.ESCAPE -> if (MagellanGame.DEBUG) Gdx.app.exit()
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.F11, Input.Keys.F -> if (Gdx.graphics.isFullscreen)
                Gdx.graphics.setDisplayMode(1280, 720, false)
            else
                Gdx.graphics.setDisplayMode(Gdx.graphics.desktopDisplayMode.width, Gdx.graphics.desktopDisplayMode.height, true)
        }
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    fun renderBG() {
        viewport.apply()
        batch.begin()
        batch.draw(bgTexture, 0f, 0f, 1280f, 720f, 0f, 0f, 2f, 2f)
        batch.end()
    }

    @JvmOverloads
    fun getWindow(title: String, message: String? = null): Window {
        windowContainer.clear()

        val window = Window(title + "    ", skin)
        window.titleLabel.setEllipsis(false)
        window.isMovable = false
        window.isModal = true
        window.width = 500f
        window.padTop(70f)
        window.padLeft(20f)

        val closeButton = Image(TextureRegion(MagellanGame.assets.get("skin/uiskin.png", Texture::class.java), 182, 128, 51, 51))
        closeButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                closeWindow()
            }
        })

        val closeContainer = Group()
        closeContainer.addActor(closeButton)
        closeButton.setSize(20f, 20f)
        closeContainer.setSize(20f, 20f)
        window.titleTable.add(closeContainer)

        if (message != null) {
            val group = VerticalGroup()
            group.space(10f)
            val labelMessage = Label(message, skin)
            val btnOK = TextButton("OK", skin)

            btnOK.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    closeWindow()
                }
            })

            group.addActor(labelMessage)
            group.addActor(btnOK)
            window.add(group)
        }

        windowContainer.actor = window
        return window
    }

    fun getWindowWithoutClose(title: String): Window {
        windowContainer.clear()

        val window = Window(title + "    ", skin)

        window.titleLabel.setEllipsis(false)
        window.isMovable = false
        window.isModal = true
        window.width = 500f
        window.padTop(70f)
        window.padLeft(20f)

        windowContainer.actor = window
        return window
    }

    fun closeWindow() {
        Gdx.app.log("closeWindow", "called")
        windowContainer.clear()
    }

    fun createStarfield() {
        val width = 1280 * 4
        val height = 720 * 4
        val amount_small = 400
        val amount_mid = 150
        val amount_big = 20

        val bg = Pixmap(width, height, Pixmap.Format.RGBA8888)
        bg.setColor(Colors.UNIVERSE_BG)
        bg.fillRectangle(0, 0, width, height)

        bg.setColor(Color.WHITE)
        var posx: Int
        var posy: Int

        for (i in 0 until amount_small) {
            posx = MathUtils.floor(width * MathUtils.random())
            posy = MathUtils.floor(height * MathUtils.random())
            bg.fillCircle(posx, posy, 1)
        }
        for (i in 0 until amount_mid) {
            posx = MathUtils.floor(width * MathUtils.random())
            posy = MathUtils.floor(height * MathUtils.random())
            bg.fillCircle(posx, posy, 2)
        }
        for (i in 0 until amount_big) {
            posx = MathUtils.floor(width * MathUtils.random())
            posy = MathUtils.floor(height * MathUtils.random())
            bg.setColor(MathUtils.random(0.7f, 1f), MathUtils.random(0.1f, 0.5f), MathUtils.random(0.0f, 0.0f), 0.3f)
            bg.fillCircle(posx, posy, 8)
            bg.setColor(1f, 1f, 1f, 1f)
            bg.fillCircle(posx, posy, 4)
        }
        val field = Texture(bg)
        bg.dispose()

        starfield = Sprite(field)
        starfield.setSize(1280f, 720f)
    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        renderBG()

        update(delta)

        viewport.apply()
    }

    fun renderUi(delta: Float) {
        topbar.renderBg(delta, batch)

        stage.draw()
    }

    private fun update(delta: Float) {
        if (bgm?.isPlaying != true)
            startBGM()

        topbar.updateStats()
        tweenManager.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {
        batch.dispose()
    }
}
