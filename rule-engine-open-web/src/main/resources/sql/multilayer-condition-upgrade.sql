-- 多层条件结构升级SQL

-- 1. 统一条件节点表（替代原有的条件组和条件表）
CREATE TABLE rule_engine_condition_node (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rule_id INT NOT NULL COMMENT '规则ID',
    parent_id INT NULL COMMENT '父节点ID，NULL表示根节点',
    node_type VARCHAR(20) NOT NULL COMMENT '节点类型：LEAF(叶子节点-具体条件) / COMPOSITE(组合节点-条件组)',
    logical_operator VARCHAR(10) NULL COMMENT '逻辑运算符：AND/OR，仅组合节点有效',
    order_no INT NOT NULL DEFAULT 0 COMMENT '同级节点排序',

    -- 叶子节点字段（具体条件）
    condition_name VARCHAR(100) NULL COMMENT '条件名称',
    condition_description VARCHAR(500) NULL COMMENT '条件描述',
    left_type TINYINT NULL COMMENT '左值类型：0参数，1变量，2固定值',
    left_value VARCHAR(2000) NULL COMMENT '左值',
    left_value_type VARCHAR(20) NULL COMMENT '左值数据类型',
    symbol VARCHAR(20) NULL COMMENT '运算符',
    right_type TINYINT NULL COMMENT '右值类型',
    right_value VARCHAR(2000) NULL COMMENT '右值',
    right_value_type VARCHAR(20) NULL COMMENT '右值数据类型',

    -- 组合节点字段
    group_name VARCHAR(100) NULL COMMENT '条件组名称',
    group_description VARCHAR(500) NULL COMMENT '条件组描述',

    -- 通用字段
    workspace_id INT NULL,
    create_user_id INT NULL,
    create_user_name VARCHAR(50) NULL,
    create_time TIMESTAMP NULL,
    update_time TIMESTAMP NULL,
    deleted TINYINT DEFAULT 0
);

-- 索引
CREATE INDEX idx_condition_node_rule_id ON rule_engine_condition_node (rule_id);
CREATE INDEX idx_condition_node_parent_id ON rule_engine_condition_node (parent_id);
CREATE INDEX idx_condition_node_type ON rule_engine_condition_node (node_type);
CREATE INDEX idx_condition_node_workspace_id ON rule_engine_condition_node (workspace_id);

-- 数据迁移示例（从旧结构迁移到新结构）
-- 注意：这个脚本需要根据实际数据情况进行调整

-- 步骤1：为每个规则创建根组合节点（原conditionSet的作用）
INSERT INTO rule_engine_condition_node (
    rule_id, parent_id, node_type, logical_operator, order_no,
    group_name, workspace_id, create_time, update_time
)
SELECT DISTINCT
    rule_id,
    NULL as parent_id,
    'COMPOSITE' as node_type,
    'OR' as logical_operator,  -- 原来conditionGroup之间是OR关系
    0 as order_no,
    '条件集' as group_name,
    2 as workspace_id,  -- 根据实际情况调整
    NOW() as create_time,
    NOW() as update_time
FROM rule_engine_condition_group
WHERE deleted = 0;

-- 步骤2：为每个条件组创建组合节点
INSERT INTO rule_engine_condition_node (
    rule_id, parent_id, node_type, logical_operator, order_no,
    group_name, workspace_id, create_time, update_time
)
SELECT
    cg.rule_id,
    (SELECT id FROM rule_engine_condition_node
     WHERE rule_id = cg.rule_id AND parent_id IS NULL AND node_type = 'COMPOSITE' LIMIT 1) as parent_id,
    'COMPOSITE' as node_type,
    'AND' as logical_operator,  -- 原来条件组内是AND关系
    cg.order_no,
    COALESCE(cg.name, CONCAT('条件组', cg.order_no)) as group_name,
    2 as workspace_id,
    cg.create_time,
    cg.update_time
FROM rule_engine_condition_group cg
WHERE cg.deleted = 0;

-- 步骤3：迁移具体条件为叶子节点
INSERT INTO rule_engine_condition_node (
    rule_id, parent_id, node_type, logical_operator, order_no,
    condition_name, condition_description,
    left_type, left_value, left_value_type, symbol,
    right_type, right_value, right_value_type,
    workspace_id, create_user_id, create_user_name, create_time, update_time
)
SELECT
    cg.rule_id,
    (SELECT id FROM rule_engine_condition_node
     WHERE rule_id = cg.rule_id AND group_name = COALESCE(cg.name, CONCAT('条件组', cg.order_no))
       AND node_type = 'COMPOSITE' AND parent_id IS NOT NULL LIMIT 1) as parent_id,
    'LEAF' as node_type,
    NULL as logical_operator,  -- 叶子节点不需要逻辑运算符
    cgc.order_no,
    c.name as condition_name,
    c.description as condition_description,
    c.left_type,
    c.left_value,
    c.left_value_type,
    c.symbol,
    c.right_type,
    c.right_value,
    c.right_value_type,
    c.workspace_id,
    c.create_user_id,
    c.create_user_name,
    c.create_time,
    c.update_time
FROM rule_engine_condition_group_condition cgc
JOIN rule_engine_condition c ON cgc.condition_id = c.id
JOIN rule_engine_condition_group cg ON cgc.condition_group_id = cg.id
WHERE cgc.deleted = 0 AND c.deleted = 0 AND cg.deleted = 0
ORDER BY cg.rule_id, cg.order_no, cgc.order_no;

-- 备注：实际使用时，建议先备份数据，然后分步执行，验证数据正确性