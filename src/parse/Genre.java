package parse;

public class Genre {
    private String genreId;
    private String movieId;

    public Genre(String genreId, String movieId) {
        this.movieId = movieId;
        this.genreId = genreId;
    }


    @Override
    public String toString() {
        return "GenreInMovie{" +
                "genreId='" + genreId + '\'' +
                ", movieId='" + movieId + '\'' +
                '}';
    }
}
