document.addEventListener('DOMContentLoaded', function () {
    var form = document.querySelector('form[action="releaseTopics"]');
    var studentTopicsButton = document.querySelector('button[value="releaseStudentTopics"]');
    var supervisorTopicsButton = document.querySelector('button[value="releaseSupervisorTopics"]');
    var confirmButton = document.getElementById('confirmBtn');
    var actionValue; // To hold the action to be submitted

    // Function to open modal with specific action
    function openConfirmationModal(action) {
        actionValue = action;
        $('#confirmationModal').modal('show');
    }

    // Attach event listeners to buttons for opening the modal
    studentTopicsButton.addEventListener('click', function () {
        openConfirmationModal('releaseStudentTopics');
    });

    supervisorTopicsButton.addEventListener('click', function () {
        openConfirmationModal('releaseSupervisorTopics');
    });

    // Confirm and submit the form
    confirmButton.addEventListener('click', function () {
        if (actionValue) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'action';
            input.value = actionValue;
            form.appendChild(input);
            form.submit(); // Submit the form with the action value
        }
        $('#confirmationModal').modal('hide'); // Hide the modal
    });
});
