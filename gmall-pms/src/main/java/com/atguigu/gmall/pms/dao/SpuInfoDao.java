package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu信息
 * 
 * @author wxz
 * @email 305326246@qq.com
 * @date 2020-01-26 12:25:21
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
    public boolean isnull(String ss);
	
}
