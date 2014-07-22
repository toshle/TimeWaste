package com.timewaste.timewaste;

import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.timewaste.utils.Timer;
import com.timewaste.utils.Timer.ITimerCallback;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GameActivity extends SimpleBaseGameActivity {

	private int mInterval; 

	private Scene mScene;

	private Context context;

	private Categories categories;

	private int gameCategory;

	private Class<?> currentGame;

	private Timer timeCallback;

	private int timeLeft;

	@Override
	public EngineOptions onCreateEngineOptions() {
		categories = new Categories();
		gameCategory = getIntent().getIntExtra("category", 0);
		currentGame = getIntent().getClass();
		mInterval = getIntent().getIntExtra("gameTime", 0);
		context = this;

		if(getIntent().getBooleanExtra("nextGame", false) == true) {
			Toast.makeText(context, "Time's up! Next game.", Toast.LENGTH_LONG).show();
		}

		timeCallback = new Timer(1f, new ITimerCallback() {
			int t = mInterval;
			@Override
			public void onTick() {
				if(t > 0)
					timeLeft = t--;
				else {
					finalization();
					loadNextGame();
				}
			}
		});

		return null;
	}

	public int timeLeft() {
		return timeLeft;
	}

	@Override
	protected void onCreateResources() {
	}

	@Override
	protected Scene onCreateScene() {
		return null;
	}

	protected void runCycle(Scene scene) {
		if(mInterval != -1) {
			mScene = scene;
			mScene.registerUpdateHandler(timeCallback);
		}
	}

	public void finalization() {
		if(mScene != null)
			mScene.unregisterUpdateHandler(timeCallback);
		this.finish();
	}

	public void loadNextGame() {
        Intent gameIntent = new Intent(context, categories.selectGame(gameCategory, currentGame));
        
        gameIntent.putExtra("category", gameCategory);
        gameIntent.putExtra("gameTime", categories.gameTime(gameCategory));
        gameIntent.putExtra("nextGame", true);
        
		context.startActivity(gameIntent);
	}

	@Override
	public void onBackPressed() {
		Intent dialogIntent = new Intent(context, CategoriesActivity.class);

		context.startActivity(dialogIntent);
	}

	@Override
	protected void onStop() {
		finalization();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}