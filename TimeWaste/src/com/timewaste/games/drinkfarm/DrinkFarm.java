package com.timewaste.games.drinkfarm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.LinkedList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.Context;

import com.timewaste.games.drinkfarm.DrinkFarmLogic;
import com.timewaste.timewaste.GameActivity;

public class DrinkFarm extends GameActivity {

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private static Camera camera;
	private static Scene scene;
	
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();

	@Override
	public EngineOptions onCreateEngineOptions() {
		super.onCreateEngineOptions();
		CAMERA_WIDTH = screen_width();
		CAMERA_HEIGHT = screen_height();
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
	}
	
	@Override
	public void onCreateResources() {
		super.onCreateResources();
		final Context context = this;
		try {
			ITexture machine = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/drinkfarm/png/machine.png");					
				}
			});
			
			ITexture cup = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/drinkfarm/png/cup.png");
				}
			});
			
			ITexture bubble = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/drinkfarm/png/bubble.png");
				}
			});
			
			machine.load();
			cup.load();
			bubble.load();
			
		    textures.put("machine", TextureRegionFactory.extractFromTexture(machine));
		    textures.put("cup", TextureRegionFactory.extractFromTexture(cup));
		    textures.put("bubble", TextureRegionFactory.extractFromTexture(bubble));	
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		super.onCreateScene();
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
		scene.setBackground(new Background(0.9f, 0.9f, 0.6f));

		/* Create the background and add it to the scene. */
		final Sprite background_image = new Sprite(0, 0, this.textures.get("machine"), this.getVertexBufferObjectManager()); 
		background_image.setWidth(CAMERA_WIDTH);
		background_image.setHeight(CAMERA_HEIGHT);
		
		scene.attachChild(background_image);
		
		DrinkFarmLogic logic = new DrinkFarmLogic(this, scene, this.textures);
		logic.render(scene);
		this.runCycle(scene);
		
		return scene;
	}
}