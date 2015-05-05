package com.example.heeelo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuPage extends Activity implements OnItemClickListener{

	private ListView menu_listview = null;
	private final int CREATE_EVENT_PAGE = 0;
	private final int CREATE_DETAILS_PAGE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menupage);
		initView();
		loadListView();
	}
	
	private void initView(){
		
		menu_listview =(ListView)findViewById(R.id.menu_listview);
		menu_listview.setOnItemClickListener(this);
	}
	
	private void loadListView(){
		
		String[] values = new String[] { "Create Event", "All Events" }; 
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		        android.R.layout.simple_list_item_1, values);
		    menu_listview.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		switch (position) {
		case CREATE_EVENT_PAGE:
			startActivity(new Intent(MenuPage.this, CreateEvent.class));
			break;
		case CREATE_DETAILS_PAGE:
			startActivity(new Intent(MenuPage.this, GetAllEvents.class));
			break;

		default:
			break;
		}
	}
}
