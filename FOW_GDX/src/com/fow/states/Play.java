package com.fow.states;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fow.handlers.Background;
import com.fow.handlers.GameStateManager;
import com.fow.handlers.Location;
import com.fow.handlers.LocationArray;
import com.fow.handlers.LocationList;
import com.fow.handlers.TiledMapStage;
import com.fow.handlers.User;
import com.fow.main.Game;

public class Play extends GameState{

	public static World world;
	public static int highlightDY;
	public static int highlightDX;
	public static Color clickColor;
	public static int moves;
	public static TiledMapStage stage;
	public static Array<StaticTiledMapTile> fcloudTiles;
	
	public static LocationList locLs;
	
	private Box2DDebugRenderer b2br;
	private TiledMap map;
	private OrthogonalTiledMapRenderer tmr;
	
	private static FitViewport viewport;
	
	private static int TILE_MAP_SIZE;
	
	
	ShapeRenderer shapeRenderer;
	Vector2 drawBody;
	
	
	public Play(GameStateManager gsm) {
		super(gsm);
		
		/* 
		 * 1: READY
		 * 2: MOVING
		*/
		TILE_MAP_SIZE = 15*9;
		
		world = new World(new Vector2(0,0),true);
		b2br = new Box2DDebugRenderer();
		highlightDY = -1;
		highlightDX = -1;
		
		//moves = ThreadLocalRandom.current().nextInt(4, 8 + 1);
		moves = 6;
		
		new Thread(new Runnable() {
            @Override
            public void run(){
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	//Game.res.getMusic("menu").setLooping(true);
        		//Game.res.getMusic("menu").setVolume(0.5f);
        		//Game.res.getMusic("menu").play();
            }
        }).start();;
        
		
		shapeRenderer = new ShapeRenderer();
		locLs = new LocationList(new List<Location>());
		map = new TmxMapLoader().load("maps/gameMap.tmx");
		
		MapProperties prop = map.getProperties();

		int mapWidth = prop.get("width", Integer.class);
		int mapHeight = prop.get("height", Integer.class);
		
		int tilePixelWidth = prop.get("tilewidth", Integer.class);
		int tilePixelHeight = prop.get("tileheight", Integer.class);
		
		int mapPixelWidth = mapWidth * tilePixelWidth;
		int mapPixelHeight = mapHeight * tilePixelHeight;
		
		
		fcloudTiles  = new Array<StaticTiledMapTile>(TILE_MAP_SIZE);
		Array<StaticTiledMapTile> fSoldierTiles  = new Array<StaticTiledMapTile>(TILE_MAP_SIZE);
		Array<StaticTiledMapTile> fCmdiles  = new Array<StaticTiledMapTile>(TILE_MAP_SIZE);
		Array<StaticTiledMapTile> fWallTiles  = new Array<StaticTiledMapTile>(TILE_MAP_SIZE);
		Array<StaticTiledMapTile> fBKTiles  = new Array<StaticTiledMapTile>(TILE_MAP_SIZE);
		
		Iterator<TiledMapTile> tCloudIter = map.getTileSets().getTileSet("Cloud_Full").iterator();
		Iterator<TiledMapTile> tSoldierIter = map.getTileSets().getTileSet("soldier").iterator();
		Iterator<TiledMapTile> tCmdIter = map.getTileSets().getTileSet("Commander_white").iterator();
		Iterator<TiledMapTile> tWallIter = map.getTileSets().getTileSet("wall").iterator();
		Iterator<TiledMapTile> tBKIter = map.getTileSets().getTileSet("BlackTile").iterator();
		
		while(tBKIter.hasNext()) {
			TiledMapTile tile = tBKIter.next();
			if(tile != null)
			if(tile.getProperties().containsKey("animated") 
					&& tile.getProperties().get("animated",String.class).equals("true")) {
				fBKTiles.add((StaticTiledMapTile) tile);
			}
		}
		
		while(tCloudIter.hasNext()) {
			TiledMapTile tile = tCloudIter.next();
			if(tile != null)
			if(tile.getProperties().containsKey("animated") 
					&& tile.getProperties().get("animated",String.class).equals("true")) {
				fcloudTiles.add((StaticTiledMapTile) tile);
			}
		}
		
		while(tSoldierIter.hasNext()) {
			TiledMapTile tile = tSoldierIter.next();
			if(tile != null)
			if(tile.getProperties().containsKey("animated") 
					&& tile.getProperties().get("animated",String.class).equals("true")) {
				fSoldierTiles.add((StaticTiledMapTile) tile);
			}
		}
		
		while(tCmdIter.hasNext()) {
			TiledMapTile tile = tCmdIter.next();
			if(tile != null)
			if(tile.getProperties().containsKey("animated") 
					&& tile.getProperties().get("animated",String.class).equals("true")) {
				fCmdiles.add((StaticTiledMapTile) tile);
			}
		}
		
		while(tWallIter.hasNext()) {
			TiledMapTile tile = tWallIter.next();
			if(tile != null)
			if(tile.getProperties().containsKey("animated") 
					&& tile.getProperties().get("animated",String.class).equals("true")) {
				fWallTiles.add((StaticTiledMapTile) tile);
			}
		}
		
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("pawns");
		
		for(int x = 0; x < layer.getWidth(); x++) {
			for(int y = 0; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					TiledMapTile tile = cell.getTile();
					if(tile.getProperties().containsKey("animated") 
							&& tile.getProperties().get("animated",String.class).equals("true")) {
						if(tile.getProperties().get("name",String.class).equals("cloud")) {
							System.out.println(x+" "+y);
							MapProperties mp = tile.getProperties();
							cell.setTile(new AnimatedTiledMapTile(1 / 12f,  fcloudTiles));
							tile = cell.getTile();
							tile.getProperties().putAll(mp);
							tile.getProperties().put("X", x);
							tile.getProperties().put("Y", y);
						} else if(tile.getProperties().get("name",String.class).equals("cmd")) {
							MapProperties mp = tile.getProperties();
							cell.setTile(new AnimatedTiledMapTile(1 / 12f,  fCmdiles));
							tile = cell.getTile();
							tile.getProperties().putAll(mp);
							tile.getProperties().put("X", x);
							tile.getProperties().put("Y", y);
							locAr.getArray().add(new Location(x,y,"cmd"));
						} else if(tile.getProperties().get("name",String.class).equals("soldier")) {
							MapProperties mp = tile.getProperties();
							cell.setTile(new AnimatedTiledMapTile(1 / 16f,  fSoldierTiles));
							tile = cell.getTile();
							tile.getProperties().putAll(mp);
							tile.getProperties().put("X", x);
							tile.getProperties().put("Y", y);
							locAr.getArray().add(new Location(x,y,"soldier"));
						} else if(tile.getProperties().get("name",String.class).equals("wall")) {
							MapProperties mp = tile.getProperties();
							cell.setTile(new AnimatedTiledMapTile(1 / 12f,  fWallTiles));
							tile = cell.getTile();
							tile.getProperties().putAll(mp);
							tile.getProperties().put("X", x);
							tile.getProperties().put("Y", y);
							locAr.getArray().add(new Location(x,y,"wall"));
						}	
					}
				}
			}
		}
		
