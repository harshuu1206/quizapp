<template>
  <div class="result-container">
    <h2>Quiz Results</h2>
    <p v-if="loading">Loading...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else>Your Score: {{ score }} / {{ totalQuestions }}</p>
    <router-link to="/dashboard" class="btn">Back to Dashboard</router-link>
  </div>
</template>

<script>
import axiosInstance from "@/axiosInstance"; // ✅ Import axios instance

export default {
  data() {
    return {
      score: 0,
      totalQuestions: 0,
      loading: true,
      error: null,
    };
  },
  async mounted() {
    const userId = localStorage.getItem("userId");

    if (!userId) {
      this.error = "User not found. Please log in.";
      this.loading = false;
      return;
    }

    try {
      const response = await axiosInstance.get(`/api/quiz/results/${userId}`);

      if (!response.data || response.data.length === 0) {
        this.error = "No quiz attempts found.";
      } else {
        let lastAttempt = response.data[response.data.length - 1]; // ✅ Get latest attempt
        this.score = lastAttempt.score;
        this.totalQuestions = lastAttempt.totalQuestions;
      }
    } catch (error) {
      console.error("Error fetching results:", error);
      this.error = "Error fetching results. Please try again.";
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
