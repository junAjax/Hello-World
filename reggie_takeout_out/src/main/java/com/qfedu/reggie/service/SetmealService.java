package com.qfedu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qfedu.reggie.dto.SetmealDto;
import com.qfedu.reggie.entity.Setmeal;
import com.qfedu.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param
     */
    public void saveWithDish(SetmealDto SetmealDto);


    /**
     * 删除套餐 同时需要删除套餐和菜品的关联数据
     */
    public void  removeWitDish(List<Long> ids);

}
