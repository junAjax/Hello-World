package com.qfedu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qfedu.reggie.Mapper.DishMapper;
import com.qfedu.reggie.dto.DishDto;
import com.qfedu.reggie.entity.Dish;
import com.qfedu.reggie.entity.DishFlavor;
import com.qfedu.reggie.service.DishFlavorService;
import com.qfedu.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品,同时保存对应的口味数据
     *
     * @param dishDto
     */
    //开启事务的注解
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本消息到菜品表dish中
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((o) -> {
            o.setDishId(dishId);
            return o;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

//    @Override
//    public void remove(Long ids) {
//
//    }


    /**
     * //根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品
        Dish byId = this.getById(id);

        DishDto dishDto = new DishDto();

        //拷贝
        BeanUtils.copyProperties(byId, dishDto);

        //查询当前菜品对应的口味信息 从dish_flavor 表格查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, byId.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }


    /**
     //更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据--dish_flavor表的inser操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((o)->{
            o.setDishId(dishDto.getId());
            return o;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);



    }
}
