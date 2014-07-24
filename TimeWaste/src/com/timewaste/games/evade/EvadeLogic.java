package com.timewaste.games.evade;

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

public class EvadeLogic implements IAccelerationListener {

	private Sprite car;
	private Sprite hole;
	private Evade game_instance;
	private List<Sprite> stones;
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
		float newX = car.getX() + pAccelerationData.getX()*3;
		float newY = car.getY() + pAccelerationData.getY()*3;

		float x = car.getX();
		float y = car.getY();

		if(newX >= 0 && newX <= screen_width() - car.getWidth()) {
			x = newX;
		}
		if(newY >= 0 && newY <= screen_height() - car.getHeight()) {
			y = newY;
		}
		car.setPosition(x, y);
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

	public EvadeLogic(Evade game_instance, final Scene scene, Map<String, ITextureRegion> textures) {
		this.game_instance = game_instance;
		this.textures = textures;
		stones = new ArrayList<Sprite>();
		this.speed = 25;
		this.current_speed = 25;
		this.timer_stop = false;
		
		set_enviroment(scene);
	}
	
	private void random_stone(final Scene scene) {
		Random random_number = new Random(System.currentTimeMillis());
		float stoneX = random_number.nextInt((int) (screen_width() - hole.getWidth())),
        	  stoneY = -hole.getHeight();

        Sprite new_stone = new Sprite(stoneX, stoneY, textures.get("hole"), game_instance.getVertexBufferObjectManager());
        scene.attachChild(new_stone);
        stones.add(new_stone);
        new_stone = null;
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

	private void stone_droping(final Scene scene) {
		TimerHandler mTimerHandler = new TimerHandler(0.01f, true, new ITimerCallback() {
		    @Override
		    public void onTimePassed(TimerHandler pTimerHandler) {
		    	if (!timer_stop) {
			    	speed--;
			    	if (speed == 0) {
			    		random_stone(scene);
			    		speed = current_speed;
			    	}
			    	Iterator<Sprite> iter = stones.iterator();
		            while(iter.hasNext()){
		                Sprite current_stone = iter.next();
		                current_stone.setPosition(current_stone.getX(), current_stone.getY() + 3);
		                
		                if (is_connection(current_stone, car)) {
		                	timer_stop = true;
		                	show_score();
		            	}
		                
		                if(current_stone.getY() > current_stone.getHeight() + screen_height()) {
		            		iter.remove();
		            	}
		            }
			    }
		    }
		});
		scene.registerUpdateHandler(mTimerHandler);
	}

	private void set_enviroment(final Scene scene) {
		this.car = new Sprite(0, 0, textures.get("car"), game_instance.getVertexBufferObjectManager());
		this.car.setPosition(screen_width() / 2 - this.car.getWidth() / 2, screen_height() / 2 - this.car.getHeight());
		scene.attachChild(this.car);
		
		this.hole = new Sprite(0, 0, textures.get("hole"), game_instance.getVertexBufferObjectManager());

		stone_droping(scene);
	}
}
