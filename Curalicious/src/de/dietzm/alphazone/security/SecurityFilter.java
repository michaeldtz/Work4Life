package de.dietzm.alphazone.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import de.dietzm.foundation.useraccess.UserAccessFactory;
import de.dietzm.foundation.useraccess.UserAccessService;

public class SecurityFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
			ServletException {

		if (req instanceof HttpServletRequest) {
			String path = ((HttpServletRequest) req).getServletPath();
			
			
			
			if (path != null) {

				if (path.startsWith("labsUser")) {
					UserAccessService uas = UserAccessFactory.getAccessService();
					if (!uas.isApplicationUser()) {
						Response.status(404);
						return;
					}
				} else if (path.startsWith("labsAdmin")) {
					UserAccessService uas = UserAccessFactory.getAccessService();
					if (!uas.isAdmin()) {
						Response.status(404);
						return;
					}
				}
			}
		}

		chain.doFilter(req, resp);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
