package tbd.president;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ToggleButton;


public class SettingsActivity extends Activity{
	Settings s;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        s = Settings.getInstance();
        
//        Spinner decks 	 = (Spinner) this.findViewById(R.id.spinnerDecks);
        Spinner time 	 =  (Spinner) this.findViewById(R.id.spinnerTime);
        Spinner numCards =  (Spinner) this.findViewById(R.id.spinnerNumPlayers);
        Spinner difficulty =  (Spinner) this.findViewById(R.id.spinnerDifficulty);
        
//        ToggleButton b = (ToggleButton) findViewById(R.id.toggleDiscard);
//        b.setChecked(s.discardOn);
        ToggleButton b = (ToggleButton) findViewById(R.id.toggleJoker);
        b.setChecked(s.jokersOn);
        b = (ToggleButton) findViewById(R.id.toggleLeader);
        b.setChecked(s.scumLeads);
        b = (ToggleButton) findViewById(R.id.togglePass);
        b.setChecked(s.autoPassOn);
        b = (ToggleButton) findViewById(R.id.togglePower);
        b.setChecked(s.powerCardEndsRound);
        b = (ToggleButton) findViewById(R.id.toggleWild);
        b.setChecked(s.wildsOn);
//        b = (ToggleButton) findViewById(R.id.toggleSequence);
//        b.setChecked(s.sequenceDiscardOn);
        b = (ToggleButton) findViewById(R.id.toggleSpeedUp);
        b.setChecked(s.speedUpComps);
//        b = (ToggleButton) findViewById(R.id.toggleBurns);
//        b.setChecked(s.burnsOn);
        
        
        
        time.setSelection(s.milliSecondsBetweenHands/1000);
        
        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        		s.milliSecondsBetweenHands = pos*1000;
        	}
		    public void onNothingSelected(AdapterView<?> arg0) {
		      // Do nothing.
		    }
        });
        
        numCards.setSelection(s.numPlayers-3);
        
        numCards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        		s.numPlayers = pos+3;
        	}
		    public void onNothingSelected(AdapterView<?> arg0) {
		      // Do nothing.
		    }
        });
        
        difficulty.setSelection((s.easyOn)? 0 : 1);
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        		s.easyOn = (pos == 0)? true : false;
        	}
		    public void onNothingSelected(AdapterView<?> arg0) {
		      // Do nothing.
		    }
        });
        
//        decks.setSelection(s.numDecks-1);
//        
//        decks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                s.numDecks = pos+1;
//            }
//
//            public void onNothingSelected(AdapterView<?> arg0) {
//              // Do nothing.
//            }
//        });
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }
	
	public void onClick(View v)
    {
    	try {
    		ToggleButton b = (ToggleButton) findViewById(v.getId());
			switch(v.getId())
			{
//			case R.id.toggleDiscard:
				//s.discardOn = b.isChecked();
//				break;
			case R.id.toggleJoker:
				s.jokersOn = b.isChecked();
				break;
			case R.id.toggleLeader:
				s.scumLeads = b.isChecked();
				break;
			case R.id.togglePass:
				s.autoPassOn = b.isChecked();
				break;
//			case R.id.toggleSequence:
				//s.sequenceDiscardOn = b.isChecked();
//				break;
			case R.id.toggleWild:
				s.wildsOn = b.isChecked();
				break;
			case R.id.togglePower:
				s.powerCardEndsRound = b.isChecked();
				break;
			case R.id.toggleSpeedUp:
				s.speedUpComps = b.isChecked();
				break;
//			case R.id.toggleBurns:
//				s.burnsOn = b.isChecked();
//				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
