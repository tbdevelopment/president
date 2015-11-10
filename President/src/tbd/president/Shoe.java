package tbd.president;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shoe {

	private List<Card> cards = new ArrayList<Card>();
    private int cardIdx;
    Settings s;


    // C'tor
    public Shoe()
    {
    	s = Settings.getInstance();
        cardIdx = 0;
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

    public int getNumDecks(){return s.numDecks; }
    public void setNumDecks(int value)
    {
        if (s.numDecks != value)
        {
            s.numDecks = value;
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
        for(int d = 0; d < s.numDecks; d++) {
            for (Card.RankID r : Card.RankID.values()){
                if(r == Card.RankID.Joker){
                	if(s.jokersOn)
                	{
	        			cards.add(new Card(r,s.wildsOn));
	        			cards.add(new Card(r,s.wildsOn));
                	}
                }else{
        			for (Card.SuitID suit : Card.SuitID.values()){
        				cards.add(new Card(suit, r, s.wildsOn));
        			}
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
