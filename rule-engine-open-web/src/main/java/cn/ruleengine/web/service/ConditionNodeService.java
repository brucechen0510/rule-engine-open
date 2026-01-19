package cn.ruleengine.web.service;

import cn.ruleengine.core.condition.ConditionNode;
import cn.ruleengine.web.vo.condition.node.*;

/**
 * 条件节点服务接口
 *
 * @author dqw
 * @since 2024-01-19
 */
public interface ConditionNodeService {

    /**
     * 根据规则ID获取条件树
     *
     * @param ruleId 规则ID
     * @return 条件树VO
     */
    ConditionTreeResponse getConditionTree(Integer ruleId);

    /**
     * 保存条件树
     *
     * @param request 保存请求
     * @return 是否成功
     */
    Boolean saveConditionTree(SaveConditionTreeRequest request);

    /**
     * 创建条件节点
     *
     * @param request 创建请求
     * @return 节点ID
     */
    Integer createConditionNode(CreateConditionNodeRequest request);

    /**
     * 更新条件节点
     *
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateConditionNode(UpdateConditionNodeRequest request);

    /**
     * 删除条件节点
     *
     * @param request 删除请求
     * @return 是否成功
     */
    Boolean deleteConditionNode(DeleteConditionNodeRequest request);

    /**
     * 移动条件节点
     *
     * @param request 移动请求
     * @return 是否成功
     */
    Boolean moveConditionNode(MoveConditionNodeRequest request);

    /**
     * 执行条件测试
     *
     * @param request 测试请求
     * @return 测试结果
     */
    ConditionTestResponse testCondition(ConditionTestRequest request);

    /**
     * 获取条件树的核心逻辑对象（用于规则引擎执行）
     *
     * @param ruleId 规则ID
     * @return 条件节点
     */
    ConditionNode buildConditionTree(Integer ruleId);
}