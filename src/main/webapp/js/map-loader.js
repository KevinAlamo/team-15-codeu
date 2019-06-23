let map;
/* Editable marker that displays when a user clicks in the map. */
let editMarker;

function createMap(){		  // eslint-disable-line no-unused-vars
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 38.5949, lng: -94.8923},
    zoom: 4
  });

  /**  When the user clicks in the map, show a marker with a text box the user can edit. */
  map.addListener('click', (event) => {
    createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
  });
  fetchMarkers();
}

/** Fetches markers from the backend and adds them to the map. */
function fetchMarkers(){
  fetch('/markers').then((response) => {
    return response.json();
  }).then((markers) => {
    markers.forEach((marker) => {
     createMarkerForDisplay(marker.lat, marker.lng, marker.content)
    });
  });
}

/** Creates a marker that shows a read-only info window when clicked & deletes when right-clicked */
function createMarkerForDisplay(lat, lng, content){
  const marker = new google.maps.Marker({
    position: {lat: lat, lng: lng},
    map: map
  });
  var infoWindow = new google.maps.InfoWindow({
    content: content
  });
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });

  // When the user right clicks the info window, remove the marker.
  marker.addListener('rightclick', () => {
	removeMarker(lat, lng);
	marker.setMap(null);
});
}

/** Sends a marker to the backend for saving. */
function postMarker(lat, lng, content){
  const params = new URLSearchParams();
  params.append('lat', lat);
  params.append('lng', lng);
  params.append('content', content);
  fetch('/markers', {
    method: 'POST',
    body: params
  });
}

/** Creates a marker that shows a textbox the user can edit. */
function createMarkerForEdit(lat, lng){
  // If we're already showing an editable marker, then remove it.
  if(editMarker){
   editMarker.setMap(null);
  }
  editMarker = new google.maps.Marker({
    position: {lat: lat, lng: lng},
    map: map
  });
  const infoWindow = new google.maps.InfoWindow({
    content: buildInfoWindowInput(lat, lng)
  });
  // When the user closes the editable info window, remove the marker.
  google.maps.event.addListener(infoWindow, 'closeclick', () => {
    editMarker.setMap(null);
  });
  infoWindow.open(map, editMarker);
}

function removeMarker(lat, lng){
	const baseURL = window.location.protocol + '//' + window.location.host;
	const url = new URL(baseURL+'/markers');
	url.searchParams.append('lat',lat);
	url.searchParams.append('lng',lng);
	
	//removes marker from the datastore
	fetch(url, {
		method:'DELETE'
	})
	.catch(error => console.log(error));
}

/** Builds and returns HTML elements that show an editable textbox and a submit button. */
function buildInfoWindowInput(lat, lng){
  const textBox = document.createElement('textarea');
  const button = document.createElement('button');
  button.appendChild(document.createTextNode('Submit'));
  button.onclick = () => {
    postMarker(lat, lng, textBox.value);
    createMarkerForDisplay(lat, lng, textBox.value);
    editMarker.setMap(null);
  };
  const containerDiv = document.createElement('div');
  containerDiv.appendChild(textBox);
  containerDiv.appendChild(document.createElement('br'));
  containerDiv.appendChild(button);
  return containerDiv;
}
/**
function buildInfoWindowDelete(lat, lng){
  const button = document.createElement('button');
  button.appendChild(document.createTextNode('Delete'));
  button.onclick = () => {
    postMarker(lat, lng, textBox.value);
    createMarkerForDisplay(lat, lng, textBox.value);
    editMarker.setMap(null);
  };
  const containerDiv = document.createElement('div');
  containerDiv.appendChild(document.createElement('br'));
  containerDiv.appendChild(button);
  return containerDiv;
}
 */