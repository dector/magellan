package com.gdxjam.magellan.ui

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Array
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.models.Sector

/**
 * Created by saibotd on 14.01.16.
 */
class Log(private val target: Group) {

    private val log = Array<LogEntry>()

    fun addEntry(s: String) {
        log.insert(0, LogEntry.from(s))
        update()
    }

    fun addEntry(s: String, sector: Sector) {
        log.insert(0, LogEntry.from(s, sector))
        update()
    }

    fun update() {
        target.clear()

        log.forEach { entry ->
            val l = Label(entry.text, MagellanGame.instance.mapScreen.skin)
                    .apply { setWrap(true) }

            entry.sector?.let { sector ->
                l.addListener(object : InputListener() {

                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                        return true
                    }

                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        if (!sector.discovered) return

                        MagellanGame.instance.mapScreen.focusOnSector(sector)
                    }
                })
            }

            target.addActor(l)
        }
    }
}

private data class LogEntry(
        val text: String,
        val sector: Sector? = null
) {

    companion object {

        fun from(text: String, sector: Sector? = null) = LogEntry(
                text = if (text == "") "" else "${MagellanGame.gameState.year} $text",
                sector = sector
        )
    }
}