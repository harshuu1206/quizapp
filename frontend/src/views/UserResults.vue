<template>
  <div class="result-container">
    <h2>Quiz Results</h2>
    <p v-if="loading">Loading...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <div v-else>
      <p><strong>Username:</strong> {{ username }}</p>
      <p><strong>Subject:</strong> {{ subject }}</p>
      <p><strong>Your Score:</strong> {{ score }} / {{ totalQuestions }}</p>
    </div>
    <router-link to="/dashboard" class="btn">Back to Dashboard</router-link>
  </div>
</template>

<script>
import axiosInstance from "@/axiosInstance"; // ✅ Import axios instance

export default {
  data() {
    return {
      username: "",
      subject: "",
      score: 0,
      totalQuestions: 0,
      loading: true,
      error: null,
    };
  },
  async mounted() {
    try {
      const userId = localStorage.getItem("userId");
      if (!userId) {
        throw new Error("User not found. Please log in.");
      }

      const response = await axiosInstance.get(`/api/quiz/results/${userId}`);
      console.log("API Response:", response.data); // ✅ Debugging log

      if (!response.data) {
        throw new Error("No quiz attempts found.");
      }

      this.username = response.data.username || "Unknown"; // ✅ Get username
      this.subject = response.data.subject || "N/A"; // ✅ Get subject
      this.score = response.data.score;
      this.totalQuestions = response.data.totalQuestions;
    } catch (error) {
      console.error("Error fetching results:", error);
      this.error = error.message || "Error fetching results. Please try again.";
    } finally {
      this.loading = false;
    }
  },
};
</script>

<style scoped>
.result-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  text-align: center;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
}

.btn {
  display: block;
  background: #007bff;
  color: white;
  padding: 10px;
  text-decoration: none;
  border-radius: 5px;
  margin-top: 10px;
}

.error {
  color: red;
  font-weight: bold;
}
</style>
