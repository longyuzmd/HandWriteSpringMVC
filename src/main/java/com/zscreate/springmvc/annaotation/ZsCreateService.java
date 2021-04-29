package com.zscreate.springmvc.annaotation;

import java.lang.annotation.*;

/**
 * @ProjectName: HandWriteSpringMVC
 * @Package: com.zscreate.springmvc.annaotation
 * @ClassName: ZsCreateService
 * @Author: longyuzmd
 * @Description: @Service 注解手写
 * @Date: 2021/4/28 10:41 上午
 * @Version: 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)  // 在运行时通过反射机制获取注解
@Documented
public @interface ZsCreateService {

    String value() default "";
}
