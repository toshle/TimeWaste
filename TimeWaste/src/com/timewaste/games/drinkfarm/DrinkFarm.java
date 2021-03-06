package com.timewaste.games.drinkfarm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.LinkedList;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
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
	private Music gameMusic;

	@Override
	public EngineOptions onCreateEngineOptions() {
		super.onCreateEngineOptions();
		if(screen_height() < screen_width()) {
			CAMERA_WIDTH = screen_width();
			CAMERA_HEIGHT = screen_height();
		} else {
			CAMERA_WIDTH = screen_height();
			CAMERA_HEIGHT = screen_width();
		}
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);

		engineOptions.getAudioOptions().setNeedsMusic(true);
		return engineOptions;
	}
	
	@Override
	public void onCreateResources() {
		super.onCreateResources();
		MusicFactory.setAssetBasePath("mfx/");
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
			
			ITexture liquid_stream = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/drinkfarm/png/liquid_stream.png");
				}
			});
			
			ITexture liquid_stream_half = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/drinkfarm/png/liquid_stream_half.png");
				}
			});
			
			machine.load();
			cup.load();
			bubble.load();
			liquid_stream.load();
			liquid_stream_half.load();
			
		    textures.put("machine", TextureRegionFactory.extractFromTexture(machine));
		    textures.put("cup", TextureRegionFactory.extractFromTexture(cup));
		    textures.put("bubble", TextureRegionFactory.extractFromTexture(bubble));
		    textures.put("liquid_stream", TextureRegionFactory.extractFromTexture(liquid_stream));
		    textures.put("liquid_stream_half", TextureRegionFactory.extractFromTexture(liquid_stream_half));
		
		    this.gameMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "drinkfarm/background.ogg");
			this.gameMusic.setLooping(true);
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
		System.out.println(CAMERA_WIDTH);
		scene.attachChild(background_image);
		
		DrinkFarmLogic logic = new DrinkFarmLogic(this, scene, this.textures);
		logic.render(scene);
		gameMusic.play();
		this.runCycle(scene);
		
		return scene;
	}
}