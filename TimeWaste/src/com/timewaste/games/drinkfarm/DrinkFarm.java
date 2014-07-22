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

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	private static Camera camera;
	private static Scene scene;
	private LinkedList TargetsToBeAdded;
	
	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();

	@Override
	public EngineOptions onCreateEngineOptions() {
		super.onCreateEngineOptions();
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new FillResolutionPolicy(), camera);
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
			
//			ITexture bubble = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
//				@Override
//				public InputStream open() throws IOException {
//					return context.getAssets().open("gfx/drinkfarm/png/bubble.png");
//				}
//			});
			
			machine.load();
			cup.load();
			//bubble.load();
			
		    textures.put("machine", TextureRegionFactory.extractFromTexture(machine));
		    textures.put("cup", TextureRegionFactory.extractFromTexture(cup));
		    //textures.put("bubble", TextureRegionFactory.extractFromTexture(bubble));	
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	public void addTarget() {
	    Random rand = new Random();
	 
	    int x = (int) camera.getWidth() + CAMERA_WIDTH;
	    int minY = CAMERA_HEIGHT;
	    int maxY = (int) (camera.getHeight() - CAMERA_HEIGHT);
	    int rangeY = maxY - minY;
	    int y = rand.nextInt(rangeY) + minY;
	 
	    Sprite target = new Sprite(x, y, textures.get("cup"), getVertexBufferObjectManager());
	    scene.attachChild(target);
	 
	    int minDuration = 2;
	    int maxDuration = 4;
	    int rangeDuration = maxDuration - minDuration;
	    int actualDuration = rand.nextInt(rangeDuration) + minDuration;
	 
	    //MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(),
	      //  -target.getWidth());
	    //target.registerEntityModifier(mod.deepCopy());
	 
	    TargetsToBeAdded.add(target);
	 
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
		
		scene.attachChild(background_image);
		
		TargetsToBeAdded = new LinkedList();
		
		DrinkFarmLogic logic = new DrinkFarmLogic(this, scene, this.textures);
		logic.render(scene);
		this.runCycle(scene);
		
		return scene;
	}
}