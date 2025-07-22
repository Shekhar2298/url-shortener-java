// /static/script.js

// Anonymous URL Shorten
const shortenForm = document.getElementById("shorten-form");
if (shortenForm) {
    shortenForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const longUrl = document.getElementById("longUrl").value;
        const res = await fetch("/shorten", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ longUrl })
        });
        const data = await res.text();
        document.getElementById("shortUrl").innerText = `Short URL: ${data}`;
    });
}

// Register
const registerForm = document.getElementById("register-form");
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("regUsername").value;
        const password = document.getElementById("regPassword").value;

        const res = await fetch("/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const text = await res.text();
        document.getElementById("registerStatus").innerText = text;
    });
}

// Login
const loginForm = document.getElementById("login-form");
if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        const res = await fetch("/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        const text = await res.text();
        if (text === "Login successful.") {
            window.location.href = "dashboard.html";
        } else {
            document.getElementById("loginStatus").innerText = text;
        }
    });
}

// Custom URL Shorten
const customForm = document.getElementById("custom-shorten-form");
if (customForm) {
    customForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const longUrl = document.getElementById("customLongUrl").value;
        const shortUrl = document.getElementById("customShortUrl").value;

        const res = await fetch("/shorten", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ longUrl, shortUrl })
        });

        const data = await res.text();
        document.getElementById("customResult").innerText = data;
    });
}

// Logout (clears session if needed on backend)
const logoutBtn = document.getElementById("logoutBtn");
if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
        await fetch("/logout");
        window.location.href = "index.html";
    });
}

const output = document.getElementById('output');

// After successful short URL generation
output.style.display = 'block';
document.getElementById('shorten-form').addEventListener('submit', async function (e) {
    e.preventDefault();

    const longUrl = document.getElementById('longUrl').value;
    const messageEl = document.getElementById('message');
    const shortUrlEl = document.getElementById('shortUrl');
    const copyBtn = document.getElementById('copyBtn');
    const output = document.getElementById('output');

    try {
        const response = await fetch('/shorten', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ longUrl })
        });

        const data = await response.json();

        if (response.ok) {
            const fullShortUrl = `${window.location.origin}/${data.shortUrl}`;

            // Update and show the short URL
            shortUrlEl.href = fullShortUrl;
            shortUrlEl.textContent = `🔗 ${fullShortUrl}`;

            // Show the output block
            output.style.display = 'block';

            // Set copy functionality
            copyBtn.onclick = function () {
                navigator.clipboard.writeText(fullShortUrl).then(() => {
                    alert('Short URL copied to clipboard!');
                });
            };

            messageEl.textContent = '✅ Short URL created successfully!';
            messageEl.className = 'message success';
            messageEl.style.display = 'block';
        } else {
            messageEl.textContent = `❌ ${data.error || 'Something went wrong!'}`;
            messageEl.className = 'message error';
            messageEl.style.display = 'block';
            output.style.display = 'none';
        }
    } catch (err) {
        console.error(err);
        messageEl.textContent = '⚠️ Network or server error.';
        messageEl.className = 'message error';
        messageEl.style.display = 'block';
        output.style.display = 'none';
    }
});

