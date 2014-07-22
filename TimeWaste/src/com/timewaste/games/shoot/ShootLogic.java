package com.timewaste.games.shoot;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.andengine.AndEngine;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.TextUtils;

import com.timewaste.timewaste.GameActivity;

import android.graphics.Color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

public class ShootLogic {
	//Constants
	private final int IMAGES_COUNT = 6;
	private final String images[] = { "buggs", "coyote", "daffy", "gonzales", "skunks", "tweety" };
	
	//Fields
	private Sprite ground[] = new Sprite[IMAGES_COUNT];
	private Shoot game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private Text score;
	
	private float screen_width() {
		return game_instance.cameraWidth();
	}
	
	private float screen_height() {
		return game_instance.cameraHeight();
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
	
	//Randomize spot and picture;
	private void randomize_image() {
		Random random_number = new Random(System.currentTimeMillis());
		ground[random_number.nextInt(IMAGES_COUNT)].
		setTextureRegion(textures.get(images[random_number.nextInt(IMAGES_COUNT)]));
	}
	
	private void increase_score() {
		int current_score = Integer.parseInt(this.score.getText().toString());
		this.score.setText(Integer.toString(current_score + 100));
		this.score.setPosition((screen_width() - this.score.getWidth()) / 2 - 30, 80);
	}
	
	//Touching logic.
	private Sprite set_image_logic(){
		return new Sprite(0, 0, textures.get("empty"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(this.getTextureRegion() != textures.get("empty")) {
					this.setTextureRegion(textures.get("empty"));
					randomize_image();
					increase_score();
					//If timer runs out -> show_score();
				}	
				return true;
			}
		};
	}
	
	private void set_fonts(Scene a_scene) {
		Font font = FontFactory.create(game_instance.getFontManager(), game_instance.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.RED);
		font.load();
		
		final Text centerText = 
				new Text(0, 0, font, "Score", 
				new TextOptions(HorizontalAlign.CENTER), game_instance.getVertexBufferObjectManager());

		this.score =
				new Text(0, 0, font, "0", 6,
				new TextOptions(HorizontalAlign.CENTER), game_instance.getVertexBufferObjectManager());
		
		centerText.setPosition((screen_width() - centerText.getWidth()) / 2 - 30, 40);
		this.score.setPosition((screen_width() - centerText.getWidth()) / 2, 80);
		
		a_scene.attachChild(centerText);
		a_scene.attachChild(this.score);
	}
	
	//Formula to set the ground images. Also registering touch events for every image.
	private void set_environment(Scene a_scene) {      
		Sprite template = new Sprite(0, 0, textures.get("empty"), game_instance.getVertexBufferObjectManager());
		float leftMargin = screen_width() / template.getWidth() * 8.5f,
			  topMargin = template.getHeight() * 1.6f;
		float current_x = leftMargin,
			  current_y = topMargin,
			  separator_x = template.getWidth() * 5.3f,
			  separator_y = template.getHeight() * 2f;
		
		for(int i = 0; i < IMAGES_COUNT; i++) {
			if(i != 0 && (i % 3) == 0) {
				current_x = leftMargin;
				current_y += separator_y;
			}
			ground[i] = set_image_logic(); 
			ground[i].setPosition(current_x, current_y);
			a_scene.registerTouchArea(ground[i]);
			a_scene.attachChild(ground[i]);
			current_x += separator_x;
			//separator_x = 270;
		}
		set_fonts(a_scene);
		randomize_image();
	}
	
	public ShootLogic(Shoot game_instance, Scene a_scene, Map<String, ITextureRegion> textures) {
		this.game_instance = game_instance;
		this.textures = textures;
		set_environment(a_scene);
	}
}
