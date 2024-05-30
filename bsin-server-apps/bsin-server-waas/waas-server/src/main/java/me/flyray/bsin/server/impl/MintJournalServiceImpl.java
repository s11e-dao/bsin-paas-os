package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.MintJournal;
import me.flyray.bsin.facade.service.MintJournalService;
import me.flyray.bsin.infrastructure.mapper.MintJournalMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/8/13
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/mintJournal", timeout = 6000)
@ApiModule(value = "mintJournal")
@Service
public class MintJournalServiceImpl implements MintJournalService {

  @Autowired private MintJournalMapper mintJournalMapper;

  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    MintJournal mintJournal = BsinServiceContext.getReqBodyDto(MintJournal.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<MintJournal> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<MintJournal> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(MintJournal::getCreateTime);
    warapper.eq(MintJournal::getTenantId, loginUser.getTenantId());
    warapper.eq(MintJournal::getMerchantNo, loginUser.getMerchantNo());
    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getTokenId()),
        MintJournal::getTokenId,
        mintJournal.getTokenId());
    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getTxHash()),
        MintJournal::getTxHash,
        mintJournal.getTxHash());
    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getDigitalAssetsCollectionNo()),
        MintJournal::getDigitalAssetsCollectionNo,
        mintJournal.getDigitalAssetsCollectionNo());

    warapper.eq(
        ObjectUtil.isNotNull(mintJournal.getAssetsType()),
        MintJournal::getAssetsType,
        mintJournal.getAssetsType());

    IPage<MintJournal> pageList = mintJournalMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    MintJournal mintJournal = mintJournalMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(mintJournal);
  }
}
