"use strict";

const usernamePage = document.querySelector("#username-page");
const container = document.querySelector(".container");
const chatList = document.querySelector(".chat-list");
const chatParent =  document.querySelector(".chat-history");
const chatHistory = document.querySelector(".chat-history ul");
const chatMessageForm = document.querySelector(".chat-message");
const messageInput = document.querySelector(".message-input");
const chatName = document.querySelector(".chat-name");
const chatImg = document.querySelector(".chat-img");

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUser = null;

function connect(event) {
  nickname = document.querySelector("#nickname").value.trim();
  fullname = document.querySelector("#fullname").value.trim();
  if (nickname && fullname) {
    usernamePage.classList.add("hidden");
    container.classList.remove("hidden");
    const socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
  }
  event.preventDefault();
}

function onConnected() {
  stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
  stompClient.subscribe("/topic/public", onNewUserConnected);
  stompClient.send(
    "/app/user.addUser",
    {},
    JSON.stringify({ fullName: fullname, nickname: nickname, status: "ONLINE" })
  );
}

function onError() {
  console.error("Some error happened");
}
async function onMessageReceived(payload) {
  await findAndDisplayConnectedUser();
  const message = JSON.parse(payload.body);
  if (selectedUser === message.senderId){
    displayMessage(message);
    chatParent.scrollTop = chatParent.scrollHeight;
  } else{
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
  connectedUser = connectedUser.filter(user => user.nickname !== nickname)
  chatList.innerHTML = '';
  connectedUser.forEach(user=>{
    appendUser(user);
  })
}
function appendUser(user){
  let userLi = document.createElement("li");
  userLi.classList.add("clearfix","user-item");
  userLi.id = user.nickname;
  let userImage = document.createElement("img");
  userImage.src = "https://bootdey.com/img/Content/avatar/avatar2.png"
  userImage.alt = "avatar"
  let userDiv = document.createElement("div");
  userDiv.classList.add("about");
  userDiv.innerHTML = `
      <div class="name">${user.nickname}</div>
      <div class="status">
        <i class="fa fa-circle ${user.status.toLowerCase()}"></i> ${user.status}
      </div>
  `
  userLi.appendChild(userImage);
  userLi.appendChild(userDiv);
  userLi.addEventListener("click", userClick, user);
  chatList.appendChild(userLi);
}

function userClick(event){
  document.querySelectorAll(".user-item").forEach(item=> item.classList.remove("active"));
  chatMessageForm.classList.remove("hidden");
  const curSelectedUser = event.currentTarget;
  curSelectedUser.classList.add("active");
  const newMessageNoti = curSelectedUser.getElementsByClassName("new-message");
  console.log(curSelectedUser)
  console.log(newMessageNoti)
  if (newMessageNoti.length > 0){
    curSelectedUser.removeChild(curSelectedUser.lastElementChild);
  }
  selectedUser = curSelectedUser.getAttribute("id");
  chatName.innerHTML = selectedUser;
  fetchAndDisplayMessage().then();
}

async function fetchAndDisplayMessage(){
  const messageListRes = await fetch(`/message/${nickname}/${selectedUser}`)
  const messageList = await messageListRes.json();
  chatHistory.innerHTML = '';
  messageList.forEach( item=>displayMessage(item));
  chatParent.scrollTop = chatParent.scrollHeight;
}

function displayMessage(message){
  let messageHTML = ''
  let messageLi = document.createElement("li");
  messageLi.classList.add("clearfix");
  if (message.senderId === nickname){
    messageHTML = `
        <div class="message-data text-right">
            <span class="message-data-time">${formatTimestamp(message.timestamp)}</span>
        </div>
        <div class="message my-message float-right">
            ${message.content}
        </div>
      `
  } else{
    messageHTML = `
        <div class="message-data">
            <span class="message-data-time">${formatTimestamp(message.timestamp)}</span>
        </div>
        <div class="message other-message">
            ${message.content}
        </div>
      `
  }
  messageLi.innerHTML = messageHTML;
  chatHistory.append(messageLi);
}

function sendMessage(event){
  event.preventDefault();
  const messageContent = messageInput.value.trim();
  const messageObject = {
    chatId: `${nickname}_${selectedUser}`,
    content: messageContent,
    senderId: nickname,
    recipientId: selectedUser,
    timestamp: Date.now()
  }
  const message = JSON.stringify(messageObject)
  stompClient.send("/app/chat", {}, message)
  displayMessage(messageObject);
  chatParent.scrollTop = chatParent.scrollHeight;
  messageInput.value = ""
}

function formatTimestamp(timestamp) {
  const date = new Date(timestamp);

  const hours = date.getHours().toString().padStart(2, '0'); // Ensure two digits
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are 0-indexed
  const year = date.getFullYear();

  return `${hours}:${minutes} ${day}/${month}/${year}`;
}

usernamePage.addEventListener("submit", connect, true);
chatMessageForm.addEventListener("submit", sendMessage, true);

