package com.qfedu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qfedu.reggie.Mapper.EmployeeMapper;
import com.qfedu.reggie.entity.Employee;
import com.qfedu.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl  extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
