package com.unidevel.andmaze;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.google.ads.*;

public class MainMenu extends Activity{
	private final Activity me = this;
	public static final String TAG = "TetrisBlast";
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Button easyBtn = (Button)findViewById(R.id.btn_main_menu_easy);
        Button normalBtn = (Button)findViewById(R.id.btn_main_menu_normal);
        Button hardBtn = (Button)findViewById(R.id.btn_main_menu_hard);
        Button exitBtn = (Button)findViewById(R.id.btn_main_menu_exit);
        //TextView verTxt = (TextView)findViewById(R.id.txt_main_menu_ver);
        //verTxt.setText(verTxt.getText().toString() + getString(R.string.ver_num));
        //Button Listenerses
        exitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				me.finish();
			}
		});
        
        easyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(MazeMap.EASY);
			}
		});
        
        normalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(MazeMap.NORMAL);
			}
		});
        
        hardBtn.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		startGame(MazeMap.HARD);
        	}
        });
		
		// a151086ad4201f9
		AdView adView = new AdView(this, AdSize.BANNER, "a151086ad4201f9");
		LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
		layout.addView(adView);
		AdRequest req = new AdRequest();
		adView.loadAd(req);
    }
    
    private void startGame(int level)
    {
    	Intent intent = new Intent(me, MazeActivity.class);
    	intent.putExtra("level", level);
		startActivity(intent);
    }
}
