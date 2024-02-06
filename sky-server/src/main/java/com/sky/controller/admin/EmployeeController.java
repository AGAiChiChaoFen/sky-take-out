package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {


    /**
     * 业务层对象
     */
    @Autowired
    private EmployeeService employeeService;

    /**
     * JwtProperties是封装Jwt参数的对象，他通过传入配置类中的属性从而获得Jwt的配置信息
     */
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

            Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();   //创建Jwt中的自定义属性对象
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId()); //封装键值对：empId: 员工ID
        String token = JwtUtil.createJWT(   //创建Jwt令牌
                jwtProperties.getAdminSecretKey(),  //登录密钥
                jwtProperties.getAdminTtl(),    //令牌有效期
                claims);    //需要封装的对象

        /**
         * 用户Build创建对象
         */
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()     //封装属性，相应给前端页面
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工登出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     *
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        System.out.println("当前线程的ID：" + Thread.currentThread().getId());
        log.info("新增员工：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }
}
