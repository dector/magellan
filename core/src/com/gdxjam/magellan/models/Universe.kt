package com.gdxjam.magellan.models

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.gameobj.*
import com.gdxjam.magellan.ships.*

class Universe(private val game: MagellanGame) {

    private val enemyPlanet: Planet

    @JvmField
    var playerShip: PlayerShip
    @JvmField
    val sectors = Array<Sector>()
    @JvmField
    var size = 3000
    @JvmField
    var bottomLeft: Sector
    @JvmField
    var topRight: Sector

    init {
        for (i in 0 until size / 2) {
            addRandomSector()
        }
        connectSectors()

        bottomLeft = sectors.random()
        topRight = sectors.random()

        for (sector in sectors) {
            if (sector.position.x < bottomLeft.position.x && sector.position.y < bottomLeft.position.y) {
                bottomLeft = sector
            }
            if (sector.position.x > topRight.position.x && sector.position.y > topRight.position.y) {
                topRight = sector
            }
        }

        bottomLeft.gameObjs.clear()

        playerShip = PlayerShip(bottomLeft)
        Shop(bottomLeft)

        topRight.gameObjs.clear()

        enemyPlanet = EnemyHomePlanet(topRight).apply {
            faction = GameObj.Factions.SAATOO
            addResources(1, 550)
            addResources(2, 550)
            addResources(3, 550)
            population = 2000
        }
        AiShipSaatoo(topRight)
    }

    private fun addSector(newSector: Sector): Boolean {
        for (sector in sectors) {
            if (newSector.circleAlone.contains(sector.position)) {
                return false
            }
        }

        sectors.add(newSector)

        return true
    }

    fun getSectorsInCircle(circle: Circle): Array<Sector> = sectors
            .filter { circle.contains(it.position) }
            .fold(Array()) { acc, sector -> acc.add(sector); acc }

    fun getSectorsInRectangle(rectangle: Rectangle): Array<Sector> = sectors
            .filter { rectangle.contains(it.position) }
            .fold(Array()) { acc, sector -> acc.add(sector); acc }

    fun getGameObjs(objType: Class<*>): Array<GameObj> = sectors
            .flatMap { it.gameObjs }
            .filter { objType == it.javaClass }
            .fold(Array()) { acc, sector -> acc.add(sector); acc }


    fun tick() {
        MagellanGame.gameState.progressYear()

        val gameObjs = Array<GameObj>()

        for (i in 0 until sectors.size) {
            for (j in 0 until sectors.get(i).gameObjs.size) {
                gameObjs.add(sectors.get(i).gameObjs.get(j))
            }
        }

        for (i in 0 until gameObjs.size) {
            gameObjs.get(i).passiveTick()
        }

        for (i in 0 until gameObjs.size) {
            gameObjs.get(i).activeTick()
        }

        MagellanGame.gameState.updatePopulationCount()
        MagellanGame.gameState.getPlanetIncome()
        MagellanGame.gameState.updateNumberOfDrones()

        if (enemyPlanet.faction == GameObj.Factions.SAATOO) {
            if (MagellanGame.gameState.yearsPassed == 1) {
                addEnemies(20, 5, 20)
            }

            addEnemies(MathUtils.random(-5, 1), MathUtils.random(-10, 1), MathUtils.random(-15, 1))
        }

        if (!MagellanGame.instance.universe.playerShip.isAlive) {
            MagellanGame.instance.showTitleScreen()
            MagellanGame.instance.restartGame()
        }

    }

    private fun addRandomSector() {
        val x = Math.round(Math.random() * size).toInt()
        val y = Math.round(Math.random() * size).toInt()
        val newSector = Sector(x, y)

        if (addSector(newSector)) {
            if (Math.random() < .2) {
                Planet(newSector)
                newSector.hasPlanet = true
            }
            if (Math.random() < .3) {
                MeteoroidField(newSector)
            }
            if (Math.random() < .05) {
                Shop(newSector)
            }
        }
    }

    private fun connectSectors() {
        sectors.filter { it.connectedSectors.size < 2 }
                .forEach { it.addConnections(getSectorsInCircle(it.circleConnect)) }

        sectors.filter { it.connectedSectors.size == 0 }
                .forEach { sectors.removeValue(it, true) }
    }


    private fun addEnemies(numberFighters: Int, numberSettlers: Int, numberSmallFighters: Int) {
        for (i in 0 until numberFighters) {
            AiShipFighter(topRight).prepareRenderingOnMap()
        }

        for (i in 0 until numberSettlers) {
            AiShipSettler(topRight).prepareRenderingOnMap()
        }

        val undiscoveredSectors = sectors
                .filter { !it.discovered && it.position.x > 200 && it.position.y > 200 }
                .fold(Array<Sector>()) { acc, sector -> acc.add(sector); acc }

        if (undiscoveredSectors.size > 0) {
            for (i in 0 until numberSmallFighters) {
                val smallFighter = AiShipSmallFighter(undiscoveredSectors.random())
                smallFighter.prepareRenderingOnMap()
            }
        }
    }
}
