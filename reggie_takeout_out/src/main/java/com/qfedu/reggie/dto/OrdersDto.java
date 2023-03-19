package com.qfedu.reggie.dto;


import com.qfedu.reggie.entity.OrderDetail;
import com.qfedu.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {

    //    private String userName;
//
    private int sumNum;
    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

    private String userName;

}
