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

    private val log = Array<LogEntry>(10).apply {
        (0 until size)
                .forEach { index -> set(index, LogEntry("")) }
    }

    fun init() {
        update()
    }

    fun tick() {
        log.filter { it.justAdded }.forEachIndexed { index, entry ->
            log.set(index, entry.copy(justAdded = false))
        }
        update()
    }

    fun addEntry(s: String, sector: Sector) {
        val text = if (s == "") "" else "${MagellanGame.gameState.year} $s"

        log.insert(0, LogEntry(text = text, sector = sector, justAdded = true))
        update()
    }

    private fun update() {
        target.clear()

        log.forEach { entry ->
            val l = Label(entry.text, MagellanGame.instance.mapScreen.skin).apply {
                setWrap(true)

                if (entry.justAdded) {
                    val newStyle = Label.LabelStyle(style)
                    newStyle.fontColor = Colors.LOG_ENTRY_JUST_ADDED
                    style = newStyle
                }
            }

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
        val sector: Sector? = null,
        val justAdded: Boolean = false)