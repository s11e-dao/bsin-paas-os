package me.flyray.bsin.domain.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrgTree implements Serializable {
    /**
     * 机构id
     */
    private String orgId;

    /**
     * 机构编码
     */
    private String orgCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 机构级别
     */
    private Integer level;

    /**
     * 机构类型 1、公司 2、部门 3、小组 4、其他
     */
    private Integer type;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 办公电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 描述
     */
    private String remark;

    /**
     * 部门人数
     */
    @TableField(exist = false)
    private Integer count;

    /**
     * 机构子集
     */
    List<OrgTree> children;
}
