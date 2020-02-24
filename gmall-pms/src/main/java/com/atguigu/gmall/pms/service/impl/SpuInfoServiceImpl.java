package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.SkuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao descDao;
    @Autowired
    private ProductAttrValueService attrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuPage(QueryCondition condition, Long cid) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (cid!=0){
            wrapper.eq("catalog_id",cid);
        }
        String key = condition.getKey();
        if (StringUtils.isNotBlank(key)){
            wrapper.and(t-> t.eq("id",key).or().like("spu_name",key));
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),wrapper

        );

        return new PageVo(page);
    }

    @GlobalTransactional
    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {
        //1.保存spu相关的3张表
        //1.1保存pms_spu_info信息
        Long spuId = saveSpuinfo(spuInfoVO);
        //1.2保存pms_spu_info_desc
        this.spuInfoDescService.saveSpuinfoDesc(spuInfoVO, spuId);
        //1.3保存pms_product_attr_value
        saveProductAttrValue(spuInfoVO, spuId);

        //2保存sku相关的3张表
        //2.1保存pms_sku_info
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if (CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.forEach(skuInfoVO -> {
            skuInfoVO.setSpuId(spuId);
            skuInfoVO.setSkuCode(UUID.randomUUID().toString());
            skuInfoVO.setBrandId(spuInfoVO.getBrandId());
            skuInfoVO.setCatalogId(spuInfoVO.getCatalogId());
            List<String> images = skuInfoVO.getImages();
            if (!CollectionUtils.isEmpty(images)){
                skuInfoVO.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoVO.getSkuDefaultImg())?skuInfoVO.getSkuDefaultImg():images.get(0));
            }
            this.skuInfoDao.insert(skuInfoVO);
            Long skuId = skuInfoVO.getSkuId();
            //2.2保存pms_sku_images
            if (!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntitys = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuInfoVO.getSkuDefaultImg(),image) ? 1 : 0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImagesEntitys);
            }
            //2.3保存pms_sale_attr_value
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(SkuSaleAttrValueEntity ->SkuSaleAttrValueEntity.setSkuId(skuId));
                this.saleAttrValueService.saveBatch(saleAttrs);
            }
            //3保存营销信息的三张表
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuInfoVO,skuSaleVo);
            skuSaleVo.setSkuId(skuInfoVO.getSkuId());
            this.gmallSmsClient.saveSale(skuSaleVo);

        });
    }



    private void saveProductAttrValue(SpuInfoVO spuInfoVO, Long spuId) {
        List<BaseAttrVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(baseAttrVO -> {
                ProductAttrValueEntity baseAttrVO1 = baseAttrVO;
                baseAttrVO1.setSpuId(spuId);
                return baseAttrVO1;
            }).collect(Collectors.toList());
            this.attrValueService.saveBatch(collect);
        }
    }

    private Long saveSpuinfo(SpuInfoVO spuInfoVO) {
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
        return spuInfoVO.getId();
    }

}