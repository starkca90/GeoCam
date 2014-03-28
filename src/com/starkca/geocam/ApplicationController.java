package com.starkca.geocam;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.text.TextUtils;

public class ApplicationController extends Application {
	
	private final String TAG = ApplicationController.class.getCanonicalName();
	
	private RequestQueue mRequestQueue;
	private String access_token;
	private String refresh_token;
	private static ApplicationController sInstance;
	
	private final String CLIENT_ID = "mOuB5d3tKO0yu3v7uLWshcAVR5TSyIfxUSxPiWYY";
	private final String CLIENT_SECRET = "eqI4xD6z3oJBeLJXyQlN2VWYcIhH021tKXUmxkSu";
	
	@Override public void onCreate() {
		super.onCreate();
		
		sInstance = this;
	}
	
	public static synchronized ApplicationController getInstance() {
		return sInstance;
	}
	
	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}
	
	public String getAccessToken() {
		return access_token;
	}
	
	public void setRefreshToken(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
	public String getRefreshToken() {
		return refresh_token;
	}
	
	public String getClientID() {
		return CLIENT_ID;
	}
	
	public String getClientSecret() {
		return CLIENT_SECRET;
	}
	
	/**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     * 
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     * 
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     * 
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}

