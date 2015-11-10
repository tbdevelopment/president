package com.example.president;

import java.util.ArrayList;

public class Player {
	public String name;
	public int cardsInHand;
	public ArrayList<Card> hand;
	        
	public Player(String n) 
	{
		name = n;
	    cardsInHand = 0;
	    hand = new ArrayList<Card>();
	}
	
	public void addCardToHand(Card c){
		this.hand.add(c);
	}
	
	public int getNumCards() {
		return this.hand.size();
	}
	
	public ArrayList<Card> getHand() { return this.hand; }
	
	public Card getCardAtIndex(int index) {return this.hand.get(index);}

}
