package com.ursinepaw.publictransport;

import java.util.ArrayList;

import com.ursinepaw.publictransport.bus.BusItem;
import com.ursinepaw.publictransport.bus.BusOverlay;
import com.ursinepaw.publictransport.route.RouteItem;
import com.ursinepaw.publictransport.route.RouteOverlay;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private MapView			mMapView = null;
	private	Button			mRouteButton = null;
	
	private MapController	mMapController = null;
	private OverlayManager	mOverlayManager = null;
	private RouteOverlay	mRouteOverlay = null;
	private	BusOverlay		mBusOverlay = null;	
	private Handler 		mHandler = new Handler();
	private GetCookieTask	mGetCookieTask = null;
	private	UpdateBusOverlayTask	mUpdateBusOverlayTask = null;
	private String			mRoute = "";
	private int				mUpdateInverval = 2000;
	private String			mCookie = "";
	
	public void setRoute(String route) {
		boolean updateRoute = false;
		if (mRoute != route) {
			mCookie = "";
			mRoute = route;
			mRouteOverlay.clearOverlayItems();
			mBusOverlay.clearOverlayItems();
			
			if (route.length() > 0)
				updateRoute = true;
		}
		else if (mRouteOverlay.getOverlayItems().size() == 0)
			updateRoute = true;
		
		if (mRoute.length() > 0) {
			mRouteButton.setText(route + "-й маршрут");
			
			if (updateRoute)
				new UpdateRouteOverlayTask(MainActivity.this, mRouteOverlay).execute(mRoute);	
			
			mHandler.removeCallbacks(mRunnable);
			mHandler.post(mRunnable);					
		}
		else
			mRouteButton.setText(R.string.pick_a_route);		
		mMapController.notifyRepaint();
	}
	
	public void setCookie(String cookie) {
		mCookie = cookie;
		if (mCookie != "") {
			mHandler.removeCallbacks(mRunnable);
			mHandler.post(mRunnable);
		}			
	}

	private final Runnable mRunnable = new Runnable() {
		public void run() {
			if (mRoute != "") {			
				mHandler.removeCallbacks(mRunnable);
				if (mCookie.length() == 0) {
					if (mGetCookieTask == null || mGetCookieTask.getStatus() == Status.FINISHED) {
						mGetCookieTask = new GetCookieTask(MainActivity.this);
						mGetCookieTask.execute(mRoute);
					}
					mHandler.postDelayed(mRunnable, 1000);
				}
				else {
					if (mUpdateBusOverlayTask == null || mUpdateBusOverlayTask.getStatus() == Status.FINISHED) {
						mUpdateBusOverlayTask = new UpdateBusOverlayTask(MainActivity.this, mCookie, mBusOverlay);
						mUpdateBusOverlayTask.execute(mRoute);
					}
									
					mHandler.postDelayed(mRunnable, mUpdateInverval);
				}
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mMapView = (MapView)findViewById(R.id.map);
        mMapController = mMapView.getMapController();        
        mOverlayManager = mMapController.getOverlayManager(); 
        
        mMapView.showZoomButtons(true);
        mMapView.showFindMeButton(true);
        mMapView.showJamsButton(false);
        
        mMapController.setPositionNoAnimationTo(new GeoPoint(54.315575, 48.442000), 9);                    
        
        mRouteOverlay = new RouteOverlay(mMapController);
        mOverlayManager.addOverlay(mRouteOverlay);
        
        mBusOverlay = new BusOverlay(mMapController);
        mOverlayManager.addOverlay(mBusOverlay);       
        
        mRouteButton = (Button)findViewById(R.id.route_button);
        mRouteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_PICK_A_ROUTE_ID);
			}
		});
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	int interval = Integer.parseInt(preferences.getString("update_interval", "2"));
    	mUpdateInverval = interval * 1000;
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
    	setRoute(mRoute);
    }
    
    @Override
	protected void onPause() {    	    	
    	mHandler.removeCallbacks(mRunnable);
    	super.onPause();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);  
        
        double longitude = savedInstanceState.getDouble("longitude");
        double latitude = savedInstanceState.getDouble("latitude");
        
        mMapController.setPositionNoAnimationTo(new GeoPoint(latitude, longitude));
        mMapController.setZoomCurrent(savedInstanceState.getFloat("zoom"));
        
        mRoute = savedInstanceState.getString("route");
        mCookie = savedInstanceState.getString("cookie");        
                
        ArrayList<BusItem> busItems = savedInstanceState.getParcelableArrayList("bus_items");
        for (BusItem item : busItems)
			mBusOverlay.addOverlayItem(item);	
        
        ArrayList<RouteItem> routeItems = savedInstanceState.getParcelableArrayList("route_items");
        for (RouteItem item : routeItems)
        	mRouteOverlay.addOverlayItem(item);
	    	    
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	
    	GeoPoint position = mMapController.getGeoPoint(new ScreenPoint(mMapController.getMapViewWidth() * 0.5f, mMapController.getMapViewHeight() * 0.5f));
        outState.putDouble("longitude", position.getLon());
        outState.putDouble("latitude", position.getLat());
        outState.putFloat("zoom", mMapController.getZoomCurrent());
        outState.putString("route", mRoute);
        outState.putString("cookie", mCookie);               
        
        @SuppressWarnings("unchecked")
		ArrayList<BusItem> busItems = new ArrayList<BusItem>(mBusOverlay.getOverlayItems());     
        outState.putParcelableArrayList("bus_items", busItems);
        
        @SuppressWarnings("unchecked")
		ArrayList<RouteItem> routeItems = new ArrayList<RouteItem>(mRouteOverlay.getOverlayItems());     
        outState.putParcelableArrayList("route_items", routeItems);
                        
        super.onSaveInstanceState(outState);               
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.preferences:
	        	Intent intent = new Intent(this,  Preferences.class);
	        	startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    static final int DIALOG_PICK_A_ROUTE_ID = 0;
    
    protected Dialog onCreateDialog(int id) {
    	
    	final CharSequence[] items = {	"2", "3", "12", "15", "19", "28", "31", "34", "43", "44",
    									"47", "52", "55", "56", "59", "65", "67", "68", "69", "71",
    									"74", "75", "81", "82", "85", "88", "90", "91", "96", "100" };
    	
        Dialog dialog = null;
        switch(id) {
	        case DIALOG_PICK_A_ROUTE_ID:
	        	dialog = new AlertDialog.Builder(this)
	        	.setTitle(R.string.pick_a_route)
	        	.setItems(items, 
	        			new DialogInterface.OnClickListener() {
	        	    		public void onClick(DialogInterface dialog, int item) {
	        	    			MainActivity.this.setRoute(items[item].toString());	        	    			
	        	    		}
	        		})
	        	.create();
	            break;
        }

        return dialog;
    }
}