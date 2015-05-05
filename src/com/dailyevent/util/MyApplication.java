package com.dailyevent.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.parse.Parse;

public class MyApplication extends Application{

	private static MyApplication myApplication = null; 
	private ProgressDialog dialog = null;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;	
	@Override
	public void onCreate() {
		super.onCreate();
		myApplication = this;

		initParse();
		initVolley();
	}

	private void initVolley(){

		mRequestQueue = Volley.newRequestQueue(MyApplication.this);
		mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {
			private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
			public void putBitmap(String url, Bitmap bitmap) {
				mCache.put(url, bitmap);
			}
			public Bitmap getBitmap(String url) {
				return mCache.get(url);
			}
		});
	}
	

	public synchronized static MyApplication getInstanse(){

		return myApplication;
	}

	private void initParse(){

		Parse.initialize(this, Constant.PARSE_APP_ID, Constant.PARSE_CKIENT_ID);
	}
	public boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public int getRamdomNumber(){

		Random random = new Random();
		return random.nextInt(80 - 65) + 65;
	}

	public byte[] convertFileToByteArray(String filePath){
		byte[] byteData = null;
		try
		{
			File f = new File(filePath);
			System.out.println("Is file exits:: "+f.exists());
			InputStream inputStream = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024*8];
			int bytesRead = 0;

			while ((bytesRead = inputStream.read(b)) != -1)
			{
				bos.write(b, 0, bytesRead);
			}


			byteData = bos.toByteArray();
			System.out.println("byteData before:: "+byteData);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("byteData after:: "+byteData);
		return byteData;
	}

	public void hideProgrssDialog(Context context){

		if(dialog !=null){
			dialog.hide();
		}
	}

	public void showProgrssDialog(Context context){

		dialog = new ProgressDialog(context);
		dialog.show();
	}
	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ProgressDialog getProgressDialog(Context context){

		ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setMessage("Loading...");
		return pDialog;
	}

}
