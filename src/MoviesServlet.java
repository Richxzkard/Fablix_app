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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        String pageChange = request.getParameter("pageChange");
        String NChange = request.getParameter("NChange");
        String ratingSort = request.getParameter("rating");
        String titleSort = request.getParameter("title");
        String sortSeq = request.getParameter("sort");
        String typeChange = request.getParameter("type");


        request.getServletContext().log("Page change " + pageChange);
        request.getServletContext().log("N change " + NChange);
        request.getServletContext().log("Rating sort " + ratingSort);
        request.getServletContext().log("Title sort " + titleSort);
        request.getServletContext().log("Sort sequence" + sortSeq);
        request.getServletContext().log("Type " + typeChange);


        HttpSession session = request.getSession();
        String N = (String) session.getAttribute("N");
        String page = (String) session.getAttribute("page");
        String rating = (String) session.getAttribute("rating");
        String title = (String) session.getAttribute("title");
        String sort = (String) session.getAttribute("sort");
        String type = (String) session.getAttribute("type");

        if (N == null) {
            N = "25";
            session.setAttribute("N", N);
        }
        if (NChange != null && !NChange.equals("null") && !NChange.equals("")) {
            N = NChange;
            page = "0";
            session.setAttribute("N", NChange);
            session.setAttribute("page", "0");
        }
        if (page == null) {
            page = "0";
            session.setAttribute("page", page);
        }
        if (pageChange != null && !pageChange.equals("null") && !pageChange.equals("")) {
            if (pageChange.equals("1")) {
                if (!page.equals("0")) {
                    page = String.valueOf(Integer.parseInt(page) - 1);
                }
            } else if (pageChange.equals("2")) {
                page = String.valueOf(Integer.parseInt(page) + 1);
            }
            session.setAttribute("page", page);
        }
        if (rating == null) {
            rating = "desc";
            session.setAttribute("rating", rating);
            page = "0";
            session.setAttribute("page", page);
        }
        if (ratingSort != null && !ratingSort.equals("null") && !ratingSort.equals("")) {
            if (ratingSort.equals("1")) {
                rating = "asc";
            } else if (ratingSort.equals("2")) {
                rating = "desc";
            }
            page = "0";
            session.setAttribute("page", page);
            session.setAttribute("rating", rating);
            //记得重置page number
        }

        if (title == null) {
            title = "asc";
            session.setAttribute("title", title);
            page = "0";
            session.setAttribute("page", page);
        }
        if (titleSort != null && !titleSort.equals("null") && !titleSort.equals("")) {
            if (titleSort.equals("1")) {
                title = "asc";
            } else if (titleSort.equals("2")) {
                title = "desc";
            }
            page = "0";
            session.setAttribute("page", page);
            session.setAttribute("title", title);
        }

        if (sort == null) {
            sort = "1";
            session.setAttribute("sort", sort);
            page = "0";
            session.setAttribute("page", page);
        }
        if (sortSeq != null && !sortSeq.equals("null") && !sortSeq.equals("")) {
            if (sortSeq.equals("1")) {
                sort = "1";
            } else if (sortSeq.equals("2")) {
                sort = "2";
            }
            page = "0";
            session.setAttribute("page", page);
            session.setAttribute("sort", sort);
        }

        if (type == null) {
            type = "default";
            session.setAttribute("type", type);
            page = "0";
            session.setAttribute("page", page);
            sort = "1";
            session.setAttribute("sort", sort);
        }
        if (typeChange != null && !typeChange.equals("null") && !typeChange.equals("")) {
            type = typeChange;
            session.setAttribute("type", type);
            page = "0";
            session.setAttribute("page", page);
            sort = "1";
            session.setAttribute("sort", sort);
        }

        System.out.println("page is " + page);
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                String[] genreValues = new String[]
                        {"g1", "g2", "g3", "g4", "g5", "g6", "g7", "g8", "g9", "g10", "g11", "g12"
                                , "g13", "g14", "g15", "g16", "g17", "g18", "g19", "g20", "g21", "g22", "g23"};
                String[] titleValues = new String[]
                        {"t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t0", "tA", "tB", "tC"
                                , "tD", "tE", "tF", "tG", "tH", "tI", "tJ", "tK", "tL", "tM", "tN", "tO", "tP"
                                , "tQ", "tR", "tS", "tT", "tU", "tV", "tW", "tX", "tY", "tZ", "t*"};
                Set<String> genreSet = new HashSet<>(Arrays.asList(genreValues));
                Set<String> titleSet = new HashSet<>(Arrays.asList(titleValues));

                if (type.charAt(0) == 'g') {

                    String query;

                    if (sort.equals("2")) {
                        query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,round(l.rating,1) as rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                                "(select m.*,r.rating from movies as m inner join ratings as r on m.id=r.movieId ) as l,\n" +
                                "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id and g.id=?\n" +
                                "group by l.id order by l.title " + title + ", max(l.rating) " + rating + " limit " + N + " offset " + String.valueOf(Integer.valueOf(page) * Integer.valueOf(N)) + " ) as l,\n" +
                                "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                                "group by l.id order by l.title " + title + ", max(l.rating) " + rating + " ) as l;";
                    } else {
                        query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,round(l.rating,1) as rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                                "(select m.*,r.rating from movies as m inner join ratings as r on m.id=r.movieId ) as l,\n" +
                                "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id and g.id=?\n" +
                                "group by l.id order by  max(l.rating) " + rating + ", l.title " + title + " limit " + N + " offset " + String.valueOf(Integer.valueOf(page) * Integer.valueOf(N)) + " ) as l,\n" +
                                "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                                "group by l.id order by max(l.rating) " + rating + ", l.title " + title + "  ) as l;";
                    }

