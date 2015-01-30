package uk.org.harwellcroquet.server;

import java.io.FileInputStream;
import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.bean.StoredFileBean;

@SuppressWarnings("serial")
public class FileDownloadServlet extends HttpServlet {

	final static Logger logger = Logger.getLogger(FileDownloadServlet.class);

	@EJB
	private StoredFileBean fileBean;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {

		String name = (String) req.getParameter("name");
		if (name.endsWith(".pdf")) {
			response.setContentType("application/pdf");
		}
		String[] bits = name.split("/");
		response.setHeader("Content-disposition", "inline; filename=" + bits[bits.length - 1]);
		ServletOutputStream sos = response.getOutputStream();

		try (FileInputStream fis = new FileInputStream(fileBean.getFile(name))) {
			byte[] buff = new byte[1024];
			int n;
			while ((n = fis.read(buff)) >= 0) {
				sos.write(buff, 0, n);
			}
		} 
	}
}
