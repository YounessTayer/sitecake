package com.sitecake.contentmanager.client.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.container.ContentContainer;
import com.sitecake.contentmanager.client.event.NewItemEvent;
import com.sitecake.contentmanager.client.item.HasContentItemCreator;
import com.sitecake.contentmanager.client.toolbar.ContentItemCreator;

public class ContentContainerDropController extends
		AbstractInsertPanelDropController {

	private EventBus eventBus;
	
	public class Positioner extends HTML {

		public Positioner() {
			super();
		}
		
	}
	
	/**
	* @param dropTarget
	*/
	public ContentContainerDropController(EventBus eventBus, ContentContainer dropTarget) {
		super(dropTarget);
		this.eventBus = eventBus;
	}
	
	@Override
	protected LocationWidgetComparator getLocationWidgetComparator() {
		return LocationWidgetComparator.BOTTOM_HALF_COMPARATOR;
	}
	
	@Override
	protected Widget newPositioner(DragContext context) {
		Positioner positioner = new Positioner();
		positioner.addStyleName(DragClientBundle.INSTANCE.css().flowPanelPositioner());
		return positioner;
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		
		if ( context.dragController instanceof ToolbarDragController ||
				context.dragController instanceof DnDUploadDragController ) {
			
			HasContentItemCreator newItem = (HasContentItemCreator)context.selectedWidgets.get(0);

			ContentItemCreator contentItemCreator = newItem.getContentItemCreator();
			ContentContainer container = (ContentContainer)dropTarget;
			int index = getDropIndex();
			
			eventBus.fireEvent(new NewItemEvent(contentItemCreator, container, index));
			throw new VetoDragException();
			
		} else {
			super.onPreviewDrop(context);
		}
		
	}
	
	private int getDropIndex() {
		int index = -1;
		
		for ( int i = 0; i < dropTarget.getWidgetCount(); i++ ) {
			if ( dropTarget.getWidget(i) instanceof Positioner ) {
				index = i;
				break;
			}
		}
		
		return index;
	}

}
