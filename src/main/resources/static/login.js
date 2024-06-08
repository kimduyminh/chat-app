// Select elements
let signupsw = document.querySelector(".signup");
let loginsw = document.querySelector(".login");
let login = document.querySelector(".login-box");
let signup = document.querySelector(".signup-box");
let slider = document.querySelector(".slider");
let formSection = document.querySelector(".form-section");
let loginButton = document.querySelector(".login-box .clkbtnlogin");
let signupButton = document.querySelector(".signup-box .clkbtnsignup");

// Event listeners for switching between login and signup
signupsw.addEventListener("click", () => {
    slider.classList.add("moveslider");
    formSection.classList.add("form-section-move");
});

loginsw.addEventListener("click", () => {
    slider.classList.remove("moveslider");
    formSection.classList.remove("form-section-move");
});

// Event listener for signup button
signupButton.addEventListener("click", () => {
    // Retrieve input values
    let name_ca = signup.querySelector('.namesign.ele').value;
    let email_ca = signup.querySelector('.emailsign.ele').value;
    let password_ca = signup.querySelector('.passwordsign.ele').value;
    let passwordcf_ca = signup.querySelector('.passwordcfsign.ele').value;

    // Check for password mismatch
    if (password_ca !== passwordcf_ca) {
        alert("Password mismatched");
        return;
    }

    // Construct signupInfo object
    const signupInfo = {
        name: name_ca,
        username: email_ca,
        password: password_ca
    };

    // Perform signup fetch request
    fetch('/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(signupInfo)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    const errorMessage = data.error || 'Unknown error'; // Extract the error message or use a default message
                    alert('Signup Error: ' + errorMessage); // Display an alert with the error message
                    throw new Error(errorMessage);
                });
            }
            return response.text();
        })

        .then(session_id => {
            console.log('Signup successful, session_id:', session_id);

            // Perform login fetch request
            const loginInfo = {
                username: email_ca,
                password: password_ca
            };
            return fetch('/app.login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginInfo)
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error('Login Error: ' + text); });
                    }
                    return response.text();
                })
                .then(session_id => {
                    console.log('Login successful, session_id:', session_id);
                    // Redirect to mainchat with session ID
                    window.location.href = '/mainchat/' + session_id;
                })
                .catch(error => console.error('Error:', error));
        });
});

// Event listener for login button
loginButton.addEventListener("click", () => {
    let username = login.querySelector('.emaillog.ele').value;
    let password = login.querySelector('.passwordlog.ele').value;

    const loginInfo = {
        username: username,
        password: password
    };

    fetch('/app.login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginInfo)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error('Login Error: ' + text); });
            }
            return response.text();
        })
        .then(session_id => {
            console.log('Login successful, session_id:', session_id);
            // Redirect to mainchat with session ID
            window.location.href = '/mainchat/' + session_id;
        })
        .catch(error => console.error('Error:', error));
})
