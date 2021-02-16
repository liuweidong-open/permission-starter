package org.permission.config;

import org.permission.annotation.Permission;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PermissionPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null) {
            Class<?> aClass = bean.getClass();
            List<String> permissionValidMethods = needPermissionValidMethods(aClass);

            if (!permissionValidMethods.isEmpty()) {
                ProxyFactory proxyFactory = new ProxyFactory();
                proxyFactory.setTarget(bean);
                proxyFactory.addAdvisor(new PermissionAdvisor(permissionValidMethods));
                return proxyFactory.getProxy();
            }
        }

        return bean;
    }

    private List<String> needPermissionValidMethods(Class<?> aClass) {
        List<String> permissionList = new ArrayList<>();

        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            boolean isPermission = method.isAnnotationPresent(Permission.class);

            if (!isPermission) {
                continue;
            }

            permissionList.add(method.getName());
        }

        return permissionList;
    }
}
