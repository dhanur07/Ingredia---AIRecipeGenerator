
    const listContainer = document.getElementById('listContainer');
    const listLoading = document.getElementById('listLoading');
    const listPlaceholder = document.getElementById('listPlaceholder');
    const clearBtn = document.getElementById('clearBtn');
    const emailBtn = document.getElementById('emailBtn');

    const addItemForm = document.getElementById('addItemForm');
    const itemNameInput = document.getElementById('itemName');
    const addItemBtn = document.getElementById('addItemBtn');

    const token = localStorage.getItem('jwtToken');

    async function loadList() {
    if (!token) {
    window.location.href = 'login.html';
    return;
}

    showLoading(true);
    hidePlaceholder();

    try {
    const response = await fetch('/api/v1/list', {
    method: 'GET',
    headers: {
    'Authorization': 'Bearer ' + token
}
});

    if (response.status === 401 || response.status === 403) {
    // Token is invalid or expired
    window.location.href = 'login.html';
    return;
}

    if (!response.ok) {
    throw new Error('Failed to fetch list.');
}

    const items = await response.json();
    displayList(items);

} catch (error) {
    console.error(error);
    showPlaceholder('Error loading list. Please try again.');
} finally {
    showLoading(false);
}
}
    async function handleSendEmail() {
    emailBtn.disabled = true;
    emailBtn.innerHTML = '<div class="spinner"></div> Sending...';

    try {
    const response = await fetch('/api/v1/list/send-email', {
    method: 'POST',
    headers: {
    'Authorization': 'Bearer ' + token
}
});

    if (!response.ok) {
    throw new Error('Failed to send email.');
}

    // Success!
    emailBtn.innerHTML = '✅ Sent!';
    setTimeout(() => {
    emailBtn.innerHTML = 'Email My List';
}, 3000);

} catch (error) {
    console.error('Failed to send email', error);
    alert(error.message);
    emailBtn.innerHTML = 'Email My List';
} finally {
    emailBtn.disabled = false;
}
}
    function displayList(items) {
    if (items.length === 0) {
    showPlaceholder();
    listContainer.innerHTML = '';
    return;
}

    hidePlaceholder();

    items.sort((a, b) => a.checked - b.checked);

    listContainer.innerHTML = items.map(item => {
    const isChecked = item.checked;
    const checkedAttr = isChecked ? 'checked' : '';
    const textClass = isChecked ? 'item-text completed' : 'item-text';

    return `
                <div class="list-item" id="list-item-div-${item.id}">
                    <input type="checkbox" id="item-${item.id}" onchange="toggleItem(${item.id}, this.checked)" ${checkedAttr}>
                    <label for="item-${item.id}" class="${textClass}">${item.itemName}</label>
                </div>
            `;
}).join('');
}

    function appendItemToDOM(item) {
    hidePlaceholder();

    const isChecked = item.checked;
    const checkedAttr = isChecked ? 'checked' : '';
    const textClass = isChecked ? 'item-text completed' : 'item-text';

    const itemHtml = `
            <div class="list-item" id="list-item-div-${item.id}">
                <input type="checkbox" id="item-${item.id}" onchange="toggleItem(${item.id}, this.checked)" ${checkedAttr}>
                <label for="item-${item.id}" class="${textClass}">${item.itemName}</label>
            </div>
        `;

    listContainer.insertAdjacentHTML('afterbegin', itemHtml);
}

    function checkLogin() {
    const token = localStorage.getItem('jwtToken');
    const loginLink = document.getElementById('login-link');

    if (loginLink && token) {
    loginLink.textContent = 'Log Out';
    loginLink.href = "#";
    loginLink.onclick = () => {
    localStorage.removeItem('jwtToken');
    window.location.reload();
};
}
}

    window.addEventListener('load', checkLogin);

    async function toggleItem(id, isChecked) {
    const label = document.querySelector(`label[for="item-${id}"]`);
    if (isChecked) {
    label.classList.add('completed');
} else {
    label.classList.remove('completed');
}

    try {
    await fetch(`/api/v1/list/item/${id}`, {
    method: 'PUT',
    headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
},
    body: JSON.stringify({ isChecked: isChecked })
});
} catch (error) {
    console.error('Failed to update item', error);
    if (isChecked) {
    label.classList.remove('completed');
} else {
    label.classList.add('completed');
}
    alert('Error saving change. Please try again.');
}
}

    async function clearCompleted() {
    clearBtn.disabled = true;

    try {
    await fetch('/api/v1/list/clear-completed', {
    method: 'DELETE',
    headers: {
    'Authorization': 'Bearer ' + token
}
});

    await loadList();

} catch (error) {
    console.error('Failed to clear items', error);
    alert('Error clearing items. Please try again.');
} finally {
    clearBtn.disabled = false;
}
}

    async function handleAddItem(e) {
    e.preventDefault();

    const itemName = itemNameInput.value.trim();
    if (!itemName) {
    return;
}

    addItemBtn.disabled = true;
    addItemBtn.innerHTML = '<div class="spinner"></div>';

    try {
    const response = await fetch('/api/v1/list/item', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
},
    body: JSON.stringify({ itemName: itemName })
});

    if (!response.ok) {
    throw new Error('Failed to add item. It might already be on your list.');
}
    const newItem = await response.json();
    appendItemToDOM(newItem);
    itemNameInput.value = '';

} catch (error) {
    console.error('Failed to add item', error);
    alert(error.message);
} finally {
    addItemBtn.disabled = false;
    addItemBtn.innerHTML = 'Add Item';
}
}

    window.addEventListener('load', loadList);
    clearBtn.addEventListener('click', clearCompleted);
    addItemForm.addEventListener('submit', handleAddItem);
    emailBtn.addEventListener('click', handleSendEmail);
    function showLoading(isLoading) {
    if (isLoading) {
    listLoading.classList.remove('hidden');
} else {
    listLoading.classList.add('hidden');
}
}

    function showPlaceholder(message = "Your shopping list is empty.") {
    listPlaceholder.querySelector('p').textContent = message;
    listPlaceholder.classList.remove('hidden');
}

    function hidePlaceholder() {
    listPlaceholder.classList.add('hidden');
}

