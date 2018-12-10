
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fow.handlers.Location;
import com.fow.handlers.LocationArray;
import com.fow.handlers.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FOWServer extends Application{
      public static final CountDownLatch latch = new CountDownLatch(1);
      public static FOWServer FOWserver = null;

       public static FOWServer waitForServer() {
           try {
               latch.await();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           return FOWserver;
       }

       public static void setFOWServer(FOWServer FOWServer0) {
         FOWserver = FOWServer0;
         latch.countDown();
       }

       public FOWServer() {
         setFOWServer(this);
       }

  private int sessionNo = 1; // Number a session
  LinkedList<userSocket> activePlayers = new LinkedList<>();
  TextArea taLog;

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
	taLog = new TextArea();

    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
    primaryStage.setTitle("TicTacToeServer"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage

    new Thread( () -> {
      try {
        // Create a server socket
    	ServerSocket serverSocket = new ServerSocket(8000);
        Platform.runLater(() -> taLog.appendText(new Date() +
          ": Server started at socket 8000\n"));
        /*ServerSocket gameBrowserServerSocket = new ServerSocket(8081);
		Platform.runLater(() -> taLog.appendText(new Date()+
			": Server started at socket 8081\n"));
		*/
        // Ready to create a session for every two players
        while (true) {
          Platform.runLater(() -> taLog.appendText(new Date() +
            ": Wait for players to join session " + sessionNo + '\n'));

          Socket holdSocket = serverSocket.accept();
          try {
        	  	getUserInformation(holdSocket);
				addUser(holdSocket);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
          
          
          
          Platform.runLater(() -> {
        	  String tempName = activePlayers.getLast().getUser().getName();
            taLog.appendText(new Date() + ": Player " + tempName + " joined session "
              + sessionNo + '\n');
            taLog.appendText(tempName+"'s IP address" +
            		activePlayers.getLast().getSocket().getInetAddress().getHostAddress() + '\n');
          });
          
          if(activePlayers.size() > 1) {
				new java.util.Timer().schedule( 
				        new java.util.TimerTask() {
				            @Override
				            public void run() {
				          	  Platform.runLater(() -> {
				                  taLog.appendText("Started Game with: " + activePlayers.get(activePlayers.size() - 2).getUser().getName()
				                  		+ " and " + activePlayers.get(activePlayers.size() - 1).getUser().getName() + '\n');
				                });				          	  
				          	  new GameSessionHandler(activePlayers.get(activePlayers.size() - 1)
				          			  ,activePlayers.get(activePlayers.size() - 2)).run();
				            }
				        }, 
				        2000 
				);
          }
          	
        }
      }
      catch(IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }

  private boolean getUserInformation(Socket holdSocket) throws IOException, ClassNotFoundException {
	  ObjectOutputStream oo = new ObjectOutputStream(holdSocket.getOutputStream());
	  oo.writeInt(1);
	  ObjectInputStream oi = new ObjectInputStream(holdSocket.getInputStream());
      Object holdObject = oi.readObject();

      User holdUser = null;
      if(holdObject instanceof User) {
      	holdUser = (User) holdObject;
      }
      System.out.println("User: "+holdUser.getName());
      updateUserData("state","In-Menus",holdUser.getID());
      activePlayers.offer(new userSocket(holdSocket,holdUser));
      oo.close();
      oi.close();
      return true;
  }

private boolean addUser(Socket holdSocket) throws IOException, ClassNotFoundException {
	  ObjectOutputStream oo = new ObjectOutputStream(holdSocket.getOutputStream());
	  oo.writeInt(1);
	  ObjectInputStream oi = new ObjectInputStream(holdSocket.getInputStream());
      Object holdObject = oi.readObject();

      User holdUser = null;
      if(holdObject instanceof User) {
      	holdUser = (User) holdObject;
      }
      System.out.println("User: "+holdUser.getName());
      updateUserData("state","In-Menus",holdUser.getID());
      activePlayers.offer(new userSocket(holdSocket,holdUser));
      oo.close();
      oi.close();
      return true;
	}
  
  public void updateUserData(String col, String newData, int userid) {
	  try {Class.forName("com.mysql.jdbc.Driver");
      Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/FogOFWar", "root", "");
      Statement smt = con.createStatement(); //Create Statement to interact

      smt.executeUpdate("UPDATE `users` SET `" + col + "` = '" + newData + "' WHERE `UserId` = " + userid);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  //finds and returns a user by its name
  public userSocket findUser(String name) {
	  ListIterator<userSocket> itr = activePlayers.listIterator(0);
	  userSocket current = activePlayers.getFirst();
	  while(itr.hasNext()) {
		  if(current.getUser().getName() == name) {
			  return current;
		  } else {
			  current = itr.next();
		  }
	  }
	  return null;
  }

  class GameSessionHandler extends Thread{

	private userSocket p1;
	private userSocket p2;

    // Create and initialize cells
    
	private LocationArray locAr1;
	private LocationArray locAr2;

    //Object Streams
    private ObjectInputStream OfromP1;
    private ObjectOutputStream OtoP1;
    private ObjectInputStream OfromP2;
    private ObjectOutputStream OtoP2;

    // Continue to play
    private boolean continueToPlay = true;

	public GameSessionHandler(userSocket player1, userSocket player2) {
		p1 = player1;
		p2 = player2;
	}

	@Override
	public void run() {
		try {
		// Create data input and output streams
		InputStream p1in = p1.getSocket().getInputStream();
		InputStream p2in = p2.getSocket().getInputStream();
		OutputStream p1out = p1.getSocket().getOutputStream();
		OutputStream p2out = p2.getSocket().getOutputStream();

		// Create Object input and output streams
        OfromP1 = new ObjectInputStream(p1in);
        OtoP1 = new ObjectOutputStream(p1out);
        OfromP2 = new ObjectInputStream(p2in);
		OtoP2 = new ObjectOutputStream(p2out);
		
		while(true) {
			try {
				String locaar1St = (String) OfromP1.readObject();
				System.out.println(locaar1St);
				String locaar2St = (String) OfromP2.readObject();
				System.out.println(locaar2St);
				locAr1 = new ObjectMapper().readValue(locaar1St, LocationArray.class);
				locAr2 = new ObjectMapper().readValue(locaar2St, LocationArray.class);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Iterator<Location> itr1 = locAr1.getArray().iterator();
			while(itr1.hasNext()) {
				Location hold = itr1.next();
				Platform.runLater(() -> {
				    try {
						taLog.appendText("Location: " + hold.toJSONString() + '\n');
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  });
			}
			Iterator<Location> itr2 = locAr2.getArray().iterator();
			while(itr2.hasNext()) {
				Location hold = itr2.next();
				Platform.runLater(() -> {
				    try {
						taLog.appendText("Location: " + hold.toJSONString() + '\n');
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  });
			}
			
		}
          } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

  }

  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */

  @Override
  public void stop(){
      System.out.println("Stage is closing");
      System.exit(0);
  }
}
