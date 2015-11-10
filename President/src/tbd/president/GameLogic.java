package tbd.president;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.util.Log;
import android.util.SparseBooleanArray;


public class GameLogic {

	private static GameLogic instance;
	public List<Player> players = new ArrayList<Player>();
	public Shoe shoe;
	Settings s;
	
	public enum playType { invalid, burn, greater };
    
	public List<Card> cards = new ArrayList<Card>();
    public List<Card> handPlayed = new ArrayList<Card>();
    public List<Card> cardsToPlay = new ArrayList<Card>();
    public List<CardGroup> handsPlayedCurrentRound = new ArrayList<CardGroup>();
    public SparseBooleanArray playersInRound = new SparseBooleanArray();
    public SparseBooleanArray playersInGame = new SparseBooleanArray();
    public int playerTurn;
    public boolean firstGamePlayed = false;
    
    private GameLogic(){
    	s = Settings.getInstance(); 
    	cardsToPlay = new ArrayList<Card>(); 
    } 
    
	public static synchronized GameLogic getInstance(){
		if (instance == null)
		{
			instance = new GameLogic();
		}
		return instance;
	}    
    
    public playType playHand(List<Card> cards, int playerIndex) {
    	cardsToPlay = cards;
    	playType type = isBetterHand();
    	if(type != playType.invalid) {
    		if(type == playType.burn) {
    			resetRound();
				for(Player p : players)
					p.lastHandPlayed.clear();
    		} else
    			handPlayed = cards;
    		// add hand to current round hand
    		List<Card> cardList = new ArrayList<Card>(cards);
    		CardGroup cg = new CardGroup(playerIndex ,getHandValue(cards), getHandRank(cards), cardList) ;
    		
    		this.players.get(playerIndex).lastHandPlayed = cards;
    		// determine value of hand
    		handsPlayedCurrentRound.add(cg);
    		this.players.get(playerIndex).hand.removeAll(cards);
    	}
    	    	
    	return type;
    }
    
    public Card.RankID getHandRank(List<Card> hand){
    	Card.RankID rank = Card.RankID.Three;
    	for (Card c : hand)
        {
            if (!c.isWildCard)
            {
                rank = c.Rank;
                break;
            }
        }
    	return rank;
    }
    
    public Player getCurrentPlayer() {
    	return players.get(playerTurn);
    }
    
    void resetPlayersInRound() {
    	for(int i = 0; i < players.size(); ++i)
    		playersInRound.put(i,playersInGame.get(i));
    }
    
    void resetPlayersInGame() {
    	for(int i = 0; i < playersInGame.size(); i++){
    		playersInGame.put(i, true);
    	}
    }
    
    public void clearLastHandPlayedForPlayer() {
    	players.get(playerTurn).lastHandPlayed.clear();
    }
    
    public void removePlayerFromRound() {
    	playersInRound.put(playerTurn, false);
    }
    
    boolean singlePlayerInRound() {
    	int numInRound = 0;
    	for(int i = 0; i < playersInRound.size(); i++){
    		if(playersInRound.get(i))
    			numInRound++;
    	}
    	return numInRound == 1 || numInRound == 0;
    }
    
    boolean singlePlayerInGame() {
    	int numInGame = 0;
    	for(int i = 0; i < playersInGame.size(); i++){
    		if(playersInGame.get(i))
    			numInGame++;
    	}
    	return numInGame == 1;
    }
    
    boolean playerInGame(int player) {
    	return playersInGame.get(player);
    }
    
    boolean playerInRound(int player) {
    	return playersInRound.get(player);
    }
    
    public int getHandValue(List<Card> hand) {
    	int curHandValue = 11;
    	if(s.wildsOn && !s.burnsOn)
    		curHandValue = 3;
        for (Card c : hand)
        {
            if (!c.isWildCard)
            {
                curHandValue = c.Value;
                break;
            }
        }
        return curHandValue;
    }
    
    public CardGroup lastCardGroupPlayed() {
    	CardGroup lastCards = new CardGroup();
    	for(CardGroup cg : handsPlayedCurrentRound) {
    		if(cg.cards.size() > 0)
    			lastCards = cg;
    	}
    	return lastCards;
    }
    
