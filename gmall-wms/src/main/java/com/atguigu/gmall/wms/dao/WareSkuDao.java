package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author wxz
 * @email 305326246@qq.com
 * @date 2020-02-05 18:00:15
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
