package com.unidevel.andmaze;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
        TextView verTxt = (TextView)findViewById(R.id.txt_main_menu_ver);
        verTxt.setText(verTxt.getText().toString() + getString(R.string.ver_num));
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
				startGame(15, 35);
			}
		});
        
        normalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startGame(20, 55);
			}
		});
        
        hardBtn.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		startGame(25, 65);
        	}
        });
    }
    
    private void startGame(int rows, int cols)
    {
    	Intent intent = new Intent(me, MazeActivity.class);
    	intent.putExtra("rows", rows);
    	intent.putExtra("cols", cols);
		startActivity(intent);
    }
}
