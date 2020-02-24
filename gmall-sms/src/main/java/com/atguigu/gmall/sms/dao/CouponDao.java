package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wxz
 * @email 305326246@qq.com
 * @date 2020-01-26 15:12:40
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
