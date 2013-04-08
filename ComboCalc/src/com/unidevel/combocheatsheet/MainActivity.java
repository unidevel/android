package com.unidevel.combocheatsheet;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener, TextWatcher, OnItemSelectedListener {
	EditText editCost, editPaid, editPrice, editMarketPrice,editFirstReturn;
	Spinner spPeriod;
	TextView textTotal, textExtra, textReturn, textActual ;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        textTotal = (TextView)findViewById(R.id.textTotal);
        textReturn = (TextView)findViewById(R.id.textReturn);
        textExtra = (TextView)findViewById(R.id.textExtra);
        textActual = (TextView)findViewById(R.id.textActual);
        
        editCost = (EditText)findViewById(R.id.editCost);
        editPaid = (EditText)findViewById(R.id.editPaid);
        editPrice = (EditText)findViewById(R.id.editPrice);
        editMarketPrice = (EditText)findViewById(R.id.editMarketPrice);
        editFirstReturn = (EditText)findViewById(R.id.editFirstReturn);
        spPeriod = (Spinner)findViewById(R.id.spPeriod);
        spPeriod.setSelection(1);
        spPeriod.setOnItemSelectedListener(this);
        textTotal.setOnClickListener(this);
        editCost.setOnClickListener(this);
        editCost.addTextChangedListener(this);
        editMarketPrice.setOnClickListener(this);
        editMarketPrice.addTextChangedListener(this);
        textExtra.setOnClickListener(this);
        editPaid.setOnClickListener(this);
        editPaid.addTextChangedListener(this);
        editPrice.setOnClickListener(this);
        editPrice.addTextChangedListener(this);
        
        Button btnShare = (Button)findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent(Intent.ACTION_SEND);
			 	CheckBox checkBox = (CheckBox)findViewById(R.id.checkWorth);
			 	String worth = checkBox.isChecked()?getString(R.string.it_worth):getString(R.string.not_worth);
			     intent.setType("text/plain");
			     String subject = String.format(getString(R.string.fmtSubject));
			     String text = String.format(getString(R.string.fmtContent),
			    		 editCost.getText().toString(), 
			    		 editPaid.getText().toString(),
			    		 spPeriod.getSelectedItem().toString(), 			    		 
			    		 textTotal.getText().toString(),
			    		 worth
			     );
			     intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			     intent.putExtra(Intent.EXTRA_TEXT, text);
			     startActivity(Intent.createChooser(intent, getString(R.string.share)));
			}
		});
    }

    private double toDouble(String text){
    	try {
    		return Double.valueOf(text);
    	}
    	catch(Throwable ex){
    		
    	}
    	return 0.0;
    }

    public void recalc(){
		double cost = toDouble(editCost.getText().toString());
		double monthlyPaid = toDouble(editPaid.getText().toString());
		double price = toDouble(editPrice.getText().toString());
		double marketPrice = toDouble(editMarketPrice.getText().toString());
		double firstReturn = toDouble(editFirstReturn.getText().toString());
		int month = spPeriod.getSelectedItemPosition() * 12 + 12;
		if ( month > 60) month = 0;
		if ( month == 0 ) {
			return;
		}
		double total = price + ((double)month)*(monthlyPaid);
		int monthlyReturn = (int)(cost-firstReturn-price)/month;
		double extra = monthlyPaid - monthlyReturn;
		double actualPaid = (total - marketPrice)/month;
		DecimalFormat df = new DecimalFormat("##0.00");
		textTotal.setText(df.format(total));
		textReturn.setText(df.format(monthlyReturn));
		textExtra.setText(df.format(extra));
		textActual.setText(df.format(actualPaid));
//		double paid = (total - price) / month;
//		textReal.setText(df.format(paid));
    }
    
	@Override
	public void onClick(View view) {
		recalc();
	}

	@Override
	public void afterTextChanged(Editable text) {
	}

	@Override
	public void beforeTextChanged(CharSequence text, int start, int before,	int count) {
		
	}

	@Override
	public void onTextChanged(CharSequence text, int start, int before, int count) {
		recalc();
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
		recalc();		
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
		recalc();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor edit = pref.edit();
		edit.putString("editCost", editCost.getText().toString());
		edit.putString("editPaid", editPaid.getText().toString());
		edit.putString("editPrice", editPrice.getText().toString());
		edit.putString("editMarketPrice", editMarketPrice.getText().toString());
		edit.putInt("spPeriod", spPeriod.getSelectedItemPosition());
		edit.commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		editCost.setText(pref.getString("editCost", "0"));
		editPaid.setText(pref.getString("editPaid", "0"));
		editPrice.setText(pref.getString("editPrice", "0"));
		editMarketPrice.setText(pref.getString("editMarketPrice", "0"));
		spPeriod.setSelection(pref.getInt("spPeriod", 1));
		recalc();
	}
}
