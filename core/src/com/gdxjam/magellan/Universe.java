package com.gdxjam.magellan;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gdxjam.magellan.gameobj.GameObj;
import com.gdxjam.magellan.gameobj.MeteoroidField;
import com.gdxjam.magellan.gameobj.Planet;
import com.gdxjam.magellan.gameobj.Shop;
import com.gdxjam.magellan.ships.AiShipFighter;
import com.gdxjam.magellan.ships.AiShipSettler;
import com.gdxjam.magellan.ships.PlayerShip;

public class Universe {
    public PlayerShip playerShip;
    public Array<Sector> sectors;
    public int size = 3000;
    private MagellanGame game;
    public Sector bottomLeft;
    public Sector topRight;
    public Universe(MagellanGame game){
        this.game = game;
        sectors = new Array();
        for(int i = 0; i < size/2; i++){
            addRandomSector();
        }
        connectSectors();
        bottomLeft = sectors.random();
        topRight = sectors.random();
        for(Sector sector : sectors){
            if(sector.position.x < bottomLeft.position.x && sector.position.y < bottomLeft.position.y){
                bottomLeft = sector;
            }
            if(sector.position.x > topRight.position.x && sector.position.y > topRight.position.y){
                topRight = sector;
            }
        }
        playerShip = new PlayerShip(bottomLeft);
    }

    public boolean addSector(Sector sector) {
        for(Sector _sector : sectors){
            if(sector.circleAlone.contains(_sector.position)){
                return false;
            }
        }
        sectors.add(sector);
        return true;
    }

    public Array<Sector> getSectorsInCircle(Circle circle) {
        Array<Sector> result = new Array();
        for(Sector sector : sectors){
            if(circle.contains(sector.position)){
                result.add(sector);
            }
        }
        return result;
    }

    public Array<Sector> getSectorsInRectangle(Rectangle rectangle) {
        Array<Sector> result = new Array();
        for(Sector sector : sectors){
            if(rectangle.contains(sector.position)){
                result.add(sector);
            }
        }
        return result;
    }

    public Array<GameObj> getGameObjs(Class objType) {
        Array<GameObj> result = new Array();
        for(Sector sector : sectors){
            for (GameObj gameObj: sector.gameObjs) {
                if (objType == gameObj.getClass()) {
                    result.add(gameObj);
                }
            }
        }
        return result;
    }


    public void tick() {

        MagellanGame.gameState.progressYear();



        for(int i = 0; i < sectors.size; i++){
            for(int j = 0; j < sectors.get(i).gameObjs.size; j++){
                sectors.get(i).gameObjs.get(j).tick();
            }
        }

        MagellanGame.gameState.updatePopulationCount();
        MagellanGame.gameState.getPlanetIncome();
        MagellanGame.gameState.updateNumberOfDrones();





        if (MagellanGame.gameState.YEARS_PASSED == 1) {
            addEnemies(50, 5);
        }

        if (MagellanGame.gameState.YEARS_PASSED % 20 == 0) {
            addEnemies(10, 1);
        }


    }

    public void addRandomSector(){
        int x = (int) Math.round(Math.random() * size);
        int y = (int) Math.round(Math.random() * size);
        Sector newSector = new Sector(x,y);
        if(addSector(newSector)) {
            if(Math.random() < .2) {
                new Planet(newSector);
                newSector.hasPlanet = true;
            }
            if (Math.random() < .3) {
                new MeteoroidField(newSector);
            }
            if (Math.random() < .05) {
                new Shop(newSector);
            }
        }
    }

    public void connectSectors(){
        for(int i = 0; i < sectors.size; i++){
            Sector sector = sectors.get(i);
            if(sector.connectedSectors.size < 2){
                sector.addConnections(getSectorsInCircle(sector.circleConnect));
            }
        }
        for(int i = 0; i < sectors.size; i++){
            Sector sector = sectors.get(i);
            if(sector.connectedSectors.size == 0){
                sectors.removeValue(sector, true);
            }
        }

    }


    public void addEnemies(int numberFighters, int numberSettlers) {
        for(int i = 0; i < numberFighters; i++){
            final AiShipFighter fighter = new AiShipFighter(topRight);
            fighter.prepareRenderingOnMap();
        }
        for(int i = 0; i < numberSettlers; i++){
            final AiShipSettler settler = new AiShipSettler(topRight);
            settler.prepareRenderingOnMap();
        }
    }


}
