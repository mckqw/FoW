package com.fow.handlers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.Actor;

/*
 * Credit to noone
 * https://stackoverflow.com/questions/24080272/libgdx-how-to-make-tiled-map-tiles-clickable
*/

public class TiledMapActor extends Actor {

    private TiledMap tiledMap;

    private TiledMapTileLayer tiledLayer;

    private Cell cell;

    public TiledMapActor(TiledMap tiledMap, TiledMapTileLayer tiledLayer, Cell cell) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        this.cell = cell;
    }

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public TiledMapTileLayer getTiledLayer() {
		return tiledLayer;
	}

	public void setTiledLayer(TiledMapTileLayer tiledLayer) {
		this.tiledLayer = tiledLayer;
	}

	public TiledMap getTiledMap() {
		return tiledMap;
	}

	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

}