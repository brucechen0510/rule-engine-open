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

import lombok.Data;

/**
 * 条件节点传输对象
 * 用于在不同层之间传递条件节点数据
 *
 * @author dingqianwen
 * @date 2024/1/19
 * @since 2.0.0
 */
@Data
public class ConditionNodeDTO {

    /**
     * 节点ID
     */
    private Integer id;

    /**
     * 规则ID
     */
    private Integer ruleId;

    /**
     * 父节点ID
     */
    private Integer parentId;

    /**
     * 节点类型：LEAF/COMPOSITE
     */
    private String nodeType;

    /**
     * 逻辑运算符：AND/OR
     */
    private String logicalOperator;

    /**
     * 排序号
     */
    private Integer orderNo;

    // ========== 叶子节点字段 ==========

    /**
     * 条件名称
     */
    private String conditionName;

    /**
     * 条件描述
     */
    private String conditionDescription;

    /**
     * 左值类型
     */
    private Integer leftType;

    /**
     * 左值
     */
    private String leftValue;

    /**
     * 左值数据类型
     */
    private String leftValueType;

    /**
     * 运算符
     */
    private String symbol;

    /**
     * 右值类型
     */
    private Integer rightType;

    /**
     * 右值
     */
    private String rightValue;

    /**
     * 右值数据类型
     */
    private String rightValueType;

    // ========== 组合节点字段 ==========

    /**
     * 条件组名称
     */
    private String groupName;

    /**
     * 条件组描述
     */
    private String groupDescription;

    // ========== 通用字段 ==========

    private Integer workspaceId;
    private Integer createUserId;
    private String createUserName;
}