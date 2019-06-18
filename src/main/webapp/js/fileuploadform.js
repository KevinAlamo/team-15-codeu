 function fetchBlobstoreUrlAndShowForm() {
        fetch('/blobstore-upload-url')
          .then(function(response) {
            return response.text();
          })
          .then(function(imageUploadUrl) {
            const messageForm = document.getElementById('message-form');
            messageForm.action = imageUploadUrl;
            messageForm.classList.remove('hidden');
          });
      }