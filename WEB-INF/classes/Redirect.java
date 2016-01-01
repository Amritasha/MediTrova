import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Redirect extends HttpServlet {

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
        //out.print("<html><h2>Redirect page</h2></html>");
        // to get whole url
        StringBuffer requestURL = request.getRequestURL();

        String path = requestURL.substring(homeURL.length()+2,requestURL.length());
        // +2 cos homeurl/go/* go=2 word; 1st / is included in home
        out.print("<html><h2>"+path+"</h2></html>");

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if(ipAddress == null){
       	   ipAddress = request.getRemoteAddr();
        }

        String org_url="";

        try{
            Class.forName(jdbcDriver);
            Connection conn = DriverManager.getConnection(jdbcURL ,jdbcUser,jdbcPass);
            // if you only need a few columns, specify them by name instead of using "*"
            String query = "SELECT * FROM m.link_detail WHERE link=\""+path+"\" ;";
            // create the java statement we can also do it by prepareStatement
            Statement st = conn.createStatement();
            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            if (!rs.next()){
                response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 page not found
            }
            else{
                // this is required else error or we can use while (rs.next())
                rs.first();
                int id = rs.getInt("id");
                org_url = rs.getString("org_url");
                int click = rs.getInt("click");
                //out.println(id+" "+org_url+"  " +click);
                // PreparedStatement allow us to use ? to aviod complication like above
                query = "UPDATE m.link_detail SET click= ? where id = ?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setInt   (1, click+1);
                preparedStmt.setInt   (2, id);
                preparedStmt.executeUpdate();

                // to update visit db
                query  = "INSERT into m.visit values(NULL, ?, ? )";
                preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString   (1, path);
                preparedStmt.setString  (2, ipAddress);
                preparedStmt.executeUpdate();

                try{
                    // now redirecing
                    response.sendRedirect(org_url);
                }
                catch (Exception e){
                  out.println("Got an exception! ");
                  //out.println(e.getMessage());
                }
            }
            conn.close();
        }
        catch (Exception e){
          out.println("Got an exception! ");
          out.println(e.getMessage());
        }
        finally {
            // very imp to close
            out.close();
        }
   }
}
