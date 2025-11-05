
    const form = document.getElementById('loginForm');
    const loginBtn = document.getElementById('loginBtn');
    const buttonText = document.getElementById('buttonText');
    const errorAlert = document.getElementById('errorAlert');
    const errorMessage = document.getElementById('errorMessage');

    form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    if (!email || !password) {
    showError('Please enter both email and password.');
    return;
}

    hideError();
    loginBtn.disabled = true;
    buttonText.innerHTML = '<div class="spinner"></div> Signing In...';

    try {
    const requestBody = {
    username: email,
    password: password
};

    const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify(requestBody)
});

    if (!response.ok) {
    let errorMsg = `Login failed: ${response.statusText}`;
    try {
    const errorData = await response.json();
    errorMsg = errorData.message || 'Invalid username or password.';
} catch (e) { /* Ignore if error response isn't JSON */ }
    throw new Error(errorMsg);
}
    const data = await response.json();
    localStorage.setItem('jwtToken', data.token);
    window.location.href = 'index.html';

} catch (error) {
    showError(error.message);
} finally {
    loginBtn.disabled = false;
    buttonText.innerHTML = 'Sign In';
}
});

    function showError(message) {
    errorMessage.textContent = message;
    errorAlert.classList.remove('hidden');
}

    function hideError() {
    errorAlert.classList.add('hidden');
}

    function loginWithGoogle() {
    console.log('Login with Google');
    window.location.href = '/oauth2/authorization/google';
}

    function loginWithFacebook() {
    console.log('Login with Facebook');
    window.location.href = '/oauth2/authorization/facebook';
}

