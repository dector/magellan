package com.gdxjam.magellan.screen

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.gdxjam.magellan.MagellanGame

/**
 * Created by saibotd on 15.01.16.
 */
class StoryScreen(game: MagellanGame) : BaseScreen(game) {

    private val label: Label

    init {
        stage.clear()
        val theStory = """
            It's the year ${MagellanGame.gameState.year}.

            At the climax of World War XIV
            it finally happened - the earth exploded.
            Luckily not all hope is lost for humanity
            because there's you.

            You, this is this ship \"The Trinidad\"
            or to be precise, it's on-board A.I.
            After 162 years of standby you received
            your bootup sequence mere hours before
            earth shattered to pieces.

            You are not alone on this mission to
            save mankind. With you on board are
            10000 humans in cryostasis, just waiting
            to get reactivated and populate a new home.

            So, this is your mission:

            1. Find new planets
            2. Make them hospitable
            3. Give humanity a new chance for their species

            This mission won't be easy, as no one
            knows what is out there in the endlessness of space.
            Some say, there is nothing - others say
            there may be an unnamed threat, capable of conquering
            all of the universe.

            GOOD LUCK.""".trimIndent()

        label = Label(theStory, skin, "window")
        label.width = 1280f
        label.setAlignment(Align.center)
        stage.addActor(label)
    }

    override fun show() {
        super.show()

        label.setPosition(0f, -label.height)
    }

    override fun render(delta: Float) {
        super.render(delta)

        label.y = label.y + delta * 30
        stage.draw()

        if (label.y > label.height) {
            game.showTitleScreen()
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        super.keyUp(keycode)

        game.showTitleScreen()

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        game.showTitleScreen()

        return false
    }
}
