package com.gdxjam.magellan.screen

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.*
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.gdxjam.magellan.BuildConfig
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.gameobj.IDrawableMap
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.models.Universe
import com.gdxjam.magellan.ships.AiShipFighter
import com.gdxjam.magellan.ships.AiShipSettler
import com.gdxjam.magellan.ui.Colors
import com.gdxjam.magellan.ui.Log

/**
 * Created by lolcorner on 19.12.2015.
 */
class MapScreen(game: MagellanGame) : BaseScreen(game) {

    private val pixel: Sprite
    private val dot: Sprite
    private val sectorNormal: Sprite
    private val sectorNotVisited: Sprite
    private val mapViewport: Viewport

    @JvmField
    var log: Log

    private var zoom = 1f
    private val touch = Vector3()
    private val touchCircle = Circle(0f, 0f, 30f)
    private var doMousePan = false
    private val dragStartMousePos = Vector2()
    private val dragStartCameraPos = Vector2()
    private val mousePos = Vector2()
    private var keyboardPanX: Float = 0.toFloat()
    private var keyboardPanY: Float = 0.toFloat()
    private val universe: Universe
    private var tmp1: Vector2? = null

    @JvmField
    var camera: OrthographicCamera

    @JvmField
    var mapBatch: SpriteBatch

    private val cameraFrame = Rectangle()
    private val cameraFramePadding = 200f
    private val starfieldScroll = Vector2()
    internal var lineWidth = 2f
    private var sectorToFocusOn: Sector? = null

    init {
        universe = game.universe

        mapBatch = SpriteBatch()

        pixel = Sprite(MagellanGame.assets.get("pixel.png", Texture::class.java))
        dot = Sprite(MagellanGame.assets.get("dot.png", Texture::class.java))
        sectorNormal = Sprite(MagellanGame.assets.get("map_sector.png", Texture::class.java))
        sectorNotVisited = Sprite(MagellanGame.assets.get("map_sector_notvisited.png", Texture::class.java))
        sectorNormal.setSize(10f, 10f)
        //sectorNormal.setColor(Color.BLACK);
        sectorNotVisited.setSize(20f, 20f)

        camera = OrthographicCamera()
        mapViewport = FitViewport(1280f, 720f, camera)
        camera.position.x = universe.playerShip.sector.position.x
        camera.position.y = universe.playerShip.sector.position.y

        val logGroup = HorizontalGroup()
        logGroup.setSize(400f, 200f)
        logGroup.setPosition((1280 - 410).toFloat(), 5f)

        val logTarget = VerticalGroup()
        val logScroll = ScrollPane(logTarget, skin, "log")

        logGroup.addActor(logScroll)
        logScroll.setFillParent(true)
        logTarget.fill()
        logTarget.space(5f)

        stage.addActor(logGroup)

        log = Log(logTarget)

        universe.sectors.forEach { sector ->
            (0 until sector.gameObjs.size)
                    .filter { sector.gameObjs.get(it) is IDrawableMap }
                    .forEach { (sector.gameObjs.get(it) as IDrawableMap).prepareRenderingOnMap() }
        }
        createStarfield()
        starfield.texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)

        Timer.schedule(object : Timer.Task() {

            override fun run() {
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
                MagellanGame.instance.mapScreen.log.addEntry("")
            }
        }, 1f)

