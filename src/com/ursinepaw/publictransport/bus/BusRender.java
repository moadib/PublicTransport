package com.ursinepaw.publictransport.bus;

import ru.yandex.yandexmapkit.overlay.IRender;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class BusRender implements IRender {
	private Path 	mPath = null;
	private Paint	mPaint = null;
	
	public BusRender() {
		mPath = new Path();	   
		mPath.moveTo(0, 3);
		mPath.lineTo(6, 6);
	    mPath.lineTo(0, -6);
	    mPath.lineTo(-6, 6);	    
	    mPath.close();
	    
	    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);			    		
		mPaint.setStrokeWidth(1);	    
	    mPaint.setAntiAlias(true);
	}
	
	@Override
	public void draw(Canvas canvas, OverlayItem item) {
		BusItem busItem = (BusItem)item;
		ScreenPoint pt = busItem.getScreenPoint();
		float angle = busItem.getAngle();
		
		canvas.save();	    	    
	    canvas.translate(pt.getX(), pt.getY());
	    canvas.rotate(angle);
	    mPaint.setStyle(Paint.Style.FILL);
	    mPaint.setColor(android.graphics.Color.RED);
	    canvas.drawPath(mPath, mPaint);	
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setColor(Color.rgb(150, 0, 0));
	    canvas.drawPath(mPath, mPaint);	
	    canvas.restore();	    	  
	}
}
