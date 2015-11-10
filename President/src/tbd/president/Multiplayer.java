package tbd.president;

import tbd.president.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Multiplayer extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);
        
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_multi, menu);
        return true;
    }
}
