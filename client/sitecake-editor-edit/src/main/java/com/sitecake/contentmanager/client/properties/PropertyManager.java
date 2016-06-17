package com.sitecake.contentmanager.client.properties;


/**
 * Represents a storage of arbitrary properties different modules/parts
 * of the application may use.
 */
public interface PropertyManager {

	/**
	 * Sets the auto-commit mode. If set, every <code>setProperty()</code> will
	 * automatically save the property into the persistent storage. If not, 
	 * all property settings are kept into a volatile storage until a <code>{@link #commit()}</code>
	 * call.
	 * 
	 * @param auto <code>true</code> enables the auto-commit mode, <code>false</code> disables it
	 */
	public void setAutoCommit(boolean auto);
	
	/**
	 * Returns the current auto-commit mode.
	 * 
	 * @return <code>true</code> if the auto-commit mode is enabled, <code>false</code> otherwise
	 */
	public boolean isAutoCommit();
	
	/**
	 * If the auto-commit mode is enabled, causes that all property changes 
	 * after the last call are saved. If the auto-commit mode is disabled, it does
	 * nothing.
	 */
	public void commit();
	
	/**
	 * Sets the default scope.
	 * 
	 * @param value the default scope
	 */
	public void setDefaultScope(PropertyScope value);
	
	/**
	 * Returns the current default scope.
	 * 
	 * @return the current default scope
	 */
	public PropertyScope getDefaultScope();
	
	/**
	 * Stores a string property in the storage under the given name and with
	 * the default scope. Any previous version of the property will be replaced.
	 * 
	 * @param name property name
	 * @param value property value
	 */
	public void setProperty(String name, String value);
	
	/**
	 * Stores a string property in the storage under the given name, and with
	 * the given value and the scope. Any previous version of the property 
	 * will be replaced.
	 * 
	 * @param name property name
	 * @param value property value
	 * @param scope property scope
	 */
	public void setProperty(String name, String value, PropertyScope scope);
	
	/**
	 * Retrieves a property with the given name from the storage. A property from
	 * a narrower scope takes precedence.   
	 * 
	 * @param name property name 
	 * @return the property value or <code>null</code> if no property with the given name found
	 */
	public String getProperty(String name);

	/**
	 * Retrieves a property with the given name from the storage or returns 
	 * the given default value if no property found. A property from
	 * a narrower scope takes precedence.
	 * 
	 * @param name property name
	 * @param defaultValue a value that is returned in case no property found
	 * @return the property value or <code>defaultValue</code> if no property with the given name found
	 */
	public String getProperty(String name, String defaultValue);

	/**
	 * Retrieves a property with the given name and the given scope from the storage.
	 * 
	 * @param name property name 
	 * @param scope the property scope
	 * @return the property value or <code>null</code> if no property with the given name found
	 */
	public String getProperty(String name, PropertyScope scope);

	/**
	 * Retrieves a property with the given name and the given scope from the storage or returns 
	 * the given default value if no property found.
	 * 
	 * @param name property name
	 * @param defaultValue a value that is returned in case no property found
	 * @param scope the property scope 
	 * @return the property value or <code>defaultValue</code> if no property with the given name found
	 */
	public String getProperty(String name, String defaultValue, PropertyScope scope);
	
	/**
	 * Stores an integer property in the storage under the given name and with
	 * the given value using <code>PropertyScope.SESSION</code> as the
	 * default scope. Any previous version of the property will be replaced.
	 * 
	 * @param name property name
	 * @param value property value
	 */
	public void setProperty(String name, Integer value);
	
	/**
	 * Stores an integer property in the storage under the given name, and with
	 * the given value and the scope. Any previous version of the property 
	 * will be replaced.
	 * 
	 * @param name property name
	 * @param value property value
	 * @param scope property scope
	 */	
	public void setProperty(String name, Integer value, PropertyScope scope);
	
	/**
	 * Retrieves a property with the given name from the storage. The property
	 * value found in the storage will be casted to <code>Integer</code>. A property from
	 * a narrower scope takes precedence.
	 * 
	 * @param name property name 
	 * @return the property value or <code>null</code> if no property with the given name found
	 */	
	public Integer getPropertyInteger(String name);

	/**
	 * Retrieves a property with the given name and from the given scope from the storage. The property
	 * value found in the storage will be casted to <code>Integer</code>.
	 * 
	 * @param name property name 
	 * @param scope property scope
	 * @return the property value or <code>null</code> if no property with the given name found
	 */	
	public Integer getPropertyInteger(String name, PropertyScope scope);
	
	/**
	 * Retrieves a property with the given name from the storage or returns 
	 * the given default value if no property found. The property
	 * value found in the storage will be casted to <code>Integer</code>.
	 * A property from a narrower scope takes precedence.
	 * 
	 * @param name property name
	 * @param defaultValue a value that is returned in case no property found
	 * @return the property value or <code>defaultValue</code> if no property with the given name found
	 */	
	public Integer getPropertyInteger(String name, Integer defaultValue);

	/**
	 * Retrieves a property with the given name and with the given scope from the storage or returns 
	 * the given default value if no property found. The property
	 * value found in the storage will be casted to <code>Integer</code>.
	 * 
	 * @param name property name
	 * @param defaultValue a value that is returned in case no property found
	 * @param scope property scope
	 * @return the property value or <code>defaultValue</code> if no property with the given name found
	 */	
	public Integer getPropertyInteger(String name, Integer defaultValue, PropertyScope scope);
	
	/**
	 * @see #setProperty(String, Integer)
	 */
	public void setProperty(String name, Boolean value);
	
	/**
	 * @see #setProperty(String, Integer, PropertyScope) 
	 */
	public void setProperty(String name, Boolean value, PropertyScope scope);
	
	/**
	 * @see #getPropertyInteger(String)
	 */
	public Boolean getPropertyBoolean(String name);

	/**
	 * @see #getPropertyInteger(String, PropertyScope)
	 */
	public Boolean getPropertyBoolean(String name, PropertyScope scope);
	
	/**
	 * @see #getPropertyInteger(String, Integer)
	 */
	public Boolean getPropertyBoolean(String name, Boolean defaultValue);

	/**
	 * @see #getPropertyInteger(String, Integer, PropertyScope)
	 */
	public Boolean getPropertyBoolean(String name, Boolean defaultValue, PropertyScope scope);
	
	/**
	 * @see #setProperty(String, Integer)
	 */
	public void setProperty(String name, Float value);
	
	/**
	 * @see #setProperty(String, Integer, PropertyScope) 
	 */
	public void setProperty(String name, Float value, PropertyScope scope);
	
	/**
	 * @see #getPropertyInteger(String)
	 */
	public Float getPropertyFloat(String name);

	/**
	 * @see #getPropertyInteger(String, PropertyScope)
	 */
	public Float getPropertyFloat(String name, PropertyScope scope);
	
	/**
	 * @see #getPropertyInteger(String, Integer)
	 */
	public Float getPropertyFloat(String name, Float defaultValue);

	/**
	 * @see #getPropertyInteger(String, Integer, PropertyScope)
	 */
	public Float getPropertyFloat(String name, Float defaultValue, PropertyScope scope);
}
