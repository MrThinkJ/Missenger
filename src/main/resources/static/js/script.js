"use strict";
const PORT = 8088;
const body = document.body;
const container = document.querySelector(".container");
const chatList = document.querySelector(".chat-list");
const chatParent = document.querySelector(".chat-history");
const chatHistory = document.querySelector(".chat-history ul");
const chatMessageForm = document.querySelector(".chat-message");
const messageInput = document.querySelector(".message-input");
const chatName = document.querySelector(".chat-name");
const chatImg = document.querySelector(".chat-img");
let requestId = null;
let callRequestId = null;
let isCalled = false;

let stompClient = null;
let nickname = null;
let password = null;
let selectedUser = null;

function init() {
  nickname = localStorage.getItem("nickname");
  password = localStorage.getItem("password");
  if (!nickname || !password) {
    window.location = "auth.html";
    return;
  }
  const socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
  stompClient.subscribe("/topic/public", onNewUserConnected);
  stompClient.subscribe(`/user/${nickname}/queue/callRequest`, onCallRequest);
  stompClient.send(
    "/app/user.addUser",
    {},
    JSON.stringify({ nickname: nickname, password: password, status: "ONLINE" })
  );
}

function onCallRequest(payload) {
  const message = JSON.parse(payload.body);
  const status = message.status;
  switch (status) {
    case "request":
      handleCallRequest(payload);
      break;
    case "accept":
      handleAcceptRequest(payload);
      break;
    case "deny":
      handleDenyRequest(payload);
      break;
    case "quit":
      handleQuit(payload);
      break;
  }
}

function handleCallRequest(payload) {
  const message = JSON.parse(payload.body);
  if (callRequestId) {
    stompClient.send(
      `/user/${message.sender}/queue/callRequest`,
      {},
      JSON.stringify({
        sender: nickname,
        receiver: message.sender,
        status: "deny",
      })
    );
    return;
  }
  callRequestId = message.sender;
  const callRequestDiv = document.createElement("div");
  callRequestDiv.classList.add("call-request");
  callRequestDiv.innerHTML = `
    <p>${message.sender}</p>
    <div class="btn">
      <button class="accept-btn">Accept</button>
      <button class="deny-btn">Deny</button>
    </div>
  `;
  container.classList.add("blur");
  body.appendChild(callRequestDiv);
  const acceptBtn = document.querySelector(".accept-btn");
  const denyBtn = document.querySelector(".deny-btn");
  acceptBtn.addEventListener("click", acceptCall);
  denyBtn.addEventListener("click", denyCall);
}

function handleAcceptRequest() {
  localStorage.setItem("type", "offer");
  localStorage.setItem("call", requestId);
  window.open(
    "https://" + window.location.hostname + ":" + PORT + "/videocall.html"
  );
}

function handleDenyRequest(payload) {
  requestId = null;
  const callDiv = document.querySelector(".call-request");
  if (callDiv) {
    document.body.removeChild(callDiv);
    container.classList.remove("blur");
  }
}

function handleQuit(payload) {
  const callDiv = document.querySelector(".call-request");
  if (callDiv) {
    document.body.removeChild(callDiv);
    container.classList.remove("blur");
  }
}

function makeCall() {
  const callDiv = document.createElement("div");
  callDiv.classList.add("call-request");
  callDiv.innerHTML = `
    <p>${selectedUser}</p>
    <div class="btn">
      <button class="quit-btn">quit</button>
    </div>
  `;
  document.body.appendChild(callDiv);
  stompClient.send(
    `/app/call`,
    {},
    JSON.stringify({
      sender: nickname,
      receiver: selectedUser,
      status: "request",
    })
  );
  requestId = selectedUser;
  container.classList.add("blur");
  document.querySelector(".quit-btn").addEventListener("click", quitCall);
}

function acceptCall() {
  stompClient.send(
    `/user/${callRequestId}/queue/callRequest`,
    {},
    JSON.stringify({
      sender: nickname,
      receiver: callRequestId,
      status: "accept",
    })
  );
  localStorage.setItem("type", "answer");
  localStorage.setItem("call", callRequestId);
  window.open(
    "https://" + window.location.hostname + ":" + PORT + "/videocall.html"
  );
}

function denyCall() {
  stompClient.send(
    `/user/${callRequestId}/queue/callRequest`,
    {},
    JSON.stringify({
      sender: nickname,
      receiver: payload.sender,
      status: "deny",
    })
  );
  const callDiv = document.querySelector(".call-request");
  if (callDiv) {
    document.body.removeChild(callDiv);
  }
  container.classList.remove("blur");
  callRequestId = null;
}

function quitCall() {
  const callDiv = document.querySelector(".call-request");
  if (callDiv) {
    document.body.removeChild(callDiv);
    document.body.classList.remove("blur");
  }
}
function onError() {
  console.error("Some error happened");
}

