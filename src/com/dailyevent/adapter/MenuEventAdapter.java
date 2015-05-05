package com.dailyevent.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dailyevent.model.EventsData;
import com.dailyevent.util.MyApplication;
import com.example.heeelo.GetAllEvents;
import com.example.heeelo.R;

public class MenuEventAdapter extends BaseAdapter{

	private Context context = null;
	List<EventsData> events = null;
	private ImageLoader mImageLoader = null;
	private GetAllEvents mEventContext =null;
	public MenuEventAdapter(GetAllEvents mEventContext, Context context,List<EventsData> events){
		
		this.context = context;
		this.events = events;
		this.mEventContext = mEventContext;
		mImageLoader = MyApplication.getInstanse().getImageLoader();
	}
	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int arg0) {
		return events.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	private class ViewHolder{

		NetworkImageView image;
		TextView location_txt,eventName_txt,createdDate_txt;
	}
	ViewHolder holder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		if(convertView == null){
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.events_list, null);
			holder.image = (NetworkImageView)convertView.findViewById(R.id.user_img);
			
			holder.location_txt = (TextView)convertView.findViewById(R.id.location_txt);
			holder.eventName_txt = (TextView)convertView.findViewById(R.id.eventName_txt);
			holder.createdDate_txt = (TextView)convertView.findViewById(R.id.createdDate_txt);
			convertView.setTag(holder);

		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		//NetworkImageView avatar = (NetworkImageView)view.findViewById(R.id.twitter_avatar);
		//avatar.setImageUrl("http://someurl.com/image.png",mImageLoader);
		Log.d("", "Url:: "+events.get(position).userImage);
		holder.eventName_txt.setText(events.get(position).task);
		holder.createdDate_txt.setText(events.get(position).createdDate);
		holder.image.setImageUrl(events.get(position).userImage,mImageLoader);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mEventContext.play3GP(events.get(position).audioUrl);
				//mEventContext.playAudio(events.get(position).audioUrl);
				
			}
		});
		return convertView;
	}
	/*public void loadImage(String url,NetworkImageView image){
		
		if(MyApplication.getInstanse().isOnline(context)){
			
			image.setImageUrl( url,mImageLoader);
		}
	}*/
}
