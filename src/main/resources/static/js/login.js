document.getElementById("loginForm").onsubmit = function(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch('/auth', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    })
        .then(response => response.json())
        .then(data => {
            if (data.AccessToken) {
                localStorage.setItem('authToken', data.AccessToken);
                localStorage.setItem('refreshToken', data.refreshToken);

                fetch('/success', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                    },
                    credentials: 'include'
                })
                    .then(response => {
                        if (response.ok) {
                            window.location.href = '/success';
                        } else {

                        }
                    })
                    .catch(error => {
                        alert('Error: ' + error);
                    });
            } else {
                alert("invalid credentials");
            }
        })
        .catch(error => {
            alert('Error: ' + error);
        });
};