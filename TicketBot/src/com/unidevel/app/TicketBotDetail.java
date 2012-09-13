package com.unidevel.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TicketBotDetail extends Activity implements TicketBotConstants {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticket_detail);
		
		TextView view = (TextView)findViewById(R.id.ticket_detail);
		Intent intent = getIntent();
		view.setText(intent.getStringExtra(EXTRA_INFO));
	}
}
