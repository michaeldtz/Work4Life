package de.dietzm.curalicious.core.model.modules;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

public class Module extends AbstractBaseEntity {

	private String name;
	
	private int type = 0;
	
	public static final int TYPE_CORE_MODULE = 0;
	public static final int TYPE_ADDON_MODULE = 1;
	public static final int TYPE_CUSTOMER_INTERNAL_MODULE = 2;
	public static final int TYPE_IN_DEVELOPMENT_MODULE = 3;
	public static final int TYPE_PARTNER_MODULE = 4;
	public static final int TYPE_EXTENSION_MODULE = 5;
	
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
