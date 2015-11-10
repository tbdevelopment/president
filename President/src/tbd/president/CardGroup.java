package tbd.president;

import java.util.ArrayList;
import java.util.List;

public class CardGroup {
	public int playerIndex;
	public int numCards;
	public int value;
	public Card.RankID rank;
	public List<Card> cards = new ArrayList<Card>();
	
	public CardGroup(int v, Card.RankID r, List<Card> c){
		this.numCards = c.size();
		this.value = v;
		this.rank = r;
		this.cards = c;
	}
	
	public CardGroup(int p, int v, Card.RankID r, List<Card> c){
		this.playerIndex = p;
		this.numCards = c.size();
		this.value = v;
		this.rank = r;
		this.cards = c;
	}
	
	
	
	public void addCardToGroup(Card c){
		cards.add(c);
	}
	
	public CardGroup(){}
}
