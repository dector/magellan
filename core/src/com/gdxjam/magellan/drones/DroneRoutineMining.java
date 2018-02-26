package com.gdxjam.magellan.drones;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.gdxjam.magellan.MagellanGame;
import com.gdxjam.magellan.gameobj.GameObj;
import com.gdxjam.magellan.gameobj.MeteoroidField;

/**
 * Created by saibotd on 27.12.15.
 */
public class DroneRoutineMining extends DroneRoutine{

    private int resourcesPerTick = 2;
    private boolean noResMessageShown = false;

    public DroneRoutineMining(Drone drone) {
        super(drone);
        routine = ROUTINES.MINING;
        sprite = new Sprite(MagellanGame.assets.get("drone_mine.png", Texture.class));
        windowSprite = new Sprite(MagellanGame.assets.get("sectorview_drone_mine.png", Texture.class));
    }

    public void tick(){
        Array<MeteoroidField> metroidFields = new Array();
        for(GameObj gameObj : drone.sector.gameObjs){
            if(gameObj instanceof MeteoroidField){
                metroidFields.add((MeteoroidField) gameObj);
            }
        }
        int resCounter = 0;
        for(MeteoroidField meteoroidField :metroidFields){
            switch (meteoroidField.resource){
                case 1:
                    MagellanGame.gameState.resource1 += meteoroidField.mine(Math.round(resourcesPerTick * powerLevel));
                    resCounter += meteoroidField.resourceAmount;
                    break;
                case 2:
                    MagellanGame.gameState.resource2 += meteoroidField.mine(Math.round(resourcesPerTick * powerLevel));
                    resCounter += meteoroidField.resourceAmount;
                    break;
                case 3:
                    MagellanGame.gameState.resource3 += meteoroidField.mine(Math.round(resourcesPerTick * powerLevel));
                    resCounter += meteoroidField.resourceAmount;
                    break;

            }
        }

        if (drone.faction == GameObj.Factions.PLAYER && resCounter <= 0 && !noResMessageShown) {
            noResMessageShown = true;
            MagellanGame.instance.mapScreen.log.addEntry("Mining drone has no resources left", drone.sector);
        }
    }
}
