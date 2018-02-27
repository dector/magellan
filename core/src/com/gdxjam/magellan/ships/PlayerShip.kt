package com.gdxjam.magellan.ships

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.drones.Drone
import com.gdxjam.magellan.gameobj.GameObj
import com.gdxjam.magellan.gameobj.IDestroyable
import com.gdxjam.magellan.gameobj.IInteractable
import com.gdxjam.magellan.models.Sector
import com.gdxjam.magellan.shopitem.ShopItem
import com.gdxjam.magellan.utils.textureSprite

/**
 * Created by lolcorner on 20.12.2015.
 */
class PlayerShip(sector: Sector) : Ship(sector), IInteractable {

    var maxHealth: Int = 0
    var HUMANS = 10000
    var drones: Array<Int> = Array()
    var inventory: Array<ShopItem>
    var mineResourcesPerTick = 10

    private lateinit var trail: ParticleEffect

    override val actor: Actor
        get() = Stack().apply {
            addActor(Image(MagellanGame.assets.get("sectorview_ship.png", Texture::class.java)))
            addActor(Image(MagellanGame.assets.get("sectorview_ship_shield.png", Texture::class.java)))
            children.get(1).setColor(1f, 1f, 1f, 0f)
        }

    override val title = "The Trinidad"

    override val info: String
        get() = """
            Your ship.
            Health: $health/$maxHealth
            Attack: $attack
            Shield: ${Math.round(shield * 100)}%
            Frozen Humans: $HUMANS
            Drones: ${drones.joinToString(prefix = "", postfix = "")}
            Equipment: ${inventory.joinToString(prefix = "", postfix = "")}""".trimIndent()

    init {
        faction = GameObj.Factions.PLAYER
        health = 15
        maxHealth = health
        attack = 4
        shield = 0.15f
        inventory = Array()

        setSectorsDiscovered()

        drones.add(1)
    }

    override fun shootAt(target: IDestroyable): Int {
        MagellanGame.gameState.aiHostility = 5

        return super.shootAt(target)
    }

    override fun moveTo(sector: Sector) {
        super.moveTo(sector)

        setSectorsDiscovered()
        MagellanGame.soundFx.shipJump.play(0.3f)

        val particlePosition = lastParkingPosition.cpy().lerp(parkingPosition, 0.10f)

        trail.emitters.forEach { em ->
            em.setPosition(particlePosition.x + spriteVessel.width / 2, particlePosition.y + spriteVessel.height / 2)
            em.angle.setHigh(flightAngle - 90f - 10f, flightAngle - 90 + 10)
            em.angle.setLow(flightAngle - 90)
        }

        trail.start()
    }

    override fun destroy() {
        MagellanGame.soundFx.doomed.play()

        dispose()
    }

    fun releaseDrone(level: Int) {
        drones.removeValue(level, false)

        val drone = Drone(this.sector, level)
        drone.faction = faction

        MagellanGame.gameState.updateNumberOfDrones()
    }

    private fun setSectorsDiscovered() {
        sector.visited = true
        sector.discovered = true

        sector.connectedSectors.forEach { _sector ->
            _sector.connectedSectors.forEach { __sector -> __sector.discovered = true }
            _sector.discovered = true
            _sector.visited = true
        }
    }

    override fun prepareRenderingOnMap() {
        super.prepareRenderingOnMap()

        spriteVessel = MagellanGame.assets.textureSprite("map_playership.png")
        spriteVessel.setSize(20f, 20f)
        spriteVessel.setOriginCenter()

        sectorSlot = 1
        getParkingPosition()

        spriteVessel.setPosition(parkingPosition.x, parkingPosition.y)
        spriteVessel.rotation = parkingAngle

        trail = ParticleEffect()
        trail.load(Gdx.files.internal("ship_trail.p"), Gdx.files.internal(""))
        trail.setPosition(parkingPosition.x, parkingPosition.y)
        trail.scaleEffect(0.3f)
    }

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        super.render(delta)

        spriteVessel.draw(batch)

        trail.draw(batch, delta)
        //if (trail.isComplete()) trail.reset();
    }

    override fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        val interactions = OrderedMap<String, IInteractable.Interaction>()

        if (submenuOpen == "") {
            if (drones.size > 0) {
                interactions.put("release drone", object : IInteractable.Interaction {
                    override fun interact() {
                        submenuOpen = "releasedrone"
                        showInteractionWindow()
                    }
                })
            }
        }
        if (submenuOpen == "releasedrone") {
            if (drones.contains(1, true)) {
                interactions.put("Level 1", object : IInteractable.Interaction {
                    override fun interact() {
                        releaseDrone(1)
                        closeWindow()
                    }
                })
            }
            if (drones.contains(2, true)) {
                interactions.put("Level 2", object : IInteractable.Interaction {
                    override fun interact() {
                        releaseDrone(2)
                        closeWindow()
                    }
                })
            }
            if (drones.contains(3, true)) {
                interactions.put("Level 3", object : IInteractable.Interaction {
                    override fun interact() {
                        releaseDrone(3)
                        closeWindow()
                    }
                })
            }
            if (drones.contains(4, true)) {
                interactions.put("Level 4", object : IInteractable.Interaction {
                    override fun interact() {
                        releaseDrone(4)
                        closeWindow()
                    }
                })
            }
            if (drones.contains(5, true)) {
                interactions.put("Level 5", object : IInteractable.Interaction {
                    override fun interact() {
                        releaseDrone(5)
                        closeWindow()
                    }
                })
            }
        }

        return interactions
    }

    fun heal(powerLevel: Float) {
        health = MathUtils.clamp(health + powerLevel, 0f, maxHealth.toFloat()).toInt()
    }
}