package com.timewaste.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.Context;

public abstract class GamePad {
	
	private Sprite left_arrow;
	private Sprite right_arrow;
	private Sprite up_arrow;
	private Sprite down_arrow;

	private Map<String, ITextureRegion> arrows = new TreeMap<String, ITextureRegion>();
	
	private void create_resources(SimpleBaseGameActivity game_instance, Scene scene) {

		final Context context = game_instance;
		
		try {
			ITexture left_arrow = new BitmapTexture(game_instance.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/arrows/left_arrow.gif");
				}
			});
			ITexture right_arrow = new BitmapTexture(game_instance.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/arrows/right_arrow.gif");
				}
			});
			ITexture up_arrow = new BitmapTexture(game_instance.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/arrows/up_arrow.gif");
				}
			});
			ITexture down_arrow = new BitmapTexture(game_instance.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/arrows/down_arrow.gif");
				}
			});
			
			left_arrow.load();
			right_arrow.load();
			up_arrow.load();
			down_arrow.load();
			
			arrows.put("left_arrow", TextureRegionFactory.extractFromTexture(left_arrow));
			arrows.put("right_arrow", TextureRegionFactory.extractFromTexture(right_arrow));
			arrows.put("up_arrow", TextureRegionFactory.extractFromTexture(up_arrow));
			arrows.put("down_arrow", TextureRegionFactory.extractFromTexture(down_arrow));

		} catch(IOException e) {
			Debug.e(e);
		}
	}

	private void attach_sprites(SimpleBaseGameActivity game_instance, Scene scene, float pX, float pY, int width, int height) {
		left_arrow = new Sprite(pX, pY + height/6.5f, this.arrows.get("left_arrow"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp())
					action_left_arrow();

				return true;
			}
		};
		right_arrow = new Sprite(pX + width/6.5f, pY + height/6.5f, this.arrows.get("right_arrow"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp())	
					action_right_arrow();

				return true;
			}
		};
		up_arrow = new Sprite(pX + width/11, pY, this.arrows.get("up_arrow"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp())
					action_up_arrow();

				return true;
			}
		};
		down_arrow = new Sprite(pX + width/11, pY + height/3.8f, this.arrows.get("down_arrow"), game_instance.getVertexBufferObjectManager()) {
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp())
					action_down_arrow();

				return true;
			}
		};
		
		scene.attachChild(left_arrow);
		scene.attachChild(right_arrow);
		scene.attachChild(up_arrow);
		scene.attachChild(down_arrow);
		
		scene.registerTouchArea(left_arrow);
		scene.registerTouchArea(right_arrow);
		scene.registerTouchArea(up_arrow);
		scene.registerTouchArea(down_arrow);
	}

	public GamePad(SimpleBaseGameActivity game_instance, Scene scene, float pX, float pY, int width, int height) {
		create_resources(game_instance, scene);
		attach_sprites(game_instance, scene, pX, pY, width, height);
	}
	
	public abstract void action_left_arrow();
	public abstract void action_right_arrow();
	public abstract void action_up_arrow();
	public abstract void action_down_arrow();
}
