package com.fow.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fow.handlers.Content;
import com.fow.handlers.GameStateManager;
import com.fow.handlers.User;

public class Game implements ApplicationListener{

	//Public Game Data
	public static final String TITLE = "Fog Of War";
	public static final int V_WIDTH = 308;//288	
	public static final int V_HEIGHT = 500;	//480
	public static int SCALE;
	public static final float STEP = 1 / 60f;
	public static final String BACKGROUND_IMAGE_PATH = "data/Background.jpg";
	
	//Accumulated time
	private float accum;
	
	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudcam;
	
	private GameStateManager gsm;
	
	public static Content res;
	
	// Input and output streams from/to server
	public static DataInputStream fromServer;
	public static DataOutputStream toServer;
	public static ObjectOutputStream objectToServer;
	public static OutputStream serverOutputStream;
	public static Socket socket;
	
	// Host IP
	public static String host = "127.0.0.1";
	
	public static User user;
	
	public static int PLAY_STATE = 1;
	
	//When the application loads
	@Override
	public void create() {
		
		res = new Content();
		//res.loadTexture("data/menu.png");
		
		res.loadSound("sfx/select_good.wav");
		res.loadSound("sfx/select_bad.wav");
		res.loadSound("sfx/menu_select.wav");
		
		res.loadMusic("music/menu.ogg");
		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		
		hudcam = new OrthographicCamera();
		hudcam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		
		gsm = new GameStateManager(this);
	}
	
	//When the application ends
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	//the game loop
	@Override
	public void render() {
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
		}
	}

	public static void updateUserData(String col, String newData, int userid) {
		  col.replaceAll("[^a-zA-Z&&^-]", "").toLowerCase();
		  newData.replaceAll("[^a-zA-Z&&^-]", "").toLowerCase();
	      try {
	      Class.forName("com.mysql.jdbc.Driver");
	      Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FogOFWar", "root", "");
	      Statement smt = con.createStatement(); //Create Statement to interact
	      smt.executeUpdate("UPDATE `users` SET `" + col + "` = '" + newData + "' WHERE `UserId` = " + userid);
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }

	public static void checklocalName() throws IOException {
		BufferedReader br;
		try {
	      br = new BufferedReader(new FileReader("name.txt"));
		  String line;
		  boolean nameFound = false;
		  line = br.readLine();
		  if ((line != null) && (line != "")) {
			    nameFound = true;
		  }

		  if (!nameFound) {
			  //globalStage.setScene(nameCreationScene);
		  } else if(isNameinDB(line)) {
			  user = getDBUser(line);
			  //loadGameBrowserScene();
		  }
		  br.close();
		  } catch (FileNotFoundException e) {
			  System.out.println("FILE NOT FOUND!");
			  PrintWriter writer = new PrintWriter("name.txt", "UTF-8");
			  writer.close();
			  //globalStage.setScene(nameCreationScene);
		  }
	  }

	  public static boolean isNameinDB(String text) {
		      try {Class.forName("com.mysql.jdbc.Driver");
		        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FogOFWar", "root", "");
		        Statement smt = con.createStatement(); //Create Statement to interact
		        ResultSet r = smt.executeQuery("select * from Users;");
		        text = scrubText(text);
		        while (r.next()) {
		        	if(r.getString("name").equalsIgnoreCase(text)) {
		        		return true;
		        	}
		        }
		      }
		      catch (Exception ex) {
		        ex.printStackTrace();
		      }
		  return false;
	  }
	  public static User getDBUser(String text) {
	      try {Class.forName("com.mysql.jdbc.Driver");
	        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FogOFWar", "root", "");
	        Statement smt = con.createStatement(); //Create Statement to interact
	        ResultSet r = smt.executeQuery("select * from Users;");
	        text = scrubText(text);
	        System.out.println(text);
	        while (r.next()) {
	        	if(r.getString("name").equalsIgnoreCase(text)) {
	        		return new User(r.getString("name"),r.getInt("UserId"),r.getString("state"));
	        	}
	        }
	      }
	      catch (Exception ex) {
	        ex.printStackTrace();
	      }
	      return null;
	}

	  public static void createName(String text) {
		  text = scrubText(text);
		  try {Class.forName("com.mysql.jdbc.Driver");
	      Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FogOFWar", "root", "");
	      Statement smt = con.createStatement(); //Create Statement to interact
	      //String query = "INSERT INTO `users`(`name`, `state`) VALUES (" + text + ",In-Game)";
	      //System.out.println(text);
	      smt.executeUpdate("INSERT INTO `users`(`name`,`state`) VALUES ('"+ text +"','In-Game')");
	    }
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }
	  }
	  
	  public static String scrubText(String dirty) {
			dirty = scrub("\\s+", dirty, " ");
			return scrub("^\\s?|\\s?$", dirty, "");
		}

	  public static String scrub(String pattern, String text, String replace) {
			Pattern p = Pattern.compile(pattern);
			Matcher matcher = p.matcher(text);
			return matcher.replaceAll(replace);
		}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	public SpriteBatch getSpriteBatch() {
		return sb;
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
	
	public OrthographicCamera getHUDCamera() {
		return hudcam;
	}
	
	
}
