package com.taskflow.service;

import com.taskflow.common.enums.ErrorCode;
import com.taskflow.common.exception.AuthException;
import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.LoginResponse;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import com.taskflow.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录认证
     * @param request 登录请求
     * @return 登录响应，包含JWT Token和用户信息
     * @throws AuthException 如果认证失败
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 根据用户名查找用户
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new AuthException("用户名或密码错误", ErrorCode.AUTH_FAILED.getCode());
        }

        User user = userOptional.get();

        // 2. 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("用户名或密码错误", ErrorCode.AUTH_FAILED.getCode());
        }

        // 3. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 4. 构建登录响应
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, userInfo);
    }
}
