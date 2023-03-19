package com.qfedu.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qfedu.reggie.common.R;
import com.qfedu.reggie.entity.Employee;
import com.qfedu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录登录方法
     */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //       getOne 唯一数据
        Employee emp = employeeService.getOne(queryWrapper);
        /// /3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
            //4、密码比对，如果不一致则返回登录失败结果
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }//5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已经禁用");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }


    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        清楚Session中当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    @PostMapping
    public R<String> save (HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工,员工信息：{}",employee.toString());

//        //设置密码 123456 进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        //获取创建时间
//        employee.setCreateTime(LocalDateTime.now());
//        //获取修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //获取当前用户的的id  通过session获得
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);//mybatis-plus提供的添加方法
        return R.success("添加员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page (int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        //添加过滤条件  StringUtils.isNotEmpty(name)不等于null
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getCreateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 更具id来修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,  @RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为: {}",id);
        //更新时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //通过Session获得更新的人
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        //更新人
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");

    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")//动态获得
    public R<Employee> getById(@PathVariable Long id){//@PathVariable路径匹配
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);

        if (employee != null){
            return  R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }




}
