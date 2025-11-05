# 🍳 Ingredia: The AI Recipe Generator

Ingredia is a smart, full-stack web application that generates unique recipes from the ingredients you have on hand. It features a smart shopping list, user accounts, and social login, all powered by a Java Spring Boot backend and a modern AI.

## ✨ Features

* **AI Recipe Generation:** Uses an LLM to generate recipes, including instructions and macros, from a list of ingredients.
* **User Authentication:** Full login/registration system built with Spring Security and JWTs.
* **Social Login:** Securely log in using your Google or Facebook account (OAuth2).
* **Smart Shopping List:**
    * Automatically parses ingredients from recipes and adds them to your personal list.
    * Manually add or remove items.
    * Check off items as you shop.
* **Email Your List:** Send your unchecked shopping list to your email with a single click (powered by Spring Mail).

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot
* **Security:** Spring Security, JWT (JSON Web Tokens), OAuth2
* **Database:** Spring Data JPA, H2 (In-Memory)
* **Frontend:** Plain HTML5, CSS3, and JavaScript (Fetch API)
* **APIs:** Google Gemini (or OpenAI), Google/Facebook OAuth, Spring Mail

## 🚀 How to Run

### 1. Prerequisites
* Java 17+
* Maven
* An LLM API Key (e.g., Google Gemini)
* Google & Facebook OAuth2 credentials
* A Gmail "App Password" for the email service

### 2. Set Environment Variables
This project reads all secrets and keys from environment variables. Set the following in your IDE's run configuration:

```bash
LLM_API_KEY=your-ai-key
JWT_SECRET_KEY=your-jwt-secret
SPRING_MAIL_PASSWORD=your-gmail-app-password
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
FACEBOOK_APP_ID=your-facebook-app-id
FACEBOOK_APP_SECRET=your-facebook-app-secret
