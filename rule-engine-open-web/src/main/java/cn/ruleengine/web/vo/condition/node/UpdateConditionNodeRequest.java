package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 更新条件节点请求VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class UpdateConditionNodeRequest {

    /**
     * 节点ID
     */
    @NotNull(message = "节点ID不能为空")
    private Integer id;

    /**
     * 逻辑运算符：AND/OR（仅组合节点）
     */
    private String logicalOperator;

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
     * 条件配置
     */
    @Valid
    private ConditionTreeResponse.ConditionNodeVO.ConditionConfig config;

    // ========== 组合节点字段 ==========

    /**
     * 条件组名称
     */
    private String groupName;

    /**
     * 条件组描述
     */
    private String groupDescription;
}