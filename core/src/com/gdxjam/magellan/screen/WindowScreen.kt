package com.gdxjam.magellan.screen

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenEquations
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Timer
import com.gdxjam.magellan.Battle
import com.gdxjam.magellan.Colors
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.Sector
import com.gdxjam.magellan.drones.Drone
import com.gdxjam.magellan.gameobj.*
import com.gdxjam.magellan.ships.AiShip
import com.gdxjam.magellan.ships.PlayerShip
import com.gdxjam.magellan.shopitem.ScreenShake
import com.gdxjam.magellan.tweening.ActorAccessor
import com.gdxjam.magellan.utils.texture

/**
 * Created by lolcorner on 20.12.2015.
 */
class WindowScreen(game: MagellanGame) : BaseScreen(game) {

    private val dronesOnScreen: HorizontalGroup
    private val shipsOnScreen: Array<Container<Actor>>
    private val planetOnScreen: Container<Actor>
    private val resourcesOnScreen: Container<Actor>
    private val playerOnScreen: Container<Actor>
    private val shopOnScreen: Container<Actor>
    private val planetClaimedOnScreen: Container<Actor>
    private val spPixel: Sprite
    private val spBar: Sprite
    private var lastShownSector: Sector? = null
    private var effects: Array<ParticleEffect>? = null
    private var startTutorialShown = false

