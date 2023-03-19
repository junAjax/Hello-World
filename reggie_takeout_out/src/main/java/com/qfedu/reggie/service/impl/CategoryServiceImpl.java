package com.qfedu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qfedu.reggie.Mapper.CategoryMapper;
import com.qfedu.reggie.common.CustomException;
import com.qfedu.reggie.entity.Category;
import com.qfedu.reggie.entity.Dish;
import com.qfedu.reggie.entity.Setmeal;
import com.qfedu.reggie.service.CategoryService;
import com.qfedu.reggie.service.DishService;
import com.qfedu.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id删除分类，删除之前进行判断
     * @param ids
     */
    @Override

    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件，根据分类id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count1 >0){
            //已经关联菜品
            throw new CustomException("当前分类下已经关联菜品，不可以删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if (count2 >0){
            //已经关联套餐
            throw new CustomException("当前分类下已经关联套餐，不可以删除");

        }


        //正常删除分类
        super.removeById(ids);
    }
}
