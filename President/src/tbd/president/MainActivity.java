package tbd.president;


import tbd.president.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	final String PREFS_NAME = "PresidentSettings";
	Settings s;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        s = Settings.getInstance();
        
        Button b = (Button)findViewById(R.id.multiButton);
        b.setEnabled(false);
        setBtnSizes();
        
        try {
			// Restore preferences
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//			s.autoPassOn = settings.getBoolean("autoPassOn", s.autoPassOn);
			s.burnsOn = settings.getBoolean("burnsOn", s.burnsOn);
//			s.discardOn = settings.getBoolean("discardOn", s.discardOn);
			s.jokersOn = settings.getBoolean("jokersOn", s.jokersOn);
			s.powerCardEndsRound = settings.getBoolean("powerCardEndsRound", s.powerCardEndsRound);
			s.scumLeads = settings.getBoolean("scumLeads", s.scumLeads);
//			s.sequenceDiscardOn = settings.getBoolean("sequenceDiscardOn", s.sequenceDiscardOn);
			s.wildsOn = settings.getBoolean("wildsOn", s.wildsOn);
			s.milliSecondsBetweenHands = settings.getInt("timeBtnHands", s.milliSecondsBetweenHands);
			s.numPlayers = settings.getInt("numPlayers", s.numPlayers);
			s.easyOn = settings.getBoolean("easyOn", s.easyOn);
//			s.numDecks = settings.getInt("numDecks", s.numDecks);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public void setBtnSizes() {
		int width = getWindowManager().getDefaultDisplay().getWidth()/4;
		int height = width/15;
		
		Button about = (Button)findViewById(R.id.aboutButton);
		Button settings = (Button)findViewById(R.id.settingsButton);
		Button single = (Button)findViewById(R.id.singleButton);
		Button multi = (Button)findViewById(R.id.multiButton);
		about.setWidth(width);
		settings.setWidth(width);
		single.setWidth(width);
		multi.setWidth(width);
		
		about.setMinimumHeight(height*5);
		settings.setMinimumHeight(height*5);
		single.setMinimumHeight(height*5);
		multi.setMinimumHeight(height*5);
		
		single.setTextSize(height);
		multi.setTextSize(height);
		about.setTextSize(height);
		settings.setTextSize(height);
		
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)single.getLayoutParams();
		params.setMargins(0, width/2, 0, 0);
		single.setLayoutParams(params);
		
		params = (RelativeLayout.LayoutParams)multi.getLayoutParams();
		params.setMargins(0, width/12, 0, 0);
		multi.setLayoutParams(params);
		
		params = (RelativeLayout.LayoutParams)about.getLayoutParams();
		params.setMargins(0, width/12, 0, 0);		
		about.setLayoutParams(params);
		
		params = (RelativeLayout.LayoutParams)settings.getLayoutParams();
		params.setMargins(0, width/12, 0, 0);
		settings.setLayoutParams(params);
	}
	
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
//      editor.putBoolean("autoPassOn", s.autoPassOn);
      editor.putBoolean("burnsOn", s.burnsOn);
//      editor.putBoolean("discardOn", s.discardOn);
      editor.putBoolean("jokersOn", s.jokersOn);
      editor.putBoolean("powerCardEndsRound", s.powerCardEndsRound);
      editor.putBoolean("scumLeads", s.scumLeads);
//      editor.putBoolean("sequenceDiscardOn", s.sequenceDiscardOn);
      editor.putBoolean("wildsOn", s.wildsOn);
      editor.putInt("timeBtnHands", s.milliSecondsBetweenHands);
      editor.putInt("numPlayers", s.numPlayers);
//      editor.putInt("numDecks", s.numDecks);
     editor.putBoolean("easyOn", s.easyOn);

      // Commit the edits!
      editor.commit();
    }
	
	public void onClick(View v)
    {
    	try {
			switch(v.getId())
			{
			case R.id.aboutButton:
//				Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
				Intent i = new Intent(getBaseContext(), About.class);
				startActivity(i);
				break;
			case R.id.settingsButton:
//				Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
				Intent j = new Intent(getBaseContext(), SettingsActivity.class);
				startActivity(j);
				break;
			case R.id.singleButton:
//				Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
	    		Intent k = new Intent(getBaseContext(), SinglePlayerActivity.class);
				startActivity(k);
				break;
			case R.id.multiButton:
//				Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_LONG).show();
//	    		Intent l = new Intent(getBaseContext(), Multiplayer.class);
//				startActivity(l);
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
