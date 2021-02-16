package org.permission.annotation;

import org.permission.enums.PermissionActionEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {

    String resource();

    PermissionActionEnum action();
}
