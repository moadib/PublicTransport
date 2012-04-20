package com.ursinepaw.publictransport;

import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import com.ursinepaw.publictransport.route.RouteItem;
import com.ursinepaw.publictransport.route.RouteOverlay;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import android.widget.Toast;

public class UpdateRouteOverlayTask extends AsyncTask<String, Integer, ArrayList<RouteItem>> {
	private Context mContext;
	private RouteOverlay mOverlay;
	
	public UpdateRouteOverlayTask(Context context, RouteOverlay overlay) {
		mContext = context;
		mOverlay = overlay;
	}	
	
	@Override
	protected ArrayList<RouteItem> doInBackground(String... nums) {				
		HttpClient hc = new DefaultHttpClient();
		for (int i = 0; i < nums.length; i++) {
			String num = nums[i];
			if (num.length() == 1)
				num = "0" + nums[i];
			String uri = String.format("http://beta.ulroad.ru/kml/visual/%s.xml", num);
			try {
				HttpGet request = new HttpGet(uri);
				request.setHeader("Host", "beta.ulroad.ru");
				
				HttpResponse rp = hc.execute(request);
				if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String packet = EntityUtils.toString(rp.getEntity());
					XmlPullParser parser = Xml.newPullParser();
					try {
			            parser.setInput(new StringReader(packet));
			            ArrayList<RouteItem> result = new ArrayList<RouteItem>();
			            
			            RouteItem 	item = null;
			            boolean		isLine = false;
			            
			            int eventType = parser.getEventType(); 
			            String name = null;
			            while (eventType != XmlPullParser.END_DOCUMENT){			            	
			                switch (eventType){
			                    case XmlPullParser.START_DOCUMENT:                    	
			                        break;
			                    case XmlPullParser.START_TAG:
			                    	name = parser.getName();
			                    	if (name.equalsIgnoreCase("Placemark"))
			                    		item = new RouteItem();
			                    	else if (name.equalsIgnoreCase("LineStyle")) {
			                    		if (item != null)
			                    			isLine = true;
			                    	}
			                        break;
			                    case XmlPullParser.TEXT:
			                    	if (name.equalsIgnoreCase("coordinates") && item != null && isLine) { 
			                    		String[] points = parser.getText().split("\\s+");
			                    		for (String point : points) {
			                    			String[] scalars = point.split(",");
			                    			if (scalars.length == 3) {
			                    				double lat = Double.parseDouble(scalars[0]);
			                    				double lon = Double.parseDouble(scalars[1]);
			                    				item.addPoint(lon, lat);
			                    			}
			                    		}
			                    	}
			                    	break;
			                    case XmlPullParser.END_TAG:
			                    	name = parser.getName();
			                    	if (name.equalsIgnoreCase("Placemark")) {
			                    		result.add(item);
			                    		item = null;
			                    	}
			                        break;
			                }
			                eventType = parser.next();
			            }
			            return result;
			        } 
			        catch (Exception e) {
			        }
				}
			}
			catch(Exception e){				
			}
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(ArrayList<RouteItem> result) {
		mOverlay.clearOverlayItems();
		if (result == null)
			Toast.makeText(mContext, R.string.couldnt_receive_data_from_server, Toast.LENGTH_SHORT).show();
		else
			for (RouteItem item : result)
				mOverlay.addOverlayItem(item);
		mOverlay.getMapController().notifyRepaint();
    }

}
