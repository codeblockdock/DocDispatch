package backend;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import org.json.*;

public class AddQueryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){ sb.append(line); }
            
            JSONObject data = new JSONObject(sb.toString());

            String sql = "INSERT INTO queries(contact,name,age,gender,temperature,days,contagious) VALUES(?,?,?,?,?,?,?)";
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, data.getString("contact"));
            pst.setString(2, data.getString("name"));
            pst.setInt(3, data.getInt("age"));
            pst.setString(4, data.getString("gender"));
            pst.setInt(5, data.getInt("temperature"));
            pst.setInt(6, data.getInt("days"));
            pst.setString(7, data.getString("contagious"));

            pst.executeUpdate();

            resp.getWriter().write("{\"status\":\"success\"}");

        } catch(Exception e) {
            resp.getWriter().write("{\"status\":\"error\",\"message\":\""+e+"\"}");
        }
    }
}
