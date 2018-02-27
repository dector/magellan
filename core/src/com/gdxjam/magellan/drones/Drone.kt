package com.gdxjam.magellan.drones

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.Battle
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.Sector
import com.gdxjam.magellan.gameobj.*

/**
 * Created by saibotd on 26.12.15.
 */
class Drone(sector: Sector, private val maxNumberOfRoutines: Int)
    : MovingGameObj(sector), IDestroyable, IDrawableMap, IInteractable, IArmed {

    override var health = 4 * maxNumberOfRoutines
    override val shield = 0f

    private val routines = Array<DroneRoutine>()
    var dimensions = Vector2(280f, 170f)
    var dimensionsWindow = Vector2(250f, 170f)
    private lateinit var listItems: Array<DroneRoutine.ROUTINES?>
    private lateinit var selectedRoutines: Array<DroneRoutine.ROUTINES?>
    private lateinit var listRight: List<DroneRoutine.ROUTINES?>
    private lateinit var listLeft: List<DroneRoutine.ROUTINES?>
    var destroyed: Boolean = false
    private var battle: Battle? = null

    override val attack: Int
        get() = routines
                .filterIsInstance<DroneRoutineFighting>()
                .sumBy { it.getAttack() }

    override val isAlive: Boolean
        get() = health > 0

    override val actor: Actor
        get() {
            val group = Group()
            val img = Image(MagellanGame.assets.get("sectorview_drone.png", Texture::class.java))
            img.setSize(dimensionsWindow.x, dimensionsWindow.y)
            group.setSize(dimensionsWindow.x, dimensionsWindow.y)
            group.addActor(img)
            for (routine in routines) {
                val imgRoutine = Image(routine.windowSprite)
                imgRoutine.setSize(dimensionsWindow.x, dimensionsWindow.y)
                group.setSize(dimensionsWindow.x, dimensionsWindow.y)
                group.addActor(imgRoutine)
            }
            return group
        }

    override val title = "DRONE"

    override val info: String
        get() {
            var s = "Faction: " + faction.toString()
            s += "\nHealth: " + health
            s += "\nLevel: " + maxNumberOfRoutines
            s += "\nRoutines: " + routines.size + "/" + maxNumberOfRoutines
            for (routine in routines) {
                s += "\n" + routine.routine!!
            }
            return s
        }

    init {
        prepareRenderingOnMap()

    }// The level of a drone decides how many routines it can handle
    // If not all routines are set, the routines become more powerful

    fun addRoutine(routine: DroneRoutine) {
        if (routines.size >= maxNumberOfRoutines)
            return
        if (hasRoutine(routine.javaClass))
            return

        routines.add(routine)
        routines.forEach { _routine -> _routine.powerLevel = maxNumberOfRoutines.toFloat() / routines.size }
    }

    override fun moveTo(sector: Sector) {
        super.moveTo(sector)
    }

    override fun passiveTick() {
        routines
                .filter { it !is DroneRoutineFighting }
                .forEach { it.tick() }
    }

    override fun activeTick() {
        (0 until routines.size)
                .filter { routines.get(it) is DroneRoutineFighting }
                .forEach { routines.get(it).tick() }
    }

    override fun receiveDamage(damage: Int): Boolean {
        health -= damage

        routines.forEach { routine -> routine.receiveDamage(damage) }

        if (health <= 0) destroy()
        return true
    }

    override fun destroy() {
        if (destroyed) return

        destroyed = true

        if (faction === GameObj.Factions.PLAYER) {
            MagellanGame.instance.mapScreen.log.addEntry("You have lost a drone", sector)
        }
        dispose()
        routines.clear()
    }

    override fun dispose() {
        this.sector.gameObjs.removeValue(this, true)
    }

    override fun inBattle() = battle != null

    override fun setBattle(battle: Battle?) {
        this.battle = battle
    }

    override fun prepareRenderingOnMap() {
        spriteVessel = Sprite(MagellanGame.assets.get("drone.png", Texture::class.java))
        spriteVessel.setSize(28f, 18f)
        spriteVessel.setOriginCenter()

        getFreeSectorSlot()
        getParkingPosition()

        spriteVessel.setPosition(parkingPosition.x, parkingPosition.y)
        spriteVessel.rotation = parkingAngle
    }

    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        super.render(delta)

        spriteVessel.draw(batch)

        for (routine in routines) {
            routine.render(batch, delta)
        }
    }

    private fun updateLists() {
        listItems = Array()

        MagellanGame.gameState.unlockedRoutines
                .filterNot { selectedRoutines.contains(it, false) }
                .forEach { listItems.add(it) }

        listLeft.setItems(listItems)
        listRight.setItems(selectedRoutines)
    }

    fun showSetupWindow() {
        val window = MagellanGame.instance.windowScreen.getWindow("SETUP DRONE LVL " + maxNumberOfRoutines)
        //window.setDebug(true, true);

        val skin = MagellanGame.instance.windowScreen.skin
        val windowContent = VerticalGroup()
        windowContent.fill()

        val lists = HorizontalGroup()
        lists.space(20f)
        lists.fill()
        //lists.debugAll();

        val leftGroup = VerticalGroup()
        val rightGroup = VerticalGroup()
        leftGroup.space(10f)
        rightGroup.space(10f)
        //leftGroup.debugAll();

        leftGroup.fill()
        rightGroup.fill()

        val menu = HorizontalGroup()
        menu.padTop(20f)

        listLeft = List(skin)
        val scrollPaneLeft = ScrollPane(listLeft)

        listRight = List(skin)
        val scrollPaneRight = ScrollPane(listRight)

        leftGroup.addActor(Label("Available routines", skin))
        leftGroup.addActor(scrollPaneLeft)
        rightGroup.addActor(Label("Installed routines", skin))
        rightGroup.addActor(scrollPaneRight)

        val doneButton = TextButton("Done", skin)
        val addButton = TextButton("Add routine", skin)
        val removeButton = TextButton("Remove routine", skin)

        addButton.addListener(object : ChangeListener() {

            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val selectedRoutine = listLeft.selected as DroneRoutine.ROUTINES

                if (selectedRoutine == null || selectedRoutines.size >= maxNumberOfRoutines) return
                if (!selectedRoutines.contains(selectedRoutine, false))
                    selectedRoutines.add(selectedRoutine)

                updateLists()
            }
        })

        removeButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val selectedRoutine = listRight.selected as DroneRoutine.ROUTINES ?: return

                selectedRoutines.removeValue(selectedRoutine, false)
                updateLists()
            }
        })

        val drone = this

        doneButton.addListener(object : ChangeListener() {

            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                clearRoutines()

                for (routine in selectedRoutines) {
                    when (routine) {
                        DroneRoutine.ROUTINES.ATTACKING -> addRoutine(DroneRoutineFighting(drone))
                        DroneRoutine.ROUTINES.SCOUTING -> addRoutine(DroneRoutineScouting(drone))
                        DroneRoutine.ROUTINES.ADVSCOUTING -> addRoutine(DroneRoutineScoutingAdvanced(drone))
                        DroneRoutine.ROUTINES.MINING -> addRoutine(DroneRoutineMining(drone))
                        DroneRoutine.ROUTINES.FOLLOWING -> addRoutine(DroneRoutineFollowing(drone))
                        DroneRoutine.ROUTINES.REPAIRING -> addRoutine(DroneRoutineReparing(drone))
                    }
                }

                MagellanGame.instance.windowScreen.closeWindow()
                MagellanGame.instance.windowScreen.drawSurroundings()
                MagellanGame.soundFx.upgrade.play(0.5f)
            }
        })

        selectedRoutines = Array()

        for (_routines in routines) {
            selectedRoutines.add(_routines.routine)
        }
        updateLists()

        menu.addActor(addButton)
        menu.addActor(removeButton)
        menu.addActor(doneButton)

        lists.addActor(leftGroup)
        lists.addActor(rightGroup)

        windowContent.addActor(lists)
        windowContent.addActor(menu)
        window.add(windowContent)
    }

    override fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        val interactions = OrderedMap<String, IInteractable.Interaction>()

        interactions.put("setup", object : IInteractable.Interaction {

            override fun interact() {
                showSetupWindow()
            }
        })

        interactions.put("load into ship", object : IInteractable.Interaction {

            override fun interact() {
                MagellanGame.instance.universe.playerShip.drones.add(maxNumberOfRoutines)
                MagellanGame.instance.windowScreen.getWindow("Drone", "Drone LVL $maxNumberOfRoutines boarded.")

                dispose()

                MagellanGame.gameState.updateNumberOfDrones()
            }
        })

        routines.forEach { routine -> interactions.putAll(routine.getInteractions(with)) }

        return interactions
    }

    override fun shootAt(target: IDestroyable): Int {
        target.receiveDamage(attack)
        return attack
    }

    private fun hasRoutine(routineClass: Class<*>): Boolean {
        return routines.any { it.javaClass == routineClass }
    }

    fun clearRoutines() {
        routines.clear()
    }

    companion object {
        var price = 1000
    }
}
