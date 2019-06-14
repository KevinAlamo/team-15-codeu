google.charts.load('current', {packages: ['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart(){
  fetch("/new-residents-charts")
  .then((response) => {
    return response.json();
})
.then((bookJson) => {
    console.log(bookJson);
});
}