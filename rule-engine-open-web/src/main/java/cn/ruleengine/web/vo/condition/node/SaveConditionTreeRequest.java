package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 保存条件树请求VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class SaveConditionTreeRequest {

    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
    private Integer ruleId;

    /**
     * 根节点
     */
    @NotNull(message = "条件树不能为空")
    @Valid
    private ConditionTreeResponse.ConditionNodeVO rootNode;
}