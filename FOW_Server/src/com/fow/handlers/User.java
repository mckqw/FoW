package com.fow.handlers;

import java.io.Serializable;
import java.net.Socket;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User implements Serializable{
    /**
	 *
	 */
	private static final long serialVersionUID = 1882574599192463322L;
	private int id;
	
	private Socket s;
    private String Name;
    private String status;

    User(String Name, int ID, String status, Socket S) {
        this.id = ID;
        this.Name = Name;
        this.status = status;
        this.s = S;
    }

    public int getID() {
    	return id;
    }
    public String getName() {
        return Name;
    }

    public void setName(String s) {
    	Name = s;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
    	status = s;
    }
    
    public Socket getSocket(){
		return s;
	}
}
