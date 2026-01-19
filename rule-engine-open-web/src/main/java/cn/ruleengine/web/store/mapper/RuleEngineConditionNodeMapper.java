package cn.ruleengine.web.store.mapper;

import cn.ruleengine.web.store.entity.RuleEngineConditionNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 条件节点Mapper接口
 *
 * @author dqw
 * @since 2024-01-19
 */
public interface RuleEngineConditionNodeMapper extends BaseMapper<RuleEngineConditionNode> {

    /**
     * 根据规则ID查询所有条件节点
     *
     * @param ruleId 规则ID
     * @return 条件节点列表
     */
    List<RuleEngineConditionNode> selectByRuleId(@Param("ruleId") Integer ruleId);

    /**
     * 根据父节点ID查询子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<RuleEngineConditionNode> selectByParentId(@Param("parentId") Integer parentId);

    /**
     * 查询规则的根节点
     *
     * @param ruleId 规则ID
     * @return 根节点
     */
    RuleEngineConditionNode selectRootByRuleId(@Param("ruleId") Integer ruleId);

    /**
     * 获取子节点的最大排序号
     *
     * @param parentId 父节点ID
     * @return 最大排序号
     */
    Integer selectMaxOrderNoByParentId(@Param("parentId") Integer parentId);

    /**
     * 批量插入条件节点
     *
     * @param nodes 条件节点列表
     * @return 插入数量
     */
    Integer batchInsert(@Param("nodes") List<RuleEngineConditionNode> nodes);

    /**
     * 根据规则ID删除所有条件节点
     *
     * @param ruleId 规则ID
     * @return 删除数量
     */
    Integer deleteByRuleId(@Param("ruleId") Integer ruleId);
}