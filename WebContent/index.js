let cart = $("#cart");
let movie_table_body = $("#movie_table_body");
let movie_table_head = $("#movie_table_head");

function handleSearchResults(resultData) {
    let headHTML=" <tr>";
    headHTML+="<th>Title</th>"
    headHTML+="<th>Year</th>"
    headHTML+="<th>Director</th>"
    headHTML+="<th>Genres</th>"
    headHTML+="<th>Stars</th>"
    headHTML+="<th>Ratings</th>"
    headHTML+="<th>Cart</th>"
    headHTML+="</tr>"

    movie_table_head.html(headHTML);

    let resultHTML="";

    for (i=0; i< resultData.length; i++){
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
        rowHTML += "<th>" ;

        let movie_genre_id = resultData[i]["movie_genre_id"];
        let movie_genre = resultData[i]["movie_genre"];
        const movie_genre_id_array =movie_genre_id.split(",");
        const movie_genre_array =movie_genre.split(",");

        for (let j = 0; j <  movie_genre_array.length; j++) {
            rowHTML+= '<a href="movie-list.html?pageChange=&NChange=25&rating=desc&title=asc&sort=1&type=g' + movie_genre_id_array[j] + '">'
                + movie_genre_array[j] +" "+
                '</a>'
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

        if (i == 0){
            console.log(rowHTML);}
        resultHTML+=rowHTML
    }

    movie_table_body.html(resultHTML);
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

let search_form = $("#search_form");

function submitSearchForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax("api/search", {
        method: "POST",
        data: search_form.serialize(),
        success: handleSearchResults
    })
}

search_form.submit(submitSearchForm)

/*
 * CS 122B Project 4. Autocomplete Example.
 *
 * This Javascript code uses this library: https://github.com/devbridge/jQuery-Autocomplete
 *
 * This example implements the basic features of the autocomplete search, features that are
 *   not implemented are mostly marked as "TODO" in the codebase as a suggestion of how to implement them.
 *
 * To read this code, start from the line "$('#autocomplete').autocomplete" and follow the callback functions.
 *
 */


/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {

    // TODO: if you want to check past query results first, you can do it here
    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data

    console.log("autocomplete initiated")
    let data = sessionStorage.getItem(query);
    if(data==null){
        console.log("sending AJAX request to backend Java Servlet")
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/full-text-search?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }else{
        var jsonData = JSON.parse(data);
        console.log(jsonData)
        doneCallback( { suggestions: jsonData })
        console.log("lookup session storage successful")
    }

}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)
    sessionStorage.setItem(query, data);
    doneCallback( { suggestions: jsonData } );
}
/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
    window.location.href = "single-movie.html?id="+ suggestion["data"]["movieID"];
}

$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    minChars:3
});

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        fulltext_search_form.submit(handleNormalSearch)
    }
})

let fulltext_search_form = $("#fulltext_search_form");

function handleNormalSearch(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax("api/full-text-search", {
        method: "POST",
        data: fulltext_search_form.serialize(),
        success: handleSearchResults
    })
}

fulltext_search_form.submit(handleNormalSearch)
