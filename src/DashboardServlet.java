import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Star input
        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");

        // Movie input
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                if (name != null) {
                    String query = "select id from stars order by id desc limit 1;";
                    PreparedStatement statement = conn.prepareStatement(query);
                    ResultSet rs = statement.executeQuery();
                    String id = "";
                    while (rs.next()) {
                        id = rs.getString("id");
                    }
                    rs.close();
                    statement.close();

                    String newid = "nm" + String.valueOf(Integer.parseInt(id.substring(2)) + 1);
                    String insert_query = "INSERT INTO stars VALUES(?,?,?);";
                    statement = conn.prepareStatement(insert_query);
                    statement.setString(1, newid);
                    statement.setString(2, name);
                    if (!birthYear.equals("")) {
                        statement.setString(3, birthYear);
                    } else {
                        statement.setNull(3, Types.INTEGER);
                    }
                    statement.executeUpdate();

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("message", "Star ID is " + newid);
                    // Write JSON string to output
                    out.write(jsonObject.toString());
                    // Set response status to 200 (OK)
                    response.setStatus(200);

                } else if (title != null) {

                    String movie_query = "call add_movie(?,?,?,?,?,@re1,@re2,@re3);";
                    PreparedStatement statement = conn.prepareStatement(movie_query);
                    statement.setString(1, title);
                    statement.setString(2, year);
                    statement.setString(3, director);
                    statement.setString(4, star);
                    statement.setString(5, genre);
                    statement.executeQuery();
                    statement.close();

                    movie_query = "select @re1,@re2,@re3;";
                    statement = conn.prepareStatement(movie_query);
                    ResultSet rs = statement.executeQuery();
                    String movieId = null;
                    String starId = null;
                    String genreId = null;
                    while (rs.next()) {
                        movieId = rs.getString("@re1");
                        starId = rs.getString("@re2");
                        genreId = rs.getString("@re3");
                    }
                    rs.close();
                    statement.close();

                    JsonObject jsonObject = new JsonObject();
                    if (movieId != null) {
                        jsonObject.addProperty("message", "movieId is " + movieId + " starId is " + starId + " genreId is " + genreId);
                    } else {
                        jsonObject.addProperty("message", "failed due to duplicated movie");
                    }

                    // Write JSON string to output
                    out.write(jsonObject.toString());
                    // Set response status to 200 (OK)
                    response.setStatus(200);

                }
            }
        }catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                JsonArray resultArray = new JsonArray();
                String query = "show fields from creditcards;";
                PreparedStatement statement = conn.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                JsonArray jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from customers;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from employees;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from genres;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from genres_in_movies;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from movies;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();


                query = "show fields from ratings;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from sales;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from stars;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                query = "show fields from stars_in_movies;";
                statement = conn.prepareStatement(query);
                rs = statement.executeQuery();
                jsonArray = new JsonArray();
                // Iterate through each row of rs
                while (rs.next()) {

                    String Field = rs.getString("Field");
                    String Type = rs.getString("Type");
                    String Null = rs.getString("Null");
                    String Key = rs.getString("Key");
                    String Default = rs.getString("Default");
                    String Extra = rs.getString("Extra");

                    // Create a JsonObject based on the data we retrieve from rs

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Field", Field);
                    jsonObject.addProperty("Type", Type);
                    jsonObject.addProperty("Null", Null);
                    jsonObject.addProperty("Key", Key);
                    jsonObject.addProperty("Default", Default);
                    jsonObject.addProperty("Extra", Extra);

                    jsonArray.add(jsonObject);
                }
                resultArray.add(jsonArray);
                rs.close();
                statement.close();

                // Write JSON string to output
                out.write(resultArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);
            }
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
