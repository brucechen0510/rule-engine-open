package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 条件测试请求VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class ConditionTestRequest {

    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
    private Integer ruleId;

    /**
     * 测试参数
     */
    private Map<String, Object> params;
}