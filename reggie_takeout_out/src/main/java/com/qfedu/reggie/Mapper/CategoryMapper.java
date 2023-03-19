package com.qfedu.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qfedu.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
