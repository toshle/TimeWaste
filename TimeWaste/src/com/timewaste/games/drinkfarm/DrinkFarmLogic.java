package com.timewaste.games.drinkfarm;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DrinkFarmLogic {	
	private Sprite game_cup;
	private Sprite game_bubble;
	
	
	private int device_width;
    private int device_height;
	
	private DrinkFarm game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	
	private int screen_width() {
		return game_instance.cameraWidth();
	}
	
	private int screen_height() {
		return game_instance.cameraHeight();
	}
	
	private Sprite set_cup_logic(){
		return new Sprite(0, 0, textures.get("cup"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				return true;
			}
		};
	}
	
	private void falling_liquid_logic(Scene a_scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.01f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	game_bubble.setPosition(game_bubble.getX(), game_bubble.getY() + 1);
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	private void set_ground_images(Scene a_scene) {      
		float current_y = 0;
		float current_x = 0;
		
		game_cup = set_cup_logic();
		game_cup.setPosition(current_x, current_y);
		
		game_bubble = new Sprite(0, 200, textures.get("bubble"), game_instance.getVertexBufferObjectManager());
		
		a_scene.registerTouchArea(game_bubble);
		a_scene.attachChild(game_bubble);
		
		a_scene.registerTouchArea(game_cup);
		falling_liquid_logic(a_scene);
	}
	
	public DrinkFarmLogic(DrinkFarm game_instance, Scene a_scene, Map<String, ITextureRegion> textures)
	{
		this.device_width  = game_instance.getResources().getDisplayMetrics().widthPixels;
	    this.device_height = game_instance.getResources().getDisplayMetrics().heightPixels;
	    
		this.game_instance = game_instance;
		this.textures = textures;
		
		set_ground_images(a_scene);	
		
	}
	
	public void render(Scene a_scene) {
		a_scene.attachChild(game_cup);
	}
	
	private int get_random_integer(int range) {
		Random random_number = new Random(System.currentTimeMillis());
		int random_int = random_number.nextInt(range);
		return random_int;
	}
}
