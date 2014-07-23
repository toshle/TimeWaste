package com.timewaste.games.drinkfarm;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.LinkedList;

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
	
	private int[] liquid_bubble_y = new int[10];
	
	private int cup_top_limit;
	private int cup_bottom_limit;
	
	private int device_width;
    private int device_height;
	
	private DrinkFarm game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private LinkedList<Sprite> liquid_bubbles = new LinkedList<Sprite>();
	
	private int screen_width() {
		return game_instance.cameraWidth();
	}
	
	private int screen_height() {
		return game_instance.cameraHeight();
	}
	
	private Sprite set_cup_logic(){ 
		return new Sprite(0, 0, textures.get("cup"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				float new_position_x = pSceneTouchEvent.getX() - this.getWidth() / 2;
				float new_position_y = pSceneTouchEvent.getY() - this.getHeight() / 2;
				if(new_position_y > cup_top_limit && new_position_y < cup_bottom_limit) {
					this.setPosition(new_position_x, new_position_y);
				}
				return true;
			}
		};
	}
	
	private void falling_liquid_logic(Scene a_scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.01f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	Iterator<Sprite> iter = liquid_bubbles.iterator();
		    	while(iter.hasNext()) {
		    		Sprite bubble = iter.next();
		    		bubble.setPosition(bubble.getX(), bubble.getY() + 1);
		    	}
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	private void make_bubbles(Scene a_scene) {
		for(int i=0; i<5; i++) {
			game_bubble = new Sprite(liquid_bubble_y[i], device_height / 2, textures.get("bubble"), game_instance.getVertexBufferObjectManager());		
			a_scene.registerTouchArea(game_bubble);
			a_scene.attachChild(game_bubble);
			
			liquid_bubbles.add(game_bubble);
		}
	}
	
	private void set_ground_images(Scene a_scene) {		
		cup_top_limit = device_height * 9 / 16;
		cup_bottom_limit = device_height * 5 / 8;
		
		game_cup = set_cup_logic();
		game_cup.setPosition(250, cup_bottom_limit);
		
		set_liquid_bubble_Ys();
		make_bubbles(a_scene);
		
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
	
	private void set_liquid_bubble_Ys() {
		liquid_bubble_y[0] = device_width * 1 / 6;
		liquid_bubble_y[1] = device_width * 2 / 6;
		liquid_bubble_y[2] = device_width * 23 / 48;
		liquid_bubble_y[3] = device_width * 31 / 48;
		liquid_bubble_y[4] = device_width * 19 / 24;
	}
}
