package com.timewaste.games.runner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;


import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;

import android.graphics.Color;
import android.graphics.Point;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Toast;

public class RunnerLogic {
	final int SPEED_VALUE = 5;
	//Fields
	final private AnimatedSprite stickmanRun, stickmanRoll;
	private Point finger_coordinates;
	private List<Rectangle> obstacles;
	private Runner game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private Text score;
	private int iteration_speed, generate_speed, current_generate_speed, roll_time, jump_time;
	private float current_speed;
	boolean isRolling, isJumping;
	
	private int screen_width() {
		return game_instance.cameraWidth();
	}
	
	private int screen_height() {
		return game_instance.cameraHeight();
	}
	
	private void speed_toast (final String message) {
		game_instance.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		        Toast toast = Toast.makeText(game_instance, message, Toast.LENGTH_SHORT);
		        toast.setGravity(Gravity.TOP|Gravity.LEFT, 720 / 2 - 60, 0);
		        toast.show();
		    }
		});
	}
	
	private void show_score() {
		game_instance.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		        //Toast.makeText(TickTackToe.this, message, Toast.LENGTH_SHORT).show();
		    	AlertDialog.Builder alert = new AlertDialog.Builder(game_instance).
		        setTitle("Game Ended").
		        setMessage("Your score is: " + score.getText()).
		        setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			        	game_instance.finalization();
			        	game_instance.loadNextGame();
			        }
		        });
		    	alert.show();
		    }
		});
	}

	private void set_speed() {
		float speed_before_change = this.current_speed;
		int current_score = Integer.parseInt(this.score.getText().toString());
		if(current_score > 1200) this.current_speed = 2.0f;
		if(current_score > 3000) this.current_speed = 2.5f;
		if(current_score > 4500) this.current_speed = 3.0f;
		if(current_score > 7500) this.current_speed = 3.5f;
		
		if(this.current_speed > speed_before_change) {
			speed_toast("Speed up!");
			current_generate_speed -= 100;
		}
	}

	
	private void change_score(int value) {
		int current_score = Integer.parseInt(this.score.getText().toString());
		this.score.setText(Integer.toString(current_score + value));
		this.score.setPosition((720 - this.score.getWidth()) / 2, 80);
	}
	
	private void generate_rectangle(Scene a_scene) {
		if(this.generate_speed == 0) {
			Random random_number = new Random(System.currentTimeMillis());
			
			Rectangle rectangle = new Rectangle(
					720,
			 	    stickmanRun.getY() + 420 / (4.2f - random_number.nextInt(4) * 0.5f),
			 	    30, 30, game_instance.getVertexBufferObjectManager());
			
			rectangle.setColor(random_number.nextFloat(), random_number.nextFloat(), random_number.nextFloat());
			a_scene.attachChild(rectangle);
			obstacles.add(rectangle);
	        generate_speed = current_generate_speed;
		}
	}
	
	private void set_fonts(Scene a_scene) {
		Font font = FontFactory.create(game_instance.getFontManager(), game_instance.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.RED);
		font.load();

		this.score =
				new Text(0, 0, font, "0", 6,
				new TextOptions(HorizontalAlign.CENTER), game_instance.getVertexBufferObjectManager());
		
		this.score.setPosition(720 - this.score.getWidth() / 2, 80);

		a_scene.attachChild(this.score);
	}
	
	private void generating_obstacles_logic(final Scene a_scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.001f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	iteration_speed--;
		    	generate_speed--;
		    	if(isRolling) roll_time--;
		    	if(isJumping) jump_time--;
		        if(iteration_speed == 0) {
		        	iteration_speed = SPEED_VALUE;
		        	generate_rectangle(a_scene);
		        	if(isRolling) roll();
		        	if(isJumping) jump();
		        	move_obstacles();
		        }
		        set_speed();
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	Sprite sprite_in_action() {
		if(stickmanRun.isVisible()) return stickmanRun;
		return stickmanRoll;
	}
	
	void action(boolean act) {
		stickmanRun.setVisible(!act);
		stickmanRoll.setVisible(act);
	}
	
	void roll() {
		if(roll_time <= 0) {
			isRolling = false;
    		action(false);
    	}
	}
	
	void jump() {
		if(jump_time >= 350) stickmanRoll.setPosition(stickmanRoll.getX(), stickmanRoll.getY() - 1);
    	if(jump_time <  350) stickmanRoll.setPosition(stickmanRoll.getX(), stickmanRoll.getY() + 1);
    	if(jump_time <= 0) {
    		isJumping = false;
    		isRolling  = false;
    		action(false);
    		stickmanRoll.setPosition(stickmanRun.getX(), stickmanRun.getY() + 100);
    	}
	}
	
	void move_obstacles() {
		Iterator<Rectangle> iter = obstacles.iterator();
        while(iter.hasNext()){
        	Rectangle rectangle = iter.next();
        	rectangle.setPosition(rectangle.getX() - 2, rectangle.getY());
            if(rectangle.getX() < -rectangle.getWidth() && rectangle.isVisible()){
            	iter.remove();
            	change_score(100);
            	game_instance.addPoints(100);
            }
         	
            //You are practically immortal when you barrel roll. :@
            if(
            	rectangle.getX() > sprite_in_action().getX() * 1.4f && !isRolling &&
            	sprite_in_action().getX() + rectangle.getX() / 1.8f > rectangle.getX() &&
            	sprite_in_action().getY() + sprite_in_action().getHeight() / 1.8f > rectangle.getY()) {
	            	show_score();
	            	this.iteration_speed = -1;
            }
        }
	}
	
	private void swipe_up() {
		if(!isJumping) {
			isJumping = true;
			jump_time = 700;
			stickmanRoll.setPosition(stickmanRun.getX(), stickmanRun.getY() + stickmanRoll.getHeight() / 3);
			if(isRolling) isRolling = false;
		}
	}
	
	private void swipe_down() {
		if(!isRolling) {
			isRolling  = true;
			roll_time = 500;
			if(isJumping) {
				isJumping = false;
				stickmanRoll.setPosition(stickmanRun.getX(), stickmanRun.getY() + 100);
			}
		}
	}
	
	private void swipe_logic(Scene a_scene) {
		a_scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if(pSceneTouchEvent.isActionDown()) {
					finger_coordinates.set((int)pSceneTouchEvent.getX(), (int)pSceneTouchEvent.getY());
				}
				if(pSceneTouchEvent.isActionUp()) { 
					action(true);
					if(finger_coordinates.y > pSceneTouchEvent.getY()) swipe_up();
					else swipe_down();
				}
				return false;
			}
		});
	}
	
	//Formula to set the ground images. Also registering touch events for every image.
	private void set_environment(Scene a_scene) {
		stickmanRun.animate(10);
		stickmanRun.setScale(0.3f, 0.3f);
		this.stickmanRun.setPosition(90, 100);
		a_scene.attachChild(this.stickmanRun);
		
		stickmanRoll.animate(100);
		stickmanRoll.setScale(0.3f, 0.3f);
		stickmanRoll.setPosition(stickmanRun.getX(), stickmanRun.getY() + 100);
		stickmanRoll.setVisible(false);
		a_scene.attachChild(this.stickmanRoll);
		
		set_fonts(a_scene);
		swipe_logic(a_scene);
		generating_obstacles_logic(a_scene);
	}
	
	public RunnerLogic(Runner game_instance, Scene a_scene, Map<String, ITextureRegion> textures) {
		this.game_instance = game_instance;
		this.textures = textures;
		this.iteration_speed = SPEED_VALUE;
		this.isRolling = false;
		this.isJumping = false;
		this.generate_speed = 1000;
		this.current_generate_speed = 1000;
		this.roll_time = 500;
		this.jump_time = 700;
		this.current_speed = 1.5f;
		this.finger_coordinates = new Point();
		this.obstacles = new ArrayList<Rectangle>();
		this.stickmanRun  = new AnimatedSprite(0, 0, (TiledTextureRegion)this.textures.get("stickmanrun"), game_instance.getVertexBufferObjectManager());
		this.stickmanRoll = new AnimatedSprite(100, 0, (TiledTextureRegion)this.textures.get("stickmanroll"), game_instance.getVertexBufferObjectManager());
		set_environment(a_scene);
	}
}
