package com.gdxjam.magellan

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Timer
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IArmed
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.gameobj.Planet
import com.gdxjam.magellan.ships.PlayerShip

/**
 * Created by saibotd on 29.12.15.
 */
class Battle(
        private var offensive: IDestroyable?,
        private var defensive: IDestroyable?) : Disposable {

    private val screen = MagellanGame.instance.windowScreen
    private var damageDone = 0

    private val isPlayerBattle: Boolean
        get() = offensive is PlayerShip || defensive is PlayerShip

    init {
        init()
    }

    private fun init() {
        if (offensive?.inBattle() == true || defensive?.inBattle() == true) {
            return
        }

        offensive?.setBattle(this)
        defensive?.setBattle(this)

        if ((offensive as GameObj).sector !== (defensive as GameObj).sector) {
            dispose()
            return
        }

        Gdx.app.log("BATTLE", offensive!!.toString() + " VS " + defensive!!.toString())

        if (defensive is PlayerShip) {
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    MagellanGame.instance.showWindowScreen()
                    screen.startBGM(MagellanGame.assets.get("battle.mp3", Music::class.java))
                }
            }, 1f)
        }

        if (offensive is PlayerShip) {
            screen.startBGM(MagellanGame.assets.get("battle.mp3", Music::class.java))
            playerTurn()
        } else {
            turn()
        }
    }

    fun turn() {
        if (offensive is Planet) {
            dispose()
            return
        }

        Gdx.app.log("BATTLE", offensive!!.toString() + " ATTACKS " + defensive!!.toString())

        if ((offensive as GameObj).sector !== (defensive as GameObj).sector) {
            dispose()
            return
        }

        val panShoot = if (offensive is PlayerShip) -0.8f else 0.8f
        val panImpact = if (offensive is PlayerShip) 0.8f else -0.8f

        if (isPlayerBattle) {
            screen.closeWindow()
            if ((offensive as IArmed).getAttack() > 1) {
                MagellanGame.soundFx.weaponFire.random().play(0.8f, 1f, panShoot)
            } else {
                MagellanGame.soundFx.weaponFireSmall.random().play(0.8f, 1f, panShoot)
            }
        }

        if (isPlayerBattle && offensive is IArmed) {
            Timer.schedule(object : Timer.Task() {

                override fun run() {
                    val armedOffensive = offensive as IArmed?
                    val i = armedOffensive!!.shootAt(defensive!!)

                    damageDone += i

                    Gdx.app.log("BATTLE", offensive!!.toString() + " ATTACKS FOR " + i)
                    Gdx.app.log("BATTLE", defensive!!.toString() + " HEALTH AT " + defensive!!.getHealth())

                    if (defensive is PlayerShip) {
                        MagellanGame.instance.windowScreen.shake(i)
                    }

                    if (i == -1) {
                        MagellanGame.instance.windowScreen.showShield(defensive as IDestroyable)
                        MagellanGame.soundFx.shield.random().play(0.7f, 1f, panImpact)
                    } else {
                        MagellanGame.instance.windowScreen.showDamage(defensive as IDestroyable, i)
                        MagellanGame.soundFx.explosions.random().play(1f, 1f, panImpact)
                    }
                }
            }, 0.7f)
        } else if (offensive is IArmed) {
            val armedOffensive = offensive as IArmed?
            val i = armedOffensive!!.shootAt(defensive!!)

            damageDone += i

            Gdx.app.log("BATTLE", offensive!!.toString() + " ATTACKS FOR " + i)
            Gdx.app.log("BATTLE", defensive!!.toString() + " HEALTH AT " + defensive!!.getHealth())
        }

        if (isPlayerBattle) {
            Timer.schedule(object : Timer.Task() {

                override fun run() {
                    if (offensive!!.isAlive && defensive!!.isAlive) {
                        val _offensive = offensive

                        offensive = defensive
                        defensive = _offensive

                        if (offensive is PlayerShip) {
                            Gdx.app.log("HERE", "HERE")

                            screen.closeWindow()
                            playerTurn()

                            return
                        } else
                            turn()
                    } else {
                        if (!defensive!!.isAlive) defensive!!.destroy()

                        Gdx.app.log("BATTLE", "OVER")

                        dispose()
                    }

                    if (offensive is PlayerShip) {
                        screen.drawSurroundings()
                    }
                }
            }, 1.6f)
        } else {
            if (offensive!!.isAlive && defensive!!.isAlive) {
                val _offensive = offensive

                offensive = defensive
                defensive = _offensive

                turn()
            } else {
                if (!defensive!!.isAlive) defensive!!.destroy()

                Gdx.app.log("BATTLE", "OVER")

                dispose()
            }
        }
    }

    fun playerTurn() {
        //screen.closeWindow();
        for (actor in screen.stage.actors) {
            (actor as? Label)?.remove()
        }

        Gdx.app.log("playerTurn", "1")

        val window = screen.getWindowWithoutClose("Battle")
        val windowContent = VerticalGroup()
        val menu = HorizontalGroup()

        val info = HorizontalGroup()
        info.space(20f)

        menu.padTop(20f)
        menu.space(6f)
        menu.fill()

        var textLeft = "Your ship\nHealth: " + offensive!!.getHealth()
        textLeft += "\nShield: " + Math.round(offensive!!.getShield() * 100) + "%"
        textLeft += "\nAttack: " + (offensive as IArmed).getAttack()

        var textRight = "Enemy\nHealth: " + defensive!!.getHealth()
        textRight += "\nShield: " + Math.round(defensive!!.getShield() * 100) + "%"

        if (defensive is IArmed)
            textRight += "\nAttack: " + (defensive as IArmed).getAttack()

        info.addActor(Label(textLeft, screen.skin, "window"))
        info.addActor(Label(textRight, screen.skin, "window"))

        val buttonAttack = TextButton("ATTACK", screen.skin)
        val buttonFlee = TextButton("Flee", screen.skin)

        buttonAttack.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                turn()
            }
        })

        buttonFlee.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                dispose()
                screen.game.showMapScreen()
            }
        })

        Gdx.app.log("playerTurn", "2")

        menu.addActor(buttonAttack)
        menu.addActor(buttonFlee)

        windowContent.addActor(info)
        windowContent.addActor(menu)

        window.add(windowContent)
    }

    private fun showOutcomeWindow() {
        if (damageDone <= 0) return

        screen.closeWindow()

        val credits = MathUtils.random(0, damageDone * 250)

        MagellanGame.gameState.credits += credits

        val window = screen.getWindow("Battle outcome")
        val windowContent = VerticalGroup()
        windowContent.space(10f)

        val info = Label("", screen.skin, "window")

        if (MagellanGame.instance.universe.playerShip.isAlive) {
            Gdx.app.log("defensive", defensive!!.toString())
            Gdx.app.log("offensive", offensive!!.toString())

            var planet: Planet? = null

            if (defensive is Planet) planet = defensive as Planet?
            if (offensive is Planet) planet = offensive as Planet?

            if (planet != null && isPlayerBattle) {
                if (planet.population == 0) {
                    info.setText("Victory! There is no life\nleft on this planet.")
                } else {
                    info.setText("You killed much of whatever was\nliving on that planet but\nthere are still some left!")
                }
            } else {
                info.setText("Victory! As you scan the\nremaining scraps you gather $credits credits!")
            }
        } else {
            info.setText("That's it. Humanity's last chance\nand you blew it. The enemy will\ncapture each of your planets\nand destroy all human life forever.")
        }

        windowContent.addActor(info)

        val menu = HorizontalGroup()
        val buttonOK = TextButton("OK", screen.skin)

        buttonOK.addListener(object : ChangeListener() {

            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (!MagellanGame.instance.universe.playerShip.isAlive) {
                    MagellanGame.instance.showTitleScreen()
                    MagellanGame.instance.restartGame()
                }

                screen.closeWindow()
                screen.drawSurroundings()
            }
        })

        menu.addActor(buttonOK)
        windowContent.addActor(menu)
        window.add(windowContent)
    }

    override fun dispose() {
        Gdx.app.log("BATTLE", "DISPOSE")

        for (actor in screen.stage.actors) {
            (actor as? Label)?.remove()
        }

        if (isPlayerBattle) {
            screen.drawSurroundings()
            showOutcomeWindow()
            screen.startBGM()
        }

        offensive?.setBattle(null)
        defensive?.setBattle(null)
        offensive = null
        defensive = null
    }
}
