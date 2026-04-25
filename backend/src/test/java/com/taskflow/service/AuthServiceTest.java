package com.taskflow.service;

import com.taskflow.common.exception.AuthException;
import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.LoginResponse;
import com.taskflow.entity.User;
import com.taskflow.repository.UserRepository;
import com.taskflow.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword"); // 模拟BCrypt加密后的密码
        testUser.setRole("USER");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void login_success() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getId(), testUser.getUsername(), testUser.getRole())).thenReturn("mockedJwtToken");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getToken());
        assertEquals(testUser.getId(), response.userInfo.getId());
        assertEquals(testUser.getUsername(), response.userInfo.getUsername());
        assertEquals(testUser.getRole(), response.userInfo.getRole());

        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtUtil, times(1)).generateToken(testUser.getId(), testUser.getUsername(), testUser.getRole());
    }

    @Test
    void login_userNotFound_throwsAuthException() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequest));

        assertEquals("用户名或密码错误", exception.getMessage());
        assertEquals(40100, exception.getCode());

        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_wrongPassword_throwsAuthException() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequest));

        assertEquals("用户名或密码错误", exception.getMessage());
        assertEquals(40100, exception.getCode());

        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), testUser.getPassword());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_emptyUsername_throwsValidationException() {
        loginRequest.setUsername(""); // 设置为空用户名
        // MethodArgumentNotValidException 会在Controller层被捕获，这里Service层不会直接抛出
        // 但为了测试Service层的逻辑，我们可以模拟一个空的Optional返回
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequest));
        assertEquals("用户名或密码错误", exception.getMessage());
        assertEquals(40100, exception.getCode());
    }

    @Test
    void login_emptyPassword_throwsValidationException() {
        loginRequest.setPassword(""); // 设置为空密码
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequest));
        assertEquals("用户名或密码错误", exception.getMessage());
        assertEquals(40100, exception.getCode());
    }
}
