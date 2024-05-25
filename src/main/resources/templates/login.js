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
    console.log("huytransieudeptrai");
    let name_ca = signup.getElementsByClassName('namesign ele');
    let email_ca = signup.getElementsByClassName('emailsign ele');
    let password_ca = signup.getElementsByClassName('passwordsign ele');
    let passwordcf_ca = signup.getElementsByClassName('passwordcfsign ele');
    console.log(name_ca[0].value);
    console.log(typeof name_ca[0].value);
    if (password_ca[0].value !== passwordcf_ca[0].value) {
        alert("Password mismatched");
    }
    else {
        const signupInfo = {
            name: name_ca[0].value,
            username: email_ca[0].value,
            password: password_ca[0].value
        };
        console.log(JSON.stringify(signupInfo))
        fetch('/app.signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(signupInfo)
        })
        const loginInfo = {
            username: email_ca,
            password: password_ca
        };

        fetch('/app.login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginInfo)
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('Error: ' + response.statusText);
                }
            })
            .then(session_id => {
                return fetch('/' + session_id);
            })
            .then(response => response.json())
            .then(data => console.log(data))
            .catch(error => console.error('Error:', error));

    }
});

loginButton.addEventListener("click", () => {
    console.log("gay");
    let username = login.getElementsByClassName('emaillog ele');
    let password = login.getElementsByClassName('passwordlog ele');
    console.log(username[0].value);
    const loginInfo = {
        username: username[0].value,
        password: password[0].value
    };

    console.log(loginInfo);

    fetch('/app.login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginInfo)
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Error: ' + response.statusText);
            }
        })
        .then(session_id => {
            return fetch('/' + session_id);
        })
        .then(response => response.json())
        .then(data => console.log(data))
        .catch(error => console.error('Error:', error));

});