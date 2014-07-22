package com.timewaste.timewaste;

import com.timewaste.games.labyrinth.Labyrinth;
import com.timewaste.games.shitmageddon.Shitmageddon;
import com.timewaste.games.shoot.Shoot;
import com.timewaste.games.tictactoe.TicTacToe;
import com.timewaste.timewaste.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class CategoriesActivity extends Activity {
	
	private Categories categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		categories = new Categories();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		stopService(new Intent(getBaseContext(), GameController.class));
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	private int categoryIdToInt(int id) {
		int category;
		switch (id) {
		    case (R.id.bus):
		        category = 2;
		    break;
		    case (R.id.toilet):
		        category = 1;
		    break;
		    case (R.id.traffic_lights):
		        category = 0;
		    break;
		    case (R.id.somewhere):
		        category = 3;
		    break;
		    default:
		    	category = -1;
	    }
		return category;
	}
	
	/*
	public void playGame(View view) {
		int gameCategory = categoryIdToInt(view.getId());
		Intent gameIntent = new Intent(this, categories.selectGame(gameCategory, null));
        
        gameIntent.putExtra("category", gameCategory);
        gameIntent.putExtra("gameTime", categories.gameTime(gameCategory));
        
    	startActivity(gameIntent);
    }
	
	*/
	public void playGame(View view) {
		  
		  Intent gameIntent = new Intent(this, Shitmageddon.class);
		        
		        gameIntent.putExtra("category", 3);
		        gameIntent.putExtra("gameTime", -1);
		        
		        startActivity(gameIntent);
		    }
	
	
	public void onStop() {
		super.onStop();
	}
}
