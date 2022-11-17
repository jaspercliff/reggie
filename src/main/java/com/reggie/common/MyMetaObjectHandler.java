package com.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义员数据对象处理器
 */

/**
 * 客户端发送的每次http请求 服务端都会分配一个心得线程来处理 在处理过程中下面类的方法都是属于相同的一个线程
 * LoginCheckFilter dofilter
 * EmpController update
 * MymetaObjectHandler updateFill
 *
 * 什么是ThreadLocal?
 * ThreadLocal并不是-一个Thread,而是Thread的局部变量。当使用ThreadLocal维护变量时，ThreadLocal为每 个使用该
 * 变量的线程提供独立的变量副本，所以每一一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
 * ThreadLocal为 每个线程提供单独- -份存储空间,具有线程隔离的效果,只有在线程内才能获取到对应的值,线程外则不
 * 能访问。
 *
 * 8 ThreadLocal常用方法:
 * ● public void set(T value) 设置当前线程的线程局部变量的值
 * ， public T get()
 * 返回当前线程所对应的线程局部变量的值
 * 我们可以在LoginCheckFilter的doFilter方法中获取当前登录用户id,并调用ThreadLocal的set方法来设置当前线程的线
 * 程局部变量的值(用户id)，然后在MyMetaObjectHandler的updateFill方 法中调用ThreadLocal的get方法来获得当前
 * 线程所对应的线程局部变量的值(用户id)
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充[insert]");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段填充[update]");
        long id = Thread.currentThread().getId();
        log.info("线程id = {}",id);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
