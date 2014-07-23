package com.timewaste.games.evade;

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

public class Evade extends GameActivity {

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
			ITexture background = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/shitmageddon/png/background.png");
				}
			});
			
			ITexture toilet = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/shitmageddon/gif/toilet.gif");
				}
			});
			
			ITexture shit = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return context.getAssets().open("gfx/shitmageddon/gif/shit.gif");
		        }
		    });
			
			background.load();
			shit.load();
		    toilet.load();

		    textures.put("background", TextureRegionFactory.extractFromTexture(background));
		    textures.put("toilet", TextureRegionFactory.extractFromTexture(toilet));
		    textures.put("shit", TextureRegionFactory.extractFromTexture(shit));
		    
		    this.gameMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "shitmageddon/Heroes_of_Might_and_Magic_3_Music-_Combat_2.ogg");
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
		final Sprite ground_image = new Sprite(0, 0, this.textures.get("background"), this.getVertexBufferObjectManager()); 
		ground_image.setWidth(CAMERA_WIDTH);
		ground_image.setHeight(CAMERA_HEIGHT);
		scene.attachChild(ground_image);
		//@SuppressWarnings("unused")
		//ShitmageddonLogic logic = new ShitmageddonLogic(this, scene, this.textures);
		
		//Evade.this.gameMusic.play();
		this.runCycle(scene);
		return scene;
	}
}