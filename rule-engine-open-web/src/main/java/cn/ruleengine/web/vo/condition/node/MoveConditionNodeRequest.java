package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 移动条件节点请求VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class MoveConditionNodeRequest {

    /**
     * 节点ID
     */
    @NotNull(message = "节点ID不能为空")
    private Integer id;

    /**
     * 新的父节点ID
     */
    private Integer newParentId;

    /**
     * 新的排序号
     */
    @NotNull(message = "排序号不能为空")
    private Integer newOrderNo;
}