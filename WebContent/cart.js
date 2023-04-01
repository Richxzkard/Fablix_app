let cart = $("#cart");

function handleSessionData(resultDataString) {

    let resultDataJson = JSON.parse(resultDataString);
    handleCartArray(resultDataJson["previousItems"])
}

let movie_table_body = $("#movie_table_body");
let movie_table_head = $("#movie_table_head");
let price_html = $("#price");

function handleCartArray(resultArray) {
    let item_list = $("#item_list");
    // change it to html list
    // let res = "<ul>";
    // for (let i = 0; i < resultArray.length; i++) {
    //     // each item will be in a bullet point
    //     res += "<li>" + resultArray[i]+ "</li>";
    // }
    // res += "</ul>";
    //
    // // clear the old array and show the new array in the frontend
    // item_list.html("");
    // item_list.append(res);

    let headHTML=" <tr>";
    headHTML+="<th>Id</th>"
    headHTML+="<th>Title</th>"
    headHTML+="<th>Price</th>"
    headHTML+="<th>Number</th>"
    headHTML+="<th>Add</th>"
    headHTML+="<th>Minus</th>"
    headHTML+="<th>Delete</th>"
    headHTML+="</tr>"

    movie_table_head.html(headHTML);

    let resultHTML="";
    let price=0;
    for (i=0; i< resultArray.length; i++){
        const moviearray=resultArray[i].split(',')
        if(moviearray[3]!=='0'){
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + moviearray[0] + "</th>";
            rowHTML += "<th>" + moviearray[1] + "</th>";
            rowHTML += "<th>" + moviearray[2] + "</th>";
            rowHTML += "<th>" + moviearray[3] + "</th>";
            rowHTML += "<th>" ;
            rowHTML += '<button class="add_button" value="'+moviearray[0]+'">Add</button>';
            rowHTML += "</th>";
            rowHTML += "<th>" ;
            rowHTML += '<button class="minus_button" value="'+moviearray[0]+'">Minus</button>';
            rowHTML += "</th>";
            rowHTML += "<th>" ;
            rowHTML += '<button class="delete_button" value="'+moviearray[0]+'">Delete</button>';
            rowHTML += "</th>";
            rowHTML += "</tr>";
            resultHTML+=rowHTML
            price+=parseInt(moviearray[2])*parseInt(moviearray[3])
        }
    }

    price_html.html('The total price of movies is '+price)
    movie_table_body.html(resultHTML);
    $(document).ready(function(){
        $('.add_button').click(function(){
            // alert("Adding Successfully");
            // console.log(JSON.stringify({'item':$(this).val()}));
            $.ajax("api/cart", {
                method: "POST",
                data: {'item':$(this).val(),'action':'add'},
                success: resultDataString => {
                    console.log(resultDataString)
                }
            });
            location.reload()
        });
    });

    $(document).ready(function(){
        $('.minus_button').click(function(){
            // alert("Adding Successfully");
            // console.log(JSON.stringify({'item':$(this).val()}));
            $.ajax("api/cart", {
                method: "POST",
                data: {'item':$(this).val(),'action':'minus'},
                success: resultDataString => {
                    console.log(resultDataString)
                }
            });
            location.reload()
        });
    });

    $(document).ready(function(){
        $('.delete_button').click(function(){
            // alert("Adding Successfully");
            // console.log(JSON.stringify({'item':$(this).val()}));
            $.ajax("api/cart", {
                method: "POST",
                data: {'item':$(this).val(),'action':'delete'},
                success: resultDataString => {
                    console.log(resultDataString)
                }
            });
            location.reload()
        });
    });
}

$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});

// cart.submit(handleCartInfo);
//

function handleSearchResults(resultData) {

    let headHTML=" <tr>";
    headHTML+="<th>Title</th>"
    headHTML+="<th>Year</th>"
    headHTML+="<th>Director</th>"
    headHTML+="<th>Genres</th>"
    headHTML+="<th>Stars</th>"
    headHTML+="<th>Ratings</th>"
    headHTML+="</tr>"

    movie_table_head.html(headHTML);

    let resultHTML="";

    for (i=0; i< resultData.length; i++){
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href=" ' + resultData[i]['movie_id'] + '">'
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
        rowHTML += "</tr>";
        if (i == 0){
            console.log(rowHTML);}
        resultHTML+=rowHTML
    }
    movie_table_body.html(resultHTML);
}
