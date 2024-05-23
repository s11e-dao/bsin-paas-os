package me.flyray.bsin.server.biz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.flyray.bsin.domain.entity.SysRegion;
import me.flyray.bsin.domain.response.RegionTree;

/**
 * @author ：bolei
 * @date ：Created in 2022/3/29 13:53
 * @description：行政区域逻辑
 * @modified By：
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class RegionBiz {

    public List<RegionTree> getChildren(SysRegion root, Map<String, List<SysRegion>> groupedRegions) {
        List<RegionTree> children = groupedRegions.getOrDefault(root.getCode(), new ArrayList<>()).stream()
                .map(r -> new RegionTree(r.getRegionId(), r.getCode(), r.getName(), r.getParentCode(),
                        r.getLayer(), r.getSort(), r.getStatus(), r.getRemark(), r.getCreateTime(), r.getUpdateTime(),
                        getChildren(r, groupedRegions)))
                .sorted(Comparator.comparing(RegionTree::getSort))
                .collect(Collectors.toList());

        return children;
    }

}
