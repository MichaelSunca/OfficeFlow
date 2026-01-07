package com.officeflow.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.officeflow.backend.entity.User;
import com.officeflow.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername called: username = " + username);
        // 1. 使用 MyBatis-Plus 查询数据库
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        // 2. 如果查不到，直接抛出异常（Spring Security 会捕获并处理）
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // 3. 将我们的 Entity 转换成 Spring Security 的 UserDetails 对象
        // 这里暂时不处理复杂的权限(authorities)，先传个空列表
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // 这里的密码是数据库里的加密串
                .authorities(user.getRole())  // 设置角色，比如 "ADMIN"
                .build();
    }
}