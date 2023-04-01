import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.*;
import java.io.IOException;
import java.io.PrintWriter;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.util.ArrayList;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String real_password = null;
        String customer_id = null;
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                // If the recaptcha response is null, then the client page does not suppport recaptcha (e.g. andriod)
                if (gRecaptchaResponse != null) {
                    RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                }
                String query = "select customers.password, customers.id from customers where customers.email=? ;";

                // Perform the query
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, email);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                while (rs.next()) {
                    real_password = rs.getString("password");
                    customer_id = rs.getString("id");
                }

                if (customer_id == null) {
                    query = "select employees.password from employees where employees.email=? ;";
                    statement = conn.prepareStatement(query);
                    statement.setString(1, email);
                    rs = statement.executeQuery();

                    while (rs.next()) {
                        real_password = rs.getString("password");
                        customer_id = "employee";
                    }
                }

                JsonObject responseJsonObject = new JsonObject();
                boolean success = false;
                success = new StrongPasswordEncryptor().checkPassword(password, real_password);

                if (success) {
                    // set this user into the session
                    request.getSession().setAttribute("user", new User(email, customer_id));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    if (customer_id.equals("employee")) {
                        responseJsonObject.addProperty("identity", "employee");
                    } else {
                        responseJsonObject.addProperty("identity", "user");
                    }

                } else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");

                    // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                    if (real_password == null) {
                        responseJsonObject.addProperty("message", email + " doesn't exist");
                    } else {
                        responseJsonObject.addProperty("message", "incorrect password");
                    }
                }
                response.getWriter().write(responseJsonObject.toString());
                rs.close();
                statement.close();
                // Set response status to 200 (OK)
                response.setStatus(200);
            }
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }
}
