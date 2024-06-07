// start: Sidebar
const session_id=getSessionIdFromQuery();

const wsUrl = 'ws://localhost:8080/ws'; // Adjust the URL according to your server configuration

const client = new StompJs.Client({
    brokerURL: wsUrl,
    connectHeaders: {
        login: 'user',
        passcode: 'password',
    },
    debug: function (str) {
        console.log('[DEBUG]', str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
});

function getSessionIdFromQuery() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

client.onConnect = function (frame) {
    console.log('Connected to WebSocket server'); // Add a console log statement to verify connection
    // Do something, all subscribes must be done in this callback
    // This is needed because this will be executed after a (re)connect
};

client.onStompError = function (frame) {
    console.log('WebSocket error:', frame.headers['message'], frame.body); // Add a console log statement for WebSocket errors
};

client.activate();

document.querySelector('.chat-sidebar-profile-toggle').addEventListener('click', function (e) {
    e.preventDefault()
    this.parentElement.classList.toggle('active')
});

document.addEventListener('click', function (e) {
    if (!e.target.matches('.chat-sidebar-profile, .chat-sidebar-profile *')) {
        document.querySelector('.chat-sidebar-profile').classList.remove('active')
    }
})
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const suggestionsList = document.getElementById('suggestionsList');

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

    document.addEventListener('click', (event) => {
        if (!event.target.closest('.content-sidebar-form')) {
            suggestionsList.style.display = 'none';
        }
    });
});

// end: Sidebar


// start: Coversation
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

document.querySelectorAll('.conversation-form-input').forEach(function (item) {
    item.addEventListener('input', function () {
        this.rows = this.value.split('\n').length
    })
})

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
// end: Coversation

//mainchat
document.addEventListener('DOMContentLoaded', (event) => {
    const chatForm = document.querySelector('.conversation-form-group');
    const chatInput = document.querySelector('.conversation-form-input');
    const chatList = document.querySelector('.conversation-wrapper');

    // Fetch chat rooms when the page loads
    fetchChatRooms();

    // Event listener for sending messages
    document.querySelector('.conversation-form-submit').addEventListener('click', () => {
        sendMessage(chatInput.value);
    });

    // Function to fetch chat rooms
    function fetchChatRooms() {
        fetch(`/app/${session_id}/loadchat`)  // Replace 'session_id' with actual session ID
            .then(response => response.json())
            .then(data => {
                // Populate the chat room list
                populateChatRooms(data);
            })
            .catch(error => console.error('Error fetching chat rooms:', error));
    }//chatroomInfo co 2 attributes: name, chat_id
    //List co nhieu chatroomInfo

    // Function to send a message

    // Function to populate chat rooms in the UI
    function populateChatRooms(chatRooms) {
        // Assuming chat rooms are displayed in .content-messages-list
        const chatList = document.querySelector('.content-messages-list');
        chatList.innerHTML = '';  // Clear the list

        chatRooms.forEach(room => {
            const chatItem = document.createElement('li');
            chatItem.innerHTML = `
                    <a href="#" data-conversation="#conversation-${room.id}">
                        <img class="content-message-image" src="${room.image}" alt="">
                        <span class="content-message-info">
                            <span class="content-message-name">${room.name}</span>
                            <!--<span class="content-message-text">${room.lastMessage}</span>
                        </span>
                        <span class="content-message-more">
                            <span class="content-message-time">${room.lastMessageTime}</span>
                        </span>-->
                        </span>
                    </a>`;
            chatList.appendChild(chatItem);
        });
    }

    // Function to add a new message to the chat
    function addMessageToChat(message) {
        const messageItem = document.createElement('li');
        messageItem.classList.add('conversation-item', 'me');
        messageItem.innerHTML = `
                <div class="conversation-item-side">
                    <img class="conversation-item-image" src="YOUR_IMAGE_URL" alt="">
                </div>
                <div class="conversation-item-content">
                    <div class="conversation-item-wrapper">
                        <div class="conversation-item-box">
                            <div class="conversation-item-text">
                                <p>${message.content}</p>
                                <div class="conversation-item-time">${message.time}</div>
                            </div>
                        </div>
                    </div>
                </div>`;
        chatList.appendChild(messageItem);
    }
});
//end: mainchat
//loadchat
document.addEventListener('DOMContentLoaded', () => {
    // Function to load chatrooms
    function loadChatrooms(session_id) {
        fetch(`/app/${session_id}/loadchat`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(chatrooms => {
                displayChatrooms(chatrooms);
            })
            .catch(error => console.error('Error:', error));
    }

    // Function to display chatrooms
    function displayChatrooms(chatrooms) {
        const chatroomContainer = document.querySelector('#chatroomContainer');
        chatroomContainer.innerHTML = ''; // Clear existing chatrooms
        chatrooms.forEach(chatroom => {
            const chatroomElement = document.createElement('div');
            chatroomElement.classList.add('chatroom');
            chatroomElement.textContent = chatroom.name; // Adjust according to your chatroomInfo structure
            chatroomContainer.appendChild(chatroomElement);
        });
    }

    // Load chatrooms for the given session_id (you need to get the session_id from somewhere)
    // Replace with actual session ID logic
    loadChatrooms(session_id);
});


document.addEventListener("DOMContentLoaded", () => {
    const session_id = "example_session_id";

    // Load chat rooms
    fetch(`/app/${session_id}/loadchat`)
        .then(response => response.json())
        .then(data => {
            const chatList = document.getElementById("chat-list");
            data.forEach(chatroom => {
                const listItem = document.createElement("li");
                listItem.innerHTML = `
                            <a href="#" data-conversation="#conversation-sai">
                                <img class="content-message-image" src="sai" alt="">
                                <span class="content-message-info">
                                    <span class="content-message-name">sai</span>
                                    <!--<span class="content-message-text">${chatroom.lastMessage}</span>
                                </span>
                                <span class="content-message-more">
                                    <span class="content-message-time">${chatroom.lastMessageTime}</span>-->
                                </span>
                            </a>
                        `;
                chatList.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error:', error));
});