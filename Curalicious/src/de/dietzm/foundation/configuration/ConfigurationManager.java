package de.dietzm.foundation.configuration;

import java.util.List;

import de.dietzm.foundation.configuration.model.SLConfiguration;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;

public class ConfigurationManager {

	private static ConfigurationManager unique;

	
	public static ConfigurationManager getInstance(){
		return unique;		
	}

	private DAO<SLConfiguration> dao;
	
	private ConfigurationManager(){
		dao = DAOFactory.getEntityManager("SLConfiguration");
	}
	
	public int getInt(String key, int def){
		
		//TODO Get Owner from User and search with Owner
		
		
		List<SLConfiguration> list = dao.query("key", key);
		
		if(list.size() == 1)
			return list.get(0).getValueInt();
		
		return def;
	}
	
	
	public String getString(String key, String def){
		
		//TODO Get Owner from User and search with Owner
		
		
		List<SLConfiguration> list = dao.query("key", key);
		
		if(list.size() == 1)
			return list.get(0).getValueString();
		
		return def;
	}
	
}
