package com.sitecake.contentmanager.client.pages;

import com.google.gwt.core.client.JavaScriptObject;

public class Page extends JavaScriptObject {
	protected Page() {}
	
	public final static native Page create()/*-{
		return {};
	}-*/;
	
	public final native String getId()/*-{
		return this.id ? this.id : null;
	}-*/;
	
	public final native void setId(String id)/*-{
		this.id = id;
	}-*/;

	public final native String getTid()/*-{
		return this.tid ? this.tid : null;
	}-*/;
	
	public final native void setTid(String tid)/*-{
		this.tid = tid;
	}-*/;

	public final native String getCid()/*-{
		return this.cid ? this.cid : null;
	}-*/;
	
	public final native void setCid(String cid)/*-{
		this.cid = cid;
	}-*/;
	
	public final native String getUrl()/*-{
		return this.url ? this.url : null;
	}-*/;
	
	public final native void setUrl(String url)/*-{
		this.url = url;
	}-*/;
	
	public final native String getTitle()/*-{
		return this.title ? this.title : null;
	}-*/;
	
	public final native void setTitle(String title)/*-{
		this.title = title;
	}-*/;

	public final native String getNavTitle()/*-{
		return this.navtitle ? this.navtitle : null;
	}-*/;
	
	public final native void setNavTitle(String navtitle)/*-{
		this.navtitle = navtitle;
	}-*/;
	
	public final native String getDesc()/*-{
		return this.desc ? this.desc : null;
	}-*/;
	
	public final native void setDesc(String desc)/*-{
		this.desc = desc;
	}-*/;

	public final native String getKeywords()/*-{
		return this.keywords ? this.keywords : null;
	}-*/;
	
	public final native void setKeywords(String keywords)/*-{
		this.keywords = keywords;
	}-*/;
	
	public final native int getIdx()/*-{
		return this.idx;
	}-*/;
	
	public final native void setIdx(int idx)/*-{
		this.idx = idx;
	}-*/;

	public final native boolean isHome()/*-{
		return this.home ? this.home : false;
	}-*/;
	
	public final native void setHome(boolean home)/*-{
		this.home = home;
	}-*/;
	
}
