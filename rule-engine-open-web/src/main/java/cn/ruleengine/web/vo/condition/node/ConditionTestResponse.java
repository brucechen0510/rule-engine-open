package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import java.util.List;

/**
 * 条件测试响应VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class ConditionTestResponse {

    /**
     * 测试结果
     */
    private Boolean result;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 详细执行日志
     */
    private List<ExecutionLog> logs;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行日志
     */
    @Data
    public static class ExecutionLog {

        /**
         * 节点ID
         */
        private Integer nodeId;

        /**
         * 节点名称
         */
        private String nodeName;

        /**
         * 节点类型
         */
        private String nodeType;

        /**
         * 执行结果
         */
        private Boolean result;

        /**
         * 执行详情（仅叶子节点）
         */
        private String detail;

        /**
         * 执行时间（毫秒）
         */
        private Long executionTime;
    }
}