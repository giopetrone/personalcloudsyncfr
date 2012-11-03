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
		// TODO Auto-generated method stub
		
	}

}
