package com.timewaste.games.arcadeshooter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.andengine.entity.scene.IOnSceneTouchListener;
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
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

public class ArcadeShooterLogic implements IAccelerationListener, IOnSceneTouchListener {
	//Fields
	private Sprite ship, enemy, explosion;
	private List<Sprite> bullets;
	private ArcadeShooter game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private Text score;
	private int speed, current_speed;
	
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
		float newX = ship.getX() + pAccelerationData.getX()*2;
		if(newX >= 0 && newX <= screen_width() - ship.getWidth()) {
			ship.setPosition(newX, ship.getY());
		}
	}
	
	private void show_score() {
		game_instance.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
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
		enemy.setPosition(random_number.nextInt((int) (screen_width() - enemy.getWidth())), -enemy.getHeight());
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
	            	explosion.setPosition(explosion.getX(), explosion.getY() + 1);
		            
		            Iterator<Sprite> iter = bullets.iterator();
		            while(iter.hasNext()){
		                Sprite bullet = iter.next();
		                if(bullet.getY() < -bullet.getHeight()) {
		            		iter.remove();
		            	}
		            	bullet.setPosition(bullet.getX(), bullet.getY() - 3);
		            	if(enemy.getY() > bullet.getY() && (enemy.getX() >= (bullet.getX() - bullet.getWidth()/2) && enemy.getX() <= bullet.getX() + bullet.getWidth())) {
		            		change_score(100);
		            		iter.remove();
			            	explosion.setPosition(enemy.getX(), enemy.getY());
			            	explosion.setVisible(true);
			            	randomize_enemy_location();
			            	bullet.setVisible(false);
		            	}
		            }

		            if(enemy.getY() > screen_height() + enemy.getHeight()) {
		            	randomize_enemy_location();
		            }
		        }
		        //set_fall_speed();
		    }
		});
		a_scene.registerUpdateHandler(mTimerHandler);
	}
	
	private void set_environment(Scene a_scene) {   
		this.explosion = new Sprite(0, 0, textures.get("explosion"), game_instance.getVertexBufferObjectManager());
		this.explosion.setVisible(false);
		a_scene.attachChild(this.explosion);
		
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
		bullets = new ArrayList<Sprite>();
		a_scene.setOnSceneTouchListener(this);
		set_environment(a_scene);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
	        float bulletX = ship.getX(),
	        	  bulletY = screen_height() - ship.getHeight() + 10;
	        Sprite bullet = new Sprite(bulletX, bulletY, textures.get("bullet"), game_instance.getVertexBufferObjectManager());
			pScene.attachChild(bullet);
	        bullets.add(bullet);
	        bullet = null;
	    }
		return false;
	}

	
}
