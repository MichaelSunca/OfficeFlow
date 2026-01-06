package com.officeflow.backend.controller;

import com.officeflow.backend.entity.User;
import com.officeflow.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController                // 说明这是一个 REST 风格的控制器，返回值会自动转为 JSON
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper; // 注入 Mapper

    /**
     * Get all users from database
     * Access via: http://localhost:8080/api/users
     */
    @GetMapping("/list")
    public List<User> getAllUsers() {
        // 直接调用 MyBatis-Plus 提供的 selectList 方法
        // null 表示没有查询条件，即查询所有
        return userMapper.selectList(null);
    }
}