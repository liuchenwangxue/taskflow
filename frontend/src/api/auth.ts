import service from '../utils/request';

// 定义登录请求参数接口
export interface LoginParams {
  username: string;
  password: string;
}

// 定义用户信息接口
export interface UserInfo {
  id: number;
  username: string;
  // 其他用户信息
}

// 定义登录响应接口
export interface LoginResponse {
  code: number;
  message: string;
  data: {
    token: string;
    userInfo: UserInfo;
  };
}

/**
 * 登录接口
 * @param data 登录参数
 * @returns Promise<LoginResponse>
 */
export function loginApi(data: LoginParams) {
  return service({
    url: '/auth/login',
    method: 'post',
    data,
  });
}
