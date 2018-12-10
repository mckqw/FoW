package com.fow.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/*
 * Credit to noone
 * https://stackoverflow.com/questions/24080272/libgdx-how-to-make-tiled-map-tiles-clickable
*/


public class TiledMapStage extends Stage {

    private TiledMap tiledMap;

    public TiledMapStage(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        
        for (MapLayer layer : tiledMap.getLayers()) {
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
            createActorsForLayer(tiledLayer);
        }
    }

    private void createActorsForLayer(TiledMapTileLayer tiledLayer) {
        for (int x = 0; x < tiledLayer.getWidth(); x++) {
            for (int y = 0; y < tiledLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
				if(cell != null) {
	                TiledMapActor actor = new TiledMapActor(tiledMap, tiledLayer, cell);
	                int tileWidth = (int) (tiledLayer.getTileWidth() * .125);
	                int tileHeight = (int) (tiledLayer.getTileWidth() * .125);
	                //System.out.println(x * tileWidth+", "+y * tileHeight);
	                actor.setBounds(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
	                addActor(actor);
	                EventListener eventListener = new TiledMapClickListener(actor);
	                actor.addListener(eventListener);
				}
            }
        }
    }
    
    public void refresh() {
        for (MapLayer layer : tiledMap.getLayers()) {
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
            createActorsForLayer(tiledLayer);
        }
    }
}