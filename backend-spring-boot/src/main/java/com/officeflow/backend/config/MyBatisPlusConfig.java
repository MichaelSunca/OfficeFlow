package com.officeflow.backend.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 用于处理 create_time 和 update_time 的自动赋值
 */
@Component
public class MyBatisPlusConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时，自动填充 createTime 和 updateTime 为当前时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时，自动填充 updateTime 为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}