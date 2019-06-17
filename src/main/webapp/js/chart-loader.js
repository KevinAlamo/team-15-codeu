google.charts.load("current", {packages:["corechart"]});
google.charts.setOnLoadCallback(drawChart);

function drawChart(){
  fetch("/new-residents-charts")
    .then(function(response) {
      return response.json();
    }).then((bookJson) => {
      var data = new google.visualization.DataTable();
      //define columns for the DataTable instance
      data.addColumn('number', 'Freedom');
      data.addColumn('number', 'Happiness');

      for (i = 0; i < bookJson.length; i++) {
        row = [];
        var freedom = bookJson[i].freedom;
        var happy = bookJson[i].happy;
        row.push(freedom, happy);

        data.addRow(row);
      }

      var options = {
        title: 'Happpiness vs Freedom',
        hAxis: {title: 'Freedom', minValue: 0, maxValue: 200},
        vAxis: {title: 'Happiness', minValue: 0, maxValue: 200},
        trendlines: { 0: {} }
      };

      var chart = new google.visualization.ScatterChart(document.getElementById('chart_div2'));
      chart.draw(data, options);
    })
}