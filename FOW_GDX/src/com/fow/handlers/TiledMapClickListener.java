package com.fow.handlers;

import java.io.IOException;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fow.handlers.GameButton;
import com.fow.handlers.GameStateManager;
import com.fow.main.Game;
import com.fow.states.Play;

/*
 * Credit to noone
 * https://stackoverflow.com/questions/24080272/libgdx-how-to-make-tiled-map-tiles-clickable
*/


public class TiledMapClickListener extends ClickListener {

    private TiledMapActor actor;

    public TiledMapClickListener(TiledMapActor actor) {
        this.actor = actor;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
    	
    	TiledMapActor a = (TiledMapActor) event.getTarget();
    	
    	int hovX = a.getCell().getTile().getProperties().get("X", Integer.class);
    	int hovY = a.getCell().getTile().getProperties().get("Y", Integer.class);
    	int higX = Play.highlightDX;
    	int higY = Play.highlightDY;
    	int dt = Math.abs(higX - hovX) + Math.abs(higY - hovY);
    	
    	if(("pawns").equals((a.getTiledLayer().getName()).trim()) && Play.moves != 0) {
	    	if(a.getCell().getTile().getProperties().get("name", String.class).equals("soldier") ||
	    			a.getCell().getTile().getProperties().get("name", String.class).equals("cmd")||
	    				a.getCell().getTile().getProperties().get("name", String.class).equals("wall")) {
		    	
		    	Play.highlightDX = hovX;
		    	Play.highlightDY = hovY;
		    	
		    	World world = Play.world;
		    	
		    	Play.clearBodies();
		    	
		    	BodyDef bdef = new BodyDef();
				bdef.position.set(hovX*32+16,hovY*32+16);
				bdef.type = BodyType.StaticBody;
				Body body = world.createBody(bdef);
				
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(16, 16);
				FixtureDef fdef = new FixtureDef();
				fdef.shape = shape;
				body.createFixture(fdef);
				
				Game.res.getSound("select_good").play();
	    	}
    	} else if(dt >= Play.moves) {
    		Game.res.getSound("select_bad").play();
    	}
    	if(!("pawns").equals((a.getTiledLayer().getName()).trim()) && Play.moves != 0) {
    		if(a.getCell().getTile().getProperties().get("name", String.class).equals("background"))
    			Game.res.getSound("select_bad").play();
    		else if(dt <= Play.moves) {
        		TiledMapTileLayer tl = (TiledMapTileLayer) actor.getTiledMap().getLayers().get("pawns");
        		AnimatedTiledMapTile gTile = (AnimatedTiledMapTile) tl.getCell(higX, higY).getTile();
        		
        		Play.locAr.getArray().indexOf(, arg1)
        		
        		MapProperties mp = gTile.getProperties();
        		tl.setCell(hovX, hovY, new Cell());
        		tl.getCell(hovX, hovY).setTile(gTile);
        		TiledMapTile tile = tl.getCell(hovX, hovY).getTile();
				tile.getProperties().putAll(mp);
				tile.getProperties().put("X", hovX);
				tile.getProperties().put("Y", hovY);
        		tl.setCell(higX, higY, null);
        		
        		
        		Iterator<Location> itr = Play.locAr.getArray().iterator();
        		Location check = new Location(higX,higY,a.getCell().getTile().getProperties().get("name", String.class));
        		
        		while(itr.hasNext()) {
        			Location hold = itr.next();
        			/*
        			try {
						System.out.println(hold.toJSONString());
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}*/
        			if(check.equals(hold)) {
        				hold.set(hovX, hovY);
        			}
        		}
        		
        		itr = Play.locAr.getArray().iterator();
        		TiledMapTileLayer clouds = (TiledMapTileLayer) 
        				actor.getTiledMap().getLayers().get("clouds");
        		
        		for(int X = 0; X < clouds.getWidth(); X++) {
        			for(int Y = 0; Y < clouds.getHeight(); Y++) {
        				Cell cell = new Cell();
        				cell.setTile(new AnimatedTiledMapTile(
        						1 / 12f, Play.fcloudTiles));
        				TiledMapTile ctile = cell.getTile();
        				ctile.getProperties().put("X", X);
        				ctile.getProperties().put("Y", Y);
        				ctile.getProperties().put("name", "cloud");
						clouds.setCell(X, Y, cell);
        			}
        		}
        		
        		Play.moves = Play.moves - dt;
        		Play.stage.refresh();
        		
        		while(itr.hasNext()) {
        			Location hold = itr.next();
        			System.out.println(hold.getX()+","+hold.getY());
        			calcClears(hold.getX(),hold.getY());
        		}
        		Game.res.getSound("select_good").play();
        	}
	    	Play.clearBodies();
	    	Play.highlightDX = -1;
		    Play.highlightDY = -1;
    	}
    }
    
    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    	TiledMapActor a = (TiledMapActor) event.getTarget();
    	int hovX = a.getCell().getTile().getProperties().get("X", Integer.class);
    	int hovY = a.getCell().getTile().getProperties().get("Y", Integer.class);
    	int higX = Play.highlightDX;
    	int higY = Play.highlightDY;
    	if(("pawns").equals((a.getTiledLayer().getName()).trim())) {
	    	if(a.getCell().getTile().getProperties().get("name", String.class).equals("soldier") ||
	    			a.getCell().getTile().getProperties().get("name", String.class).equals("cmd")||
	    				a.getCell().getTile().getProperties().get("name", String.class).equals("wall")) {
	    		Play.clickColor = new Color(1f, 0f, 0f, 0.2f);
	    	}
    	} else {
    		Play.clickColor = new Color(0f, 0f, 1f, 0.2f);
    	}
    	if(hovX != higX || hovY != higY) {
    		int dt = Math.abs(higX - hovX) + Math.abs(higY - hovY);
    		if(dt > Play.moves) {
    			Play.clickColor = new Color(1f, 0f, 0f, 0.2f);
    		}
    	}
    }
    
    private void calcClears(int row, int col){
    	TiledMapTileLayer clouds = (TiledMapTileLayer) actor.getTiledMap().getLayers().get("clouds");
    	for (int r = (row - 1); r < (row + 2); r++) {
            for (int c = (col - 1); c < (col + 2); c++) {
            	if( r >= 0 && c >= 0 && r < 20 && c < 20) {
            		if(clouds.getCell(r, c) != null) {
            			if(clouds.getCell(r, c).getTile(). 
            					getProperties().get("name",String.class).equals("cloud")) {
	            			clouds.setCell(r, c, null);
	            		}
            		}
            	}
            }
    	}
    }
}