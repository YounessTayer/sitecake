package com.sitecake.contentmanager.client.properties;

import com.google.inject.Inject;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.storage.ClientPermanentStorage;
import com.sitecake.contentmanager.client.storage.ClientSessionStorage;


/**
 * A basic implements of <code>PropertyManager</code> interface.
 * The autoCommit mode is true and it is ready immediately upon creation.
 */
public class BasicPropertyManager implements PropertyManager {

	private static final String NAME_PREFIX = "prop_";
	
	protected String namePrefix = NAME_PREFIX;
	
	/**
	 * The global event bus.
	 */
	protected EventBus eventBus;
	
	/**
	 * A scope that will be use in set/get methods where no explicit scope is given.
	 */
	protected PropertyScope defaultScope;
	
	/**
	 * Indicates whether the auto-commit mode is enabled.
	 */
	protected boolean autoCommit;

	/**
	 * Indicates whether the service is ready.
	 */
	protected boolean ready;
	
	protected ClientSessionStorage sessionStorage;
	
	protected ClientPermanentStorage applicationStorage;
	
	/**
	 * Default constructor.
	 */
	@Inject
	public BasicPropertyManager(EventBus eventBus, ClientSessionStorage sessionStorage, ClientPermanentStorage appliactionStorage) {
		defaultScope = PropertyScope.SESSION;
		autoCommit = true;
		ready = true;
		this.sessionStorage = sessionStorage;
		this.applicationStorage = appliactionStorage;
	}

	@Override
	public PropertyScope getDefaultScope() {
		return defaultScope;
	}

	@Override
	public String getProperty(String name) {
		return getProperty(name, defaultScope);
	}

	@Override
	public String getProperty(String name, String defaultValue) {
		String property = getProperty(name, defaultScope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public String getProperty(String name, String defaultValue,
			PropertyScope scope) {
		String property = getProperty(name, scope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public Boolean getPropertyBoolean(String name) {
		return getPropertyBoolean(name, defaultScope);
	}

	@Override
	public Boolean getPropertyBoolean(String name, Boolean defaultValue) {
		Boolean property = getPropertyBoolean(name, defaultScope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public Boolean getPropertyBoolean(String name, Boolean defaultValue,
			PropertyScope scope) {
		Boolean property = getPropertyBoolean(name, scope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public Float getPropertyFloat(String name) {
		return getPropertyFloat(name, defaultScope);
	}

	@Override
	public Float getPropertyFloat(String name, Float defaultValue) {
		Float property = getPropertyFloat(name, defaultScope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public Float getPropertyFloat(String name, Float defaultValue,
			PropertyScope scope) {
		Float property = getPropertyFloat(name, scope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public Integer getPropertyInteger(String name) {
		return getPropertyInteger(name, defaultScope);
	}

	@Override
	public Integer getPropertyInteger(String name, Integer defaultValue) {
		Integer property = getPropertyInteger(name, defaultScope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public Integer getPropertyInteger(String name, Integer defaultValue,
			PropertyScope scope) {
		Integer property = getPropertyInteger(name, scope);
		return ( property != null ) ? property : defaultValue;
	}

	@Override
	public boolean isAutoCommit() {
		return autoCommit;
	}

	@Override
	public void setAutoCommit(boolean auto) {
		autoCommit = auto;
	}

	@Override
	public void setDefaultScope(PropertyScope value) {
		defaultScope = value;
	}

	@Override
	public void setProperty(String name, String value) {
		setProperty(name, value, defaultScope);
	}

	@Override
	public void setProperty(String name, Integer value) {
		setProperty(name, value, defaultScope);
	}

	@Override
	public void setProperty(String name, Boolean value) {
		setProperty(name, value, defaultScope);
	}

	@Override
	public void setProperty(String name, Float value) {
		setProperty(name, value, defaultScope);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#getProperty(java.lang.String, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public String getProperty(String name, PropertyScope scope) {
		String resultProp = null;
		Object obj = getCanonicalProperty(name, scope);
		
		if ( obj != null ) {
			resultProp = String.valueOf(obj);
		}
		
		return resultProp;
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#getPropertyBoolean(java.lang.String, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public Boolean getPropertyBoolean(String name, PropertyScope scope) {
		Boolean resultProp = null;
		Object obj = getCanonicalProperty(name, scope);
		
		if ( obj != null ) {
			resultProp = Boolean.parseBoolean(String.valueOf(obj));
		}
		
		return resultProp;
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#getPropertyFloat(java.lang.String, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public Float getPropertyFloat(String name, PropertyScope scope) {
		Float resultProp = null;
		Object obj = getCanonicalProperty(name, scope);
		
		if ( obj != null ) {
			resultProp = Float.valueOf(String.valueOf(obj));
		}
		
		return resultProp;
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#getPropertyInteger(java.lang.String, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public Integer getPropertyInteger(String name, PropertyScope scope) {
		Integer resultProp = null;
		Object obj = getCanonicalProperty(name, scope);
		
		if ( obj != null ) {
			resultProp = Integer.valueOf(String.valueOf(obj));
		}
		
		return resultProp;
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#setProperty(java.lang.String, java.lang.Boolean, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public void setProperty(String name, Boolean value, PropertyScope scope) {
		setCanonicalProperty(name, value, scope);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#setProperty(java.lang.String, java.lang.Float, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public void setProperty(String name, Float value, PropertyScope scope) {
		setCanonicalProperty(name, value, scope);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#setProperty(java.lang.String, java.lang.Integer, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public void setProperty(String name, Integer value, PropertyScope scope) {
		setCanonicalProperty(name, value, scope);
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#setProperty(java.lang.String, java.lang.String, com.sitecake.editor.client.properties.PropertyScope)
	 */
	@Override
	public void setProperty(String name, String value, PropertyScope scope) {
		setCanonicalProperty(name, value, scope);
	}

	/**
	 * Saves the given property as a string value.
	 * 
	 * @param name
	 * @param value
	 * @param scope
	 */
	protected void setCanonicalProperty(String name, Object value, PropertyScope scope) {
		if ( PropertyScope.APPLICATION.equals(scope) ) {
			applicationStorage.setItem(namePrefix + name, value.toString());
		} else if ( PropertyScope.SESSION.equals(scope) ) {
			sessionStorage.setItem(namePrefix + name, value.toString());
		}
	}
	
	/**
	 * Gets the value of requested property as a string.
	 * 
	 * @param name
	 * @param scope
	 * @return
	 */
	protected Object getCanonicalProperty(String name, PropertyScope scope) {
		Object property = null;
		
		if ( PropertyScope.APPLICATION.equals(scope) ) {
			property = applicationStorage.getItem(namePrefix + name);
		} else if ( PropertyScope.SESSION.equals(scope) ) {
			property = sessionStorage.getItem(namePrefix + name);
		}
		
		return property;
	}

	/* (non-Javadoc)
	 * @see com.sitecake.editor.client.properties.PropertyManager#commit()
	 */
	@Override
	public void commit() {
		// do nothing as the autoCommit is enabled
	}
}
