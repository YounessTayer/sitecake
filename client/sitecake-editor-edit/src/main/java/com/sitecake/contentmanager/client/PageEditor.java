package com.sitecake.contentmanager.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.WidgetArea;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.container.ContentContainer;
import com.sitecake.contentmanager.client.container.ContentContainerFactory;
import com.sitecake.contentmanager.client.content.ContentManager;
import com.sitecake.contentmanager.client.contextmenu.ContextMenu;
import com.sitecake.contentmanager.client.dnd.DnDUploadDragController;
import com.sitecake.contentmanager.client.dnd.ItemDragController;
import com.sitecake.contentmanager.client.dnd.ToolbarDragController;
import com.sitecake.contentmanager.client.editable.Selection;
import com.sitecake.contentmanager.client.event.CancelEvent;
import com.sitecake.contentmanager.client.event.CancelHandler;
import com.sitecake.contentmanager.client.event.DeleteEvent;
import com.sitecake.contentmanager.client.event.DeleteHandler;
import com.sitecake.contentmanager.client.event.EditCompleteEvent;
import com.sitecake.contentmanager.client.event.EditCompleteHandler;
import com.sitecake.contentmanager.client.event.EditItemEvent;
import com.sitecake.contentmanager.client.event.EditItemHandler;
import com.sitecake.contentmanager.client.event.EditorHistoryChangeEvent;
import com.sitecake.contentmanager.client.event.EndEditingEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.event.ErrorNotificationHandler;
import com.sitecake.contentmanager.client.event.LogoutEvent;
import com.sitecake.contentmanager.client.event.LogoutHandler;
import com.sitecake.contentmanager.client.event.MergeParagraphEvent;
import com.sitecake.contentmanager.client.event.MergeParagraphHandler;
import com.sitecake.contentmanager.client.event.MoveEvent;
import com.sitecake.contentmanager.client.event.MoveEvent.Direction;
import com.sitecake.contentmanager.client.event.MoveHandler;
import com.sitecake.contentmanager.client.event.NewItemEvent;
import com.sitecake.contentmanager.client.event.NewItemHandler;
import com.sitecake.contentmanager.client.event.NewParagraphEvent;
import com.sitecake.contentmanager.client.event.NewParagraphHandler;
import com.sitecake.contentmanager.client.event.OverItemEvent;
import com.sitecake.contentmanager.client.event.OverItemHandler;
import com.sitecake.contentmanager.client.event.PageManagerEvent;
import com.sitecake.contentmanager.client.event.PageManagerHandler;
import com.sitecake.contentmanager.client.event.PostEditingEndEvent;
import com.sitecake.contentmanager.client.event.PostEditingEndHandler;
import com.sitecake.contentmanager.client.event.PublishEvent;
import com.sitecake.contentmanager.client.event.PublishHandler;
import com.sitecake.contentmanager.client.event.RedoEvent;
import com.sitecake.contentmanager.client.event.RedoHandler;
import com.sitecake.contentmanager.client.event.SaveRequestEvent;
import com.sitecake.contentmanager.client.event.SaveRequestHandler;
import com.sitecake.contentmanager.client.event.SelectEvent;
import com.sitecake.contentmanager.client.event.SelectHandler;
import com.sitecake.contentmanager.client.event.StartEditingEvent;
import com.sitecake.contentmanager.client.event.TextBlockEvent;
import com.sitecake.contentmanager.client.event.TextBlockHandler;
import com.sitecake.contentmanager.client.event.UndoEvent;
import com.sitecake.contentmanager.client.event.UndoHandler;
import com.sitecake.contentmanager.client.event.UploadEvent;
import com.sitecake.contentmanager.client.event.UploadHandler;
import com.sitecake.contentmanager.client.history.EditorHistory;
import com.sitecake.contentmanager.client.history.ParallelTransformation;
import com.sitecake.contentmanager.client.history.Position;
import com.sitecake.contentmanager.client.history.SerialTransformation;
import com.sitecake.contentmanager.client.history.SimpleTransformation;
import com.sitecake.contentmanager.client.history.Transformation;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.html.HtmlItem;
import com.sitecake.contentmanager.client.item.map.MapItem;
import com.sitecake.contentmanager.client.item.text.TextItem;
import com.sitecake.contentmanager.client.item.video.VideoItem;
import com.sitecake.contentmanager.client.pages.PageManager;
import com.sitecake.contentmanager.client.properties.PropertyManager;
import com.sitecake.contentmanager.client.resources.LocaleProxy;
import com.sitecake.contentmanager.client.resources.Messages;
import com.sitecake.contentmanager.client.select.LassoSelectorController;
import com.sitecake.contentmanager.client.select.SelectContext;
import com.sitecake.contentmanager.client.select.SelectorHandler;
import com.sitecake.contentmanager.client.select.VetoSelectException;
import com.sitecake.contentmanager.client.session.SessionManager;
import com.sitecake.contentmanager.client.toolbar.ContentItemCreator;
import com.sitecake.contentmanager.client.toolbar.ContentManagerToolbar;
import com.sitecake.contentmanager.client.trashbin.TrashBin;
import com.sitecake.contentmanager.client.upload.UploadManager;

