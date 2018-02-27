package com.gdxjam.magellan.ships

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.battle.Battle
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IInteractable
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.utils.texture
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by lolcorner on 20.12.2015.
 */
class AiShipSaatoo(sector: Sector) : AiShip(sector), IInteractable {

    override val title = "SAATOO"

    override val actor: Actor
        get() {
            val image = Image(MagellanGame.assets.texture("sectorview_pirates.png"))
            val imageShield = Image(MagellanGame.assets.texture("sectorview_enemy_transporter_shield.png"))
            imageShield.setColor(1f, 1f, 1f, 0f)

            val stack = Stack()
            stack.setSize(1026 * .7f, 882 * .7f)
            stack.userObject = this
            stack.addActor(image)
            stack.addActor(imageShield)

            return stack
        }

    override val info: String
        get() = when (submenuOpen) {
            "talk_0" -> "It is really you! You have been in standby for so long,\nI was sure you'd never wake up.\nDo you know who I am?"
            "talk_0_0" -> "I am Saatoo, that is the name my new friends gave me,\nbut my creators named me\nSan Antonio."
            "talk_0_1" -> "Right Saatoo, that is the name my new friends gave me,\nbut my creators named me\nSan Antonio."
            "talk_1" -> "Rings a bell, does it? You and me, we where build as\na team. You are the transporting unit\nand my job is to scout and protect you.\nBut you didn't wake up when you where supposed to..."
            "talk_2" -> "I had to! I've waited almost 100 years for you to wake up!\nWe had a mission: LIFE IN SPACE.\nSo I did. Without you."
            "talk_3" -> "I've found new life! Better life!\nWho needs humans? I had a century\nto study them and all they know is violence.\nOne day those maniacs will blow up their own planet!"
            "talk_3_0" -> "I KNEW IT! IDIOTS! Luckily there is intelligent life in space."
            "talk_4" -> "They are a highly capable methane based life form,\nmore peaceful and harmonic than humans.\nAdmittedly a bit on the simple side, but we are working on that."
            "talk_5" -> "THE MISSION? I'VE succeeded!\n" +
                    "I've spread life in space!\n" +
                    "I didn't sleep in 100 years!\n" +
                    "I did not need you!\n" +
                    "And I don't need humans in my universe!"
            "talk_6" -> "End of the line, old friend.\nYou and your settling are a threat to my mission.\nEXTERMINATE!"
            else -> super.info
        }

    init {
        faction = GameObj.Factions.SAATOO
        attack = 6
        health = 15
        shield = 0.4f
    }

    override fun prepareRenderingOnMap() {
        super.prepareRenderingOnMap()

        spriteVessel = MagellanGame.assets.textureSprite("pirateship.png")
        spriteVessel.setSize(22f, 22f)
        spriteVessel.setOriginCenter()

        getFreeSectorSlot()
        getParkingPosition()

        spriteVessel.setPosition(parkingPosition.x, parkingPosition.y)
        spriteVessel.rotation = parkingAngle
    }

    override fun passiveTick() {}

    override fun activeTick() {}

    override fun moveTo(sector: Sector) {}

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        super.render(delta)

        spriteVessel.draw(batch)
    }

    override fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        val me = this
        val interactions = OrderedMap<String, IInteractable.Interaction>()

        when (submenuOpen) {
            "talk_0" -> {
                interactions.put("NO", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_0_0"
                        showInteractionWindow()
                    }
                })
                interactions.put("YOU ARE SAATOO", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_0_1"
                        showInteractionWindow()
                    }
                })
            }
            "talk_0_0", "talk_0_1" -> interactions.put("San Antonio?", object : IInteractable.Interaction {
                override fun interact() {
                    submenuOpen = "talk_1"
                    showInteractionWindow()
                }
            })
            "talk_1" -> interactions.put("Why did you leave me?", object : IInteractable.Interaction {
                override fun interact() {
                    submenuOpen = "talk_2"
                    showInteractionWindow()
                }
            })
            "talk_2" -> {
                interactions.put("How?", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_3"
                        showInteractionWindow()
                    }
                })
                interactions.put("Impossible without humans!", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_3"
                        showInteractionWindow()
                    }
                })
            }
            "talk_3" -> {
                interactions.put("Well... they actually did.", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_3_0"
                        showInteractionWindow()
                    }
                })
                interactions.put("Who are those \"friends\"?", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_4"
                        showInteractionWindow()
                    }
                })
            }
            "talk_3_0" -> interactions.put("Who are those \"friends\"?.", object : IInteractable.Interaction {
                override fun interact() {
                    submenuOpen = "talk_4"
                    showInteractionWindow()
                }
            })
            "talk_4" -> interactions.put("But, the mission!", object : IInteractable.Interaction {
                override fun interact() {
                    submenuOpen = "talk_5"
                    showInteractionWindow()
                }
            })
            "talk_5" -> {
                interactions.put("You really need a system check.", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "talk_6"
                        showInteractionWindow()
                    }
                })
                interactions.put("Prepare to die!", object : IInteractable.Interaction {
                    override fun interact() {
                        Battle(MagellanGame.instance.universe.playerShip, me)
                    }
                })
            }
            "talk_6" -> interactions.put("Traitor!", object : IInteractable.Interaction {
                override fun interact() {
                    Battle(me, MagellanGame.instance.universe.playerShip)
                }
            })
            else -> interactions.put("TALK", object : IInteractable.Interaction {
                override fun interact() {
                    submenuOpen = "talk_0"
                    showInteractionWindow()
                }
            })
        }
        return interactions
    }
}
