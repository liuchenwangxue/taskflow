import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:8080/api', // 基础 URL
  timeout: 5000, // 请求超时时间
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    // 对请求错误做些什么
    console.log(error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做些什么
    const res = response.data;
    if (res.code !== 20000) { // 假设后端返回的错误码不是 200
      console.error('Error:', res.message || 'Error');
      // 可以根据不同的错误码进行不同的处理，例如：
      // if (res.code === 401) {
      //   // token 过期，重新登录
      // }
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return res;
    }
  },
  error => {
    // 对响应错误做些什么
    console.log('Error:', error.response ? error.response.data : error.message);
    return Promise.reject(error);
  }
);

export default service;
