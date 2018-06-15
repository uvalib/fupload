package edu.virginia.lib.fupload;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DistributeFiles extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private FileMapper mapper;

    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		mapper = new FileMapper(this);
    }
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final int moveCount = mapper.redistributeFromRoot();
	    response.setStatus(response.SC_OK);
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    response.getWriter().write("\n{\"files_moved\": " + moveCount + " }\n");
		response.getWriter().close();
    }
}
