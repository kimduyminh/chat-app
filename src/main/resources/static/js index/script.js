// MAPPING ELEMENTS
const session_id=getSessionID();
let chat_id_current="";
let client =null;
const conversationFormButtons = {
    emotion: document.querySelector(".conversation-form-button .ri-emotion-line"),
    record: document.querySelector(".conversation-form-record .ri-mic-line"),
    submitMessage: document.querySelector(".conversation-form-submit .ri-send-plane-2-line")
};
var chatLink = document.getElementById("chat-link");
var contactsLink = document.getElementById("contacts-link");
let inputData="";
let newlyCreatedChatId="";

// GET SESSION ID
connectWebSocket()
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

    const wsUrl = '/ws'; // Adjust the URL according to your server configuration

    client = new StompJs.Client({
        brokerURL: wsUrl,
        connectHeaders: {
            'session-id': session_id, // Use session ID in connection headers
        },
        debug: function (str) {
            console.log('[DEBUG]', str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 400,
        heartbeatOutgoing: 400,
    });

    client.onConnect = function (frame) {
        console.log('Connected to WebSocket server');
    };
    client.onMessage=function (event) {
        loadMessages(session_id,chat_id_current)
    }

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

// Define loadchat globally
function loadchat(session_id) {
    fetch(`/app/${session_id}/loadchat`)
        .then(response => response.json())
        .then(data => {
            const messagesList = document.querySelector(".content-messages-list");

            // Clear existing messages
            messagesList.innerHTML = '';
            data.forEach(chatroomInfo => {
                const listItem = document.createElement("li");
                listItem.classList.add("content-message");
                listItem.innerHTML = `
                    <a href="#" data-conversation="${chatroomInfo.chat_id}">
                        <img class="content-message-image" src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8cGVvcGxlfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60" alt="">
                        <span class="content-message-info">
                            <span class="content-message-name">${chatroomInfo.name}</span>
                        </span>
                    </a>
                `;

                listItem.addEventListener("click", function(event) {
                    event.preventDefault();  // Prevent default anchor behavior
                    const conversationId = this.querySelector("a").getAttribute("data-conversation");
                    console.log("Conversation ID:", conversationId);
                    const userDiv = createConversationUser(chatroomInfo.name);
                    document.querySelector(".conversation").prepend(userDiv);
                    newSubscription(conversationId);
                    chat_id_current = conversationId;

                    // Clear the existing conversation items
                    const conversationList = document.querySelector(".conversation-list");
                    conversationList.innerHTML = '';
                    loadMessages(session_id,chat_id_current)

                    // if (conversationId) {
                    //     fetch(`/app/${session_id}/${conversationId}/loadm`)
                    //         .then(response => response.json())
                    //         .then(messages => {
                    //             if (Array.isArray(messages)) {
                    //                 messages.forEach(message => {
                    //                     const conversationItem = createConversationItem(message);
                    //                     conversationList.appendChild(conversationItem);
                    //                 });
                    //             } else {
                    //                 console.log('No messages to display');
                    //             }
                    //         })
                    //         .catch(error => console.error('Error:', error));
                    // } else {
                    //     console.error('Conversation ID is undefined');
                    // }
                    //
                    const formDiv = createSubmitForm();
                    document.querySelector(".conversation").appendChild(formDiv);
                    addChatOption()
                });

                messagesList.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener("DOMContentLoaded", function() {
    loadchat(session_id);
});



function createConversationUser(name) {
    // const
    // userDiv.innerHTML = `
    //     <div>
    //         <div class="conversation-user-name">${name}</div>
    //     </div>
    // `;
    // return userDiv;

    const userDiv = document.getElementsByClassName('conversation-user')[0];
    if (userDiv) {
        userDiv.innerHTML = ''; // Clear existing content
        const userHTML = `
        <div class="conversation-user-name">${name}</div>
    `;
        userDiv.insertAdjacentHTML('beforeend', userHTML);
    } else {
        console.error('No element with class conversation-user found');
    }
    return userDiv;
}

// function createConversationItem(message) {
//     const itemLi = document.createElement("li");
//     const isMe = message.sentBySession;
//     itemLi.classList.add("conversation-item", isMe ? "me" : "");
//
//     const formattedTime = new Date(message.time).toLocaleString();
//     itemLi.innerHTML = `
//         <div class="conversation-item-side">
//             <img class="conversation-item-image" src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8cGVvcGxlfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60" alt="">
//         </div>
//         <div class="conversation-item-content">
//             <div class="conversation-item-wrapper">
//                 <div class="conversation-item-box">
//                     <div class="conversation-item-text">
//                         <p>${escapeHtml(message.message)}</p>
//                         <div class="conversation-item-time">${formattedTime}</div>
//                     </div>
//                     <div class="conversation-item-dropdown">
//                         <button type="button" class="conversation-item-dropdown-toggle"><i class="ri-more-2-line"></i></button>
//                         <ul class="conversation-item-dropdown-list">
//                             <li><a href="#"><i class="ri-share-forward-line"></i> Forward</a></li>
//                             <li><a href="#"><i class="ri-delete-bin-line"></i> Delete</a></li>
//                         </ul>
//                     </div>
//                 </div>
//             </div>
//         </div>
//     `;
//     return itemLi;
// }

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function createSubmitForm() {
    // const conversationDiv = document.createElement("div");
    // conversationDiv.classList.add("conversation-form");
    // conversationDiv.innerHTML = '';
    // conversationDiv.innerHTML = `
    //     <button type="button" class="conversation-form-button"><i class="ri-emotion-line"></i></button>
    //     <div class="conversation-form-group">
    //         <textarea class="conversation-form-input" rows="1" placeholder="Type here..."></textarea>
    //         <button type="button" class="conversation-form-record"><i class="ri-mic-line"></i></button>
    //     </div>
    //     <button type="button" id="submitButton" class="conversation-form-button conversation-form-submit"><i class="ri-send-plane-2-line"></i></button>
    // `;

    // const converDiv = document.getElementById('delete-temp')[0];
    // converDiv.innerHTML = '';
    const conversationDiv = document.getElementsByClassName('conversation-form')[0];
    conversationDiv.innerHTML = '';
    const conversationHTML = `
        <button type="button" class="conversation-form-button"><i class="ri-emotion-line"></i></button>
        <div class="conversation-form-group">
            <textarea class="conversation-form-input" rows="1" placeholder="Type here..."></textarea>
            <button type="button" class="conversation-form-record"><i class="ri-mic-line"></i></button>
        </div>
        <button type="button" id="submitButton" class="conversation-form-button conversation-form-submit"><i class="ri-send-plane-2-line"></i></button>
    `;
    conversationDiv.insertAdjacentHTML('beforeend', conversationHTML);

    const submitButton = conversationDiv.querySelector('#submitButton');
    //SEND MESSAGE BUTTON EXECUTION
    submitButton.addEventListener('click', (event) => {
        const inputData = conversationDiv.querySelector('.conversation-form-input').value;
        console.log(inputData);
        console.log(chat_id_current)
        sendMessage(session_id, chat_id_current, inputData, new Date());

        loadMessages(session_id, chat_id_current);


    });

    return conversationDiv;
}

// WS MESSAGE FUNCTION

function newSubscription(chat_id){
    client.subscribe(`/topic/${session_id}/${chat_id}`);
}
function sendMessage(session_id, chat_id, message, timestamp) {
    client.publish({
        destination: `/app/${session_id}/${chat_id}/sendm`,
        body: JSON.stringify({
            session_id: session_id,
            chat_id: chat_id,
            message: message,
            timestamp: timestamp
        }),
    });
}
function unsubscribe(chat_id){
    client.unsubscribe(`/topic/${session_id}/${chat_id}`);
}

// MESSAGE DATA

document.querySelectorAll('.conversation-form-input').forEach(function (item) {
    item.addEventListener('input', function () {
        this.rows = this.value.split('\n').length;
        inputData=this.value
    })
})

// CREATE CHATROOM POPUP

document.addEventListener('DOMContentLoaded', (event) => {
    const createChatButton = document.querySelector('.create-chat');
    const popup = document.getElementById('popup');
    const closeButton = document.querySelector('.close-button');
    const popupSearchInput = document.getElementById('popupSearchInput');
    const popupSearchResults = document.getElementById('popupSearchResults');
    const selectedItems = document.getElementById('selectedItems');
    const groupNameInput = document.getElementById('groupName');
    const createGroupBtn = document.querySelector('.create-group-btn');

    let selectedUserIds = [];

    // Function to show the popup
    function showPopup() {
        popup.style.display = 'block';
    }

    // Function to close the popup
    function closePopup() {
        popup.style.display = 'none';
    }

    // Event listener for the create chat button
    createChatButton.addEventListener('click', showPopup);

    // Event listener for the close button
    closeButton.addEventListener('click', closePopup);

    // Event listener for closing the popup when clicking outside
    window.onclick = function(event) {
        if (event.target == popup) {
            popup.style.display = "none";
        }
    }

    popupSearchInput.onkeyup = function() {
        const filter = popupSearchInput.value;

        fetch(`/app/${session_id}/find`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ info: filter })
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Failed to fetch users');
                }
            })
            .then(data => {
                popupSearchResults.innerHTML = "";
                const filteredData = data.filter(item => item.name.toLowerCase().includes(filter));
                filteredData.forEach(item => {
                    const li = document.createElement("li");
                    li.textContent = item.name;
                    li.onclick = () => addItem(item);
                    popupSearchResults.appendChild(li);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                popupSearchResults.innerHTML = "<li>Failed to fetch users</li>";
            });
    }

    function addItem(item) {
        selectedUserIds.push(item.user_id);
        const li = document.createElement("li");
        li.textContent = item.name;
        li.onclick = function() {
            selectedItems.removeChild(li);
            selectedUserIds = selectedUserIds.filter(id => id !== item.user_id);
        };
        selectedItems.appendChild(li);
    }

    // createGroupBtn.onclick = function() {
    //     const groupName = groupNameInput.value;
    //     const newGroup = {
    //         name: groupName,
    //         session_id: session_id
    //     }
    //
    //     fetch(`/app/${session_id}/createChatroom`, {
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         },
    //         body: JSON.stringify(newGroup)
    //     })
    //         .then(response => {
    //             if (response.ok) {
    //                 loadchat(session_id);
    //                 return response.text();
    //             } else {
    //                 throw new Error('Failed to create chatroom');
    //             }
    //         })
    //         .then(chatId => {
    //             alert(`Nhóm đã tạo với tên: ${groupName} và các thành viên: ${selectedUserIds.join(", ")}`);
    //             popup.style.display = "none";
    //             selectedItems.innerHTML = "";
    //             groupNameInput.value = "";
    //
    //             selectedUserIds.forEach(userId => {
    //                 fetch(`/app/${session_id}/${chatId}/${userId}/add`, {
    //                     method: 'GET'
    //                 })
    //                     .then(response => {
    //                         if (response.ok) {
    //                             console.log(`User ${userId} added to chat ${chatId}`);
    //                         } else {
    //                             throw new Error(`Failed to add user ${userId} to chat ${chatId}`);
    //                         }
    //                     })
    //                     .catch(error => {
    //                         console.error('Error:', error);
    //                     });
    //             });
    //
    //             selectedUserIds = [];
    //         })
    //         .catch(error => {
    //             console.error('Error:', error);
    //             alert("Failed to create chatroom");
    //         });
    // }
    if (!createGroupBtn._listenerAttached) {
        createGroupBtn.onclick = function() {
            console.log("Button clicked");

            const groupName = groupNameInput.value;
            const newGroup = {
                name: groupName,
                session_id: session_id
            };

            fetch(`/app/${session_id}/createChatroom`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newGroup)
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Chatroom created successfully");
                        loadchat(session_id);
                        return response.text();
                    } else {
                        throw new Error('Failed to create chatroom');
                    }
                })
                .then(chatId => {
                    alert(`Nhóm đã tạo với tên: ${groupName} và các thành viên: ${selectedUserIds.join(", ")}`);
                    popup.style.display = "none";
                    selectedItems.innerHTML = "";
                    groupNameInput.value = "";

                    selectedUserIds.forEach(userId => {
                        fetch(`/app/${session_id}/${chatId}/${userId}/add`, {
                            method: 'GET'
                        })
                            .then(response => {
                                if (response.ok) {
                                    console.log(`User ${userId} added to chat ${chatId}`);
                                } else {
                                    throw new Error(`Failed to add user ${userId} to chat ${chatId}`);
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                            });
                    });

                    selectedUserIds = [];
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert("Failed to create chatroom");
                });
        };
        createGroupBtn._listenerAttached = true;
    }
});

// Huy Tran's work
function getSessionID() {
    const queryParams = new URLSearchParams(window.location.search);
    return queryParams.get('id');
}

async function loadMessages(session_id, chat_id) {
    console.log('fetching data ' + session_id + ' ' + chat_id);
    try {
        const response = await fetch(`/app/${session_id}/${chat_id}/loadm`);
        if (response.ok) {
            const responseData = await response.json();
            console.log('huy tran ' + responseData.messages);
            const chatGrid = document.getElementById('chat-content-fetch');
            chatGrid.innerHTML = ''; // Clear existing content

            responseData.messages.forEach(message => {
                let chatHTML = '';

                if (message.sentBySession) {
                    chatHTML = `
                    <li class="conversation-item">
                    `;
                } else {
                    chatHTML = `
                    <li class="conversation-item me">
                    `;
                }

                chatHTML += `
                    <div class="conversation-item-side">
                        <img class="conversation-item-image" src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8cGVvcGxlfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60" alt="">
                    </div>
                    <div class="conversation-item-content">
                        <div class="conversation-item-wrapper">
                            <div class="conversation-item-box">
                                <div class="conversation-item-text">
                                    <p>${escapeHtml(message.message)}</p>
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
                    </li>
                `;

                chatGrid.insertAdjacentHTML('beforeend', chatHTML);
            });
        } else {
            console.error('Failed to fetch messages');
        }
    } catch (error) {
        console.error('Error fetching messages:', error);
    }
}

// Chat option
function addChatOption() {
    const chatOption = document.getElementById('conversation-option');
    chatOption.innerHTML = '';

    let buttonHTML = `
        <button class="menu-btn-chat" id="menuBtn-chat">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
        </button>

        <div class="popup-chat" id="popup-chat">
            <ul>
                <li onclick="showChangeChatName()">Change Chat Name</li>
                <li onclick="showKickFromChat()">Kick from Chat</li>
                <li onclick="showDeleteChat()">Delete Chat</li>
                <li onclick="showAddToChat()">Add to Chat</li>
            </ul>
        </div>

        <!-- Popups for each action -->
        <div class="action-popup-chat" id="changeChatNamePopup-chat">
            <h3>Change Chat Name</h3>
            <form id="changeChatNameForm-chat">
                <input type="text" placeholder="New Chat Name">
                <button type="submit">Submit</button>
            </form>
        </div>

        <div class="action-popup-chat" id="kickFromChatPopup-chat">
            <h3>Kick User</h3>
            <form id="kickFromChatForm-chat">
                <!-- User list will be populated dynamically -->
            </form>
        </div>

        <div class="action-popup-chat" id="deleteChatPopup-chat">
            <h3>Are you sure?</h3>
            <button onclick="deleteChat()">Yes</button>
            <button onclick="closePopup('deleteChatPopup-chat')">No</button>
        </div>

        <div class="action-popup-chat" id="addToChatPopup-chat">
            <h3>Add User</h3>
            <form id="addToChatForm-chat">
                <!-- User list will be populated dynamically -->
            </form>
        </div>
    `;

    chatOption.innerHTML = buttonHTML;

    const menuBtn = document.getElementById('menuBtn-chat');
    if (menuBtn) {
        menuBtn.addEventListener('click', () => {
            const popup = document.getElementById('popup-chat');
            popup.style.display = popup.style.display === 'block' ? 'none' : 'block';
        });
    } else {
        console.error('Element with ID "menuBtn-chat" not found.');
    }

    // Kick button assign
    const kickForm = document.getElementById('kickFromChatForm-chat');
    if (kickForm) {
        kickForm.addEventListener('submit', handleKickFromChatSubmit);
    }

    // Ensure no form has an action attribute
    document.querySelectorAll('form').forEach(form => {
        form.removeAttribute('action');
    });
}

// Button function

function showChangeChatName() {
    closeAllPopups();
    document.getElementById('changeChatNamePopup-chat').style.display = 'block';
}

function showKickFromChat() {
    closeAllPopups();
    document.getElementById('kickFromChatPopup-chat').style.display = 'block';

    // Dynamically fetch users
    fetch(`/app/${session_id}/${chat_id_current}/listUser`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const users = data.map(user => ({
                user_id: user.user_id,
                name: user.name
            }));

            const form = document.getElementById('kickFromChatForm-chat');
            form.innerHTML = users.map(user => `
                <label>
                    <input type="checkbox" name="users" value="${user.user_id}"> ${user.name}
                </label>
            `).join('');
            form.innerHTML += '<button type="submit">Submit</button>';
        })
        .catch(error => {
            console.error('Error fetching users:', error);
        });
}

function handleKickFromChatSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const selectedUsers = formData.getAll('users');

    const kickPromises = selectedUsers.map(user_id => {
        return fetch(`/app/${session_id}/${chat_id_current}/${user_id}/kick`, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log(`User ${user_id} kicked successfully`, data);
            })
            .catch(error => {
                console.error(`Error kicking user ${user_id}:`, error);
            });
    });

    Promise.all(kickPromises).then(() => {
        closePopup('kickFromChatPopup-chat');
        loadchat(session_id);
    });
}

