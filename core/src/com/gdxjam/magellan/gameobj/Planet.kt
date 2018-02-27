package com.gdxjam.magellan.gameobj

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.battle.Battle
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.ships.AiShipSettler
import com.gdxjam.magellan.ships.PlayerShip
import com.gdxjam.magellan.ships.Ship
import com.gdxjam.magellan.ui.Colors
import com.gdxjam.magellan.ui.Strings
import com.gdxjam.magellan.utils.texture


/**
 * Created by lolcorner on 19.12.2015.
 */
open class Planet(sector: Sector) : GameObj(sector), IDrawableMap, IDestroyable, IInteractable {

    lateinit var mapSprite: Sprite
    lateinit var mapClaimedSprite: Sprite

    override val health: Int
        get() = population
    override val shield = 0f

    @JvmField
    var population = 0

    var color = when (MathUtils.random(4)) {
        0 -> Colors.PLANET_1
        1 -> Colors.PLANET_2
        2 -> Colors.PLANET_3
        3 -> Colors.PLANET_4
        4 -> Colors.PLANET_5
        else -> Colors.PLANET_1
    }

    var visualType = MathUtils.random(1, 4)

    @JvmField
    var resource1 = MathUtils.random(50, 200)

    @JvmField
    var resource2 = MathUtils.random(50, 200)

    @JvmField
    var resource3 = MathUtils.random(50, 200)

    var minResourcesForSettling = 100

    private var poplimitMessageShown = false

    override val actor: Actor
        get() = Stack().also { stack ->
            Image(MagellanGame.assets.texture("sectorview_planet_$visualType.png")).apply {
                color = this@Planet.color
                setFillParent(true)
            }.let { stack.addActor(it) }
        }

    override val title = "PLANET"

    override val isAlive: Boolean
        get() = population > 0

    private val populationLimit: Int
        get() = if (!isHabitable) {
            0
        } else {
            resource1 * 10 + resource2 * 10 + resource3 * 10
        }

    private val isHabitable: Boolean
        get() = resource1 >= minResourcesForSettling
                && resource2 >= minResourcesForSettling
                && resource3 >= minResourcesForSettling

    override val info: String
        get() = StringBuilder().apply {
            appendln("Faction: $faction")
            appendln("Population: $population / $populationLimit")
            if (faction == GameObj.Factions.PLAYER) {
                appendln("Credits production: ${this@Planet.creditsByTick()}")
            }

            appendln("${Strings.resource1}: $resource1")
            appendln("${Strings.resource2}: $resource2")
            appendln("${Strings.resource3}: $resource3")

            if (faction == GameObj.Factions.NEUTRAL || faction == GameObj.Factions.PLAYER && population == 0) {
                if (isHabitable) {
                    appendln("This Planet should support human life.")
                } else {
                    appendln("This Planet is inhabitable right now.")
                    appendln("(Need $minResourcesForSettling of each resource for settling here)")
                }
            }
        }.toString()

    override fun prepareRenderingOnMap() {
        mapSprite = Sprite(MagellanGame.assets.texture("map_planet_$visualType.png")).apply {
            color = this@Planet.color
            setSize(30f, 30f)
        }

        mapClaimedSprite = Sprite(MagellanGame.assets.texture("map_planet_claimed.png")).apply {
            setSize(15f, 20f)
        }
    }

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        mapSprite.setPosition(sector.position.x - mapSprite.width / 2, sector.position.y - mapSprite.height / 2)
        mapSprite.draw(batch)

