package edu.virginia.lib.fupload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
 
/**
 * A Java servlet that handles file upload from client.
 *
 * @author www.codejava.net
 */
public class Fupload extends HttpServlet {
    private static final long serialVersionUID = 1L;
     
    // location to store file uploaded
    private static final String UPLOAD_DIR = "upload";
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024;  // 1K
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 200; // 200MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 210; // 210MB
 
    /**
     * Upon receiving file upload submission, parses the request to read
     * upload data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            PrintWriter writer = response.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
	
	String config_dir = this.getInitParameter("upload_dir");
	String uploadPath = "";
	if ((config_dir != null) && (!config_dir.equals(""))) {
		uploadPath = config_dir;
	} else {
        	uploadPath = getServletContext().getRealPath("")
                	+ File.separator + UPLOAD_DIR;
	}
         
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
		String msg = "Processed - ";
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
			if ((fileName == null) || fileName.equals("")) {
				throw new Exception("empty file name.");
			}
			/* if passed a path, lop off last bit */
			/*   or throw up your hands if there's no last bit */
			int i = -1;
			if ((i = fileName.lastIndexOf("/")) > -1) {
				if (++i < fileName.length()) {
					fileName = fileName.substring(i);
				} else {
					throw new Exception("illegal path name ending '/'");
				}
			}
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
 
                        // saves the file on disk
                        item.write(storeFile);
			msg = msg + fileName + " has been uploaded to " + uploadPath;
                        request.setAttribute("message",
                            msg);
                    }
                }
            }
        } catch (Exception ex) {
	    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("message",
                    "There was an error: " + ex.getMessage());
        }
        // redirects client to message page
        getServletContext().getRequestDispatcher("/message.jsp").forward(
                request, response);
    }
}
