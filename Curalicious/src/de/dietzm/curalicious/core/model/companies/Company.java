package de.dietzm.curalicious.core.model.companies;

import de.dietzm.foundation.db.base.AbstractBaseEntity;

public class Company extends AbstractBaseEntity {

	private String name;
	private String adress;
	private String country;
	private String orgform;

	private String namespace;

	private int type = 0;

	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_SUBCOMPANY = 1;
	public static final int TYPE_MASTER_COMPANY = 2;
	public static final int TYPE_INDEPENDENT_SUBCOMPANY = 3;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOrgform() {
		return orgform;
	}

	public void setOrgform(String orgform) {
		this.orgform = orgform;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
