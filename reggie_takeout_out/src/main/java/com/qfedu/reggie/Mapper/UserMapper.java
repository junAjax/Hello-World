package com.qfedu.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qfedu.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
