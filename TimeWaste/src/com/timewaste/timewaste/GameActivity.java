package com.timewaste.timewaste;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.timewaste.games.shoot.Shoot;
import com.timewaste.games.tictactoe.TicTacToe;
import com.timewaste.utils.Timer;
import com.timewaste.utils.Timer.ITimerCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */
public class GameActivity extends SimpleBaseGameActivity {
	

	private int mInterval = 10000; // 10 seconds by default, can be changed later
	private Scene mScene;
	
	private Context context;
	

	private int category;
	
	private Random randomizer;
	
	private List<Class<?>>[] categories;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		randomizer = new Random();
		categories = new List[4];
		categories[0] = new ArrayList<Class<?>>();
		categories[1] = new ArrayList<Class<?>>();
		categories[2] = new ArrayList<Class<?>>();
		categories[3] = new ArrayList<Class<?>>();
		
		categories[0].add(TicTacToe.class);
		categories[0].add(Shoot.class);
		
		context = this;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onCreateResources() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Scene onCreateScene() {
		
		return null;
	}
	
	private Timer timeCallback = new Timer(1f, new ITimerCallback() {
		int t = 30;
		@Override
		public void onTick() {
			if(t > 0)
				t -= 1;
			else {
				finalization();
				loadNextGame();
			}
		}
	});
	
	protected void runCycle(Scene scene) {
		mScene = scene;
		mScene.registerUpdateHandler(timeCallback);
	}
	
	public void finalization() {
		if(mScene != null)
			mScene.unregisterUpdateHandler(timeCallback);
		this.finish();
	}
	
	public void loadNextGame() {
		//Toast.makeText(context, "Next game. Begin!", Toast.LENGTH_SHORT).show();
        Intent dialogIntent = new Intent(context, categories[category].get(randomizer.nextInt(categories[category].size())));
		//dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(dialogIntent);
	}
	
	@Override
	public void onBackPressed() {
		Intent dialogIntent = new Intent(context, CategoriesActivity.class);
		//dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

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
