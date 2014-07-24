package com.timewaste.games.drinkfarm;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.LinkedList;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;

public class DrinkFarmLogic {	
	private Sprite game_cup;
	private Sprite liquid_stream;
	private Sprite liquid_stream_half;
	private Scene scene;
	
	private int[] liquid_source = new int[10];
	
	private int cup_bottom_limit;
	private int device_width;
    private int device_height;
    private float current_left_corner_cup;
    private int current_liquid_source;
    private int current_stream_duration;
    private int current_duration;
    private Text score;
    
	private DrinkFarm game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private TimerHandler mTimerHandler;
	
	private int screen_width() {
		return game_instance.cameraWidth();
	}
	
	private int screen_height() {
		return game_instance.cameraHeight();
	}
	
	private Sprite set_cup_logic(){ 
		return new Sprite(0, 0, textures.get("cup"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {				
				current_left_corner_cup = pSceneTouchEvent.getX() - this.getWidth() / 2;
				
				this.setPosition(current_left_corner_cup, cup_bottom_limit);
				
				return true;
			}
		};
	}
	
	private void show_score() {
		scene.unregisterUpdateHandler(mTimerHandler);
    	game_instance.finalization();
    	game_instance.loadNextGame();
	}
	
	private void change_score(int value) {
		int current_score = Integer.parseInt(this.score.getText().toString());
		this.score.setText(Integer.toString(current_score + value));
		this.score.setPosition(device_width / 2 - score.getWidth(), 80);
	}
	
	private void set_fonts(Scene a_scene) {
		Font font = FontFactory.create(game_instance.getFontManager(), game_instance.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.RED);
		font.load();

		this.score =
				new Text(0, 0, font, "0", 6,
				new TextOptions(HorizontalAlign.CENTER), game_instance.getVertexBufferObjectManager());
		this.score.setPosition(device_width / 2 - score.getWidth(), 80);

		a_scene.attachChild(this.score);
	}
	
	private void increase_or_reset_duration(){
		if(current_duration < current_stream_duration){
    		current_duration++;
    	
	    	if(current_duration == current_stream_duration){
    			current_duration = 0;
    			current_stream_duration = 0;
    		}
    	}
	}
	
	private void falling_liquid_logic(Scene a_scene) {
		mTimerHandler = new TimerHandler(0.01f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {		    	
		    	if(current_stream_duration == 0){
		    		current_stream_duration = 50 * (3 + get_random_integer(7));
		    		
		    		current_liquid_source = get_random_integer(5);
			    	liquid_stream.setX(liquid_source[current_liquid_source]);
			    	liquid_stream_half.setX(liquid_source[current_liquid_source]);
		    	}
		    	increase_or_reset_duration();		    	
		    	
		    	if(game_cup.getX() < liquid_source[current_liquid_source] + liquid_stream.getWidth() / 2 && game_cup.getX() + game_cup.getWidth() > liquid_source[current_liquid_source] + liquid_stream.getWidth() / 2){
		    		//show_score();
		    		liquid_stream_half.setVisible(true);
		    		liquid_stream.setVisible(false);
		    		change_score(1);
		    		game_instance.addPoints(1);
		    	}
		    	else{
		    		liquid_stream.setVisible(true);
		    		liquid_stream_half.setVisible(false);
		    	}
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	private void set_ground_images(Scene a_scene) {
		cup_bottom_limit = device_height * 5 / 8;
		
		game_cup = set_cup_logic();
		game_cup.setPosition(250, cup_bottom_limit);
		
		set_liquid_bubble_Ys();
		
		liquid_stream = new Sprite(liquid_source[0], device_height * 11 / 20, this.textures.get("liquid_stream"), game_instance.getVertexBufferObjectManager());
		liquid_stream_half = new Sprite(liquid_source[0], device_height * 11 / 20, this.textures.get("liquid_stream_half"), game_instance.getVertexBufferObjectManager());
		scene.attachChild(liquid_stream);
		scene.attachChild(liquid_stream_half);
		
		a_scene.registerTouchArea(game_cup);
		falling_liquid_logic(a_scene);
	}
	
	public DrinkFarmLogic(DrinkFarm game_instance, Scene a_scene, Map<String, ITextureRegion> textures)	{
		this.device_width  = game_instance.getResources().getDisplayMetrics().widthPixels;
	    this.device_height = game_instance.getResources().getDisplayMetrics().heightPixels;
	    this.current_stream_duration = 0;
	    this.current_duration = 0;
	    
		this.game_instance = game_instance;
		this.textures = textures;
		
		this.scene = a_scene;
		
		set_ground_images(a_scene);	
		set_fonts(a_scene);
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
		liquid_source[0] = device_width * 16 / 96;
		liquid_source[1] = device_width * 31 / 96;
		liquid_source[2] = device_width * 23 / 48;
		liquid_source[3] = device_width * 63 / 100;
		liquid_source[4] = device_width * 75 / 96;
	}
}
