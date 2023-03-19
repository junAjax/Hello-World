package com.qfedu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qfedu.reggie.entity.Category;

public interface CategoryService  extends IService<Category> {
     void remove(Long ids);
}
