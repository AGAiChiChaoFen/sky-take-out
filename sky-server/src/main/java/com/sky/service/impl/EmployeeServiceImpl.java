package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;


@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的明文密码进行MD5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());     //调用第一个提示的方法，并且将字符串的字节数组传进去
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {

        System.out.println("当前线程的ID：" + Thread.currentThread().getId());

        //传进来的是一个DTO对象，DTO就是用于各层之间传送数据的对象，就是封装相关信息的，这里是用作封装前端传过来的员工信息
        //为什么不直接用Employee实体类对象接收？   --因为实体类对象中有很多参数前端不会传过来，当传过来的参数和实体类差异太大时
        //我们推荐使用DTO来将数据 先封装好

        //当然保存到数据库的时候我们肯定要保存完整的属性信息，所以应该使用Employee类
        //一般情况下是使用employee.set()方法一个个的设置，但是太繁琐
        //这里我们直接调用一个API --BeanUtils

        Employee employee = new Employee();

        //实体类对象的封装
        BeanUtils.copyProperties(employeeDTO , employee);  //前一个参数是源参数，后一个是目的参数，要保证属性名一致

        //还有些其他信息要手动设置

        //设置帐号状态，1->正常 0->禁用
        employee.setStatus(StatusConstant.ENABLE);  //用常量类来标记，否则就是把代码写死了

        //设置密码，默认密码123456，注意要转换成MD5加密后的格式
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateTime(LocalDateTime.now());

        //设置当前记录的创建人id和修改人的id   -->我现在是登录哪个用户来新建这个员工的？
        //显然目前我们办不到，只能先写死
        // TODO这里后面要修改
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);

    }

}
