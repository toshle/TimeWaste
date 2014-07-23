package com.timewaste.timewaste;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.timewaste.games.arcadeshooter.ArcadeShooter;
import com.timewaste.games.drinkfarm.DrinkFarm;
import com.timewaste.games.evade.Evade;
import com.timewaste.games.labyrinth.Labyrinth;
import com.timewaste.games.shitmageddon.Shitmageddon;
import com.timewaste.games.shoot.Shoot;
import com.timewaste.games.tictactoe.TicTacToe;

public class Categories {

	private Random randomizer = new Random();
	
	@SuppressWarnings("unchecked")
	private List<Class<?>>[] categories = new List[4];
	
	public Categories() {
		categories[0] = new ArrayList<Class<?>>();
		categories[1] = new ArrayList<Class<?>>();
		categories[2] = new ArrayList<Class<?>>();
		categories[3] = new ArrayList<Class<?>>();

		//categories[0].add(TicTacToe.class);
		categories[0].add(Labyrinth.class);
		categories[2].add(Shoot.class);
		categories[1].add(DrinkFarm.class);
		//categories[3].add(Evade.class);
		//categories[3].add(Shitmageddon.class);
		categories[3].add(ArcadeShooter.class);
	}
	
	public Class<?> selectGame(int category, Class<?> current) {
		Class<?> game = randomizeGame(category);
		while(game == current) {
			game = randomizeGame(category);
		}
		return game;
	}
	
	public int gameTime(int category) {
		int time;
		switch(category) {
			case 0:
				time = 30;
				break;
			case 1:
				time = 60;
				break;
			case 2:
				time = 120;
				break;
			default:
				time = -1;
				break;
		}
		return time;
	}
	
	private Class<?> randomizeGame(int category) {
		return categories[category].get(randomizer.nextInt(categories[category].size()));
	}
}