        btnMap.remove()
    }

    override fun show() {
        super.show()

        displayTutorialIfNeeded()
    }

    private fun displayTutorialIfNeeded() {
        if (BuildConfig.DontDisplayTutorial) return
        if (MagellanGame.gameState.mapTutorialShown) return

        MagellanGame.gameState.mapTutorialShown = true
        getWindow("Info", """
            Here you can jump to other sectors.
            Just click on a connected sector to try it out!""".trimIndent())
    }

    override fun render(delta: Float) {
        super.render(delta)

        batch.begin()

        starfieldScroll.x = camera.position.x / 800
        starfieldScroll.y = -camera.position.y / 450
        starfield.u = starfieldScroll.x
        starfield.u2 = starfieldScroll.x + 1
        starfield.v = starfieldScroll.y
        starfield.v2 = starfieldScroll.y + 1
        starfield.draw(batch)

        batch.end()

        mapViewport.apply()

        camera.update()
        mapBatch.projectionMatrix = camera.combined

        camera.zoom = zoom
        camera.position.x += keyboardPanX
        camera.position.y += keyboardPanY

        if (sectorToFocusOn != null) {
            camera.position.lerp(Vector3(sectorToFocusOn!!.position.x, sectorToFocusOn!!.position.y, 0f), delta * 4)
            zoom = MathUtils.clamp(zoom - delta * 2, .5f, 5f)
            if (sectorToFocusOn!!.position.dst(camera.position.x, camera.position.y) < .2 && zoom <= 0.5f) sectorToFocusOn = null
        }

        cameraFrame.set(
                camera.position.x - camera.viewportWidth * zoom / 2 - cameraFramePadding,
                camera.position.y - camera.viewportHeight * zoom / 2 - cameraFramePadding,
                camera.viewportWidth * zoom + cameraFramePadding * 2,
                camera.viewportHeight * zoom + cameraFramePadding * 2
        )

        if (doMousePan) {
            camera.position.set(dragStartCameraPos.x + (dragStartMousePos.x - mousePos.x) * zoom * viewport.worldWidth / viewport.screenWidth, dragStartCameraPos.y + (mousePos.y - dragStartMousePos.y) * zoom * viewport.worldHeight / viewport.screenHeight, 1f)
        }

        mapBatch.begin()

        for (sector in universe.getSectorsInRectangle(cameraFrame)) {
            if (!sector.visited && !MagellanGame.DISPLAY_FOG_OF_WAR) continue

            sector.connectedSectors.forEach { _sector ->
                tmp1 = sector.position.cpy().sub(_sector.position)

                if (sector == universe.playerShip.sector || _sector == universe.playerShip.sector) {
                    pixel.color = Colors.MAP_POSSIBLE_MOVEMENT
                    pixel.setAlpha(1f)
                    pixel.setSize(tmp1!!.len() + lineWidth / 2, lineWidth)
                    pixel.setOrigin(0f, lineWidth / 2)
                } else {
                    pixel.color = Color.WHITE
                    pixel.setAlpha(0.2f)
                    pixel.setSize(tmp1!!.len() + lineWidth / 2, lineWidth)
                    pixel.setOrigin(0f, lineWidth / 2)
                }

                pixel.setPosition(_sector.position.x - pixel.originX, _sector.position.y - pixel.originY)
                pixel.rotation = tmp1!!.angle()
                pixel.draw(mapBatch)
            }
        }

        universe.getSectorsInRectangle(cameraFrame).forEach { sector ->
            if (!sector.discovered && !MagellanGame.DISPLAY_FOG_OF_WAR) return@forEach

            if (sector.visited) {
                sectorNormal.setPosition(sector.position.x - sectorNormal.width / 2, sector.position.y - sectorNormal.height / 2)
                sectorNormal.draw(mapBatch)
            } else {
                sectorNotVisited.setPosition(sector.position.x - sectorNotVisited.width / 2, sector.position.y - sectorNotVisited.height / 2)
                sectorNotVisited.draw(mapBatch)
            }

            // Fog of war disabled for debugging
            if (sector.visited || MagellanGame.DISPLAY_FOG_OF_WAR) {
                sector.gameObjs
                        .filterIsInstance<IDrawableMap>()
                        .forEach { it.renderOnMap(mapBatch, delta) }
            }
        }

        mapBatch.end()

        viewport.apply()

        renderUi()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        mapViewport.update(width, height)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.W -> keyboardPanY = 10 * camera.zoom
            Input.Keys.A -> keyboardPanX = -10 * camera.zoom
            Input.Keys.S -> keyboardPanY = -10 * camera.zoom
            Input.Keys.D -> keyboardPanX = 10 * camera.zoom
            else -> super.keyDown(keycode)
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        super.keyUp(keycode)

        when (keycode) {
            Input.Keys.W -> keyboardPanY = 0f
            Input.Keys.A -> keyboardPanX = 0f
            Input.Keys.S -> keyboardPanY = 0f
            Input.Keys.D -> keyboardPanX = 0f

            Input.Keys.K -> if (BuildConfig.DevMode) {
                val ship = AiShipFighter(universe.playerShip.sector)
                ship.prepareRenderingOnMap()
            }
            Input.Keys.J -> if (BuildConfig.DevMode) {
                val ship2 = AiShipSettler(universe.playerShip.sector)
                ship2.prepareRenderingOnMap()
            }
            Input.Keys.L -> if (BuildConfig.DevMode) {
                MagellanGame.gameState.credits += 100000
                MagellanGame.gameState.resource1 += 1000
                MagellanGame.gameState.resource2 += 1000
                MagellanGame.gameState.resource3 += 1000

                universe.playerShip.drones.add(1)
                universe.playerShip.drones.add(2)
                universe.playerShip.drones.add(3)
                universe.playerShip.drones.add(4)
                universe.playerShip.drones.add(5)

                MagellanGame.instance.mapScreen.topbar.updateStats()

                universe.playerShip.health = universe.playerShip.maxHealth
            }
        }

        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        sectorToFocusOn = null

        camera.unproject(touch.set(screenX.toFloat(), screenY.toFloat(), zoom))

        touchCircle.setPosition(touch.x, touch.y)

        doMousePan = true

        dragStartMousePos.set(screenX.toFloat(), screenY.toFloat())
        dragStartCameraPos.set(camera.position.x, camera.position.y)
        mousePos.set(screenX.toFloat(), screenY.toFloat())

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        doMousePan = false

        if (dragStartMousePos.dst(mousePos) < 10) {
            if (universe.getSectorsInCircle(touchCircle).size > 0) {
                val sector = universe.getSectorsInCircle(touchCircle).get(0)

                if (universe.playerShip.sector.connectedSectors.contains(sector, true)) {
                    universe.playerShip.moveTo(sector)
                    universe.tick()
                }
            }
        }

        dragStartMousePos.set(mousePos)

        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        mousePos.set(screenX.toFloat(), screenY.toFloat())

        return true
    }

    override fun scrolled(amount: Int): Boolean {
        sectorToFocusOn = null
        zoom = MathUtils.clamp(zoom + amount * .3f, .5f, 2f)

        return false
    }

    fun focusOnSector(sector: Sector) {
        sectorToFocusOn = sector
    }
}