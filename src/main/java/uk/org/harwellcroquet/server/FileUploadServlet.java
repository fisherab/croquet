package uk.org.harwellcroquet.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.bean.LoginBean;
import uk.org.harwellcroquet.server.bean.StoredFileBean;
import uk.org.harwellcroquet.server.entity.User;

@SuppressWarnings("serial")
public class FileUploadServlet extends HttpServlet {

	@EJB
	private StoredFileBean fileBean;

	@PersistenceContext(unitName = "croquet")
	private EntityManager entityManager;

	final static Logger logger = Logger.getLogger(FileUploadServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		ServletFileUpload upload = new ServletFileUpload();
		resp.setContentType("text/plain");

		String filename = null;
		String sessionid = null;
		File dir = Paths.get("..", "data", "croquet", "files").toFile();
		File temp = File.createTempFile("harwellcroquet.", null, dir);
		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();

				if (!item.isFormField()) {
					FileUploadServlet.logger.debug("Got an uploaded file: " + item.getFieldName() + ", name = "
							+ item.getName());

					byte[] buffer = new byte[1024];
					FileOutputStream fos = new FileOutputStream(temp);
					int n;
					while ((n = stream.read(buffer)) != -1) {
						fos.write(buffer, 0, n);
					}
					fos.close();
				} else {
					String value = Streams.asString(stream);
					String name = item.getFieldName();
					FileUploadServlet.logger.debug("Got a field: " + item.getFieldName() + " = " + name);
					if (name.equals("filename")) {
						filename = value;
					} else if (name.equals("sessionid")) {
						sessionid = value;
					}
				}
				stream.close();
			}

		} catch (FileUploadException e) {
			logger.debug("FileUploadException " + e.getMessage());
			throw new ServletException(e.getMessage());
		}

		try {
			User ui = LoginBean.getUser(entityManager, sessionid);
			String id = Long.toString(fileBean.storeFileName(ui.getId(), filename));
			resp.getWriter().print("File upload succesful");
			temp.renameTo(new File(dir, id));
		} catch (Throwable e) {
			logger.debug(e);
			throw new ServletException(e.getMessage());
		}

	}
}
