package parse;

public class MovieStar {
    private String starId;
    private String name;
    private String movieId;

    public MovieStar(String movieId, String name) {
        this.movieId = movieId;
        this.name = name;
    }


    public String getName() {return name;}
    public String getMovieId() {
        return movieId;
    }


    @Override
    public String toString() {
        return "StarInMovie{" +
                "starId='" + starId + '\'' +
                ", movieId='" + movieId + '\'' +
                '}';
    }
}
