package tbd.president;

import java.util.List;

public class Human extends Player {

	public Human(String n, int i) {
		super(n, PlayerType.human, i);
	}

	@Override
	public List<Card> playHand() {
		return null;
	}
	
}
