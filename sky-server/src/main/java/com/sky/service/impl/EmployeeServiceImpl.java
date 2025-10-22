package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

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
        //进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
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
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        //DTO只包含了前端传进来的参数对应的属性，要进行属性复制到属性更全的持久层实体类Entity中
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置启动状态和默认密码（md5加密过的）
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        /*这部分已经通过aop实现
        //通过线程局部变量设置创建操作人员id和更新操作人员id
        Long operatorId = BaseContext.getCurrentId();
        employee.setCreateUser(operatorId);
        employee.setUpdateUser(operatorId);

        //更新创建和操作时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());*/

        //调用数据层mapper存入数据
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工(通过pagehelper来实现)
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        //启动pagehelper插件配置分页查询的页码和记录数，为下一行(仅一行)sql代码加上动态的分页limit？，？语句
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //随后跟上查询sql代码，返回page对象
        Page<Employee> page=employeeMapper.query(employeePageQueryDTO);

        //获取插件封装好的查询结果Page对象的total和records
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        //对员工密码进行赋值覆盖保护
        records.forEach(employee -> employee.setPassword("*********"));

        //返回自定义的PageResult对象
        return new PageResult(total,records);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //一般数据库操作使用一个Entity类去操作，规范开发
        //基于已添加@Builder注解的类快速构建其对象
        Employee employee=Employee.builder()
                .id(id)
                .status(status)
                .build();

        //将Entity类传给持久层就行更新数据库操作
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        //因为要返回给前端，所以需要吧员工信息的隐私数据进行修改隐藏
        employee.setPassword("**********");
        return employee;
    }

    /**
     * 修改员工数据
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        //将DTO属性复制到Entity
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        /*这部分已经通过aop实现
        //更新操作时间,通过线程局部变量设置更新操作人员id
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());*/

        //传给持久层进行数据库操作
        employeeMapper.update(employee);
    }

    /**
     * 修改当前登录员工密码
     * @param passwordEditDTO
     */
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        //给获取当前操作员工Id，修改的是他的密码
        Long empId = BaseContext.getCurrentId();
        //现根据id查询员工，获取其密码
        String oldPassword = employeeMapper.getById(empId).getPassword();

        //根据加密后是否与原密码相同判断是否执行修改密码
        if(oldPassword.equals(DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes()))){
            //与原密码相同，执行加密密码并更新
            Employee employee =Employee.builder()
                    .id(empId)
                    .password(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()))
                    .build();
            employeeMapper.update(employee);
        }else {
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_EDIT_FAILED);
        }
    }
}
