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
import android.graphics.Point;

import com.timewaste.timewaste.GameActivity;
import com.timewaste.utils.GamePad;

public class Labyrinth extends GameActivity {

	private Map<String, ITextureRegion> textures = new TreeMap<String, ITextureRegion>();

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

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
	}
	
	@Override
	public void onCreateResources() {
		super.onCreateResources();
		final Context context = this;
		try {
			ITexture face_box = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/labyrinth/face_box.png");
				}
			});
			ITexture grass = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/labyrinth/background_grass.png");
				}
			});
			ITexture wall = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return context.getAssets().open("gfx/labyrinth/snake_tailpart.png");
				}
			});
		
			face_box.load();
			grass.load();
			wall.load();
			
			textures.put("face_box", TextureRegionFactory.extractFromTexture(face_box));
			textures.put("grass", TextureRegionFactory.extractFromTexture(grass));
			textures.put("wall", TextureRegionFactory.extractFromTexture(wall));
		} catch (IOException e) {
			Debug.e(e);
		}
	}
	
	@Override
	public Scene onCreateScene() {
		super.onCreateScene();
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.52f, 0.71f, 0.44f));
		
		Maze maze = new Maze(22, 15);
		maze.recursive_division_maze_generation(1, maze.get_width(), 1, maze.get_length());
		final MovingObject moving_object = new MovingObject(maze);
		final Sprite[][] matrix = new Sprite[maze.get_width() + 1][maze.get_length() + 1];
		
		visualize_labyrinth(scene, matrix, moving_object);
		
		GamePad pad = new GamePad(this, scene, 0, CAMERA_HEIGHT/1.8f, CAMERA_WIDTH, CAMERA_HEIGHT) {
			public void action_left_arrow() {
				int x = moving_object.get_x();
				int y = moving_object.get_y();
				moving_object.move_left();
				update_labyrinth(x, y, moving_object.get_x(), moving_object.get_y(), matrix, scene, moving_object);
			}
			public void action_right_arrow() {
				int x = moving_object.get_x();
				int y = moving_object.get_y();
				moving_object.move_right();
				update_labyrinth(x, y, moving_object.get_x(), moving_object.get_y(), matrix, scene, moving_object);
			}
			public void action_up_arrow() {
				int x = moving_object.get_x();
				int y = moving_object.get_y();
				moving_object.move_up();
				update_labyrinth(x, y, moving_object.get_x(), moving_object.get_y(), matrix, scene, moving_object);
			}
			public void action_down_arrow() {
				int x = moving_object.get_x();
				int y = moving_object.get_y();
				moving_object.move_down();
				update_labyrinth(x, y, moving_object.get_x(), moving_object.get_y(), matrix, scene, moving_object);
			}
		};
		
		this.runCycle(scene);
		return scene;
	}
	
	private void update_labyrinth(int x, int y, int new_x, int new_y, Sprite[][] matrix, Scene scene, MovingObject moving_object) {
		if (x != new_x || y != new_y) {
			float width = CAMERA_WIDTH / moving_object.get_maze().get_width();
			float length = CAMERA_HEIGHT / moving_object.get_maze().get_length();

			matrix[x][y] = 	new Sprite((x -1)*width, (y - 1)*length, this.textures.get("grass"), this.getVertexBufferObjectManager());
			matrix[new_x][new_y] = 	new Sprite((new_x - 1)*width, (new_y - 1)*length, this.textures.get("face_box"), this.getVertexBufferObjectManager());
			scene.attachChild(matrix[x][y]);
			scene.attachChild(matrix[new_x][new_y]);

			if ((x - 1)*width < CAMERA_WIDTH/2 || (y - 1)*length < CAMERA_HEIGHT/2)
				matrix[x][y].setZIndex(1);
			if ((new_x - 1)*width < CAMERA_WIDTH/2 || (new_y - 1)*length < CAMERA_HEIGHT/2)
				matrix[new_x][new_y].setZIndex(1);

			matrix[x][y].setSize(width, length);
			matrix[new_x][new_y].setSize(width, length);
		}
		scene.sortChildren();
	}
	
	private void visualize_labyrinth(Scene scene, Sprite[][] matrix, MovingObject moving_object) {
		for (int j = 1; j <= moving_object.get_maze().get_length(); j++) {
			for (int i = 1; i <= moving_object.get_maze().get_width(); i++) {
				int status = moving_object.get_maze().game_maze.get(new Point(i, j));
				
				float width = CAMERA_WIDTH / moving_object.get_maze().get_width();
				float length = CAMERA_HEIGHT / moving_object.get_maze().get_length();

				if (status == 0)
					matrix[i][j] = new Sprite((i - 1)*width, (j - 1)*length, this.textures.get("grass"), this.getVertexBufferObjectManager());
				else if (status == 1)
					matrix[i][j] = new Sprite((i - 1)*width, (j - 1)*length, this.textures.get("wall"), this.getVertexBufferObjectManager());
				else if (status == 2)
					matrix[i][j] = new Sprite((i - 1)*width, (j - 1)*length, this.textures.get("face_box"), this.getVertexBufferObjectManager());
				else if (status == 3)
					matrix[i][j] = new Sprite((i - 1)*width, (j - 1)*length, this.textures.get("face_box"), this.getVertexBufferObjectManager());
				
				matrix[i][j].setSize(width, length);
				scene.attachChild(matrix[i][j]);
			}
		}
	}
	
}
