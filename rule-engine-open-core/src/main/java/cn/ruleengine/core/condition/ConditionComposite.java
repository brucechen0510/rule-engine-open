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
import cn.ruleengine.core.Input;
import cn.ruleengine.core.RuleEngineConfiguration;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 条件组合节点 - 包含多个子节点
 * 支持AND/OR逻辑运算符
 *
 * @author dingqianwen
 * @date 2024/1/19
 * @since 2.0.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionComposite extends ConditionNode {

    /**
     * 逻辑运算符：AND/OR
     */
    private String logicalOperator;

    /**
     * 子节点列表
     */
    private List<ConditionNode> children = new ArrayList<>();

    @Override
    public String getNodeType() {
        return NodeType.COMPOSITE;
    }

    @Override
    public void addChild(ConditionNode child) {
        Objects.requireNonNull(child, "子节点不能为空");
        child.setParentId(this.id);
        this.children.add(child);
        // 按orderNo排序
        this.children.sort((a, b) -> {
            if (a.getOrderNo() == null && b.getOrderNo() == null) return 0;
            if (a.getOrderNo() == null) return 1;
            if (b.getOrderNo() == null) return -1;
            return a.getOrderNo().compareTo(b.getOrderNo());
        });
    }

    @Override
    public void removeChild(ConditionNode child) {
        Objects.requireNonNull(child, "子节点不能为空");
        this.children.remove(child);
    }

    @Override
    public List<ConditionNode> getChildren() {
        return this.children;
    }

    /**
     * 条件组运算
     *
     * @param input         入参
     * @param configuration 引擎配置信息
     * @return 返回true时，条件成立
     */
    @Override
    public boolean compare(Input input, RuleEngineConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("开始执行组合条件节点: {}, 逻辑运算符: {}, 子节点数量: {}",
                    this.name, this.logicalOperator, this.children.size());
        }

        // 如果没有子节点，返回true
        if (CollUtil.isEmpty(this.children)) {
            if (log.isDebugEnabled()) {
                log.debug("组合节点 {} 无子节点，返回true", this.name);
            }
            return true;
        }

        // 根据逻辑运算符执行不同的逻辑
        boolean result = LogicalOperator.AND.equals(this.logicalOperator) ?
                executeAndLogic(input, configuration) :
                executeOrLogic(input, configuration);

        if (log.isDebugEnabled()) {
            log.debug("组合节点 {} 执行结果: {}", this.name, result);
        }

        return result;
    }

    /**
     * 执行AND逻辑：所有子节点都为true时返回true
     */
    private boolean executeAndLogic(Input input, RuleEngineConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("执行AND逻辑，需要所有子节点都为true");
        }

        for (int i = 0; i < this.children.size(); i++) {
            ConditionNode child = this.children.get(i);
            if (log.isDebugEnabled()) {
                log.debug("开始验证子节点[{}]: {}", i, child.getName());
            }

            if (!child.compare(input, configuration)) {
                if (log.isDebugEnabled()) {
                    log.debug("子节点[{}]: {} 不成立，AND逻辑失败", i, child.getName());
                }
                return false;
            }

            if (log.isDebugEnabled()) {
                log.debug("子节点[{}]: {} 成立", i, child.getName());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("所有子节点都成立，AND逻辑成功");
        }
        return true;
    }

    /**
     * 执行OR逻辑：任一子节点为true时返回true
     */
    private boolean executeOrLogic(Input input, RuleEngineConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("执行OR逻辑，只需要任一子节点为true");
        }

        for (int i = 0; i < this.children.size(); i++) {
            ConditionNode child = this.children.get(i);
            if (log.isDebugEnabled()) {
                log.debug("开始验证子节点[{}]: {}", i, child.getName());
            }

            if (child.compare(input, configuration)) {
                if (log.isDebugEnabled()) {
                    log.debug("子节点[{}]: {} 成立，OR逻辑成功", i, child.getName());
                }
                return true;
            }

            if (log.isDebugEnabled()) {
                log.debug("子节点[{}]: {} 不成立", i, child.getName());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("所有子节点都不成立，OR逻辑失败");
        }
        return false;
    }

    /**
     * 静态方法创建AND组合节点
     */
    public static ConditionComposite createAnd(@NonNull String name) {
        ConditionComposite composite = new ConditionComposite();
        composite.setName(name);
        composite.setLogicalOperator(LogicalOperator.AND);
        return composite;
    }

    /**
     * 静态方法创建OR组合节点
     */
    public static ConditionComposite createOr(@NonNull String name) {
        ConditionComposite composite = new ConditionComposite();
        composite.setName(name);
        composite.setLogicalOperator(LogicalOperator.OR);
        return composite;
    }
}