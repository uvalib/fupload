package edu.virginia.lib.fupload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.text.DateFormat;
import java.util.Locale;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
 
/**
 * A Java servlet that lists files in upload dir
 *
 * @author www.codejava.net
 */
public class ListFiles extends HttpServlet {
    private static final long serialVersionUID = 1L;
     
    // location to store file uploaded
    private static final String UPLOAD_DIR = "upload";
    private static String config_dir;
    private static String uploadPath;
    private DateFormat df;
 
 
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
	df = DateFormat.getDateInstance(DateFormat.LONG,Locale.US);
    }
	
    /**
     *  List out files in upload dir
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
 
	String msg = "";
        try {
	    response.setStatus(response.SC_OK);
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    response.getWriter().write("\n{\"dirlisting\": [\n");
	    File dir = new File(uploadPath);
	    File [] files = dir.listFiles();
	    boolean commaHack = false;
	    for (File aFile : files ) {
		String fName = aFile.getName();
		String mDate = df.format(new java.util.Date(aFile.lastModified()));
		long fLen = aFile.length();
	    	response.getWriter().write("\t{\"fileName\": \"" +
				fName + "\",\"modDate\": \"" + mDate + "\",\"fileLen\": \"" + fLen + "\"},\n");
	    }
	    response.getWriter().write("{}]}\n");
        } catch (Exception ex) {
	    response.setStatus(response.SC_BAD_REQUEST);
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    response.getWriter().write("{\n\"retcode\": \"400\",\n\"msg\": \"" + ex.getMessage() + "\"\n}");
        }
    }
}
