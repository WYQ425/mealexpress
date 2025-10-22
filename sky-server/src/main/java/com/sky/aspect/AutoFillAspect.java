package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 定义切入点：即通过切点表达式子定义连接点匹配的满足条件
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 定义通知并与切入点组成切面，完成自动填充功能
     * @param joinPoint 获取连接点信息的对象参数
     */
    //配置通知类型并引入切入点表达式
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("进行公共字段自动填充...");

        //获取连接点方法的AutoFill注解的数据库操作类型
        //获取方法签名对象getSignature返回Signature，强转成MethodSignature
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();
        //获取方法对象，再获取注解对象(指定注解类)
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获取注解的属性：OperationType
        OperationType operationType = autoFill.value();

        //获取方法参数中的Entity对象(规定开发放在第一个)
        Object[] args = joinPoint.getArgs();
        if(args==null||args.length==0) return;
        Object entity = args[0];

        //需要填充的数据
        LocalDateTime time = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据数据库操作类型进行填充字段
        try {
            switch (operationType) {
                case INSERT:
                    insertFill(entity,time,currentId);
                    break;
                case UPDATE:
                    updateFill(entity,time,currentId);
                    break;
                default:
                    log.info(AutoFillConstant.SET_OPERATION_TYPE_ERROR);
            }
        } catch (Exception e) {
            log.info(AutoFillConstant.SET_ERROR);
        }
    }

    /**
     * 添加创建和更新的时间和操作人id数据(4项目)
     * @param entity 被添加的实体类对象
     */
    public void insertFill(Object entity,LocalDateTime time,Long id)throws Exception{
        //获取类对象
        Class<?> entityClass = entity.getClass();
        //通过方法名和形参的类来获取方法对象
        Method setCreateTime = entityClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
        Method setCreateUser = entityClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);

        //执行方法
        setCreateTime.invoke(entity,time);
        setCreateUser.invoke(entity,id);

        //同时填充更新字段
        updateFill(entity,time,id);
    }

    /**
     * 添加更新的时间和操作人id数据(2项目)
     * @param entity 被添加的实体类对象
     */
    public void updateFill(Object entity,LocalDateTime time,Long id) throws Exception{
        //获取类对象
        Class<?> entityClass = entity.getClass();
        //通过方法名和形参的类来获取方法对象
        Method setUpdateTime = entityClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entityClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

        //执行方法
        setUpdateTime.invoke(entity,time);
        setUpdateUser.invoke(entity,id);
    }
}