async function onMessageReceived(payload) {
  await findAndDisplayConnectedUser();
  const message = JSON.parse(payload.body);
  if (selectedUser === message.senderId) {
    displayMessage(message);
    chatParent.scrollTop = chatParent.scrollHeight;
  } else {
    const newMessageLi = document.querySelector(`#${message.senderId}`);
    const newMessageNoti = document.createElement("div");
    newMessageNoti.classList.add("new-message");
    newMessageLi.appendChild(newMessageNoti);
  }
}
async function onNewUserConnected() {
  await findAndDisplayConnectedUser();
}
async function findAndDisplayConnectedUser() {
  const connectedUserRes = await fetch("/users");
  let connectedUser = await connectedUserRes.json();
  connectedUser = connectedUser.filter((user) => user.nickname !== nickname);
  chatList.innerHTML = "";
  connectedUser.forEach((user) => {
    appendUser(user);
  });
}
function appendUser(user) {
  let userLi = document.createElement("li");
  userLi.classList.add("clearfix", "user-item");
  userLi.id = user.nickname;
  let userImage = document.createElement("img");
  userImage.src = "https://bootdey.com/img/Content/avatar/avatar2.png";
  userImage.alt = "avatar";
  let userDiv = document.createElement("div");
  userDiv.classList.add("about");
  userDiv.innerHTML = `
      <div class="name">${user.nickname}</div>
      <div class="status">
        <i class="fa fa-circle ${user.status.toLowerCase()}"></i> ${user.status}
      </div>
  `;
  userLi.appendChild(userImage);
  userLi.appendChild(userDiv);
  userLi.addEventListener("click", userClick, user);
  chatList.appendChild(userLi);
}

function userClick(event) {
  document
    .querySelectorAll(".user-item")
    .forEach((item) => item.classList.remove("active"));
  chatMessageForm.classList.remove("hidden");
  const callDiv = document.querySelector(".call-btn");
  callDiv.classList.remove("hidden");
  const callBtn = callDiv.querySelector("button");
  callBtn.addEventListener("click", makeCall);

  const curSelectedUser = event.currentTarget;
  curSelectedUser.classList.add("active");
  const newMessageNoti = curSelectedUser.getElementsByClassName("new-message");
  console.log(curSelectedUser);
  console.log(newMessageNoti);
  if (newMessageNoti.length > 0) {
    curSelectedUser.removeChild(curSelectedUser.lastElementChild);
  }
  selectedUser = curSelectedUser.getAttribute("id");
  chatName.innerHTML = selectedUser;
  fetchAndDisplayMessage().then();
}

async function fetchAndDisplayMessage() {
  const messageListRes = await fetch(`/message/${nickname}/${selectedUser}`);
  const messageList = await messageListRes.json();
  chatHistory.innerHTML = "";
  messageList.forEach((item) => displayMessage(item));
  chatParent.scrollTop = chatParent.scrollHeight;
}

function displayMessage(message) {
  let messageHTML;
  let messageLi = document.createElement("li");
  messageLi.classList.add("clearfix");
  if (message.senderId === nickname) {
    messageHTML = `
        <div class="message-data text-right">
            <span class="message-data-time">${formatTimestamp(
              message.timestamp
            )}</span>
        </div>
        <div class="message my-message float-right">
            ${message.content}
        </div>
      `;
  } else {
    messageHTML = `
        <div class="message-data">
            <span class="message-data-time">${formatTimestamp(
              message.timestamp
            )}</span>
        </div>
        <div class="message other-message">
            ${message.content}
        </div>
      `;
  }
  messageLi.innerHTML = messageHTML;
  chatHistory.append(messageLi);
}

function sendMessage(event) {
  event.preventDefault();
  const messageContent = messageInput.value.trim();
  const image = document.querySelector("#image-file").files[0];
  let formData = new FormData();
  formData.append("chatId", `${nickname}_${selectedUser}`);
  formData.append("content", messageContent);
  formData.append("senderId", nickname);
  formData.append("recipientId", selectedUser);
  formData.append("timestamp", Date.now().toString());
  formData.append("image", image);
  axios.post('/chat', formData,{
    headers:{
      "Content-Type": "multipart/form-data"
    }
  }).then(res=>{
    console.log(res);
    const messageObject = JSON.parse(res.data);
    displayMessage(messageObject);
    chatParent.scrollTop = chatParent.scrollHeight;
    messageInput.value = "";
  }).catch(err=>console.error("Upload error: "+err));

}

function formatTimestamp(timestamp) {
  const date = new Date(timestamp);

  const hours = date.getHours().toString().padStart(2, "0"); // Ensure two digits
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0"); // Months are 0-indexed
  const year = date.getFullYear();

  return `${hours}:${minutes} ${day}/${month}/${year}`;
}

window.onload = init;
chatMessageForm.addEventListener("submit", sendMessage, true);
