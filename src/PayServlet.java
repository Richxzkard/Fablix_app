//import com.google.gson.JsonObject;
//import com.google.gson.JsonArray;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.servlet.ServletConfig;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//
//@WebServlet(name = "PayServlet", urlPatterns = "/api/pay")
//public class PayServlet extends HttpServlet {
//    /**
//     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//     */
//    private static final long serialVersionUID = 1L;
//
//    // Create a dataSource which registered in web.
//    private DataSource dataSource;
//    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String first_name = request.getParameter("first_name");
//        String last_name = request.getParameter("last_name");
//        String card_number = request.getParameter("card_number");
//        String expiration_date = request.getParameter("expiration_date");
//
//        String checkfirst = null;
//        String checklast = null;
//        String checkdate = null;
//
//        PrintWriter out = response.getWriter();
//
//
//        try (Connection conn = dataSource.getConnection()) {
//
//            // return fail if the card_number is empty.
//            if (card_number.isEmpty()){
//                throw new Exception();
//            }
//
//            String query = "select firstName, lastName, expiration from moviedb.creditcards where creditcards.id = \"" + card_number + "\";";
//
//            // Perform the query
//            PreparedStatement statement = conn.prepareStatement(query);
//
//            // Perform the query
//            ResultSet rs = statement.executeQuery();
//
//            // Iterate through each row of rs
//            while (rs.next()) {
//                checkfirst = rs.getString("firstName");
//                checklast = rs.getString("lastName");
//                checkdate = rs.getString("expiration");
//            }
//
//            JsonObject responseJsonObject = new JsonObject();
//            if (first_name.equals(checkfirst) || last_name.equals(checklast) || expiration_date.equals(checkdate)) {
//                // checkout, store the payment record and return a success message
//                HttpSession session = request.getSession();
//
//                // get the previous items in a ArrayList
//                ArrayList<HashMap> previousItems = (ArrayList<HashMap>) session.getAttribute("previousItems");
//                User user = (User) session.getAttribute("user");
//
//                String date = String.valueOf(java.time.LocalDate.now());
//                Float total_price = Float.valueOf(0);
//
//                for (HashMap item: previousItems){
//                    //generate sql method
//                    // STILL NEEDS IMPLEMENTATION TO ADD SALES
//                    String movieId = (String) item.get("id");
//                    String title = (String) item.get("title");
//                    Float price = Float.valueOf((String) item.get("price"));
//                    Integer number = Integer.valueOf((String) item.get("number"));
//
//                    total_price = total_price + (price*number);
//
//                    String add_query = "INSERT INTO `moviedb`.`sales` (`customerId`, `movieId`, `saleDate`)" +
//                            " VALUES (\'" + user.userId + "\', \'"+ movieId +"\', \'" + date + "\');";
//                }
//
//                // add your code here to empty the session cart
//                session.setAttribute("previousItems", new ArrayList<HashMap<String,String>>());
//                // write out success message
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("message", "Payment success, the total is " + total_price.toString());
//                out.write(jsonObject.toString());
//
//
//
//            } else {
//                // Payment fail
//                responseJsonObject.addProperty("status", "fail");
//                // Log to localhost log
//                request.getServletContext().log("Invalid payment info.");
//
//                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
//                responseJsonObject.addProperty("message", "Invalid payment info.");
//            }
//            response.getWriter().write(responseJsonObject.toString());
//            rs.close();
//            statement.close();
//            // Set response status to 200 (OK)
//            response.setStatus(200);
//
//        } catch (Exception e) {
//
//            // Write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("message", "Invalid payment info.");
//            out.write(jsonObject.toString());
//
//            // Set response status to 500 (Internal Server Error)
//            response.setStatus(500);
//        } finally {
//            out.close();
//        }
//
//    }
//}
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "PayServlet", urlPatterns = "/api/pay")
public class PayServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String card_number = request.getParameter("card_number");
        String expiration_date = request.getParameter("expiration_date");

        String checkfirst = null;
        String checklast = null;
        String checkdate = null;

        PrintWriter out = response.getWriter();


        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                // return fail if the card_number is empty.
                if (card_number.isEmpty()) {
                    throw new Exception();
                }

                String query = "select firstName, lastName, expiration from moviedb.creditcards where creditcards.id = ? ";

                // Perform the query
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, card_number);
                // Perform the query
                ResultSet rs = statement.executeQuery();

                // Iterate through each row of rs
                while (rs.next()) {
                    checkfirst = rs.getString("firstName");
                    checklast = rs.getString("lastName");
                    checkdate = rs.getString("expiration");
                }

                JsonObject responseJsonObject = new JsonObject();
                if (first_name.equals(checkfirst) && last_name.equals(checklast) && expiration_date.equals(checkdate)) {
                    // checkout, store the payment record and return a success message
                    HttpSession session = request.getSession();

                    // get the previous items in a ArrayList
                    ArrayList<HashMap> previousItems = (ArrayList<HashMap>) session.getAttribute("previousItems");

                    if (previousItems.isEmpty()) {
                        responseJsonObject.addProperty("status", "fail");
                        // Log to localhost log
                        request.getServletContext().log("Shopping cart is empty.");

                        // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                        responseJsonObject.addProperty("message", "Shopping cart is empty.");
                    } else {
                        User user = (User) session.getAttribute("user");
                        String date = String.valueOf(java.time.LocalDate.now());
                        Float total_price = Float.valueOf(0);

                        for (HashMap item : previousItems) {
                            //generate sql method
                            // STILL NEEDS IMPLEMENTATION TO ADD SALES
                            String movieId = (String) item.get("id");
                            String title = (String) item.get("title");
                            Float price = Float.valueOf((String) item.get("price"));
                            Integer number = Integer.valueOf((String) item.get("number"));

                            total_price = total_price + (price * number);

                            String add_query = "INSERT INTO `moviedb`.`sales` (`customerId`, `movieId`, `saleDate`)" +
                                    " VALUES (\'" + user.userId + "\', \'" + movieId + "\', \'" + date + "\');";

                            System.out.println(add_query);
                            statement.executeUpdate(add_query);
                        }

                        // add your code here to empty the session cart
                        session.setAttribute("previousItems", new ArrayList<HashMap<String, String>>());
                        // write out success message

                        responseJsonObject.addProperty("message", "Payment success, the total is " + total_price.toString());
//                out.write(responseJsonObject.toString());

                    }


                } else {
                    // Payment fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Invalid payment info.");

                    // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                    responseJsonObject.addProperty("message", "Invalid payment info.");
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
            jsonObject.addProperty("message", "Invalid payment info.");
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // get the previous items in a ArrayList

    }
}
