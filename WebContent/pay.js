let payment_form = $("#payment_form");

function handelPaymentResult(resultDataString){
    console.log(resultDataString)
    let resultDataJson = JSON.parse(resultDataString);
        $("#payment_message").text(resultDataJson["message"]);
}

function submitPaymentForm(formSubmitEvent) {

    formSubmitEvent.preventDefault();
    $.ajax("api/pay", {
        method: "POST",
        data: payment_form.serialize(),
        success: result=> handelPaymentResult(result)
    })
}

payment_form.submit(submitPaymentForm)