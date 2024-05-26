package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/22
 * @desc
 */

public interface TokenParamService {

    /**
     * 配置数字积分参数
     * 配置会产生一个提案信息
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 分页查询合约协议
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);


    /**
     * 查询商户数字积分信息
     */
    Map<String, Object> getDetailByMerchantNo(Map<String, Object> requestMap);


    /**
     * 查询数字积分详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

  /**
   * token释放执行任务
   * 1、定时任务触发
   * 2、曲线积分铸造触发
   * */
  public Map<String, Object> release(Map<String, Object> requestMap) throws Exception;

    /**
     * 商户捕获劳动价值联合曲线积分释放
     * 2、查询商户的数字积分tokenParm释放配置参数:触发释放条件，满足则释放分配
     * 3、？？查询分配客户群体（持有会员卡的用户参与分配）
     * 4、按照tokenParm参数兑换汇率释放支 虚拟余额账户(暂位铸造到链上)，待用户提现到链上时可以从此虚拟账户出账
     * @param requestMap
     * @return
     */
    public Map<String, Object> releaseBcPointToVirtualAccount(Map<String, Object> requestMap) throws Exception;

}
