package com.sitecake.publicmanager.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.sitecake.commons.client.util.Locale;

public class LocaleProxyImpl implements LocaleProxy {

	
	private Messages messages;
	
	@Override
	public Messages messages() {
		return messages;
	}

	@Inject
	protected LocaleProxyImpl(Locale locale) {
		String code = locale.code().toLowerCase();
		
		if (code.startsWith("sl")) {
			messages = GWT.create(MessagesSl.class);
		} else if (code.startsWith("sr")) {
			messages = GWT.create(MessagesSr.class);
		} else if (code.startsWith("es")) {
			messages = GWT.create(MessagesEs.class);
		} else if (code.startsWith("de")) {
			messages = GWT.create(MessagesDe.class);
		} else if (code.startsWith("fr")) {
			messages = GWT.create(MessagesFr.class);
		} else if (code.startsWith("dk")) {
			messages = GWT.create(MessagesDk.class);
		} else if (code.startsWith("it")) {
			messages = GWT.create(MessagesIt.class);
		} else if (code.startsWith("ru")) {
			messages = GWT.create(MessagesRu.class);
		} else if (code.startsWith("cs")) {
			messages = GWT.create(MessagesCs.class);
		} else if (code.startsWith("sk")) {
			messages = GWT.create(MessagesSk.class);
		} else if (code.equals("pt-br")) {
			messages = GWT.create(MessagesPtBr.class);
		} else if (code.startsWith("pt")) {
			messages = GWT.create(MessagesPt.class);
		} else if (code.startsWith("pl")) {
			messages = GWT.create(MessagesPl.class);
		} else if (code.startsWith("zh-cn")) {
			messages = GWT.create(MessagesZhCn.class);
		} else {
			messages = GWT.create(MessagesEn.class);
		}
	}
	
}