//                System.out.println(query);

                    PreparedStatement statement = conn.prepareStatement(query);

                    // Set the parameter represented by "?" in the query to the id we get from url,

                    statement.setString(1, type.substring(1));

                    // Perform the query
                    ResultSet rs = statement.executeQuery();

                    JsonArray jsonArray = new JsonArray();

                    // Iterate through each row of rs
                    while (rs.next()) {
                        String movie_id = rs.getString("id");
                        String movie_title = rs.getString("title");
                        String movie_year = rs.getString("year");
                        String movie_director = rs.getString("director");
                        String movie_genre = rs.getString("genres");
                        String movie_genre_id = rs.getString("genres_id");
                        String movie_rating = rs.getString("rating");
                        String movie_star_name = rs.getString("star_name");
                        String movie_star_id = rs.getString("star_id");

                        String[] id_list = movie_star_id.split(",");

                        if (id_list.length == 3) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=? \n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;\n" +
                                    "\n";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);
                            statement_stars.setString(2, id_list[1]);
                            statement_stars.setString(3, id_list[2]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        } else if (id_list.length == 2) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                                    "select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);
                            statement_stars.setString(2, id_list[1]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        } else if (id_list.length == 1) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                                    "select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        }

                        // Create a JsonObject based on the data we retrieve from rs
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("movie_id", movie_id);
                        jsonObject.addProperty("movie_title", movie_title);
                        jsonObject.addProperty("movie_year", movie_year);
                        jsonObject.addProperty("movie_director", movie_director);
                        jsonObject.addProperty("movie_genre", movie_genre);
                        jsonObject.addProperty("movie_genre_id", movie_genre_id);
                        jsonObject.addProperty("movie_rating", movie_rating);
                        jsonObject.addProperty("movie_star_name", movie_star_name);
                        jsonObject.addProperty("movie_star_id", movie_star_id);
                        jsonArray.add(jsonObject);
                    }
                    rs.close();
                    statement.close();

                    // Log to localhost log
                    request.getServletContext().log("getting " + jsonArray.size() + " results");

                    // Write JSON string to output
                    out.write(jsonArray.toString());
                    // Set response status to 200 (OK)
                    response.setStatus(200);

                } else if (titleSet.contains(type)) {

                    String s = "";
                    if (type.substring(1).equals("*")) {
                        s = " m.title regexp \"^[^a-zA-Z0-9]+\"";
                    } else {
                        s = " m.title like \"" + type.substring(1) + "%\"";
                    }
                    // Declare our statement
                    Statement statement = conn.createStatement();
                    String query;

                    if (sort.equals("2")) {
                        query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,round(l.rating,1) as rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                                "(select m.*,r.rating from movies as m inner join ratings as r on m.id=r.movieId where " + s + " ) as l,\n" +
                                "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id " +
                                "group by l.id order by l.title " + title + ", max(l.rating) " + rating + " limit " + N + " offset " + String.valueOf(Integer.valueOf(page) * Integer.valueOf(N)) + " ) as l,\n" +
                                "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                                "group by l.id order by l.title " + title + ", max(l.rating) " + rating + " ) as l;";
                    } else {
                        query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,round(l.rating,1) as rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                                "(select m.*,r.rating from movies as m inner join ratings as r on m.id=r.movieId where " + s + " ) as l,\n" +
                                "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id " +
                                "group by l.id order by  max(l.rating) " + rating + ", l.title " + title + " limit " + N + " offset " + String.valueOf(Integer.valueOf(page) * Integer.valueOf(N)) + " ) as l,\n" +
                                "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                                "group by l.id order by max(l.rating) " + rating + ", l.title " + title + "  ) as l;";
                    }

