package com.starkca.geocam;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentLogin extends Fragment {

	private final String TAG = FragmentLogin.class.getCanonicalName();
	private final String URL = "http://picture.jessestark.com/apps/";

	private final String GRANT_TYPE = "password";
	private String clientId = "ERROR";
	private String clientSecret = "ERROR";
	private final String USERNAME = "&username=";
	private final String PASSWORD = "&password=";
	
	private final String ERROR_MESSAGE = "error_description";
	private final String ACCESS_TOKEN = "access_token";
	private final String REFRESH_TOKEN = "refresh_token";

	private OnLoginListener listener;

	private EditText etUName;
	private EditText etPass;
	// TODO
	private CheckBox chkRemember;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		
		clientId = ApplicationController.getInstance().getClientID();
		clientSecret = ApplicationController.getInstance().getClientSecret();

		View view = inflater.inflate(R.layout.fragment_login, container, false);

		etUName = (EditText) view.findViewById(R.id.etUName);
		etPass = (EditText) view.findViewById(R.id.etPass);

		Button btnLogin = (Button) view.findViewById(R.id.butLogin);
		chkRemember = (CheckBox) view.findViewById(R.id.chkRemember);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String uName = etUName.getText().toString();
				String pass = etPass.getText().toString();

				String urlArguments = "access_token" + "?grant_type="
						+ GRANT_TYPE + "&client_id=" + clientId + "&client_secret="
						+ clientSecret;
				
				String fullURL = URL + urlArguments + USERNAME + uName
						+ PASSWORD + pass;

				JsonObjectRequest req;
				req = new JsonObjectRequest(Method.POST, fullURL, null,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									ApplicationController.getInstance().setAccessToken(response.getString(ACCESS_TOKEN));
									ApplicationController.getInstance().setRefreshToken(response.getString(REFRESH_TOKEN));
									
									listener.onLoginSuccess();
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
				// add the request object to the queue to be executed
				ApplicationController.getInstance().addToRequestQueue(req);
			}

		});

		return view;
	}

	public interface OnLoginListener {
		public void onLoginSuccess();

		public void onLoginFail();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnLoginListener) {
			listener = (OnLoginListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement FragmentLogin.OnLoginListener");
		}
	}
}
