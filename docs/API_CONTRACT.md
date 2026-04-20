# 用户登录接口

```markdown
- **路径**: `/api/auth/login`
- **方法**: `POST`
- **Content-Type**: `application/json`
```

## 请求体 (LoginDTO)

```json
{
  "username": "string, required",
  "password": "string, required (min 6 chars)"
}
```



## 成功响应 (200 OK)

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

## 失败响应 (401/400)

```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null
}
```

# 统一响应包装类

所有接口均使用此结构：

```java
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
}
```

