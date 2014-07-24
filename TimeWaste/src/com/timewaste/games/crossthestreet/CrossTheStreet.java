package com.timewaste.games.crossthestreet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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
import android.view.Display;
import android.view.Surface;

import com.timewaste.games.shitmageddon.ShitmageddonLogic;
import com.timewaste.timewaste.GameActivity;

public class CrossTheStreet extends GameActivity {
	
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
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);

		engineOptions.getAudioOptions().setNeedsMusic(true);
		return engineOptions;
	}
	
	@Override
	public void onCreateResources() {
		MusicFactory.setAssetBasePath("mfx/");
		final Context context = this;
		try {
			ITexture road = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/crossthestreet/road.png");
				}
			});
			ITexture turtle = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/crossthestreet/turtle.png");
				}
			});
			ITexture red_car = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/crossthestreet/red_car.png");
		        }
		    });
			ITexture blue_car = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/crossthestreet/blue_car.png");
		        }
		    });
			ITexture green_car = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/crossthestreet/green_car.png");
		        }
		    });
			ITexture yellow_car = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/crossthestreet/yellow_car.png");
		        }
		    });
			
			road.load();
			turtle.load();
			red_car.load();
			blue_car.load();
			green_car.load();
			yellow_car.load();

		    textures.put("road", TextureRegionFactory.extractFromTexture(road));
		    textures.put("turtle", TextureRegionFactory.extractFromTexture(turtle));
		    textures.put("red_car", TextureRegionFactory.extractFromTexture(red_car));
		    textures.put("blue_car", TextureRegionFactory.extractFromTexture(blue_car));
		    textures.put("green_car", TextureRegionFactory.extractFromTexture(green_car));
		    textures.put("yellow_car", TextureRegionFactory.extractFromTexture(yellow_car));
		    
		    this.gameMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "evade/driving.ogg");
			this.gameMusic.setLooping(true);
			
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		super.onCreateScene();
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.9f, 0.9f, 0.6f));

		final Sprite ground_image = new Sprite(0, 0, this.textures.get("road"), this.getVertexBufferObjectManager()); 
		ground_image.setWidth(CAMERA_WIDTH);
		ground_image.setHeight(CAMERA_HEIGHT);
		scene.attachChild(ground_image);

		CrossTheStreetLogic logic = new CrossTheStreetLogic(this, scene, this.textures);
		this.enableAccelerationSensor(logic);
		//CrossTheStreet.this.gameMusic.play();
		this.runCycle(scene);
		return scene;
	}
}
