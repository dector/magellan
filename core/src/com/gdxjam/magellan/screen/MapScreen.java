package com.gdxjam.magellan.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gdxjam.magellan.*;

/**
 * Created by lolcorner on 19.12.2015.
 */
public class MapScreen extends BaseScreen {
    private final Sprite pixel;
    private final Sprite dot;
    private final Sprite circle;
    private final FitViewport mapViewport;
    private float zoom = 1;
    private float panX;
    private float panY;
    private Vector3 touch = new Vector3();
    private Circle touchCircle = new Circle(0,0,30);
    private Universe universe;
    private Vector2 tmp1;
    public OrthographicCamera camera;
    public SpriteBatch mapBatch;

    public MapScreen(MagellanGame game){
        super(game);
        universe = game.universe;
        mapBatch = new SpriteBatch();
        pixel = new Sprite(MagellanGame.assets.get("pixel.png", Texture.class));
        circle = new Sprite(MagellanGame.assets.get("circle.png", Texture.class));
        dot = new Sprite(MagellanGame.assets.get("dot.png", Texture.class));
        camera = new OrthographicCamera();
        mapViewport = new FitViewport(1280, 720, camera);
        camera.position.x = universe.playerShip.sector.position.x;
        camera.position.y = universe.playerShip.sector.position.y;
        mapViewport.apply();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        camera.update();
        mapBatch.setProjectionMatrix(camera.combined);

        camera.zoom = zoom;
        camera.position.x += panX;
        camera.position.y += panY;

        mapBatch.begin();

        for(Sector sector : universe.sectors){
            if(!sector.discovered && !MagellanGame.DEBUG) continue;
            for(Sector _sector : sector.connectedSectors){
                tmp1 = sector.position.cpy().sub(_sector.position);
                if(sector == universe.playerShip.sector || _sector == universe.playerShip.sector)
                    pixel.setColor(Color.WHITE);
                else
                    pixel.setColor(Color.CYAN);
                pixel.setSize(tmp1.len(), 1);
                pixel.setOrigin(0,.5f);
                pixel.setPosition(_sector.position.x-.5f, _sector.position.y-.5f);
                pixel.setRotation(tmp1.angle());
                pixel.draw(mapBatch);
                dot.setColor(Color.CYAN);
                dot.setSize(10,10);
                dot.setPosition(_sector.position.x - 5, _sector.position.y - 5);
                dot.draw(mapBatch);
            }
        }
        for(Sector sector : universe.sectors){
            if(!sector.discovered && !MagellanGame.DEBUG) continue;
            dot.setColor(Color.CYAN);
            dot.setSize(10,10);
            dot.setPosition(sector.position.x - 5, sector.position.y - 5);
            dot.draw(mapBatch);
            for(GameObj gameObj:sector.gameObjs){
                Sprite sprite = dot;
                if(gameObj instanceof PlayerShip)
                    sprite = circle;
                sprite.setColor(gameObj.colorOnMap);
                sprite.setSize(gameObj.sizeOnMap,gameObj.sizeOnMap);
                sprite.setPosition(sector.position.x - gameObj.sizeOnMap/2, sector.position.y - gameObj.sizeOnMap/2);
                sprite.draw(mapBatch);
            }
        }
        mapBatch.end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mapViewport.update(width, height);
    }

    public boolean keyDown(int keyCode) {
        switch (keyCode){
            case Input.Keys.W:
                panY = 10*camera.zoom;
                break;
            case Input.Keys.A:
                panX = -10*camera.zoom;
                break;
            case Input.Keys.S:
                panY = -10*camera.zoom;
                break;
            case Input.Keys.D:
                panX = 10*camera.zoom;
                break;
            case Input.Keys.Z:
                MagellanGame.DEBUG = !MagellanGame.DEBUG;
                break;
        }
        return false;
    }

    public boolean keyUp(int keyCode) {

        switch (keyCode){
            case Input.Keys.W:
                panY = 0;
                break;
            case Input.Keys.A:
                panX = 0;
                break;
            case Input.Keys.S:
                panY = 0;
                break;
            case Input.Keys.D:
                panX = 0;
                break;
        }
        return false;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        camera.unproject(touch.set(x, y, zoom));
        touchCircle.setPosition(touch.x, touch.y);
        if(universe.getSectorsInCircle(touchCircle).size > 0){
            Sector sector = universe.getSectorsInCircle(touchCircle).get(0);
            if(universe.playerShip.sector.connectedSectors.contains(sector, true)){
                universe.playerShip.moveTo(sector);
                universe.tick();
            }
        }
        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        panX = panY = 0;
        return false;
    }

    public boolean touchDragged(int x, int y, int i) {
        camera.unproject(touch.set(x, y, zoom));
        panX += touchCircle.x - touch.x;
        panY += touchCircle.y - touch.y;
        touchCircle.x = touch.x;
        touchCircle.y = touch.y;
        return true;
    }

    public boolean scrolled(int i) {
        zoom += i*.5;
        if(zoom < 1) zoom = 1;
        return false;
    }

}