package me.flyray.bsin.domain.response;

import me.flyray.bsin.domain.entity.SysApp;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author bolei
 * @date 2024/1/27
 * @desc
 */

@Data
public class AppListResp implements Serializable {

    List<SysApp> apps;

    public SysApp defaultApp;

}
