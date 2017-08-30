package edu.virginia.lib.fupload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
 
/**
 * A Java servlet that handles file upload from client.
 *
 * @author www.codejava.net
 */
public class Prune extends HttpServlet {
    private static final long serialVersionUID = 1L;
     
    // location to store file uploaded
    private static final String UPLOAD_DIR = "upload";
    private static String config_dir;
    private static String uploadPath;
 
 
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	config_dir = this.getInitParameter("upload_dir");
	uploadPath = "";
	if ((config_dir != null) && (!config_dir.equals(""))) {
		uploadPath = config_dir;
	} else {
        	uploadPath = getServletContext().getRealPath("")
                	+ File.separator + UPLOAD_DIR;
	}
    }
	
    /**
     * Upon receiving file upload submission, parses the request to read
     * upload data and saves the file on disk.
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
 
	String fileName = request.getParameter("fileName");
	String msg = "";
        try {
	    if ((fileName == null) || fileName.equals("") || fileName.contains("..")) {
		throw new Exception("illegal or missing file name - " + fileName);
	    }
	    int i = -1;
	    if ((i = fileName.lastIndexOf("/")) > -1) {
		if (++i < fileName.length()) {
			fileName = fileName.substring(i);
		} else {
			throw new Exception("illegal path name ending '/'");
		}
	    }
            String filePath = uploadPath + File.separator + fileName;
	    File dFile = new File(filePath);
	    if (dFile.delete()) {
            	msg = filePath + " deleted.";
	    } else {
            	msg = filePath + " not deleted.";
	    }

	
	    response.setStatus(response.SC_OK);
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    response.getWriter().write("{\n\"retcode\": \"200\",\n\"msg\": \"" + msg + "\"\n}");
        } catch (Exception ex) {
	    response.setStatus(response.SC_BAD_REQUEST);
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    response.getWriter().write("{\n\"retcode\": \"400\",\n\"msg\": \"" + ex.getMessage() + "\"\n}");
        }
    }
}