//                System.out.println(query);
//            PreparedStatement statement = conn.prepareStatement(query);
                    ResultSet rs = statement.executeQuery(query);
                    // Set the parameter represented by "?" in the query to the id we get from url,

                    JsonArray jsonArray = new JsonArray();

                    // Iterate through each row of rs
                    while (rs.next()) {
                        String movie_id = rs.getString("id");
                        String movie_title = rs.getString("title");
                        String movie_year = rs.getString("year");
                        String movie_director = rs.getString("director");
                        String movie_genre = rs.getString("genres");
                        String movie_genre_id = rs.getString("genres_id");
                        String movie_rating = rs.getString("rating");
                        String movie_star_name = rs.getString("star_name");
                        String movie_star_id = rs.getString("star_id");

                        String[] id_list = movie_star_id.split(",");

                        if (id_list.length == 3) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=? \n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;\n" +
                                    "\n";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);
                            statement_stars.setString(2, id_list[1]);
                            statement_stars.setString(3, id_list[2]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        } else if (id_list.length == 2) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                                    "select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);
                            statement_stars.setString(2, id_list[1]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        } else if (id_list.length == 1) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                                    "select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        }

                        // Create a JsonObject based on the data we retrieve from rs
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("movie_id", movie_id);
                        jsonObject.addProperty("movie_title", movie_title);
                        jsonObject.addProperty("movie_year", movie_year);
                        jsonObject.addProperty("movie_director", movie_director);
                        jsonObject.addProperty("movie_genre", movie_genre);
                        jsonObject.addProperty("movie_genre_id", movie_genre_id);
                        jsonObject.addProperty("movie_rating", movie_rating);
                        jsonObject.addProperty("movie_star_name", movie_star_name);
                        jsonObject.addProperty("movie_star_id", movie_star_id);
                        jsonArray.add(jsonObject);
                    }
                    rs.close();
                    statement.close();

                    // Log to localhost log
                    request.getServletContext().log("getting " + jsonArray.size() + " results");

                    // Write JSON string to output
                    out.write(jsonArray.toString());
                    // Set response status to 200 (OK)
                    response.setStatus(200);
                } else {
                    Statement statement = conn.createStatement();
                    String query = "";

                    if (sort.equals("2")) {
                        query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,l.rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                                "(select m.*,r.rating from movies as m inner join ratings as r\n" +
                                "on m.id=r.movieId order by m.title " + title + ", r.rating " + rating + " limit " +
                                N + " offset " + String.valueOf(Integer.valueOf(page) * Integer.valueOf(N)) +
                                " ) as l,\n" +
                                "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id\n" +
                                "group by l.id order by l.title " + title + " ,max(l.rating) " + rating + " ) as l,\n" +
                                "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                                "group by l.id order by l.title " + title + " ,max(l.rating) " + rating + " ) as l;";
                    } else {
                        query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,l.rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                                "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                                "(select m.*,r.rating from movies as m inner join ratings as r\n" +
                                "on m.id=r.movieId order by r.rating " + rating + ",m.title " + title + " limit " +
                                N + " offset " + String.valueOf(Integer.valueOf(page) * Integer.valueOf(N)) +
                                " ) as l,\n" +
                                "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id\n" +
                                "group by l.id order by max(l.rating) " + rating + ", l.title " + title + " ) as l,\n" +
                                "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                                "group by l.id order by max(l.rating) " + rating + ", l.title " + title + " ) as l;";
                    }

                    ResultSet rs = statement.executeQuery(query);

                    JsonArray jsonArray = new JsonArray();

                    // Iterate through each row of rs
                    while (rs.next()) {
                        String movie_id = rs.getString("id");
                        String movie_title = rs.getString("title");
                        String movie_year = rs.getString("year");
                        String movie_director = rs.getString("director");
                        String movie_genre = rs.getString("genres");
                        String movie_genre_id = rs.getString("genres_id");
                        String movie_rating = rs.getString("rating");
                        String movie_star_name = rs.getString("star_name");
                        String movie_star_id = rs.getString("star_id");

                        String[] id_list = movie_star_id.split(",");

                        if (id_list.length == 3) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=? \n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;\n" +
                                    "\n";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);
                            statement_stars.setString(2, id_list[1]);
                            statement_stars.setString(3, id_list[2]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        } else if (id_list.length == 2) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                                    "select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                                    "union\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);
                            statement_stars.setString(2, id_list[1]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        } else if (id_list.length == 1) {
                            String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                                    "select * from(\n" +
                                    "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                            PreparedStatement statement_stars = conn.prepareStatement(query_stars);
                            statement_stars.setString(1, id_list[0]);

                            ResultSet rs_stars = statement_stars.executeQuery();
                            while (rs_stars.next()) {
                                movie_star_name = rs_stars.getString("star_name");
                                movie_star_id = rs_stars.getString("star_id");
                            }
                        }

                        // Create a JsonObject based on the data we retrieve from rs
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("movie_id", movie_id);
                        jsonObject.addProperty("movie_title", movie_title);
                        jsonObject.addProperty("movie_year", movie_year);
                        jsonObject.addProperty("movie_director", movie_director);
                        jsonObject.addProperty("movie_genre", movie_genre);
                        jsonObject.addProperty("movie_genre_id", movie_genre_id);
                        jsonObject.addProperty("movie_rating", movie_rating);
                        jsonObject.addProperty("movie_star_name", movie_star_name);
                        jsonObject.addProperty("movie_star_id", movie_star_id);
                        jsonArray.add(jsonObject);
                    }
                    rs.close();
                    statement.close();

                    // Log to localhost log
                    request.getServletContext().log("getting " + jsonArray.size() + " results");

                    // Write JSON string to output
                    out.write(jsonArray.toString());
                    // Set response status to 200 (OK)
                    response.setStatus(200);
                }
            }



        } catch (Exception e) {
            System.out.println("exception: " + e.toString());
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
