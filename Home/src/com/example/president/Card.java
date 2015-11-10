package com.example.president;

public class Card {
	String[] cardValues = { "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Jack", "Queen", "King", "Ace", "Three", "Two", "Joker" };
	
	// enum of possible values for the card's suit
	//Taras test 1
    public enum SuitID
    {
        Clubs, Diamonds, Hearts, Spades
    };
    
 // enum of possible values for the card's rank
    public enum RankID
    {
        Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace, Three, Two, Joker
    };

    int populateCardValue()
    {
        for (int i = 0; i < cardValues.length; ++i)
        {
            if (Rank.toString() == cardValues[i])
                return i;
        }
        return -1;
    }
    
    public String toString() {
    	if(this.Rank != RankID.Joker)
    		return this.Rank.toString() + this.Suit.toString();
    	else 
    		return this.Rank.toString();
    }
    
 // C'tor which identifies which card this is

    public Card(SuitID s, RankID r)
    {
        Suit = s;
        Rank = r;
        if (r == RankID.Two)
            isPowerCard = true;
        else
        {
            isPowerCard = false;
        }
        if (r == RankID.Three)
            isWildCard = true;
        else
        {
            isWildCard = false;
        }
        Value = populateCardValue();
        Name = Rank.toString() + Suit.toString();
        Image = "Cards\\" + Rank.toString() + Suit.toString() + ".png";
    }

    public Card(RankID r)
    {
        Rank = r;
        isPowerCard = true;
        Value = populateCardValue();
        Name = Rank.toString();
        Image = "Cards\\" + Rank.toString() + ".png";
    }

    // Member variables and accessor methods
    public SuitID Suit;
    public RankID Rank;
    public String Name;
    public int Value;
    public String Image;
    public boolean isPowerCard;
    public boolean isWildCard;

}
