package com.sitecake.contentmanager.client.item.fileuploader;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.commons.client.config.ConfigRegistry;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.commons.client.util.HumanReadable;
import com.sitecake.commons.client.util.MimeType;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.dom.File;
import com.sitecake.contentmanager.client.dom.FileInput;
import com.sitecake.contentmanager.client.dom.FileList;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.event.UploadEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.image.ImageItem;
import com.sitecake.contentmanager.client.item.image.ImageObject;
import com.sitecake.contentmanager.client.item.photoset.PhotoSetItem;
import com.sitecake.contentmanager.client.item.slider.SliderEntry;
import com.sitecake.contentmanager.client.item.slider.SliderItem;
import com.sitecake.contentmanager.client.item.slideshow.SlideshowImage;
import com.sitecake.contentmanager.client.item.slideshow.SlideshowItem;
import com.sitecake.contentmanager.client.item.text.TextItem;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;
import com.sitecake.contentmanager.client.resources.Messages;
import com.sitecake.contentmanager.client.toolbar.ContentItemCreator;
import com.sitecake.contentmanager.client.upload.UploadBatch;
import com.sitecake.contentmanager.client.upload.UploadManager;
import com.sitecake.contentmanager.client.upload.UploadObject;

public abstract class FileUploaderItem extends ContentItem {
	public static final String CONTENT_TYPE_NAME = "FILEUPLOADER";
	
	private static final String DEFAULT_ALLOWED_EXTENSIONS = 
		// images
		"bmp, jpg, jpeg, png, gif, tif," +
		// audio
		"wma, mp3, mpa, m3u, ra, mid," +
		// video
		"wmv, vob, swf, mov, mp4, mpg, mpeg, avi, flv, rm," + 
		// documents
		"pdf, doc, docx, xls, ppt, pptx, xlsx, wks, odt, sxw, rtf, txt, dot, xml, otf, ttf," +
		// archives
		"zip, 7z, tar, bz, tgz, rar, deb, rpm, zipx, pkg, iso, dmg," +
		// misc
		"ai, drw, psd, eps, ps, svg";
	
	private static final String CONFIG_UPLOAD_ALLOWED_EXTENSIONS = "FileUploaderItem.allowedExtensions";
	
	private static List<String> allowedExtensions;
	
	private static final String CONFIG_UPLOAD_SIZE_LIMIT = "FileUploaderItem.uploadSizeLimit";
	
	private static final String CONFIG_THUMBNAIL_IMAGE_DIMENSION = "FileUploaderItem.thumbnailImageDimension";
	
	/**
	 * The default upload size limit in KB.
	 */
	private static final int DEFAULT_UPLOAD_SIZE_LIMIT = 8*1024; // 8MB
	
	/**
	 * The default thumbnail image max dimension in pixels.
	 */
	private static final int DEFAULT_THUMBNAIL_IMAGE_DIMENSION = 170;
	
	private static FileUploaderItemUiBinder uiBinder = GWT.create(FileUploaderItemUiBinder.class);

	interface FileUploaderItemUiBinder extends UiBinder<Element, FileUploaderItem> {}

	public enum Type {
		GENERIC,
		IMAGE,
		SLIDESHOW,
		SLIDER,
		PHOTOSET,
		AUDIO,
		VIDEO
	}

	public enum Status {
		SELECTING,
		UPLOADING
	}
	
	protected Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	protected Type type;
	
	protected Status status;
	
	/**
	 * Upload size limit in bytes.
	 */
	protected long uploadSizeLimit;
	
	protected int thumbnailImageDimension;
	
	@UiField
	public DivElement textMessage;

	@UiField
	public DivElement selectContainer;

	@UiField
	public DivElement uploadContainer;
	
	@UiField
	public DivElement overlay;
	
	private long progressPercenage;

	private FileList fileList;
	
	private UploadBatch uploadBatch;
	
	private UploadManager uploadManager = GinInjector.instance.getUploadManager();
	
	private boolean imageUploadForSlideshow;
	
	private boolean dragging;
	
	private boolean pendingCompletition = false;
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	private ConfigRegistry configRegistry = GinInjector.instance.getConfigRegistry();
	
	public static FileUploaderItem create(Type type) {
		FileUploaderItem item = GWT.create(FileUploaderItem.class);
		item.init(type);
		return item;
	}

