package com.timewaste.games.shoot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
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

import com.timewaste.games.arcadeshooter.ArcadeShooter;
import com.timewaste.timewaste.GameActivity;

public class Shoot extends GameActivity {

	
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private Music gameMusic;

	@Override
	public EngineOptions onCreateEngineOptions() {
		super.onCreateEngineOptions();
		CAMERA_WIDTH = 720;
		CAMERA_HEIGHT = 480;
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		return engineOptions;
	}
	
	@Override
	public void onCreateResources() {
		MusicFactory.setAssetBasePath("mfx/");
		SoundFactory.setAssetBasePath("mfx/");
		final Context context = this;
		try {
			ITexture building = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/shoot/jpg/building.jpg");
				}
			});
			
			ITexture empty = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/shoot/jpg/empty.jpg");
				}
			});
			
			ITexture buggs = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shoot/jpg/buggs.jpg");
		        }
		    });
			ITexture coyote = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shoot/jpg/coyote.jpg");
		        }
		    });
			ITexture daffy = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shoot/jpg/daffy.jpg");
		        }
		    });
			
			ITexture gonzales = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shoot/jpg/gonzales.jpg");
		        }
		    });
			
			ITexture skunks = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shoot/jpg/skunks.jpg");
		        }
		    });
			
			ITexture tweety = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shoot/jpg/tweety.jpg");
		        }
		    });
			
			building.load();
			empty.load();
		    buggs.load();
		    coyote.load();
		    daffy.load();
		    gonzales.load();
		    skunks.load();
		    tweety.load();

		    textures.put("building", TextureRegionFactory.extractFromTexture(building));
		    textures.put("empty", TextureRegionFactory.extractFromTexture(empty));
		    textures.put("buggs", TextureRegionFactory.extractFromTexture(buggs));
		    textures.put("coyote", TextureRegionFactory.extractFromTexture(coyote));
		    textures.put("daffy", TextureRegionFactory.extractFromTexture(daffy));
		    textures.put("gonzales", TextureRegionFactory.extractFromTexture(gonzales));
		    textures.put("skunks", TextureRegionFactory.extractFromTexture(skunks));
		    textures.put("tweety", TextureRegionFactory.extractFromTexture(tweety));
		    
		    this.gameMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "shoot/background.ogg");
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

		/* Create the background and add it to the scene. */
		final Sprite ground_image = new Sprite(0, 0, this.textures.get("building"), this.getVertexBufferObjectManager()); 
		ground_image.setWidth(CAMERA_WIDTH);
		scene.attachChild(ground_image);
		@SuppressWarnings("unused")
		ShootLogic logic = new ShootLogic(this, scene, this.textures);
		Shoot.this.gameMusic.play();
		this.runCycle(scene);
		return scene;
	}
}
