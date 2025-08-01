/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.validate.AddGroup;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 角色表
 * </p>
 *
 * @author lengleng
 * @since 2019/2/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role")
public class SysRole implements Serializable {

	/**
	 * 雪花算法生成唯一id
	 */
	@TableId
	private String roleId;

	/**
	 * 角色名称
	 */
	@NotBlank(message = "roleName不能为空！", groups = AddGroup.class)
	private String roleName;

	/**
	 * 角色编码
	 */
	@NotBlank(message = "roleCode不能为空", groups = AddGroup.class)
	private String roleCode;

	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 应用id
	 */
	@NotBlank(message = "appId不能为空", groups = AddGroup.class)
	private String appId;

	/**
	 * 应用名称
	 */
//	@Transient
	private String appName;

	/**
	 * 创建者
	 */
	private String createBy;

	/**
	 * 租户ID
	 */
	private String tenantId;

	/**
	 * 商户的专属角色：解决商户和店铺权限问题
	 */
	private String orgId;

	/**
	 * 角色类型: 1、超级租户默认角色 2、授权应用默认角色 3、普通角色
	 */
	private Integer type;

	/**
	 * 创建时间
	 */
	@JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 更新者
	 */
	private String updateBy;

	/**
	 * 更新时间
	 */
	@JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	/**
	 * 删除标识（0-正常,1-删除）
	 */
	private String delFlag;

	public SysRole(String roleId, String roleName, String roleCode, String appId) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleCode = roleCode;
		this.appId = appId;
	}

	public SysRole(String roleId, String roleName, String roleCode, String appId, String tenantId, Integer type, String orgId) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleCode = roleCode;
		this.appId = appId;
		this.tenantId = tenantId;
		this.type = type;
		this.orgId = orgId;
	}
}
