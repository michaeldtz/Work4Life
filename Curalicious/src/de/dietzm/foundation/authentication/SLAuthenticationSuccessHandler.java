package de.dietzm.foundation.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("SLAuthenticationSuccessHandler")
public class SLAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		//super.onAuthenticationSuccess(request, response, authentication);

		//HttpSession session = request.getSession();
		
		if ("true".equals(request.getHeader("z_noredirect"))) {
			try {
				response.setContentType("application/json");
				response.getWriter().print("{ \"success\" : true }");// return "ok" string
				response.getWriter().flush();
			} catch (IOException e) {
				logger.error(e.toString());
			}
		} else {
			setAlwaysUseDefaultTargetUrl(false);
		}

	}
}
