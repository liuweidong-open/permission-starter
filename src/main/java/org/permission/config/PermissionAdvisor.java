package org.permission.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.aop.Advice;
import org.permission.annotation.Permission;
import org.permission.domain.ResourceActionDO;
import org.permission.enums.PermissionActionEnum;
import org.permission.exception.AccessDeniedException;
import org.permission.exception.AuthenticationException;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PermissionAdvisor implements PointcutAdvisor {

    private ObjectMapper mapper = new ObjectMapper();

    private Base64.Decoder decoder = Base64.getDecoder();

    List<String> permissionValidMethods;

    public PermissionAdvisor(List<String> needProxyMethodList) {
        this.permissionValidMethods = needProxyMethodList;
    }

    @Override
    public Pointcut getPointcut() {
        NameMatchMethodPointcut methodPointcut = new NameMatchMethodPointcut();
        methodPointcut.setMappedNames(permissionValidMethods.toArray(new String[permissionValidMethods.size()]));

        return methodPointcut;
    }

    @Override
    public Advice getAdvice() {
        MethodBeforeAdvice methodBeforeAdvice = (method, args, target) -> {
            Permission permissionAnnotation = method.getAnnotation(Permission.class);
            String permissionResource = permissionAnnotation.resource();
            PermissionActionEnum permissionActionEnum = permissionAnnotation.action();
            int permissionAction = permissionActionEnum.getAction();

            String headerPermission = getHeaderPermission();
            byte[] decode = decoder.decode(headerPermission.getBytes());

            List<ResourceActionDO> resourceActionList = mapper.readValue(new String(decode), new TypeReference<List<ResourceActionDO>>() { });
            Map<String, Integer> resourceActionMap = resourceActionList.stream().collect(Collectors.toMap(ResourceActionDO::getResource, ResourceActionDO::getAction));

            // 不包含需要的资源 或者 action不够 value & action == action才算有此action权限
            if (!resourceActionMap.containsKey(permissionResource) || (resourceActionMap.get(permissionResource) & permissionAction) != permissionAction) {
                throw new AccessDeniedException("permission denied");
            }
        };

        return methodBeforeAdvice;
    }

    @Override
    public boolean isPerInstance() {
        return false;
    }

    private String getHeaderPermission() throws AuthenticationException {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String permission = httpServletRequest.getHeader("permission");

        if (permission == null || permission.length() == 0) {
            throw new AuthenticationException("request has not permission header info");
        }

        return permission;
    }
}
