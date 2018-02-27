package com.gdxjam.magellan.gameobj

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.ships.PlayerShip
import com.gdxjam.magellan.ui.Colors
import com.gdxjam.magellan.ui.Strings
import com.gdxjam.magellan.utils.texture

/**
 * Created by lolcorner on 22.12.2015.
 */
class MeteoroidField(sector: Sector) : GameObj(sector), IDrawableMap, IDrawableWindow, IInteractable {

    @JvmField
    var resource = MathUtils.random(1, 3) // 1 - 3
    @JvmField
    var resourceAmount = Math.round((sector.position.x + sector.position.y) * .25f)

    private lateinit var mapSprite: Sprite

    override val actor: Stack
        get() = Stack().apply {
            Image(MagellanGame.assets.texture("sectorview_asteroids.png"))
                    .let { addActor(it) }

            Image(MagellanGame.assets.texture("sectorview_asteroids_resources.png")).apply {
                if (resourceAmount == 0) {
                    color = Color.LIGHT_GRAY
                } else {
                    when (resource) {
                        1 -> color = Colors.RESOURCE_1
                        2 -> color = Colors.RESOURCE_2
                        3 -> color = Colors.RESOURCE_3
                    }
                }
            }.let { addActor(it) }
        }

    override val title = "Meteoroid field"

    override val info: String
        get() = """
                Faction: $faction
                Resource: ${resourceName()}
                Resource amount: $resourceAmount""".trimIndent()

    private fun resourceName() = when (resource) {
        1 -> Strings.resource1
        2 -> Strings.resource2
        3 -> Strings.resource3
        else -> ""
    }

    override fun prepareRenderingOnMap() {
        mapSprite = if (sector.hasPlanet) {
            Sprite(MagellanGame.assets.texture("map_meteoroids_planetsector.png")).apply {
                setSize(45f, 45f)
            }
        } else {
            Sprite(MagellanGame.assets.texture("map_meteoroids_emptysector.png")).apply {
                setSize(23f, 23f)
            }
        }.apply {
            color = if (resourceAmount == 0) {
                Color.LIGHT_GRAY
            } else when (resource) {
                1 -> Colors.RESOURCE_1
                2 -> Colors.RESOURCE_2
                3 -> Colors.RESOURCE_3
                else -> Color.LIGHT_GRAY
            }
        }
    }

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        mapSprite.apply {
            setPosition(sector.position.x - width / 2, sector.position.y - height / 2)
            draw(batch)
        }
    }

    fun mine(amount: Int): Int {
        val minedAmount = if (amount > resourceAmount) {
            resourceAmount
        } else amount

        resourceAmount -= minedAmount
        if (resourceAmount == 0) {
            prepareRenderingOnMap()
        }
        return minedAmount
    }

    override fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        val meteoroidField = this
        val interactions = OrderedMap<String, IInteractable.Interaction>()

        if (resourceAmount > 0 && with.faction == GameObj.Factions.PLAYER) {
            interactions.put("Mine", object : IInteractable.Interaction {
                override fun interact() {
                    when (meteoroidField.resource) {
                        1 -> MagellanGame.gameState.resource1 += meteoroidField.mine((with as PlayerShip).mineResourcesPerTick)
                        2 -> MagellanGame.gameState.resource2 += meteoroidField.mine((with as PlayerShip).mineResourcesPerTick)
                        3 -> MagellanGame.gameState.resource3 += meteoroidField.mine((with as PlayerShip).mineResourcesPerTick)
                    }
                    MagellanGame.instance.universe.tick()
                    MagellanGame.soundFx.mine.play(0.4f)
                    showInteractionWindow()
                }
            })
        }

        return interactions
    }
}