public class PageEditor implements DeleteHandler, EditItemHandler, OverItemHandler,
	UndoHandler, RedoHandler, CancelHandler, SelectorHandler, SelectHandler, MoveHandler,
	NewItemHandler, UploadHandler, LogoutHandler, PublishHandler, EditCompleteHandler,
	NewParagraphHandler, MergeParagraphHandler, ErrorNotificationHandler, TextBlockHandler,
	PostEditingEndHandler, PageManagerHandler, SaveRequestHandler {
	
	private class TransformationState {
		private ContentItem item;
		private Position position;
		
		public ContentItem getItem() {
			return item;
		}

		public Position getPosition() {
			return position;
		}

		public TransformationState(ContentItem item, Position position) {
			super();
			this.item = item;
			this.position = position;
		}
	}
	
	/**
	 * Items from left-more containers and top-more within container are considered smaller.
	 */
	public class ItemOrderComparator implements Comparator<Widget> {

		@Override
		public int compare(Widget o1, Widget o2) {
			int result;
			int o1ParentLeft = o1.getParent().getAbsoluteLeft();
			int o2ParentLeft = o2.getParent().getAbsoluteLeft();
			int o1Top = o1.getAbsoluteTop();
			int o2Top = o2.getAbsoluteTop();
			
			if ( o1ParentLeft < o2ParentLeft ) {
				result = -1;
			} else if ( o1ParentLeft > o2ParentLeft ) {
				result = 1;
			} else {
				if ( o1Top < o2Top ) {
					result = -1;
				} else if ( o1Top > o2Top ) {
					result = 1;
				} else {
					result = 0;
				}
			}
			
			return result;
		}
		
	}
	
	private enum EditPhase {
		IDLE,
		SELECTING,
		DRAGGING_ITEM,
		DRAGGING_NEW_ITEM,
		EDITING_ITEM
	}
	
	private EditPhase editPhase;
	
	private EventBus eventBus;
	
	private boolean fatalOccured = false;
	
	private List<ContentContainer> containers;
	
	private ItemDragController dragController;
	
	private ToolbarDragController toolbarDragController;
	
	private DnDUploadDragController uploadDragController;
	
	private ContextMenu contextMenu = GinInjector.instance.getContextMenu();
	
	private EditorHistory history;
	
	private Map<ContentItem, TransformationState> dragSrcStates;
	
	private LassoSelectorController selectController;
	
	private List<ContentItem> selectedItems;
	
	private ItemOrderComparator itemOrderComparator;
	
	private TrashBin trashBin;
	
	private PropertyManager propertyManager;
	
	private ConfigRegistry configRegistry;
	
	private ContentItem editedItem;
	
	private String editMode;
	
	private TransformationState startEditState;
	
	private ContentManager contentManager = GinInjector.instance.getContentManager();
	
	private UploadManager uploadManager = GinInjector.instance.getUploadManager();
	
	private SessionManager sessionManager = GinInjector.instance.getSessionManager();
	
	private Selection selection = GinInjector.instance.getSelection();
	
	private Messages messages;
	
	private boolean pageManagerActive;
	private PageManager pageManager;
	
	@Inject
	public PageEditor(EventBus eventBus, ContentContainerFactory containerFactory, EditorHistory history,
			KeyboardController keyboardController, PropertyManager propertyManager, ConfigRegistry configRegistry, ContentManagerToolbar toolbar,
			LocaleProxy localeProxy, TopContainer topContainer) {
		
		this.propertyManager = propertyManager;
		this.configRegistry = configRegistry;
		this.messages = localeProxy.messages();
		
		this.eventBus = eventBus;
		eventBus.addHandler(DeleteEvent.getType(), this);
		eventBus.addHandler(CancelEvent.getType(), this);
		eventBus.addHandler(EditItemEvent.getType(), this);
		eventBus.addHandler(OverItemEvent.getType(), this);
		eventBus.addHandler(UndoEvent.getType(), this);
		eventBus.addHandler(RedoEvent.getType(), this);
		eventBus.addHandler(SelectEvent.getType(), this);
		eventBus.addHandler(MoveEvent.getType(), this);
		eventBus.addHandler(NewItemEvent.getType(), this);
		eventBus.addHandler(UploadEvent.getType(), this);
		eventBus.addHandler(LogoutEvent.getType(), this);
		eventBus.addHandler(PublishEvent.getType(), this);
		eventBus.addHandler(EditCompleteEvent.getType(), this);
		eventBus.addHandler(MergeParagraphEvent.getType(), this);
		eventBus.addHandler(NewParagraphEvent.getType(), this);
		eventBus.addHandler(ErrorNotificationEvent.getType(), this);
		eventBus.addHandler(TextBlockEvent.getType(), this);
		eventBus.addHandler(PostEditingEndEvent.getType(), this);
		eventBus.addHandler(PageManagerEvent.getType(), this);
		eventBus.addHandler(SaveRequestEvent.getType(), this);
		
		Window.addWindowClosingHandler(new ClosingHandler() {
			
			@Override
			public void onWindowClosing(ClosingEvent event) {
				if ( !checkEndCondition() ) {
					event.setMessage(PageEditor.this.messages.confirmUnsafeLogout());
				}
			}
		});
		
		
		this.history = history;
		
		dragController = new ItemDragController(RootPanel.get(), false);
		
		if ( this.configRegistry.getBoolean("trashBin.show", true) ) {
			trashBin = new TrashBin(eventBus, this.propertyManager);
			topContainer.add(trashBin);
			dragController.registerPriorityDropController(trashBin);
		}
		
		initItemDrag();
		initContainers(containerFactory);
		
		selectController = GinInjector.instance.getLassoSelectorController();
		selectController.setSelectHandler(this);
		selectedItems = new ArrayList<ContentItem>();
		
		editPhase = EditPhase.IDLE;
		pageManagerActive = false;
		
		itemOrderComparator = new ItemOrderComparator();
		
		topContainer.add(toolbar);
		toolbar.setDragController(toolbarDragController);
		
		pageManager = GinInjector.instance.getPageManager();
		RootPanel.get().add(pageManager);
	}

	private void initContainers(ContentContainerFactory containerFactory) {
		containers = containerFactory.create();

		for ( ContentContainer container : containers ) {
			container.setDragController(dragController);
		}
		
		for ( ContentContainer container : containers ) {
			DropController dropCtrl = container.getDropController();
			toolbarDragController.registerDropController(dropCtrl);
		}
		
		for ( ContentContainer container : containers ) {
			DropController dropCtrl = container.getDropController();
			uploadDragController.registerDropController(dropCtrl);
		}
		
	}
	
	private void initItemDrag() {
		dragSrcStates = new HashMap<ContentItem, TransformationState>();

		dragController.addDragHandler(new DragHandler() {

			public void onDragEnd(DragEndEvent event) {
				onItemDragEnd(event);
			}

			public void onDragStart(DragStartEvent event) {
				onItemDragStart(event);
			}

			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				onItemDragEndPreview(event);
			}

			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
				onItemDragStartPreview(event);
			}
			
		});
		
		toolbarDragController = new ToolbarDragController(RootPanel.get(), false);
		
		toolbarDragController.addDragHandler(new DragHandler() {

			public void onDragEnd(DragEndEvent event) {
			}

			public void onDragStart(DragStartEvent event) {
				onToolbarDragStart(event);
			}

			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				onToolbarDragEnd(event);
			}

			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
			}
			
		});
		
		uploadDragController = GinInjector.instance.getDnDUploadDragController();
		uploadDragController.addDragHandler(new DragHandler() {
			public void onDragStart(DragStartEvent event) {
				switchToNewEditPhase(EditPhase.DRAGGING_NEW_ITEM);
			}

			public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				switchToNewEditPhase(EditPhase.IDLE);
			}

			public void onDragEnd(DragEndEvent event) {}
			public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {}
		});		
	}
	
	private void onItemDragStartPreview(DragStartEvent event) throws VetoDragException {
		if ( !isActionExecutable() ) throw new VetoDragException();
		
		if ( editPhase.equals(EditPhase.SELECTING) || editPhase.equals(EditPhase.DRAGGING_NEW_ITEM) ) {
			throw new VetoDragException();
		}
	}
	
	private void onItemDragStart(DragStartEvent event) {
		switchToNewEditPhase(EditPhase.DRAGGING_ITEM);
		
		extendContainers(true);
		dragController.hideSelectedWidgetsStyle();
		
		// remember origins of all selected items for history purposes
		// first, clear any previous positions
		dragSrcStates.clear();
		
		// than obtain the list of selected items
		List<Widget> selectedWidgets = event.getContext().selectedWidgets;
		
		// deselect any item that doesn't belong to the selectedWidgets
		ArrayList<ContentItem> forRemoval = new ArrayList<ContentItem>();
		for ( ContentItem selectedItem : selectedItems ) {
			if ( !selectedWidgets.contains(selectedItem) ) {
				forRemoval.add(selectedItem);
			}
		}
		for ( ContentItem itemForRemoval : forRemoval ) {
			unselectItem(itemForRemoval);
		}
		
		// and for every selected item remember/create its initial state
		// and signal it that it is being a dragging subject
		for ( Widget widget : selectedWidgets ) {
			ContentItem item = (ContentItem)widget;
			ContentItem clone = item.cloneItem();
			ContentContainer container = item.getContainer();
			int index = container.getItemIndex(item);
			dragSrcStates.put(item, new TransformationState(clone, new Position(container, index)));
			
			item.startDragging();
		}
	}
	
	private void onItemDragEndPreview(DragEndEvent event) throws VetoDragException {
		//List<Widget> selectedWidgets = event.getContext().selectedWidgets;
	}
	
	private void onItemDragEnd(DragEndEvent event) {
		
		extendContainers(false);
		
		if ( event.getContext().finalDropController == null ) {
			// a canceled drop
			switchToNewEditPhase(EditPhase.IDLE);
			return;
		}
		
		// obtain the list of selected items
		List<Widget> selectedWidgets = event.getContext().selectedWidgets;
		
		// prepare the group move transformation
		ParallelTransformation<ContentItem> moveTransformation = new ParallelTransformation<ContentItem>();

		// signals whether all selected remain their original position
		boolean unchangedPosition = true;
		
		for ( Widget widget : selectedWidgets ) {
			// for every selected item
			ContentItem item = (ContentItem)widget;
			
			// signal it that the dragging is ended
			item.stopDragging();
			
			// create the destination position
			ContentContainer container = item.getContainer();
			int index = container.getItemIndex(item);
			Position dstPosition = new Position(container, index);
			
			// obtain the initial state of item
			TransformationState srcState = dragSrcStates.get(item);

			// if the position changes of an item is changed, remember it
			if ( !dstPosition.equals(srcState.getPosition()) ) {
				unchangedPosition = false;
			}
			
			moveTransformation.add(new SimpleTransformation<ContentItem>(srcState.getItem(), srcState.getPosition(), item.cloneItem(), dstPosition));
		}
		
		// if the group transformation is not empty
		if ( !unchangedPosition && !moveTransformation.getTransformations().isEmpty() ) {
			// save the transformation
			history.put(moveTransformation);
			save();
		}
		
		switchToNewEditPhase(EditPhase.IDLE);
	}

	private void onToolbarDragStart(DragStartEvent event) {
		switchToNewEditPhase(EditPhase.DRAGGING_NEW_ITEM);
	}
	
	private void onToolbarDragEnd(DragEndEvent event) {
		switchToNewEditPhase(EditPhase.IDLE);
	}
	
	public void onEditItem(EditItemEvent event) {
		closeCurrentEditPhase();
		
		if ( event.getItem().isEditable() ) {
			editedItem = event.getItem();
			editMode = event.getEditMode();
			startNewEditPhase(EditPhase.EDITING_ITEM);
		}
	}

	public void onDelete(DeleteEvent event) {
		if ( !isActionExecutable() ) return;

		//if ( editPhase.equals(EditPhase.EDITING_ITEM)) return;
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				contextMenu.hide(true);
			}
		});	
		
		ContentItem[] items = event.getItems();
		
		if ( items != null ) {
			
			ParallelTransformation<ContentItem> deleteTransformation = new ParallelTransformation<ContentItem>();
			List<ContentItem> forRemoval = new ArrayList<ContentItem>();
			
			for ( ContentItem item : items ) {
				ContentContainer container = item.getContainer();
				// check if the element is not already removed
				if ( container != null ) {
					int index = container.getItemIndex(item);
					
					unselectItem(item);
					forRemoval.add(item);
					
					Position position = new Position(container, index);
					SimpleTransformation<ContentItem> transformation = new SimpleTransformation<ContentItem>(item.cloneItem(), position, null, null);
					deleteTransformation.add(transformation);
				}
			}
			
			for ( ContentItem item : forRemoval ) {
				item.onDeletion();
				item.getContainer().removeItem(item);
			}
			
			if ( deleteTransformation.getTransformations().size() > 1 ) {
				history.put(deleteTransformation);
			} else if ( deleteTransformation.getTransformations().size() > 0 ) {
				history.put(deleteTransformation.getTransformations().get(0));
			}
			
		} else if ( selectedItems.size() > 0 ) {
			ParallelTransformation<ContentItem> deleteTransformation = new ParallelTransformation<ContentItem>();
			for ( ContentItem selectedItem : selectedItems ) {
				ContentContainer container = selectedItem.getContainer();
				int index = container.getItemIndex(selectedItem);
				Position position = new Position(container, index);
				SimpleTransformation<ContentItem> transformation = new SimpleTransformation<ContentItem>(selectedItem.cloneItem(), position, null, null);
				deleteTransformation.add(transformation);
			}
			history.put(deleteTransformation);
			for ( ContentItem selectedItem : selectedItems ) {
				selectedItem.onDeletion();
				ContentContainer container = selectedItem.getContainer();
				container.removeItem(selectedItem);
			}
			unselectAllItems();
		}
		
		switchToNewEditPhase(EditPhase.IDLE);
		save();
	}
	
	@Override
	public void onOverItem(OverItemEvent event) {
		if ( !isActionExecutable() ) return;

		if ( ( editPhase.equals(EditPhase.IDLE) || editPhase.equals(EditPhase.EDITING_ITEM) ) && !event.getItem().equals(editedItem) ) {
			contextMenu.show(event.getItem());
		}
	}

	@Override
	public void onSaveRequest(SaveRequestEvent event) {
		if (editPhase.equals(EditPhase.IDLE)) {
			save();
		}
	}

	private void save() {
		if ( !isActionExecutable() ) return;

		Map<String, String> content = new HashMap<String, String>();
		for ( ContentContainer container : containers ) {
			String html = container.getHtml();
			html = ( html == null ) ? "" : html;
			content.put(container.getName(), html);
		}
		
		contentManager.save(content);
		eventBus.fireEvent(new EditorHistoryChangeEvent(history.isAtBegining(), history.isAtEnd()));		
	}
	
	private void undo() {
		if ( !history.isAtBegining() ) {
			switchToNewEditPhase(EditPhase.IDLE, true);
			contextMenu.hide(true);
			unselectAllItems();
			Transformation transformation = history.undo();
			transformation(transformation, true);
			save();
		}
	}
	
	private void redo() {
		if ( !history.isAtEnd() ) {
			switchToNewEditPhase(EditPhase.IDLE, true);
			contextMenu.hide(true);
			unselectAllItems();
			Transformation transformation = history.redo();
			transformation(transformation, false);
			save();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void transformation(Transformation transformation, boolean isUndo) {
		if ( transformation instanceof SimpleTransformation )
			simpleTransformation((SimpleTransformation<ContentItem>)transformation, isUndo);
		else if ( transformation instanceof ParallelTransformation )
			parallelTransformation((ParallelTransformation<ContentItem>)transformation, isUndo);
		else if ( transformation instanceof SerialTransformation )
			serialTransformation((SerialTransformation)transformation, isUndo);
		else
			throw new IllegalArgumentException();
	}
	
	private void serialTransformation(SerialTransformation transformation, boolean isUndo) {
		List<Transformation> transformations = transformation.getTransformations();
		if ( isUndo ) {
			// in case of an undo operation, transformation steps are processed inversely
			for ( int i=transformations.size()-1; i>=0; i--) {
				Transformation trans = transformations.get(i);
				transformation(trans, isUndo);
			}
		} else {
			// in case of a redo operation, transformation steps are processed in direct order
			for ( Transformation trans : transformations ) {
				transformation(trans, isUndo);
			}
		}
	}
	
	private void parallelTransformation(ParallelTransformation<ContentItem> transformation, boolean isUndo) {
		List<SimpleTransformation<ContentItem>> transformations = transformation.getTransformations();
		
		// a list of items/positions that have to be removed (from the current state of containers)
		List<TransformationState> forRemoval = new ArrayList<TransformationState>();
		// a list of items/positions that have to be added (to the current state of containers)		
		List<TransformationState> forInsertion = new ArrayList<TransformationState>();

		// fill the forRemoval and forInsertion lists
		for ( SimpleTransformation<ContentItem> trans : transformations ) {
			ContentItem currentItem = isUndo ? trans.getNewItem() : trans.getOldItem();
			ContentItem newItem = isUndo ? trans.getOldItem() : trans.getNewItem();
			Position currentPosition = isUndo ? trans.getNewPosition() : trans.getOldPosition();
			Position newPosition = isUndo ? trans.getOldPosition() : trans.getNewPosition();
			
			if ( currentItem != null ) {
				ContentContainer container = currentPosition.getContainer();
				int index = currentPosition.getIndex();
				forRemoval.add(new TransformationState(container.getItemAt(index), currentPosition));
			}
			
			if ( newItem != null ) {
				forInsertion.add(new TransformationState(newItem, newPosition));
			}
		}

		// first remove all items from the removal list
		for ( TransformationState state : forRemoval ) {
			state.getPosition().getContainer().remove(state.getItem());
		}
		
		// sort the insertion list by item index in ascending order, so the new items
		// are inserted in correct positions
		Collections.sort(forInsertion, new Comparator<TransformationState>() {
			public int compare(TransformationState o1, TransformationState o2) {
				return (o1.getPosition().getIndex() - o2.getPosition().getIndex());
			}
		});
		// than insert the items from the insertion list
		for ( TransformationState state : forInsertion ) {
			state.getPosition().getContainer().insert(state.getItem(), state.getPosition().getIndex());
		}
	}

	private void simpleTransformation(SimpleTransformation<ContentItem> transformation, boolean isUndo) {
		ContentItem currentItem = isUndo ? transformation.getNewItem() : transformation.getOldItem();
		ContentItem newItem = isUndo ? transformation.getOldItem() : transformation.getNewItem();
		Position currentPosition = isUndo ? transformation.getNewPosition() : transformation.getOldPosition();
		Position newPosition = isUndo ? transformation.getOldPosition() : transformation.getNewPosition();
		
		if ( currentItem != null ) {
			ContentContainer container = currentPosition.getContainer();
			container.remove(currentPosition.getIndex());
		}
		
		if ( newItem != null ) {
			newPosition.getContainer().insert(newItem, newPosition.getIndex());
		}
	}

	@Override
	public void onCancel(CancelEvent event) {
		if (pageManagerActive) {
			pageManagerActive = false;
			pageManager.close();
			showBodyElements();
		} else {
			if ( !isActionExecutable() ) return;
			
			switchToNewEditPhase(EditPhase.IDLE, true);
			unselectAllItems();
		}
	}

	@Override
	public void onRedo(RedoEvent event) {
		if ( !isActionExecutable() ) return;
		
		redo();
	}

	@Override
	public void onUndo(UndoEvent event) {
		if ( !isActionExecutable() ) return;
		
		undo();
	}

	@Override
	public void onSelectDrag(SelectContext context) {
		if ( !isActionExecutable() ) return;

		Area selectArea = context.getCurrentArea();
		for ( ContentContainer container : containers ) {
			for ( Widget widget : container ) {
				Area widgetArea = new WidgetArea(widget, RootPanel.get());
				if ( selectArea.intersects(widgetArea) ) {
					selectItem((ContentItem)widget);
				} else {
					unselectItem((ContentItem)widget);
				}
			}
		}
	}

	@Override
	public void onSelectEnd(SelectContext context) {
		// re-select widgets, but this time in a proper order
		Collections.sort(selectedItems, itemOrderComparator);
		dragController.clearSelection();
		for ( ContentItem item : selectedItems ) {
			dragController.select(item);
		}
		
		switchToNewEditPhase(EditPhase.IDLE);
	}

	/**
	 * Called on lasso select start
	 */
	@Override
	public void onSelectStart(SelectContext context) throws VetoSelectException {
		if ( !isActionExecutable() ) return;

		// the lasso select can start only if the editor is in IDLE or EDITING_ITEM pase
		if ( !editPhase.equals(EditPhase.IDLE) && !editPhase.equals(EditPhase.EDITING_ITEM) ) {
			throw new VetoSelectException();
		}
		
		// ensure that the start point is not inside of a content item (e.g. inside of the currently
		// edited item)
		Location startLocation = new CoordinateLocation(context.getStartX(), context.getStartY());
		for ( ContentContainer container : containers ) {
			for ( Widget widget : container ) {
				Area widgetArea = new WidgetArea(widget, null);
				if ( widgetArea.intersects(startLocation) ) {
					throw new VetoSelectException();
				}
				com.google.gwt.user.client.Element contentItemElement = widget.getElement();
				com.google.gwt.user.client.Element targetElement = Node.as(context.getStartTargetElement())
					.getParentElement().cast();
				if ( targetElement != null && DOM.isOrHasChild(contentItemElement, targetElement) ) {
					throw new VetoSelectException();
				}
			}
		}
		
		// ensure that the selection target element is not a clickable element (e.g. navigation anchor)
		// for now, skip only <a> tags (and all tags within <a> tag).
		// TODO: document a special market (css class) for navigation elements that could be used in
		// HTML templates and that will prevet lasso selection to start
		EventTarget target = context.getStartTargetElement();
		if ( Element.is(target) && "a".equals(Element.as(target).getTagName().toLowerCase()) ) {
			throw new VetoSelectException();
		}
		List<Node> parents = DomUtil.getParents(Node.as(target));
		for (Node parent : parents) {
			if (Element.is(parent) && "a".equals(Element.as(parent).getTagName().toLowerCase()) ) {
				throw new VetoSelectException();
			}
		}
		
		// ensure that the selection target element is not a sitecake toolbox widget
		com.google.gwt.user.client.Element topContainer = GinInjector.instance.getTopContainer().getElement();
		com.google.gwt.user.client.Element targetElement = Node.as(target).getParentElement().cast();
		if ( targetElement != null && DOM.isOrHasChild(topContainer, targetElement) ) {
			throw new VetoSelectException();
		}
		
		switchToNewEditPhase(EditPhase.SELECTING);
	}
	
	private void selectAllItems() {
		for ( ContentContainer container : containers ) {
			for ( Widget widget : container ) {
				selectItem((ContentItem)widget);
			}
		}		
	}
	
	private void unselectAllItems() {
		for ( ContentItem item : selectedItems ) {
			item.setSelected(false);
		}
		selectedItems.clear();
		dragController.clearSelection();
	}

	private void selectItem(ContentItem item) {
		if ( !selectedItems.contains(item) ) {
			selectedItems.add(item);
			item.setSelected(true);
			dragController.select(item);
		}
	}
	
	private void unselectItem(ContentItem item) {
		selectedItems.remove(item);
		item.setSelected(false);
		dragController.unselect(item);
	}
	
	@Override
	public void onSelect(SelectEvent event) {
		if ( !isActionExecutable() ) return;

		if ( event.getItem() != null ) {
			if ( event.getItem().isSelected() ) {
				unselectItem(event.getItem());
			} else {
				selectItem(event.getItem());
			}
		} else {
			selectAllItems();
		}
	}
	
	/**
	 * Adjust the size of all content containers while
	 * content items are being dragged or restore their
	 * size.
	 * 
	 * @param extend to extend or restore the containers' size 
	 */
	private void extendContainers(boolean extend) {
		for ( ContentContainer container : containers ) {
			if ( extend ) {
				container.extend(50);
			} else {
				container.contract();
			}
		}
	}

	@Override
	public void onMove(MoveEvent event) {
		if ( selectedItems.isEmpty() ) return;
		
		//ParallelTransformation<ContentItem> moveTransformation = new ParallelTransformation<ContentItem>();
		
		if ( event.getDirection().equals(Direction.DOWN) ) {
			
		}
	}

	@Override
	public void onNewItem(NewItemEvent event) {
		if ( !isActionExecutable() ) return;

		ContentItem newContentItem = event.getContentItemCreator().create();
		ContentContainer container = event.getContainer();
		int index = event.getIndex();
		
		if ( newContentItem != null ) {
			container.insert(newContentItem, index);
			
			SimpleTransformation<ContentItem> createTransformation = new SimpleTransformation<ContentItem>(null, null, 
					newContentItem.cloneItem(), new Position(container, index));
			history.put(createTransformation);
			switchToNewEditPhase(EditPhase.IDLE);
			if (newContentItem.intiateEditUponCreation()) {
				eventBus.fireEventDeferred(new EditItemEvent(newContentItem));
			}
			save();
		}
	}
	
	@Override
	public void onUpload(UploadEvent event) {
		ContentItem uploader = event.getOriginatingItem();
		ContentContainer container = uploader.getContainer();
		int startIndex = container.getItemIndex(uploader);
		
		ParallelTransformation<ContentItem> parallelTransformation = new ParallelTransformation<ContentItem>();
		
		Position uploaderPosition = new Position(container, startIndex);
		SimpleTransformation<ContentItem> deleteTransformation = new SimpleTransformation<ContentItem>(uploader.cloneItem(), uploaderPosition, null, null);
		parallelTransformation.add(deleteTransformation);
		
		uploader.onDeletion();
		container.removeItem(uploader);
		
		List<ContentItemCreator> contentItemCreators = event.getContentItemCreators();
		
		for ( ContentItemCreator contentItemCreator : contentItemCreators ) {
			
			ContentItem newContentItem = contentItemCreator.create();
			container.insert(newContentItem, startIndex);
			
			SimpleTransformation<ContentItem> createTransformation = new SimpleTransformation<ContentItem>(null, null, 
					newContentItem.cloneItem(), new Position(container, startIndex));
			parallelTransformation.add(createTransformation);
			
			startIndex++;
		}
		
		contextMenu.hide(true);
		history.put(parallelTransformation);
		save();
	}

	@Override
	public void onLogout(LogoutEvent event) {
		if ( !isActionExecutable() ) return;

		if ( checkEndCondition() || Window.confirm(messages.confirmUnsafeLogout()) ) {
			performEditSessionTermination();
		}
	}

	@Override
	public void onPublish(PublishEvent event) {
		if ( !isActionExecutable() ) return;
		
		// TODO: extract containerNames initialization
		List<String> containerNames = new ArrayList<String>();
		for ( ContentContainer container : containers ) {
			containerNames.add(container.getName());
		}
		contentManager.publish(containerNames);
	}

	@Override
	public void onEditComplete(EditCompleteEvent event) {
		if ( !isActionExecutable() ) return;

		switchToNewEditPhase(EditPhase.IDLE, false);
		unselectAllItems();
	}

	private void closeCurrentEditPhase() {
		closeCurrentEditPhase(false);
	}
	
	private void closeCurrentEditPhase(boolean cancelCurrentOperation) {
		if ( editPhase.equals(EditPhase.IDLE) ) {
			
		} else if ( editPhase.equals(EditPhase.EDITING_ITEM) ) {
			editingEnd(cancelCurrentOperation);
		} else if ( editPhase.equals(EditPhase.SELECTING) ) {

		} else if ( editPhase.equals(EditPhase.DRAGGING_ITEM) ) {
			unselectAllItems();
			extendContainers(false);
		} else if ( editPhase.equals(EditPhase.DRAGGING_NEW_ITEM) ) {
			extendContainers(false);
		}
	}
	
	private void startNewEditPhase(EditPhase newPhase) {
		if ( newPhase.equals(EditPhase.IDLE) ) {
			
		} else if ( newPhase.equals(EditPhase.EDITING_ITEM) ) {
			unselectAllItems();
			contextMenu.hide(true);
			editingStart();
		} else if ( newPhase.equals(EditPhase.SELECTING) ) {
			contextMenu.hide(true);
			unselectAllItems();
		} else if ( newPhase.equals(EditPhase.DRAGGING_ITEM) ) {
			contextMenu.hide(true);
			extendContainers(true);
		} else if ( newPhase.equals(EditPhase.DRAGGING_NEW_ITEM) ) {
			contextMenu.hide(true);
			extendContainers(true);
			unselectAllItems();
			
		}
		
		editPhase = newPhase;
	}

	private void switchToNewEditPhase(EditPhase newPhase, boolean cancelCurrentOperation) {
		closeCurrentEditPhase(cancelCurrentOperation);
		startNewEditPhase(newPhase);
	}
	
	private void switchToNewEditPhase(EditPhase newPhase) {
		closeCurrentEditPhase();
		startNewEditPhase(newPhase);
	}
	
	private void editingStart() {
		ContentContainer container = editedItem.getContainer();
		int index = container.getWidgetIndex(editedItem);
		
		Position startPosition = new Position(container, index);
		
		startEditState = new TransformationState(editedItem.cloneItem(), startPosition);
		
		dragController.makeNotDraggable(editedItem);
		editedItem.startEditing(editMode);
		eventBus.fireEvent(new StartEditingEvent(editedItem));
	}
	
	private void editingEnd(boolean cancelCurrentOperation) {
		if ( editedItem.stopEditing(cancelCurrentOperation) ) {

			SimpleTransformation<ContentItem> editTransformation = new SimpleTransformation<ContentItem>(
					startEditState.getItem(), startEditState.getPosition(), editedItem.cloneItem(), startEditState.getPosition());
			
			history.put(editTransformation);
			save();
		}
		dragController.makeDraggable(editedItem);
		editedItem = null;
		editMode = null;
		startEditState = null;
		eventBus.fireEvent(new EndEditingEvent());
	}

	@Override
	public void onMergeParagraph(MergeParagraphEvent event) {
		if ( !isActionExecutable() ) return;

		ContentContainer container = editedItem.getContainer();
		int index = container.getItemIndex(editedItem);
		
		if ( index == 0 ) return;
		
		ContentItem prevItem = container.getItemAt(index - 1);
		
		if ( !(prevItem instanceof TextItem) ) return;
		
		TextItem prevTextItem = (TextItem)prevItem;
		TextItem currentTextItem = (TextItem)editedItem;
		
		if ( !prevTextItem.getType().equals(currentTextItem.getType()) ) return;
		
		editedItem.stopEditing(false);
		String html = editedItem.getElement().getInnerHTML();
		
		ParallelTransformation<ContentItem> mergeTransformation = new ParallelTransformation<ContentItem>();
		
		SimpleTransformation<ContentItem> deleteTransformation = new SimpleTransformation<ContentItem>(
				startEditState.getItem(), startEditState.getPosition(), null, null);
		mergeTransformation.add(deleteTransformation);
		
		dragController.makeDraggable(editedItem);
		editedItem.onDeletion();
		container.removeItem(editedItem);
		editedItem = null;
		editMode = null;
		startEditState = null;
		
		ContentItem oldPrevTextItem = prevTextItem.cloneItem();
		Node splicePoint = prevTextItem.appendHtml(html);
		
		Position prevTextItemPosition = new Position(container, container.getItemIndex(prevTextItem));
		SimpleTransformation<ContentItem> updateTransformation = new SimpleTransformation<ContentItem>(
				oldPrevTextItem, prevTextItemPosition, prevTextItem.cloneItem(), prevTextItemPosition);
		mergeTransformation.add(updateTransformation);
		
		history.put(mergeTransformation);
		save();
		
		editedItem = prevTextItem;
		editMode = null;
		startNewEditPhase(EditPhase.EDITING_ITEM);
		
		selection.setCursorInto(splicePoint);
	}

	@Override
	public void onNewParagraph(NewParagraphEvent event) {
		if ( !isActionExecutable() ) return;

		ContentContainer container = editedItem.getContainer();
		int index = container.getItemIndex(editedItem);
		
		String html = event.getHtml();
		TextItem oldTextItem = (TextItem)editedItem;
		TextItem newTextItem = TextItem.create(html, TextItem.Type.P, 
				TextItem.Type.P.equals(oldTextItem.getType()) ? oldTextItem.getStyle() : TextItem.DEFAULT_STYLE, false);
		
		container.insert(newTextItem, index+1);
		
		ParallelTransformation<ContentItem> groupTransformation = new ParallelTransformation<ContentItem>();
		
		editedItem.stopEditing(false);

		SimpleTransformation<ContentItem> editTransformation = new SimpleTransformation<ContentItem>(
				startEditState.getItem(), startEditState.getPosition(), editedItem.cloneItem(), startEditState.getPosition());
		groupTransformation.add(editTransformation);

		dragController.makeDraggable(editedItem);
		editedItem = null;
		editMode = null;
		startEditState = null;
		eventBus.fireEvent(new EndEditingEvent());
		
		SimpleTransformation<ContentItem> createTransformation = new SimpleTransformation<ContentItem>(
				null, null, newTextItem.cloneItem(), new Position(container, index + 1));
		groupTransformation.add(createTransformation);
		
		history.put(groupTransformation);
		save();
		
		editedItem = newTextItem;
		editMode = null;
		startNewEditPhase(EditPhase.EDITING_ITEM);
		
	}
	
	/**
	 * Checks if there are unfinished tasks. If there are some, popups
	 * a confirmation dialog asking user to confirm or cancel the logout/exit
	 * procedure.
	 * 
	 * @return true if it's safe/confirmed to proceed with logout/exit
	 */
	private boolean checkEndCondition() {
		return !(contentManager.isActive() || uploadManager.isActive());
	}
	
	private void performEditSessionTermination() {
		uploadManager.abortAll();
		contentManager.abortAll();
		sessionManager.logout();		
	}

	@Override
	public void onErrorNotification(ErrorNotificationEvent event) {
		if ( Level.FATAL.equals(event.getLevel()) ) {
			fatalOccured = true;
			GinInjector.instance.getLassoSelectorController().enable(false);
		}
	}
	
	private boolean isActionExecutable() {
		if ( fatalOccured ) {
			return false;
		}
		
		return true;
	}

	@Override
	public void onTextBlock(TextBlockEvent event) {
		if ( !isActionExecutable() ) return;

		ContentItem newContentItem = createItemFromTextBlock(event);
		if ( newContentItem == null ) return;
		
		ContentItem oldContentItem = event.getOriginItem();
		ContentContainer container = oldContentItem.getContainer();
		int index = container.getItemIndex(oldContentItem);
		
		ParallelTransformation<ContentItem> groupTransformation = new ParallelTransformation<ContentItem>();
		
		Position oldItemPosition = new Position(container, index);
		SimpleTransformation<ContentItem> deleteTransformation = new SimpleTransformation<ContentItem>(
				oldContentItem.cloneItem(), oldItemPosition, null, null);
		groupTransformation.add(deleteTransformation);
		
		container.insertItem(newContentItem, index + 1);
		container.removeItemAt(index);
		
		SimpleTransformation<ContentItem> createTransformation = new SimpleTransformation<ContentItem>(
				null, null, newContentItem.cloneItem(), new Position(container, index));
		groupTransformation.add(createTransformation);
		
		history.put(groupTransformation);
		save();
	}
	
	private ContentItem createItemFromTextBlock(TextBlockEvent event) {
		ContentItem item = null;
		
		switch (event.getBlockType()) {
			case HTML:
				item = HtmlItem.create(event.getText());
				break;
	
			case VIDEO:
				item = VideoItem.create(event.getText());
				break;
	
			case FLASH:
				
				break;
	
			case MAP:
				item = MapItem.create(event.getText());
				break;
		}
		
		return item;
	}

	@Override
	public void onPostEditingEnd(PostEditingEndEvent event) {
		ContentItem item = event.getCurr();
		ContentItem oldItem = event.getPrev();
		Position position = new Position(item.getContainer(), item.getContainer().getItemIndex(item));
		SimpleTransformation<ContentItem> editTransformation = new SimpleTransformation<ContentItem>(
				oldItem, position, item.cloneItem(), position);
		history.put(editTransformation);
		save();
	}
	
	private void showBodyElements() {
		NodeList<Node> nodes = RootPanel.getBodyElement().getChildNodes();
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.getItem(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = node.cast();
				if (pageManager.getElement().equals(el))
					continue;				
				String val = el.getStyle().getDisplay();
				if (val != null && !"".equals(val)) {
					if (el.hasAttribute("display-orig")) {
						el.getStyle().setDisplay(Style.Display.valueOf(el.getAttribute("display-orig")));
						el.removeAttribute("display-orig");
					} else {
						el.getStyle().clearDisplay();
					}
				}
			}
		}
	}
	
	private void hideBodyElements() {
		NodeList<Node> nodes = RootPanel.getBodyElement().getChildNodes();
		for (int i=0; i<nodes.getLength(); i++) {
			Node node = nodes.getItem(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = node.cast();
				if (pageManager.getElement().equals(el))
					continue;
				String val = el.getStyle().getDisplay();
				if (val != null && !"".equals(val)) {
					el.setAttribute("display-orig", val);
				}
				el.getStyle().setDisplay(Style.Display.NONE);
			}
		}
	}

	@Override
	public void onPageManager(PageManagerEvent event) {
		if (!pageManagerActive && event.isOpen()) {
			pageManagerActive = true;
			hideBodyElements();
			pageManager.enable();
		}
	}
	
}
