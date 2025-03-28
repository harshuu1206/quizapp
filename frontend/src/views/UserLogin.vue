<template>
  <div class="login-container">
    <div class="login-box">
      <h2>Login</h2>
      <form @submit.prevent="login">
        <div class="input-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="email" placeholder="Enter email" required />
        </div>
        <div class="input-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" placeholder="Enter password" required />
        </div>
        <button type="submit" class="btn">Login</button>

        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </form>

      <p class="register-link">
        Don't have an account? <router-link to="/register">Register</router-link>
      </p>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      email: "",
      password: "",
      errorMessage: "",
    };
  },
  created() {
    // Axios interceptor: Automatically adds token to requests
    axios.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem("token");
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );
  },
  methods: {
    async login() {
      console.log("🛠️ Sending Login Request:", this.email, this.password);

      try {
        const response = await axios.post("http://localhost:8090/api/auth/login", {
          email: this.email,
          password: this.password,
        });

        console.log("✅ Server Response:", response.data);

        if (response.data.token) {
          localStorage.setItem("token", response.data.token.trim());
          localStorage.setItem("userId", response.data.userId); // ✅ Store userId

          console.log("✅ Token saved in localStorage:", localStorage.getItem("token"));
          console.log("✅ User ID saved in localStorage:", localStorage.getItem("userId"));

          this.$router.push("/dashboard");
        } else {
          this.errorMessage = response.data.error || "Invalid response from server";
        }
      } catch (error) {
        console.error("❌ Login Error:", error);
        this.errorMessage = error.response?.data?.error || "Login failed. Please check your credentials.";
      }
    },
  },
};
</script>

<style scoped>
/* Background */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #007bff, #00d4ff);
}

/* Login Box */
.login-box {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

/* Form Inputs */
.input-group {
  margin-bottom: 15px;
  text-align: left;
}

.input-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.input-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
}

/* Login Button */
.btn {
  width: 100%;
  background: #007bff;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.btn:hover {
  background: #0056b3;
}

/* Messages */
.error-message {
  color: red;
  margin-top: 10px;
}

.success-message {
  color: green;
  margin-top: 10px;
}

/* Register Link */
.register-link {
  margin-top: 10px;
  font-size: 14px;
}

.register-link a {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