		TiledMapTileLayer ground = (TiledMapTileLayer) map.getLayers().get("ground");
		Iterator<TiledMapTile> itr = map.getTileSets().getTileSet("dirt-tiles").iterator();
		StaticTiledMapTile gTile = (StaticTiledMapTile) itr.next();
		
		for(int x = 0; x < ground.getWidth(); x++) {
			for(int y = 0; y < ground.getHeight(); y++) {
				Cell cell = ground.getCell(x, y);
				TiledMapTile tile = cell.getTile();
				MapProperties mp = tile.getProperties();
				cell.setTile(new StaticTiledMapTile(gTile));
				tile = cell.getTile();
				tile.getProperties().putAll(mp);
				tile.getProperties().put("X", x);
				tile.getProperties().put("Y", y);
			}
		}
		
		TiledMapTileLayer cloud = (TiledMapTileLayer) map.getLayers().get("clouds");
		itr = map.getTileSets().getTileSet("Cloud_Full").iterator();
		StaticTiledMapTile cTile = (StaticTiledMapTile) itr.next();
		
		for(int x = 0; x < cloud.getWidth(); x++) {
			for(int y = 0; y < cloud.getHeight(); y++) {
				Cell cell = cloud.getCell(x, y);
				if(cell != null) {
					TiledMapTile tile = cell.getTile();
					MapProperties mp = tile.getProperties();
					cell.setTile(new AnimatedTiledMapTile(1 / 12f,  fcloudTiles));
					tile = cell.getTile();
					tile.getProperties().putAll(mp);
					tile.getProperties().put("X", x);
					tile.getProperties().put("Y", y);
				}
			}
		}
		/*
		try {
			game.objectToServer.writeObject(locAr.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		clickColor = new Color(0f, 0f, 1f, 0.2f);
		
		//Impose an input grid
		stage = new TiledMapStage(map);
		stage.getViewport().setCamera(cam);
		Gdx.input.setInputProcessor(stage);
		
		
		// Instantiate Orthogonal Camera
		tmr = new OrthogonalTiledMapRenderer(map, .125f);
		cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
		
	}

	public void handleInput(){
		
	}
	
	public void update(float dt) {
		world.step(dt, 6, 2);
		if(Game.PLAY_STATE == 2 && moves == 0) {
			Game.PLAY_STATE = 1;
			moves = 6;
		}
	}
	
	public static void clearBodies()
	{
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for(int i = 0; i < bodies.size; i++)
        {
            if(!world.isLocked())
                    world.destroyBody(bodies.get(i));
        }
	}
	
	public void render() {
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		AnimatedTiledMapTile.updateAnimationBaseTime();
		
		tmr.setView(cam);
		tmr.render();
		
		//b2br.render(world, cam.combined);
		
		//sb.setProjectionMatrix(cam.combined);
		
		//sb.begin();
		
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.setColor(clickColor);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect((com.fow.main.Game.V_WIDTH*Game.SCALE)/9 * highlightDX, (com.fow.main.Game.V_HEIGHT*Game.SCALE)/15 * highlightDY,
		(com.fow.main.Game.V_WIDTH*Game.SCALE)/9, (com.fow.main.Game.V_HEIGHT*Game.SCALE)/15);
		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public void dispose() {
		
	}
}
