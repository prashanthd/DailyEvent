package com.example.heeelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.dailyevent.adapter.MenuEventAdapter;
import com.dailyevent.model.EventsData;
import com.dailyevent.util.Constant;
import com.dailyevent.util.MyApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GetAllEvents extends Activity{
	private String TAG = GetAllEvents.class.getName();

	private ListView menu_events_lv = null;
	private List<EventsData> eventsData= new ArrayList<EventsData>();
	private MediaPlayer mediaPlayer = null; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getallevents);

		initView();
		getAllEventsData();
	}
	private void initView(){
		menu_events_lv = (ListView)findViewById(R.id.menu_events_lv);
	}
	@SuppressWarnings("unchecked")
	private void getAllEventsData(){

		@SuppressWarnings("rawtypes")
		//ParseQuery query = new ParseQuery(Constant.USER_TABLE);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constant.USER_TABLE);
		final ProgressDialog mPDialog= MyApplication.getInstanse().getProgressDialog(GetAllEvents.this);
		mPDialog.show();
		//query.whereEqualTo("objectId", "ag77EoCJHf");
		//MyApplication.getInstanse().
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException exception) {

				/*for (ParseObject parseObject : objects) {
					Log.d(TAG, "parseObject::: "+parseObject);
					ParseFile image = (ParseFile) parseObject.get("userImage");
					Log.d(TAG, "Image::: "+image.getUrl());
					Date date = (Date) parseObject.get("createdAt");
					Log.d(TAG, "dateStr::: "+date);
					//DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Log.d(TAG, "task::: "+parseObject.getString("task"));

					//String dateStr = df.format(date);
					//Log.d(TAG, "dateStr::: "+dateStr);
					eventsData.add(new EventsData(parseObject.getString("updatedAt"),
							parseObject.getString("task"),
							"",
							image.getUrl()));
				}*/
				
				for (ParseObject parseObject : objects) {
					Log.d(TAG, "parseObject::: "+parseObject);
					ParseFile image = (ParseFile) parseObject.get("userImage");
					Log.d(TAG, "Image::: "+image.getUrl());
					ParseFile audioFile = (ParseFile) parseObject.get("audioData");
					Log.d(TAG, "audioFile::: "+audioFile.getUrl());
					
					Date date = (Date) parseObject.get("createdAt");
					Log.d(TAG, "dateStr::: "+date);
					//DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Log.d(TAG, "task::: "+parseObject.getString("task"));

					//String dateStr = df.format(date);
					//Log.d(TAG, "dateStr::: "+dateStr);
					try {
						/*eventsData.add(new EventsData(parseObject.getString("updatedAt"),
								parseObject.getString("task"),
								"",
								image.getUrl(),
								audioFile.getData()));*/
						eventsData.add(new EventsData(parseObject.getString("updatedAt"),
								parseObject.getString("task"),
								"",
								image.getUrl(),
								audioFile.getData()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				/*try{
					Gson gson = new Gson();
					String responseData = gson.toJson(objects);	
					Log.d(TAG, "responseData:: "+responseData);
					//eventsData = gson.fromJson(responseData, EventsData.class);

					//Log.d(TAG, "Checking Data::: "+eventsData.EventsData.get(0).updatedAt);

					Type collectionType = new TypeToken<List<EventsData>>(){}.getType();
					eventsData = (List<EventsData>) new Gson()
					               .fromJson( responseData , collectionType);
					Log.d(TAG, "updatedAt:: "+eventsData.get(0).updatedAt);
					Log.d(TAG, "updatedAt:: "+eventsData.get(0).updatedAt);

				}catch(Exception e){
					e.printStackTrace();
				}*/
				MenuEventAdapter adapter = new MenuEventAdapter(GetAllEvents.this,GetAllEvents.this,eventsData);
				menu_events_lv.setAdapter(adapter);
				mPDialog.hide();
			}
		});
	}
	private  void initPlayer(){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}
	public void playAudio(String url){

		try {
			initPlayer();
			mediaPlayer.setDataSource(url);
			//mediaPlayer.prepare(); // might take long! (for buffering, etc)
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { 
	        @Override 
	        public void onPrepared(MediaPlayer mp) {
	 
	        	Log.d(TAG, "setOnPreparedListener");
	           // mp.start();
	 
	        } 
	    }); 
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				
				Log.d(TAG, "onCompletion");
				mediaPlayer.release();
				mediaPlayer = null;
			}
		});
	}
	
	public void play3GP(byte[] gpSoundByteArray) {
	    try {
	        File temp3GP = File.createTempFile("temp_audio", "3gp", getCacheDir());
	        temp3GP.deleteOnExit();
	        FileOutputStream fos = new FileOutputStream(temp3GP);
	        fos.write(gpSoundByteArray);
	        fos.close();

	        MediaPlayer mediaPlayer = new MediaPlayer();

	        FileInputStream fis = new FileInputStream(temp3GP);
	        mediaPlayer.setDataSource(fis.getFD());

	        mediaPlayer.prepare();
	        mediaPlayer.start();
	    } catch (IOException ex) {
	        String s = ex.toString();
	        ex.printStackTrace();
	    }
	}
}
