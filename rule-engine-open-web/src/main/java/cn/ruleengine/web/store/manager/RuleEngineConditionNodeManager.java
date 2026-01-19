package cn.ruleengine.web.store.manager;

import cn.ruleengine.web.store.entity.RuleEngineConditionNode;

import java.util.List;

/**
 * 条件节点Manager接口
 *
 * @author dqw
 * @since 2024-01-19
 */
public interface RuleEngineConditionNodeManager {

    /**
     * 根据规则ID查询所有条件节点
     *
     * @param ruleId 规则ID
     * @return 条件节点列表
     */
    List<RuleEngineConditionNode> listByRuleId(Integer ruleId);

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<RuleEngineConditionNode> listByParentId(Integer parentId);

    /**
     * 查询规则的根节点
     *
     * @param ruleId 规则ID
     * @return 根节点
     */
    RuleEngineConditionNode getRootByRuleId(Integer ruleId);

    /**
     * 保存条件节点
     *
     * @param node 条件节点
     * @return 是否成功
     */
    boolean save(RuleEngineConditionNode node);

    /**
     * 更新条件节点
     *
     * @param node 条件节点
     * @return 是否成功
     */
    boolean update(RuleEngineConditionNode node);

    /**
     * 根据ID删除条件节点
     *
     * @param id 节点ID
     * @return 是否成功
     */
    boolean removeById(Integer id);

    /**
     * 根据规则ID删除所有条件节点
     *
     * @param ruleId 规则ID
     * @return 是否成功
     */
    boolean removeByRuleId(Integer ruleId);

    /**
     * 批量保存条件节点
     *
     * @param nodes 条件节点列表
     * @return 是否成功
     */
    boolean saveBatch(List<RuleEngineConditionNode> nodes);

    /**
     * 获取子节点的最大排序号
     *
     * @param parentId 父节点ID
     * @return 最大排序号
     */
    Integer getMaxOrderNoByParentId(Integer parentId);
}