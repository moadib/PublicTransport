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

import com.ursinepaw.publictransport.bus.BusItem;
import com.ursinepaw.publictransport.bus.BusOverlay;

import ru.yandex.yandexmapkit.utils.GeoPoint;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import android.widget.Toast;

public class UpdateBusOverlayTask extends AsyncTask<String, Integer, ArrayList<BusItem>> {
	private Context mContext;
	private String mCookie;
	private BusOverlay mOverlay;
	
	public UpdateBusOverlayTask(Context context, String cookie, BusOverlay overlay) {
		mContext = context;
		mCookie = cookie;
		mOverlay = overlay;
	}	
	
	@Override
	protected ArrayList<BusItem> doInBackground(String... nums) {				
		HttpClient hc = new DefaultHttpClient();
		for (int i = 0; i < nums.length; i++) {             
			String uri = String.format("http://beta.ulroad.ru/getxml.php?n=%s", nums[i]);
			try {
				HttpGet request = new HttpGet(uri);	
				request.setHeader("Cookie", mCookie);
				request.setHeader("Host", "beta.ulroad.ru");
				request.setHeader("Referer", "http://beta.ulroad.ru/");
				
				HttpResponse rp = hc.execute(request);
				if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String packet = EntityUtils.toString(rp.getEntity());
					XmlPullParser parser = Xml.newPullParser();
					try {
			            parser.setInput(new StringReader(packet));
			            ArrayList<BusItem> result = new ArrayList<BusItem>();
			            
			            double lat = 0.0;
			            double lon = 0.0;
			            float course = 0;
			            
			            int eventType = parser.getEventType(); 
			            String name = null;
			            while (eventType != XmlPullParser.END_DOCUMENT){			            	
			                switch (eventType){
			                    case XmlPullParser.START_DOCUMENT:                    	
			                        break;
			                    case XmlPullParser.START_TAG:
			                    	name = parser.getName();
			                        break;
			                    case XmlPullParser.TEXT:
			                    	if (name.equalsIgnoreCase("lat")) 
			                        	lat = Double.parseDouble(parser.getText());
			                        else if (name.equalsIgnoreCase("lng"))
			                        	lon = Double.parseDouble(parser.getText());
			                        else if (name.equalsIgnoreCase("course"))
			                        	course = Float.parseFloat(parser.getText());
			                    	break;
			                    case XmlPullParser.END_TAG:
			                    	name = parser.getName();
			                    	if (name.equalsIgnoreCase("object"))
			                    		result.add(new BusItem(new GeoPoint(lon, lat), course));
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
	protected void onPostExecute(ArrayList<BusItem> result) {		
		mOverlay.clearOverlayItems();
		if (result == null)
			Toast.makeText(mContext, R.string.couldnt_receive_data_from_server, Toast.LENGTH_SHORT).show();
		else
			for (BusItem item : result)
				mOverlay.addOverlayItem(item);
		mOverlay.getMapController().notifyRepaint();
    }

}
