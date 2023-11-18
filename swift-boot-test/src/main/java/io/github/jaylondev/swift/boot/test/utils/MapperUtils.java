package io.github.jaylondev.swift.boot.test.utils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jaylon 2023/11/18 16:04
 */
public class MapperUtils {

    public static List<Object> createMapperProxy(String[] basePackages, ClassLoader classLoader, SqlSessionFactory sqlSessionFactory) throws Exception {
        List<Object> proxys = new ArrayList<>();
        // 扫描mapper类
        List<Class<?>> mapperClasses = ClassUtils.scanPackages(basePackages, classLoader);
        // 创建 Mapper 接口的代理对象并注册到容器中
        for (Class<?> mapperClass : mapperClasses) {
            MapperFactoryBean<?> mapperFactoryBean = new MapperFactoryBean<>(mapperClass);
            // 设置其他属性，如 SqlSessionFactory
            mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
            Object mapperObject = mapperFactoryBean.getObject();
            if (mapperObject != null) {
                proxys.add(mapperObject);
            }
        }
        return proxys;
    }
}
