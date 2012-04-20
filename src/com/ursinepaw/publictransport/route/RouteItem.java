package com.ursinepaw.publictransport.route;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class RouteItem extends OverlayItem implements Parcelable {
	
	public class LinePoint {
		public GeoPoint 	geoPoint;
		public ScreenPoint	screenPoint;
		
		public LinePoint(double lon, double lat) {
			geoPoint = new GeoPoint(lon, lat);
		}

		public LinePoint(GeoPoint point) {
			geoPoint = point;
		}
	}
	
	private ArrayList<LinePoint>	points = new ArrayList<LinePoint>();
	
	public ArrayList<LinePoint> getPoints() {
		return points;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RouteItem createFromParcel(Parcel in) {
            return new RouteItem(in);
        }
 
        public RouteItem[] newArray(int size) {
            return new RouteItem[size];
        }
    };
	
	public RouteItem() {
		super(new GeoPoint(0.0, 0.0), null);	
	}
	
	public RouteItem(Parcel in) {
		super(new GeoPoint(0.0, 0.0), null);
		GeoPoint[] geoPoints = (GeoPoint[])in.readParcelableArray(GeoPoint.class.getClassLoader());
		for (GeoPoint point : geoPoints)
			points.add(new LinePoint(point));
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		GeoPoint geoPoints[] = new GeoPoint[points.size()];
		int i = 0;
		for (LinePoint point : points) {
			geoPoints[i] = point.geoPoint;
			i++;
		}
		dest.writeParcelableArray(geoPoints, 0);
	}
	
	public void addPoint(double lon, double lat) {
		points.add(new LinePoint(lon, lat));
	}

}
