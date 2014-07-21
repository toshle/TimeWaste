package com.timewaste.games.shoot;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ShootLogic {
	//Constants
	private final int IMAGES_COUNT = 6;
	private final String images[] = { "buggs", "coyote", "daffy", "gonzales", "skunks", "tweety" };
	
	//Fields
	private Sprite ground[] = new Sprite[IMAGES_COUNT];
	private SimpleBaseGameActivity game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	
	private void announce_winner(final int winner) {
		game_instance.runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		        //Toast.makeText(TickTackToe.this, message, Toast.LENGTH_SHORT).show();
		    	AlertDialog.Builder alert = new AlertDialog.Builder(game_instance).
		        setTitle("Game Ended");
		    	switch (winner) {
					case 1:
						alert.setMessage("You Win!") ;
						break;
					case 2:
						alert.setMessage("Computer Wins!") ;
						break;
					default:
						alert.setMessage("No one wins.") ;
						break;
				}
		        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			        	game_instance.finish();
			        }
		        });
		    	alert.show();
		    }
		});
	}
	
	//Randomize spot and picture;
	private void randomize_image() {
		Random random_number = new Random(System.currentTimeMillis());
		ground[random_number.nextInt(IMAGES_COUNT)].
		setTextureRegion(textures.get(images[random_number.nextInt(IMAGES_COUNT)]));
	}
	
	//Touching logic.
	private Sprite set_image_logic(){
		return new Sprite(0, 0, textures.get("empty"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(this.getTextureRegion() != textures.get("empty")) {
					this.setTextureRegion(textures.get("empty"));
					randomize_image();
					//Score++?
				}	
				return true;
			}
		};
	}
	
	//Formula to set the ground images. Also registering touch events for every image.
	private void set_ground_images(Scene a_scene) {
		float device_width  = game_instance.getResources().getDisplayMetrics().widthPixels;
        float device_height = game_instance.getResources().getDisplayMetrics().heightPixels;
        
		float current_x = a_scene.getFirstChild().getX() + device_width / 16;
		float current_y = a_scene.getFirstChild().getY() + device_height / 3.7f;
		float separator_x = device_width / 2.8f, separator_y = device_height / 2.55f;
	
		for(int i = 0; i < IMAGES_COUNT; i++) {
			if(i != 0 && (i % 3) == 0) {
				current_x = a_scene.getFirstChild().getX() + device_width / 16;
				current_y += separator_y;
			}
			if(i != 0 && ((i + 1) % 2) == 0) separator_x /= 1.1f; 
			ground[i] = set_image_logic(); 
			ground[i].setPosition(current_x, current_y);
			a_scene.registerTouchArea(ground[i]);
			current_x += separator_x;
			separator_x = device_width / 2.8f;
		}
		ground[4].setPosition(ground[4].getX() + separator_x / 12f, ground[4].getY());
		randomize_image();
	}
	
	public ShootLogic(SimpleBaseGameActivity game_instance, Scene a_scene, Map<String, ITextureRegion> textures)
	{
		this.game_instance = game_instance;
		this.textures = textures;
		set_ground_images(a_scene);
		
//		String asd = "asdasd";
//		int bar = Integer.parseInt(asd);
//		String asdd = Integer.toString(bar);

	}
	
	public void render(Scene a_scene) {
		for(int i = 0; i < IMAGES_COUNT; i++) {
			a_scene.attachChild(ground[i]);
		}
	}
}
