package com.gdxjam.magellan.gameobj

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.gdxjam.magellan.Colors
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.Sector
import com.gdxjam.magellan.utils.texture


/**
 * Created by lolcorner on 19.12.2015.
 */
class EnemyHomePlanet(sector: Sector) : Planet(sector) {

    override val actor: Actor
        get() = Image(MagellanGame.assets.texture("sectorview_planet_$visualType.png")).apply {
            color = this@EnemyHomePlanet.color
            setFillParent(true)
        }

    init {
        color = Colors.FACTION_ENEMY
    }

    override fun prepareRenderingOnMap() {
        mapSprite = Sprite(MagellanGame.assets.get("map_planet_$visualType.png", Texture::class.java)).apply {
            this.color = this@EnemyHomePlanet.color
            setSize(50f, 50f)
        }

        mapClaimedSprite = Sprite(MagellanGame.assets.get("map_planet_claimed.png", Texture::class.java)).apply {
            setSize(15f, 20f)
        }
    }
}
