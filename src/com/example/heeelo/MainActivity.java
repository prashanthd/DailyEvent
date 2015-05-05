package com.example.heeelo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dailyevent.util.Constant;
import com.dailyevent.util.MyApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;


public class MainActivity extends Activity implements OnClickListener,
ConnectionCallbacks, OnConnectionFailedListener {

	public String username = "";

	private static final int RC_SIGN_IN = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	// Logcat tag
	private static final String TAG = "MainActivity";

	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 300;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private SignInButton btnSignIn;
	private Button btnSignOut, btnRevokeAccess,btn_liststories,btn_upload_picture,startBtn,stopBtn,playBtn,stopPlayBtn;
	private ImageView imgProfilePic;
	private TextView txtName, txtEmail;
	private LinearLayout llProfileLayout;
	private LinearLayout llmainscreen;
	private EditText mTaskInput,mtask_title;
	private ListView mListView;


	private byte[] image;
	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	byte[] audio = null;

	ProgressDialog ringProgressDialog;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		initParse();
		outputFile = Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/"+MyApplication.getInstanse().getRamdomNumber()+".3gpp";


		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);
		btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
		btn_liststories = (Button) findViewById(R.id.btn_liststories);
		btn_upload_picture = (Button) findViewById(R.id.upload_picture);
		imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		txtName = (TextView) findViewById(R.id.txtName);
		txtEmail = (TextView) findViewById(R.id.txtEmail);
		llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
		llmainscreen = (LinearLayout) findViewById(R.id.llmainscreen);
		mTaskInput = (EditText) findViewById(R.id.task_input);
		mtask_title = (EditText) findViewById(R.id.task_title);

		// mListView = (ListView) findViewById(R.id.task_list);


		// Button click listeners
		btnSignIn.setOnClickListener(this);
		btnSignOut.setOnClickListener(this);
		btnRevokeAccess.setOnClickListener(this);
		btn_liststories.setOnClickListener(this);
		//btn_upload_picture.setOnClickListener(this);

		btn_upload_picture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		myRecorder = new MediaRecorder();
		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myRecorder.setOutputFile(outputFile);

		startBtn = (Button)findViewById(R.id.start);
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				start(v);
			}
		});

		stopBtn = (Button)findViewById(R.id.stop);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stop(v);
			}
		});

		playBtn = (Button)findViewById(R.id.play);
		playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).addApi(Plus.API).addApi(LocationServices.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		//Parse.initialize(this, "srvTMoHM7Pc3eJyZMzTe8JfjLwE0Z5Il3LOMTdtU", "9prrFpnCCHPUpALfaI0C7dm7tI0WpQwg2Okxt2eW");
		/*   ParseObject n = new ParseObject("n");
        n.put("foo1", "bar");
        n.put("foo2", "bar");
        n.put("foo3", "bar");
        n.saveInBackground();	*/
		/*ParseObject testObject = new ParseObject("hhhhh");
		testObject.put("foo", "bar");
		testObject.saveInBackground();*/

		//ParseObject.registerSubclass(Task.class);
	}

	private void initParse(){

		Parse.initialize(this, Constant.PARSE_APP_ID, Constant.PARSE_CKIENT_ID);
	}

	private void uploadAudioParseFile(){

		Log.d(TAG, "outputFile Data :: "+outputFile);

		try
		{
			File f = new File(outputFile);
			InputStream inputStream = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024*8];
			int bytesRead = 0;

			while ((bytesRead = inputStream.read(b)) != -1)
			{
				bos.write(b, 0, bytesRead);
			}

			audio = bos.toByteArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}


		Log.d(TAG, "Array Data :: "+MyApplication.getInstanse().convertFileToByteArray(outputFile));
		Log.d(TAG, "audioData:after: "+audio);
		final ParseFile file = new ParseFile(String.valueOf(MyApplication.getInstanse().getRamdomNumber()+".3gpp"),audio);
		file.saveInBackground()	;	



		//Upload data
		ParseObject jobApplication = new ParseObject("User");
		//jobApplication.put("createdAt", new Date());
		//jobApplication.put("updatedAt", new Date());
		jobApplication.put("task", "upload audio");
		jobApplication.put("description", "Test desc");
		jobApplication.put("location", "LingamPally");
		jobApplication.put("audioData", file);
		jobApplication.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "arg0:: "+arg0);
			}
		});
	}	




	public void createTask(View v) {
		if  (mTaskInput.getText().length() > 0 && mtask_title.getText().length() > 0){

			try
			{
				File f = new File(outputFile);
				InputStream inputStream = new FileInputStream(f);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] b = new byte[1024*8];
				int bytesRead = 0;

				while ((bytesRead = inputStream.read(b)) != -1)
				{
					bos.write(b, 0, bytesRead);
				}

				audio = bos.toByteArray();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			ParseObject testObject = new ParseObject(txtEmail.getText().toString().split("@")[0]);
			testObject.put("title", mtask_title.getText().toString());
			testObject.put("body", mTaskInput.getText().toString());
			if(image!= null || audio!= null) {
				ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Uploading media ...", true);

			}
			/*if(image != null) {

	        	ParseFile file = new ParseFile("blogpostimage.png", image);
	        	file.saveInBackground(new SaveCallback() {
	        		  @Override
					public void done(ParseException e) {
	        		    // Handle success or failure here ...

	        			  ringProgressDialog.dismiss();

	        				  ringProgressDialog = ProgressDialog.show(MainActivity.this, "Successful", "Image saved", true);
	        				  try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	        				  ringProgressDialog.dismiss();


	        		  }
	        		}, new ProgressCallback() {
	        		  @Override
					public void done(Integer percentDone) {

	        		  }
	        	});
	        	testObject.put("ImageFile", file); 

        	}*/


			if(audio != null) {
				System.out.println("in audio....");
				ParseFile file1 = new ParseFile("Blogaudio.3gpp", audio);
				file1.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						// Handle success or failure here ...

						ringProgressDialog.dismiss();
						File file = new File(outputFile);
						if(file.exists()) {

							boolean deleted = file.delete();

							if(deleted)
								System.out.println("File deleted");
							else
								System.out.println("cant delete file");

						}
						if(e == null) {
							ringProgressDialog = ProgressDialog.show(MainActivity.this, "Successful", "Audio saved", true);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							ringProgressDialog.dismiss();

						}
					}
				}, new ProgressCallback() {
					@Override
					public void done(Integer percentDone) {

					}
				});
				testObject.put("AudioFile", file1); 

			}


			testObject.saveInBackground();

			image = null;
			audio = null;

			/*Task t = new Task();         
            t.setUsername(txtEmail.getText().toString());
            t.setBody(mTaskInput.getText().toString());
            t.setTitle(mtask_title.getText().toString());
            t.saveEventually();*/

			mTaskInput.setText("");
			mtask_title.setText("");
		}
	}

	public void start(View view){
		try {
			myRecorder.prepare();
			myRecorder.start();
		} catch (IllegalStateException e) {
			// start:it is called before prepare()
			// prepare: it is called after start() or before setOutputFormat() 
			e.printStackTrace();
		} catch (IOException e) {
			// prepare() fails
			e.printStackTrace();
		}

		// text.setText("Recording Point: Recording");
		startBtn.setEnabled(false);
		stopBtn.setEnabled(true);

		Toast.makeText(getApplicationContext(), "Start recording...", 
				Toast.LENGTH_SHORT).show();
	}

	public void stop(View view){
		try {

			uploadAudioParseFile();
			/*myRecorder.stop();
			myRecorder.release();
			myRecorder  = null;

			stopBtn.setEnabled(false);
			playBtn.setEnabled(true);
			//text.setText("Recording Point: Stop recording");

			Toast.makeText(getApplicationContext(), "Stop recording...",
					Toast.LENGTH_SHORT).show();*/


		} catch (IllegalStateException e) {
			//  it is called before start()
			e.printStackTrace();
		} catch (RuntimeException e) {
			// no valid audio/video data has been received
			e.printStackTrace();
		}finally{
			uploadAudioParseFile();
		}
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
		}finally{

		}
	}

	public void upload_picture(View v) {

		System.out.println("hi");
		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	public void goToShowTitles() {



		Intent intent = new Intent(this, showtitles.class);

		intent.putExtra("username", username);
		startActivity(intent);   


	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}

		if (requestCode == RESULT_LOAD_IMAGE && responseCode == RESULT_OK && null != intent) {
			Uri selectedImage = intent.getData();
			String[] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			image = stream.toByteArray();



			// ImageView imageView = (ImageView) findViewById(R.id.imgView);
			//responseCodeimageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		Location mLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		if (mLocation != null) {
			Toast.makeText(this, String.valueOf(mLocation.getLatitude()) + "  " + String.valueOf(mLocation.getLongitude()), Toast.LENGTH_LONG).show();
			/*            mLatitudeText.setText(String.valueOf(mLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLocation.getLongitude()));*/
		}



		// Get user's information
		getProfileInformation();

		// Update the UI after signin
		updateUI(true);

	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			btnSignIn.setVisibility(View.GONE);
			btnSignOut.setVisibility(View.VISIBLE);
			btn_liststories.setVisibility(View.VISIBLE);

			// btnRevokeAccess.setVisibility(View.VISIBLE);btn_liststories
			llProfileLayout.setVisibility(View.VISIBLE);
			llmainscreen.setVisibility(View.VISIBLE);
			//mListView.setVisibility(View.VISIBLE);
		} else {
			btnSignIn.setVisibility(View.VISIBLE);
			btnSignOut.setVisibility(View.GONE);
			btn_liststories.setVisibility(View.GONE);

			//btnRevokeAccess.setVisibility(View.GONE);
			llProfileLayout.setVisibility(View.GONE);
			llmainscreen.setVisibility(View.GONE);
			//mListView.setVisibility(View.GONE);
		}
	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				username = email.split("@")[0];

				Log.e(TAG, "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl);

				txtName.setText(personName);
				txtEmail.setText(email);

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Button on click listener
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_in:
			// Signin button clicked
			signInWithGplus();
			break;
		case R.id.btn_sign_out:
			// Signout button clicked
			signOutFromGplus();
			break;
		case R.id.btn_revoke_access:
			// Revoke access button clicked
			revokeGplusAccess();
			break;
		case R.id.btn_liststories:
			goToShowTitles();
			break;
		}
	}

	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateUI(false);
		}
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
			.setResultCallback(new ResultCallback<Status>() {
				@Override
				public void onResult(Status arg0) {
					Log.e(TAG, "User access revoked!");
					mGoogleApiClient.connect();
					updateUI(false);
				}

			});
		}
	}

	/**
	 * Background Async task to load user profile picture from url
	 * */
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
	private void uploadAudioFile(){

	}
}