package edu.virginia.lib.fupload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.Hashtable;
 
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
public class Fupload extends HttpServlet {
    private static final long serialVersionUID = 1L;
     
    // location to store file uploaded
    private static final String UPLOAD_DIR = "upload";
    private static String config_dir;
    private static String uploadPath;
    private static String convBin;
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024;  // 1K
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 200; // 200MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 210; // 210MB

    // image magick command args
    private static final String TIFARGS  = "-delete 1--1 -define jp2:rate=1.0,0.5,0.25";
    private static final String JPGARGS  = "-define jp2:rate=1.0,0.5,0.25";
    private static final String JP2ARGS  = "-define jp2:rate=1.0,0.5,0.25";
    private static final String DEFCONVERTBIN = "/usr/bin/convert";
    private Hashtable<String,String> convArgs;

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
	convBin = this.getInitParameter("convert_bin");
	if (convBin == null) convBin = DEFCONVERTBIN;
	convArgs = new Hashtable<String, String>();
	convArgs.put(".tif",TIFARGS);
	convArgs.put(".TIF",TIFARGS);
	convArgs.put(".jpg",JPGARGS);
	convArgs.put(".JPG",JPGARGS);
	convArgs.put(".jp2",JP2ARGS);
    }
 
    /**
     * Upon receiving file upload submission, parses the request to read
     * upload data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
		throw new ServletException("Bad request - not multipart form");
        }
 
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 
        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);
	    String msg = ""; 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
		msg = "Processed - ";
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        String inFileName = new File(item.getName()).getName();
			int inLen = inFileName.length();
			if ((inFileName == null) || inFileName.equals("") || 
				(inLen < 5) ||
				inFileName.endsWith("/")) {
				throw new Exception("no image name");
			}
			/* check file extension */
			String inExt = inFileName.substring(inLen - 4);
			String args= convArgs.get(inExt);
			if (args == null) {
				throw new Exception("illegal file extension for "  + inFileName);
			}
			/* if passed a path, lop off last bit */
			int i = inFileName.lastIndexOf("/");
			if (i  > -1) {
				inFileName = inFileName.substring(++i);
			}
                        String inFilePath = uploadPath + File.separator + inFileName;
                        File inFile = new File(inFilePath);
 
                        // save the tiff file on disk
                        item.write(inFile);
				String filePathJp2 = inFilePath.replace(inExt,".jp2");
				// convert  jp2
				String convCmd = convBin + " " + inFilePath + " " + args + " " + filePathJp2;
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec(convCmd);
				pr.waitFor();
				int retCode = pr.exitValue();
				pr.destroy();
	        		if (retCode != 0) {
	            			throw new Exception("Error " + retCode + " occured running " + convCmd);
	        		}
				msg = msg + filePathJp2 + " has been created. ";
				if (!inExt.equals(".jp2") && inFile.delete()) {
                			msg = msg + inFilePath + " deleted. ";
            			} 
			}
                    }
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
