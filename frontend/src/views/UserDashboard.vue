<template>
  <div class="dashboard-container">
    <div class="dashboard-box">
      <h2>Welcome to the Dashboard</h2>
      <p>Select an option below:</p>
      <button class="quiz-btn" @click="startQuiz">Start Quiz</button>
      <button class="chat-btn" @click="joinChat">Join Chat Room</button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    startQuiz() {
      this.$router.push("/quiz");
    },
    joinChat() {
      this.$router.push("/chat");
    },
  },
};
</script>

<style scoped>
/* Background */
.dashboard-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #007bff, #00d4ff);
}

/* Dashboard Box */
.dashboard-box {
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

/* Buttons */
.quiz-btn {
  width: 100%;
  background: #ff5733; /* Changed color to orange-red */
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.3s ease;
  margin-bottom: 10px;
}

.quiz-btn:hover {
  background: #cc4629;
}

.chat-btn {
  width: 100%;
  background: #28a745;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.chat-btn:hover {
  background: #1e7e34;
}
</style>
