package com.fow.handlers;

import java.io.Serializable;
import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocationList implements Serializable{

	
	private List<Location> locLs;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LocationList(List<Location> locls) {
		locLs = locls;
	}
	public List<Location> getList(){
		return locLs;
	}
	
	public void setList(List<Location> locls){
		locLs = locls;
	}
	
	public String toJSONString() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}
}
