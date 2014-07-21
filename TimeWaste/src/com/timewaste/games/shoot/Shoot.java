package com.timewaste.games.shoot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import com.timewaste.timewaste.GameActivity;

public class Shoot extends GameActivity {

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	
	@Override
	public void onCreateResources() {
		try {
			ITexture background = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return getAssets().open("gfx/ticktacktoe/gif/tick_tack.gif");
				}
			});
			
			ITexture empty_texture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ticktacktoe/gif/empty.gif");
		        }
		    });
			ITexture tick_texture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ticktacktoe/gif/tick.gif");
		        }
		    });
			ITexture tack_texture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/ticktacktoe/gif/tack.gif");
		        }
		    });
			
			background.load();
		    empty_texture.load();
		    tick_texture.load();
		    tack_texture.load();

		    textures.put("empty", TextureRegionFactory.extractFromTexture(empty_texture));
		    textures.put("tick", TextureRegionFactory.extractFromTexture(tick_texture));
		    textures.put("tack", TextureRegionFactory.extractFromTexture(tack_texture));
			textures.put("background", TextureRegionFactory.extractFromTexture(background));
			
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.9f, 0.9f, 0.6f));

		/* Calculate the coordinates for the background, so its centered on the camera. */
		final float centerX = (CAMERA_WIDTH - this.textures.get("background").getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.textures.get("background").getHeight()) / 2;
		final float margin  = 35;

		/* Create the background and add it to the scene. */
		final Sprite ground_image = new Sprite(centerX, centerY + margin, this.textures.get("background"), this.getVertexBufferObjectManager()); 
		ground_image.setHeight(ground_image.getHeight() - margin * 2);
		
		scene.attachChild(ground_image);
		//TickTackToeGround ground = new TickTackToeGround(this, scene, this.textures);
		//ground.render(scene);
		
		return scene;
	}
}
