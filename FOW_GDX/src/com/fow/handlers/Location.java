package com.fow.handlers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Location {

	private int x;
	private int y;
	private String type;
	
	public Location(int X, int Y,String t) {
		set(X, Y, t);
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
	public String getType() {
		return type;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void set(int x, int y, String t) {
		this.x = x;
		this.y = y;
		type = t;
	}
	public boolean equals(Location ch) {
		if(this.getX() == ch.getX() && this.getY() == ch.getY()
				&& this.getType() == ch.getType()) {
			return true;
		}
		return false;
	}
	
	public String toJSONString() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
	
	public ObjectMapper toJSON() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(System.out, this);
		return mapper;
	}
}
