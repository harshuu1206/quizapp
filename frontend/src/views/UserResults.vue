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
export default {
  data() {
    return {
      score: 0,
      totalQuestions: 0,
      loading: true,
      error: null,
    };
  },
  mounted() {
    const userId = localStorage.getItem("userId"); // ✅ Retrieve userId

    if (!userId) {
      this.error = "User not found. Please log in.";
      this.loading = false;
      return;
    }

    fetch(`http://localhost:8090/api/quiz/results/${userId}`) // ✅ Fetch results for the logged-in user
      .then(response => {
        if (!response.ok) {
          throw new Error("Failed to fetch results");
        }
        return response.json();
      })
      .then(data => {
        if (data.length === 0) {
          this.error = "No quiz attempts found.";
        } else {
          let lastAttempt = data[data.length - 1]; // ✅ Get the latest quiz attempt
          this.score = lastAttempt.score;
          this.totalQuestions = lastAttempt.totalQuestions;
        }
      })
      .catch(error => {
        console.error("Error fetching results:", error);
        this.error = "Error fetching results. Please try again.";
      })
      .finally(() => {
        this.loading = false;
      });
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
