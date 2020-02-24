package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wxz
 * @email 305326246@qq.com
 * @date 2020-01-26 12:25:21
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
