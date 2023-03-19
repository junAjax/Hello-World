package com.qfedu.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据处理器
 */

@Component
@Slf4j
public class MyMetaObjethandler  implements MetaObjectHandler {
    /**
     * 插入操作 自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());//创建时间
        metaObject.setValue("updateTime", LocalDateTime.now());//修改时间
        metaObject.setValue("createUser", BaseContext.getCurrentId());//创建人
        metaObject.setValue("updateUser", BaseContext.getCurrentId());//修改人





    }

    /**
     * 修改操作 自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为: {}",id);
        metaObject.setValue("updateTime", LocalDateTime.now());//修改时间
        metaObject.setValue("updateUser", BaseContext.getCurrentId());//修改人



    }
}
