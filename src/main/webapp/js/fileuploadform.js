 function fetchBlobstoreUrlAndShowForm() {
        fetch('/blobstore-upload-url')
          .then(function(response) {
            return response.text();
          })
          .then(function(imageUploadUrl) {
            const messageForm = document.getElementById('my-form');
            messageForm.action = imageUploadUrl;
            messageForm.classList.remove('hidden');
          });
      }
