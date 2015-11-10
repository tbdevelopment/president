package com.example.president;

import java.util.Collections;
import java.util.Comparator;

public class SinglePlayer {

	public GameLogic gameLogic = new GameLogic();
	public String name;
	public int playerTurn;
	
	public SinglePlayer(int nd, int np, String nm) {
		gameLogic.numDecks = nd;
		gameLogic.numPlayers = np;
		this.name = nm;
		gameLogic.shoe = new Shoe(nd, gameLogic.jokersOn);
		
		// add one human player and computer players
		gameLogic.players.add(new Player(this.name));
		for (int i = 1; i < np; ++i)
			gameLogic.players.add(new Player("Computer " + i));
		deal();
	}
	
	public void deal() {
		while(gameLogic.shoe.getNumCards() != 0) {
			for (int i = 0; i < gameLogic.numPlayers; ++i) {
				try {
					gameLogic.players.get(i).addCardToHand(gameLogic.shoe.Draw());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		sortPlayersHands();
	}
	
	private void sortPlayersHands() {
		for (int i = 0; i < gameLogic.numPlayers; ++i) {
			Collections.sort(gameLogic.players.get(i).getHand(), new Comparator<Card>() {
				public int compare(Card lhs, Card rhs) {
					return lhs.Value - rhs.Value;
				}
		    });
		}
	}
	

}