	public static FileUploaderItem create(Type type, FileList fileList) {
		final FileUploaderItem item = create(type);
		item.fileList = fileList;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				item.enterUploadingPhase();
			}
		});
		return item;
	}
	
	public FileUploaderItem() {
		super();
		uploadSizeLimit = configRegistry.getInteger(CONFIG_UPLOAD_SIZE_LIMIT, DEFAULT_UPLOAD_SIZE_LIMIT) * 1024L;
		thumbnailImageDimension = configRegistry.getInteger(CONFIG_THUMBNAIL_IMAGE_DIMENSION, DEFAULT_THUMBNAIL_IMAGE_DIMENSION);
	}
	
	public void init(Type type) {
		this.type = type;
		Element element = uiBinder.createAndBindUi(this);
		setElement(element);
		
		switch ( type ) {
		case IMAGE:
			textMessage.addClassName(EditorClientBundle.INSTANCE.css().uploadTextImage());
			break;
		case SLIDESHOW:
		case PHOTOSET:
			textMessage.addClassName(EditorClientBundle.INSTANCE.css().uploadTextSlideshow());
			break;
		case SLIDER:
			textMessage.addClassName(EditorClientBundle.INSTANCE.css().uploadTextSlider());
			break;
		default:
			textMessage.addClassName(EditorClientBundle.INSTANCE.css().uploadTextFile());
			break;
		}
		
		addDomHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
						fileList = FileInput.valueOf(selectContainer.getFirstChildElement()).files();
						enterUploadingPhase();
					}
				});
			}
		}, ChangeEvent.getType());
		
		enterSelectingPhase();
	}
	
	private List<String> getAllowedExtensions() {
		if ( allowedExtensions == null ) {
			allowedExtensions = new ArrayList<String>();
			String confValue = configRegistry.get(CONFIG_UPLOAD_ALLOWED_EXTENSIONS, DEFAULT_ALLOWED_EXTENSIONS);
			if ( confValue != null && !"".equals(confValue) ) {
				String [] values = confValue.split(",");
				for ( String value : values ) {
					allowedExtensions.add(value.trim().toLowerCase());
				}
			}
		}
		return allowedExtensions;
	}
	
	private boolean isAllowedExtension(String extension) {
		for ( String allowedExtension : getAllowedExtensions() ) {
			if ( allowedExtension.equalsIgnoreCase(extension) )
				return true;
		}
		return false;
	}
	
	@Override
	public void onDeletion() {
		super.onDeletion();
		
		if ( status.equals(Status.UPLOADING) && uploadBatch != null ) {
			uploadBatch.abort();
		}
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public ContentItem cloneItem() {
		return null;
	}

	@Override
	public void setSelected(boolean select) {
		if ( select && !selected ) {
			selected = true;
			overlay.getStyle().setDisplay(Display.BLOCK);
		} else if ( !select && selected ){
			selected = false;
			overlay.getStyle().setDisplay(Display.NONE);
		}
	}
	
	@Override
	public void startDragging() {
		super.startDragging();
		dragging = true;
	}

	@Override
	public void stopDragging() {
		super.stopDragging();
		dragging = false;
		
		if ( pendingCompletition ) {
			doCompletition();
		}
	}

	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}
	
	@Override
	public String getItemSelector() {
		return "." + CONTENT_TYPE_NAME;
	}

	@Override
	public String getHtml() {
		// this kind of item should not be saved
		return "";
	}

	private void enterSelectingPhase() {
		this.status = Status.SELECTING;
		addStyleName(EditorClientBundle.INSTANCE.css().uploadSelect());
		setSelectTextMessage();
		uploadContainer.getStyle().setDisplay(Display.NONE);
		
		selectContainer.setInnerHTML("<input type=\"file\" multiple=\"true\"></input>");
		setInputAcceptTypes(selectContainer.getFirstChildElement());
		
		selectContainer.getStyle().setDisplay(Display.BLOCK);
	}
	
	private void enterUploadingPhase() {
		if ( createUploadBatch() ) {
			this.status = Status.UPLOADING;
			removeStyleName(EditorClientBundle.INSTANCE.css().uploadSelect());
			setProgress();
			progressPercenage = 0;
			uploadContainer.getStyle().setDisplay(Display.BLOCK);
			selectContainer.getStyle().setDisplay(Display.NONE);
			uploadManager.upload(uploadBatch);
		}
	}
	
	private boolean createUploadBatch() {
		checkImageUploadMode();
		List<UploadObject> uploadObjects = createUploadObjects();
		
		if ( uploadObjects.size() == 0 ) {
			return false;
		}
		
		uploadBatch = new UploadBatch(uploadObjects) {
			public void onUploadAborted() {
				uploadAbortHandler();
			}

			public void onUploadCompleted() {
				uploadCompleteHandler();
			}

			public void onUploadError(UploadObject uploadObject) {
				uploadErrorHandler(uploadObject);
			}

			public void onUploadProgress(long percentage) {
				progressPercenage = percentage;
				setProgress();
			}

			public void onUploadStarted() {
			}
		};
		
		return true;
	}
	
	private void uploadAbortHandler() {
		
	}
	
	private void uploadCompleteHandler() {
		
		if ( !dragging ) {
			doCompletition();
		} else {
			pendingCompletition = true;
		}
	}

	private void uploadErrorHandler(UploadObject uploadObject) {
		sendErrorNotification(Level.ERROR, 
				messages.failedToUploadFiles(), uploadObject.getErrorMessage());
	}
	
	private void doCompletition() {
		
		List<ContentItemCreator> contentItemCreators = new ArrayList<ContentItemCreator>();
		UploadEvent event = new UploadEvent(this, contentItemCreators);
		
		if ( type.equals(Type.SLIDESHOW) || imageUploadForSlideshow ) {
		
			// detect if we're in the slideshow mode and if the first (cover) image upload failed
			// and take needed actions
			if ( UploadObject.Status.ERROR.equals(uploadBatch.getUploadObjects().get(0).getStatus()) ) {
				// TODO: notify about the error situation
				//return;
			}
			
			final List<SlideshowImage> slideshowImages = new ArrayList<SlideshowImage>();
			
			for ( UploadObject uploadObject : uploadBatch.getUploadObjects() ) {
				
				if ( !uploadObject.getStatus().equals(UploadObject.Status.UPLOADED) ) {
					continue;
				}
				
				SlideshowImage slideshowImage = new SlideshowImage();
				
				slideshowImage.setId(uploadObject.getResponse().getProperty("id"));
				slideshowImage.setUrl(uploadObject.getResponse().getProperty("url"));
				slideshowImage.setCover(uploadObject.getResponse().getProperty("resizedUrl") != null );
				slideshowImage.setCoverUrl(uploadObject.getResponse().getProperty("resizedUrl"));
				
				String coverWidth = uploadObject.getResponse().getProperty("resizedWidth");
				if ( coverWidth != null )
					slideshowImage.setCoverWidth(Double.valueOf(coverWidth));
				String coverHeight = uploadObject.getResponse().getProperty("resizedHeight");
				if ( coverHeight != null )
					slideshowImage.setCoverHeight(Double.valueOf(coverHeight));
				
				slideshowImages.add(slideshowImage);
			}
			
			ContentItemCreator slideshowCreator = new ContentItemCreator() {
				
				public ContentItem create() {
					SlideshowItem item = SlideshowItem.create(slideshowImages);
					return item;
				}
			};
			
			contentItemCreators.add(slideshowCreator);
			
		} else if ( type.equals(Type.SLIDER) ) {
			
			final List<SliderEntry> sliderEntries = new ArrayList<SliderEntry>();
			
			for ( UploadObject uploadObject : uploadBatch.getUploadObjects() ) {
				
				if ( !uploadObject.getStatus().equals(UploadObject.Status.UPLOADED) ) {
					continue;
				}
				
				SliderEntry entry = new SliderEntry();
				entry.setUrl(uploadObject.getResponse().getProperty("url"));
				sliderEntries.add(entry);
			}
			
			ContentItemCreator slideshowCreator = new ContentItemCreator() {
				
				public ContentItem create() {
					SliderItem item = SliderItem.create(sliderEntries);
					return item;
				}
			};
			
			contentItemCreators.add(slideshowCreator);
			
		} else if ( type.equals(Type.PHOTOSET) ) {
			
			final List<ImageObject> images = new ArrayList<ImageObject>();
			
			for ( UploadObject uploadObject : uploadBatch.getUploadObjects() ) {
				
				if ( !uploadObject.getStatus().equals(UploadObject.Status.UPLOADED) ) {
					continue;
				}
				
				final ImageObject image = ImageObject.create(uploadObject.getResponse());
				images.add(image);
			}
			
			ContentItemCreator slideshowCreator = new ContentItemCreator() {
				
				public ContentItem create() {
					PhotoSetItem item = PhotoSetItem.create(images);
					return item;
				}
			};
			
			contentItemCreators.add(slideshowCreator);
			
		} else {
			
			for ( UploadObject uploadObject : uploadBatch.getUploadObjects() ) {
				
				if ( !uploadObject.getStatus().equals(UploadObject.Status.UPLOADED) ) {
					continue;
				}
				
				if ( uploadObject.getHeader("X-IMAGE") != null ) {

					final double cntWidth = DomUtil.getElementInnerWidth(container.getElement());
					final ImageObject image = ImageObject.create(uploadObject.getResponse(), cntWidth);
					
					ContentItemCreator imageItemCreator = new ContentItemCreator() {
						
						public ContentItem create() {
							ImageItem item = ImageItem.create(image, cntWidth);
							return item;
						}
					};
					
					contentItemCreators.add(imageItemCreator);
					
				} else {
					
					final String link = "<a href=\"" + uploadObject.getResponse().getProperty("url") + "\" target=\"_blank\">" + 
						uploadObject.getFileName() + " (" + HumanReadable.bytes(uploadObject.getFile().size()) + ")</a>";
					
					ContentItemCreator fileItemCreator = new ContentItemCreator() {
						
						public ContentItem create() {
							TextItem item = TextItem.create(link, TextItem.Type.P, DEFAULT_STYLE, false);
							return item;
						}
					};
					
					contentItemCreators.add(fileItemCreator);
				}
			}
			
		}
		
		eventBus.fireEventDeferred(event);
	}
	
	private void checkImageUploadMode() {
		if ( type.equals(Type.SLIDESHOW) ) {
			imageUploadForSlideshow = true;
		} else if ( type.equals(Type.GENERIC) && fileList.length() > 1 ) {
			imageUploadForSlideshow = true;
			for ( int i = 0; i < fileList.length(); i++ ) {
				File file = fileList.index(i);
				String fileExt = file.name().substring(file.name().lastIndexOf('.') + 1);
				MimeType mimeType = MimeType.lookup(fileExt);
				if ( !( mimeType.equals(MimeType.IMAGE_JPEG) ||
						mimeType.equals(MimeType.IMAGE_GIF) ||
						mimeType.equals(MimeType.IMAGE_PNG) ||
						mimeType.equals(MimeType.IMAGE_BMP) ) ) {
					imageUploadForSlideshow = false;
					break;
				}
			}
			if (imageUploadForSlideshow) {
				type = Type.PHOTOSET;
				imageUploadForSlideshow = false;
			}
		} else {
			imageUploadForSlideshow = false;
		}
	}
	
	private List<UploadObject> createUploadObjects() {
		List<UploadObject> uploadObjects = new ArrayList<UploadObject>();
		for ( int i = 0; i < fileList.length(); i++ ) {
			File file = fileList.index(i);
			if ( checkRestrictions(file) && checkMediaType(file) ) {
				UploadObject uploadObject = createUploadObject(file, i);
				uploadObjects.add(uploadObject);
			}
		}
		return uploadObjects;
	}
	
	private boolean checkRestrictions(File file) {
		String name = file.name();
		if ( name == null || name == "" ) {
			sendErrorNotification(Level.WARNING, messages.invalidUploadFileSelection("0"));
			return false;
		}

		if ( file.size() == 0 ) {
			sendErrorNotification(Level.WARNING, messages.invalidUploadFileSelection(name));
			return false;
		}
		
		if ( file.size() >= uploadSizeLimit ) {
			sendErrorNotification(Level.WARNING, 
					messages.invalidUploadFileMaxSize(name, HumanReadable.bytes(uploadSizeLimit)));
			return false;
		}

		return true;
	}
	
	private boolean checkMediaType(File file) {
		String fileExt = file.name().substring(file.name().lastIndexOf('.') + 1);
		MimeType mimeType = MimeType.lookup(fileExt);
		boolean isAllowed;
		
		switch ( type ) {
		
		case IMAGE:
		case SLIDESHOW:
		case SLIDER:
		case PHOTOSET:
			isAllowed = mimeType.equals(MimeType.IMAGE_JPEG) ||
				mimeType.equals(MimeType.IMAGE_GIF) ||
				mimeType.equals(MimeType.IMAGE_PNG) ||
				mimeType.equals(MimeType.IMAGE_BMP);
			break;
			
		case VIDEO:
			isAllowed = mimeType.equals(MimeType.VIDEO_MPEG) ||
				mimeType.equals(MimeType.VIDEO_QUICKTIME) ||
				mimeType.equals(MimeType.VIDEO_VND_RN_REALVIDEO) ||
				mimeType.equals(MimeType.VIDEO_X_FLV);
			break;
			
		case AUDIO:
			isAllowed = mimeType.equals(MimeType.AUDIO_MPEG) ||
					mimeType.equals(MimeType.AUDIO_X_WAV);
			break;
			
		case GENERIC:
		default:
			isAllowed = isAllowedExtension(fileExt);
			
		}
		
		if ( !isAllowed ) {
			sendErrorNotification(Level.WARNING, messages.invalidUploadFileType(file.name()));
		}
		
		return isAllowed;
	}
	
	private UploadObject createUploadObject(File file, int listIndex) {
		UploadObject uploadObject;
		
		String fileName = file.name();
		String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
		MimeType mimeType = MimeType.lookup(fileExt);
		
		switch ( mimeType ) {
			case IMAGE_JPEG:
			case IMAGE_PNG:
			case IMAGE_GIF:
				if ( imageUploadForSlideshow ) {
					uploadObject = createSlideshowUploadObject(file, listIndex);
				} else if ( type.equals(Type.SLIDER) ) {
					uploadObject = createSliderImageUploadObject(file);
				} else if ( type.equals(Type.PHOTOSET) ) {
					uploadObject = createImageUploadObject(file);
				} else {
					uploadObject = createImageUploadObject(file);
				}
				break;
				
			default:
				uploadObject = new UploadObject(file);
		}
		
		return uploadObject;
	}
	
	private UploadObject createImageUploadObject(File file) {
		UploadObject object = new UploadObject(file);
		object.setHeader("X-IMAGE", "true");
		return object;
	}

	private UploadObject createSliderImageUploadObject(File file) {
		UploadObject object = new UploadObject(file);
		object.setHeader("X-IMAGE", "true");
		return object;
	}
	
	private UploadObject createSlideshowUploadObject(File file, int listIndex) {
		UploadObject object = new UploadObject(file);
		object.setHeader("X-IMAGE", "true");
		return object;
	}
	
	private void setSelectTextMessage() {
		String message = "";
		
		switch ( type ) {
		case IMAGE:
		case SLIDESHOW:
		case SLIDER:
		case PHOTOSET:
			message = messages.selectFilesToUploadImage();
			break;
		case VIDEO:
			message = messages.selectFilesToUploadVideo();
			break;
		case AUDIO:
			message = messages.selectFilesToUploadAudio();
			break;
		case GENERIC:
		default:
			message = messages.selectFilesToUploadGeneric();
		}
		
		textMessage.setInnerText(message);
	}

	private void setProgress() {
		String message = messages.uploadingFiles() + " " + progressPercenage + "%";
		textMessage.setInnerText(message);
		uploadContainer.getFirstChildElement().getStyle().setWidth(progressPercenage, Unit.PCT);
	}

	private void setInputAcceptTypes(Element input) {
		String mimeTypes = "";
		
		switch ( type ) {
		case IMAGE:
		case SLIDESHOW:
		case SLIDER:
		case PHOTOSET:
			mimeTypes = MimeType.IMAGE_JPEG + "," + MimeType.IMAGE_GIF + "," + MimeType.IMAGE_PNG;
			break;
		case VIDEO:
			mimeTypes = MimeType.VIDEO_MPEG + "," + MimeType.VIDEO_QUICKTIME + "," + MimeType.VIDEO_X_FLV;
			break;
		case AUDIO:
			mimeTypes = MimeType.AUDIO_MPEG + "," + MimeType.AUDIO_X_WAV;
			break;
		case GENERIC:
		default:
			mimeTypes = "*/*";
		}
		
		input.setAttribute("accept", mimeTypes);
	}
	
	private void sendErrorNotification(Level level, String message) {
		sendErrorNotification(level, message, null);
	}

	private void sendErrorNotification(Level level, String message, String details) {
		eventBus.fireEventDeferred(new ErrorNotificationEvent(level, message, details));
	}	
}
