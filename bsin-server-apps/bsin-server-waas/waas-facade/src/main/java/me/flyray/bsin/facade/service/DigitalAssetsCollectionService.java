package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/26 13:36
 * @desc 数字资产集
 */
public interface DigitalAssetsCollectionService {

  /**
   * 发行资产（部署合约）
   *
   * @param requestMap 请求参数
   * @return 发行资产
   */
  Map<String, Object> issue(Map<String, Object> requestMap) throws Exception;

  /**
   * mintNft 铸造数字资产
   *
   * @param requestMap 请求参数
   * @return 铸造结果
   */
  Map<String, Object> mint(Map<String, Object> requestMap) throws Exception;

  /**
   * trasaction 转让
   *
   * @param requestMap 请求参数
   * @return 转赠结果
   */
  Map<String, Object> transfer(Map<String, Object> requestMap) throws Exception;

  /**
   * 空投
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  Map<String, Object> airdrop(Map<String, Object> requestMap) throws Exception;

  /**
   * batchTrasactionNft 批量转让NFT
   *
   * @param requestMap 请求参数
   * @return 转赠结果
   */
  Map<String, Object> batchTransfer(Map<String, Object> requestMap) throws Exception;

  /**
   * settingSponsor 设置代付
   *
   * @param requestMap 请求参数
   * @return 设置结果
   */
  Map<String, Object> getSponsor(Map<String, Object> requestMap) throws Exception;

  /**
   * setSponsor 设置代付
   *
   * @param requestMap 请求参数
   * @return 设置结果
   */
  Map<String, Object> setSponsor(Map<String, Object> requestMap) throws Exception;

  /**
   * isWhitelisted 查询白名单
   *
   * @param requestMap 请求参数
   * @return 设置结果
   */
  Map<String, Object> isWhitelisted(Map<String, Object> requestMap) throws Exception;

  /**
   * addWhiteList 添加代付白名单
   *
   * @param requestMap 请求参数
   * @return 设置结果
   */
  Map<String, Object> addWhiteList(Map<String, Object> requestMap) throws Exception;

  /**
   * removeWhiteList 移除代付白名单
   *
   * @param requestMap 请求参数
   * @return 设置结果
   */
  Map<String, Object> removeWhiteList(Map<String, Object> requestMap) throws Exception;

  /**
   * NFT 销毁
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  Map<String, Object> burn(Map<String, Object> requestMap) throws Exception;

  /** 租户下所有合约协议 */
  public Map<String, Object> getList(Map<String, Object> requestMap);

  /**
   * 查询数字资产列表
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  Map<String, Object> getPageList(Map<String, Object> requestMap) throws Exception;

  /** 查询合约协议详情 */
  public Map<String, Object> getDetail(Map<String, Object> requestMap);

  /** NFT上架到市场交易 成为数字商品交易 */
  public Map<String, Object> putOnShelves(Map<String, Object> requestMap);


  /** NFT下架 */
  public Map<String, Object> pullOffShelves(Map<String, Object> requestMap);

  /** 获取数字资产元数据图片信息 */
  public Map<String, Object> getDigitalAssetsMetadataImageInfo(Map<String, Object> requestMap);

}
