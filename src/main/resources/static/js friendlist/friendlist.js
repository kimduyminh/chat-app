document.addEventListener("DOMContentLoaded", function () {
    const friendList = document.getElementById("friendList");
    const friendRequestsList = document.getElementById("friendRequestsList");
    const searchInput = document.getElementById("searchInput");
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
    searchInput.addEventListener("input", function () {
        const query = searchInput.value.trim();
        if (query) {
            findFriend(query);
        } else {
            listFriends();
        }
    });

    // Event listener for add friend button
    addFriendBtn.addEventListener("click", function () {
        const friendName = searchInput.value.trim();
        if (friendName) {
            sendFriendRequest(friendName);
        }
    });

    // Initial load
    listFriends();
    loadFriendRequests();
});

function getSessionIdFromUrl() {
    const queryParams = new URLSearchParams(window.location.search);
    return queryParams.get('id');
}

function getSessionID() {
    const sessionSuperId = getSessionIdFromUrl();
    return sessionSuperId;
}

function chat_Link() {
    console.log(getSessionID());
    window.location.href = `mainchat.html?id=${getSessionID()}`;
}

function contact_Link() {
    console.log(getSessionID());
    window.location.href = `friendlist.html?id=${getSessionID()}`;
}
