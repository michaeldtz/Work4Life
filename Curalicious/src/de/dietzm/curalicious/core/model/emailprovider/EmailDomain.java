package de.dietzm.curalicious.core.model.emailprovider;

import com.googlecode.objectify.Ref;

import de.dietzm.curalicious.core.model.companies.Company;
import de.dietzm.foundation.db.base.AbstractBaseEntity;

public class EmailDomain extends AbstractBaseEntity {

	private String emaildomain;
	
	private int type = 0;
	
	public static final int TYPE_EMAIL_PROVIDER = 3;
	public static final int TYPE_COMPANY_EMAIL = 2;
	public static final int TYPE_MIXED = 1;
	public static final int TYPE_UNKNOWN = 0;
	
	private String name;
	
	private Ref<Company> belongsToCompany;

	public String getEmaildomain() {
		return emaildomain;
	}

	public void setEmaildomain(String emaildomain) {
		this.emaildomain = emaildomain;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ref<Company> getBelongsToCompany() {
		return belongsToCompany;
	}

	public void setBelongsToCompany(Ref<Company> belongsToCompany) {
		this.belongsToCompany = belongsToCompany;
	}
	
	
	
}
