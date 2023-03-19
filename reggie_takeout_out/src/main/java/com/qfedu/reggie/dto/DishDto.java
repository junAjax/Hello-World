package com.qfedu.reggie.dto;

import com.qfedu.reggie.entity.Dish;
import com.qfedu.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
//DTO，全称为Data Transfer object，即数据传输对象，一般用于展示层与服务层之间的数据传输。
@Data
public class DishDto extends Dish {

    //菜品对应口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
