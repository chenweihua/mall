/*
 * 
 * 
 * 
 */
package com.igomall.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "lx_plugin_config")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "lx_plugin_config_sequence")
public class PluginConfig extends OrderEntity {

	private static final long serialVersionUID = -4357367409438384806L;

	private String pluginId;

	private Boolean isEnabled;

	private Map<String, String> attributes = new HashMap<String, String>();

	@Column(nullable = false, updatable = false, unique = true, length = 100)
	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Column(nullable = false)
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "lx_plugin_config_attribute")
	@MapKeyColumn(name = "name", length = 100)
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	@Transient
	public String getAttribute(String name) {
		if (getAttributes() != null && name != null) {
			return getAttributes().get(name);
		} else {
			return null;
		}
	}

	@Transient
	public void setAttribute(String name, String value) {
		if (getAttributes() != null && name != null) {
			getAttributes().put(name, value);
		}
	}

}