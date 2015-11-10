package com.example.president;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameLogic {

	public ArrayList<Player> players = new ArrayList<Player>();
	public Shoe shoe;
	public int numDecks = 1;
	public int numPlayers = 4;
	public boolean scumLeads = true;
	public boolean wildsOn = true;
	public boolean jokersOn = true;
	public boolean powerCardEndsRound = true;
	public boolean discardOn = true;
	public boolean sequenceDiscardOn = true;
	public boolean autoPassOn= true;
    
	public ArrayList<Card> cards = new ArrayList<Card>();
    public ArrayList<Card> handPlayed = new ArrayList<Card>();
    public ArrayList<Card> cardsToPlay = new ArrayList<Card>();
    
    private int isBetterHand() { // TODO: add burns, and power cards
        if (handPlayed.size() > 0)
        {
            int prevNumCards = handPlayed.size();
            int prevHandValue = 11; // set default to wild
            for (Card c : handPlayed)
            {
                if (!c.isWildCard)
                {
                    prevHandValue = c.Value;
                    break;
                }
            }

            int curHandValue = 11;
            int curNumCards = cardsToPlay.size();
            for (Card c : cardsToPlay)
            {
                if (!c.isWildCard)
                {
                    curHandValue = c.Value;
                    break;
                }
            }
            if (curHandValue < prevHandValue && prevHandValue != 11)
                return 0;
            if (curHandValue == 13)     //joker
            {
                if (curNumCards > 1)
                    return 0;
                if (prevHandValue == 13)
                {
                    //burnHand();
                    return -1;
                }
                return 1;
            }
            else if (curHandValue == 12)    //two
            {  
                if( prevHandValue == 12 )
                {
                    if(curNumCards != prevNumCards)
                        return 0;
                    else
                    {
                        //burnHand();
                        return -1;
                    }                        
                } 
                else 
                {
                    if (prevNumCards == 1 && curNumCards == 1)
                        return 1;
                    else if (curNumCards != prevNumCards - 1)
                        return 0;
                    return 1;
                }

            }
            else if ((curHandValue == prevHandValue || curHandValue == 11 || (prevHandValue == 11 && curHandValue < 12)) && curNumCards == prevNumCards)
            {
                //burn
                //burnHand();
                return -1;
            }
            else if (curHandValue < prevHandValue || curNumCards != prevNumCards)
                return 0;
            
        }
        return 1;
    }

    private boolean isValidHand() 
    {
        if (cardsToPlay.size() == 0)
            return false;
        int curHandValue = 11;
        int curNumCards = cardsToPlay.size();
        for (Card c : cardsToPlay)
        {
            if (!c.isWildCard)
            {
                curHandValue = c.Value;
                break;
            }
        }
        if (handPlayed.size() == 0 && (curHandValue == 13 && curNumCards > 1))   // can't lay more than one joker
            return false;
        Collections.sort(cardsToPlay, new Comparator<Card>() {
			public int compare(Card lhs, Card rhs) {
				return lhs.Value - rhs.Value;
			}                   
        });
        //cardsToPlay.Sort((a, b) => a.Value.CompareTo(b.Value));
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
        return true;
    }
    
    public int determineFirstPlayer(){
    	for(int i = 0; i < this.players.size(); ++ i){
    		for(int j = 0; j < this.players.get(i).hand.size(); ++ j){
    			if(this.players.get(i).getCardAtIndex(j).Rank == Card.RankID.Four
    				&& this.players.get(i).getCardAtIndex(j).Suit == Card.SuitID.Spades)
    				return j;
    		}
    	}
    	return -1;
    }
}
