package com.qfedu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qfedu.reggie.common.R;
import com.qfedu.reggie.dto.SetmealDto;
import com.qfedu.reggie.entity.Category;
import com.qfedu.reggie.entity.Setmeal;
import com.qfedu.reggie.service.CategoryService;
import com.qfedu.reggie.service.SetmealDishService;
import com.qfedu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    //删除套餐清理关于setmealCache类的全部数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageIfon = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();  //SetmealDto里面有categoryName属性


        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据name经行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新时间降序排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageIfon, queryWrapper);

        //对新拷贝
        BeanUtils.copyProperties(pageIfon, dtoPage, "records");

        List<Setmeal> records = pageIfon.getRecords();

        List<SetmealDto> toList = records.stream().map((o) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(o, setmealDto);

            //分类id
            Long categoryId = o.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());


        dtoPage.setRecords(toList);//赋值
        return R.success(dtoPage);//返回
    }


    /**
     * 删除套餐 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    //删除套餐清理关于setmealCache类的全部数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        setmealService.removeWitDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 根据条件来查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+ '_'+ #setmeal.status")
    public R<List<Setmeal>> list( Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询id
        queryWrapper.eq(setmeal.getCategoryId() !=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //查询状态
        queryWrapper.eq(setmeal.getStatus() !=null,Setmeal::getStatus,setmeal.getStatus());
        //根据更新时间降序来排
        queryWrapper.orderByDesc(Setmeal::getCreateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);


    }


}
