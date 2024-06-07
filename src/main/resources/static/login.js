let signupsw = document.querySelector(".signup");
let loginsw = document.querySelector(".login");
let login = document.querySelector(".login-box")
let signup = document.querySelector(".signup-box")
let slider = document.querySelector(".slider");
let formSection = document.querySelector(".form-section");
let loginButton = document.querySelector(".login-box .clkbtnlogin");
let signupButton = document.querySelector(".signup-box .clkbtnsignup");

signupsw.addEventListener("click", () => {
    slider.classList.add("moveslider");
    formSection.classList.add("form-section-move");
});

loginsw.addEventListener("click", () => {
    slider.classList.remove("moveslider");
    formSection.classList.remove("form-section-move");
});

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
    fetch('/app.signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(signupInfo)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error: ' + response.statusText);
            }
            return response.text();
        })
        .then(session_id => {
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
            });
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error: ' + response.statusText);
            }
            return response.text();
        })
        .then(session_id => {
            // Fetch mainchat data using session_id
            return fetch('/mainchat/' + session_id, {
                method: 'GET'
            });
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            // Handle mainchat data
        })
        .catch(error => console.error('Error:', error));
});


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
                throw new Error('Error: ' + response.statusText);
            }
            return response.text();
        })
        .then(session_id => {
            // Redirect to mainchat with session ID
            window.location.href = '/mainchat/' + session_id;
        })
        .catch(error => console.error('Error:', error));
});
