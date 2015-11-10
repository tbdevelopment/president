package com.example.president;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
	// Member variables
    private ArrayList<Card> cards = new ArrayList<Card>();
    private int numDecks = 6;
    private int cardIdx;
    public Deck()
    {
        cardIdx = 0;
        repopulate();
    }

    public Card Draw() throws Exception
    {
        if (cardIdx == cards.size())
            throw new Exception("The Deck is empty. Please reset.");
        Card card = cards.get(cardIdx++);
        System.out.println("Dealing the " + card.Name + ".");
        return card;
    }

    public void Shuffle()
    {
    	System.out.println("Shuffling the Deck.");
        randomizeCards();
    }

    public int getNumCards()
    {
         return cards.size() - cardIdx;
    }

    public int getNumDecks()
    {
        return numDecks;
    }
    
    public void setNumDecks(int decks)
    {
        if (numDecks != decks)
        {
            numDecks = decks;
            repopulate();
        }
    }

    // Helper methods

    private void repopulate()
    {
        // Remove "old" cards

        cards.clear();

        // Populate with new cards

        // For each deck
        for(int d = 0; d < numDecks; d++) {
        	for (Card.RankID r : Card.RankID.values())
            {
        		//int i = Card.RankID.Joker.compareTo(r);
        		if(r == Card.RankID.Joker)
        		{
        			cards.add(new Card(r));
        			cards.add(new Card(r));
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
