package com.timewaste.timewaste;

import com.timewaste.games.tictactoe.TicTacToe;
import com.timewaste.timewaste.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class CategoriesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
	
	public void playGame(View view) {
		//startService(new Intent(getBaseContext(), GameController.class).putExtra("category", 1));
    	Intent my_intent = new Intent(CategoriesActivity.this, TicTacToe.class);
    	startActivity(my_intent);
    }
	
	public void onStop() {
		super.onStop();
		//stopService(new Intent(getBaseContext(), GameController.class));
	}
}
