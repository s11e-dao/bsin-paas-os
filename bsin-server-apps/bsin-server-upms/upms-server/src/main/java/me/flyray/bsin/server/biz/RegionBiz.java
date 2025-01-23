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

    // 构建区域树
    public List<RegionTree> buildRegionTree(List<SysRegion> regions) {
        // 按 code 映射所有节点
        Map<String, RegionTree> nodeMap = regions.stream()
                .collect(Collectors.toMap(SysRegion::getCode, r -> new RegionTree(
                        r.getRegionId(), r.getCode(), r.getName(), r.getParentCode(),
                        r.getLayer(), r.getSort(), r.getStatus(), r.getRemark(),
                        r.getCreateTime(), r.getUpdateTime(), new ArrayList<>()
                )));

        // 构建树的根节点列表
        List<RegionTree> rootList = new ArrayList<>();

        // 遍历所有节点，将其放到父节点的 children 中
        for (RegionTree node : nodeMap.values()) {
            if ("0".equals(node.getParentCode()) || !nodeMap.containsKey(node.getParentCode())) {
                // 没有父节点的，作为根节点
                rootList.add(node);
            } else {
                // 有父节点的，加入父节点的 children
                RegionTree parent = nodeMap.get(node.getParentCode());
                parent.getChildren().add(node);
            }
        }

        // 对根节点及其子节点按排序规则排序
        rootList.sort(Comparator.comparing(RegionTree::getSort));
        sortChildren(rootList);

        return rootList;
    }

    // 递归对子节点进行排序
    private void sortChildren(List<RegionTree> rootNodes) {
        for (RegionTree rootNode : rootNodes) {
            if (rootNode.getChildren() != null && !rootNode.getChildren().isEmpty()) {
                rootNode.getChildren().sort(Comparator.comparing(RegionTree::getSort));
                sortChildren(rootNode.getChildren());
            }
        }
    }

}
