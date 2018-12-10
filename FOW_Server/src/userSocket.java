
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.fow.handlers.User;

public class userSocket extends Socket {
	
	private Socket s;
	private User u;
	
	userSocket(Socket s, User u){
		this.s = s;
		this.u = u;
	}
	
	public User getUser(){
		return u;
	}
	
	public Socket getSocket(){
		return s;
	}
	
	/*public <T> void sendData(T data) throws IOException {
		toUser.writeObject(data);
	}
	public <T> T getData() throws ClassNotFoundException, IOException {
		return (T) fromUser.readObject();
	}*/

}
