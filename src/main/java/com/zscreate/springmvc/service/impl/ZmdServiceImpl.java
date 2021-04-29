package com.zscreate.springmvc.service.impl;

import com.zscreate.springmvc.annaotation.ZsCreateService;
import com.zscreate.springmvc.service.ZmdService;

/**
 * @ProjectName: HandWriteSpringMVC
 * @Package: com.zscreate.springmvc.service
 * @ClassName: ZmdServiceImpl
 * @Author: longyuzmd
 * @Description: 业务层实现类
 * @Date: 2021/4/28 10:51 上午
 * @Version: 1.0
 */
@ZsCreateService("ZmdServiceImpl")
public class ZmdServiceImpl implements ZmdService {

    public String queryParamInfo(String name, String age) {
        return "name===="+name+"=====age===="+age;
    }
}
