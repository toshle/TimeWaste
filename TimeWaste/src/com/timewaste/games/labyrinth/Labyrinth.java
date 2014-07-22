package com.timewaste.games.labyrinth;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

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

import com.timewaste.games.shoot.ShootLogic;
import com.timewaste.timewaste.GameActivity;
import com.timewaste.utils.GamePad;

public class Labyrinth extends GameActivity {

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
	}
	
	@Override
	public void onCreateResources() {
		final Context context = this;
		try {
			ITexture face_box = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/labyrinth/face_box.png");
				}
			});
		
			face_box.load();
			
			textures.put("face_box", TextureRegionFactory.extractFromTexture(face_box));
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.9f, 0.9f, 0.6f));
		final Sprite face_box = new Sprite(100, 100, this.textures.get("face_box"), this.getVertexBufferObjectManager());
		scene.attachChild(face_box);

		GamePad pad = new GamePad(this, scene, 0, CAMERA_HEIGHT/1.8f, CAMERA_WIDTH, CAMERA_HEIGHT) {
			public void action_left_arrow() {
				face_box.setPosition(200, 200);
			}
			public void action_right_arrow() {
				face_box.setPosition(100, 100);
			}
			public void action_up_arrow() {
				
			}
			public void action_down_arrow() {
				
			}
		};
		
		return scene;
	}
}
