package com.feng.graduationproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.graduationproject.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper  // 标识为MyBatis Mapper接口
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper后，无需手写CRUD方法，MyBatis-Plus自动实现
}