function showDeleteChat() {
    closeAllPopups();
    document.getElementById('deleteChatPopup-chat').style.display = 'block';
}

function deleteChat() {
    fetch(`/app/${session_id}/${chat_id_current}/delete`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Chat deleted successfully', data);
            closePopup('deleteChatPopup-chat');
            loadchat(session_id);
        })
        .catch(error => {
            console.error('Error deleting chat:', error);
        });
}

function showAddToChat() {
    closeAllPopups();
    document.getElementById('addToChatPopup-chat').style.display = 'block';

    // Dynamically fetch users
    fetch(`/app/${session_id}/${chat_id_current}/listNonMembers`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            const users = data.map(user => ({
                user_id: user.user_id,
                name: user.name
            }));

            const form = document.getElementById('addToChatForm-chat');
            form.innerHTML = users.map(user => `
                <label>
                    <input type="checkbox" name="users" value="${user.user_id}"> ${user.name}
                </label>
            `).join('');
            form.innerHTML += '<button type="submit">Submit</button>';
        })
        .catch(error => {
            console.error('Error fetching users:', error);
        });
}

function closeAllPopups() {
    document.querySelectorAll('.action-popup-chat').forEach(popup => {
        popup.style.display = 'none';
    });
}

function closePopup(id) {
    document.getElementById(id).style.display = 'none';
}




