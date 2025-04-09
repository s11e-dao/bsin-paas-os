package me.flyray.bsin.facade.service;


import jakarta.validation.Valid;
import me.flyray.bsin.domain.entity.SysApp;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;


import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.response.AppResp;
import me.flyray.bsin.domain.response.OrgResp;
import me.flyray.bsin.domain.response.OrgTree;
import me.flyray.bsin.validate.AddGroup;

@Validated
public interface OrgService {
    /**
     * 新增
     *
     * @return
     */
    @Validated(AddGroup.class)
    public SysOrg add(@Valid SysOrg sysOrg);

    /**
     * 删除
     */
    public SysOrg delete(String orgId);

    /**
     * 更新
     *
     * @return
     */
    @Validated(AddGroup.class)
    public SysOrg edit(@Valid SysOrg sysOrg);

    /**
     * 根据条件获取机构列表
     *
     * @return
     */
    public List<OrgResp> getList(String orgCode, String orgName);

    /**
     * 获取机构数
     */
    public List<OrgTree> getOrgTree();


    public List<SysOrg> getOrgListById(String orgId);

    /**
     * 根据租户id查询机构列表
     *
     * @return
     */
    public List<SysOrg> getListByTenantId();

    /**
     * 给部门授权应用
     *
     * @return
     */
    public void authorizationApps(String orgId, List<String> appIds);


    /**
     * 授权给商户应用
     * @return
     */
    public void authorizeMercahntOrgApps(String orgCode, List<String> appIds);

    /**
     * 分配岗位
     * @return
     */
    public void assignPost(String orgId, List<String> postIds);

    /**
     * 解除分配
     *
     * @return
     */
    public void unbindPost(String orgId, String postId);

    /**
     * 根据机构id查询已授权应用
     *
     * @return
     */
    public List<AppResp> getAppListByOrgId(String orgId);

    /**
     * 根据机构id查询已授权应用
     * @return
     */
    public List<AppResp> getAppListByOrgCode(Map<String, Object> requestMap);

    /**
     * 查询机构可授权应用
     */
    public List<SysApp> getAuthorizableAppListByOrgCode(Map<String, Object> requestMap);

    /**
     * 根据租户查询所有机构
     * @return
     */
    public List<OrgResp> getOrgsByTenantId(Map<String, Object> requestMap);

    /**
     * 根据机构id获取机构
     * @return
     */
    public SysOrg getOrg(Map<String, Object> requestMap);

    /**
     * 根据机构id获取机构
     *
     * @return
     */
    public SysOrg getDetail(String orgId);

    /**
     * 根据机构ids获取机构
     *
     * @return
     */
    public List<SysOrg> getOrgListByIds(List<String> orgIds);

    /**
     * 根据父级机构ids获取机构
     *
     * @return
     */
    public List<SysOrg> getOrgListByParentId(String parentId);



    List<SysOrg> getSubOrgList(String tenantId);

}
