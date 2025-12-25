package backend;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.*;

public class AttendQueryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){ sb.append(line); }

            JSONObject data = new JSONObject(sb.toString());
            int qid = data.getInt("qid");

            Connection con = DBConnection.getConnection();

            // Mark attended
            PreparedStatement pst = con.prepareStatement(
                "UPDATE queries SET attended=1 WHERE qid=?");
            pst.setInt(1, qid);
            pst.executeUpdate();

            // Insert into attended table
            String sql2 = "INSERT INTO attended(qid,contact,doctor,treatment,remarks) VALUES(?,?,?,?,?)";
            PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setInt(1, qid);
            pst2.setString(2, data.getString("contact"));
            pst2.setString(3, data.getString("doctor"));
            pst2.setString(4, data.getString("treatment"));
            pst2.setString(5, data.optString("remarks",""));
            pst2.executeUpdate();

            resp.getWriter().write("{\"status\":\"attended\"}");

        } catch(Exception e) {
            resp.getWriter().write("{\"error\":\""+e+"\"}");
        }
    }
}
