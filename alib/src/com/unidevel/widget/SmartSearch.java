package com.unidevel.widget;
import android.widget.*;
import android.content.*;
import android.util.*;
import com.unidevel.*;
import android.text.*;

public class SmartSearch extends LinearLayout implements TextWatcher
{
	public interface SearchListener
	{
		
	}
	
	EditText searchText;
	ImageButton searchButton;
	public SmartSearch(Context ctx, AttributeSet attrs)
	{
		super(ctx,attrs);
		this.searchText=(EditText) this.findViewById(R.id.searchText);
		this.searchButton= (ImageButton) this.findViewById(R.id.searchButton);

		this.searchText.addTextChangedListener(this);
	}
	
	@Override
	public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
	}

	@Override
	public void afterTextChanged(Editable p1)
	{
		// TODO: Implement this method
	}

}
