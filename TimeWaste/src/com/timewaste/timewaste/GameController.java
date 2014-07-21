package com.timewaste.timewaste;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.timewaste.games.shoot.Shoot;
import com.timewaste.games.tictactoe.*;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class GameController extends Service {
	/** indicates how to behave if the service is killed */
	int mStartMode;
	/** interface for clients that bind */
	IBinder mBinder;     
	/** indicates whether onRebind should be used */
	boolean mAllowRebind;
	
	private int category;
	
	private Random randomizer;
	
	private List<Class<?>>[] categories;
	
	private int mInterval = 30000; // 30 seconds by default, can be changed later
	private Handler mHandler;
	
	Context context;

	/** Called when the service is being created. */
	@Override
	public void onCreate() {
		randomizer = new Random();
		mHandler = new Handler();
		categories = new List[4];
		categories[0] = new ArrayList<Class<?>>();
		categories[1] = new ArrayList<Class<?>>();
		categories[2] = new ArrayList<Class<?>>();
		categories[3] = new ArrayList<Class<?>>();
		
		categories[0].add(TicTacToe.class);
		categories[0].add(Shoot.class);
		
		context = this;
		startRepeatingTask();
		
	} 
    
	/** The service is starting, due to a call to startService() */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		category = intent.getIntExtra("category", 1) - 1;
		return mStartMode;
	}
	
	Runnable mGameLoader = new Runnable() {
		@Override 
		public void run() {
			loadNextGame();
		    mHandler.postDelayed(mGameLoader, mInterval);
		}
	};
	
	void startRepeatingTask() {
		mGameLoader.run(); 
	}
	
	void stopRepeatingTask() {
	    mHandler.removeCallbacks(mGameLoader);
	}
	
	public void loadNextGame() {
        Intent dialogIntent = new Intent(context, categories[category].get(randomizer.nextInt(categories[category].size())));
		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(dialogIntent);
        Toast.makeText(this, "Game loaded", Toast.LENGTH_LONG).show();
	}
	
	/** A client is binding to the service with bindService() */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/** Called when all clients have unbound with unbindService() */
 	@Override
 	public boolean onUnbind(Intent intent) {
 		return mAllowRebind;
 	}

 	/** Called when a client is binding to the service with bindService()*/
 	@Override
 	public void onRebind(Intent intent) {

 	}

 	/** Called when The service is no longer used and is being destroyed */
 	@Override
 	public void onDestroy() {
 		stopRepeatingTask();
 	}
}
