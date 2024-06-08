package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.IOException;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/26 11:05
 * @desc 具体的数字资产条目
 */
public interface DigitalAssetsItemService {

  /** 领取品牌商户发行的资产: 1.小程序端领取 2.扫码h5界面领取：需要anyweb code 3.task任务激励数字资产授予发放 */
  public Map<String, Object> claim(Map<String, Object> requestMap) throws Exception;

  /** 购买品牌商户发行的资产 */
  public Map<String, Object> buy(Map<String, Object> requestMap) throws IOException;

  /** 开盲盒 */
  public Map<String, Object> openBlindBox(Map<String, Object> requestMap) throws IOException;

  /** 领取NFT口令检查 */
  public Map<String, Object> obtainNftPasswordCheck(Map<String, Object> requestMap);

  /**
   * 查询数字资产列表（上架的资产）
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  Map<String, Object> getList(Map<String, Object> requestMap) throws Exception;

  /** 查询品牌商户发行的数字资产（上架的资产） */
  public IPage<?> getPageList(Map<String, Object> requestMap);

  /**
   * 查询商户发行的数字会员卡
   * @param requestMap
   * @return
   */
  public Map<String, Object> getPassCard(Map<String, Object> requestMap);

  /** 数字资产详情 */
  public Map<String, Object> getDetail(Map<String, Object> requestMap);

  /**
   * 查询数字资产领取码列表
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  Map<String, Object> getObtainCodePageList(Map<String, Object> requestMap) throws Exception;

  /** 数字资产权益配置 */
  public Map<String, Object> equityConfig(Map<String, Object> requestMap);

}