    init {
        createStarfield()

        dronesOnScreen = HorizontalGroup()
        shipsOnScreen = Array()
        shipsOnScreen.add(Container())
        shipsOnScreen.add(Container())
        shipsOnScreen.add(Container())
        playerOnScreen = Container()
        planetOnScreen = Container()
        planetClaimedOnScreen = Container()
        shopOnScreen = Container()
        resourcesOnScreen = Container()
        dronesOnScreen.setPosition(500f, 200f)

        shipsOnScreen.get(0).setPosition(600f, 200f)
        shipsOnScreen.get(1).setPosition(740f, 100f)
        shipsOnScreen.get(2).setPosition(800f, 400f)

        shipsOnScreen.get(0).setSize(assetToGameSize(959).toFloat(), assetToGameSize(649).toFloat())
        shipsOnScreen.get(1).setSize(assetToGameSize(959).toFloat(), assetToGameSize(649).toFloat())
        shipsOnScreen.get(2).setSize(assetToGameSize(959).toFloat(), assetToGameSize(649).toFloat())

        playerOnScreen.setPosition(-600f, -110f)
        playerOnScreen.setSize(assetToGameSize(2523).toFloat(), assetToGameSize(2064).toFloat())
        planetOnScreen.setPosition((1280 - assetToGameSize(1386)).toFloat(), (720 - assetToGameSize(1677)).toFloat())
        planetOnScreen.setSize(assetToGameSize(1386).toFloat(), assetToGameSize(1677).toFloat())

        planetClaimedOnScreen.setPosition(960f, 480f)
        planetClaimedOnScreen.setSize(90f, 125f)

        shopOnScreen.setPosition(530f, 360f)
        shopOnScreen.setSize(assetToGameSize(717).toFloat(), assetToGameSize(790).toFloat())

        resourcesOnScreen.setPosition((1280 - assetToGameSize(2341)).toFloat(), 0f)
        resourcesOnScreen.setSize(assetToGameSize(2341).toFloat(), assetToGameSize(1318).toFloat())

        sectorContainer.addActor(planetOnScreen)
        sectorContainer.addActor(planetClaimedOnScreen)
        sectorContainer.addActor(resourcesOnScreen)
        sectorContainer.addActor(shopOnScreen)
        sectorContainer.addActor(shipsOnScreen.get(0))
        sectorContainer.addActor(shipsOnScreen.get(1))
        sectorContainer.addActor(shipsOnScreen.get(2))
        sectorContainer.addActor(dronesOnScreen)
        sectorContainer.addActor(playerOnScreen)

        btnWindow.remove()

        btnWait.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                drawSurroundings()
            }
        })

        spPixel = Sprite(MagellanGame.assets.get("pixel.png", Texture::class.java))
        spBar = Sprite(MagellanGame.assets.get("bar.png", Texture::class.java))
    }

    override fun show() {
        super.show()

        closeWindow()

        effects = Array()

        drawSurroundings()

        playerOnScreen.setPosition(-500f, -110f)
        shipsOnScreen.get(0).setPosition(700f, 150f)
        shipsOnScreen.get(1).setPosition(840f, 50f)
        shipsOnScreen.get(2).setPosition(900f, 350f)

        tweenManager.killAll()

        if (lastShownSector !== MagellanGame.instance.universe.playerShip.sector) {
            Tween.to(playerOnScreen, ActorAccessor.POSITION_XY, 0.8f).target(-300f, -100f).ease(TweenEquations.easeOutCubic).start(tweenManager)
            Tween.to(shipsOnScreen.get(0), ActorAccessor.POSITION_XY, 0.8f).target(600f, 200f).ease(TweenEquations.easeOutCubic).start(tweenManager)
            Tween.to(shipsOnScreen.get(1), ActorAccessor.POSITION_XY, 0.8f).target(740f, 100f).ease(TweenEquations.easeOutCubic).start(tweenManager)
            Tween.to(shipsOnScreen.get(2), ActorAccessor.POSITION_XY, 0.8f).target(800f, 400f).ease(TweenEquations.easeOutCubic).start(tweenManager)
        } else {
            playerOnScreen.setPosition(-300f, -100f)
            shipsOnScreen.get(0).setPosition(600f, 200f)
            shipsOnScreen.get(1).setPosition(740f, 100f)
            shipsOnScreen.get(2).setPosition(800f, 400f)
        }

        Tween.to(playerOnScreen, ActorAccessor.POSITION_Y, 5f).target(-80f).ease(TweenEquations.easeInOutCubic).repeatYoyo(-1, 0f).delay(1f).start(tweenManager)
        Tween.to(playerOnScreen, ActorAccessor.POSITION_X, 7f).target(-290f).ease(TweenEquations.easeInOutCubic).repeatYoyo(-1, 0f).delay(1f).start(tweenManager)

        Tween.to(shipsOnScreen.get(0), ActorAccessor.POSITION_Y, MathUtils.random(.4f, .6f)).target(215f).ease(TweenEquations.easeInOutCubic).repeatYoyo(-1, 0f).delay(MathUtils.random(0.8f, 1.3f)).start(tweenManager)
        Tween.to(shipsOnScreen.get(1), ActorAccessor.POSITION_Y, MathUtils.random(.4f, .6f)).target(115f).ease(TweenEquations.easeInOutCubic).repeatYoyo(-1, 0f).delay(MathUtils.random(0.8f, 1.3f)).start(tweenManager)
        Tween.to(shipsOnScreen.get(2), ActorAccessor.POSITION_Y, MathUtils.random(.4f, .6f)).target(415f).ease(TweenEquations.easeInOutCubic).repeatYoyo(-1, 0f).delay(MathUtils.random(0.8f, 1.3f)).start(tweenManager)

        lastShownSector = game.universe.playerShip.sector

        if (!startTutorialShown) {
            startTutorialShown = true
            getWindow("Info", "Click on your ship to see it's stats.\nClick on the shop to interact with it.\nClick 'Star Map' to see your surroundings.")
        }
    }


    fun drawSurroundings() {
        dronesOnScreen.clear()

        resourcesOnScreen.clear()

        playerOnScreen.clear()
        planetOnScreen.clear()
        planetClaimedOnScreen.clear()

        shopOnScreen.clear()

        shipsOnScreen.get(0).clear()
        shipsOnScreen.get(1).clear()
        shipsOnScreen.get(2).clear()

        var i = 0
        game.universe.playerShip.sector.gameObjs.filterIsInstance<IDrawableWindow>().forEach { gameObj ->
            val actor = gameObj.actor

            if (gameObj is Planet) {
                planetOnScreen.actor = actor

                if (gameObj.faction == GameObj.Factions.PLAYER || gameObj.faction == GameObj.Factions.SAATOO) {
                    val imageClaimed = Image(MagellanGame.assets.texture("map_planet_claimed.png"))

                    if (gameObj.faction == GameObj.Factions.PLAYER) {
                        imageClaimed.color = Colors.FACTION_PLAYER
                    }
                    if (gameObj.faction == GameObj.Factions.SAATOO) {
                        imageClaimed.color = Colors.FACTION_ENEMY
                    }

                    imageClaimed.rotation = 102f
                    imageClaimed.scaleX = -1f

                    planetClaimedOnScreen.actor = imageClaimed
                }
            }

            if (gameObj is Drone) {
                dronesOnScreen.width = actor.width
                dronesOnScreen.addActor(actor)
            }

            if (gameObj is Shop) {
                shopOnScreen.actor = actor
            }

            if (gameObj is PlayerShip) {
                playerOnScreen.actor = actor
            }

            if (gameObj is AiShip) {
                if (i < 3) {
                    shipsOnScreen.get(i).setSize(actor.width, actor.height)
                    shipsOnScreen.get(i).actor = actor
                    i++
                }
            }

            if (gameObj is MeteoroidField) {
                resourcesOnScreen.actor = actor
            }

            actor.addListener(object : InputListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (game.universe.playerShip.inBattle()) return true

                    game.universe.playerShip.sector.gameObjs.forEach { _gameObj ->
                        if (gameObj !is AiShip && _gameObj is AiShip && MagellanGame.gameState.aiHostility >= 5) {
                            getWindow("Alert!", "Beware! Enemy ships in sector!")
                            return false
                        }
                    }

                    return true
                }

                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    if (game.universe.playerShip.inBattle()) return

                    (gameObj as GameObj).submenuOpen = ""

                    showInteractionWindow(gameObj)
                }
            })
        }
    }

    fun shake(i: Int) {
        ScreenShake.shakeScreen(30, stage.camera.position.cpy(), (10 * i).toFloat())
    }

    fun showInteractionWindow(gameObj: IDrawableWindow) {
        val window = getWindow(gameObj.title)

        val windowContent = VerticalGroup()
        windowContent.fill()

        val info = Label(gameObj.info, skin, "window")
        val menu = VerticalGroup()
        menu.padTop(20f)
        menu.space(6f)
        menu.fill()

        if (gameObj is IInteractable) {
            gameObj.getInteractions(game.universe.playerShip).keys().forEach { key ->
                val button = TextButton(key, skin, "yellow")

                button.addListener(object : ChangeListener() {

                    override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                        if (game.universe.playerShip.inBattle()) return

                        gameObj.getInteractions(game.universe.playerShip).get(key).interact()
                        drawSurroundings()
                    }
                })

                menu.addActor(button)
            }
        }

        if (gameObj is IDestroyable &&
                gameObj != game.universe.playerShip &&
                (gameObj as GameObj).submenuOpen == "" &&
                (gameObj as IDestroyable).isAlive) {

            val destroyable = gameObj as IDestroyable
            val button = TextButton("ATTACK", skin, "red")

            button.addListener(object : ChangeListener() {

                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    if (game.universe.playerShip.inBattle()) return
                    closeWindow()
                    Battle(game.universe.playerShip, destroyable)
                }
            })

            menu.addActor(button)
        }

        windowContent.addActor(info)
        windowContent.addActor(menu)
        window.add(windowContent).expandX().fill()
    }


    override fun render(delta: Float) {
        super.render(delta)

        batch.begin()
        starfield.draw(batch)
        batch.end()

        renderUi(delta)

        batch.begin()
        effects?.forEach { pe ->
            pe.draw(batch, delta)
            if (pe.isComplete) effects?.removeValue(pe, true)
        }

        renderHealthBar()
        batch.end()

        ScreenShake.update(stage.camera)
    }

    private fun renderHealthBar() {
        spBar.setSize(200f, 40f)
        spBar.setPosition(1280 / 2 - spBar.width / 2, 20f)
        spPixel.setSize(spBar.width / game.universe.playerShip.maxHealth * game.universe.playerShip.health, spBar.height)
        spPixel.setPosition(spBar.x, spBar.y)
        spPixel.color = Colors.FACTION_PLAYER
        spPixel.setScale(1f)
        spPixel.rotation = 0f
        spPixel.draw(batch)
        spBar.draw(batch)
    }

    private fun assetToGameSize(size: Int): Int {
        return size / 3
    }

    fun showDamage(target: IDestroyable, damage: Int) {
        val pe = ParticleEffect()
        pe.load(Gdx.files.internal("explosion"), Gdx.files.internal(""))

        val l = Label("-" + damage, skin, "damage")
        when (target) {
            is PlayerShip -> {
                l.setPosition(200f, 400f)
                pe.setPosition(200f, 200f)
                pe.scaleEffect((damage * 2).toFloat())
            }
            is Planet -> {
                pe.setPosition(980f, 550f)
                l.setPosition(980f, 550f)
                pe.scaleEffect(4f)
            }
            else -> {
                l.setPosition(900f, 500f)
                pe.setPosition(900f, 500f)
                pe.scaleEffect(2f)

                shipsOnScreen.forEach { c ->
                    if (c.actor != null
                            && c.actor.userObject != null
                            && c.actor.userObject === target) {
                        l.setPosition(c.x + c.actor.width / 2, c.y + c.actor.height / 2)
                        pe.setPosition(c.x + c.actor.width / 2, c.y + c.actor.height / 2)
                    }
                }
            }
        }

        stage.addActor(l)

        Tween.to(l, ActorAccessor.ALPHA, 0.5f).target(0f).ease(TweenEquations.easeOutCubic).delay(0.5f).start(tweenManager)
        Tween.to(l, ActorAccessor.POSITION_Y, 1f).target(l.y + 50).ease(TweenEquations.easeInCubic).setCallback { type, source -> l.remove() }.start(tweenManager)

        effects?.add(pe)

        pe.start()
    }

    fun showShield(target: IDestroyable) {
        if (target is PlayerShip) {
            (playerOnScreen.actor as Stack).children.get(1).setColor(1f, 1f, 1f, 1f)

            Timer.schedule(object : Timer.Task() {

                override fun run() {
                    (playerOnScreen.actor as Stack).children.get(1).setColor(1f, 1f, 1f, 0f)
                }
            }, 0.5f)
        } else {
            shipsOnScreen.forEach { c ->
                if (c.actor != null
                        && c.actor.userObject != null
                        && c.actor.userObject == target) {
                    (c.actor as Stack).children.get(1).setColor(1f, 1f, 1f, 1f)

                    Timer.schedule(object : Timer.Task() {

                        override fun run() {
                            (c.actor as Stack).children.get(1).setColor(1f, 1f, 1f, 0f)
                        }
                    }, 0.5f)
                }
            }
        }
    }
}
