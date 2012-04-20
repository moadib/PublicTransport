package com.ursinepaw.publictransport.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;

public class RouteOverlay extends Overlay {	
	private RouteRender mRender = new RouteRender();
	
	public RouteOverlay(MapController controller) {
		super(controller);
		setIRender(mRender);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List prepareDraw()
	{
		this.b.clear();
	    if (!isVisible())
	    	return this.b;
	   
		Iterator localIterator = this.a.iterator();
	    while (localIterator.hasNext()) {
	    	OverlayItem item = (OverlayItem)localIterator.next();
	    	try {
	    		if (!item.isVisible())
	    			continue;
	    		
	    		ArrayList<RouteItem.LinePoint> points = ((RouteItem)item).getPoints();
	    		for (RouteItem.LinePoint point : points)
	    			point.screenPoint = this.c.getScreenPoint(point.geoPoint);
	    	}
	    	catch (Exception e) {
	    	}
	    	this.b.add(item);
	    }
	    return getPrepareDrawList();
	}
	
}
