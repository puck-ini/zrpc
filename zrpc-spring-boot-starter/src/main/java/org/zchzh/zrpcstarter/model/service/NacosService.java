package org.zchzh.zrpcstarter.model.service;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.zchzh.zrpcstarter.model.ServiceObject;

/**
 * @author zengchzh
 * @date 2021/5/9
 */
public class NacosService extends ServiceObject {

    public NacosService(String name) {
        super(name);
    }


    public void transferTo(Instance instance) {

    }

    public void transferFrom(Instance instance) {

    }
}
