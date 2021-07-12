package org.zchzh.zrpcstarter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author zengchzh
 * @date 2021/3/11
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceObject implements Serializable {

    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 服务ip
     */
    private String ip;
    /**
     * 服务端口
     */
    private Integer port;
    /**
     * 服务地址
     */
    private String address;
    /**
     * 服务权重
     */
    private Double weight;
    /**
     * 服务接口
     */
    private Class<?> clazz;
    /**
     * 服务元数据
     */
    private Map<String, String> meta;

    public String getAddress() {
        if (StringUtils.isEmpty(address)) {
            address = ip + ":" + port;
        }
        return address;
    }

    public Class<?> getClazz() {
        if (Objects.isNull(clazz)) {
            try {
                clazz = Class.forName(serviceName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }
}
