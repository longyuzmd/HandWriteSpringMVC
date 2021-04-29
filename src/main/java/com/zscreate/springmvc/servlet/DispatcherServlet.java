package com.zscreate.springmvc.servlet;

import com.zscreate.springmvc.annaotation.*;
import com.zscreate.springmvc.controller.ZmdController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: HandWriteSpringMVC
 * @Package: com.zscreate.springmvc.servlet
 * @ClassName: DispatcherServlet
 * @Author: longyuzmd
 * @Description: 前端控制器
 * @Date: 2021/4/28 11:27 上午
 * @Version: 1.0
 */
public class DispatcherServlet extends HttpServlet {

    ArrayList<String> classNames = new ArrayList<String>();

    HashMap<String, Object> beans = new HashMap<String, Object>();

    HashMap<String, Object> urlMethod = new HashMap<String, Object>();

    private static final long serialVersionUID = 1L;

    public void init(ServletConfig config){
        // IOC
        // 扫描所有的bean 扫描所有的class文件
        scanPackage("com.zscreate");

        // 装配实例化对象到Map
        doInstance();

        // 实现@Autowired注解功能
        doIoc();

        // 路径与方法的对应关系处理
        buildUrlMapping();
    }

    /**
     * 功能：处理路径和方法的对应关系
     *
     * 编程思路：
     *
     * 1、 首先遍历beans,获取controller的class对象
     *
     */
    private void buildUrlMapping() {
        if(beans.size() <= 0){
            System.out.println("实例化对象装配失败......!");
            return;
        }

        for(Map.Entry<String, Object> entry : beans.entrySet()) {

            Object instance = entry.getValue();  // 得到实例化对象

            Class<?> clazz = instance.getClass();  // 得到实例化对象的class对象

            if(clazz.isAnnotationPresent(ZsCreateController.class)){
                Method[] methods = clazz.getMethods();
                String controllerUrl = "";
                if(clazz.isAnnotationPresent(ZsCreateRequestMapping.class)){
                    ZsCreateRequestMapping requestMapping_controller = clazz.getAnnotation(ZsCreateRequestMapping.class);
                    controllerUrl = requestMapping_controller.value();
                }
                for(Method method : methods){
                    if(method.isAnnotationPresent(ZsCreateRequestMapping.class)){
                        ZsCreateRequestMapping requestMapping_method = method.getAnnotation(ZsCreateRequestMapping.class);
                        urlMethod.put(controllerUrl+requestMapping_method.value(),method);
                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }
    }

    /**
     *  功能：实现@Autowired注解功能，
     *  将service实例注入到controller属性中（成员变量）
     *
     *  编程思路：
     *  1、判断装配实例化对象是否成功
     *
     *  2、获取controller的class对象
     *
     *  3、获取所有的属性，并给对应实例的属性设置实例化对象 field.set(controller_instance, .....)
     */
    private void doIoc() {
        if(beans.size() <= 0){
            System.out.println("实例化对象装配失败......!");
            return;
        }

        for(Map.Entry<String, Object> entry : beans.entrySet()){

            Object instance = entry.getValue();  // 得到实例化对象

            Class<?> clazz = instance.getClass();  // 得到实例化对象的class对象

            if(clazz.isAnnotationPresent(ZsCreateController.class)){
                // 表示该是controller的class对象
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields){
                    if(field.isAnnotationPresent(ZsCreateAutowired.class)) {
                        // 判断字段有没有@ZsCreateAutowired注解
                        ZsCreateAutowired autowired = field.getAnnotation(ZsCreateAutowired.class);
                        field.setAccessible(true);
                        try {
                            field.set(instance, beans.get(autowired.value()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else{
                        continue;
                    }
                }
            }
        }
    }

    private void doInstance() {
        /**
         * 编程思路：
         *
         * 1、拿到所有的class的全类名  com.zscreate.xxxxxxxxx.class
         *
         * 2、判断注解类型，确定是service 还是 controller
         *
         * 3、controller(key = @ZsCreateRequestMapping.value)
         * 和 service(key = @ZsCreateService.value ) 装配到map
         */
        if(classNames.size() <= 0){
            System.out.println("扫包失败........!");
            return;
        }

        for(String className : classNames){
            try {
                Class<?> clazz = Class.forName(className.replaceAll(".class", ""));
                if(clazz.isAnnotationPresent(ZsCreateController.class)){
                    // controller 的 class对象
                    ZsCreateRequestMapping zsCreateRequestMapping = clazz.getAnnotation(ZsCreateRequestMapping.class);
                    beans.put(zsCreateRequestMapping.value(),clazz.newInstance());
                }else if(clazz.isAnnotationPresent(ZsCreateService.class)){
                    // service 的 class对象
                    ZsCreateService zsCreateService = clazz.getAnnotation(ZsCreateService.class);
                    beans.put(zsCreateService.value(),clazz.newInstance());
                }else{
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void scanPackage(String basePackage) {
        URL url = this.getClass().getClassLoader()
                .getResource("/"+basePackage.replaceAll("\\.","/"));
        String fileStr = url.getFile();

        File file = new File(fileStr);

        String[] filesStr = file.list();  //springmvc

        for(String path : filesStr){
            File filePath = new File(fileStr + path);

            if(filePath.isDirectory()){
                scanPackage(basePackage+"."+path);
            }else{
                classNames.add(basePackage+"."+filePath.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理路径
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        String path = uri.replaceAll(contextPath, "");

        // 获取该路径需要的调用的方法
        Method method = (Method) urlMethod.get(path);

        // 需要获取controller实例
        ZmdController instance =(ZmdController) beans.get("/" + path.split("/")[1]);

        try {
            method.invoke(instance,hand(req,resp,method));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private static Object[] hand(HttpServletRequest req, HttpServletResponse resp,Method method){
        // 首先获取方法的所有参数
        Class<?>[] parameterTypes = method.getParameterTypes();

        // new 参数数组
        Object[] args = new Object[parameterTypes.length];

        int args_i = 0;
        int index = 0;

        for(Class<?> paramClazz : parameterTypes){
            if(ServletRequest.class.isAssignableFrom(paramClazz)){
                args[args_i++] = req;
            }
            if(ServletResponse.class.isAssignableFrom(paramClazz)){
                args[args_i++] = resp;
            }

            Annotation[] paramAns = method.getParameterAnnotations()[index];

            if(paramAns.length > 0){
                for (Annotation paramAn : paramAns) {
                    if(ZsCreateRequestParam.class.isAssignableFrom(paramAn.getClass())){
                        ZsCreateRequestParam rp = (ZsCreateRequestParam)paramAn;
                        args[args_i++] = req.getParameter(rp.value());
                    }
                }
            }
            index++;
        }

        return args;
    }

}
