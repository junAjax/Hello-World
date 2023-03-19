package com.qfedu.reggie.dto;

import com.qfedu.reggie.entity.Setmeal;

import com.qfedu.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;
//DTO，全称为Data Transfer object，即数据传输对象，一般用于展示层与服务层之间的数据传输。
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
