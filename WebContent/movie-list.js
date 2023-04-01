
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

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i <  resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let movie_genre_id = resultData[i]["movie_genre_id"];
        let movie_genre = resultData[i]["movie_genre"];
        const movie_genre_id_array =movie_genre_id.split(",");
        const movie_genre_array =movie_genre.split(",");

        // rowHTML += "<th>" + resultData[i]["movie_genre"] + "</th>";
        rowHTML += "<th>" ;
        for (let j = 0; j <  movie_genre_array.length; j++) {
            rowHTML+= '<a href="movie-list.html?pageChange=&NChange=25&rating=desc&title=asc&sort=1&type=g' + movie_genre_id_array[j] + '">'
                + movie_genre_array[j] +" "+
                '</a>'
            // console.log(movie_genre_array[j])
            // console.log(movie_genre_id_array[j])
        }
        rowHTML += "</th>";

        //Split movie_star list and creat link to star page
        let movie_star_id = resultData[i]["movie_star_id"];
        let movie_star_name = resultData[i]["movie_star_name"];
        const movie_star_id_array =movie_star_id.split(",");
        const movie_star_name_array =movie_star_name.split(",");
        //Add star page link
        rowHTML += "<th>" ;
        for (let j = 0; j <  movie_star_name_array.length; j++) {
            rowHTML+= '<a href="single-star.html?id=' + movie_star_id_array[j] + '">'
            + movie_star_name_array[j] +" "+
            '</a>'
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] +"</th>";
        rowHTML += "<th>" ;

        rowHTML += '<button class="add_button" value="'+resultData[i]["movie_id"]+'">Add</button>';

        rowHTML += "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }

    $(document).ready(function(){
        $('.add_button').click(function(){
            alert("Adding Successfully");
            // console.log(JSON.stringify({'item':$(this).val()}));
            $.ajax("api/cart", {
                method: "POST",
                data: {'item':$(this).val()},
                success: resultDataString => {
                    console.log(resultDataString)
                }
            });
        });
    });

}

// Get id from URL
let pageChange = getParameterByName('pageChange');
let NChange = getParameterByName('NChange');
let rating = getParameterByName('rating');
let title = getParameterByName('title');
let sort = getParameterByName('sort');
let type = getParameterByName('type');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movies?pageChange=" + pageChange+"&NChange="+NChange+"&rating="+rating+"&title="+title+"&sort="+sort+"&type="+type, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});


