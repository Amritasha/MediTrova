import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Check extends HttpServlet {

    private String jdbcDriver   = "";
    private String jdbcURL      = "";
    private String jdbcUser     = "";
    private String jdbcPass     = "";
    private String homeURL      = "";


    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();

        jdbcDriver  = context.getInitParameter("jdbcDriver");
        jdbcURL     = context.getInitParameter("jdbcURL");
        jdbcUser    = context.getInitParameter("jdbcUser");
        jdbcPass    = context.getInitParameter("jdbcPass");
        homeURL     = context.getInitParameter("homeURL");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException
    {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        StringBuffer requestURL = request.getRequestURL();
        // out.print("<html><h2>checking page</h2></html>");
        String link = requestURL.substring(homeURL.length()+5,requestURL.length());
        //out.println(path+"<br>"+requestURL+"<br>"+link+"<br>");
        // used to find from where it is redirected to
        String redir = request.getHeader("referer"); // Yes, with the legendary misspelling.

        if(!redir.equals(homeURL)){
            //out.println(redir);
                //    response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 page not found
        }
        else{
            //out.print(redir);
            try{
                Class.forName(jdbcDriver);
                Connection conn = DriverManager.getConnection(jdbcURL ,jdbcUser,jdbcPass);
                String query = "SELECT * FROM m.link_detail WHERE link=\""+link+"\" ;";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query);
                if (!rs.next())
                    out.println("Available");
                else{
                    out.println("Not Available");
                }
                conn.close();
            }
            catch (Exception e){
             // out.println("Got an exception! ");
              out.println("Invalid URL");
            }
            finally{
                
                out.close();
            }
        }
   }
}
