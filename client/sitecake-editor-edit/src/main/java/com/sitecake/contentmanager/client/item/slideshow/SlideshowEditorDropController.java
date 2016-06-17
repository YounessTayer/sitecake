package com.sitecake.contentmanager.client.item.slideshow;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.commons.client.util.Console;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class SlideshowEditorDropController extends AbstractInsertPanelDropController {

	public class Positioner extends HTML {

		public Positioner() {
			super();
		}
		
	}
	
	/**
	* @param dropTarget
	*/
	public SlideshowEditorDropController(InsertPanel dropTarget) {
		super(dropTarget);
	}
	
	@Override
	protected LocationWidgetComparator getLocationWidgetComparator() {
		return new LocationWidgetComparator() {
			public boolean locationIndicatesIndexFollowingWidget(Area widgetArea, Location location) {
				double w = widgetArea.getWidth();
				double h = widgetArea.getHeight();
				double x = location.getLeft() - widgetArea.getLeft();
				double y = widgetArea.getBottom() - location.getTop();
				
				boolean leftTop = y > ( x * h / w );
				boolean rightTop = y > ( h - x * h / w );
				boolean left = x < ( w / 2 );
				
				Console.log("leftTop:" + leftTop, "rightTop:" + rightTop, "left:" + left);
				
				// left triangle
				if ( left && leftTop && !rightTop ) {
					return true;
				}
				// left upper triangle
				else if ( left && leftTop && rightTop ) {
					return false;
				}
				// left bottom triangle
				else if ( left && !leftTop && !rightTop ) {
					return false;
				}
				// right triangle
				else if ( !left && !leftTop && rightTop ) {
					return false;
				}
				// right upper triangle
				else if ( !left && leftTop && rightTop ) {
					return true;
				}
				// right bottom triangle
				else if ( !left && !leftTop && !rightTop ) {
					return true;
				}
				
				return false;
			}
		};
	}
	
	@Override
	protected Widget newPositioner(DragContext context) {
		Positioner positioner = new Positioner();
		positioner.addStyleName(EditorClientBundle.INSTANCE.css().slideshowEditorDropPositioner());
		return positioner;
	}
	
}