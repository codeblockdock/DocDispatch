package backend;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.*;

public class GetQueriesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String contact = req.getParameter("contact");
        JSONArray arr = new JSONArray();

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT qid, contact, attended FROM queries WHERE contact=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, contact);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                JSONObject obj = new JSONObject();
                int attended = rs.getInt("attended");
                obj.put("qid", rs.getInt("qid"));
                obj.put("contact", rs.getString("contact"));
                obj.put("attended", attended);

                if(attended == 1) {
                    PreparedStatement pst2 = con.prepareStatement("SELECT * FROM attended WHERE qid=?");
                    pst2.setInt(1, rs.getInt("qid"));
                    ResultSet rs2 = pst2.executeQuery();
                    if(rs2.next()) {
                        obj.put("doctor", rs2.getString("doctor"));
                        obj.put("treatment", rs2.getString("treatment"));
                        obj.put("remarks", rs2.getString("remarks"));
                        obj.put("attended_at", rs2.getString("attended_at"));
                    }
                }
                arr.put(obj);
            }
            resp.getWriter().write(arr.toString());
        } catch(Exception e){
            resp.getWriter().write("{\"error\":\""+e+"\"}");
        }
    }
}
