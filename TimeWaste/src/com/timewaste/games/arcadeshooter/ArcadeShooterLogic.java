package com.timewaste.games.arcadeshooter;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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

import android.graphics.Color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.Gravity;
import android.widget.Toast;

public class ArcadeShooterLogic implements IAccelerationListener {
	//Fields
	private Sprite ship, enemy;
	private ArcadeShooter game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private Text score;
	private int speed, current_speed;
	 
    private int accellerometerSpeedX;
    private int accellerometerSpeedY;
	
	private int screen_width() {
		return game_instance.cameraWidth();
	}
	
	private int screen_height() {
		return game_instance.cameraHeight();
	}
	
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		float newX = ship.getX() + pAccelerationData.getX();
		if(newX >= 0 && newX <= screen_width() - ship.getWidth()) {
			ship.setPosition(newX, ship.getY());
		}
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
	
	private void change_score(int value) {
		int current_score = Integer.parseInt(this.score.getText().toString());
		this.score.setText(Integer.toString(current_score + value));
		this.score.setPosition((screen_width() - this.score.getWidth()) / 2 - 30, 80);
	}
	
	private void randomize_enemy_location() {
		Random random_number = new Random(System.currentTimeMillis());
		enemy.setPosition(random_number.nextInt(screen_width()), 0);
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
	
	//Dragging ship logic.
	private Sprite set_image_logic(){
		return new Sprite(0, 0, textures.get("ship"), game_instance.getVertexBufferObjectManager());
	}
	
	private void catching_enemy_logic(Scene a_scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.001f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	speed--;
		        if(speed == 0) {
		        	speed = current_speed;
		            enemy.setPosition(enemy.getX(), enemy.getY() + 1);
		            //If you catch the enemy
		            if(enemy.getY() > ship.getY() && (enemy.getX() >= ship.getX() && enemy.getX() <= ship.getX() + ship.getWidth())) {
		            	change_score(100);
		            	randomize_enemy_location();
		            }
		            //If you miss the enemy
		            if(enemy.getY() > screen_height() - enemy.getHeight()) {
		            	change_score(-200);
		            	randomize_enemy_location();
		            }
		        }
		        //set_fall_speed();
		        //show_score when 30 seconds pass Roska?
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	//Formula to set the ground images. Also registering touch events for every image.
	private void set_environment(Scene a_scene) {       
		this.ship = set_image_logic();
		this.ship.setPosition(screen_width() / 2 - this.ship.getWidth() / 2, screen_height() - this.ship.getHeight() + 10);
		a_scene.registerTouchArea(this.ship);
		a_scene.attachChild(this.ship);
		
		this.enemy = new Sprite(screen_width() / 2 - this.ship.getWidth() / 2, 0, textures.get("enemy"), game_instance.getVertexBufferObjectManager());
		a_scene.registerTouchArea(this.enemy);
		a_scene.attachChild(this.enemy);
		set_fonts(a_scene);
		catching_enemy_logic(a_scene);
	}
	
	public ArcadeShooterLogic(ArcadeShooter game_instance, Scene a_scene, Map<String, ITextureRegion> textures) {
		this.game_instance = game_instance;
		this.textures = textures;
		this.speed = 10;
		this.current_speed = 10;
		
		set_environment(a_scene);
	}

	
}
