package com.gdxjam.magellan.screen

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.ui.Colors
import com.gdxjam.magellan.utils.music
import com.gdxjam.magellan.utils.texture

/**
 * Created by lolcorner on 09.01.2016.
 */
class TitleScreen(game: MagellanGame) : BaseScreen(game) {

    private val bg: Sprite
    private val title: Sprite

    init {
        stage.clear()
        createStarfield()

        bg = Sprite(MagellanGame.assets.texture("sectorview_planet_" + MathUtils.random(1, 4) + ".png"))
        bg.setSize(600f, 600f)
        bg.color = Colors.PLANET_2
        bg.setPosition(1280 - bg.width, 720 - bg.height)

        title = Sprite(MagellanGame.assets.texture("title.png"))
        title.setSize(721 * 0.7f, 317 * .7f)
        title.setPosition((1280 - title.width) / 2, 350f)

        var infoText = "Created for libGDXJam 2015/16"
        infoText += "\nIdea and programming by Felix Schittig and Tobias Duehr"
        infoText += "\nArtwork by Kilian Wilde"
        infoText += "\nMusic by Mark Sparling"

        val info = Label(infoText, skin)
        info.setAlignment(Align.center)
        info.width = 1280f
        info.setPosition(0f, 100f)

        stage.addActor(info)

        val mainMenu = VerticalGroup()
        mainMenu.pad(20f)
        mainMenu.space(10f)
        mainMenu.width = 200f
        mainMenu.setPosition((1280 / 2 - 100).toFloat(), 300f)

        val btnStart = TextButton("Start game", skin)
        btnStart.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                game.showWindowScreen()
                startBGM()
            }
        })

        val btnStory = TextButton("Story", skin)
        btnStory.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                game.showStoryScreen()
            }
        })

        mainMenu.addActor(btnStart)
        mainMenu.addActor(btnStory)
        stage.addActor(mainMenu)
    }

    override fun show() {
        super.show()
        startBGM(MagellanGame.assets.music("bgm3.mp3"))
    }

    override fun render(delta: Float) {
        renderBG()

        batch.begin()

        starfield.draw(batch)
        bg.draw(batch)
        title.draw(batch)

        batch.end()

        stage.draw()
    }
}
