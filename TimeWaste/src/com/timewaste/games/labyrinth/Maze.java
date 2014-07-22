package com.timewaste.games.labyrinth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.graphics.Point;

public class Maze {
	
	public Map<Point, Integer> game_maze;
	private int width;
	private int length;

	public Maze(int width, int length) {
		this.width = width;
		this.length = length;
		this.game_maze = new HashMap<Point, Integer>();
		
		for (int i = 1; i <= length; i++)
			for (int j = 1; j <= width; j++)
				this.game_maze.put(new Point(j, i), 0);
	}
	
	public int get_width() {
		return this.width;
	}
	
	public int get_length() {
		return this.length;
	}
	
	private int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    
	    return randomNum;
	}
	
	public void recursive_division_maze_generation(int left, int right, int top, int bottom) {
		if (bottom - top <= 0 || right - left <= 0)
			return;
		
		if ((bottom-top) - (right-left) >= 0) {
            int y = randInt(top, bottom);
            while (y % 2 != 0)
            	y = randInt(top, bottom);
            for (int x = left; x <= right; x++)
                this.game_maze.put(new Point(x, y), 1);
            int path = randInt(left, right);
            while (path % 2 != 0)
            	path = randInt(left, right);
            this.game_maze.put(new Point(path, y), 0);
            this.recursive_division_maze_generation(left, right, top, y - 1);
            this.recursive_division_maze_generation(left, right, y + 1, bottom);
		}
		else {
			int x = randInt(left, right);
            while (x % 2 == 0)
            	x = randInt(left, right);
            for (int y = top; y <= bottom; y++)
                this.game_maze.put(new Point(x, y), 1);
            int path = randInt(top, bottom);
            while (path % 2 == 0)
            	path = randInt(top, bottom);
            this.game_maze.put(new Point(x, path), 0);
            this.recursive_division_maze_generation(left, x - 1, top, bottom);
            this.recursive_division_maze_generation(x + 1, right, top, bottom);
		}
	}
}
