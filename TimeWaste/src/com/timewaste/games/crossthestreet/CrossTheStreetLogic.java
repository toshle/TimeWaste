package com.timewaste.games.crossthestreet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.timewaste.games.arcadeshooter.ArcadeShooter;
import com.timewaste.timewaste.GameActivity;

public class CrossTheStreetLogic implements IAccelerationListener {
	
	private final int IMAGES_COUNT = 4;
	private final String images[] = { "red_car", "blue_car", "yellow_car", "green_car"};
	private Sprite turtle;
	private Sprite car;
	private CrossTheStreet game_instance;
	private List<Sprite> cars_left;
	private List<Sprite> cars_right;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private int speed, current_speed;
	private boolean timer_stop;
	
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
		float newY = turtle.getY() + pAccelerationData.getY()*3;

		if(newY >= 0 && newY <= screen_height() - turtle.getHeight()) {
			turtle.setPosition(turtle.getX(), newY);
		}
	}
	
	private void show_score() {
		game_instance.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		        //Toast.makeText(TickTackToe.this, message, Toast.LENGTH_SHORT).show();
		    	AlertDialog.Builder alert = new AlertDialog.Builder(game_instance).
		        setTitle("Game Ended").
		        setMessage("You lost!").
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
	
	private void show_win_score() {
		game_instance.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		        //Toast.makeText(TickTackToe.this, message, Toast.LENGTH_SHORT).show();
		    	AlertDialog.Builder alert = new AlertDialog.Builder(game_instance).
		        setTitle("Game Ended").
		        setMessage("You win!").
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

	public CrossTheStreetLogic(CrossTheStreet game_instance, final Scene scene, Map<String, ITextureRegion> textures) {
		this.game_instance = game_instance;
		this.textures = textures;
		cars_left = new ArrayList<Sprite>();
		cars_right = new ArrayList<Sprite>();
		this.speed = 150;
		this.current_speed = 150;
		this.timer_stop = false;
		
		set_enviroment(scene);
	}
	
	private void random_carl(final Scene scene) {
		Random random_car = new Random(System.currentTimeMillis());
		float y;
		if (random_car.nextInt(2) == 1) 
			y = screen_height() / 4 * 3;
		else
			y = 0;
		Random random_number = new Random(System.currentTimeMillis());

        Sprite new_car = new Sprite(-car.getWidth(), y, textures.get(images[random_number.nextInt(IMAGES_COUNT)]), game_instance.getVertexBufferObjectManager());
        scene.attachChild(new_car);
        cars_left.add(new_car);
        new_car = null;
	}
	
	private void random_carr(final Scene scene) {
		Random random_car = new Random(System.currentTimeMillis());
		float y;
		if (random_car.nextInt(2) == 1) 
			y = screen_height() / 2;
		else
			y = screen_height() / 4;
		Random random_number = new Random(System.currentTimeMillis());

        Sprite new_car = new Sprite(screen_width() + car.getWidth(), y, textures.get(images[random_number.nextInt(IMAGES_COUNT)]), game_instance.getVertexBufferObjectManager());
        new_car.setRotation(180);
        scene.attachChild(new_car);
        cars_right.add(new_car);
        new_car = null;
	}
	
	private boolean is_inside(float x, float y, Sprite ship) {
		return ship.getX() < x && x < ship.getX() + ship.getWidth() && ship.getY() < y && y < ship.getY() + ship.getHeight();
	}
	
	private boolean is_connection(Sprite current_stone, Sprite ship) {
		boolean b1 = is_inside(current_stone.getX(), current_stone.getY(), ship);
		boolean b2 = is_inside(current_stone.getX() + current_stone.getWidth(), current_stone.getY(), ship);
		boolean b3 = is_inside(current_stone.getX(), current_stone.getY() + current_stone.getHeight(), ship);
		boolean b4 = is_inside(current_stone.getX() + current_stone.getWidth(), current_stone.getY() + current_stone.getHeight(), ship);
		
		boolean b5 = is_inside(ship.getX(), ship.getY(), current_stone);
		boolean b6 = is_inside(ship.getX() + ship.getWidth(), ship.getY(), current_stone);
		boolean b7 = is_inside(ship.getX(), ship.getY() + ship.getHeight(), current_stone);
		boolean b8 = is_inside(ship.getX() + ship.getWidth(), ship.getY() + ship.getHeight(), current_stone);
		
		return b1 || b2 || b3 || b4 || b5 || b6 || b7 || b8;
	}

	private void car_dropping(final Scene scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.01f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	if (!timer_stop) {
			    	speed--;
			    	if (speed == 0) {
			    		Random random_direction = new Random(System.currentTimeMillis());
				    	//if (random_direction.nextInt(2) == 1)
				    		random_carr(scene);
				    	//else
				    		random_carl(scene);
			    		speed = current_speed;
			    	}
			    	Iterator<Sprite> iterl = cars_left.iterator();
			    	Iterator<Sprite> iterr = cars_right.iterator();
		            while(iterr.hasNext()){
		                Sprite current_carr = iterr.next();
		                current_carr.setPosition(current_carr.getX() - 3, current_carr.getY());
		                
		                if (is_connection(current_carr, turtle)) {
		                	timer_stop = true;
		                	show_score();
		            	}
		                
		                if (turtle.getY() < 10) {
		                	timer_stop  = true;
		                	show_win_score();
		                }
		                if(current_carr.getX() > current_carr.getWidth() + screen_width()) {
		            		iterr.remove();
		            	}
		            }
		            
		            while(iterl.hasNext()){
		                Sprite current_carl = iterl.next();
		                current_carl.setPosition(current_carl.getX() + 3, current_carl.getY());
		                
		                if (is_connection(current_carl, turtle)) {
		                	timer_stop = true;
		                	show_score();
		            	}
		                
		                if (turtle.getY() < 10) {
		                	timer_stop  = true;
		                	show_win_score();
		                }
		                
		                if(current_carl.getX() > current_carl.getWidth() + screen_width()) {
		            		iterl.remove();
		            	}
		            }
			    }
		    }
		});
		scene.registerUpdateHandler(mTimerHandler);
	}

	private void on_init(Scene scene) {
		Random random_number = new Random(System.currentTimeMillis());
		float y = screen_height() / 4 * 2;
		Sprite new_car = new Sprite(screen_width()/2, y, textures.get(images[random_number.nextInt(IMAGES_COUNT)]), game_instance.getVertexBufferObjectManager());
		new_car.setRotation(180);
		scene.attachChild(new_car);
        cars_right.add(new_car);
        new_car = null;
       
		y = screen_height() / 4;
		Sprite next_car = new Sprite(screen_width()/3, y, textures.get(images[random_number.nextInt(IMAGES_COUNT)]), game_instance.getVertexBufferObjectManager());
        scene.attachChild(next_car);
        cars_left.add(next_car);
        next_car = null;
	}

	private void set_enviroment(final Scene scene) {
		this.turtle = new Sprite(0, 0, textures.get("turtle"), game_instance.getVertexBufferObjectManager());
		this.turtle.setPosition(screen_width() / 2 - this.turtle.getWidth() / 2, screen_height() - this.turtle.getHeight());
		scene.attachChild(this.turtle);
		
		this.car = new Sprite(0, 0, textures.get("green_car"), game_instance.getVertexBufferObjectManager());

		on_init(scene);
		car_dropping(scene);
	}
}
