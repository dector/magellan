package com.gdxjam.magellan.gameobj

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.OrderedMap
import com.gdxjam.magellan.MagellanGame
import com.gdxjam.magellan.Sector
import com.gdxjam.magellan.drones.DroneRoutine
import com.gdxjam.magellan.shopitem.ShopItem
import com.gdxjam.magellan.shopitem.ShopItemDrone
import com.gdxjam.magellan.shopitem.ShopItemDroneRoutine
import com.gdxjam.magellan.shopitem.ShopItemUpgrade
import com.gdxjam.magellan.utils.texture

/**
 * Created by Felix on 29.12.2015.
 */
class Shop(sector: Sector) : MovingGameObj(sector), IDrawableWindow, IDrawableMap, IInteractable {

    private val inventory = Array<ShopItem>()
    private lateinit var _info: Label
    private var lastSelectedIndex = -1
    private var droneBought = false

    override val info: String
        get() = StringBuilder().apply {
            appendln("INTERCOM: Hello good friend! We have drones, upgrades,\nanything you need!\nIf you have the credits, that is.")
            if (MagellanGame.gameState.credits == 0) {
                appendln("INTERCOM: Hey there! Weird, our scanners pick up 0 credits on your ship\nand no, we won't accept frozen lifeforms as a substitute!")
                appendln("\nHow about this:\nYou populate some planets with those creatures\nand they will generate credits for you.")
                appendln("\nYou bring those credits to our trading outposts\nand we sell you cool stuff! Deal? Deal!")
            }
        }.toString()

    //image.setScale(.4f);

    override val actor: Actor
        get() = Image(MagellanGame.assets.texture("sectorview_shop.png"))

    override val title = "Trading Post"

    private fun fillInventory() {
        inventory.clear()
        inventory.add(ShopItemDrone(1))
        inventory.add(ShopItemDrone(2))
        inventory.add(ShopItemDrone(3))
        inventory.add(ShopItemDrone(4))
        inventory.add(ShopItemDrone(5))

        if (!MagellanGame.gameState.unlockedRoutines.contains(DroneRoutine.ROUTINES.ATTACKING, false))
            inventory.add(ShopItemDroneRoutine(DroneRoutine.ROUTINES.ATTACKING, 6000))
        if (!MagellanGame.gameState.unlockedRoutines.contains(DroneRoutine.ROUTINES.ADVSCOUTING, false))
            inventory.add(ShopItemDroneRoutine(DroneRoutine.ROUTINES.ADVSCOUTING, 1200))
        if (!MagellanGame.gameState.unlockedRoutines.contains(DroneRoutine.ROUTINES.FOLLOWING, false))
            inventory.add(ShopItemDroneRoutine(DroneRoutine.ROUTINES.FOLLOWING, 4000))

        inventory.add(ShopItemUpgrade(MagellanGame.instance.universe.playerShip.attack * 2 * 430, ShopItemUpgrade.UpgradeType.ATTACK))
        inventory.add(ShopItemUpgrade(MagellanGame.instance.universe.playerShip.maxHealth * 2 * 320, ShopItemUpgrade.UpgradeType.HEALTH))
        if (MagellanGame.instance.universe.playerShip.shield < .5)
            inventory.add(ShopItemUpgrade(((MagellanGame.instance.universe.playerShip.shield * 10 + 1) * 3500).toInt(), ShopItemUpgrade.UpgradeType.SHIELD))
    }

    override fun getInteractions(with: GameObj): OrderedMap<String, IInteractable.Interaction> {
        val interactions = OrderedMap<String, IInteractable.Interaction>()
        interactions.put("Buy", object : IInteractable.Interaction {
            override fun interact() {
                showInventoryWindow()
            }
        })

        /*
        interactions.put("Sell", new Interaction() {
            @Override
            public void interact() {
                showInteractionWindow();
            }
        });
        */

        return interactions
    }

    fun showInventoryWindow() {
        fillInventory()

        MagellanGame.instance.windowScreen.closeWindow()

        val window = MagellanGame.instance.windowScreen.getWindowWithoutClose("Buy")
        val skin = MagellanGame.instance.windowScreen.skin
        val windowContent = VerticalGroup()
        val listAndInfo = HorizontalGroup()
        listAndInfo.space(20f)

        val menu = HorizontalGroup()
        menu.padTop(20f)

        val list = List<Label>(skin)
        _info = Label("", skin, "inlay")

        val scrollPane = ScrollPane(list)

        val buyButton = TextButton("Buy", skin)
        val doneButton = TextButton("Done", skin)

        listAndInfo.addActor(scrollPane)
        listAndInfo.addActor(_info)

        _info.setFillParent(true)

        //scrollPane.setFillParent(true);

        val listItems = Array<String>()

        for (item in inventory) {
            listItems.add(item.title)
        }
        list.setItems(listItems)

        list.addListener(object : ChangeListener() {

            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                selectItem(list.getSelectedIndex())
            }
        })

        buyButton.addListener(object : ChangeListener() {

            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val item = inventory.get(list.getSelectedIndex())

                if (item.price <= MagellanGame.gameState.credits) {
                    MagellanGame.gameState.credits -= item.price

                    item.buy(MagellanGame.instance.universe.playerShip)
                    MagellanGame.soundFx.buy.play(0.7f)

                    droneBought = item is ShopItemDrone
                } else {
                    MagellanGame.soundFx.nope.play(0.7f)
                }

                MagellanGame.gameState.updateNumberOfDrones()
                showInventoryWindow()
            }
        })

        doneButton.addListener(object : ChangeListener() {

            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                MagellanGame.instance.windowScreen.closeWindow()

                if (!MagellanGame.gameState.droneInfoShown && droneBought) {
                    val s = StringBuilder()
                            .appendln("Drones can be deployed by clicking on your ship")
                            .appendln("\non the left side. After deploying a drone,")
                            .appendln("\nbe sure to click on it and setup routines.")
                            .appendln("\ndepending on the level, a drone can hold up to 5")
                            .appendln("\nof them. If you setup less routines than maximum")
                            .appendln("\nthe set up routines become more powerful.").toString()
                    MagellanGame.instance.windowScreen.getWindow("INFO", s)
                    MagellanGame.gameState.droneInfoShown = true
                }
            }
        })

        lastSelectedIndex = MathUtils.clamp(lastSelectedIndex, 0, inventory.size - 1)

        if (lastSelectedIndex != -1) {
            list.setSelectedIndex(lastSelectedIndex)
            selectItem(lastSelectedIndex)
        } else {
            list.setSelectedIndex(0)
            selectItem(0)
        }

        menu.addActor(buyButton)
        menu.addActor(doneButton)
        windowContent.addActor(listAndInfo)
        windowContent.addActor(menu)
        window.add(windowContent)
    }

    override fun moveTo(sector: Sector) {}

    private fun selectItem(index: Int) {
        val item = inventory.get(index)
        _info.setText(item.description + "\n\nPrice: " + item.price)
        lastSelectedIndex = index
    }


    override fun prepareRenderingOnMap() {
        spriteVessel = Sprite(MagellanGame.assets.get("shop.png", Texture::class.java))
        spriteVessel.setSize(16f, 16f)
        spriteVessel.setOriginCenter()

        sectorSlot = 0
        getParkingPosition()

        spriteVessel.setPosition(parkingPosition.x, parkingPosition.y)
        spriteVessel.rotation = parkingAngle
    }


    override fun renderOnMap(batch: SpriteBatch, delta: Float) {
        super.render(delta)
        spriteVessel.draw(batch)
    }
}
