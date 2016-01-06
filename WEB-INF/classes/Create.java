import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Create extends HttpServlet {

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


    public String shorten(Connection conn, Statement st){
        int id=0;
        try{
            String query = "SELECT id FROM m.link_detail ORDER BY id DESC LIMIT 1 ;";
            ResultSet rs = st.executeQuery(query);

            rs.first();
            id = rs.getInt("id");
        }
        catch (Exception e){}
        id++;
        String temp = Integer.toString(id,36);
        return temp;
    }


    public int chkagain(Connection conn, Statement st,String s)
    {
        String link=new String("/"+s);
            try{
                String query = "SELECT * FROM m.link_detail WHERE link=\""+link+"\" ;";
                ResultSet rs = st.executeQuery(query);
                if (!rs.next())
                    return 1; // link is available
                else{
                    return 0; // link is not available
                }
            }
            catch (Exception e){
             // out.println("Got an exception! ");
            }
        return 0;
     }


public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException
{
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        String redir = request.getHeader("referer"); // Yes, with the legendary misspelling.
        //out.print(" redir = "+ redir +"<br>");
        if(!redir.equals(homeURL)){
                    response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 page not found
        }
        else{
            try{
                int x=1;
                Class.forName(jdbcDriver);
                Connection conn = DriverManager.getConnection(jdbcURL ,jdbcUser,jdbcPass);
                Statement st = conn.createStatement();

                String org_url=request.getParameter("url");
                String temp=request.getParameter("custom");
                if(temp != null && temp.isEmpty()){
                    temp=shorten(conn,st);
                }
                else{// if not empyt check again
                    x = chkagain(conn,st,temp);
                }
                String link= new String("/"+temp);

                if(x==1){
                    // to get IP of the user
                    String ipAddress = request.getHeader("X-FORWARDED-FOR");
                    if(ipAddress == null){
               	        ipAddress = request.getRemoteAddr();
                    }

                    // get date and time
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date dateobj = new Date();
                    String time = (String) df.format(dateobj) ;

                    String query = "INSERT into m.link_detail values(NULL, ?, ?, 0, ?, ?)";
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.setString   (1, link);
                    preparedStmt.setString   (2, org_url);
                    preparedStmt.setString   (3, ipAddress);
                    preparedStmt.setString   (4, time);
                    preparedStmt.executeUpdate();
                    out.println("ok="+link);
                    conn.close();
                }
                else{
                    out.println("Custom URL not availble");
                }
             conn.close();   
            }
            catch(Exception e){
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                //out.println("Got an exception! ");
                //out.println(e.getMessage());
            }
        }
        out.close();
   }
}
