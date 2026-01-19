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
import cn.ruleengine.core.exception.ConditionException;
import cn.ruleengine.core.value.Value;
import cn.ruleengine.core.value.ValueType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * 条件叶子节点 - 具体条件
 * 包含左值、运算符、右值的具体比较逻辑
 *
 * @author dingqianwen
 * @date 2024/1/19
 * @since 2.0.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionLeaf extends ConditionNode {

    /**
     * 条件左值
     */
    private Value leftValue;

    /**
     * 运算符
     */
    private Operator operator;

    /**
     * 条件右值
     */
    private Value rightValue;

    @Override
    public String getNodeType() {
        return NodeType.LEAF;
    }

    /**
     * 条件比较
     *
     * @param input         入参
     * @param configuration 引擎配置信息
     * @return 比较结果
     */
    @Override
    public boolean compare(Input input, RuleEngineConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("开始执行叶子条件节点: {}", this.name);
        }

        // 验证条件配置
        verify();

        // 执行条件比较逻辑
        Compare compare = ConditionCompareFactory.getCompare(this.leftValue.getValueType());
        Object lValue = this.leftValue.getValue(input, configuration);
        Object rValue = this.rightValue.getValue(input, configuration);

        if (log.isDebugEnabled()) {
            log.debug("条件比较：{} {} {} {}", this.name, lValue, this.operator, rValue);
        }

        boolean result = compare.compare(lValue, this.operator, rValue);

        if (log.isDebugEnabled()) {
            log.debug("条件 {} 执行结果: {}", this.name, result);
        }

        return result;
    }

    /**
     * 校验条件配置
     */
    private void verify() {
        if (Objects.isNull(this.operator)) {
            throw new ConditionException("条件:{}运算符不能为null", this.name);
        }
        if (Objects.isNull(this.leftValue)) {
            throw new ConditionException("条件:{}左值不能为null", this.name);
        }
        if (Objects.isNull(this.rightValue)) {
            throw new ConditionException("条件:{}右值不能为null", this.name);
        }
        // 左边类型必须等于右边类型
        if (!this.leftValue.getValueType().equals(ValueType.COLLECTION) &&
            !this.leftValue.getValueType().equals(this.rightValue.getValueType())) {
            throw new ConditionException("条件:{}左值类型与右值类型不匹配", this.name);
        }
        // 类型需要与运算符匹配
        List<Operator> symbol = this.leftValue.getValueType().getSymbol();
        if (!symbol.contains(this.operator)) {
            throw new ConditionException("条件:{}条件值与运算符不匹配", this.name);
        }
    }

    /**
     * 静态方法创建条件叶子节点
     */
    public static ConditionLeaf create(@NonNull String name,
                                     @NonNull Value leftValue,
                                     @NonNull Operator operator,
                                     @NonNull Value rightValue) {
        ConditionLeaf leaf = new ConditionLeaf();
        leaf.setName(name);
        leaf.setLeftValue(leftValue);
        leaf.setOperator(operator);
        leaf.setRightValue(rightValue);
        return leaf;
    }
}