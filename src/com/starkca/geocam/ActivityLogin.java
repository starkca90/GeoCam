package com.starkca.geocam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class ActivityLogin extends ActionBarActivity implements FragmentLogin.OnLoginListener {

	private final String TAG = ActivityLogin.class.getCanonicalName();
	
	private final String URL = "http://picture.jessestark.com";
	private final String URL_ARGUMENTS = "/token_login?";
	private final String ACCESS_TOKEN = "access_token=";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public void onLoginSuccess() {
		FragmentWebview fragment = (FragmentWebview) getSupportFragmentManager().findFragmentById(R.id.wvFrag);
		String url = URL + URL_ARGUMENTS + ACCESS_TOKEN + ApplicationController.getInstance().getAccessToken();
		if(fragment != null && fragment.isInLayout()) {
			fragment.updateURL(url);
		} else {
			Intent intent = new Intent(getApplicationContext(), ActivityWebview.class);
			intent.putExtra(ActivityWebview.EXTRA_URL, url);
			startActivity(intent);
		}
		
	}

	@Override
	public void onLoginFail() {
		// TODO Auto-generated method stub
		
	}
}
