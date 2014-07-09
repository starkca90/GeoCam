package com.starkca.geocam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

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
	
	private final String URL = "http://picture.jessestark.com/apps/";
	
	private final String GRANT_TYPE_REFRESH = "refresh_token";
	private final String ACCESS_TOKEN = "access_token";
	private final String ERROR_MESSAGE = "error_description";
	
	private Object tryAgain;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = inflater.inflate(R.layout.fragment_webview, container,
				false);

		myWebView = (WebView) view.findViewById(R.id.mainView);

		myWebView.getSettings().setJavaScriptEnabled(true);

		final Activity activity = getActivity();
		
//		  activity.getWindow().requestFeature(Window.FEATURE_PROGRESS);
//		  myWebView.setWebChromeClient(new WebChromeClient() { public void
//		  onProgressChanged(WebView view, int progress) {
//		  activity.setProgress(progress * 1000); } });
		 

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

		private boolean errorEncountered = false;
		// you want to catch when an URL is going to be loaded
		public boolean shouldOverrideUrlLoading(WebView view,
				String urlConection) {
			 try {
				 ApplicationController appController = ApplicationController.getInstance();
				 String url = urlConection + "token_login?access_token=" + appController.getAccessToken();

				int response = new RetreiveServerResponse().execute(url).get();
				Log.i(TAG, Integer.toString(response));
				switch(response) {
				case 200: 
					errorEncountered = false;
					return false;
				case 400:
					// TODO Time For Update
					if(!errorEncountered) {
						Toast.makeText(getActivity(), "BAD REQUEST", Toast.LENGTH_SHORT).show();
						errorEncountered = true;
						
						// TODO: Try to correct -> Retry
					}
					errorEncountered = false;
					return true;
				case 401:
					// TODO Unauthorized User
					if(!errorEncountered) {
					Toast.makeText(getActivity(), "UNAUTHORIZED", Toast.LENGTH_SHORT).show();
					errorEncountered = true;
					
					// TODO: Try to correct -> Retry
					}
					errorEncountered = false;
					return true;
				case 403:
					if(!errorEncountered) {
						tryAgain = new Object();

						attemptReauth();
//						synchronized(tryAgain) {
//							tryAgain.wait();
//						if(!shouldOverrideUrlLoading(view, urlConection)) {
							errorEncountered = false;
							return false;
//						}
//						}
					}

					errorEncountered = false;
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
		
		
		/*
		 * http://picture.jessestark.com/apps/access_token?grant_type=refresh_token&refresh_token=<>&client_id=<>&%20client_secret=<>
		 */
		
		private void attemptReauth() {
			String refreshToken = ApplicationController.getInstance().getRefreshToken();
			String clientId = ApplicationController.getInstance().getClientID();
			String clientSecret = ApplicationController.getInstance().getClientSecret();
			
			String urlArguments = "access_token" 
					+ "?grant_type=" + GRANT_TYPE_REFRESH 
					+ "&refresh_token=" + refreshToken
					+ "&client_id=" + clientId 
					+ "&client_secret=" + clientSecret;
			
			String fullURL = URL + urlArguments;
			

			
			JsonObjectRequest req;

			

			req = new JsonObjectRequest(Method.POST, fullURL, null,
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							try {
								ApplicationController appController = ApplicationController.getInstance();
								appController.setAccessToken(response.getString(ACCESS_TOKEN));
								
								tryAgain.notify();
								
							} catch (JSONException e) {
								// token does not exist, something broke
								Toast.makeText(getActivity(),
										"App Update Required To Connect",
										Toast.LENGTH_SHORT).show();
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {

							VolleyLog.e("Error: "
									+ Integer
											.toString(error.networkResponse.statusCode));
							String errorMessage = "ERROR NO MESSAGE";

							try {
								byte[] data = error.networkResponse.data;
								String stringData = new String(data,
										"UTF-8");

								JSONObject response = new JSONObject(
										stringData);
								errorMessage = response
										.getString(ERROR_MESSAGE);
							} catch (JSONException e) {
								Toast.makeText(getActivity(),
										"App Update Required To Connect",
										Toast.LENGTH_SHORT).show();
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							switch (error.networkResponse.statusCode) {
							case 400:
								Toast.makeText(getActivity(), errorMessage,
										Toast.LENGTH_SHORT).show();
								break;
							default:
								// TODO Something???
								break;
							}
						}

					});  
			ApplicationController.getInstance().addToRequestQueue(req);
			
		}
		
		public class RetreiveServerResponse extends AsyncTask<String, Void, Integer> {
			
			@Override
			protected Integer doInBackground(String... urls) {
				try {
					URL url = new URL(urls[0]);
					URLConnection conexion = url.openConnection();
					conexion.setConnectTimeout(3000);
					conexion.connect();
					
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(urls[0]);
					HttpResponse response = httpClient.execute(httpPost);
					
					return response.getStatusLine().getStatusCode();
				} catch (Exception e) {
					Log.e(TAG, "RetreiveServerResponse:", e);
					return null;
				}
			}
		}
	}


}
