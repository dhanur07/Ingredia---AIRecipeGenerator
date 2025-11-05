    const form = document.getElementById('registerForm');
    const registerBtn = document.getElementById('registerBtn');
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

    if (password.length < 6) {
    showError('Password must be at least 6 characters long.');
    return;
}
    hideError();
    registerBtn.disabled = true;
    buttonText.innerHTML = '<div class="spinner"></div> Creating Account...';

    try {
    const requestBody = {
    username: email,
    password: password
};

    const response = await fetch('/api/auth/register', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify(requestBody)
});

    if (!response.ok) {
    let errorMsg = `Registration failed: ${response.statusText}`;
    try {
    // Check if user already exists
    if (response.status === 400) {
    errorMsg = 'An account with this email already exists.';
}
} catch (e) { /* Ignore */ }
    throw new Error(errorMsg);
}

    const data = await response.json();

    localStorage.setItem('jwtToken', data.token);
    window.location.href = 'index.html'; // Or '/shopping-list.html'

} catch (error) {
    showError(error.message);
} finally {
    registerBtn.disabled = false;
    buttonText.innerHTML = 'Create Account';
}
});

    function showError(message) {
    errorMessage.textContent = message;
    errorAlert.classList.remove('hidden');
}

    function hideError() {
    errorAlert.classList.add('hidden');
}
