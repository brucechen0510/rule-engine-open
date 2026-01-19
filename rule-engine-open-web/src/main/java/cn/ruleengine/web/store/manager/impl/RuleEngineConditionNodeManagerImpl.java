package cn.ruleengine.web.store.manager.impl;

import cn.ruleengine.web.store.entity.RuleEngineConditionNode;
import cn.ruleengine.web.store.manager.RuleEngineConditionNodeManager;
import cn.ruleengine.web.store.mapper.RuleEngineConditionNodeMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 条件节点Manager实现类
 *
 * @author dqw
 * @since 2024-01-19
 */
@Service
public class RuleEngineConditionNodeManagerImpl extends ServiceImpl<RuleEngineConditionNodeMapper, RuleEngineConditionNode>
        implements RuleEngineConditionNodeManager {

    @Resource
    private RuleEngineConditionNodeMapper conditionNodeMapper;

    @Override
    public List<RuleEngineConditionNode> listByRuleId(Integer ruleId) {
        return conditionNodeMapper.selectByRuleId(ruleId);
    }

    @Override
    public List<RuleEngineConditionNode> listByParentId(Integer parentId) {
        return conditionNodeMapper.selectByParentId(parentId);
    }

    @Override
    public RuleEngineConditionNode getRootByRuleId(Integer ruleId) {
        return conditionNodeMapper.selectRootByRuleId(ruleId);
    }

    @Override
    public boolean save(RuleEngineConditionNode node) {
        return this.save(node);
    }

    @Override
    public boolean update(RuleEngineConditionNode node) {
        return this.updateById(node);
    }

    @Override
    public boolean removeById(Integer id) {
        return this.removeById(id);
    }

    @Override
    public boolean removeByRuleId(Integer ruleId) {
        return conditionNodeMapper.deleteByRuleId(ruleId) > 0;
    }

    @Override
    public boolean saveBatch(List<RuleEngineConditionNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return true;
        }
        return conditionNodeMapper.batchInsert(nodes) > 0;
    }

    @Override
    public Integer getMaxOrderNoByParentId(Integer parentId) {
        return conditionNodeMapper.selectMaxOrderNoByParentId(parentId);
    }
}