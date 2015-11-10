package tbd.president;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import android.util.Log;


public class SinglePlayer {
	public GameLogic gameLogic;
	public String name;
	Settings s;
	public int presIndex, viceIndex, viceScumIndex, scumIndex;
	
	public SinglePlayer(int nd, int np, String nm) {
		s = Settings.getInstance();
		gameLogic = GameLogic.getInstance();
		s.numDecks = nd;
		s.numPlayers = np;
		this.name = nm;
		gameLogic.shoe = new Shoe();	
		
		// add one human player and computer players
		gameLogic.players = new ArrayList<Player>();
		gameLogic.players.add(new Human(this.name, 0));
		gameLogic.playersInRound.put(0,true);
		gameLogic.playersInGame.put(0,true);
		for (int i = 1; i < s.numPlayers; ++i){
			if(s.easyOn)
				gameLogic.players.add(new EasyComputer("Computer " + i, i));
			else 
				gameLogic.players.add(new HardComputer("Computer " + i, i));
			gameLogic.playersInRound.put(i,true);
			gameLogic.playersInGame.put(i,true);
		}
		gameLogic.resetAll();
		deal();
		Log.v("difficulty", (s.easyOn)? "easy":"hard");
	}
	
	void reset() {
		gameLogic.shoe = new Shoe();
		// add one human player and computer players
		gameLogic.resetPlayersInGame();
		gameLogic.resetPlayersInRound();
		for (int i = 0; i < s.numPlayers; ++i)
			gameLogic.players.get(i).reset();
		gameLogic.handPlayed.clear();
		gameLogic.handsPlayedCurrentRound.clear();
		deal();
	}
	
	public void deal() {
		while(gameLogic.shoe.getNumCards() != 0) {
			for (int i = 0; i < s.numPlayers; ++i) {
				try {
					gameLogic.players.get(i).addCardToHand(gameLogic.shoe.Draw());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
		sortPlayersHands();
	}
	
	public void swapCards(){
		// look for president, vice pres, vice scum, scum
		presIndex = viceIndex = viceScumIndex = scumIndex = -1;	

		for(int i = 0; i < gameLogic.players.size(); ++i){
			switch(gameLogic.players.get(i).rank){
				case President:
					presIndex = i;
					break;
				case Scum:
					scumIndex = i;
					break;
				case Vice_Pres:
					viceIndex = i;
					break;
				case Vice_Scum:
					viceScumIndex = i;
					break;
				default:
					break;					
			}
		}
		
		// hack for three players and computer is pres
		if(gameLogic.players.size() == 3)
			viceIndex = 1;
		
		// swap the cards
		if(presIndex > 0 && viceIndex > 0){ //human is not pres or vice
			// cards to give to president
			gameLogic.players.get(scumIndex).discardCards((gameLogic.players.size() > 3)? 2:1, true);
			gameLogic.players.get(presIndex).discardCards((gameLogic.players.size() > 3)? 2:1, false);
			// swap cards
			gameLogic.players.get(scumIndex).addCards(gameLogic.players.get(presIndex).discardedCards);
			gameLogic.players.get(presIndex).addCards(gameLogic.players.get(scumIndex).discardedCards);
			
			if(gameLogic.players.size() > 3){
				gameLogic.players.get(viceScumIndex).discardCards(1, true);
				gameLogic.players.get(viceIndex).discardCards(1, false);
				// swap cards
				gameLogic.players.get(viceScumIndex).addCards(gameLogic.players.get(viceIndex).discardedCards);
				gameLogic.players.get(viceIndex).addCards(gameLogic.players.get(viceScumIndex).discardedCards);
			}			
		} else { // human is vice or pres
			// cards to give to president
			if(presIndex == 0){ // get highest cards from scum and add to humans hand
				gameLogic.players.get(scumIndex).discardCards((gameLogic.players.size() > 3)? 2:1, true);
				// swap cards
				gameLogic.players.get(presIndex).addCards(gameLogic.players.get(scumIndex).discardedCards);
				
				if(gameLogic.players.size() > 3){
					gameLogic.players.get(viceScumIndex).discardCards(1, true);
					gameLogic.players.get(viceIndex).discardCards(1, false);
					// swap cards
					gameLogic.players.get(viceScumIndex).addCards(gameLogic.players.get(viceIndex).discardedCards);
					gameLogic.players.get(viceIndex).addCards(gameLogic.players.get(viceScumIndex).discardedCards);
				}	
			} else if(viceIndex == 0){ // human is vice pres
				
				gameLogic.players.get(viceScumIndex).discardCards(1, true);
				// swap cards
				gameLogic.players.get(viceIndex).addCards(gameLogic.players.get(viceScumIndex).discardedCards);
				
				// cards to give to president
				gameLogic.players.get(scumIndex).discardCards(2, true);
				gameLogic.players.get(presIndex).discardCards(2, false);
				// swap cards
				gameLogic.players.get(scumIndex).addCards(gameLogic.players.get(presIndex).discardedCards);
				gameLogic.players.get(presIndex).addCards(gameLogic.players.get(scumIndex).discardedCards);				
			}	
		}
		sortPlayersHands();
	}
	
	private void sortPlayersHands() {
		for (int i = 0; i < s.numPlayers; ++i) {
			Collections.sort(gameLogic.players.get(i).getHand(), new Comparator<Card>() {
				public int compare(Card lhs, Card rhs) {
					return lhs.Value - rhs.Value;
				}
		    });
		}
	}

	
	
	public GameLogic.playType play(List<Card> cards) {
		return gameLogic.playHand(cards, gameLogic.playerTurn);
	}
}
