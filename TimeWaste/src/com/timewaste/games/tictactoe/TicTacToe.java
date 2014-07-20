package com.timewaste.games.tictactoe;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.timewaste.timewaste.GameActivity;
import com.timewaste.utils.Timer;
import com.timewaste.utils.Timer.ITimerCallback;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.view.Display;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */
public class TicTacToe extends GameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	/* Initializing the Random generator produces a comparable result over different versions. */
	private static final long RANDOM_SEED = 1234567890;
	
	private int width;
	private int height;
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@SuppressLint("NewApi")
	private void getSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		getSize();
		final Camera camera = new Camera(0, 0, width, height);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(width, height), camera);
	}

	@Override
	public void onCreateResources() {
		
	}

	@Override
	public Scene onCreateScene() {
		super.onCreateScene();
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		final Random random = new Random(RANDOM_SEED);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
	
		scene.registerUpdateHandler(new Timer(1f, new ITimerCallback() {
			 
            int t = 300;
           
            @Override
            public void onTick() {
            		if(t > 0)
            			t -= 10;
                    final Line line2 = new Line(400 - t, 100, 400 - t, 200, 10, vertexBufferObjectManager);

        			line2.setColor(random.nextFloat(), random.nextFloat(), random.nextFloat());

        			scene.attachChild(line2);
            }
		}));

		return scene;
	}
	
	public void onStop() {
		super.onStop();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
