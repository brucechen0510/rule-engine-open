package cn.ruleengine.web.store.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 多层条件节点实体
 * 使用组合模式支持任意层级的条件结构
 *
 * @author dqw
 * @since 2024-01-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RuleEngineConditionNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 规则ID
     */
    private Integer ruleId;

    /**
     * 父节点ID，NULL表示根节点
     */
    private Integer parentId;

    /**
     * 节点类型：LEAF(叶子节点-具体条件) / COMPOSITE(组合节点-条件组)
     */
    private String nodeType;

    /**
     * 逻辑运算符：AND/OR，仅组合节点有效
     */
    private String logicalOperator;

    /**
     * 同级节点排序
     */
    private Integer orderNo;

    // ========== 叶子节点字段（具体条件） ==========

    /**
     * 条件名称
     */
    private String conditionName;

    /**
     * 条件描述
     */
    private String conditionDescription;

    /**
     * 左值类型：0参数，1变量，2固定值
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

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    // ========== 常量定义 ==========

    /**
     * 节点类型常量
     */
    public static class NodeType {
        public static final String LEAF = "LEAF";           // 叶子节点（具体条件）
        public static final String COMPOSITE = "COMPOSITE"; // 组合节点（条件组）
    }

    /**
     * 逻辑运算符常量
     */
    public static class LogicalOperator {
        public static final String AND = "AND"; // 与
        public static final String OR = "OR";   // 或
    }

    // ========== 便捷方法 ==========

    /**
     * 判断是否为叶子节点
     */
    public boolean isLeaf() {
        return NodeType.LEAF.equals(this.nodeType);
    }

    /**
     * 判断是否为组合节点
     */
    public boolean isComposite() {
        return NodeType.COMPOSITE.equals(this.nodeType);
    }

    /**
     * 判断是否为根节点
     */
    public boolean isRoot() {
        return this.parentId == null;
    }
}