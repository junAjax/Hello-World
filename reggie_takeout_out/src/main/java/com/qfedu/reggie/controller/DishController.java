package com.qfedu.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qfedu.reggie.common.R;
import com.qfedu.reggie.dto.DishDto;
import com.qfedu.reggie.entity.Category;
import com.qfedu.reggie.entity.Dish;
import com.qfedu.reggie.entity.DishFlavor;
import com.qfedu.reggie.service.CategoryService;
import com.qfedu.reggie.service.DishFlavorService;
import com.qfedu.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * // * 新加菜品
     * //
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        //清理某个分类下面的缓存数据
        String key = "dish_" +dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }


    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return //
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

//        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
        //        //构造分页构造器对象
//        Page<Dish> pageInfo = new Page<>(page, pageSize);
//        Page<DishDto> dishDtoPage = new Page<>();
//
//        //条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加过滤条件
//        queryWrapper.like(name != null, Dish::getName, name);
//        //添加排序条件
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
//
//        //执行分页查询
//        dishService.page(pageInfo, queryWrapper);
//
//        //对象拷贝  忽略这个属性records
//        BeanUtils.copyProperties(page, dishDtoPage, "records");
//
//        List<Dish> records = pageInfo.getRecords();
//        List<DishDto> list = records.stream().map((o) -> {
//            DishDto dishDto = new DishDto();
//            BeanUtils.copyProperties(o, dishDto);
//            Long categoryId = o.getCategoryId();//分页id
//            //根据id查询分类对象
//            Category category = categoryService.getById(categoryId);
//
//            if (category != null) {
//                String c = category.getName();
//                dishDto.setCategoryName(c);
//            }
//            return dishDto;
//
//
//        }).collect(Collectors.toList());
//        dishDtoPage.setRecords(list);
//
//
//        return R.success(dishDtoPage);

    }


    /**
     * 修改菜品根据id查询菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);

    }
    /**
     * 修改菜品//更新菜品信息，同时更新对应的口味信息
     */

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        //清理所有菜品的缓存数据
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);'
        //清理某个分类下面的缓存数据
        String key ="dish_" + dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);



        return R.success("修改菜品成功");
    }


    /**
     * 删除菜品
     */

    @DeleteMapping
    public R<String> delete(Long ids){
        dishService.removeById(ids);
        return R.success("删除成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list (Dish dish){
//
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() !=null,Dish::getCategoryId,dish.getCategoryId());
//        //查询状态为1的（启售的菜品）
//        queryWrapper.eq(Dish::getSort,0);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }


    @GetMapping("/list")
    public R<List<DishDto>> list (Dish dish){
        List<DishDto> dishDtoList =null;
        //动态拼接key
        String key ="dish_"+dish.getCategoryId() +"_" +dish.getStatus();

        //从redis 中获得缓存数据  通过key来获取value
        dishDtoList  = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList !=null){
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() !=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((o) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(o,dishDto);

            Long categoryId = o.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category !=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = o.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }


}
