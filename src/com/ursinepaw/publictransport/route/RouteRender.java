package com.ursinepaw.publictransport.route;

import java.util.ArrayList;

import ru.yandex.yandexmapkit.overlay.IRender;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class RouteRender implements IRender {
	private Paint	mPaint = null;
	
	public RouteRender() {	    
	    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);			    		
		mPaint.setStrokeWidth(4);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setColor(Color.rgb(54, 124, 220));
		mPaint.setShadowLayer(1, 0, 0, Color.BLACK);
	    mPaint.setAntiAlias(true);
	}
	
	protected double distance(ScreenPoint pt1, ScreenPoint pt2) {
		double dx = pt2.getX() - pt1.getX();
		double dy = pt2.getY() - pt1.getY();
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	@Override
	public void draw(Canvas canvas, OverlayItem item) {		
		ArrayList<RouteItem.LinePoint> points = ((RouteItem)item).getPoints();
		
		Path path = null;
		ScreenPoint pt = null;
		for (RouteItem.LinePoint point : points) {
			if (path == null) {
				path = new Path();
				pt = point.screenPoint;
				path.moveTo(pt.getX(), pt.getY());
				
			}
			else if (distance(point.screenPoint, pt) > 5) {
					pt = point.screenPoint;				
					path.lineTo(pt.getX(), pt.getY());
				}
		}
		canvas.drawPath(path, mPaint);	 	 
	}
}
