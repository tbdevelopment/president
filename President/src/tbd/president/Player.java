package tbd.president;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;

public abstract class Player {
	
	public List<ImageView> cardImgs;
	public String name;
	public int cardsInHand;
	public List<Card> hand;
	public List<Card> lastHandPlayed;
	public enum PlayerType { human, computer };
	public enum RankTypes { President, Vice_Pres, Neutral, Vice_Scum, Scum };
	public RankTypes rank;
	public PlayerType type;
	public int index;
	public List<Card> receivedCards;
	public List<Card> discardedCards;
	        
	public Player(String n, PlayerType t, int i) 
	{
		name = n;
		rank = RankTypes.Neutral;
		index = i;
	    type = t;
		reset();
		receivedCards =  discardedCards = new ArrayList<Card>();
	}
	
	public void resetReceivedAndDiscarded(){
		receivedCards.clear();
		discardedCards.clear();
	}
	
	public void discardCards(int numCards, boolean high){
		List<Card> discard = new ArrayList<Card>();
		if(high){// discard highest cards
			for(int i = 0; i < numCards; ++i)
				discard.add(this.hand.get(this.hand.size()-1-i));
		}else {
			for(int i = 0; i < numCards; ++i)
				discard.add(this.hand.get(i));
		}
		this.discardedCards = discard;
		this.hand.removeAll(discard);		
	}
	
	public void addCards(List<Card> cards){
		this.receivedCards = cards;
		this.hand.addAll(cards);
	}
	
	public void greyOutAllCards() {
		for (ImageView card : this.cardImgs) {
			PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(0x33ff0000, PorterDuff.Mode.SRC_ATOP);
			card.setColorFilter(colorFilter);
			card.setEnabled(false);
		}
	}
	
	public void reset() {
	    cardsInHand = 0;
	    hand = new ArrayList<Card>();
	    lastHandPlayed = new ArrayList<Card>();
	    cardImgs = new ArrayList<ImageView>();
	}
	
	public boolean isHuman() {
		return this.type == PlayerType.human;
	}
	
	public boolean isComputer() {
		return this.type == PlayerType.computer;
	}
	
	public void addCardToHand(Card c){
		this.hand.add(c);
	}
	
	public int getNumCards() {
		return this.hand.size();
	}
	
	public List<Card> getHand() { return this.hand; }
	
	public Card getCardAtIndex(int index) {return this.hand.get(index);}
	
	public abstract List<Card> playHand();

}

