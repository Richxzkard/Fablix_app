/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;

    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");
    let movie_genre_id = resultData[0]["movie_genre_id"];
    let movie_genre = resultData[0]["movie_genre"];
    const movie_genre_id_array =movie_genre_id.split(",");
    const movie_genre_array =movie_genre.split(",");

    // rowHTML += "<th>" + resultData[i]["movie_genre"] + "</th>";
    let str=''
    for (let j = 0; j < movie_genre_array.length; j++) {
        str += '<a href="movie-list.html?pageChange=&NChange=25&rating=desc&title=asc&sort=1&type=g' + movie_genre_id_array[j] + '">'
            + movie_genre_array[j] + " " +
            '</a>'
    }
    console.log(str)
    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append(
        "<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Genres: " + str + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>"
    );

    let movieListLinkElement = jQuery("#movie_list_link");
    movieListLinkElement.append(
        '<a href="movie-list.html">'+
        "<= Movie List"+
        '</a>'
    );

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let movie_star_id = resultData[i]["movie_star_id"];
        let movie_star_name = resultData[i]["movie_star_name"];
        const movie_star_id_array =movie_star_id.split(",");
        const movie_star_name_array =movie_star_name.split(",");
        let rowHTML = "";

        for (let j = 0; j <  movie_star_name_array.length; j++) {
            rowHTML += "<tr>";
            rowHTML += "<th>" ;
            rowHTML+= '<a href="single-star.html?id=' + movie_star_id_array[j] + '">'
                + movie_star_name_array[j] +" "+
                '</a>';
            rowHTML += "</th>";
            rowHTML += "</tr>";
        }

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});