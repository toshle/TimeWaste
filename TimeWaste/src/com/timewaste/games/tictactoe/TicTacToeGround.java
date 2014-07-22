package com.timewaste.games.tictactoe;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import com.timewaste.timewaste.GameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class TicTacToeGround {
	private Sprite ground[] = new Sprite[9];
	private GameActivity game_instance;
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	
	//Constants
	final int IMAGES_COUNT = 9;
	
	private void AI_move() {
		boolean move_made = false;
		Random random = new Random(System.currentTimeMillis());
		while(!move_made) {
			int current_index = random.nextInt(9);
			if(ground[current_index].getTextureRegion() == textures.get("empty")) {
				ground[current_index].setTextureRegion(textures.get("tack"));
				move_made = true;
			}
		}
	}
	
	private int horizontal_check() {
		for(int i = 0; i < 9; i += 3) {
			boolean tick_wins = true, tack_wins = true;
			for(int j = 0; j < 3; j++) {
				if(ground[j + i].getTextureRegion() != textures.get("tick")) tick_wins = false;
				if(ground[j + i].getTextureRegion() != textures.get("tack")) tack_wins = false;
			}
			if(tick_wins) { return 1; }
			if(tack_wins) { return 2; }
		}
		return -1;
	}
	
	private int vertical_check() {
		for(int i = 0; i < 3; i++) {
			boolean tick_wins = true, tack_wins = true;
			for(int j = 0; j < 9; j += 3) {
				if(ground[j + i].getTextureRegion() != textures.get("tick")) tick_wins = false;
				if(ground[j + i].getTextureRegion() != textures.get("tack")) tack_wins = false;
			}
			if(tick_wins) { return 1; }
			if(tack_wins) { return 2; }
		}
		return -1;
	}
	
	private int diagonals_check() {
		if
		(
			(ground[0].getTextureRegion() == textures.get("tick") && ground[4].getTextureRegion() == textures.get("tick") && ground[8].getTextureRegion() == textures.get("tick")) ||
			(ground[2].getTextureRegion() == textures.get("tick") && ground[4].getTextureRegion() == textures.get("tick") && ground[6].getTextureRegion() == textures.get("tick"))
		) return 1;
		
		if
		(
			(ground[0].getTextureRegion() == textures.get("tack") && ground[4].getTextureRegion() == textures.get("tack") && ground[8].getTextureRegion() == textures.get("tack")) ||
			(ground[2].getTextureRegion() == textures.get("tack") && ground[4].getTextureRegion() == textures.get("tack") && ground[6].getTextureRegion() == textures.get("tack"))
		) return 2;
		
		return -1;
	}
	
	private int is_game_won() {
		int winner = horizontal_check();
		if(winner == -1) winner = vertical_check();
		if(winner == -1) winner = diagonals_check();
		return winner;
	}
	
	private boolean board_full() {
		for(int i = 0; i < IMAGES_COUNT; i++) {
			if(ground[i].getTextureRegion() == textures.get("empty")) return false;
		}
		return true;
	}
	
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
			        	game_instance.finalization();
			        	game_instance.loadNextGame();
			        }
		        });
		    	alert.show();
		    }
		});
	}
	
	//Touching logic.
	private Sprite set_image_logic() {
		return new Sprite(0, 0, textures.get("empty"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if(this.getTextureRegion() == textures.get("empty")) {
					this.setTextureRegion(textures.get("tick"));
					int winner = is_game_won();
					if(winner == -1 && !board_full()) {
						AI_move();
						winner = is_game_won();
						if(winner != -1) announce_winner(winner);
					}
					else {
						announce_winner(winner);
					}
				}	
				return true;
			}
		};
	}
	
	//Formula to set the ground images. Also registering touch events for every image.
	private void set_ground_images(Scene a_scene) {
		float separator_x = 25,  separator_y = 15, current_x = a_scene.getChildByIndex(0).getX(), current_y = a_scene.getChildByIndex(0).getY();
		for(int i = 0; i < IMAGES_COUNT; i++) {
			if(i % 3 == 0 && i != 0) {
				current_y += textures.get("empty").getHeight();
				current_x  = a_scene.getChildByIndex(0).getX();
			}
			ground[i] = set_image_logic();
			ground[i].setPosition(current_x + separator_x * ((i % 3) + 1), current_y + separator_y);
			a_scene.attachChild(ground[i]);
			a_scene.registerTouchArea(ground[i]);
			current_x += textures.get("empty").getWidth();
		}
	}
	
	public TicTacToeGround(GameActivity game_instance, Scene a_scene, Map<String, ITextureRegion> textures)
	{
		this.game_instance = game_instance;
		this.textures = textures;
		set_ground_images(a_scene);
	}
}
