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

import java.util.List;

/**
 * 多层条件节点抽象基类
 * 使用组合模式支持任意层级的条件结构
 *
 * @author dingqianwen
 * @date 2024/1/19
 * @since 2.0.0
 */
@Data
public abstract class ConditionNode implements ConditionCompare {

    /**
     * 节点ID
     */
    protected Integer id;

    /**
     * 节点名称
     */
    protected String name;

    /**
     * 节点描述
     */
    protected String description;

    /**
     * 父节点ID
     */
    protected Integer parentId;

    /**
     * 排序号
     */
    protected Integer orderNo;

    /**
     * 节点类型常量
     */
    public static class NodeType {
        public static final String LEAF = "LEAF";           // 叶子节点（具体条件）
        public static final String COMPOSITE = "COMPOSITE"; // 组合节点（条件组）
    }

    /**
     * 逻辑运算符常量
     */
    public static class LogicalOperator {
        public static final String AND = "AND"; // 与
        public static final String OR = "OR";   // 或
    }

    /**
     * 获取节点类型
     */
    public abstract String getNodeType();

    /**
     * 添加子节点（仅组合节点有效）
     */
    public void addChild(ConditionNode child) {
        throw new UnsupportedOperationException("This node type does not support adding children");
    }

    /**
     * 移除子节点（仅组合节点有效）
     */
    public void removeChild(ConditionNode child) {
        throw new UnsupportedOperationException("This node type does not support removing children");
    }

    /**
     * 获取子节点列表（仅组合节点有效）
     */
    public List<ConditionNode> getChildren() {
        throw new UnsupportedOperationException("This node type does not have children");
    }

    /**
     * 是否为叶子节点
     */
    public boolean isLeaf() {
        return NodeType.LEAF.equals(getNodeType());
    }

    /**
     * 是否为组合节点
     */
    public boolean isComposite() {
        return NodeType.COMPOSITE.equals(getNodeType());
    }
}