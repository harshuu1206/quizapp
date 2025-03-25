import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8090", // Adjust based on your backend URL
});

// Automatically attach JWT token
axiosInstance.interceptors.request.use(config => {
  const token = localStorage.getItem("jwt");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

export default axiosInstance;
