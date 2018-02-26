package com.gdxjam.magellan.gameobj;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdxjam.magellan.Colors;
import com.gdxjam.magellan.MagellanGame;
import com.gdxjam.magellan.Sector;


/**
 * Created by lolcorner on 19.12.2015.
 */
public class EnemyHomePlanet extends Planet {

    public EnemyHomePlanet(Sector sector) {
        super(sector);
        color = Colors.FACTION_ENEMY;
    }

    @Override
    public void prepareRenderingOnMap() {
        mapSprite = new Sprite(MagellanGame.assets.get("map_planet_"+visualType+".png", Texture.class));
        mapSprite.setColor(color);
        mapSprite.setSize(50,50);

        mapClaimedSprite = new Sprite(MagellanGame.assets.get("map_planet_claimed.png", Texture.class));
        mapClaimedSprite.setSize(15, 20);
    }

    @Override
    public Actor getActor() {
        Image image = new Image(MagellanGame.assets.get("sectorview_planet_"+visualType+".png", Texture.class));
        image.setColor(color);
        image.setFillParent(true);
        return image;
    }


}
