function handleResult(resultData) {

    console.log("handleResult: populating database metadata from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let creditcardsTableBodyElement = jQuery("#creditcards_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[0].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[0][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[0][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[0][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[0][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[0][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[0][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        creditcardsTableBodyElement.append(rowHTML);
    }
    let customersTableBodyElement = jQuery("#customers_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[1].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[1][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[1][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[1][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[1][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[1][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[1][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        customersTableBodyElement.append(rowHTML);
    }
    let employeesTableBodyElement = jQuery("#employees_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[2].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[2][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[2][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[2][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[2][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[2][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[2][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        employeesTableBodyElement.append(rowHTML);
    }
    let genresTableBodyElement = jQuery("#genres_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[3].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[3][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[3][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[3][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[3][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[3][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[3][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        genresTableBodyElement.append(rowHTML);
    }
    let genres_in_moviesTableBodyElement = jQuery("#genres_in_movies_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[4].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[4][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[4][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[4][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[4][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[4][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[4][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        genres_in_moviesTableBodyElement.append(rowHTML);
    }
    let moviesTableBodyElement = jQuery("#movies_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[5].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[5][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[5][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[5][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[5][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[5][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[5][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        moviesTableBodyElement.append(rowHTML);
    }
    let ratingsTableBodyElement = jQuery("#ratings_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[6].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[6][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[6][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[6][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[6][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[6][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[6][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        ratingsTableBodyElement.append(rowHTML);
    }
    let salesTableBodyElement = jQuery("#sales_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[7].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[7][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[7][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[7][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[7][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[7][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[7][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        salesTableBodyElement.append(rowHTML);
    }
    let starsTableBodyElement = jQuery("#stars_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i <resultData[8].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[8][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[8][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[8][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[8][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[8][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[8][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        starsTableBodyElement.append(rowHTML);
    }
    let stars_in_moviesTableBodyElement = jQuery("#stars_in_movies_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[9].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[9][i]["Field"] + "</th>";
        rowHTML += "<th>" + resultData[9][i]["Type"] + "</th>";
        rowHTML += "<th>" + resultData[9][i]["Null"] + "</th>";
        rowHTML += "<th>" + resultData[9][i]["Key"] + "</th>";
        rowHTML += "<th>" + resultData[9][i]["Default"] + "</th>";
        rowHTML += "<th>" + resultData[9][i]["Extra"] + "</th>";
        rowHTML += "</tr>";
        // Append the row created to the table body, which will refresh the page
        stars_in_moviesTableBodyElement.append(rowHTML);
    }

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
function handleAddResult(resultData) {
    alert(resultData["message"])
}

let star_from = $("#star_from");

function submitStarForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    // console.log("click submit")
    $.ajax("api/dashboard", {
        method: "POST",
        data: star_from.serialize(),
        success: handleAddResult
    })
}

star_from.submit(submitStarForm)

let movie_from = $("#movie_from");

function submitMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    // console.log("click submit")
    $.ajax("api/dashboard", {
        method: "POST",
        data: movie_from.serialize(),
        success: handleAddResult
    })
}

movie_from.submit(submitMovieForm)

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});