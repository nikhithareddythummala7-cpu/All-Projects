const baseURL = "http://localhost:8080";
const viewContactModal = document.getElementById('view_contact_modal');

// Function to open the modal and load contact data
async function openContactModal(id) {
    // Initialize the Bootstrap modal
    const modal = new bootstrap.Modal(viewContactModal, {
        backdrop: 'static', // Use static backdrop to prevent closing when clicking outside. You can use 'dynamic' also
        keyboard: true // Allow closing the modal with keyboard
    });

    // Load contact data when the modal is shown
    viewContactModal.addEventListener('shown.bs.modal', async () => {
        console.log('modal is shown');
        await loadContactData(id); // Load contact data
    });

    // Log when the modal is hidden
    viewContactModal.addEventListener('hidden.bs.modal', () => {
        console.log('modal is hidden');
    });

    // Show the modal
    modal.show();
}

// Function to close the modal
function closeContactModal() {
    const modal = bootstrap.Modal.getInstance(viewContactModal);
    modal.hide();
}

// Function to load contact data
async function loadContactData(id) {
    console.log(id);
    try {
        const response = await fetch(`${baseURL}/api/contacts/${id}`);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();
        console.log(data);

        document.querySelector('#contact_image').src = data.picture;
        document.querySelector('#contact_name').innerHTML = data.name;
        document.querySelector('#contact_email').innerHTML = data.email;
        document.querySelector('#contact_phone').innerHTML = data.phoneNumber;
        document.querySelector('#contact_address').innerHTML = data.address;
        document.querySelector('#contact_about').innerHTML = data.description;
        const contactFavorite = document.querySelector('#contact_favorite');
        if(data.favorite){
            contactFavorite.innerHTML = '<i class="fa-solid fa-star text-warning"></i><i class="fa-solid fa-star text-warning"></i><i class="fa-solid fa-star text-warning"></i><i class="fa-solid fa-star text-warning"></i><i class="fa-solid fa-star text-warning"></i>';
        } else {
            contactFavorite.innerHTML = 'Not Favorite Contact';
        }
        document.querySelector('#contact_website').href = data.websiteLink;
        document.querySelector('#contact_website').innerHTML = data.websiteLink;
        document.querySelector('#contact_linkedIn').href = data.linkedInLink;
        document.querySelector('#contact_linkedIn').innerHTML = data.linkedInLink;

        // Display the fetched data in the modal
        document.getElementById('contactData').innerText = JSON.stringify(data, null, 2); // Format data as needed
    } catch (error) {
        console.log("Error:", error);
        document.getElementById('contactData').innerText = "Failed to load contact data."; // Handle error
    }
}

async function deleteContact(id) {
    swal({
      title: "Are you sure?",
      text: "Your will not be able to recover this contact!",
      type: "warning",
      showCancelButton: true,
      confirmButtonClass: "btn-danger",
      confirmButtonText: "Yes, delete it!",
      closeOnConfirm: false
    },
    function(){
        const url = `${baseURL}/user/contacts/delete/` + id;
        window.location.replace(url);
        swal("Deleted!", "Your contact has been deleted.", "success");
    });
}
