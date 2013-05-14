package com.unidevel.mibox.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AboutActivity extends Activity
{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		AdView adView = new AdView( this, AdSize.BANNER, "a1518d142014149" ); //$NON-NLS-1$
		LinearLayout layout = (LinearLayout)findViewById( R.id.adLayout );
		layout.addView( adView );
		AdRequest req = new AdRequest();
		adView.loadAd( req );
		TextView text = (TextView)findViewById( R.id.labelAbout );

		findViewById( R.id.btnBack ).setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				AboutActivity.this.finish();
			}
		} );
	}
}
