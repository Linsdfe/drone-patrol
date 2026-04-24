package com.drone.patrol.config;

import com.drone.patrol.entity.SysUser;
import com.drone.patrol.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitDataConfig implements ApplicationRunner {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查是否已存在管理员账号
        if (sysUserService.count() == 0) {
            // 创建默认管理员账号
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // 密码会在 SysUserService 中自动加密
            admin.setNickname("系统管理员");
            admin.setRole("ADMIN");
            sysUserService.addUser(admin);
            
            // 创建默认普通用户
            SysUser user = new SysUser();
            user.setUsername("user");
            user.setPassword("admin123");
            user.setNickname("普通用户");
            user.setRole("USER");
            sysUserService.addUser(user);
            
            System.out.println("默认账号初始化完成：");
            System.out.println("管理员账号：admin / admin123");
            System.out.println("普通用户账号：user / admin123");
        }
    }
}
