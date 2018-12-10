package com.fow.main;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import javafx.application.Application;

public class FOWDesktopClient {

	public static void main(String[]args) {
		
		/*(new Thread(){
	        @Override
	        public void run(){
	                Application.launch(WebContainer.class);
	        }
		}).start();
		*/
		LwjglApplicationConfiguration cfg =
				new LwjglApplicationConfiguration();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		if(screenSize.getWidth() > 2000 && screenSize.getHeight() > 1200) {
			Game.SCALE = 2;
		} else {
			Game.SCALE = 2;
		}

		cfg.title = Game.TITLE;
		cfg.width = Game.V_WIDTH * Game.SCALE;
		cfg.height = Game.V_HEIGHT * Game.SCALE;
		cfg.resizable = false;
		
		new LwjglApplication(new Game(), cfg);

	}
}