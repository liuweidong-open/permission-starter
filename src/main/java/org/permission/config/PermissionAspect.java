//package org.permission.config;
//
//import com.alibaba.fastjson.JSONObject;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.permission.annotation.Permission;
//import org.permission.domain.ResourceActionDO;
//import org.permission.enums.PermissionActionEnum;
//import org.permission.exception.AccessDeniedException;
//import org.permission.exception.AuthenticationException;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.Base64;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Aspect
//public class PermissionAspect {
//
//    private Base64.Decoder decoder = Base64.getDecoder();
//
//    @Pointcut("@annotation(org.permission.annotation.Permission)")
//    public void pointcut() {
//
//    }
//
//    @Around("pointcut()")
//    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        System.out.println("enter around");
//        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
//        Method method = methodSignature.getMethod();
//        Permission permissionAnnotation = method.getAnnotation(Permission.class);
//        String permissionResource = permissionAnnotation.resource();
//        PermissionActionEnum permissionActionEnum = permissionAnnotation.action();
//        int permissionAction = permissionActionEnum.getAction();
//
//        String headerPermission = getHeaderPermission();
//        byte[] decode = decoder.decode(headerPermission.getBytes());
//
//        List<ResourceActionDO> resourceActionList = JSONObject.parseArray(new String(decode), ResourceActionDO.class);
//        Map<String, Integer> resourceActionMap = resourceActionList.stream().collect(Collectors.toMap(ResourceActionDO::getResource, ResourceActionDO::getAction));
//
//        // 不包含需要的资源 或者 action不够 value & action == action才算有此action权限
//        if (!resourceActionMap.containsKey(permissionResource) || (resourceActionMap.get(permissionResource) & permissionAction) != permissionAction) {
//            throw new AccessDeniedException("permission denied");
//        }
//
//        Object result = proceedingJoinPoint.proceed();
//
//        return result;
//    }
//
//    private String getHeaderPermission() throws AuthenticationException {
//        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String permission = httpServletRequest.getHeader("permission");
//
//        if (permission == null || permission.length() == 0) {
//            throw new AuthenticationException("request has not permission header info");
//        }
//
//        return permission;
//    }
//}
