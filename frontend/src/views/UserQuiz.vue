<template>
  <div class="quiz-container">
    <h2>Quiz</h2>
    <label>Select Subject:</label>
    <select v-model="selectedSubject" @change="fetchQuestions">
      <option v-for="(subject, key) in subjects" :key="key" :value="subject.apiValue">
        {{ subject.displayName }}
      </option>
    </select>

    <div v-if="questions.length">
      <div v-for="(question, index) in questions" :key="index" class="question">
        <p v-html="decodeHtml(question.question)"></p>
        <div v-for="(option, optIndex) in question.shuffledOptions" :key="optIndex">
          <input type="radio" :name="'question-' + index" :value="option" v-model="userAnswers[index]" />
          <label v-html="decodeHtml(option)"></label>
        </div>
      </div>
      <button @click="submitQuiz">Submit</button>
    </div>
  </div>
</template>

<script>
import axiosInstance from "@/axiosInstance"; // ✅ Import axios instance

export default {
  data() {
    return {
      selectedSubject: "",
      subjects: [
        { displayName: "History", apiValue: "1" },
        { displayName: "Mathematics", apiValue: "2" },
        { displayName: "English", apiValue: "3" },
      ],
      questions: [],
      userAnswers: {},
    };
  },
  methods: {
  async fetchQuestions() {
    if (!this.selectedSubject) return;

    // 🛠 Map DB subject IDs back to OpenTDB category IDs
    const subjectToOpenTDB = {
      1: 23, // History
      2: 19, // Mathematics
      3: 17  // English (Previously Science, correct it if needed)
    };

    const opentdbCategory = subjectToOpenTDB[this.selectedSubject] || this.selectedSubject; // Default to selectedSubject if not found

    const apiUrl = `https://opentdb.com/api.php?amount=10&category=${opentdbCategory}&difficulty=easy&type=multiple`;

    try {
      const response = await fetch(apiUrl);
      const data = await response.json();

      this.questions = data.results.map((q, index) => ({
        id: index,
        question: q.question,
        correctAnswer: q.correct_answer,
        options: [...q.incorrect_answers, q.correct_answer],
        shuffledOptions: this.shuffleArray([...q.incorrect_answers, q.correct_answer]),
      }));

      this.userAnswers = {};
    } catch (error) {
      console.error("Error fetching questions:", error);
    }
    },
    async submitQuiz() {
      let score = 0;
      const userId = localStorage.getItem("userId");

      if (!userId) {
        alert("User not logged in. Please log in first.");
        this.$router.push("/login");
        return;
      }

      this.questions.forEach((q, index) => {
        if (this.userAnswers[index] === q.correctAnswer) {
          score++;
        }
      });

      const totalQuestions = this.questions.length;
      const requestBody = {
        userId: userId,
        subjectId: this.selectedSubject,
        score: score,
        totalQuestions: totalQuestions,
        timeTaken: 60,
        answers: this.questions.map((q, index) => ({
          questionId: q.id,
          selectedAnswer: this.userAnswers[index] || "",
        })),
      };

      try {
        const response = await axiosInstance.post("/api/quiz/attempts", requestBody);
        console.log("Quiz submission response:", response.data); // ✅ Now using response

        alert(`Quiz submitted! Score: ${score} / ${totalQuestions}`);
        this.$router.push("/results");
      } catch (error) {
        console.error("Error submitting quiz:", error);
        alert("Error submitting quiz. Please try again.");
      }
    },
    shuffleArray(array) {
      return array.sort(() => Math.random() - 0.5);
    },
    decodeHtml(html) {
      const txt = document.createElement("textarea");
      txt.innerHTML = html;
      return txt.value;
    },
  },
};
</script>

<style scoped>
.quiz-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
}

.question {
  margin-bottom: 15px;
}

button {
  width: 100%;
  background: #007bff;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background: #0056b3;
}
</style>
