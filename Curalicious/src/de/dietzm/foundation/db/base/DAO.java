package de.dietzm.foundation.db.base;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;

import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.alphazone.repository.model.RepositoryProject;
import de.dietzm.foundation.configuration.model.SLConfiguration;
import de.dietzm.foundation.usermgmt.model.Invitation;
import de.dietzm.foundation.usermgmt.model.LoginAttemp;
import de.dietzm.foundation.usermgmt.model.Registration;
import de.dietzm.foundation.usermgmt.model.SLRole;
import de.dietzm.foundation.usermgmt.model.SLRoleUserAssignment;
import de.dietzm.foundation.usermgmt.model.SLUser;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DAO<E extends AbstractBaseEntity>  {

	static {
		 
		
		ObjectifyService.run(new VoidWork() {
		    public void vrun() {
				registerWithName("SLUser", SLUser.class);
				registerWithName("SLRole", SLRole.class);
				registerWithName("SLRoleUserAssignment", SLRoleUserAssignment.class);
				registerWithName("SLConfiguration", SLConfiguration.class);
				
				registerWithName("Registration", Registration.class);
				registerWithName("LoginAttemp", LoginAttemp.class);
				registerWithName("Invitation", Invitation.class);
				registerWithName("RepositoryProject", RepositoryProject.class);
				registerWithName("RepositoryItem", RepositoryItem.class);
				
				
		    }
		});
		
		
		//registerWithName("Invitation", Invitation.class);
	}

	private static HashMap<String, Class> registeredClasses;

	public static void registerWithName(String name, Class<? extends AbstractBaseEntity> clz) {
		if (registeredClasses == null)
			registeredClasses = new HashMap<String, Class>();

		if (!registeredClasses.containsKey(name)) {
			registeredClasses.put(name, clz);
			ObjectifyService.register(clz);
		}
	}

	public static DAO getByName(String name) {
		if (registeredClasses == null)
			registeredClasses = new HashMap<String, Class>();
		if (registeredClasses.containsKey(name)) {
			Class clz = registeredClasses.get(name);
			return new DAO(clz);

		}
		return null;
	}

	private Class clz;

	public DAO(Class<? extends AbstractBaseEntity> clz) {
		super();
		this.clz = clz;
		
	}

	public E get(Long id) {
		try {
			return (E) ofy().load().type(clz).id(id).now();
		} catch (NotFoundException e) {
			return null;
		}
	}

	public List<E> get(Long... ids) {
		Map<Long, E> fetched = ofy().load().type(clz).ids(ids);
		Iterator<E> it = fetched.values().iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}
		return list;
	}

	public Long create(E object) {
		Key<E> key = ofy().save().entity(object).now();
		return key.getId();
	}
	
	public void update(E object) {
		ofy().save().entity(object).now();
	}

	public void delete(E... objects) {
		ofy().delete().entities((Object[]) objects).now();
	}

	public void deleteIDs(Long... ids) {
		for (int i = 0; i < ids.length; i++) {
			ofy().delete().type(clz).id(ids[i]).now();
		}	
	}

	public void delete(List<E> repoContentList) {
		ofy().delete().entities(repoContentList).now();
	}

	public List<E> queryAll() {
		//Query<E> queryResult = ;
		Iterator<E> it = ofy().load().type(clz).iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}
		return list;
	}

	public List<E> query(String condition, Object value) {
		Query<E> queryResult = ofy().consistency(Consistency.STRONG).load().type(clz).filter(condition, value);
		
		ArrayList<E> list = new ArrayList<E>();
		if(queryResult.count() == 0)
			return list;
		
		Iterator<E> it = queryResult.iterator();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}

		return list;

	}

	public List<E> queryAndOrder(String condition, Object value, String orderField) {
		Iterator<E> it = ofy().consistency(Consistency.STRONG).load().type(clz).filter(condition, value).order(orderField).iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}

		return list;

	}

	public List<E> query2(String condition1, String value1, String condition2, String value2) {
		Query<E> queryResult = ofy().consistency(Consistency.STRONG).load().type(clz).filter(condition1, value1).filter(condition2, value2);
		Iterator<E> it = queryResult.iterator();
		ArrayList<E> list = new ArrayList<E>();
		while (it.hasNext()) {
			E obj = it.next();
			list.add(obj);
		}

		return list;
	}

}