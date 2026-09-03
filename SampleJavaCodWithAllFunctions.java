import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "Main", value = "/Main")
public class Main extends HttpServlet {

    private static final String dburl = "jdbc:mysql://localhost:3306/jdbcdb";
    private static final String user = "root";
    private static final String password = "4545";

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        allowCrossOrigin(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }


    public void allowCrossOrigin(HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(dburl, user, password);



            return conn;
        }
        catch (Exception e) {
//            String msg = e.getMessage();
            return null ;
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        allowCrossOrigin(request, response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        PrintWriter out = response.getWriter();

        try {
            Connection conn = getConnection();
            String query = "select * from employees";

            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            String al = "[";
            boolean first = true;

            while (resultSet.next()) {

                int id = resultSet.getInt("employee_id");
                String name = resultSet.getString("employee_name");
                int age = resultSet.getInt("age");
                String department = resultSet.getString("department");

                String obj = "{\"id\":" + id
                        + ",\"name\":\"" + name
                        + "\",\"age\":" + age
                        + ",\"department\":\"" + department + "\"}";

                if (!first) {
                    al += ",";
                }

                al += obj;
                first = false;
            }

            al += "]";

            out.println(al);


        }
        catch (Exception e) {
            e.printStackTrace();
        }






    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        allowCrossOrigin(request, response);



        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            int age = Integer.parseInt(request.getParameter("age"));
            String department = request.getParameter("department");

            String sql = "INSERT INTO employees " +
                    "(employee_id, employee_name, age, department) " +
                    "VALUES (?, ?, ?, ?)";

            try (
                    Connection conn = getConnection();
                    PreparedStatement preparedStatement = conn.prepareStatement(sql)
            ) {

                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, department);

                int result = preparedStatement.executeUpdate();

                if (result == 1) {

                    response.setStatus(HttpServletResponse.SC_CREATED);

                    out.print("{\"message\":\"Employee Inserted\"}");

                } else {

                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                    out.print("{\"message\":\"Error Occurred While Inserting Employee\"}");
                }
            }

        } catch (NumberFormatException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            out.print("{\"message\":\"Invalid ID or Age\"}");

            e.printStackTrace();

        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.print("{\"message\":\"" + e.getMessage() + "\"}");

            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request , HttpServletResponse response) throws ServletException , IOException {

        allowCrossOrigin(request, response);
        response.setContentType("application/jsom");
        response.setCharacterEncoding("UTF-8");


        PrintWriter out = response.getWriter();


        int employee_id = Integer.parseInt(request.getParameter("id"));
        try {

            Connection conn = getConnection();

            Statement statement = conn.createStatement();
            
            String query = "delete from employees where employee_id = " + employee_id;

            int result = statement.executeUpdate(query);

            if (result == 1) {
                out.print("{\"message\":\"Employee Deleted SuceesFully \"}");


            }
            else{
                out.println("{\"message\":\"Employee DeletionFailed\"}");

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
