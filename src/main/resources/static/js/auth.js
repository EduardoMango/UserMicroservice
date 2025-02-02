document.addEventListener("DOMContentLoaded", function() {
    const token = document.getElementById("githubToken").value;
    if (token) {
        fetch('/auth/exchange/github', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ token })
        })
            .then(response => response.json())
            .then(data => {
                if (data.AccessToken) {
                    localStorage.setItem('authToken', data.AccessToken);
                    localStorage.setItem('refreshToken', data.refreshToken);
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                alert('Error: ' + error);
            });
    }
})

document.getElementById("testTokenValidity")
    .addEventListener("click", function() {

    const token = localStorage.getItem('authToken');

    fetch('/validate-token', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();  // Solo hacer parse si la respuesta es OK
            } else {
                if (response.status === 401) {
                    alert("Token has expired. Refreshing token.. Please try again");

                    const refreshToken = localStorage.getItem('refreshToken');

                    fetch('/auth/refresh', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ refreshToken })
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.AccessToken) {
                                localStorage.setItem('authToken', data.AccessToken);
                                localStorage.setItem('refreshToken', data.refreshToken);
                            }
                        })
                }
            }
        })
        .then(data => {
            alert(data.message);
        })
        .catch(error => {
            console.error(error)
        });
});

document.getElementById("logoutButton")
    .onclick = function(event) {

    localStorage.removeItem('authToken');
    window.location.href = '/logout';
};