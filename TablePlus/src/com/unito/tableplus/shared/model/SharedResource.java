package com.unito.tableplus.shared.model;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class SharedResource implements Resource {
	
	private static final long serialVersionUID = 5115867477108503053L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String key;
	@Persistent
	private String name;
	@Persistent
	private String icon;
	@Persistent
	private String uri;
	@Persistent
	private Provider provider;
	
	//delete this and the sky will tumble and fall!!!
	public SharedResource(){
	}
	
	public SharedResource(Resource resource, Table table) {
		this.name = resource.getName();
		this.icon = resource.getIcon();
		this.uri = resource.getURI();
		this.provider = resource.getProvider();
		this.id = resource.getID();
		this.table = table;
	}

	@Persistent
	private String id;
	/**
	 * Used for mapping the resource with the table it belongs to.
	 */
	@Persistent
	private Table table;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getIcon() {
		return this.icon;
	}

	@Override
	public String getURI() {
		return this.uri;
	}

	@Override
	public Provider getProvider() {
		return this.provider;
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public void setURI(String uri) {
		this.uri =  uri;
	}

	@Override
	public String toString() {
		return "SharedResource [key=" + key + ", name=" + name + ", icon="
				+ icon + ", uri=" + uri + ", provider=" + provider + ", id="
				+ id + ", table=" + table + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SharedResource other = (SharedResource) obj;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (provider != other.provider)
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
	

}
