package com.example.servletshop;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

@WebServlet(name = "OrderServlet", value = "/OrderServlet")
public class OrderServlet extends HttpServlet {
    @Resource(name="jdbc/ebookshop")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (
                // Allocate mysql Connection object
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
        ) {
            String[] ids = request.getParameterValues("id");
            if (ids != null) {
                String sqlStr;
                int count;

                // Process each of the books
                for (int i = 0; i < ids.length; ++i) {
                    // Update the qty of the table books
                    sqlStr = "UPDATE book SET qty = qty - 1 WHERE id = " + ids[i];
                    out.println("<p>" + sqlStr + "</p>");  // for debugging
                    count = stmt.executeUpdate(sqlStr);
                    out.println("<p>" + count + " record updated.</p>");

                    // Create a transaction record
                    sqlStr = "INSERT INTO order_records (id, qty_ordered) VALUES ("
                            + ids[i] + ", 1)";
                    out.println("<p>" + sqlStr + "</p>");  // for debugging
                    count = stmt.executeUpdate(sqlStr);
                    out.println("<p>" + count + " record inserted.</p>");
                    out.println("<h3>Your order for book id=" + ids[i]
                            + " has been confirmed.</h3>");
                }
                out.println("<h3>Thank you.<h3>");
            } else {
                out.println("<h3>Please go back and select a book...</h3>");
            }

        } catch (Exception e) {
                out.println("<p>Error: " + e.getMessage() + "</p>");
                out.println("<p>Check Tomcat console for details.</p>");
                e.printStackTrace();

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
