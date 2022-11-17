package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    EmployeeService employeeService;


    /**
     * 1.将页面提交的密码进行md5加密
     * 2.根据页面提交的用户名查询数据库
     * 3.如果没有查询到则返回登录失败结果
     * 4.密码比对，不对则返回登录失败结果 对的话
     * 5.查看员工状态  如果被禁用 则返回禁用结果
     * 6.登录成功 将员工id存入session 并返回登录结果
     * @param request      获取session对象 保存 用户信息
     * @param employee     将接收到的信息封装到employee对象中 前端的name和employee中的必须一样
     * @return   R 服务端的数据都封装成这个对象
     */
//    员工登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp==null) {
            return R.error("登录失败");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        if (emp.getStatus()==0) {
            return R.error("账号已禁用");
        }

        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

//员工的退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        清理session中的保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息 {}",employee.toString());
//         设置初始密码 md5 加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name   查询时输入的名字
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {} ,pageSize = {},name = {}",page,pageSize,name);

//        构造分页构造器
        Page<Employee> pageInfo = new Page(page, pageSize);
//        条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
//        添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
//        执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        long id = Thread.currentThread().getId();
        log.info("线程id = {}",id);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }


}
