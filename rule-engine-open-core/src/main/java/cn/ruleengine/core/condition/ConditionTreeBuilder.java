/*
 * Copyright (c) 2020 dingqianwen (761945125@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ruleengine.core.condition;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 条件树构建器
 * 用于从扁平的节点列表构建条件树结构
 *
 * @author dingqianwen
 * @date 2024/1/19
 * @since 2.0.0
 */
@Slf4j
public class ConditionTreeBuilder {

    /**
     * 从节点列表构建条件树
     *
     * @param nodes 扁平的节点列表
     * @return 根节点
     */
    public static ConditionNode buildTree(List<ConditionNodeDTO> nodes) {
        if (CollUtil.isEmpty(nodes)) {
            log.warn("节点列表为空，无法构建条件树");
            return null;
        }

        // 按父子关系分组
        Map<Integer, List<ConditionNodeDTO>> parentChildrenMap = nodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.getParentId() == null ? -1 : node.getParentId()));

        // 创建节点映射表
        Map<Integer, ConditionNode> nodeMap = new HashMap<>();

        // 找到根节点
        List<ConditionNodeDTO> rootNodes = parentChildrenMap.get(-1);
        if (CollUtil.isEmpty(rootNodes)) {
            throw new IllegalArgumentException("未找到根节点");
        }
        if (rootNodes.size() > 1) {
            throw new IllegalArgumentException("存在多个根节点");
        }

        ConditionNodeDTO rootDTO = rootNodes.get(0);
        ConditionNode root = createNode(rootDTO);
        nodeMap.put(rootDTO.getId(), root);

        // 递归构建树
        buildChildren(root, rootDTO.getId(), parentChildrenMap, nodeMap);

        return root;
    }

    /**
     * 递归构建子节点
     */
    private static void buildChildren(ConditionNode parent, Integer parentId,
                                      Map<Integer, List<ConditionNodeDTO>> parentChildrenMap,
                                      Map<Integer, ConditionNode> nodeMap) {
        List<ConditionNodeDTO> children = parentChildrenMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return;
        }

        // 按orderNo排序
        children.sort(Comparator.comparing(ConditionNodeDTO::getOrderNo,
                Comparator.nullsLast(Integer::compareTo)));

        for (ConditionNodeDTO childDTO : children) {
            ConditionNode child = createNode(childDTO);
            nodeMap.put(childDTO.getId(), child);

            // 添加到父节点
            if (parent.isComposite()) {
                parent.addChild(child);
            } else {
                throw new IllegalStateException("叶子节点不能有子节点: " + parent.getName());
            }

            // 递归构建子节点的子节点
            buildChildren(child, childDTO.getId(), parentChildrenMap, nodeMap);
        }
    }

    /**
     * 根据DTO创建具体的节点实例
     */
    private static ConditionNode createNode(ConditionNodeDTO dto) {
        if (ConditionNode.NodeType.LEAF.equals(dto.getNodeType())) {
            return createLeafNode(dto);
        } else if (ConditionNode.NodeType.COMPOSITE.equals(dto.getNodeType())) {
            return createCompositeNode(dto);
        } else {
            throw new IllegalArgumentException("未知的节点类型: " + dto.getNodeType());
        }
    }

    /**
     * 创建叶子节点
     */
    private static ConditionLeaf createLeafNode(ConditionNodeDTO dto) {
        ConditionLeaf leaf = new ConditionLeaf();
        leaf.setId(dto.getId());
        leaf.setName(dto.getConditionName());
        leaf.setDescription(dto.getConditionDescription());
        leaf.setParentId(dto.getParentId());
        leaf.setOrderNo(dto.getOrderNo());

        // 这里需要根据具体的Value实现来设置leftValue, operator, rightValue
        // 暂时留空，需要在实际使用时补充
        // leaf.setLeftValue(...);
        // leaf.setOperator(...);
        // leaf.setRightValue(...);

        return leaf;
    }

    /**
     * 创建组合节点
     */
    private static ConditionComposite createCompositeNode(ConditionNodeDTO dto) {
        ConditionComposite composite = new ConditionComposite();
        composite.setId(dto.getId());
        composite.setName(dto.getGroupName());
        composite.setDescription(dto.getGroupDescription());
        composite.setParentId(dto.getParentId());
        composite.setOrderNo(dto.getOrderNo());
        composite.setLogicalOperator(dto.getLogicalOperator());

        return composite;
    }

    /**
     * 将条件树转换为扁平的节点列表（用于持久化）
     *
     * @param root 根节点
     * @return 扁平的节点列表
     */
    public static List<ConditionNodeDTO> flattenTree(ConditionNode root) {
        if (root == null) {
            return Collections.emptyList();
        }

        List<ConditionNodeDTO> result = new ArrayList<>();
        flattenNode(root, result);
        return result;
    }

    /**
     * 递归扁平化节点
     */
    private static void flattenNode(ConditionNode node, List<ConditionNodeDTO> result) {
        ConditionNodeDTO dto = convertToDTO(node);
        result.add(dto);

        if (node.isComposite()) {
            for (ConditionNode child : node.getChildren()) {
                flattenNode(child, result);
            }
        }
    }

    /**
     * 将节点转换为DTO
     */
    private static ConditionNodeDTO convertToDTO(ConditionNode node) {
        ConditionNodeDTO dto = new ConditionNodeDTO();
        dto.setId(node.getId());
        dto.setParentId(node.getParentId());
        dto.setNodeType(node.getNodeType());
        dto.setOrderNo(node.getOrderNo());

        if (node.isLeaf()) {
            ConditionLeaf leaf = (ConditionLeaf) node;
            dto.setConditionName(leaf.getName());
            dto.setConditionDescription(leaf.getDescription());
            // 设置具体条件的属性
            // dto.setLeftType(...);
            // dto.setLeftValue(...);
            // ... 其他属性
        } else {
            ConditionComposite composite = (ConditionComposite) node;
            dto.setGroupName(composite.getName());
            dto.setGroupDescription(composite.getDescription());
            dto.setLogicalOperator(composite.getLogicalOperator());
        }

        return dto;
    }
}