        if (faction == GameObj.Factions.PLAYER) {
            mapClaimedSprite.color = Colors.FACTION_PLAYER
        }
        if (faction == GameObj.Factions.SAATOO) {
            mapClaimedSprite.color = Colors.FACTION_ENEMY
        }
        if (faction == GameObj.Factions.PLAYER || faction == GameObj.Factions.SAATOO) {
            mapClaimedSprite.setPosition(sector.position.x, sector.position.y + mapSprite.height / 4)
            mapClaimedSprite.draw(batch)
        }
    }

    override fun receiveDamage(damage: Int): Boolean {
        population -= damage * 333

        if (population <= 0) {
            population = 0
            if (faction === GameObj.Factions.PLAYER) {
                MagellanGame.instance.mapScreen.log.addEntry("The population of a planet has ben eradicated.", sector)
            }
            faction = GameObj.Factions.NEUTRAL
        }

        return true
    }

    override fun destroy() {}

    override fun dispose() {}

    override fun inBattle(): Boolean {
        return false
    }

    override fun setBattle(battle: Battle?) {}

    override fun passiveTick() {
        growPopulation()

        if (faction == GameObj.Factions.PLAYER && population > 0 && population == populationLimit && !poplimitMessageShown) {
            poplimitMessageShown = true

            MagellanGame.instance.mapScreen.log.addEntry("Planet has reached it's population limit.", sector)
        }
    }

    fun claim(ship: Ship) {
        this.faction = ship.faction

        if (faction == GameObj.Factions.PLAYER) {
            MagellanGame.instance.mapScreen.log.addEntry("Planet claimed (A:$resource1 P:$resource2 E:$resource3)", sector)
        }
    }

    fun populate(ship: Ship, _humans: Int): Int {
        var humans = _humans

        if (ship is PlayerShip) {
            faction = ship.faction

            humans = MathUtils.clamp(humans, 0, Math.min(ship.HUMANS, populationLimit - population))

            if (humans > 0) {
                MagellanGame.soundFx.population.play(0.6f)
            } else {
                if (populationLimit - population == 0) {
                    MagellanGame.instance.windowScreen.getWindow("Capacity reached", "This planet has reached\nit's capacity for humans.")
                } else {
                    MagellanGame.instance.windowScreen.getWindow("No humans left", "There are no frozen\nhumans on your ship.")
                }

            }
            population += humans
            ship.HUMANS -= humans
        }

        if (ship is AiShipSettler) {
            faction = ship.faction
            population += 500
        }

        MagellanGame.gameState.updatePopulationCount()

        return humans
    }

    fun boardHumans(ship: Ship, _humans: Int) {
        var humans = _humans

        if (ship is PlayerShip) {
            faction = ship.faction
            humans = MathUtils.clamp(humans, 0, population)
            population -= humans
            ship.HUMANS += humans
        }

        MagellanGame.gameState.updatePopulationCount()
    }

    fun growPopulation() {
        population = if (population <= 1) {
            0
        } else {
            val popGrowth = MathUtils.floor(population * 0.05f)
            MathUtils.clamp(population + popGrowth, 0, populationLimit)
        }
    }

    fun creditsByTick(): Int {
        return Math.round((population / 30).toFloat())
    }

    override fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        val interactions = OrderedMap<String, IInteractable.Interaction>()
        val me = this

        if (submenuOpen == "") {
            if (faction == GameObj.Factions.NEUTRAL) {
                interactions.put("claim", object : IInteractable.Interaction {
                    override fun interact() {
                        claim(with as Ship)
                        if (me is EnemyHomePlanet) {
                            MagellanGame.instance.windowScreen.getWindow("A WINNER IS YOU", "You defeated SAATOO and captured their home planet!\n" +
                                    "But more important you finished our little game :)\n" +
                                    "We would like to really thank you for your patience\n" +
                                    "and hope you had some fun with Project Magellan.\n" +
                                    "Did you see the plot-twist coming?\n" +
                                    "Probably. But anyways, you are free to capture the\n" +
                                    "rest of the universe as no more enemy units will\n" +
                                    "spawn from now on.\n" +
                                    "\n" +
                                    "Thanks for playing!\n" +
                                    "Felix, Kilian and Tobias.")
                        } else {
                            showInteractionWindow()
                        }
                    }
                })
            }

            if (faction == with.faction && isHabitable) {
                interactions.put("Settle 1000 humans", object : IInteractable.Interaction {
                    override fun interact() {
                        if (populate(with as Ship, 1000) > 0) {
                            showInteractionWindow()
                        }


                    }
                })
            }

            if (faction == with.faction && population > 0) {
                interactions.put("Board 1000 humans", object : IInteractable.Interaction {
                    override fun interact() {
                        boardHumans(with as Ship, 1000)
                        showInteractionWindow()
                    }
                })
            }

            if (faction == with.faction) {
                interactions.put("Upgrade planet", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "upgrade"
                        showInteractionWindow()
                    }
                })
            }
        }

        if (submenuOpen == "upgrade") {
            interactions.put("Add 10 " + Strings.resource1, object : IInteractable.Interaction {
                override fun interact() {
                    addResources(1, MagellanGame.gameState.spendResource(1, 10))
                    showInteractionWindow()
                }
            })
            interactions.put("Add 10 " + Strings.resource2, object : IInteractable.Interaction {
                override fun interact() {
                    addResources(2, MagellanGame.gameState.spendResource(2, 10))
                    showInteractionWindow()
                }
            })
            interactions.put("Add 10 " + Strings.resource3, object : IInteractable.Interaction {
                override fun interact() {
                    addResources(3, MagellanGame.gameState.spendResource(3, 10))
                    showInteractionWindow()
                }
            })
            interactions.put("Back", object : IInteractable.Interaction {
                override fun interact() {
                    submenuOpen = ""
                    showInteractionWindow()
                }
            })
        }
        return interactions
    }

    fun addResources(resourcetype: Int, amount: Int) {
        if (amount > 0) poplimitMessageShown = false
        when (resourcetype) {
            1 -> resource1 += amount
            2 -> resource2 += amount
            3 -> resource3 += amount
        }
    }
}
