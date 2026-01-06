package com.officeflow.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.officeflow.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // All CRUD methods (insert, delete, update, select) are inherited from BaseMapper.
    // No need to write basic SQL!
}
