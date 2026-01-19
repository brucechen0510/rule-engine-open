package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 删除条件节点请求VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class DeleteConditionNodeRequest {

    /**
     * 节点ID
     */
    @NotNull(message = "节点ID不能为空")
    private Integer id;
}