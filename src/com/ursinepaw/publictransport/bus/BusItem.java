package com.ursinepaw.publictransport.bus;

import android.os.Parcel;
import android.os.Parcelable;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class BusItem extends OverlayItem implements Parcelable {

	private float mAngle = 0.f;
	
	public float getAngle() {
		return mAngle;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BusItem createFromParcel(Parcel in) {
            return new BusItem(in);
        }
 
        public BusItem[] newArray(int size) {
            return new BusItem[size];
        }
    };
	
	public BusItem(GeoPoint point, float angle) {
		super(point, null);	
		mAngle = angle;
		
		setPriority((byte)1);	// to render over route
	}
	
	public BusItem(Parcel in) {
		super(new GeoPoint(0.0, 0.0), null);
		GeoPoint point = (GeoPoint)in.readParcelable(GeoPoint.class.getClassLoader());		
		setGeoPoint(point);		
		mAngle = in.readFloat();
		
		setPriority((byte)1);	// to render over route
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(a, 0);
		dest.writeFloat(mAngle);
	}
	
}
