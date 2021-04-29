package com.zscreate.springmvc.controller;

import com.zscreate.springmvc.annaotation.ZsCreateAutowired;
import com.zscreate.springmvc.annaotation.ZsCreateController;
import com.zscreate.springmvc.annaotation.ZsCreateRequestMapping;
import com.zscreate.springmvc.annaotation.ZsCreateRequestParam;
import com.zscreate.springmvc.service.ZmdService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ProjectName: HandWriteSpringMVC
 * @Package: com.zscreate.springmvc.annaotation
 * @ClassName: ZmdController
 * @Author: longyuzmd
 * @Description: 控制层代码
 * @Date: 2021/4/28 10:48 上午
 * @Version: 1.0
 */
@ZsCreateController
@ZsCreateRequestMapping("/zmd")
public class ZmdController {

    @ZsCreateAutowired("ZmdServiceImpl")
    private ZmdService zmdService;

    @ZsCreateRequestMapping("/param")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @ZsCreateRequestParam("name")String name,
                      @ZsCreateRequestParam("age")String age){
        try {
            PrintWriter pw = response.getWriter();
            String result = zmdService.queryParamInfo(name, age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
