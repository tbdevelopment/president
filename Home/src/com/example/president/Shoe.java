package com.example.president;

import java.util.ArrayList;
import java.util.Random;

public class Shoe {

	private ArrayList<Card> cards = new ArrayList<Card>();
    private int numDecks = 6;
    private int cardIdx;
    private boolean hasJokers;
    //private Dictionary<int, ICallback> clientCallbacks = new Dictionary<int, ICallback>();
    //private int nextCallbackId = 1;

    // C'tor
    public Shoe(int nd, boolean jokers)
    {
        cardIdx = 0;
        this.numDecks = nd;
        this.hasJokers = jokers;
        repopulate();
    }
    
    public Card Draw() throws Exception
    {
        if (cardIdx == cards.size())
            throw new Exception("The shoe is empty. Please reset.");

        Card card = cards.get(cardIdx++);

        return card;
    }

    public void Shuffle()
    {
        randomizeCards();
    }

    public int getNumCards()
    {
        return cards.size() - cardIdx; 
    }

    public int getNumDecks(){return numDecks; }
    public void setNumDecks(int value)
    {
        if (numDecks != value)
        {
            numDecks = value;
            repopulate();
        }
    }

    // Helper methods

    private void repopulate()
    {
        // Remove "old" cards
        cards = new ArrayList<Card>();

        // Populate with new cards

        // For each deck
        for(int d = 0; d < numDecks; d++) {
            for (Card.RankID r : Card.RankID.values())
            {
                
                if(r == Card.RankID.Joker)
        		{
                	if(this.hasJokers){
	        			cards.add(new Card(r));
	        			cards.add(new Card(r));
                	}
        		}
        		else
                {
        			for (Card.SuitID s : Card.SuitID.values())
        				cards.add(new Card(s, r));
                }
            }
        }

        // Shuffle the cards
        randomizeCards();
    }

    private void randomizeCards()
    {
        Random rand = new Random();
        Card hold;
        int randIndex;

        for (int i = 0; i < cards.size(); i++)
        {
            // Choose a random index 
            randIndex = rand.nextInt(cards.size());

            if (randIndex != i)
            {
                // Swap elements at indexes i and randIndex
                hold = cards.get(i);
                cards.set(i, cards.get(randIndex));
                cards.set(randIndex, hold);
            }
        }

        // Start dealing off the top of the deck
        cardIdx = 0;
    }
}
