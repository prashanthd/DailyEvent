package com.example.heeelo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dailyevent.util.MyApplication;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class CreateEvent extends Activity{

	private TextView mlocation_edt;
	private ImageView user_img;
	private Button selectimg_btn,audio_btn,date_btn,create_event_btn;
	private String TAG = CreateEvent.class.getName();
	private String outputFile = null;
	private MediaRecorder myRecorder;
	private byte[] audio = null;
	private byte[] imageData = null;
	private static final int RESULT_LOAD_IMAGE = 1;
	private String imagepath ="";
	private ParseFile imageFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createevent);

		initView();
		initLocation();
	}

	private void initView(){

		user_img = (ImageView) findViewById(R.id.user_img);
		mlocation_edt = (TextView) findViewById(R.id.mlocation_edt);
		audio_btn = (Button)findViewById(R.id.audio_btn);

		audio_btn.setText(getString(R.string.record_audio));

		outputFile = Environment.getExternalStorageDirectory().
				getAbsolutePath() + "/"+MyApplication.getInstanse().getRamdomNumber()+".3gpp";

		//Hide keyboard
		/*InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mlocation_edt.getWindowToken(), 0);
		mlocation_edt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {

				onStopRecording(null);
			}
		});*/
	}


	public void onAudioClick(View v) throws IllegalStateException, IOException{

		if(!audio_btn.getText().toString().equals(getString(R.string.audio_rec))){
			myRecorder = new MediaRecorder();
			myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
			myRecorder.setOutputFile(outputFile);
			myRecorder.prepare();
			myRecorder.start();
		}else{
			onStopRecording(null);
		}
	}

	public void onStopRecording(View v){

		Log.d(TAG , "outputFile Data :: "+outputFile);
		stopMediaPlayer();
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
	}
	public void onDateClick(View v){

	}

	private void stopMediaPlayer(){
		myRecorder.stop();
		myRecorder.release();
		myRecorder  = null;
	}
	public void onImageUpload(View v) {

		Intent i = new Intent(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}	
	public void onCreateEvent(View v){

		if(myRecorder !=null){
			onStopRecording(null);
		}
		final ProgressDialog mPdialog = MyApplication.getInstanse().getProgressDialog(CreateEvent.this);
		mPdialog.show();
		ParseFile audioFile = new ParseFile(String.valueOf(MyApplication.getInstanse().getRamdomNumber()+".3gpp"),audio);
		audioFile.saveInBackground();
		if(imageFile !=null && !TextUtils.isEmpty(mlocation_edt.getText().toString())){
			//Upload data
			ParseObject jobApplication = new ParseObject("User");
			jobApplication.put("task", "upload audio");
			jobApplication.put("description", "Test desc");
			jobApplication.put("location", mlocation_edt.getText().toString());
			jobApplication.put("audioData", audioFile);
			jobApplication.put("userImage", imageFile);
			jobApplication.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException arg0) {
					Log.d(TAG, "arg0:: "+arg0);
					finish();
					mPdialog.hide();
				}
			});
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1 && resultCode == RESULT_OK) {
			//Bitmap photo = (Bitmap) data.getData().getPath(); 
			//Uri imagename=data.getData();
			//Working code
			/*Uri selectedImageUri = data.getData();
			imagepath = getPath(selectedImageUri);
			Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
			user_img.setImageBitmap(bitmap);
			//save to byte array
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageData = stream.toByteArray();

			//Save into path
			imageFile = new ParseFile(MyApplication.getInstanse().getRamdomNumber()+".png", imageData);*/

			//========================================

			Uri selectedImageUri = data.getData();
			imagepath = getPath(selectedImageUri);
			Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
			//get image 100*100

			Bitmap bitmapResized = getResizedBitmap(bitmap,100,100);

			user_img.setImageBitmap(bitmapResized);
			//save to byte array
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmapResized.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageData = stream.toByteArray();

			//Save into path
			imageFile = new ParseFile(MyApplication.getInstanse().getRamdomNumber()+".png", imageData);
		}
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION 
		Matrix matrix = new Matrix(); 
		// RESIZE THE BIT MAP 
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP 
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	} 


	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void initLocation(){
		LocationManager	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Define a listener that responds to location updates 
		LocationListener locationListener = new LocationListener() { 
		    public void onLocationChanged(Location location) { 
		      // Called when a new location is found by the network location provider. 
		    	Log.d(TAG, "loaction l "+location.getLatitude());
				Log.d(TAG, "loaction long:::  "+location.getLongitude());
				String cityName = getLocationName(location.getLatitude(),location.getLongitude());
				mlocation_edt.setText("Location : "+cityName);
		    } 
		 
		    public void onStatusChanged(String provider, int status, Bundle extras) {} 
		 
		    public void onProviderEnabled(String provider) {} 
		 
		    public void onProviderDisabled(String provider) {} 
		  }; 
		 
		// Register the listener with the Location Manager to receive location updates 
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	public String getLocationName(double lattitude, double longitude) {

		String cityName = "Not Found";
		Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
		try { 

			List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
					10); 

			for (Address adrs : addresses) {
				if (adrs != null) {

					String city = adrs.getLocality();
					if (city != null && !city.equals("")) {
						cityName = city;
						System.out.println("city ::  " + cityName);
					} else { 

					} 
					// // you should also try with addresses.get(0).toSring(); 
				} 

			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return cityName;
	} 
}
