package com.fow.states;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fow.handlers.Background;
import com.fow.handlers.GameButton;
import com.fow.handlers.GameStateManager;
import com.fow.handlers.User;
import com.fow.main.Game;

public class LevelSelect extends GameState {
	
	
	private GameButton[][] buttons;
	
    private Skin skin;
    private Stage stage;
    private final Label srvMessages;
    private final Label UserNameLabel;
    private final Label Pass1Label;
    private final Label Pass2Label;
    
    
    private final TextButton button;
    private final TextButton CreateAccountButton;
    
    private final TextField UserName;
    private final TextField Pass1;
    private final TextField Pass2;
    private Thread ConnectionThread;
    private Runnable connect;
	private int stageHeight;
	private int stageWidth;
    
	public LevelSelect(GameStateManager gsm) {
		
		super(gsm);
		
		skin = new Skin(Gdx.files.internal("data/terra-mother-ui.json"));
        stage = new Stage();
        
        
        srvMessages = new Label("Searching for Server", skin );
        button = new TextButton("Cancel", skin, "default");
        CreateAccountButton = new TextButton("Create Account", skin, "default");
        UserName = new TextField("",skin);
        UserNameLabel = new Label("User Name", skin);
        Pass1 = new TextField("",skin);
        Pass1.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        Pass1.setPasswordMode(true);
        Pass1.setPasswordCharacter('*');
        
        Pass1Label = new Label("Pin", skin);
        Pass2 = new TextField("",skin);
        Pass2.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
        Pass2.setPasswordMode(true);
        Pass2.setPasswordCharacter('*');
        
        Pass2.setPasswordMode(true);
        Pass2Label = new Label("Re-Enter Pin", skin);
        
        Group UI = new Group();
        UI.addActor(srvMessages);
        UI.addActor(button);
        UI.addActor(UserName);
        UI.addActor(Pass1);
        UI.addActor(Pass2);
        UI.setZIndex(1);
        
        button.setDisabled(true);
        
        stageHeight = Gdx.graphics.getHeight();
        stageWidth = Gdx.graphics.getWidth();
        
        UserName.setWidth(stageWidth/3 * Game.SCALE);
        Pass1.setWidth(stageWidth/3 * Game.SCALE);
        Pass2.setWidth(stageWidth/3 * Game.SCALE);
        
        
        button.setWidth(200f * Game.SCALE);
        button.setHeight(20f * Game.SCALE);
        button.setPosition(stageWidth/2 - button.getWidth()/2, stageHeight/2 - 10f);
        srvMessages.setPosition(stageWidth/2 - srvMessages.getWidth()/2, stageHeight/2 + 25f);
        
        CreateAccountButton.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
            	UserName.getText();
            	if(Game.isNameinDB(UserName.getText())) {
            		srvMessages.setText("Name Already Exists");
            	} else {
            		System.out.println(UserName.getText());
            		gsm.setState(2);
            	}
            	
            }
        });
        
        button.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){
            	
            	if(button.getLabel().getText().toString().equals("Retry")) {
            		try {
            			ConnectionThread.join(500);
    				} catch (InterruptedException e1) {
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
            		ConnectionThread = new Thread(connect);
            		ConnectionThread.start();
            		
            	} else if(button.getLabel().getText().toString().equals("Cancel") && srvMessages.getText().equals("Logging In") ){
            		
            	} else if(button.getLabel().getText().toString().equals("Cancel") ){
	            	
            	} else {
            		gsm.setState(GameStateManager.PLAY);
	                button.setText("You clicked the button");
	                Game.res.getSound("menu_select").play();
            	}
            }
        });
        
        Background bg = new Background();
        bg.setZIndex(0);
        stage.addActor(bg);
        stage.addActor(UI);
        
        Gdx.input.setInputProcessor(stage);

		cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
		
		connect = () -> {
			//Attempt to connect to Game Server
			
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			
			if(tryConnection()) {
				srvMessages.setText("Established Connection");
				
				try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
				
				try {
					login();
				} catch (FileNotFoundException | XMLStreamException e) {
					e.printStackTrace();
				}
				//srvMessages.setText("Logging In");
				try {
					Game.PLAY_STATE = Game.fromServer.readInt();
					Game.objectToServer.writeObject(new User("Joe", 1, "waiting"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		ConnectionThread = new Thread(connect);
		ConnectionThread.start();
	}
	
	public boolean tryConnection() {
		// Initialize Server Socket
		try {
			button.getLabel().setText("Cancel");
			Game.socket = new Socket(Game.host, 8000);
			// Create an input stream to receive data from the server
			Game.fromServer = new DataInputStream(Game.socket.getInputStream());
	
			// Create an output stream to send data to the server
			Game.serverOutputStream = Game.socket.getOutputStream();
			Game.toServer = new DataOutputStream(Game.serverOutputStream);
	
			Game.objectToServer = new ObjectOutputStream(Game.serverOutputStream);
		} catch (UnknownHostException e) {
			srvMessages.setText("Server Not Found");
			button.getLabel().setText("Retry");
			button.setDisabled(false);
			return false;
		} catch (IOException e) {
			srvMessages.setText("Connection Refused");
			button.getLabel().setText("Retry");
			button.setDisabled(false);
			return false;
		}
		return true;
	}
	
	public void login() throws FileNotFoundException, XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // Setup a new eventReader
        InputStream in = new FileInputStream("data/gameData.xml");
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        while (eventReader.hasNext()) {
        	XMLEvent event = eventReader.nextEvent();
        	if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                // If we have an item element, we create a new item
                if (startElement.getName().getLocalPart().equals("Users")) {
                	Iterator<Attribute> attributes = startElement.getAttributes();
                	if(!attributes.hasNext()) {
                		srvMessages.setText("No Account Found");
                		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                		button.setVisible(false);
                		button.setDisabled(true);
                		srvMessages.setText("Create Your Account");
                		
                		
                		srvMessages.setPosition(stageWidth/2 - srvMessages.getWidth()/2, stageHeight/2 + stageHeight/6);
                		UserName.setPosition(stageWidth/2 - UserName.getWidth()/2, srvMessages.getY() - UserName.getHeight()*2f);
                		Pass1.setPosition(stageWidth/2 - Pass1.getWidth()/2, UserName.getY() - Pass1.getHeight()*2f);
                		Pass2.setPosition(stageWidth/2 - Pass2.getWidth()/2, Pass1.getY() - Pass2.getHeight()*2f);
                		CreateAccountButton.setPosition(stageWidth/2 - CreateAccountButton.getWidth()/2,
                				Pass2.getY() - CreateAccountButton.getHeight()*2f);
                		
                		UserNameLabel.setPosition((UserName.getX()-5f), (UserName.getY() + UserName.getHeight()*1.1f));
                		Pass1Label.setPosition((Pass1.getX()-5f), (Pass1.getY() + Pass1.getHeight()*1.1f));
                		Pass2Label.setPosition((Pass2.getX()-5f), (Pass2.getY() + Pass2.getHeight()*1.1f));
                		
                		stage.addActor(UserName);
                		stage.addActor(Pass1);
                		stage.addActor(Pass2);
                		stage.addActor(UserNameLabel);
                		stage.addActor(Pass1Label);
                		stage.addActor(Pass2Label);
                		stage.addActor(CreateAccountButton);
                		
                		
                	} else {
                		
                	}
                }
        	}
        }
	}
	
	public void handleInput() {
	}
	
	public void update(float dt) {
		handleInput();
	}
	
	public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
		sb.setProjectionMatrix(cam.combined);
		
		sb.begin();
		stage.draw();
		sb.end();
		
	}
	
	public void dispose() {
		// everything is in the resource manager com.neet.blockbunny.handlers.Content
	}
	
}
