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

    private String serviceName;

    private String ip;

    private Integer port;

    private String address;

    private Double weight;

    private Class<?> clazz;

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
