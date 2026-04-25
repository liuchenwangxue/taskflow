import { defineStore } from 'pinia';
import { loginApi, LoginParams, UserInfo } from '../api/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}') as UserInfo,
  }),
  actions: {
    async login(loginParams: LoginParams) {
      try {
        const response = await loginApi(loginParams);

        // 🔥🔥🔥 加调试日志 🔥🔥🔥
        console.log('=== Login Debug ===');
        console.log('response:', response);
        console.log('response.data:', (response as any).data);
        console.log('token:', (response as any).data?.token);
        console.log('userInfo:', (response as any).data?.userInfo);
        console.log('typeof token:', typeof (response as any).data?.token);
        // 🔥🔥🔥 调试日志结束 🔥🔥🔥

        const { token, userInfo } = response.data;

        // 🔥 加空值检查
        if (!token) {
          console.error('Token is empty! response.data =', response.data);
          throw new Error('Token is empty');
        }

        this.token = token;
        this.userInfo = userInfo;
        localStorage.setItem('token', token);
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
        return Promise.resolve(response);
      } catch (error: any) {
        console.error('Login failed:', error);
        return Promise.reject(error);
      }
    },
    logout() {
      this.token = '';
      this.userInfo = {} as UserInfo;
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    },
  },
});
