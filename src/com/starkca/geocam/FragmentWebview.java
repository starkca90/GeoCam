package com.starkca.geocam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FragmentWebview extends Fragment {

	private final String TAG = FragmentWebview.class.getCanonicalName();

	private WebView myWebView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = inflater.inflate(R.layout.fragment_webview, container,
				false);

		myWebView = (WebView) view.findViewById(R.id.mainView);

		myWebView.getSettings().setJavaScriptEnabled(true);

		final Activity activity = getActivity();
		/*
		 * activity.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		 * myWebView.setWebChromeClient(new WebChromeClient() { public void
		 * onProgressChanged(WebView view, int progress) {
		 * activity.setProgress(progress * 1000); } });
		 */

		myWebView.setWebViewClient(new YourWebClient() {

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.i(TAG, "Strange");
				Toast.makeText(
						activity,
						"Oh no! " + description + " "
								+ Integer.toString(errorCode),
						Toast.LENGTH_SHORT).show();
			}
		});

		myWebView.loadUrl("http://picture.jessestark.com/pictures");
		return view;
	}

	public boolean backPressed() {
		if (myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		return false;
	}

	public void updateURL(String url) {
		myWebView.loadUrl(url);
	}

	private class YourWebClient extends WebViewClient {

		
		// you want to catch when an URL is going to be loaded
		public boolean shouldOverrideUrlLoading(WebView view,
				String urlConection) {
			 try {
				int response = new RetreiveServerResponse().execute(urlConection).get();
				Log.i(TAG, Integer.toString(response));
				switch(response) {
				case 200: 
					return false;
				case 401:
					// TODO Load Page
					Toast.makeText(getActivity(), "You Have Been logged Out", Toast.LENGTH_SHORT).show();
					return true;
				default:
					// TODO Error Page
					return true;
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 return true;
		}
		
		public class RetreiveServerResponse extends AsyncTask<String, Void, Integer> {

			Exception myException;
			
			@Override
			protected Integer doInBackground(String... urls) {
				try {
					URL url = new URL(urls[0]);
					URLConnection conexion = url.openConnection();
					conexion.setConnectTimeout(3000);
					conexion.connect();
					
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(urls[0]);
					HttpResponse response = httpClient.execute(httpGet);
					
					return response.getStatusLine().getStatusCode();
				} catch (Exception e) {
					myException = e;
					return null;
				}
			}
			
			protected void onPostExecute(Integer value) {
				
			}

		}
	}


}
