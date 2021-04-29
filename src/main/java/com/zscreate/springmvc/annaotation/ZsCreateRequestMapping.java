package com.zscreate.springmvc.annaotation;

import java.lang.annotation.*;

/**
 * @ProjectName: HandWriteSpringMVC
 * @Package: com.zscreate.springmvc.annaotation
 * @ClassName: ZsCreataAutowired
 * @Author: longyuzmd
 * @Description: @RequestMapping注解的手写
 * @Date: 2021/4/28 10:21 上午
 * @Version: 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)  // 在运行时通过反射机制获取注解
@Documented  // javadoc
public @interface ZsCreateRequestMapping {

    String value() default "";
}
