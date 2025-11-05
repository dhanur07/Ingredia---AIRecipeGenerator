
(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (token) {
    localStorage.setItem('jwtToken', token);
    window.history.replaceState({}, document.title, window.location.pathname);
}
})();
    const form = document.getElementById('recipeForm');
    const generateBtn = document.getElementById('generateBtn');
    const buttonText = document.getElementById('buttonText');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const errorAlert = document.getElementById('errorAlert');
    const errorMessage = document.getElementById('errorMessage');
    const placeholder = document.getElementById('placeholder');
    const recipeContent = document.getElementById('recipeContent');

    form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const ingredientsInput = document.getElementById('ingredients').value.trim();
    const cuisine = document.getElementById('cuisine').value;
    const mealType = document.getElementById('mealType').value;

    const ingredientsArray = ingredientsInput
    .split(',')
    .map(ing => ing.trim())
    .filter(Boolean);

    if (ingredientsArray.length === 0) {
    showError('Please enter at least one ingredient.');
    return;
}

    hideError();
    placeholder.classList.add('hidden');
    recipeContent.classList.add('hidden');
    recipeContent.innerHTML = '';
    generateBtn.disabled = true;
    buttonText.innerHTML = '<div class="spinner"></div> Generating...';
    loadingIndicator.classList.remove('hidden');

    const requestBody = {
    ingredients: ingredientsArray,
    cuisineType: cuisine,
    mealType: mealType
};

    try {
    const response = await fetch('http://localhost:8080/api/v1/recipes/generate', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify(requestBody)
});


    const rawResponse = await response.text();
    const cleanResponse = rawResponse.trim().replace(/%$/, '');
    const data = JSON.parse(cleanResponse);


    if (!response.ok) {
    throw new Error(data.error || `Server error: ${response.status}`);
}

    if (data.error) {
    showError(data.error);
    placeholder.classList.remove('hidden');
} else {
    displayRecipe(data);
}

} catch (err) {
    console.error("Error fetching or parsing:", err);
    showError(err.message || 'Failed to connect. Is the backend server running?');
    placeholder.classList.remove('hidden');
} finally {
    generateBtn.disabled = false;
    buttonText.innerHTML = 'Generate Recipe';
    loadingIndicator.classList.add('hidden');
}
});

    function displayRecipe(recipe) {

    const safeIngredients = recipe.ingredients || [];
    const ingredientsHtml = `
            <h3 class="section-title">
                <svg xmlns="http://www.w3.org/2000/svg" ...>...</svg>
                Ingredients
            </h3>
            <div class="ingredients-box">
                <ul class="ingredients-list">
                    ${safeIngredients.map((ingredient, index) => `
                        <li class="ingredient-item">
                            <span class="ingredient-number">${index + 1}</span>
                            <span>${ingredient.display}</span>
                        </li>
                    `).join('')}
                </ul>
            </div>
            <button class="button" style="margin-top: 1.5rem;" onclick="addToList(event)">
                Add to Shopping List
            </button>
        `;

    const nutritionHtml = recipe.macros ? `
            <div>
                <h3 class="section-title">Nutrition (per serving)</h3>
                <div class="nutrition-grid">
                    <div class="nutrition-card calories">
                        <div class="nutrition-label">Calories</div>
                        <div class="nutrition-value">${recipe.macros.calories}</div>
                    </div>
                    <div class="nutrition-card protein">
                        <div class="nutrition-label">Protein</div>
                        <div class="nutrition-value">${recipe.macros.proteinGrams}g</div>
                    </div>
                    <div class="nutrition-card fat">
                        <div class="nutrition-label">Fat</div>
                        <div class="nutrition-value">${recipe.macros.fatGrams}g</div>
                    </div>
                    <div class="nutrition-card carbs">
                        <div class="nutrition-label">Carbs</div>
                        <div class="nutrition-value">${recipe.macros.carbGrams}g</div>
                    </div>
                </div>
            </div>` : '';

    const safeInstructions = recipe.instructions || [];
    const instructionsHtml = `
            <div>
                <h3 class="section-title">
                    <svg xmlns="http://www.w3.org/2000/svg" ...>...</svg>
                    Instructions
                </h3>
                <div>
                    ${safeInstructions.map((instruction, index) => `
                        <div class="instruction-item">
                            <div class="instruction-number">${index + 1}</div>
                            <div class="instruction-text">${instruction}</div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;

    const html = `
            <div class="recipe-header">
                <h2 class="recipe-title">${recipe.title || 'Untitled Recipe'}</h2>
                <span class="badge">AI Generated</span>
            </div>
            <p class="recipe-description">${recipe.description || 'No description provided.'}</p>
            <div class="separator"></div>
            <div class="time-info">
                 <div class="time-value">${recipe.prepTimeMinutes || '?'} minutes</div>
                 <div class="time-value">${recipe.cookTimeMinutes || '?'} minutes</div>
            </div>
            <div class="separator"></div>
            ${nutritionHtml}
            <div class="separator"></div>
            ${ingredientsHtml}
            <div class="separator"></div>
            ${instructionsHtml}
        `;

    recipeContent.innerHTML = html;
    recipeContent.classList.remove('hidden');
    window.currentRecipeIngredients = safeIngredients;
}

    async function addToList(event) {
    event.target.disabled = true;
    event.target.innerHTML = '<div class="spinner"></div> Adding...';

    const token = localStorage.getItem('jwtToken');

    if (!token) {
    alert("Please log in to save ingredients.");
    window.location.href = 'login.html';
    return;
}

    const ingredients = window.currentRecipeIngredients;
    if (!ingredients) {
    alert("Error: Could not find recipe ingredients.");
    return;
}

    const itemsToSave = ingredients
    .map(ing => ing.item)
    .filter(Boolean);

    const body = { ingredients: itemsToSave };

    try {
    const response = await fetch('/api/v1/list/add-from-recipe', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + token
},
    body: JSON.stringify(body)
});

    if (!response.ok) {
    throw new Error("Failed to add items.");
}
    event.target.innerHTML = '✅ Added to List!';
} catch (error) {
    alert(error.message);
    event.target.disabled = false;
    event.target.innerHTML = 'Add to Shopping List';
}
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

    function showError(message) {
    errorMessage.textContent = message;
    errorAlert.classList.remove('hidden');
    setTimeout(() => {
    hideError();
}, 5000);
}

    function hideError() {
    errorAlert.classList.add('hidden');
}

