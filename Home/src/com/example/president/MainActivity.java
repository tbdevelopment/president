package com.example.president;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	RelativeLayout layout;
	int leftOffset;
	int minBottomMargin;
	List<ImageView> cardImgs;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        //layout = new RelativeLayout(this);
        layout = (RelativeLayout)this.findViewById(R.id.mainLayout);
        layout.setBackgroundColor(Color.rgb(16, 142, 58));
        
        setContentView(layout);
        
        SinglePlayer game = new SinglePlayer(1,3,"Dave");
        int numCards = game.gameLogic.players.get(0).getNumCards();
        int midScreen = getWindowManager().getDefaultDisplay().getWidth()/2;
        leftOffset = midScreen - (numCards/2+1) * 25;
        minBottomMargin = 75;
        
        cardImgs = new ArrayList<ImageView>();
        List<Card> cards = game.gameLogic.players.get(0).getHand();
        for(Card c : cards)
        	drawCardToScreen(c.toString().toLowerCase());
        
        setMarginsOnBtns(midScreen);
        
        
    }
	
	private void setMarginsOnBtns(int midScreen) {
		Button btn = (Button)this.findViewById(R.id.playBtn);
        RelativeLayout.LayoutParams playParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        playParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        playParams.setMargins(midScreen+100, 0, 0, 0);
        btn.setLayoutParams(playParams);
        
        Button btn2 = (Button)this.findViewById(R.id.passBtn);
        RelativeLayout.LayoutParams passParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        passParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        passParams.setMargins(midScreen-100-btn2.getWidth(), 0, 0, 0);
        btn2.setLayoutParams(passParams);
	}
	
    
	private void drawCardToScreen(String cardName) {
    	ImageView image = new ImageView(this);
        image.setImageResource(getResources().getIdentifier(cardName, "drawable", getPackageName()));
        image.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(leftOffset,0,0,minBottomMargin);
        image.setLayoutParams(params);
        
        // set on touch listener
        image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectCard(v);
				
			}
        });
        
        layout.addView(image);
        cardImgs.add(image);
        leftOffset += 25;
    }

	private void selectCard(View card) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) card.getLayoutParams();
        if(params.bottomMargin > minBottomMargin)
        	params.setMargins(params.leftMargin, 0, 0, params.bottomMargin-20);
        else
        	params.setMargins(params.leftMargin, 0, 0, params.bottomMargin+20);
        card.setLayoutParams(params);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
