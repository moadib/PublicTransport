package com.ursinepaw.publictransport.bus;

import java.util.Iterator;
import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;


public class BusOverlay extends Overlay {	
	private BusRender mRender = new BusRender();
	
	public BusOverlay(MapController controller) {
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
	    	OverlayItem localOverlayItem = (OverlayItem)localIterator.next();
	    	try {
	    		if (!localOverlayItem.isVisible())
	    			continue;
	    		ScreenPoint localScreenPoint = this.c.getScreenPoint(localOverlayItem.getPoint());
	    		localOverlayItem.setScreenPoint(localScreenPoint);
	    	}
	    	catch (Exception e) {
	    	}
	    	this.b.add(localOverlayItem);
	    }
	    return getPrepareDrawList();
	}
}
