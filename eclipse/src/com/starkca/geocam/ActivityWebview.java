package com.starkca.geocam;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

public class ActivityWebview extends ActionBarActivity {

	private final String TAG = ActivityWebview.class.getCanonicalName();

	public static final String EXTRA_URL = "url";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
	
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			String url = extras.getString(EXTRA_URL);
			FragmentWebview myWVFrag = (FragmentWebview) getSupportFragmentManager().findFragmentById(R.id.wvFrag);
			if(myWVFrag != null && myWVFrag.isInLayout()) {
				myWVFrag.updateURL(url);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu");

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.geo_cam, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FragmentWebview myWVFrag = (FragmentWebview) getSupportFragmentManager()
					.findFragmentById(R.id.wvFrag);
			if (myWVFrag.backPressed()) {
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}
}
