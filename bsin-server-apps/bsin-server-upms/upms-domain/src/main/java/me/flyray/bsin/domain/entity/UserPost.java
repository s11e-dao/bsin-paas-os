package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_post")
@NoArgsConstructor
public class UserPost implements Serializable {

    private String postId;

    private String userId;
}
