package com.timewaste.timewaste;

import com.timewaste.timewaste.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.widget.TextView;

public class CategoriesActivity extends Activity {
	
	private Categories categories;
	
	private SharedPreferences storage;
	
	private long totalScore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		categories = new Categories();
		storage = this.getSharedPreferences(getString(R.string.localStorage), Context.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
	}
	
	private void refreshPoints() {
		((TextView) findViewById(R.id.scorePoints)).setText("" + totalScore);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		totalScore = storage.getLong("totalScore", 0);
		refreshPoints();
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
	
	public void playGame(View view) {
		int gameCategory = categoryIdToInt(view.getId());
		Intent gameIntent = new Intent(this, categories.selectGame(gameCategory, null));
        
        gameIntent.putExtra("category", gameCategory);
        gameIntent.putExtra("gameTime", categories.gameTime(gameCategory));
        
    	startActivity(gameIntent);
    }
	
	public void onStop() {
		super.onStop();
	}
}
