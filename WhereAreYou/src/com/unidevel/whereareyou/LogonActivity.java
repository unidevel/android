package com.unidevel.whereareyou;

import java.util.List;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMObjectResult;
import com.ibm.mobile.services.data.IBMQuery;
import com.ibm.mobile.services.data.IBMQueryResult;
import com.unidevel.BaseActivity;
import com.unidevel.whereareyou.model.User;

public class LogonActivity extends BaseActivity
{
	EditText userNameText;
	EditText passwordText;
	ProgressDialog progressDialog;
	Handler handler;
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.logon );
		this.handler = new Handler();
		this.userNameText = (EditText)this.findViewById( R.id.userName );
		this.passwordText = (EditText)this.findViewById( R.id.password );
		this.findViewById( R.id.btnLogon ).setOnClickListener( new OnClickListener(){
			public void onClick( View v )
			{
				String userName = userNameText.getText().toString();
				String password = passwordText.getText().toString();
				validateUser(userName, password); 
			}
		} );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		String user = pref.getString( "user", "NewUser" );
		String pass = pref.getString( "password", "" );
		this.userNameText.setText( user );
		this.passwordText.setText( pass );
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		String user = this.userNameText.getText().toString();
		String pass = this.passwordText.getText().toString();
		saveUser(user, pass);
	}
	
	public void validateUser(final String userName, final String password)
	{
		IBMQuery<User> query;
		try
		{
			progressDialog = ProgressDialog.show( this, "", "Logging in ...", true, false );
			query = IBMQuery.queryForClass( User.class );
			query.whereKeyEqualsTo( User.USERNAME, userName );
			query.findObjectsInBackground( new IBMQueryResult<User>(){
				@Override
				public void onError( IBMDataException except )
				{
					progressDialog.cancel();
				}
				
				@Override 
				public void onResult( List<User> users )
				{
					User user;
					if ( users != null && users.size() == 1)
					{
						progressDialog.dismiss();
						user = users.get( 0 );
						if ( password.equals( user.getPassword() ) )
						{
							setCurrentUser(user);
						}
						else
						{
							handler.post( new Runnable(){
								@Override
								public void run()
								{
									t("Password incorrect, please try again!");	
								}
							});
						}
					}
					else
					{
						handler.post( new Runnable(){
							public void run() {
								progressDialog.setMessage( "Registering as new user" );
								progressDialog.setTitle( "Registering" );								
							}
						});
						user = new User();
						user.setUserName( userName );
						user.setPassword( password );
						user.saveInBackground( new IBMObjectResult<User>(){

							@Override
							public void onError( IBMDataException error )
							{
								progressDialog.dismiss();
							}

							@Override
							public void onResult( User user )
							{
								progressDialog.dismiss();
								setCurrentUser( user );
							}
						});
					}
				}
			});
		}
		catch (IBMDataException e)
		{
			this.finish();
			progressDialog.dismiss();
			progressDialog = null;
			Log.e( "validateUser", e.getMessage(), e );
		}
	}
	
	public void setCurrentUser(User user)
	{
		BlueListApplication app = (BlueListApplication)getApplication();
		app.setCurrentUser( user );
		saveUser(user.getUserName(), user.getPassword());
		this.setResult( RESULT_OK );
		this.finish();
		Intent intent = new Intent( this, MapActivity.class );
		startActivity( intent );
	}
	
	public void saveUser(String user, String password)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		pref.edit().putString( "user", user ).putString( "password", password ).commit();
	}
}
