package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建条件节点请求VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class CreateConditionNodeRequest {

    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
    private Integer ruleId;

    /**
     * 父节点ID
     */
    private Integer parentId;

    /**
     * 节点类型：LEAF/COMPOSITE
     */
    @NotBlank(message = "节点类型不能为空")
    private String nodeType;

    /**
     * 逻辑运算符：AND/OR（仅组合节点）
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