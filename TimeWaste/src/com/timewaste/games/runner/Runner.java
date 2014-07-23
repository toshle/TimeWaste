package com.timewaste.games.runner;

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
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.R.animator;
import android.content.Context;
import android.graphics.Point;

import com.timewaste.timewaste.GameActivity;

public class Runner extends GameActivity {

	private int CAMERA_WIDTH = 720;
	private int CAMERA_HEIGHT = 480;

	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();
	private TiledTextureRegion stickmanRun;
	private Music gameMusic;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		super.onCreateEngineOptions();
		CAMERA_WIDTH = screen_width();
		CAMERA_HEIGHT = screen_height();
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
		
		engineOptions.getAudioOptions().setNeedsMusic(true);
		return engineOptions;
	}
	
	@Override
	public void onCreateResources() {
		//Load music.
		MusicFactory.setAssetBasePath("mfx/");
		
		//Load animated Texture
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/runner/gif/");
		BuildableBitmapTextureAtlas animated_texture = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.NEAREST);
		stickmanRun = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(animated_texture, this, "stickman_run2.gif", 2, 1);
		
		try {
			animated_texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			animated_texture.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		//Load simple Texture
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
		
		//Runner.this.gameMusic.play();
		
		final AnimatedSprite stickman = new AnimatedSprite(100, 100, stickmanRun, this.getVertexBufferObjectManager());
		stickman.animate(140);
		scene.attachChild(stickman);
		stickman.setScale(1.0f, 2.0f);
		
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if(pSceneTouchEvent.isActionDown()) {
					finger_coordinates = new Point((int)pSceneTouchEvent.getX(), (int)pSceneTouchEvent.getY());
				}
				if(pSceneTouchEvent.isActionUp()) { 
					if(finger_coordinates.y > pSceneTouchEvent.getY()) stickman.animate(500);//System.out.println("Up");
					else stickman.animate(140);//System.out.println("Down");
				}
				return false;
			}
		});
		
		
		
		this.runCycle(scene);
		return scene;
	}
	
	private Point finger_coordinates = new Point(0, 0);
}

