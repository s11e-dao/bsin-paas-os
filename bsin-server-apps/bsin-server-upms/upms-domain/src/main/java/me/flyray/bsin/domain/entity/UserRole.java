package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
@NoArgsConstructor
public class UserRole implements Serializable {

    /**
     * 用户id
     */
    @TableId
    private String userId;

    private String roleId;

    private String appId;
}
