import { createRouter, createWebHistory } from 'vue-router';
import HomePage from "../views/HomePage.vue";
import UserDashboard from '../views/UserDashboard.vue';
import UserLogin from '../views/UserLogin.vue';
import UserQuiz from '../views/UserQuiz.vue';
import UserRegister from '../views/UserRegister.vue';
import UserResults from '../views/UserResults.vue';

const routes = [
{ path: "/", component: HomePage },
  { path: '/login', component: UserLogin },
  { path: '/dashboard', component: UserDashboard },
  { path: '/quiz', component: UserQuiz },
  { path: '/register', component: UserRegister },
 { path: '/results', component: UserResults }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;