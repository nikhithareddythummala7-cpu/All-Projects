//console.log("Admin user");

//document.querySelector("#image_file_input").addEventListener('change', function(event){
//    let file = event.target.files[0];
//    let reader = new FileReader();
//    reader.onload = function(){
//        document.querySelector("#upload_image_preview").setAttribute("src", reader.result);
//    }
//    reader.readAsDataURL(file);
//});


function previewImage(event) {
        const imagePreview = document.getElementById('upload_image_preview');
        const file = event.target.files[0];

        if (file) {
            // Display the image preview
            imagePreview.src = URL.createObjectURL(file);
            imagePreview.classList.remove('d-none');
        } else {
            // Hide the image preview if no file is selected
            imagePreview.src = '';
            imagePreview.classList.add('d-none');
        }
    }