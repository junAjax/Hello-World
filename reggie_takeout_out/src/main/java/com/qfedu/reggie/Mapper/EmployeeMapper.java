package com.qfedu.reggie.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qfedu.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper  extends BaseMapper<Employee> {
}
