package com.qfedu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qfedu.reggie.Mapper.OrdersDtoMapper;
import com.qfedu.reggie.dto.OrdersDto;
import com.qfedu.reggie.service.OrdersDtoService;
import org.springframework.stereotype.Service;

@Service
public class OrdersDtoServiceImpl extends ServiceImpl<OrdersDtoMapper,OrdersDto> implements OrdersDtoService {
}
