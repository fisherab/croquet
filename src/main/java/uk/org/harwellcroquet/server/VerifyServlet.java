package uk.org.harwellcroquet.server;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.org.harwellcroquet.server.bean.VerifyBean;

@SuppressWarnings("serial")
public class VerifyServlet extends HttpServlet {

	@EJB
	private VerifyBean verifyBean;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		verifyBean.doGet(req, resp);
	}

}
