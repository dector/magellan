package com.gdxjam.magellan

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.gdxjam.magellan.utils.skin
import com.gdxjam.magellan.utils.texture

/**
 * Created by Felix on 22.12.2015.
 */
class UiTopbar(var game: MagellanGame, private var table: Table) {

    var viewport: FitViewport? = null
    var skin = MagellanGame.assets.skin("skin/uiskin.json")

    private lateinit var valueResource1: Label
    private lateinit var valueResource2: Label
    private lateinit var valueResource3: Label
    private lateinit var valueYear: Label
    private lateinit var valueCredits: Label
    private lateinit var valuePopulation: Label
    //private lateinit var valueDrones: Label
    //private lateinit var valueHealth: Label

    private lateinit var topBarBg: Sprite

    init {
        createTopBar()
    }

    fun createTopBar() {
        topBarBg = Sprite(MagellanGame.assets.texture("topbarBg.png")).apply {
            setPosition(0f, (720 - 60).toFloat())
            setSize(1280f, 60f)
        }

        // TopBar
        HorizontalGroup().apply {
            align(Align.right)
        }.let { table.addActor(it) }

        // Resources
        HorizontalGroup().apply {
            space(15f)
            setPosition(0f, (720 - 60).toFloat())
            setSize(640f, 60f)
            pad(20f)

            valueResource1 = Label("0", skin, "value")
            valueResource2 = Label("0", skin, "value")
            valueResource3 = Label("0", skin, "value")

            addActor(resourceLabel(1, valueResource1))
            addActor(resourceLabel(2, valueResource2))
            addActor(resourceLabel(3, valueResource3))
        }.let { table.addActor(it) }

        // Stats
        HorizontalGroup().apply {
            space(15f)
            setPosition(740f, (720 - 60).toFloat())
            setSize(640f, 60f)
            pad(20f)

            valueYear = Label("0", skin, "value")
            valueCredits = Label("0", skin, "value")
            valuePopulation = Label("0", skin, "value")
            //valueDrones = new Label("0", skin, "value");
            //valueHealth = new Label("0", skin, "value");

            addActor(simpleLabel("Year", valueYear))
            addActor(simpleLabel("Credits", valueCredits))
            addActor(simpleLabel("Population", valuePopulation))
            //addActor(simpleLabel("Drones", valueDrones));
            //addActor(simpleLabel("Health", valueHealth));
        }.let { table.addActor(it) }
    }

    fun updateStats() {
        valueResource1.setText(MagellanGame.gameState.resource1.toString() + "")
        valueResource2.setText(MagellanGame.gameState.resource2.toString() + "")
        valueResource3.setText(MagellanGame.gameState.resource3.toString() + "")
        valueYear.setText(MagellanGame.gameState.year.toString() + "")
        valueCredits.setText(MagellanGame.gameState.credits.toString() + "")
        valuePopulation.setText(MagellanGame.gameState.population.toString() + "")
        //valueDrones.setText(MagellanGame.gameState.drones + "");
        //valueHealth.setText(game.universe.playerShip.health + "");
    }

    fun dispose() {}

    fun resourceLabel(resourceNum: Int, _valueField: Label) = HorizontalGroup().apply {
        bottom()
        space(5f)
        height = 50f

        val (color, label, valueField) = when (resourceNum) {
            1 -> Triple(Colors.RESOURCE_1, Strings.resource1, _valueField)
            2 -> Triple(Colors.RESOURCE_2, Strings.resource2, _valueField)
            3 -> Triple(Colors.RESOURCE_3, Strings.resource3, _valueField)
            else -> Triple(Color.WHITE, "", valueResource1)
        }

        Container<Image>().apply {
            size(13f, 13f)
            padBottom(3f)

            actor = Image(MagellanGame.assets.get("dot.png", Texture::class.java)).apply {
                this.color = color
            }
        }.let { addActor(it) }

        addActor(Label(label + ":", skin))
        addActor(valueField)
    }


    private fun simpleLabel(label: String, valueField: Label) = HorizontalGroup().apply {
        bottom()
        space(5f)
        height = 50f

        addActor(Label(label + ":", skin))
        addActor(valueField)
    }

    fun renderBg(delta: Float, batch: SpriteBatch) {
        batch.begin()
        topBarBg.draw(batch)
        batch.end()
    }
}
