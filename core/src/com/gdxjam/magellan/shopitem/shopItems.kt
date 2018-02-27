package com.gdxjam.magellan.shopitem

import com.badlogic.gdx.math.MathUtils
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.drones.DroneRoutine
import com.gdxjam.magellan.ships.PlayerShip


/**
 * Originally created by lolcorner on 31.12.2015.
 * Helper class for encapsulating items for the shops
 */

sealed class ShopItem(
        @JvmField val title: String,
        @JvmField val description: String,
        @JvmField val price: Int) {

    open fun buy(buyer: PlayerShip) {
        buyer.inventory.add(this)
    }
}

class ShopItemDrone(
        private val level: Int) : ShopItem(
        title = "Drone Level " + level,
        description = "This drone can handle\nup to $level subroutines",
        price = level * level * 1000) {

    override fun buy(buyer: PlayerShip) {
        buyer.drones.add(level)
    }
}

class ShopItemDroneRoutine(
        private val routine: DroneRoutine.ROUTINES,
        price: Int) : ShopItem(
        title = "Drone Routine " + routine.toString(),
        description = getInfo(routine),
        price = price) {

    override fun buy(buyer: PlayerShip) {
        MagellanGame.gameState.unlockedRoutines.add(routine)
    }

    companion object {

        fun getInfo(routine: DroneRoutine.ROUTINES) = when (routine) {
            DroneRoutine.ROUTINES.MINING -> "This routine unlocks the\nDrone's mining equipment."
            DroneRoutine.ROUTINES.SCOUTING -> "This routine unlocks the\nDrone's thruster."
            DroneRoutine.ROUTINES.ATTACKING -> "This routine unlocks the\nDrone's cannon!"
            DroneRoutine.ROUTINES.ADVSCOUTING -> "This routine offers\na smarter algorithm\nfor scouting."
            DroneRoutine.ROUTINES.FOLLOWING -> "This routine programs\nthe drone to follow\nyour ship."
            DroneRoutine.ROUTINES.REPAIRING -> "This routine programs\nthe drone to repair\nyour ship."
        }
    }
}

class ShopItemUpgrade(
        price: Int,
        private val type: UpgradeType) : ShopItem(
        title = "Ship Upgrade " + type,
        description = "+1 to your " + type,
        price = price) {

    enum class UpgradeType {
        ATTACK, HEALTH, SHIELD
    }

    override fun buy(buyer: PlayerShip) {
        when (type) {
            ShopItemUpgrade.UpgradeType.ATTACK -> buyer.attack++
            ShopItemUpgrade.UpgradeType.HEALTH -> buyer.maxHealth++
            ShopItemUpgrade.UpgradeType.SHIELD -> buyer.shield += .025f
        }
        buyer.shield = MathUtils.clamp(buyer.shield, .1f, .5f)
    }
}