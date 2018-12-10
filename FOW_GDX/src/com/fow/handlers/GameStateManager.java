package com.fow.handlers;

import java.util.Stack;

import com.fow.main.Game;
import com.fow.states.GameState;
import com.fow.states.LevelSelect;
import com.fow.states.Play;

public class GameStateManager {

	private Game game;
	private Stack<GameState> gameStates;
	public static final int PLAY = 2;	
	public static final int levelSelect = 1;	
	
	
	public GameStateManager(Game game) {
		this.game = game;
		gameStates  = new Stack<GameState>();
		pushState(levelSelect);
	}

	public Game game() {
		return game;
	}
	
	public void update(float dt) {
		gameStates.peek().update(dt);
	}
	
	public void render() {
		gameStates.peek().render();
	}
	
	public void setState(int state) {
		popState();
		pushState(state);
	}
	
	public void pushState(int state) {
		gameStates.push(getState(state));
	}

	public void popState() {
		GameState g = gameStates.pop();
		g.dispose();
	}

	private GameState getState(int state) {
		if(state == PLAY) return new Play(this);
		if(state == levelSelect) return new LevelSelect(this);
		return null;
	}

}
