package com.dailyevent.model;


public class EventsData {

	//public ArrayList<Events> EventsData;
	//public class Events{
		public String updatedAt;
		public String task;
		public String createdDate;
		public String userImage;
		public byte[] audioUrl;
		//public String audioUrl;
		public EventsData(String updatedAt, String task, String createdDate,
			String userImage,byte[] audioUrl) {
		super();
		this.updatedAt = updatedAt;
		this.task = task;
		this.createdDate = createdDate;
		this.userImage = userImage;
		this.audioUrl = audioUrl;
	}
		
		/*public  class serverData{

			public String description;
			public String location;
		public  class userImage
		{

			public String url;
		}

		public  class audioData{

			public String url;
		}
	}*/
}
