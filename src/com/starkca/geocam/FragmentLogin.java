package com.starkca.geocam;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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
	private final String URL = "http://picture.jessestark.com/login";

	private OnLoginListener listener;

	private EditText etUName;
	private EditText etPass;
	private CheckBox chkRemember;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View view = inflater.inflate(R.layout.fragment_login, container, false);

		etUName = (EditText) view.findViewById(R.id.etUName);
		etPass = (EditText) view.findViewById(R.id.etPass);

		Button btnLogin = (Button) view.findViewById(R.id.butLogin);
		chkRemember = (CheckBox) view.findViewById(R.id.chkRemember);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("token", "");
				params.put("email", etUName.getText().toString());
				params.put("password", etPass.getText().toString());
				params.put("remember",
						Boolean.toString(chkRemember.isChecked()));

				JsonObjectRequest req;
				req = new JsonObjectRequest(URL, new JSONObject(params),
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response.getBoolean("loggedin")) {
										String url = response
												.getString("redirect");
										listener.onLoginSuccess(url);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								
								VolleyLog.e("Error: " + Integer.toString(error.networkResponse.statusCode));
								switch(error.networkResponse.statusCode) {
								case 401: 
									Toast.makeText(getActivity(), "Creds Wrong", Toast.LENGTH_SHORT).show();
									// TODO show creds wrong
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
		public void onLoginSuccess(String url);

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
