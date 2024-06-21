const changeLink = document.querySelector(".change-link");
const userForm = document.getElementById("usernameForm");
let registerButton;
loginButton = document.getElementById("login-btn");
loginButton.addEventListener("click", login);
function changeForm() {
  const title = document.getElementById("title");
  if (title.innerHTML === "Login") {
    title.innerHTML = "Register";
    userForm.innerHTML = "";
    const formHTML = `
    <label for="realname">Real Name:</label>
    <input type="text" id="realName" name="realname" required />
    <label for="nickname">Nickname:</label>
    <input type="text" id="nickname" name="nickname" required />
    <label for="password">Password:</label>
    <input type="text" id="password" name="password" required />
    <button id="register-btn" type="submit">Register</button>
  `;
    userForm.innerHTML = formHTML;
    registerButton = document.getElementById("register-btn");
    registerButton.addEventListener("click", register);
    changeLink.innerHTML = "Login ?";
  } else {
    title.innerHTML = "Login";
    userForm.innerHTML = "";
    const formHTML = `
    <label for="nickname">Nickname:</label>
    <input type="text" id="nickname" name="nickname" required />
    <label for="password">Password:</label>
    <input type="text" id="password" name="password" required />
    <button id="login-btn" type="submit">Enter Chatroom</button>
  `;
    userForm.innerHTML = formHTML;
    loginButton = document.getElementById("login-btn");
    loginButton.addEventListener("click", login);
    changeLink.innerHTML = "Register ?";
  }
}

function register(event) {
  event.preventDefault();
  const realName = document.querySelector("#realName").value.trim();
  const nickName = document.querySelector("#nickname").value.trim();
  const password = document.querySelector("#password").value.trim();
  axios
    .post("/register", {
      fullName: realName,
      nickname: nickName,
      password: password,
    })
    .then((res) => {
      alert("Register successfully");
      changeForm();
    })
    .catch((err) => {
      alert("Accout with this nickname already exist!");
    });
}
function login(event) {
  event.preventDefault();
  const nickName = document.querySelector("#nickname").value.trim();
  const password = document.querySelector("#password").value.trim();
  axios
    .post("/login", {
      nickname: nickName,
      password: password,
    })
    .then((res) => {
      const { nickname, password } = res.data;
      localStorage.setItem("nickname", nickname);
      localStorage.setItem("password", password);
      window.location = "index.html";
    })
    .catch((err) => {
      alert("Wrong username or password");
    });
}
changeLink.addEventListener("click", changeForm);
