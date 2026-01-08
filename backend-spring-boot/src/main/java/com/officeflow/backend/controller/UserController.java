package com.officeflow.backend.controller;

import com.officeflow.backend.common.Result;
import com.officeflow.backend.entity.User;
import com.officeflow.backend.mapper.UserMapper;
import com.officeflow.backend.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController                // 说明这是一个 REST 风格的控制器，返回值会自动转为 JSON
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper; // 注入 Mapper

    /**
     * Get all users from database
     * Access via: http://localhost:8080/api/users/list
     */
    @GetMapping("/list")
    public Result<List<UserVO>> getAllUsers() {
        // 直接调用 MyBatis-Plus 提供的 selectList 方法
        // null 表示没有查询条件，即查询所有
        var users = userMapper.selectList(null);
        var data = users.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(data);
    }

    /**
     * 根据 ID 获取用户信息
     * 访问路径: GET http://localhost:8080/api/users/1
     */
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable("id") Long id) {
        // 1. 从数据库查询实体
        User user = userMapper.selectById(id);

        // 2. 如果没找到，可以抛出异常或返回 null (拦截器会处理)
        if (user == null) {
            return null;
        }

        // 3. 转换为 VO 并返回
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }
}