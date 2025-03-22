<template>
  <div class="register-container">
    <div class="register-box">
      <h2>Create an Account</h2>
      <form @submit.prevent="register">
        <div class="input-group">
          <label>Username</label>
          <input type="text" v-model="user.username" placeholder="Enter username" required />
        </div>
        <div class="input-group">
          <label>Email</label>
          <input type="email" v-model="user.email" placeholder="Enter email" required />
        </div>
        <div class="input-group">
          <label>Password</label>
          <input type="password" v-model="user.password" placeholder="Enter password" required />
        </div>
        <button type="submit" class="btn">Register</button>
      </form>

      <p v-if="message" :class="{ 'error-message': isError, 'success-message': !isError }">
        {{ message }}
      </p>

      <p class="login-link">Already have an account? <router-link to="/login">Login</router-link></p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { ref } from "vue";
import { useRouter } from "vue-router";

export default {
  setup() {
    const router = useRouter();
    const user = ref({ username: "", email: "", password: "" });
    const message = ref("");
    const isError = ref(false);

    const register = async () => {
      console.log("Register button clicked!");
      console.log("User Data:", user.value);

      try {
        const response = await axios.post("http://localhost:8090/api/auth/register", user.value, {
          headers: { "Content-Type": "application/json" },
        });

        console.log("Registration Successful:", response.data);
        message.value = "Registration successful! Redirecting to login...";
        isError.value = false;

        setTimeout(() => router.push("/login"), 2000);
      } catch (error) {
        console.error("Registration Error:", error.response?.data || error.message);
        message.value = error.response?.data?.message || "Registration failed!";
        isError.value = true;
      }
    };

    return { user, register, message, isError };
  },
};
</script>

<style scoped>
/* Background */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #007bff, #00d4ff);
}

/* Register Box */
.register-box {
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

/* Register Button */
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

/* Login Link */
.login-link {
  margin-top: 10px;
  font-size: 14px;
}

.login-link a {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>