package cn.ruleengine.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.ruleengine.core.Input;
import cn.ruleengine.core.RuleEngineConfiguration;
import cn.ruleengine.core.condition.*;
import cn.ruleengine.core.value.InputParameter;
import cn.ruleengine.core.value.Value;
import cn.ruleengine.web.service.ConditionNodeService;
import cn.ruleengine.web.service.VariableService;
import cn.ruleengine.web.store.entity.RuleEngineConditionNode;
import cn.ruleengine.web.store.manager.RuleEngineConditionNodeManager;
import cn.ruleengine.web.vo.condition.node.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 条件节点服务实现类
 *
 * @author dqw
 * @since 2024-01-19
 */
@Slf4j
@Service
public class ConditionNodeServiceImpl implements ConditionNodeService {

    @Resource
    private RuleEngineConditionNodeManager conditionNodeManager;

    @Resource
    private VariableService variableService;

    @Override
    public ConditionTreeResponse getConditionTree(Integer ruleId) {
        List<RuleEngineConditionNode> nodes = conditionNodeManager.listByRuleId(ruleId);
        if (CollUtil.isEmpty(nodes)) {
            return null;
        }

        ConditionTreeResponse response = new ConditionTreeResponse();
        response.setRuleId(ruleId);
        response.setRootNode(buildTreeFromNodes(nodes));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveConditionTree(SaveConditionTreeRequest request) {
        try {
            // 先删除原有的条件节点
            conditionNodeManager.removeByRuleId(request.getRuleId());

            // 将树结构转换为扁平的节点列表
            List<RuleEngineConditionNode> nodes = flattenTreeToNodes(
                    request.getRootNode(), request.getRuleId(), null);

            // 批量保存
            return conditionNodeManager.saveBatch(nodes);
        } catch (Exception e) {
            log.error("保存条件树失败", e);
            throw new RuntimeException("保存条件树失败: " + e.getMessage());
        }
    }

    @Override
    public Integer createConditionNode(CreateConditionNodeRequest request) {
        RuleEngineConditionNode node = new RuleEngineConditionNode();

        // 设置基础属性
        node.setRuleId(request.getRuleId());
        node.setParentId(request.getParentId());
        node.setNodeType(request.getNodeType());
        node.setLogicalOperator(request.getLogicalOperator());

        // 设置排序号
        if (request.getOrderNo() != null) {
            node.setOrderNo(request.getOrderNo());
        } else {
            Integer maxOrderNo = conditionNodeManager.getMaxOrderNoByParentId(request.getParentId());
            node.setOrderNo(maxOrderNo + 1);
        }

        // 根据节点类型设置不同的属性
        if (RuleEngineConditionNode.NodeType.LEAF.equals(request.getNodeType())) {
            // 叶子节点
            node.setConditionName(request.getConditionName());
            node.setConditionDescription(request.getConditionDescription());

            if (request.getConfig() != null) {
                setConditionConfig(node, request.getConfig());
            }
        } else {
            // 组合节点
            node.setGroupName(request.getGroupName());
            node.setGroupDescription(request.getGroupDescription());
        }

        conditionNodeManager.save(node);
        return node.getId();
    }

    @Override
    public Boolean updateConditionNode(UpdateConditionNodeRequest request) {
        RuleEngineConditionNode node = conditionNodeManager.getRootByRuleId(request.getId());
        if (node == null) {
            throw new RuntimeException("条件节点不存在");
        }

        // 更新基础属性
        if (StrUtil.isNotBlank(request.getLogicalOperator())) {
            node.setLogicalOperator(request.getLogicalOperator());
        }

        // 根据节点类型更新不同的属性
        if (node.isLeaf()) {
            // 叶子节点
            if (StrUtil.isNotBlank(request.getConditionName())) {
                node.setConditionName(request.getConditionName());
            }
            if (StrUtil.isNotBlank(request.getConditionDescription())) {
                node.setConditionDescription(request.getConditionDescription());
            }

            if (request.getConfig() != null) {
                setConditionConfig(node, request.getConfig());
            }
        } else {
            // 组合节点
            if (StrUtil.isNotBlank(request.getGroupName())) {
                node.setGroupName(request.getGroupName());
            }
            if (StrUtil.isNotBlank(request.getGroupDescription())) {
                node.setGroupDescription(request.getGroupDescription());
            }
        }

        return conditionNodeManager.update(node);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConditionNode(DeleteConditionNodeRequest request) {
        // 删除节点及其所有子节点
        return deleteNodeRecursively(request.getId());
    }

    @Override
    public Boolean moveConditionNode(MoveConditionNodeRequest request) {
        RuleEngineConditionNode node = conditionNodeManager.getRootByRuleId(request.getId());
        if (node == null) {
            throw new RuntimeException("条件节点不存在");
        }

        node.setParentId(request.getNewParentId());
        node.setOrderNo(request.getNewOrderNo());

        return conditionNodeManager.update(node);
    }

    @Override
    public ConditionTestResponse testCondition(ConditionTestRequest request) {
        ConditionTestResponse response = new ConditionTestResponse();
        List<ConditionTestResponse.ExecutionLog> logs = new ArrayList<>();

        try {
            long startTime = System.currentTimeMillis();

            // 构建条件树
            ConditionNode conditionTree = buildConditionTree(request.getRuleId());
            if (conditionTree == null) {
                response.setResult(true);
                response.setExecutionTime(0L);
                response.setLogs(logs);
                return response;
            }

            // 构建输入参数
            InputParameter input = new InputParameter();
            if (request.getParams() != null) {
                request.getParams().forEach(input::put);
            }

            // 执行条件测试
            RuleEngineConfiguration config = new RuleEngineConfiguration();
            boolean result = conditionTree.compare(input, config);

            long endTime = System.currentTimeMillis();

            response.setResult(result);
            response.setExecutionTime(endTime - startTime);
            response.setLogs(logs);

        } catch (Exception e) {
            log.error("条件测试失败", e);
            response.setResult(false);
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public ConditionNode buildConditionTree(Integer ruleId) {
        List<RuleEngineConditionNode> nodes = conditionNodeManager.listByRuleId(ruleId);
        if (CollUtil.isEmpty(nodes)) {
            return null;
        }

        // 转换为DTO
        List<ConditionNodeDTO> dtos = nodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建条件树
        return ConditionTreeBuilder.buildTree(dtos);
    }

    // ========== 私有方法 ==========

    /**
     * 从节点列表构建树结构
     */
    private ConditionTreeResponse.ConditionNodeVO buildTreeFromNodes(List<RuleEngineConditionNode> nodes) {
        // 找到根节点
        RuleEngineConditionNode root = nodes.stream()
                .filter(RuleEngineConditionNode::isRoot)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到根节点"));

        return buildNodeVO(root, nodes);
    }

    /**
     * 递归构建节点VO
     */
    private ConditionTreeResponse.ConditionNodeVO buildNodeVO(RuleEngineConditionNode node,
                                                            List<RuleEngineConditionNode> allNodes) {
        ConditionTreeResponse.ConditionNodeVO vo = new ConditionTreeResponse.ConditionNodeVO();

        vo.setId(node.getId());
        vo.setParentId(node.getParentId());
        vo.setNodeType(node.getNodeType());
        vo.setLogicalOperator(node.getLogicalOperator());
        vo.setOrderNo(node.getOrderNo());

        if (node.isLeaf()) {
            // 叶子节点
            vo.setConditionName(node.getConditionName());
            vo.setConditionDescription(node.getConditionDescription());
            vo.setConfig(buildConditionConfig(node));
        } else {
            // 组合节点
            vo.setGroupName(node.getGroupName());
            vo.setGroupDescription(node.getGroupDescription());

            // 构建子节点
            List<RuleEngineConditionNode> children = allNodes.stream()
                    .filter(n -> Objects.equals(n.getParentId(), node.getId()))
                    .sorted(Comparator.comparing(RuleEngineConditionNode::getOrderNo,
                            Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());

            List<ConditionTreeResponse.ConditionNodeVO> childVOs = children.stream()
                    .map(child -> buildNodeVO(child, allNodes))
                    .collect(Collectors.toList());

            vo.setChildren(childVOs);
        }

        return vo;
    }

    /**
     * 构建条件配置
     */
    private ConditionTreeResponse.ConditionNodeVO.ConditionConfig buildConditionConfig(RuleEngineConditionNode node) {
        ConditionTreeResponse.ConditionNodeVO.ConditionConfig config =
                new ConditionTreeResponse.ConditionNodeVO.ConditionConfig();

        // 构建左值
        ConditionTreeResponse.ConditionNodeVO.ConditionConfig.ValueConfig leftValue =
                new ConditionTreeResponse.ConditionNodeVO.ConditionConfig.ValueConfig();
        leftValue.setType(node.getLeftType());
        leftValue.setValueType(node.getLeftValueType());
        leftValue.setValue(node.getLeftValue());
        config.setLeftValue(leftValue);

        // 运算符
        config.setSymbol(node.getSymbol());

        // 构建右值
        ConditionTreeResponse.ConditionNodeVO.ConditionConfig.ValueConfig rightValue =
                new ConditionTreeResponse.ConditionNodeVO.ConditionConfig.ValueConfig();
        rightValue.setType(node.getRightType());
        rightValue.setValueType(node.getRightValueType());
        rightValue.setValue(node.getRightValue());
        config.setRightValue(rightValue);

        return config;
    }

    /**
     * 将树结构扁平化为节点列表
     */
    private List<RuleEngineConditionNode> flattenTreeToNodes(
            ConditionTreeResponse.ConditionNodeVO nodeVO, Integer ruleId, Integer parentId) {
        List<RuleEngineConditionNode> nodes = new ArrayList<>();

        RuleEngineConditionNode node = new RuleEngineConditionNode();
        node.setRuleId(ruleId);
        node.setParentId(parentId);
        node.setNodeType(nodeVO.getNodeType());
        node.setLogicalOperator(nodeVO.getLogicalOperator());
        node.setOrderNo(nodeVO.getOrderNo());

        if (RuleEngineConditionNode.NodeType.LEAF.equals(nodeVO.getNodeType())) {
            // 叶子节点
            node.setConditionName(nodeVO.getConditionName());
            node.setConditionDescription(nodeVO.getConditionDescription());

            if (nodeVO.getConfig() != null) {
                setConditionConfig(node, nodeVO.getConfig());
            }
        } else {
            // 组合节点
            node.setGroupName(nodeVO.getGroupName());
            node.setGroupDescription(nodeVO.getGroupDescription());
        }

        nodes.add(node);

        // 递归处理子节点
        if (CollUtil.isNotEmpty(nodeVO.getChildren())) {
            for (ConditionTreeResponse.ConditionNodeVO child : nodeVO.getChildren()) {
                nodes.addAll(flattenTreeToNodes(child, ruleId, node.getId()));
            }
        }

        return nodes;
    }

    /**
     * 设置条件配置
     */
    private void setConditionConfig(RuleEngineConditionNode node,
                                  ConditionTreeResponse.ConditionNodeVO.ConditionConfig config) {
        if (config.getLeftValue() != null) {
            node.setLeftType(config.getLeftValue().getType());
            node.setLeftValue(config.getLeftValue().getValue());
            node.setLeftValueType(config.getLeftValue().getValueType());
        }

        node.setSymbol(config.getSymbol());

        if (config.getRightValue() != null) {
            node.setRightType(config.getRightValue().getType());
            node.setRightValue(config.getRightValue().getValue());
            node.setRightValueType(config.getRightValue().getValueType());
        }
    }

    /**
     * 递归删除节点
     */
    private Boolean deleteNodeRecursively(Integer nodeId) {
        // 先删除子节点
        List<RuleEngineConditionNode> children = conditionNodeManager.listByParentId(nodeId);
        for (RuleEngineConditionNode child : children) {
            deleteNodeRecursively(child.getId());
        }

        // 再删除自己
        return conditionNodeManager.removeById(nodeId);
    }

    /**
     * 将实体转换为DTO
     */
    private ConditionNodeDTO convertToDTO(RuleEngineConditionNode node) {
        ConditionNodeDTO dto = new ConditionNodeDTO();

        dto.setId(node.getId());
        dto.setRuleId(node.getRuleId());
        dto.setParentId(node.getParentId());
        dto.setNodeType(node.getNodeType());
        dto.setLogicalOperator(node.getLogicalOperator());
        dto.setOrderNo(node.getOrderNo());

        if (node.isLeaf()) {
            dto.setConditionName(node.getConditionName());
            dto.setConditionDescription(node.getConditionDescription());
            dto.setLeftType(node.getLeftType());
            dto.setLeftValue(node.getLeftValue());
            dto.setLeftValueType(node.getLeftValueType());
            dto.setSymbol(node.getSymbol());
            dto.setRightType(node.getRightType());
            dto.setRightValue(node.getRightValue());
            dto.setRightValueType(node.getRightValueType());
        } else {
            dto.setGroupName(node.getGroupName());
            dto.setGroupDescription(node.getGroupDescription());
        }

        dto.setWorkspaceId(node.getWorkspaceId());
        dto.setCreateUserId(node.getCreateUserId());
        dto.setCreateUserName(node.getCreateUserName());

        return dto;
    }
}