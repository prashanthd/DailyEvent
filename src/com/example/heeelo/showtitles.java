package com.example.heeelo;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class showtitles extends ListActivity{
	
	Button button;
	private List<Task> posts;
	String username;
	ProgressDialog ringProgressDialog;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showtitles);
		
		ringProgressDialog = ProgressDialog.show(showtitles.this, "Please wait ...", "Retrieving posts", true);

		
		posts = new ArrayList<Task>();
		ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this, R.layout.list_item_layout, posts);
		setListAdapter(adapter);
		refreshPostList();
		
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	 
	    Task note = posts.get(position);
	    Intent intent = new Intent(this, Detailview.class);
	    intent.putExtra("username",username);
	    intent.putExtra("noteId", note.getId());
	    intent.putExtra("noteTitle", note.getTitle());
	    intent.putExtra("noteContent", note.getContent());
	    startActivity(intent);
	 
	}
	
	private void refreshPostList() {
		 
		Bundle extras = getIntent().getExtras();  
		username = extras.getString("username");  
		
	    ParseQuery<ParseObject> query = ParseQuery.getQuery(username);
	 
	    query.findInBackground(new FindCallback<ParseObject>() {
	 
	        @Override
	        public void done(List<ParseObject> postList, ParseException e) {
	            if (e == null) {
	                // If there are results, update the list of posts
	                // and notify the adapter
	            	ringProgressDialog.dismiss();
	                posts.clear();
	                for (ParseObject post : postList) {
	                    Task note = new Task(post.getObjectId(), post.getString("title"), post.getString("body"));
	                    posts.add(note);
	                }
	                ((ArrayAdapter<Task>) getListAdapter()).notifyDataSetChanged();
	            } else {
	                Log.d(getClass().getSimpleName(), "Error: " + e.getMessage());
	            }
	        }
	    });
	}

}