    public List<Card> lastCardsPlayed() {
    	List<Card> lastCards = new ArrayList<Card>();
    	for(CardGroup cg : handsPlayedCurrentRound) {
    		if(cg.cards.size() > 0)
    			lastCards = cg.cards;
    	}
    	return lastCards;
    }
    
    // returns -1 if burn
    private playType isBetterHand() { // TODO: add burns, and power cards
    	if(!isValidHand())
    		return playType.invalid;
    	List<Card> lastCardsPlayed = lastCardsPlayed();
    	
    	Card.RankID curHandRank = this.getHandRank(cardsToPlay);
    	
    	if(!s.burnsOn || s.powerCardEndsRound){
        	if(s.jokersOn){
        		if(curHandRank == Card.RankID.Joker)
        			return playType.burn;
        	}else if(curHandRank == Card.RankID.Two)
        		return playType.burn;
        }
    	
        if (lastCardsPlayed.size() > 0)
        {
            int prevHandValue = this.getHandValue(lastCardsPlayed);
            int curHandValue = this.getHandValue(this.cardsToPlay);
            Card.RankID prevHandRank = this.getHandRank(lastCardsPlayed);
            
            if(curHandValue == prevHandValue || (curHandRank == Card.RankID.Three && s.wildsOn))
            	return playType.burn;
            
            if(prevHandRank == Card.RankID.Three && s.wildsOn){
            	if(curHandRank != Card.RankID.Joker && curHandRank != Card.RankID.Two)
            		return playType.burn;
            }
        }
        return playType.greater;
    }

    private boolean isValidHand() 
    {
        if (cardsToPlay.size() == 0)
            return true;
        
        int size = this.handsPlayedCurrentRound.size();
		int prevNumCards = (size > 0)? this.lastCardGroupPlayed().numCards : -1;
		CardGroup lastHandPlayed = (size > 0)? this.lastCardGroupPlayed(): null;
        int prevHandValue = (lastHandPlayed != null)? lastHandPlayed.value : -1;
        Card.RankID prevHandRank = (lastHandPlayed != null)? lastHandPlayed.rank : Card.RankID.Three;
        
        int curHandValue = 11;
        int curNumCards = cardsToPlay.size();
        Card.RankID curHandRank = this.getHandRank(this.cardsToPlay);
        
        if(curHandRank == Card.RankID.Joker){
        	if(curNumCards == 1)
        		return true;
        	return false;
        }
        
        for (Card c : cardsToPlay)
        {
            if (!c.isWildCard)
            {
                curHandValue = c.Value;
                break;
            }
        }
        if (curHandRank == Card.RankID.Joker){
        	if(curNumCards != 1)   // can't lay more than one joker
        		return false;
        	return true;
        }
            
        Collections.sort(cardsToPlay, new Comparator<Card>() {
			public int compare(Card lhs, Card rhs) {
				return lhs.Value - rhs.Value;
			}                   
        });

        if( cardsToPlay.size() > 1 ) {
            for (Card c : cardsToPlay){
                if (!c.isWildCard)
                {
                    for (Card compare : cardsToPlay)
                    {
                        if (c.Rank != compare.Rank && !compare.isWildCard)
                            return false;
                    }
                }
                else   // is a wild card
                {
                    for (Card compare : cardsToPlay)
                    {
                        if (compare.isPowerCard)    // cannot play wild with power card
                            return false;
                    }
                }
            }
        }
        if(prevNumCards > 0){
	        if(prevHandRank == Card.RankID.Two){ // last hand played was two
	        	if(curHandValue < prevHandValue)
	        		return false;
	        	else if(curHandValue == prevHandValue && s.burnsOn){
	        		if(curNumCards == prevNumCards)
	        			return true;
	        	}  		
        		return false;
	        } else if(curHandRank == Card.RankID.Two){ // player is trying to play 2(s) on non power
	        	if(prevNumCards == 1){
	        		if(curNumCards != 1)
	        			return false;
	        		return true;
	        	} else if(curNumCards != prevNumCards-1)
	        		return false;
	        } else if(curNumCards != prevNumCards)
	        	return false;
        }
        	
        return true;
    }
    
