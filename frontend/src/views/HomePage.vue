<template>
  <div class="home-container">
    <!-- Navbar -->
    <nav class="navbar">
      <h1 class="logo">QuizChat</h1>
      <div class="nav-links">
        <router-link to="/">Home</router-link>
        <router-link to="/login">Login</router-link>
        <router-link to="/register">Register</router-link>
        <router-link to="/dashboard">Dashboard</router-link>
        <router-link to="/quiz">Quiz</router-link>
        <router-link to="/result">Results</router-link>
      </div>
    </nav>

    <!-- Hero Section with Background Image -->
    <div class="background">
      <div class="overlay">
        <h2>Welcome to QuizChat</h2>
        <p>Test your knowledge and challenge your friends!</p>
        <button @click="goToRegister" class="btn">Get Started</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    goToRegister() {
      this.$router.push("/register");
    }
  }
};
</script>

<style scoped>
/* Navbar */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: #007bff;
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1000;
}

.nav-links {
  display: flex;
  gap: 15px; /* Add spacing between links */
  padding-right: 20px; /* Ensure space at the end */
}

.nav-links a {
  color: white;
  text-decoration: none;
  font-size: 16px;
  white-space: nowrap; /* Prevent text from breaking */
  padding: 8px 12px; /* Increase padding */
}

.nav-links a:hover {
  text-decoration: underline;
}

/* Background Image */
.background {
  background: url("@/assets/study-image.jpg") no-repeat center center/cover;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  position: relative;
}

.overlay {
  background: rgba(0, 0, 0, 0.5);
  padding: 20px;
  border-radius: 10px;
  color: white;
}

.btn {
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.btn:hover {
  background-color: #0056b3;
}
</style>