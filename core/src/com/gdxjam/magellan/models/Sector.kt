package com.gdxjam.magellan.models

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.gdxjam.magellan.gameobj.GameObj

data class Sector(private val x: Int, private val y: Int) {

    @JvmField
    var position = Vector2(x.toFloat(), y.toFloat())
    @JvmField
    var connectedSectors = Array<Sector>()
    @JvmField
    var circleConnect = Circle(position, 200f)
    @JvmField
    var circleAlone = Circle(position, 64f)
    @JvmField
    var gameObjs: Array<GameObj> = Array()
    @JvmField
    var discovered = false
    @JvmField
    var visited = false
    @JvmField
    var hasPlanet = false

    private fun addConnection(sector: Sector) {
        if (sector === this) return
        if (connectedSectors.contains(sector, true)) return

        connectedSectors.add(sector)
        sector.addSingleConnection(this)
    }

    private fun addSingleConnection(sector: Sector) {
        if (sector === this) return
        if (connectedSectors.contains(sector, true)) return

        connectedSectors.add(sector)
    }

    fun addConnections(sectors: Array<Sector>) {
        sectors.forEach { sector -> addConnection(sector) }
    }
}

