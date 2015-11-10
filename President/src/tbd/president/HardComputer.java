package tbd.president;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

public class HardComputer extends Player {
	private GameLogic gameLogic;
	public HardComputer(String n, int i) {
		super(n,PlayerType.computer, i);
		gameLogic = GameLogic.getInstance();
	}

	@Override
	public List<Card> playHand() {
		List<Card> returnList = new ArrayList<Card>();
		int size = gameLogic.handsPlayedCurrentRound.size();
		CardGroup handPlayed = (size == 0) ? null : gameLogic.lastCardGroupPlayed();
		//get playable cards
		List<CardGroup> cardsPlayable = gameLogic.groupHand(gameLogic.getPlayableCards(super.index));
		
		Collections.sort(cardsPlayable, new Comparator<CardGroup>() {
			public int compare(CardGroup lhs, CardGroup rhs) {
				return lhs.value - rhs.value;
			}
	    });
		
		////////////////// logging /////////////////////
		String output = "";
		
		for(CardGroup c : cardsPlayable){
			for(Card d: c.cards)
				output += d.Rank.toString();
			output += ",";
		}
		Log.v("output", output);
		
		output = "";
		
		for(Card c : super.hand){
			output += c.Rank + ",";
		}
		Log.v("handplayed", output);
		////////////////////////////////////////////////
		
		if(cardsPlayable.size() > 0){
			// if new hand, lay lowest single that is not a wild or power card, if no singles, lay lowest multiple
			if(handPlayed == null){
				if(cardsPlayable.get(0).cards.get(0).Rank == Card.RankID.Joker)
					returnList.add(cardsPlayable.get(0).cards.get(0));
				else 
					returnList = cardsPlayable.get(0).cards;
			}
			else { 
				// two or joker was played
				if(handPlayed.rank == Card.RankID.Two || handPlayed.rank == Card.RankID.Joker){
					if(gameLogic.s.burnsOn && handPlayed.rank == Card.RankID.Two ){ 
						// try and burn
						for(CardGroup c : cardsPlayable){
							if(c.rank == Card.RankID.Two && c.numCards >= handPlayed.numCards){
								for(int i = 0; i < handPlayed.numCards; ++i)
									returnList.add(c.cards.get(i));
								break;
							}
						}
					} 
					// look for joker
					if(returnList.size() == 0){
						for(CardGroup c : cardsPlayable){
							if(c.rank == Card.RankID.Joker)
								returnList.add(c.cards.get(0));
						}
					}
					
				} else { // try to burn if not play higher card
					
					//look for number of wilds
					//int numWilds = 0;
					int wildIndex = 0;
					
//					Collections.sort(cardsPlayable, new Comparator<CardGroup>() {
//						public int compare(CardGroup lhs, CardGroup rhs) {
//							return lhs.value - rhs.value;
//						}
//				    });
					
					for( int i = 0; i < cardsPlayable.size(); ++i){
						if(gameLogic.s.wildsOn && cardsPlayable.get(i).rank == Card.RankID.Three){
							//numWilds = cardsPlayable.get(i).numCards;
							wildIndex = i;
							break;
						}
					}
					
					if(returnList.size() == 0){ // try and burn without wilds
						for(CardGroup c : cardsPlayable){
							if(c.value == handPlayed.value && c.numCards == handPlayed.numCards
								&& (c.rank != Card.RankID.Joker && c.rank != Card.RankID.Two)){
								for(int i = 0; i < handPlayed.numCards; ++i)
									returnList.add(c.cards.get(i));
								break;
							}
						}
					}
					if(returnList.size() == 0){ // try and burn with wilds
						for(CardGroup c : cardsPlayable){
							if(c.value == handPlayed.value && c.numCards < handPlayed.numCards
								&& (c.rank != Card.RankID.Joker && c.rank != Card.RankID.Two) && 
								(c.rank != Card.RankID.Three && gameLogic.s.wildsOn)){
									returnList = c.cards;
								int numCardsToAdd = handPlayed.numCards - returnList.size();
								if(numCardsToAdd > 0) {
									// add wilds to return list
									for(int i = 0; i < numCardsToAdd; ++i)
										returnList.add(cardsPlayable.get(wildIndex).cards.get(i));
								}
								break;
							}
						}	
						if(returnList.size() == 0){
							for(CardGroup c : cardsPlayable){
								if(c.numCards >= handPlayed.numCards && 
									(c.rank == Card.RankID.Three && gameLogic.s.wildsOn)){
									for(int i = 0; i < handPlayed.numCards; ++i)
										returnList.add(c.cards.get(i));
									break;
								}
							}	
						}
					}
					// try to burn, split multiple
					if(returnList.size() == 0) {
						for(CardGroup c : cardsPlayable){
							if(c.value == handPlayed.value && c.numCards > handPlayed.numCards
								&& (c.rank != Card.RankID.Joker && c.rank != Card.RankID.Two )){
								for(int i = 0; i < handPlayed.numCards; ++i)
									returnList.add(c.cards.get(i));
								break;
							}
						}
					}
					
					// look for higher card without wilds, same number of cards\
					if(returnList.size() == 0) {
						for(CardGroup c : cardsPlayable){
							if(c.value > handPlayed.value && c.numCards == handPlayed.numCards
								&& (c.rank != Card.RankID.Joker && c.rank != Card.RankID.Two)){
								for(int i = 0; i < handPlayed.numCards; ++i)
									returnList.add(c.cards.get(i));
								break;
							}
						}
					}
					
					// look for higher with wilds
					if(returnList.size() == 0) {
						for(CardGroup c : cardsPlayable){
							if(c.value > handPlayed.value && c.numCards < handPlayed.numCards
								&& (c.rank != Card.RankID.Joker && c.rank != Card.RankID.Two)){
								returnList = c.cards;
								int numCardsToAdd = handPlayed.numCards - returnList.size();
								if(numCardsToAdd > 0) {
									// add wilds to return list
									for(int i = 0; i < numCardsToAdd; ++i)
										returnList.add(cardsPlayable.get(wildIndex).cards.get(i));
								}
								break;
							}
						}
					}		
					
					// look for higher without wilds, split multiple
					if(returnList.size() == 0) {
						for(CardGroup c : cardsPlayable){
							if(c.value > handPlayed.value && c.numCards > handPlayed.numCards
								&& (c.rank != Card.RankID.Joker && c.rank != Card.RankID.Two )){
								for(int i = 0; i < handPlayed.numCards; ++i)
									returnList.add(c.cards.get(i));
								break;
							}
						}
					}
								
					
					if(returnList.size() == 0) { // try and play power card
						for(CardGroup c : cardsPlayable){
							if(c.rank == Card.RankID.Two && c.numCards >= (handPlayed.numCards -1)){
								if(handPlayed.numCards == 1)
									returnList.add(c.cards.get(0));
								else 
									for(int i = 0; i < handPlayed.numCards-1; ++i)
										returnList.add(c.cards.get(i));
								break;
							} else if(c.rank == Card.RankID.Joker){
								 returnList.add(c.cards.get(0));
								 break;
							}
						}
					}	
						
				}	
				
			}
		}

		output = "";
		for(Card d: returnList)
			output += d.Rank.toString();

		Log.v("handplayed", output + ", " + super.index);
		
		return returnList;
	}

}
