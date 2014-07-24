package com.timewaste.games.shitmageddon;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;






import org.andengine.AndEngine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;

import com.timewaste.timewaste.GameActivity;

import android.graphics.Color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Surface;
import android.widget.Toast;

public class ShitmageddonLogic implements IAccelerationListener {
	//Fields
	private Sprite toilet, shit;
	private Shitmageddon game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private Text score;
	private int speed, current_speed;
	
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
		        toast.setGravity(Gravity.TOP|Gravity.LEFT, screen_width() / 2 - 80, 0);
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
	
	private void set_fall_speed() {
		int speed_before_change = this.current_speed;
		int current_score = Integer.parseInt(this.score.getText().toString());
		if(current_score < 1000) this.current_speed = 5;
		if(current_score >= 1000 && current_score < 2000) this.current_speed = 4;
		if(current_score >= 2000 && current_score < 3000) this.current_speed = 3;
		if(current_score >= 3000 && current_score < 5000) this.current_speed = 2;
		if(current_score >= 5000) this.current_speed = 1;
		
		if(this.current_speed > speed_before_change) speed_toast("Speed down!");
		if(this.current_speed < speed_before_change) speed_toast("Speed up!");
	}
	
	private void change_score(int value) {
		int current_score = Integer.parseInt(this.score.getText().toString());
		this.score.setText(Integer.toString(current_score + value));
		this.score.setPosition((screen_width() - this.score.getWidth()) / 2 - 30, 80);
	}
	
	private void randomize_shit_location() {
		Random random_number = new Random(System.currentTimeMillis());
		shit.setPosition(random_number.nextInt((int)(screen_width() - shit.getWidth())), 0);
	}
	
	private void set_fonts(Scene a_scene) {
		Font font = FontFactory.create(game_instance.getFontManager(), game_instance.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.RED);
		font.load();

		this.score =
				new Text(0, 0, font, "0", 6,
				new TextOptions(HorizontalAlign.CENTER), game_instance.getVertexBufferObjectManager());
		
		this.score.setPosition((screen_width() - this.score.getWidth()) / 2 - 30, 80);

		a_scene.attachChild(this.score);
	}
	
	//Dragging toilet logic.
	private Sprite set_image_logic(){
		return new Sprite(0, 0, textures.get("toilet"), game_instance.getVertexBufferObjectManager());
	}
	
	private void catching_shit_logic(Scene a_scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.001f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	speed--;
		        if(speed == 0) {
		        	speed = current_speed;
		            shit.setPosition(shit.getX(), shit.getY() + 1);
		            //If you catch the shit
		            if(shit.getY() > toilet.getY() && (shit.getX() >= toilet.getX() && shit.getX() <= toilet.getX() + toilet.getWidth())) {
		            	change_score(100);
	            		game_instance.addPoints(100);
		            	randomize_shit_location();
		            }
		            //If you miss the shit
		            if(shit.getY() > screen_height() - shit.getHeight()) {
		            	change_score(-200);
	            		game_instance.addPoints(-200);
		            	randomize_shit_location();
		            }
		        }
		        set_fall_speed();
		        //show_score when 30 seconds pass Roska?
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	//Formula to set the ground images. Also registering touch events for every image.
	private void set_environment(Scene a_scene) {       
		this.toilet = set_image_logic();
		this.toilet.setPosition(screen_width() / 2 - this.toilet.getWidth() / 2, screen_height() - this.toilet.getHeight() + 10);
		a_scene.registerTouchArea(this.toilet);
		a_scene.attachChild(this.toilet);
		
		this.shit = new Sprite(screen_width() / 2 - this.toilet.getWidth() / 2, 0, textures.get("shit"), game_instance.getVertexBufferObjectManager());
		a_scene.registerTouchArea(this.shit);
		a_scene.attachChild(this.shit);
		set_fonts(a_scene);
		catching_shit_logic(a_scene);
	}
	
	public ShitmageddonLogic(Shitmageddon game_instance, Scene a_scene, Map<String, ITextureRegion> textures) {
		this.game_instance = game_instance;
		this.textures = textures;
		this.speed = 5;
		this.current_speed = 5;
		set_environment(a_scene);
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		float newX = toilet.getX() + pAccelerationData.getX()*2;
		if(newX >= 0 && newX <= screen_width() - toilet.getWidth()) {
			toilet.setPosition(newX, toilet.getY());
		}
	}
}
