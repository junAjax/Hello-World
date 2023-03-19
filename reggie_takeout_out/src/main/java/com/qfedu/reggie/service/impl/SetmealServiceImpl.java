package com.qfedu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qfedu.reggie.Mapper.SetmealMapper;
import com.qfedu.reggie.common.CustomException;
import com.qfedu.reggie.dto.SetmealDto;
import com.qfedu.reggie.entity.Setmeal;
import com.qfedu.reggie.entity.SetmealDish;
import com.qfedu.reggie.service.SetmealDishService;
import com.qfedu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    //关联两张表的关联
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param SetmealDto
     */
    //事物的注解 要不全成功要不全失败
    @Transactional
    @Override
    public void saveWithDish(SetmealDto SetmealDto) {
        ///保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(SetmealDto);

        List<SetmealDish> setmealDishes = SetmealDto.getSetmealDishes();
        setmealDishes.stream().map((o)->{
            o.setSetmealId(SetmealDto.getId());
            return o;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);


    }

    /**
     * 删除套餐 同时需要删除套餐和菜品的关联数据
     * @param ids
     *   @Transactional 业务操作两张表格 加上事务注解
     */

    @Transactional
    @Override
    public void removeWitDish(List<Long> ids) {

        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可以用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);//判断是否为1 为1就是起售

        int count = this.count(queryWrapper);
        if (count > 0){
            //如果不能删除，就抛一个业务异常
            throw new CustomException("套餐正在售卖，不可以删除");
        }

        //如果可以删除，先删除套餐表中的数据 --setmeal
        this.removeByIds(ids);
        /// / delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        //删除关联表中的数据--setmeal_dish'
        setmealDishService.remove(queryWrapper1);




    }
}