// FRIEND PAGE
document.addEventListener("DOMContentLoaded", function () {
    const friendList = document.getElementById("friendList");
    const friendRequestsList = document.getElementById("friendRequestsList");
    const searchFriendInput = document.getElementById("searchFriendInput");
    const addFriendBtn = document.getElementById("addFriendBtn");

    // var chatLink = document.getElementById("chat-link");
    // var contactsLink = document.getElementById("contacts-link");
    // chatLink.href = `mainchat.html?id=${session_id}`;
    // contactsLink.href = `friendlist.html?id=${session_id}`;
    // Dummy data
    let friends = ["Alice", "Bob", "Charlie"];
    let friendRequests = ["David", "Eve"];
    let allUsers = ["Alice", "Bob", "Charlie", "David", "Eve", "Tuan Minh", "Kim Minh", "Quan Ngoo", "Huy Tran"];

    // Function to list friends
    function listFriends() {
        friendList.innerHTML = "";
        friends.forEach(friend => {
            const li = document.createElement("li");
            li.textContent = friend;
            friendList.appendChild(li);
        });
    }

    // Function to send friend request
    function sendFriendRequest(friendName) {
        if (!friends.includes(friendName) && !friendRequests.includes(friendName)) {
            friendRequests.push(friendName);
            alert(`Friend request sent to ${friendName}`);
            loadFriendRequests();
        } else {
            alert(`${friendName} is already your friend or has a pending request.`);
        }
    }

    // Function to load friend requests
    function loadFriendRequests() {
        friendRequestsList.innerHTML = "";
        friendRequests.forEach(request => {
            const li = document.createElement("li");
            li.textContent = request;
            const acceptBtn = document.createElement("button");
            acceptBtn.textContent = "Accept";
            acceptBtn.addEventListener("click", () => acceptFriendRequest(request));
            li.appendChild(acceptBtn);
            friendRequestsList.appendChild(li);
        });
    }

    // Function to accept friend request
    function acceptFriendRequest(friendName) {
        friendRequests = friendRequests.filter(request => request !== friendName);
        friends.push(friendName);
        alert(`${friendName} accepted`);
        loadFriendRequests();
        listFriends();
    }

    // Function to find friends
    function findFriend(query) {
        const results = allUsers.filter(user => user.toLowerCase().includes(query.toLowerCase()) && !friends.includes(user));
        friendList.innerHTML = "";
        results.forEach(result => {
            const li = document.createElement("li");
            li.textContent = result;
            const requestBtn = document.createElement("button");
            requestBtn.textContent = "Send Request";
            requestBtn.addEventListener("click", () => sendFriendRequest(result));
            li.appendChild(requestBtn);
            friendList.appendChild(li);
        });
    }

    // Event listener for search input
    searchFriendInput.addEventListener("input", function () {
        const query = searchFriendInput.value.trim();
        if (query) {
            findFriend(query);
        } else {
            listFriends();
        }
    });

    // Event listener for add friend button
    addFriendBtn.addEventListener("click", function () {
        const friendName = searchFriendInput.value.trim();
        if (friendName) {
            sendFriendRequest(friendName);
        }
    });

    // Initial load
    listFriends();
    loadFriendRequests();
});

function chat_Link() {
    console.log(getSessionID());
    window.location.href = `mainchat.html?id=${getSessionID()}`;
}

function contact_Link() {
    console.log(getSessionID());
    window.location.href = `friendlist.html?id=${getSessionID()}`;
}
