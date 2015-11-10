package tbd.president;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SinglePlayerActivity extends Activity {
	RelativeLayout layout;
	int leftOffset;
	int minBottomMargin;
	List<ImageView> cardsPlayed;
	List<ImageView> cardImgsSelected;
	List<RelativeLayout> compLayouts;
	SinglePlayer game;
	int midScreenY;
	int midScreenX;
	boolean gameStarted;
	Settings settings;
	RelativeLayout humanLastPlayed;
	int heightOfCard;
	int widthOfCard;
	Dialog dialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
	    try {
	    	gameStarted = false;
			//layout = new RelativeLayout(this);
		    layout = (RelativeLayout)this.findViewById(R.id.singleLayout);
		   // layout.setBackgroundColor(Color.rgb(16, 142, 58));
		    settings = Settings.getInstance();
		    
		    setContentView(layout);
		    
		    game = new SinglePlayer(1,settings.numPlayers,"NoName");
		    game.gameLogic.firstGamePlayed = false;
		    int numCards = game.gameLogic.players.get(0).getNumCards();
		    midScreenX = getWindowManager().getDefaultDisplay().getWidth()/2;
		    midScreenY = getWindowManager().getDefaultDisplay().getHeight()/2;
		    heightOfCard = midScreenY/2;
		    widthOfCard = (int)((double)heightOfCard/1.36);
		    leftOffset = midScreenX - (numCards/2+1) * widthOfCard/3;
		    minBottomMargin = 75;
		    
		    cardImgsSelected = new ArrayList<ImageView>();
		    cardsPlayed 	 = new ArrayList<ImageView>();
		    compLayouts		 = new ArrayList<RelativeLayout>();
		    
		    Player human = game.gameLogic.players.get(0);
		    List<Card> cards = human.getHand(); 
		    for(Card c : cards)
		    	drawCardToScreen(c);
		    human.greyOutAllCards();
		    showComputerBoxes();			    
		    setMarginsOnBtns();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	@Override
	protected void onSaveInstanceState(Bundle president) {
	  super.onSaveInstanceState(president);
	  president.putBoolean("has_paused", true);
	}
	

	protected void showDialog(List<Card> received, List<Card> discarded) { 
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        //dialog.setTitle("Cards");
        Player human = game.gameLogic.players.get(0);
        
		List<ImageView> receivedCardImages = new ArrayList<ImageView>(Arrays.asList(
			(ImageView)dialog.findViewById(R.id.receivedCard1),
			(ImageView)dialog.findViewById(R.id.receivedCard2)
		));
		List<ImageView> discardedCardImages = new ArrayList<ImageView>(Arrays.asList(
				(ImageView)dialog.findViewById(R.id.discardCard1),
				(ImageView)dialog.findViewById(R.id.discardCard2)
			));
		Button okButton = (Button)dialog.findViewById(R.id.dialogOk);
		okButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				dialog.dismiss();
				Player human = game.gameLogic.players.get(0);
				if(human.rank == Player.RankTypes.President || human.rank == Player.RankTypes.Vice_Pres){
					// change start button to discard
					Button btn = (Button)layout.findViewById(R.id.startBtn);
					btn.setText("Discard");
					btn.setEnabled(true);
				} else {
					startGame();
				}
					
				
			}
		});
		for(int i = 0; i < received.size(); ++i){
			String cardName = received.get(i).toString().toLowerCase();    	
			receivedCardImages.get(i).setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
			receivedCardImages.get(i).setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

	        params.height = heightOfCard;
	        params.width =  widthOfCard;
	        receivedCardImages.get(i).setLayoutParams(params);
		}
		TextView textview;
		
		if(discarded == null || discarded.size() == 0) {
			 textview = (TextView)dialog.findViewById(R.id.textView2);
			 if(human.rank == Player.RankTypes.President){
				 int numCards = (game.gameLogic.players.size() >3 ) ? 2 : 1; 
				 textview.setText("Please select " + numCards + " card(s) to discard.");
			 } else if(human.rank == Player.RankTypes.Vice_Pres){
				 textview.setText("Please select 1 card to discard.");
			 }
		}		
		
		for(int i = 0; i < discarded.size(); ++i){
			String cardName = discarded.get(i).toString().toLowerCase();    	
			discardedCardImages.get(i).setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
			discardedCardImages.get(i).setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

	        params.height = heightOfCard;
	        params.width =  widthOfCard;	
	        discardedCardImages.get(i).setLayoutParams(params);
		}
		
		dialog.setCanceledOnTouchOutside(false);
        dialog.show();
	
}
	
	private void showComputerBoxes() {
		int widthOfBox =  midScreenX/2 - 30;
		int compPlayers = settings.numPlayers-1;
		int totalMargin = (compPlayers-1) * 15; 
		int marginLeft = ((midScreenX*2 - widthOfBox*compPlayers)-totalMargin)/2;

		for(Player p : game.gameLogic.players) {
			if(p.isComputer()) {
				RelativeLayout compBox = new RelativeLayout(this);
				compBox.setBackgroundResource(R.drawable.border);
				layout.addView(compBox);
				compBox.getLayoutParams().width = widthOfBox;
				compBox.getLayoutParams().height = midScreenY/2;
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) compBox.getLayoutParams();
				compBox.setPadding(0, 0, 8, 8);
				params.setMargins(marginLeft, 10, 0, 0);
				compBox.setLayoutParams(params);
				compLayouts.add(compBox);
				marginLeft += 15 + compBox.getLayoutParams().width;
			}
		}
		
		humanLastPlayed = new RelativeLayout(this);
		humanLastPlayed.setBackgroundResource(R.drawable.border);
		layout.addView(humanLastPlayed);
		humanLastPlayed.getLayoutParams().height = widthOfBox/2;
		humanLastPlayed.getLayoutParams().width = (int)((double)midScreenY/1.8);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) humanLastPlayed.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		humanLastPlayed.setLayoutParams(params);
		
	}
	
	private void showLastCardsPlayedComp() {
		int indexOffset = 1;
		for(RelativeLayout compBox : compLayouts) {
			compBox.removeAllViews();
			Player comp = game.gameLogic.players.get(indexOffset);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			TextView cover = new TextView(this);
			cover.setText("" + comp.hand.size());
			cover.setTypeface(null, Typeface.BOLD);
			cover.setGravity(Gravity.CENTER);
			cover.setBackgroundResource(R.drawable.cover);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			cover.setPadding(0, 0, 0, 0);
			params.setMargins(0, 10, 2, 0);
			cover.setLayoutParams(params);
			compBox.addView(cover);
			
			// show rank textview
			TextView rank = new TextView(this);
			rank.setText(comp.rank.toString().replace('_', ' '));
			params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			rank.setTypeface(null, Typeface.BOLD);
			rank.setPadding(0, 0, 0, 0);
			params.setMargins(7, 10, 2, 0);
			rank.setLayoutParams(params);
			compBox.addView(rank);
			
			int heightOfCards = (int)((double)compBox.getLayoutParams().height/2.25);			
			cover.getLayoutParams().width = heightOfCards;
			cover.getLayoutParams().height = (int)((double)heightOfCards/1.36);	// 1.36 is the ratio difference of the card between height and width
			cover.setTextSize((int)((double)heightOfCards/1.36)/2);
			int leftMargin = 15;
			int marginBetweenCards = compBox.getWidth()/14;
			if(game.gameLogic.playerInRound(comp.index) && comp.lastHandPlayed.size() > 0) {
				for(Card card : comp.lastHandPlayed) {
					String cardName = card.toString().toLowerCase();
			    	ImageView image = new ImageView(this);
			        image.setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
			        
			        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        image.setPadding(0, 0, 0, 0);
			        params.setMargins(leftMargin, 0, 0, 0);
			        image.setLayoutParams(params);
			        compBox.addView(image);	
			        image.getLayoutParams().height = heightOfCards;
			        image.getLayoutParams().width = (int)((double)heightOfCards/1.36);
			        leftMargin += marginBetweenCards;	
				}
			} else {
				if(!game.gameLogic.playerInRound(comp.index)) {
					TextView pass = new TextView(this);
					params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			        params.setMargins(leftMargin, 0, 0, 0);
			        if(!game.gameLogic.playerInGame(comp.index))
			        	pass.setText(comp.rank.toString().replace('_', ' ') + "!");
			        else
			        	pass.setText("Passed!");
			        pass.setTypeface(null, Typeface.BOLD);
			        pass.setGravity(Gravity.CENTER);
			        pass.setBackgroundResource(R.drawable.cover);
			        pass.setLayoutParams(params);
			        int passSize = heightOfCards;
			        pass.getLayoutParams().width = passSize*2;
			        pass.getLayoutParams().height = passSize;	// 1.36 is the ratio difference of the card between height and width
			        compBox.addView(pass);
				}
			}
			indexOffset++;
			
		}
	}
	
	private void showLastCardPlayedHuman() {
		RelativeLayout.LayoutParams params;
		int leftMargin = 0;
		int heightOfCards = (int)((double)humanLastPlayed.getLayoutParams().height/2.25);
        int marginBetweenCards = humanLastPlayed.getWidth()/14;
        Player human = game.gameLogic.players.get(0);
        
        humanLastPlayed.removeAllViews();
        
        // show rank textview
     	TextView rank = new TextView(this);
     	rank.setText(human.rank.toString().replace('_', ' '));
     	params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
     	rank.setTypeface(null, Typeface.BOLD);
     	rank.setPadding(0, 0, 0, 0);
     	params.setMargins(0, 10, 2, 0);
     	humanLastPlayed.addView(rank);
        
        if (game.gameLogic.playerInRound(human.index) && human.lastHandPlayed.size() > 0) {
			for(Card card : human.lastHandPlayed) {
				String cardName = card.toString().toLowerCase();
		    	ImageView image = new ImageView(this);
		        image.setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
		        
		        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		        image.setPadding(0, 0, 0, 0);
		        params.setMargins(leftMargin, 0, 0, 0);
		        image.setLayoutParams(params);
		        humanLastPlayed.addView(image);
		        
		        image.getLayoutParams().height = heightOfCards;
		        image.getLayoutParams().width = (int)((double)heightOfCards/1.36);
		        
		        leftMargin += marginBetweenCards;
			}
        } else {
        	if(!game.gameLogic.playerInRound(human.index)) {
				TextView pass = new TextView(this);
				params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		        params.setMargins(leftMargin, 0, 0, 0);
		        if(!game.gameLogic.playerInGame(human.index))
		        	pass.setText(human.rank.toString().replace('_', ' ') + "!");
		        else
		        	pass.setText("Passed!");
		        pass.setTypeface(null, Typeface.BOLD);
		        pass.setGravity(Gravity.CENTER);
		        pass.setBackgroundResource(R.drawable.cover);
		        pass.setLayoutParams(params);
		        int passSize = humanLastPlayed.getLayoutParams().width-10;
		        pass.getLayoutParams().width = passSize*2;
		        pass.getLayoutParams().height = passSize;	// 1.36 is the ratio difference of the card between height and width
		        humanLastPlayed.addView(pass);
			}
        }
	}
	
	public void greyOutNonPlayableCards() {
		List<Card> playable = game.gameLogic.getPlayableCards(0);
		Player human = game.gameLogic.players.get(0);
		int numPlayable = human.cardImgs.size();
		
		for (ImageView card : human.cardImgs) {			
			card.setColorFilter(null);
			card.setEnabled(true);
			if(!playable.contains(human.hand.get(human.cardImgs.indexOf(card)))) {
				PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(0x33ff0000, PorterDuff.Mode.SRC_ATOP);
				card.setColorFilter(colorFilter);
				card.setEnabled(false);
				numPlayable--;
			}
		}
		
		if(numPlayable < 1 && Settings.getInstance().autoPassOn) {
			findViewById(R.id.passBtn).performClick();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_single, menu);
        return true;
    }
	
	private void setBtnSize(Button b){
		int width = getWindowManager().getDefaultDisplay().getWidth()/5;
		int height = getWindowManager().getDefaultDisplay().getHeight()/9+10;
		
		if(height > 60)
			height = 60;
		if(width > 200)
			width = 200;
		b.setWidth(width);
        b.setHeight(height);
        b.setTextSize(height/4+3);

	}
	
	private void setMarginsOnBtns() {
		Button btn = (Button)this.findViewById(R.id.passBtn);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        params.setMargins(midScreenX, 0, 0, 5);
		btn.setLayoutParams(params);
        setBtnSize(btn);
        
        btn = (Button)this.findViewById(R.id.playBtn);
        params = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        params.setMargins(midScreenX-(midScreenX/2), 0, 0, 5);
        btn.setLayoutParams(params);
        setBtnSize(btn);
        
        btn = (Button)this.findViewById(R.id.startBtn);
        params = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        params.setMargins(midScreenX+(midScreenX/2), 0, 0, 5);
        btn.setLayoutParams(params);
        setBtnSize(btn);
        
        if(!gameStarted)
        	disablePlayerButtons();
	}
	
	public void startGame(){
		Button btn = (Button)this.findViewById(R.id.startBtn);
		btn.setEnabled(false);
		game.gameLogic.playerTurn = game.gameLogic.determineFirstPlayer();
		Log.v("numPlayer", game.gameLogic.players.size() + "," +game.gameLogic.playerTurn);
		Log.v("numPlayer", "" + game.gameLogic.players.get(game.gameLogic.playerTurn).type);
		if(game.gameLogic.getCurrentPlayer().isComputer()){
			Log.v("isComputer", "yes");
			playComputerTurns();
		}else{
			Log.v("isComputer", "no");
			enablePlayerButtons();
		}
	}
	
	public void restartGame(){
		Player human = game.gameLogic.players.get(0);
		for(ImageView v : human.cardImgs)
	    	layout.removeView(v);
		for(ImageView v : cardsPlayed)
			layout.removeView(v);
		
		game.reset();
	    int numCards = human.getNumCards();
	    midScreenX = getWindowManager().getDefaultDisplay().getWidth()/2;
	    midScreenY = getWindowManager().getDefaultDisplay().getHeight()/2;
	    heightOfCard = midScreenY/2;
	    widthOfCard = (int)((double)heightOfCard/1.36);
	    leftOffset = midScreenX - (numCards/2+1) * widthOfCard/3;
	    minBottomMargin = 75;	    
	    
	    human.cardImgs 	 = new ArrayList<ImageView>();
	    cardImgsSelected = new ArrayList<ImageView>();
	    cardsPlayed 	 = new ArrayList<ImageView>();
	    compLayouts		 = new ArrayList<RelativeLayout>();
	    
	    // reset swapped cards
	    for(Player p : game.gameLogic.players)
	    	p.resetReceivedAndDiscarded();
	    
	    // swap cards
	    game.swapCards();
	    List<Card> cards = human.getHand(); 
	    for(Card c : cards)
	    	drawCardToScreen(c);
	    showComputerBoxes();
	    if(human.rank == Player.RankTypes.Neutral)
	    	startGame();
	    else 
	    	showDialog(human.receivedCards, human.discardedCards);
	}
	
	public void redrawHumanHand(){
		game.gameLogic.sortHand(0);
		Player human = game.gameLogic.players.get(0);
	    int numCards = human.getNumCards();
	    midScreenX = getWindowManager().getDefaultDisplay().getWidth()/2;
	    midScreenY = getWindowManager().getDefaultDisplay().getHeight()/2;
	    heightOfCard = midScreenY/2;
	    widthOfCard = (int)((double)heightOfCard/1.36);
	    leftOffset = midScreenX - (numCards/2+1) * widthOfCard/3;
	    minBottomMargin = 75;
	    
	    for(ImageView v : human.cardImgs)
	    	layout.removeView(v);
	    
		human.cardImgs.clear();	       
	    List<Card> cards = human.getHand(); 
	    for(Card c : cards)
	    	drawCardToScreen(c);
	}
	
	
	public void showBurn() {
		Toast.makeText(getApplicationContext(), "New Round!", Toast.LENGTH_SHORT).show();
	}
	
	Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            String text = (String)msg.obj;
	            if(text == "disableAndUpdate") {
	            	game.gameLogic.setPlayerOut();
	            	game.gameLogic.firstGamePlayed = true;
	            	Toast.makeText(getApplicationContext(), "Game Over!", Toast.LENGTH_LONG).show();
				    updateCardPile();
					Button btn = (Button)layout.findViewById(R.id.startBtn);
					btn.setEnabled(true);
					btn.setText("Restart");
					disablePlayerButtons();
	            } else if(text == "showburn"){
	            	showBurn();
	            } else if(text == "greyOutAndEnable"){
					enablePlayerButtons();
	            } else {
	            	updateCardPile();
	            }
	            	
	        }
	};
	
	void playComputerTurns() {
		try {
			game.gameLogic.players.get(0).greyOutAllCards();
			Thread t = new Thread(new Runnable(){
				public void run() {
					Message msg;

					while(true) {
						try {
							if(game.gameLogic.playerInGame(0) || !Settings.getInstance().speedUpComps)
								Thread.sleep(Settings.getInstance().milliSecondsBetweenHands);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(game.gameLogic.singlePlayerInGame()){
							game.gameLogic.resetRound();
							msg = new Message();
							msg.obj = "disableAndUpdate";
							mHandler.sendMessage(msg);
							return;
						}
						
						boolean burn = false;
						if(game.gameLogic.players.get(game.gameLogic.playerTurn).isHuman()) {
							break;
						} else { // play turn
							List<Card> handToPlay = new ArrayList<Card>();
							// if comp plays a hand and everyone passes, this allows him to start the round again
							if(game.gameLogic.singlePlayerInRound()) {
								boolean canPlay = false;
								if(game.gameLogic.handsPlayedCurrentRound.size() > 0) { 
									// check if he's not the last player
									if(!game.gameLogic.isLastPlayer()) { // check to see that the last player is not this player
										// see if the current player can play on the last players cards
										handToPlay = game.gameLogic.generateComputerHand();
										if(handToPlay.size() != 0)// can play round will reset to his turn
											canPlay = true;
										else {
											game.gameLogic.revertToLastPlayer();
											game.gameLogic.resetRound();
											msg = new Message();
											msg.obj = "update";
											mHandler.sendMessage(msg);
											if(game.gameLogic.isOutOfCards()) {
												game.gameLogic.nextPlayer();
												// if next player is human, then return
												if(game.gameLogic.getCurrentPlayer().isHuman())
													break;
											} else if(game.gameLogic.getCurrentPlayer().isHuman())
													break;
										}
									} else // if he is the last player, reset round
										game.gameLogic.resetRound();
								}
								if(!canPlay) {
									game.gameLogic.resetRound();
								}
							}
							if(handToPlay.size() == 0) {
								handToPlay = game.gameLogic.generateComputerHand();
							}
							Log.v("compHandToPlay", "" + handToPlay);
							
							if(handToPlay.size() > 0) {
								GameLogic.playType type = game.play(handToPlay);
								if(type == GameLogic.playType.burn) {
									burn = true;
									if(game.gameLogic.playerInGame(0)){
										//showBurn();
										msg = new Message();
										msg.obj = "showburn";
										mHandler.sendMessage(msg);
									}
									game.gameLogic.resetRound();
								}
								
								if(game.gameLogic.isOutOfCards()) {
									burn = false;
									game.gameLogic.setPlayerOut();
								}
							} else { // pass
								game.gameLogic.clearLastHandPlayedForPlayer();
								game.gameLogic.removePlayerFromRound();
								
								logPlayersInRound();
								
								if(game.gameLogic.singlePlayerInRound()){ // check for single player in round
									game.gameLogic.revertToLastPlayer();
									game.gameLogic.resetRound();
									// clear the cards played in the middle
									msg = new Message();
									msg.obj = "update";
									mHandler.sendMessage(msg);
									if(game.gameLogic.isOutOfCards())
										game.gameLogic.nextPlayer();										
									else if(game.gameLogic.getCurrentPlayer().isHuman())
										break;
									burn = true;
								}
							}
						}
						//updateCardPile();
						msg = new Message();
						msg.obj = "update";
						mHandler.sendMessage(msg);
						if(!burn){
							game.gameLogic.nextPlayer();
						}
					}
					
					msg = new Message();
					msg.obj = "greyOutAndEnable";
					mHandler.sendMessage(msg);
					
				}
			});
			t.start();
			t = null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void logPlayersInRound() {
		String output = "";
		for(int i = 0; i < game.gameLogic.playersInRound.size(); i++) {
			output += game.gameLogic.playersInRound.get(i) + ", ";
		}
		Log.v("players", "" + output);
		Log.v("players", "" + game.gameLogic.singlePlayerInRound() + " in comupter turn");
	}
	
	private void drawCardToScreen(Card card) {
		String cardName = card.toString().toLowerCase();
    	ImageView image = new ImageView(this);
        image.setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
        image.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(leftOffset,0,0,minBottomMargin);
        params.height = heightOfCard;
        params.width =  widthOfCard;	
        image.setLayoutParams(params);
        
        // set on touch listener
        image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectCard(v);
			}
        });
        
        layout.addView(image);
        game.gameLogic.players.get(0).cardImgs.add(image);
        leftOffset += widthOfCard/3;
    }
	
	private void selectCard(View card) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) card.getLayoutParams();
		ImageView c = (ImageView)card;
        
        if(c.isEnabled()){
		    if(params.bottomMargin > minBottomMargin) {
		    	params.setMargins(params.leftMargin, 0, 0, params.bottomMargin-20);
		    	cardImgsSelected.remove((ImageView)card);
		    } else {
		    	params.setMargins(params.leftMargin, 0, 0, params.bottomMargin+20);
		    	cardImgsSelected.add((ImageView)card);
		    }
		    card.setLayoutParams(params);
        }
    }
	
	private void deSelectAllCards() {
		for(ImageView card : game.gameLogic.players.get(0).cardImgs) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) card.getLayoutParams();
			if(params.bottomMargin > minBottomMargin) {
	        	params.setMargins(params.leftMargin, 0, 0, params.bottomMargin-20);
	        	card.setLayoutParams(params);
	        	cardImgsSelected.remove((ImageView)card);
			}
		}
		
	}

	
	private void updateCardPile() {
		showLastCardsPlayedComp();
		showLastCardPlayedHuman();
		
		int numCards = game.gameLogic.handPlayed.size();
		int leftMargin = midScreenX - widthOfCard/3;
		if(numCards > 0)
			leftMargin -= numCards * widthOfCard/3;
		
		for (ImageView i : cardsPlayed)
			layout.removeView(i);
		cardsPlayed.clear();
		
		for (Card c : game.gameLogic.handPlayed)
		{
			String cardName = c.toString().toLowerCase();
	    	ImageView image = new ImageView(this);
	        image.setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	        params.setMargins(leftMargin,0,0,midScreenY-heightOfCard/4);
	        params.height = heightOfCard;
	        params.width = widthOfCard;
	        image.setLayoutParams(params);
	        leftMargin += widthOfCard/3;
	        		
	        layout.addView(image);
	        
	        cardsPlayed.add(image);
		}
	}
	
	void disablePlayerButtons() {
		Button btn = (Button)this.findViewById(R.id.passBtn);
		btn.setEnabled(false);
        btn = (Button)this.findViewById(R.id.playBtn);
        btn.setEnabled(false);
	}
	
	void enablePlayerButtons(){
		Button btn = (Button)this.findViewById(R.id.passBtn);
		btn.setEnabled(true);
        btn = (Button)this.findViewById(R.id.playBtn);
        btn.setEnabled(true);
        greyOutNonPlayableCards();
	}
	
	public void onStartResetClick(View v){
		Button btn = (Button)this.findViewById(v.getId());
		
		if(btn.getText().equals("Start")){
			startGame();
		} else if(btn.getText().equals("Discard")){
			Player human = game.gameLogic.players.get(0);
			List<ImageView> toRemove = new ArrayList<ImageView>();
			int numCards = (human.rank == Player.RankTypes.President && game.gameLogic.players.size() > 3) ? 2:1;
			if(cardImgsSelected.size() != numCards){
				Toast.makeText(getApplicationContext(), "Please select " + numCards +  " card(s) to discard.", Toast.LENGTH_SHORT).show();
				return;
			}
			else {
				List<Card> discarded = new ArrayList<Card>();
				for(ImageView card : cardImgsSelected)
				{
					int i = human.cardImgs.indexOf(card);
					discarded.add(human.hand.get(i));
					toRemove.add(human.cardImgs.get(i));
				}
				// remove all card imgs from screen
				for(ImageView v2 : toRemove) {
					layout.removeView(v2);
					human.cardImgs.remove(v2);
				}
				human.discardedCards = discarded;
				human.hand.removeAll(discarded);
				if(human.rank == Player.RankTypes.President)
					game.gameLogic.players.get(game.scumIndex).addCards(discarded);
				else if(human.rank == Player.RankTypes.Vice_Pres)
					game.gameLogic.players.get(game.viceScumIndex).addCards(discarded);				
				cardImgsSelected.clear();
				redrawHumanHand();
				startGame();
			}
		} else {
			restartGame();
			btn.setEnabled(false);
		}
	}

	public void onPlayPassClick(View v)
    {
		GameLogic.playType type = null;
		boolean pass = false;
		boolean burn = false;
		boolean valid = true;
    	try {
			switch(v.getId())
			{
			case R.id.playBtn:
				List<Card> playedCards = new ArrayList<Card>();
				Player human = game.gameLogic.players.get(0);
				List<Card> hand = human.getHand();
				List<ImageView> toRemove = new ArrayList<ImageView>();
				for(ImageView card : cardImgsSelected)
				{
					int i = human.cardImgs.indexOf(card);
					playedCards.add(hand.get(i));
					toRemove.add(human.cardImgs.get(i));
				}
				cardImgsSelected.clear();				
				
				if (playedCards.size() > 0)
					type = game.play(playedCards);
				else
					type = GameLogic.playType.invalid;
				switch(type)
				{
				case invalid:
					deSelectAllCards();
					valid = false;
					break;
				case burn:
					burn = true;
					showBurn();
					// remove all card imgs from screen						
					for(ImageView v2 : toRemove) {
						layout.removeView(v2);
						human.cardImgs.remove(v2);
					}
					
					Log.v("humanHandPlayed", "" + playedCards);
					
					updateCardPile();
					
					game.gameLogic.resetPlayersInRound();
					game.gameLogic.handsPlayedCurrentRound.clear();
					
					if(game.gameLogic.getCurrentPlayer().hand.size() == 0)
						disablePlayerButtons();
					else
						greyOutNonPlayableCards();
					
					break;
				case greater:
					// remove all card imgs from screen
					for(ImageView v2 : toRemove) {
						layout.removeView(v2);
						human.cardImgs.remove(v2);
					}
					
					Log.v("humanHandPlayed", "" + playedCards);
					
					disablePlayerButtons();
					updateCardPile();
					game.gameLogic.players.get(0).greyOutAllCards();
					break;					
				}
				
				break;
			case R.id.passBtn:
				deSelectAllCards();
				disablePlayerButtons();
				//game.play(new ArrayList<Card>());
				game.gameLogic.removePlayerFromRound();
				pass = true;
				
				
				String output = "";
				for(int i = 0; i < game.gameLogic.playersInRound.size(); i++){
					output += game.gameLogic.playersInRound.get(i) + ", ";
				}
				Log.v("players", "" + output);
				Log.v("players", "" + game.gameLogic.singlePlayerInRound() + " in human turn");
				break;
			}
			
			if(game.gameLogic.isOutOfCards()){
				burn = false;
				game.gameLogic.setPlayerOut();
			}
			
			if(valid && (!burn || pass)){
				game.gameLogic.nextPlayer();
				playComputerTurns();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("error", ""+e.getMessage());
		}
    }
}
