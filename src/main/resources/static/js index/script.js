// MAPPING ELEMENTS
const session_id=getSessionIdFromQuery();
const chat_id_current="";
const conversationFormButtons = {
    emotion: document.querySelector(".conversation-form-button .ri-emotion-line"),
    record: document.querySelector(".conversation-form-record .ri-mic-line"),
    submitMessage: document.querySelector(".conversation-form-submit .ri-send-plane-2-line")
};
var chatLink = document.getElementById("chat-link");
var contactsLink = document.getElementById("contacts-link");
let inputData="";

// GET SESSION ID

function getSessionIdFromQuery() {
    var url = window.location.href;
    var queryString = url.split('?')[1];
    var queryParams = queryString.split('&');
    for (var i = 0; i < queryParams.length; i++) {
        var pair = queryParams[i].split('=');
        if (pair[0] === 'id') {
            return pair[1];
        }
    }
    return null;
}

//SETTING UP FOR ELEMENT TO LOAD PAGE INCLUDING SESSION ID

chatLink.href = `mainchat.html?id=${session_id}`;
contactsLink.href = `friendlist.html?id=${session_id}`;

//INITIALIZE WEBSOCKET

function connectWebSocket() {// Retrieve session ID from local storage

    if (!session_id) {
        console.error('Session ID is not available.');
        return;
    }

    const wsUrl = 'ws://localhost:8080/ws'; // Adjust the URL according to your server configuration

    const client = new StompJs.Client({
        brokerURL: wsUrl,
        connectHeaders: {
            'session-id': session_id, // Use session ID in connection headers
        },
        debug: function (str) {
            console.log('[DEBUG]', str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    client.onConnect = function (frame) {
        console.log('Connected to WebSocket server');
    };

    client.onStompError = function (frame) {
        console.log('WebSocket error:', frame.headers['message'], frame.body);
    };

    client.activate();
}

// UI STUFFS

document.querySelector('.chat-sidebar-profile-toggle').addEventListener('click', function (e) {
    e.preventDefault()
    this.parentElement.classList.toggle('active')
});

document.addEventListener('click', function (e) {
    if (!e.target.matches('.chat-sidebar-profile, .chat-sidebar-profile *')) {
        document.querySelector('.chat-sidebar-profile').classList.remove('active')
    }
})

//SEARCH BAR IN CHAT PAGE

document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const suggestionsList = document.getElementById('suggestionsList');
    // implement the search into this suggestions array
    const suggestions = [
        {text: 'Apple', icon: '🍎'},
        {text: 'Banana', icon: '🍌'},
        {text: 'Cherry', icon: '🍒'},
        {text: 'Date', icon: '🌴'},
        {text: 'Elderberry', icon: '🍇'},
        {text: 'Fig', icon: '🍈'},
        {text: 'Grape', icon: '🍇'},
        {text: 'Honeydew', icon: '🍈'}
    ];

    searchInput.addEventListener('input', function () {
        const query = this.value;
        //fetch search engine inside here
        // For demonstration, we'll use static suggestions.
        // In a real implementation, this would involve making an AJAX request to fetch suggestions.
        const suggestions = [
            'apple',
            'banana',
            'cherry',
            'date',
            'elderberry',
            'fig',
            'grape',
            'honeydew'
        ].filter(item => item.toLowerCase().includes(query.toLowerCase()));

        // Clear existing suggestions
        while (suggestionsList.firstChild) {
            suggestionsList.removeChild(suggestionsList.firstChild);
        }

        if (suggestions.length > 0) {
            suggestionsList.style.display = 'block';
            suggestions.forEach(item => {
                const li = document.createElement('li');
                li.textContent = item;
                li.addEventListener('click', () => {
                    searchInput.value = item;
                    suggestionsList.style.display = 'none';
                });
                suggestionsList.appendChild(li);
            });
        } else {
            suggestionsList.style.display = 'none';
        }
    });
    //display the search bar
    document.addEventListener('click', (event) => {
        if (!event.target.closest('.content-sidebar-form')) {
            suggestionsList.style.display = 'none';
        }
    });
});

// MESSAGE BUTTON FUNCTION (SHARE,DELETE WHEN CLICK THE MESSAGE)

document.querySelectorAll('.conversation-item-dropdown-toggle').forEach(function (item) {
    item.addEventListener('click', function (e) {
        e.preventDefault()
        if (this.parentElement.classList.contains('active')) {
            this.parentElement.classList.remove('active')
        } else {
            document.querySelectorAll('.conversation-item-dropdown').forEach(function (i) {
                i.classList.remove('active')
            })
            this.parentElement.classList.add('active')
        }
    })
})

document.addEventListener('click', function (e) {
    if (!e.target.matches('.conversation-item-dropdown, .conversation-item-dropdown *')) {
        document.querySelectorAll('.conversation-item-dropdown').forEach(function (i) {
            i.classList.remove('active')
        })
    }
})

//UI STUFFS TO FOCUS ON 1 CHATROOM AT THE SAME TIME

document.querySelectorAll('[data-conversation]').forEach(function (item) {
    item.addEventListener('click', function (e) {
        e.preventDefault()
        document.querySelectorAll('.conversation').forEach(function (i) {
            i.classList.remove('active')
        })
        document.querySelector(this.dataset.conversation).classList.add('active')
    })
})

document.querySelectorAll('.conversation-back').forEach(function (item) {
    item.addEventListener('click', function (e) {
        e.preventDefault()
        this.closest('.conversation').classList.remove('active')
        document.querySelector('.conversation-default').classList.add('active')
    })
})

// LOAD ALL CHATROOM

document.addEventListener("DOMContentLoaded", () => {
    fetch(`/app/${session_id}/loadchat`)
        .then(response => response.json())
        .then(data => {
            const messagesList = document.querySelector(".content-messages-list"); // Target the ul element
            data.forEach(chatroomInfo => {
                const listItem = document.createElement("li");
                listItem.classList.add("content-message");
                listItem.innerHTML = `
                <a href="#" data-conversation="${chatroomInfo.chat_id}">
                    <img class="content-message-image" src="${chatroomInfo.name}" alt="">
                    <span class="content-message-info">
                        <span class="content-message-name">${chatroomInfo.name}</span>
                    </span>
                </a>
            `;

                // Attach click event listener to the list item
                listItem.addEventListener("click", function(event) {

                    const conversationId = this.querySelector("a").getAttribute("data-conversation");
                    console.log("Conversation ID:", conversationId);
                    createConversationUser(chatroomInfo.name)
                    fetch(`/app/${session_id}/${conversationId}/loadm`)
                        .then(response => response.json())
                        .then(messages => {
                            messages.forEach(message => {
                                createConversationItem(message);
                            });
                        })
                        .catch(error => console.error('Error:', error));
                    createSubmitForm()
                });


                messagesList.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error:', error));

});

// CREATE CHAT WINDOW FUNCTIONS

function createConversationUser(name,status) {
    const userDiv = document.createElement("div");
    userDiv.classList.add("conversation-user");

    userDiv.innerHTML = `
        <div>
            <div class="conversation-user-name">${name}</div>
        </div>
    `;

    return userDiv;
}
//isMe: false=sent by me
//true=sent by other user
function createConversationItem(message) {
    const itemLi = document.createElement("li");
    itemLi.classList.add("conversation-item", isMe ? "me" : "");

    itemLi.innerHTML = `
        <div class="conversation-item-side">
            <img class="conversation-item-image" src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8cGVvcGxlfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60" alt="">
        </div>
        <div class="conversation-item-content">
            <div class="conversation-item-wrapper">
                <div class="conversation-item-box">
                    <div class="conversation-item-text">
                        <p>${message.message}</p>
                        <div class="conversation-item-time">${message.time}</div>
                    </div>
                    <div class="conversation-item-dropdown">
                        <button type="button" class="conversation-item-dropdown-toggle"><i class="ri-more-2-line"></i></button>
                        <ul class="conversation-item-dropdown-list">
                            <li><a href="#"><i class="ri-share-forward-line"></i> Forward</a></li>
                            <li><a href="#"><i class="ri-delete-bin-line"></i> Delete</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    `;
    return itemLi;
}
function createSubmitForm() {
    const conversationDiv = document.createElement("div");
    conversationDiv.classList.add("conversation-form");

    conversationDiv.innerHTML = `
        <button type="button" class="conversation-form-button"><i class="ri-emotion-line"></i></button>
        <div class="conversation-form-group">
            <textarea class="conversation-form-input" rows="1" placeholder="Type here..."></textarea>
            <button type="button" class="conversation-form-record"><i class="ri-mic-line"></i></button>
        </div>
        <button type="button" class="conversation-form-button conversation-form-submit"><i class="ri-send-plane-2-line"></i></button>
    `;

    return conversationDiv;
}

// WS MESSAGE FUNCTION

function newSubcription(chat_id){
    client.subcribe(`/app/${session_id}/${chat_id}`);
}
function sendMessage(session_id,chat_id,message,timestamp){
    client.publish({
        destination: `/app/${session_id}/${chat_id}/sendm`,
        body: JSON.stringify(session_id,chat_id,message,timestamp),
    })
}
function unsubcribe(chat_id){
    client.unsubscribe(`/app/${session_id}/${chat_id}`);
}

// MESSAGE DATA

document.querySelectorAll('.conversation-form-input').forEach(function (item) {
    item.addEventListener('input', function () {
        this.rows = this.value.split('\n').length;
        inputData=this.value
    })
})

//SEND MESSAGE BUTTON EXECUTION
conversationFormButtons.submitMessage.addEventListener('click', (event) => {
    sendMessage(session_id,chat_id_current,inputData,new Date());
})