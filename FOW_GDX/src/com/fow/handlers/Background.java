package com.fow.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

//Code Modified from http://williammora.com/a-running-game-with-libgdx-part-4

public class Background extends Actor {

    private final TextureRegion textureRegion;
    private Rectangle textureRegionBounds1;
    private Rectangle textureRegionBounds2;
    private int speed = 100;
    
	private int stageHeight;
	private int stageWidth;

    public Background() {
    	
    	stageHeight = Gdx.graphics.getHeight();
        stageWidth = Gdx.graphics.getWidth();
        
        textureRegion = new TextureRegion(new Texture(Gdx.files.internal(com.fow.main.Game.BACKGROUND_IMAGE_PATH)));
        textureRegionBounds1 = new Rectangle(0 - stageWidth / 2, 0, stageWidth, stageHeight);
        textureRegionBounds2 = new Rectangle(stageWidth / 2, 0, stageWidth, stageHeight);
    }

    @Override
    public void act(float delta) {
        if (leftBoundsReached(delta)) {
            resetBounds();
        } else {
            updateXBounds(-delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(textureRegion, textureRegionBounds1.x, textureRegionBounds1.y, stageWidth,
        		stageHeight);
        batch.draw(textureRegion, textureRegionBounds2.x, textureRegionBounds2.y, stageWidth,
        		stageHeight);
    }

    private boolean leftBoundsReached(float delta) {
        return (textureRegionBounds2.x - (delta * speed)) <= 0;
    }

    private void updateXBounds(float delta) {
        textureRegionBounds1.x += delta * speed;
        textureRegionBounds2.x += delta * speed;
    }

    private void resetBounds() {
        textureRegionBounds1 = textureRegionBounds2;
        textureRegionBounds2 = new Rectangle(stageWidth, 0, stageWidth, stageHeight);
    }

}