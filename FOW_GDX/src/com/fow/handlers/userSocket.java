package com.fow.handlers;
import java.net.Socket;

public class userSocket {
	
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

}
