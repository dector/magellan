package com.gdxjam.magellan.ships;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gdxjam.magellan.*;
import com.gdxjam.magellan.drones.Drone;
import com.gdxjam.magellan.drones.DroneRoutineScouting;
import com.gdxjam.magellan.screen.BaseScreen;

/**
 * Created by lolcorner on 20.12.2015.
 */
public class PlayerShip extends Ship implements IInteractable {

    public int HUMANS = 10000;
    public Array<Integer> drones = new Array();
    public Array<ShopItem> inventory;

    public PlayerShip(Sector sector) {
        super(sector);
        faction = Factions.PLAYER;
        inventory = new Array<ShopItem>();
        setSectorsDiscovered();
        drones.add(4);
    }

    public void moveTo(Sector sector) {
        super.moveTo(sector);
        setSectorsDiscovered();
        tweenManager.killAll();
        Timeline.createSequence()
                .push(Tween.to(this.spriteVessel, SpriteAccessor.ROTATION, 0.3f).target((float)Math.atan2(sector.position.y - lastSector.position.y, sector.position.x - lastSector.position.x)*180f/(float)Math.PI-90f))
                .push(Tween.to(this.spriteVessel, SpriteAccessor.POSITION_XY, 0.5f).target(sector.position.x + 20, sector.position.y - 30).ease(TweenEquations.easeInOutQuint))
                .push(Tween.to(this.spriteVessel, SpriteAccessor.ROTATION, 1f).target(50).ease(TweenEquations.easeInOutCubic))
                .push(Tween.to(this.spriteVessel, SpriteAccessor.POSITION_XY, 0.5f).target(sector.position.x + 12, sector.position.y - 22).ease(TweenEquations.easeInOutCubic)).delay(-0.2f)
        .start(tweenManager);
    }

    public void releaseDrone(int level){
        drones.removeValue(level, false);
        Drone drone = new Drone(this.sector, level);
        drone.faction = faction;
        drone.addRoutine(new DroneRoutineScouting(drone));
        MagellanGame.gameState.updateNumberOfDrones();
    }

    private void setSectorsDiscovered() {
        sector.visited = true;
        sector.discovered = true;
        for(Sector _sector : sector.connectedSectors){
            _sector.discovered = true;
        }
    }

    @Override
    public void prepareRenderingOnMap() {
        //spriteDot = new Sprite(MagellanGame.assets.get("circle.png", Texture.class));
        //spriteDot.setSize(24,24);
        //spriteDot.setColor(Color.YELLOW);

        spriteVessel = new Sprite(MagellanGame.assets.get("map_playership.png", Texture.class));
        spriteVessel.setSize(20, 20);
        spriteVessel.setOriginCenter();
        spriteVessel.setPosition(sector.position.x + 12, sector.position.y - 22);
        spriteVessel.setRotation(50);
        spriteVessel.setColor(MagellanColors.FACTION_PLAYER);
    }

    @Override
    public void renderOnMap(SpriteBatch batch, float delta) {
        super.render(delta);
        //spriteDot.setPosition(sector.position.x - spriteDot.getWidth()/2, sector.position.y - spriteDot.getHeight()/2);
        //spriteDot.draw(batch);

        spriteVessel.draw(batch);
    }

    @Override
    public ObjectMap<String, Interaction> getInteractions(GameObj with) {
        ObjectMap<String, Interaction> interactions = new ObjectMap();
        if (submenuOpen == "") {
            if (drones.size > 0) {
                interactions.put("release drone", new Interaction() {
                    @Override
                    public void interact() {
                        submenuOpen = "releasedrone";
                        showInteractionWindow();
                    }
                });
            }
        }
        if (submenuOpen == "releasedrone") {
            if (drones.contains(1,true)) {
                interactions.put("Level 1", new Interaction() {
                    @Override
                    public void interact() {
                        releaseDrone(1);
                        closeWindow();
                    }
                });
            }
            if (drones.contains(2,true)) {
                interactions.put("Level 2", new Interaction() {
                    @Override
                    public void interact() {
                        releaseDrone(2);
                        closeWindow();
                    }
                });
            }
            if (drones.contains(3,true)) {
                interactions.put("Level 3", new Interaction() {
                    @Override
                    public void interact() {
                        releaseDrone(3);
                        closeWindow();
                    }
                });
            }
            if (drones.contains(4,true)) {
                interactions.put("Level 4", new Interaction() {
                    @Override
                    public void interact() {
                        releaseDrone(4);
                        closeWindow();
                    }
                });
            }
            if (drones.contains(5,true)) {
                interactions.put("Level 5", new Interaction() {
                    @Override
                    public void interact() {
                        releaseDrone(5);
                        closeWindow();
                    }
                });
            }
        }

        return interactions;
    }

    @Override
    public String getTitle() {
        return "The Trinidad";
    }

    @Override
    public String getInfo() {
        String s = "Your ship.";
        s += "\nHealth: " + getHealth();
        s += "\nFrozen Humans: " + HUMANS;
        s += "\nDrones: " + drones.toString(", ");
        s += "\nEquipment: " + inventory.toString(", ");
        return s;
    }
}
