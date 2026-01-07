package com.officeflow.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.officeflow.backend.entity.User;
import com.officeflow.backend.mapper.UserMapper;
import com.officeflow.backend.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername called: username = " + username);
        System.out.println("密码 " + new BCryptPasswordEncoder().encode("123456"));

        // 1. 使用 MyBatis-Plus 查询数据库
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );

        // 2. 如果查不到，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // 3. 把查出来的 user 实体塞进 LoginUser
        return new LoginUser(user);
    }
}