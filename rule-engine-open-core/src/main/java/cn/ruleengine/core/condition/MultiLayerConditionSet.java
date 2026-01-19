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

import cn.ruleengine.core.Input;
import cn.ruleengine.core.RuleEngineConfiguration;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 多层条件集
 * 支持任意层级的条件结构，替代原有的ConditionSet
 *
 * @author dingqianwen
 * @date 2024/1/19
 * @since 2.0.0
 */
@ToString
@Slf4j
@Data
public class MultiLayerConditionSet implements ConditionCompare {

    /**
     * 条件树根节点
     */
    private ConditionNode rootNode;

    /**
     * 构造方法
     */
    public MultiLayerConditionSet(ConditionNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * 条件集运算，支持任意层级的且/或关系组合
     *
     * @param input         入参
     * @param configuration 引擎配置信息
     * @return 返回true时，条件成立
     */
    @Override
    public boolean compare(Input input, RuleEngineConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("开始执行多层条件集");
        }

        // 如果没有根节点，返回true
        if (this.rootNode == null) {
            if (log.isDebugEnabled()) {
                log.debug("条件树为空，返回true");
            }
            return true;
        }

        try {
            boolean result = this.rootNode.compare(input, configuration);

            if (log.isDebugEnabled()) {
                log.debug("多层条件集执行完成，结果: {}", result);
            }

            return result;
        } catch (Exception e) {
            log.error("多层条件集执行异常", e);
            throw new RuntimeException("条件执行异常: " + e.getMessage(), e);
        }
    }

    /**
     * 设置根节点
     */
    public void setRootNode(ConditionNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * 获取根节点
     */
    public ConditionNode getRootNode() {
        return this.rootNode;
    }

    /**
     * 静态工厂方法
     */
    public static MultiLayerConditionSet create(ConditionNode rootNode) {
        return new MultiLayerConditionSet(rootNode);
    }

    /**
     * 创建空的条件集
     */
    public static MultiLayerConditionSet createEmpty() {
        return new MultiLayerConditionSet(null);
    }
}