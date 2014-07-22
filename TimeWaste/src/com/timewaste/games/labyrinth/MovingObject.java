package com.timewaste.games.labyrinth;

import android.graphics.Point;

public class MovingObject {
	
	private int x;
	private int y;
	private Maze maze;
	
	public MovingObject(Maze maze) {
		this.x = 1;
		this.y = 1;
		maze.game_maze.put(new Point(1, 1), 2);
		maze.game_maze.put(new Point(maze.get_width(), maze.get_length()), 3);
		this.maze = maze;
	}
	
	public Maze get_maze() {
		return this.maze;
	}
	
	public int get_x() {
		return this.x;
	}
	
	public int get_y() {
		return this.y;
	}
	
	public void move_left() {
		if (this.x != 1 && this.maze.game_maze.get(new Point(this.x - 1, this.y)) != 1) {
			this.maze.game_maze.put(new Point(this.x, this.y), 0);
			this.x -= 1;
			this.maze.game_maze.put(new Point(this.x, this.y), 2);
		}
	}
	
	public void move_right() {
		if (this.x != this.maze.get_width() && this.maze.game_maze.get(new Point(this.x + 1, this.y)) != 1) {
			this.maze.game_maze.put(new Point(this.x, this.y), 0);
			this.x += 1;
			if (this.maze.game_maze.get(new Point(this.x, this.y)) != 3)
				this.maze.game_maze.put(new Point(this.x, this.y), 2);
		}
	}
	
	public void move_up() {
		if (this.y != 1 && this.maze.game_maze.get(new Point(this.x, this.y - 1)) != 1) {
			this.maze.game_maze.put(new Point(this.x, this.y), 0);
			this.y -= 1;
			this.maze.game_maze.put(new Point(this.x, this.y), 2);
		}
	}
	
	public void move_down() {
		if (this.y != this.maze.get_length() && this.maze.game_maze.get(new Point(this.x, this.y + 1)) != 1) {
			this.maze.game_maze.put(new Point(this.x, this.y), 0);
			this.y += 1;
			if (this.maze.game_maze.get(new Point(this.x, this.y)) != 3)
				this.maze.game_maze.put(new Point(this.x, this.y), 2);
		}
	}
}
