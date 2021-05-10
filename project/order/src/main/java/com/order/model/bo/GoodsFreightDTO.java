package com.order.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsFreightDTO implements Serializable {
    private Integer weight;
    private Long freightModelId;
    private Long shopId;
}
