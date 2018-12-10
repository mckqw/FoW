package com.fow.handlers;

import java.io.Serializable;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationArray implements Serializable{

	private Array<Location> locAr;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LocationArray(Array<Location> locar) {
		locAr = locar;
	}
	public Array<Location> getArray(){
		return locAr;
	}
	
	public void setArray(Array<Location> locar){
		locAr = locar;
	}
	
	public String toJSONString() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}
