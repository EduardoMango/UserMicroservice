document.getElementById("registerForm")
    .addEventListener("submit", function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    console.log("Sending request")
    fetch("/auth/user", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, username, password })
    })
    .then(response => {
        if (response.status === 201) {
            alert("Registration successful!");
            window.location.href = "/login";
        } else {
            alert("Error: " + response.message);
        }
    })
    .catch(error => {
        alert("Error: " + error);
    });
    });
