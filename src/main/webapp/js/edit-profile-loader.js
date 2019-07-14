// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace('/');
}

function setDefaults() {
  var inputs = document.getElementById('profile-form')
  for (var i = 0; i < inputs.length; i++) {
    if (inputs[i].name == "name") {
      inputs[i].value = parameterUsername;
    }
  }
}

function goBack() {
  location.href='/user-page.html?user=' + parameterUsername;
}