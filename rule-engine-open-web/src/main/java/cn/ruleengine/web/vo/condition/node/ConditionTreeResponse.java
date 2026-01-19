package cn.ruleengine.web.vo.condition.node;

import lombok.Data;

import javax.validation.Valid;

/**
 * 条件树响应VO
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
public class ConditionTreeResponse {

    /**
     * 规则ID
     */
    private Integer ruleId;

    /**
     * 根节点
     */
    @Valid
    private ConditionNodeVO rootNode;

    /**
     * 条件节点VO
     */
    @Data
    public static class ConditionNodeVO {

        /**
         * 节点ID
         */
        private Integer id;

        /**
         * 父节点ID
         */
        private Integer parentId;

        /**
         * 节点类型：LEAF/COMPOSITE
         */
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
        private ConditionConfig config;

        // ========== 组合节点字段 ==========

        /**
         * 条件组名称
         */
        private String groupName;

        /**
         * 条件组描述
         */
        private String groupDescription;

        /**
         * 子节点列表
         */
        private java.util.List<ConditionNodeVO> children;

        /**
         * 条件配置
         */
        @Data
        public static class ConditionConfig {

            /**
             * 左值配置
             */
            @Valid
            private ValueConfig leftValue;

            /**
             * 运算符
             */
            private String symbol;

            /**
             * 右值配置
             */
            @Valid
            private ValueConfig rightValue;

            /**
             * 值配置
             */
            @Data
            public static class ValueConfig {

                /**
                 * 值类型：0参数，1变量，2固定值
                 */
                private Integer type;

                /**
                 * 值类型枚举
                 */
                private String valueType;

                /**
                 * 值
                 */
                private String value;

                /**
                 * 值名称（用于显示）
                 */
                private String valueName;

                /**
                 * 变量值（变量类型时的具体值）
                 */
                private String variableValue;
            }
        }
    }
}