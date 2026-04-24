package com.drone.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.drone.patrol.common.Result;
import com.drone.patrol.dto.LoginDTO;
import com.drone.patrol.dto.RegisterDTO;
import com.drone.patrol.entity.SysUser;
import com.drone.patrol.mapper.SysUserMapper;
import com.drone.patrol.util.JwtUtil;
import com.drone.patrol.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 *
 * 功能说明：
 * - 提供用户登录功能，验证用户名密码并生成JWT令牌
 * - 提供用户注册功能，创建新用户账号
 * - 使用BCryptPasswordEncoder进行密码加密存储
 * - 使用JWT实现无状态认证
 *
 * 安全说明：
 * - 密码使用BCrypt加密存储，不可逆向解密
 * - JWT令牌包含用户ID、用户名和角色信息
 * - 令牌用于后续请求的身份验证
 *
 * @author Drone Patrol Team
 */
@Service
public class AuthService {

    /** 用户Mapper接口 */
    @Autowired
    private SysUserMapper sysUserMapper;

    /** JWT工具类 */
    @Autowired
    private JwtUtil jwtUtil;

    /** 密码加密器 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求参数（包含用户名和密码）
     * @return 统一返回结果，包含JWT令牌和用户信息
     *
     * @example
     * // 登录成功返回示例
     * {
     *   "code": 200,
     *   "message": "登录成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIs...",
     *     "userId": 1,
     *     "username": "admin",
     *     "nickname": "管理员",
     *     "role": "ADMIN"
     *   }
     * }
     */
    public Result<LoginVO> login(LoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginVO loginVO = new LoginVO(token, user.getId(), user.getUsername(), user.getNickname(), user.getRole());

        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户注册
     *
     * @param registerDTO 注册请求参数（包含用户名、密码、昵称、角色）
     * @return 统一返回结果
     *
     * @example
     * // 注册成功返回示例
     * {
     *   "code": 200,
     *   "message": "操作成功",
     *   "data": null
     * }
     *
     * @note 默认角色为USER，如需创建管理员账号需传入role="ADMIN"
     */
    public Result<Void> register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            return Result.error("账号已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setRole(registerDTO.getRole() != null ? registerDTO.getRole() : "USER");

        int result = sysUserMapper.insert(user);
        return result > 0 ? Result.success() : Result.error("注册失败");
    }
}
