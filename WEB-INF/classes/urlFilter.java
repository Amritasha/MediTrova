import java.io.*;
import java.util.*;
import java.sql.*;
import java.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;

// we have use filter instead fo servelet cos we need to make decision ater all request came to one place
// if we use servelet /* would redirect all request to the defined servelet
// but using filter we can sort request after comming inside filter
public class urlFilter implements Filter{

    private String jdbcDriver   = "";
    private String jdbcURL      = "";
    private String jdbcUser     = "";
    private String jdbcPass     = "";
    private String homeURL      = "";

    public void init(FilterConfig config) throws ServletException {
        // here we r getting init param value via filtercnfig not by servelet context
        jdbcDriver  = config.getInitParameter("jdbcDriver");
        jdbcURL     = config.getInitParameter("jdbcURL");
        jdbcUser    = config.getInitParameter("jdbcUser");
        jdbcPass    = config.getInitParameter("jdbcPass");
        homeURL     = config.getInitParameter("homeURL");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{

        response.setContentType("text/html");
        PrintWriter out=response.getWriter();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        StringBuffer temp = httpRequest.getRequestURL();
        String link = temp.toString();

        int len=homeURL.length();
        String final_uri="";
        //out.println("<br>"+homeURL+"<br>"+link+"<br>");

        if(link.equals(homeURL)){ // use to compare contents of string woth string buffer
            // just to count ip of visiters
            String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
            if(ipAddress == null){
           	   ipAddress = httpRequest.getRemoteAddr();
            }
            try { // always call db conn inside try catch
                Class.forName(jdbcDriver);
                Connection conn = DriverManager.getConnection(jdbcURL ,jdbcUser,jdbcPass);
                // to update visit db
                String query  = "INSERT into m.visit values(NULL, ?, ? )";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString   (1, "/"); //#can change#
                preparedStmt.setString  (2, ipAddress);
                preparedStmt.executeUpdate();
                conn.close();
            }
            catch (Exception e){
              out.println("Some internal problem has occured!");
              //out.println(e);
            }
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.html");
            requestDispatcher.include(request, response);
        }
        else if(link.equals(homeURL+"create/")){
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/create");
            requestDispatcher.include(request, response);
        }
        else{
            //out.println("<br>"+org_url+"<br>"+link+"<br>");
            // to access static files
            if(link.matches("(.*).js")){
                chain.doFilter(request, response);
            }
            else if(link.matches("(.*).css")){
                chain.doFilter(request, response);
            }
            // !!! change !!!
           else if(link.charAt(len)=='@'){
               final_uri = "/check/"+link.substring(len+1,link.length());
               //out.println("<br>"+final_uri+"<br>");
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(final_uri);
                requestDispatcher.forward(request, response);
            }
            else{
                final_uri = "/go/"+link.substring(len,link.length());
                //out.println("<br>"+final_uri+"<br>");
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(final_uri);
                requestDispatcher.forward(request, response);
                // we r not using send rerect as it will generate new request which will agar be redirected to filter
                // but filter is used to send request to servelet after applyin some dicesion to where req must go
            }
            // and we are not chaining as we do not need any filter with send requests
            //chain.doFilter(request, response);*/
        }
        //out.println( "<br>filter is invoked after  <br>");
        out.close();
    }

    public void destroy() {}
}
