package de.dietzm.foundation.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component("SLAuthenticationFailureHandler")
public class SLAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		if ("true".equals(request.getHeader("z_noredirect"))) {
			try {
				response.setContentType("application/json");
				response.getWriter().print("{ \"success\" : false, \"code\": \"AUTH10\", \"message\" : \"" + exception.getLocalizedMessage() + "\" }");// return "ok" string
				response.getWriter().flush();				
			} catch (IOException e) {
				logger.error(e.toString());
			}
		} else {
			setUseForward(true);			
		}
		
		
		
	}
}
