import java.io.*;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet("/api/full-text-search")
public class FullTextSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static HashMap<Integer, String> SearchMap = new HashMap<>();

	public FullTextSearchServlet() {
		super();
	}

	// Create a dataSource which registered in web.xml
	private DataSource dataSource;

	public void init(ServletConfig config) {
		try {
			dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

//	public void WriteToFile(long TS, long TJ) {
//			try {
//				System.out.println("Writing to file");
//
//				BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/runcat/Documents/GithubProjects/cs122b-fall-team-58/src/log"));
//				StringBuffer sBuffer = new StringBuffer();
//				sBuffer.append(String.valueOf(TS));
//				sBuffer.append(" ");
//				sBuffer.append(String.valueOf(TJ));
//
//				System.out.println("final string " + sBuffer.toString());
//
//				writer.write(sBuffer.toString());
//				writer.close();
//			} catch (IOException e) {
//				System.out.println("An error occurred.");
//				e.printStackTrace();
//			}
//	}

	public static void WriteToFile(long TS, long TJ) {

		FileOutputStream fos = null;

		try {

//true不覆盖已有内容

			fos = new FileOutputStream("/home/ubuntu/log", true);

			StringBuffer sBuffer = new StringBuffer();
			sBuffer.append(String.valueOf(TS));
			sBuffer.append(" ");
			sBuffer.append(String.valueOf(TJ));

			System.out.println("final string " + sBuffer);
// 写入一个换行
			fos.write(sBuffer.toString().getBytes());
			fos.write("\r\n".getBytes());

		} catch (IOException e) {

			e.printStackTrace();

		}finally{

			if(fos != null){

				try {

					fos.flush();
					fos.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		try (out; Connection conn = dataSource.getConnection()) {
			if (conn == null) {
				out.println("conn is null.");
			} else {
				// setup the response json arrray
				JsonArray jsonArray = new JsonArray();

				// get the query string from parameter
				String query = request.getParameter("query");

				// return the empty json array if query is null or empty
				if (query == null || query.trim().isEmpty()) {
					response.getWriter().write(jsonArray.toString());
					return;
				}

				String[] query_list = query.split(" ");
				String concat_query = "";

				for (int i = 0; i < query_list.length; i++) {
					concat_query = query_list[i] + "* " + concat_query;
				}

				String full_text_query =
						"SELECT * from (SELECT movies.*, MATCH (title) AGAINST (? IN BOOLEAN MODE) as score FROM movies ORDER BY score DESC) as l where score>0 limit 10;\n";

				PreparedStatement statement_full_text = conn.prepareStatement(full_text_query);
				statement_full_text.setString(1, concat_query);
				ResultSet rs_stars = statement_full_text.executeQuery();



				while (rs_stars.next()) {
					String movie_id = rs_stars.getString("id");
					String movie_title = rs_stars.getString("title");
					jsonArray.add(generateJsonObject(movie_id, movie_title));
				}

				response.getWriter().write(jsonArray.toString());
				return;
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




	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("dopost is called");
		//zikangx
		long servletTime;
		long JDBCTime = (long) 0.0;
		long servletStart = System.nanoTime();

		String query = request.getParameter("query");

		response.setContentType("application/json"); // Response mime type

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		// Get a connection from dataSource and let resource manager close the connection after usage.
		try (out; Connection conn = dataSource.getConnection()) {
			if (conn == null) {
				out.println("conn is null.");
			} else {
				JsonArray jsonArray = new JsonArray();

				// return the empty json array if query is null or empty
				if (query == null || query.trim().isEmpty()) {
					response.getWriter().write(jsonArray.toString());
					return;
				}

				String[] query_list = query.split(" ");
				String concat_query = "";

				for (int i = 0; i < query_list.length; i++) {
					concat_query = query_list[i] + "* " + concat_query;
				}

				String full_text_query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,l.rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id,l.score  from\n" +
						"(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id,l.score  from\n" +
						"(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating,l.score from\n" +
						"(select m.*, r.rating, MATCH (title) AGAINST (? IN BOOLEAN MODE) as score from movies as m inner join ratings as r\n" +
						"on m.id=r.movieId order by score desc ,m.title asc limit 20 ) as l,\n" +
						"genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id\n" +
						"group by l.id order by l.score desc, l.title asc ) as l,\n" +
						"stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
						"group by l.id order by l.score desc, l.title asc ) as l where l.score>0;";
				long JDBCStart = System.nanoTime();

				PreparedStatement statement_full_text = conn.prepareStatement(full_text_query);
				statement_full_text.setString(1, concat_query);
				ResultSet rs = statement_full_text.executeQuery();

				long JDBCEnd = System.nanoTime();
				JDBCTime = JDBCEnd - JDBCStart;
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
				statement_full_text.close();

				// Log to localhost log
				request.getServletContext().log("getting " + jsonArray.size() + " results");

				// Write JSON string to output
				out.write(jsonArray.toString());
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
			//zikangx
			long servletEnd = System.nanoTime();
			servletTime = servletEnd - servletStart;
			System.out.println(JDBCTime);
			if (!(JDBCTime == 0.0)){
				//write log to file
				System.out.println("Calling write function");
				WriteToFile(servletTime, JDBCTime);
			}
			out.close();
		}

		// Always remember to close db connection after usage. Here it's done by try-with-resources

	}
	private static JsonObject generateJsonObject(String movie_id, String movie_title) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movie_title);

		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("movieID", movie_id);

		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}


}
