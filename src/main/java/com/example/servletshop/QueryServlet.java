package com.example.servletshop;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "QueryServlet", value = "/QueryServlet")
public class QueryServlet extends HttpServlet {
    @Resource(name="jdbc/ebookshop")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Query Response</title></head>");
        out.println("<body>");

        try (
                // Allocate mysql Connection object
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
        ) {
            String sqlStr = "select * from book where author = "
                    + "'" + request.getParameter("author") + "'"   // Single-quote SQL string
                    + " and qty > 0 order by price desc";

            out.println("<h3>Thank you for your query.</h3>");
            out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging
            ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server


            out.println("<form method='GET' action='OrderServlet'>");
            while(rset.next()) {
                out.println("<p><input type='checkbox' name='id' value="
                        + "'" + rset.getString("id") + "' />"
                        + rset.getString("author") + ", "
                        + rset.getString("title") + ", $"
                        + rset.getString("price") + "</p>");
            }
            out.println("<p><input type='submit' value='ORDER' />");
            out.println("</form>");

        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("<p>Check Tomcat console for details.</p>");
            e.printStackTrace();
        }
        out.println("</body></html>");
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
