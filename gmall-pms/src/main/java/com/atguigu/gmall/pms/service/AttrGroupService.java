package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author wxz
 * @email 305326246@qq.com
 * @date 2020-01-26 12:25:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryGroupByPage(QueryCondition condition, Long catId);

    GroupVO queryGroupWithAttrsByGid(Long gid);

    List<GroupVO> queryGroupWithAttrsByCid(Long catId);
}

