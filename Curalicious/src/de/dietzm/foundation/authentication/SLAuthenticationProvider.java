package de.dietzm.foundation.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.dietzm.foundation.usermgmt.UserManagement;
import de.dietzm.foundation.usermgmt.model.SLRole;


public class SLAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		
		String username = auth.getName();
        String password = auth.getCredentials().toString();
        
        UserManagement userMgmt = new UserManagement();
        
        if (username.equals("admin") && password.equals("T7a")) {
        	
        	//SLUser user = userMgmt.getUserByName(username);
        	ArrayList<GrantedAuthority> grantedAuths = new ArrayList<>();
        	        	
        	grantedAuths.add(new SimpleGrantedAuthority("ROLE_CORE_BASIC_USER"));
        	grantedAuths.add(new SimpleGrantedAuthority("ROLE_CORE_ADMIN"));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
            
            return authentication;
            
        } else if (userMgmt.isUserPasswordCombinationCorrect(username, password)) {
        	
        	//SLUser user = userMgmt.getUserByName(username);
        	List<SLRole> roles = userMgmt.getRolesOfUser(username);
        	ArrayList<GrantedAuthority> grantedAuths = new ArrayList<>();
        	        	
        	for (SLRole role : roles) {
        		grantedAuths.add(new SimpleGrantedAuthority(role.getRolename()));
			}
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
            
            return authentication;
        } else {
            throw new BadCredentialsException("Provided user/password combination is not valid");
        }
        
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;		
	}

}
