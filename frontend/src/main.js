import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // Importing the router

const app = createApp(App);
app.use(router); // Using the router in Vue app
app.mount('#app'); // Mounting the app to the DOM