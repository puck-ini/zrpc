package com.zchzh.demoapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author zengchzh
 * @date 2021/9/25
 */

@Data
@AllArgsConstructor
@Builder
public class TestDTO implements Serializable {

    private static final long serialVersionUID = -3610065709589867551L;

    private Long id;

    private String msg;

    private LocalDateTime createTime;

    private Date updateTime;

    private Double num;

    private BigDecimal money;

    private List<MsgDTO> dtoList;

    private MsgDTO[] dtoArray;

    public TestDTO() {
        Random random = new Random();
        this.id = random.nextLong();
        this.msg = "msg: " + id;
        this.createTime = LocalDateTime.now();
        this.updateTime = new Date();
        this.num = random.nextDouble();
        this.money = new BigDecimal(this.num);
        int loop = random.nextInt(1000);
        dtoList = new ArrayList<>(loop);
        dtoArray = new MsgDTO[loop];
        for (int i = 0; i < loop; i++) {
            dtoList.add(new MsgDTO(i));
            dtoArray[i] = new MsgDTO(i);
        }
    }

}
