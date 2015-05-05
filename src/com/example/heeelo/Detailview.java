package com.example.heeelo;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Detailview extends ActionBarActivity {
	
    private VideoView mVideoView;
    private MediaPlayer myPlayer;
    private Button playBtn,stopPlayBtn;
    final String outputFile = Environment.getExternalStorageDirectory().
            getAbsolutePath() + "/DailyBlogtemp.3gpp";
	ProgressDialog ringProgressDialog;
	private Button loadImage,loadAudio;
	private TextView body;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailview);
		ringProgressDialog = ProgressDialog.show(Detailview.this, "Please wait ...", "Retrieving posts", true);

		body = (TextView) findViewById(R.id.contentbody);
		playBtn = (Button)findViewById(R.id.play);
        playBtn.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
  				play(v);	
    		}
        });
        
        stopPlayBtn = (Button)findViewById(R.id.stopPlay);
        stopPlayBtn.setOnClickListener(new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			stopPlay(v);
    		}
        });
		
		Bundle extras = getIntent().getExtras();  
		String username = extras.getString("username");  
		String id  = extras.getString("noteId");  
		String content = extras.getString("noteContent"); 
		
		body.setText(content);
	
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(username);
		
		query.getInBackground(id, new GetCallback<ParseObject>() {
			  @Override
			public void done(ParseObject object, ParseException e) {
			    if (e == null) {
			      // object will be your game score
			    	ParseFile imageF = (ParseFile)object.get("ImageFile");
			    	ParseFile videoF = (ParseFile)object.get("AudioFile");
			    	
			    	imageF.getDataInBackground(new GetDataCallback() {
			    		  @Override
						public void done(byte[] data, ParseException e) {
			    		    if (e == null) {
			    		      // data has the bytes for the resume
			    	            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			    	            ImageView pic;
			    	            pic = (ImageView) findViewById(R.id.imageView1);
			    	            pic.setImageBitmap(bmp);
			    	            ringProgressDialog.dismiss();
			    		    	
			    		    } else {
			    		      // something went wrong
			    		    }
			    		  }
			    		});
			    	
			    	videoF.getDataInBackground(new GetDataCallback() {
			    		  @Override
						public void done(byte[] data, ParseException e) {
			    		    if (e == null) {
			    		      // data has the bytes for the resume
			    		    	InputStream input = new ByteArrayInputStream(data);
			    		    	try {
									OutputStream output = new FileOutputStream(outputFile);
									byte data1[] = new byte[4096];
									int count;
									while ((count = input.read(data1)) != -1) {
									    output.write(data1, 0, count);
									}
									ringProgressDialog.dismiss();
									
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
			    		    	
			    		    	
			    		    } else {
			    		      // something went wrong
			    		    }
			    		  }
			    		});
			    	
			    	
			    } else {
			      // something went wrong
			    }
			  }
			});
		


	}
	
	 public void play(View view) {
	 	   try{
	 		   myPlayer = new MediaPlayer();
	 		   myPlayer.setDataSource(outputFile);
	 		   myPlayer.prepare();
	 		   myPlayer.start();
	 		   
	 		   playBtn.setEnabled(false);
	 		   stopPlayBtn.setEnabled(true);
	 		   //text.setText("Recording Point: Playing");
	 		   
	 		   Toast.makeText(getApplicationContext(), "Start play the recording...", 
	 				   Toast.LENGTH_SHORT).show();
	 	   } catch (Exception e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	    }
	    
	    public void stopPlay(View view) {
	 	   try {
	 	       if (myPlayer != null) {
	 	    	   myPlayer.stop();
	 	           myPlayer.release();
	 	           myPlayer = null;
	 	           playBtn.setEnabled(true);
	 	           stopPlayBtn.setEnabled(false);
	 	          // text.setText("Recording Point: Stop playing");
	 	           
	 	           Toast.makeText(getApplicationContext(), "Stop playing the recording...", 
	 					   Toast.LENGTH_SHORT).show();
	 	       }
	 	   } catch (Exception e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
	    }
	    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detailview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
