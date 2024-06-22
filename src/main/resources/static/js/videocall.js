let peerConnection = null;
let dataChannel = null;
const nickname = localStorage.getItem("nickname");
const callId = localStorage.getItem("call");
let stompClient = null;
let isReceiveOffer = false;
const PORT = 8088;
const constraints = {
  video: true,
  audio: true,
};
const configuration = {
  iceServers: [{ urls: "stun:stun2.1.google.com:19302" }],
};
const type = localStorage.getItem("type");
let localStream = null;
const selfView = document.getElementById("selfView");
const remoteView = document.getElementById("remoteView");

function init() {
  navigator.mediaDevices
      .getUserMedia(constraints)
      .then((stream) => {
        localStream = stream;
        selfView.srcObject = new MediaStream([localStream.getTracks()[1]]);
        setUpPeerConnection();
        const socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
      })
      .catch((err) => {
        console.log("Error when stream: " + err);
        alert(err);
      });
}

function setUpPeerConnection() {
  peerConnection = new RTCPeerConnection(configuration);
  peerConnection.onicecandidate = (event) => {
    if (event.candidate) {
      send(JSON.stringify({
          type: "ice",
          receiver: callId,
          data: event.candidate,
      }));
    }
  };
  dataChannel = peerConnection.createDataChannel("dataChannel");
  dataChannel.onopen = (event) => {
    console.log("Data channel is opened");
  };
  dataChannel.onmessage = (event) => {
    console.log("New message: " + event.data);
  };
  dataChannel.onclose = (event) => {
    console.log("Data channel is closed");
  };
  dataChannel.onerror = (error) => {
    console.log("Data channel has error: " + error);
  };

  peerConnection.ondatachannel = (event) => {
    dataChannel = event.channel;
    dataChannel.onmessage = (event) => {
      console.log("On datachannel message: " + event.data);
    };
  };
  localStream.getTracks().forEach((track) => {
    peerConnection.addTrack(track, localStream);
  });

  peerConnection.addEventListener("track", async (event) => {
    const [remoteStream] = event.streams;
    console.log("Before add");
    remoteView.srcObject = remoteStream;
    console.log("After add")
  });
}

function onConnected() {
  stompClient.subscribe(`/user/${nickname}/queue/call`, onMessageReceived);
  loopWhenReady();
}

function loopWhenReady(){
    if (!isReceiveOffer){
        createReadyMessage();
        setTimeout(loopWhenReady, 1000);
    }
}

function createReadyMessage(){
    if (type === "answer"){
        send(JSON.stringify({
            type: "ready",
            sender: nickname,
            receiver: callId
        }))
    }
}

function onError(err){
    console.error(err);
}

function onMessageReceived(payload) {
  const message = JSON.parse(payload.body);
  const type = message.type;
  switch (type) {
    case "ready":
      createOffer(message);
      break
    case "offer":
      handleOffer(message);
      break;
    case "answer":
      handleAnswer(message);
      break;
    case "ice":
      handleIce(message);
      break;
    case "logout":
      handleLogout(message);
      break;
  }
}

function createOffer() {
  peerConnection
    .createOffer()
    .then((sdp) => {
      peerConnection.setLocalDescription(sdp);
      console.log("Create offer for: " + callId);
      send(
        JSON.stringify({
          type: "offer",
          sender: nickname,
          receiver: callId,
          data: sdp,
        })
      );
    })
    .catch((err) => console.error(err));
}

function handleOffer(signal) {
    isReceiveOffer = true;
  peerConnection
    .setRemoteDescription(new RTCSessionDescription(signal.data))
    .then(() => {
      peerConnection
        .createAnswer()
        .then((sdp) => {
          peerConnection.setLocalDescription(sdp);
          send(
            JSON.stringify({
              type: "answer",
              sender: nickname,
              receiver: callId,
              data: sdp,
            })
          );
        })
        .catch((err) => console.error(err));
    });
}

function handleAnswer(signal) {
  if (signal.data) {
    peerConnection
      .setRemoteDescription(new RTCSessionDescription(signal.data))
      .then(() =>
        console.log(
          "Setting remote description by answer from: " + signal.sender
        )
      );
  }
}

function handleIce(signal) {
  if (signal.data) {
    console.log("Adding ICE");
    peerConnection
      .addIceCandidate(new RTCIceCandidate(signal.data))
      .catch((err) => console.error(err));
      remoteView.srcObject = remoteStream;
  }
}

function handleLogout(signal) {
  console.log(signal.sender + " logout!");
}

function send(message) {
  stompClient.send(`/app/callConnect`, {}, message);
}

window.onload = init;