    public int determineFirstPlayer(){
    	if(!firstGamePlayed){
	    	for(int i = 0; i < this.players.size(); ++ i){
	    		for(int j = 0; j < this.players.get(i).hand.size(); ++ j){
	    			if(this.players.get(i).getCardAtIndex(j).Rank == Card.RankID.Four
	    				&& this.players.get(i).getCardAtIndex(j).Suit == Card.SuitID.Spades && this.s.wildsOn)
	    				return i;
	    			else if(this.players.get(i).getCardAtIndex(j).Rank == Card.RankID.Three
	        				&& this.players.get(i).getCardAtIndex(j).Suit == Card.SuitID.Spades && !this.s.wildsOn)
	        				return i;
	    		}
	    	}
    	}else{
    		for(int i = 0; i < this.players.size(); ++ i){
    			if(this.players.get(i).rank == Player.RankTypes.Scum && s.scumLeads == true)
    				return i;
    			else if(this.players.get(i).rank == Player.RankTypes.President && s.scumLeads == false)
    				return i;
	    	}
    	}
    	return -1;
    }
    
	public List<Card> getPlayableCards(int playerIndex){
		List<Card> playable = new ArrayList<Card>();
		List<Card> currentHand = this.players.get(playerIndex).getHand();
		this.sortHand(playerIndex);
		int numWilds = 0;
		if(s.wildsOn)
			for(Card c : currentHand)
				if(c.Rank == Card.RankID.Three)
					numWilds++;
		
		// determine num cards and rank of hand played
		int size = this.handsPlayedCurrentRound.size();
		int cardsPlayed = (size > 0)? this.lastCardGroupPlayed().numCards : -1;
		CardGroup lastHandPlayed = (size > 0)? this.lastCardGroupPlayed() : null;
		
		
		if(size == 0)
			return currentHand;
		int playedHandValue = lastHandPlayed.value; // set default to wild
		Card.RankID playedHandRank = lastHandPlayed.rank;
	
        // if only wilds were played, make hand value = -1
		if(s.wildsOn && playedHandRank == Card.RankID.Three)
			playedHandValue = -1;

		// determine if player has any playable cards
		sortHand(playerIndex);
		
		int sameCardCount = 1;
		int currentSetValue = 0;
		Card.RankID currentSetRank = null;		
		boolean wildsPlayable = false;
		for(int i = 0; i < currentHand.size(); ++i){
			// check to see if card is same as previous
			if(i == 0){
				currentSetValue = currentHand.get(i).Value;
				currentSetRank = currentHand.get(i).Rank;
			}else if(currentHand.get(i).Rank == currentHand.get(i-1).Rank) {
				sameCardCount++;
				currentSetValue = currentHand.get(i).Value;
				currentSetRank = currentHand.get(i).Rank;
			}
			else { // card is not the same as previous
				// check if previous set is valid
				if(s.burnsOn){
					if(playedHandRank == Card.RankID.Joker){
						if(currentSetRank == Card.RankID.Joker){
							//add cards to deck
							for(int j = i-sameCardCount; j < i; j++)
								playable.add(currentHand.get(j));
						}	
					} else if(playedHandRank == Card.RankID.Two){
						if(sameCardCount >= cardsPlayed && currentSetRank == Card.RankID.Two){
							//add cards to deck
							for(int j = i-sameCardCount; j < i; j++)
								playable.add(currentHand.get(j));
						}	
					} else if(s.wildsOn && currentSetRank == Card.RankID.Three) {
						if(sameCardCount >= cardsPlayed || wildsPlayable)
							for(int j = i-sameCardCount; j < i; j++)
								playable.add(currentHand.get(j));
					} else if(currentSetRank == Card.RankID.Two) { 
						if ( sameCardCount >= (cardsPlayed - 1)){
							for(int j = i-sameCardCount; j < i; j++)
								playable.add(currentHand.get(j));
						}
					} else if(sameCardCount + numWilds >= cardsPlayed && currentSetValue >= playedHandValue){
						for(int j = i-sameCardCount; j < i; j++){
							playable.add(currentHand.get(j));
							wildsPlayable = true;
						}
					}
				} else {
					if(currentSetRank == Card.RankID.Two) { 
						if (sameCardCount >= (cardsPlayed - 1)){
							for(int j = i-sameCardCount; j < i; j++)
								playable.add(currentHand.get(j));
						}
					} else if(s.wildsOn && currentSetRank == Card.RankID.Three) {
						if(wildsPlayable)
							for(int j = currentHand.size()-sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
					} else if(sameCardCount + numWilds >= cardsPlayed && currentSetValue > playedHandValue){
						for(int j = i-sameCardCount; j < i; j++){
							playable.add(currentHand.get(j));
							wildsPlayable = true;
						}
					}
				}
				currentSetValue = currentHand.get(i).Value;
				currentSetRank = currentHand.get(i).Rank;
				sameCardCount = 1;
			}
			// last card
			if(i == currentHand.size() - 1){
				if(currentSetRank == Card.RankID.Joker){
					for(int j = currentHand.size()-sameCardCount; j < currentHand.size(); j++)
						playable.add(currentHand.get(j));
				} else if(s.burnsOn){
					if(playedHandRank == Card.RankID.Joker){
						if(currentSetRank == Card.RankID.Joker){
							//add cards to deck
							for(int j = currentHand.size() - sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
						}	
					} else if(playedHandRank == Card.RankID.Two){
						if(sameCardCount >= cardsPlayed && currentSetRank == Card.RankID.Two){
							for(int j = currentHand.size() - sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
						}	
					} else if(s.wildsOn && currentSetRank == Card.RankID.Three) {
						if(sameCardCount >= cardsPlayed || wildsPlayable)
							for(int j = currentHand.size()-sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
					} else if(currentSetRank == Card.RankID.Two) { 
						if(sameCardCount >= (cardsPlayed - 1)){
							for(int j = currentHand.size() - sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
						}
					} else if(sameCardCount + numWilds >= cardsPlayed && currentSetValue >= playedHandValue){
						for(int j = currentHand.size() - sameCardCount; j < currentHand.size(); j++)
							playable.add(currentHand.get(j));
					} 
				} else {
					if(playedHandRank == Card.RankID.Joker){
						if(currentSetRank == Card.RankID.Joker){
							//add cards to deck
							for(int j = currentHand.size() - sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
						}	
					}else if(currentSetRank == Card.RankID.Two) { 
						if (sameCardCount >= (cardsPlayed - 1)){
							for(int j = i-sameCardCount; j < i; j++)
								playable.add(currentHand.get(j));
						}
					} else if(s.wildsOn && currentSetRank == Card.RankID.Three) {
						if(wildsPlayable)
							for(int j = currentHand.size()-sameCardCount; j < currentHand.size(); j++)
								playable.add(currentHand.get(j));
					} else if(sameCardCount + numWilds >= cardsPlayed && currentSetValue > playedHandValue){
						wildsPlayable = true;
						for(int j = currentHand.size() - sameCardCount; j < currentHand.size(); j++)
							playable.add(currentHand.get(j));
					}
				}
			}
		}

		return playable;
	}
	
	public List<Card> generateComputerHand() {
		return players.get(playerTurn).playHand();
	}
	
	public void revertToLastPlayer() {
		playerTurn = this.lastCardGroupPlayed().playerIndex;
	}
	
	public void resetRound() {
		resetPlayersInRound();
		handsPlayedCurrentRound.clear();
		handPlayed.clear();
	}
	
	public void resetAll(){
		this.handsPlayedCurrentRound.clear();
		this.handPlayed.clear();
		this.resetRound();
	}
	public boolean isLastPlayer() {
		return this.lastCardGroupPlayed().playerIndex == playerTurn;
	}
	
	public List<Card> getNonPlayableCards(int playerIndex){
		List<Card> currentHand = this.players.get(playerIndex).getHand();
		List<Card> playableCards = this.getPlayableCards(playerIndex);
		if(playableCards.size() == currentHand.size())
			return new ArrayList<Card>();
		for(int i = 0; i < currentHand.size(); i++){
			for(int j = 0; j < playableCards.size(); ++j){
				if(currentHand.get(i) == playableCards.get(j)){
					currentHand.remove(i);
					playableCards.remove(j);
					i--;
					j--;
				}
			}
		}
		
		return currentHand;
	}
	
	public List<CardGroup> groupHand(List<Card> cardsToGroup){
		List<CardGroup> cardGroups = new ArrayList<CardGroup>();
		List<Card> cardsInGroup = new ArrayList<Card>();
		List<Card> currentHand = cardsToGroup;
		int currentSetValue = 0;
		Card.RankID currentSetRank = null;
		
		Collections.sort(currentHand, new Comparator<Card>() {
			public int compare(Card lhs, Card rhs) {
				return lhs.Value - rhs.Value;
			}
	    });
		
		// group cards
		for(int i = 0; i < currentHand.size(); ++i){
			
			// check to see if card is same as previous
			if(i == 0){
				currentSetValue = currentHand.get(i).Value;
				currentSetRank = currentHand.get(i).Rank;
				cardsInGroup.add(currentHand.get(i));
			}else if(currentHand.get(i).Rank == currentHand.get(i-1).Rank) {
				cardsInGroup.add(currentHand.get(i));
				currentSetValue = currentHand.get(i).Value;
				currentSetRank = currentHand.get(i).Rank;				
			}
			else { // card is not the same as previous
				cardGroups.add(new CardGroup(currentSetValue, currentSetRank,cardsInGroup));
				cardsInGroup = new ArrayList<Card>();
				cardsInGroup.add(currentHand.get(i));
				currentSetValue = currentHand.get(i).Value;
				currentSetRank = currentHand.get(i).Rank;
			}
			
			if(i == currentHand.size() - 1) // 
				cardGroups.add(new CardGroup(currentSetValue, currentSetRank,cardsInGroup));
		}
		return cardGroups;
	}
	
	public void sortHand(int playerIndex) {
		Collections.sort(this.players.get(playerIndex).getHand(), new Comparator<Card>() {
			public int compare(Card lhs, Card rhs) {
				return lhs.Value - rhs.Value;
			}
	    });
	}
	
	public void nextPlayer(){
		// if its the last players turn
		if(playerTurn == players.size()-1)
			playerTurn = 0;	
		else
			playerTurn++;
		
		while(!playersInRound.get(playerTurn)){
			if(playerTurn == players.size()-1)
				playerTurn = 0;
			else
				playerTurn++;
		}
		Log.v("nextplayerturn", "" + playerTurn);
	}
	
	public boolean isOutOfCards(){
		return players.get(playerTurn).hand.size() == 0;
	}
	
	public void setPlayerOut(){
		
		int numOut = 0;
		for(Player p : players)
			if(!playerInGame(p.index))
					numOut++;
		
		int numPlayers = players.size();
		switch(numOut) {
			case 0:
				players.get(playerTurn).rank = Player.RankTypes.President;
				break;
			case 1:
				if(numPlayers == 3)
					players.get(playerTurn).rank = Player.RankTypes.Neutral;
				else
					players.get(playerTurn).rank = Player.RankTypes.Vice_Pres;
				break;
			case 2:
				if(numPlayers == 3)
					players.get(playerTurn).rank = Player.RankTypes.Scum;
				else if(numPlayers == 4)
					players.get(playerTurn).rank = Player.RankTypes.Vice_Scum;
				else
					players.get(playerTurn).rank = Player.RankTypes.Neutral;
				break;
			case 3:
				if(numPlayers == 4)
					players.get(playerTurn).rank = Player.RankTypes.Scum;
				else
					players.get(playerTurn).rank = Player.RankTypes.Vice_Scum;
				break;
			case 4:
				players.get(playerTurn).rank = Player.RankTypes.Scum;
				break;
		}
		
		// messing with the card pile when they go out...
		//players.get(playerTurn).lastHandPlayed.clear();
		playersInGame.put(playerTurn, false);
		playersInRound.put(playerTurn, false);
	}
}
