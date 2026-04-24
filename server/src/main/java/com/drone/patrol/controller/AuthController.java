package com.drone.patrol.controller;

import com.drone.patrol.common.Result;
import com.drone.patrol.dto.LoginDTO;
import com.drone.patrol.dto.RegisterDTO;
import com.drone.patrol.service.AuthService;
import com.drone.patrol.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 处理登录和注册请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 认证服务
     */
    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        System.out.println("进入login方法");
        return authService.login(loginDTO);
    }

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }
}
