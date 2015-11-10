package tbd.president;

public class Settings {
	private static Settings instance;
	public int numDecks = 1;
	public int numPlayers = 5;
	public boolean scumLeads = true;
	public boolean wildsOn = true;
	public boolean jokersOn = true;
	public boolean powerCardEndsRound = true;
	public boolean discardOn = true;
	public boolean sequenceDiscardOn = true;
	public boolean autoPassOn= true;
	public boolean burnsOn = true;
	public int milliSecondsBetweenHands = 1000;
	public boolean easyOn = true;
	public boolean speedUpComps = true;
	
	private Settings(){} 
	public static synchronized Settings getInstance(){
		if (instance == null)
		{
			instance = new Settings();
		}
		return instance;
	}
}
