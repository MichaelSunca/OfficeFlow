package com.officeflow.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.officeflow.backend.entity.User;
import com.officeflow.backend.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // All CRUD methods (insert, delete, update, select) are inherited from BaseMapper.
    // No need to write basic SQL!

    /**
     * 根据用户名查询用户信息
     * 用于登录认证和权限校验
     */
    UserVO selectUserByName(@Param("username") String username);
}
