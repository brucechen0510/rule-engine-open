# 多层条件系统升级指南

## 概述

本次升级将原有的2层条件系统（第一层只支持OR，第二层只支持AND）改造成支持任意层级且或关系组合的多层条件系统。

## 主要改进

### 1. 核心改进
- ✅ **任意层级嵌套**：支持无限层级的条件组嵌套
- ✅ **灵活的逻辑关系**：每个条件组都可以独立设置AND/OR逻辑
- ✅ **可视化构建界面**：全新的树形条件构建器
- ✅ **条件测试功能**：支持在线测试条件逻辑
- ✅ **向后兼容**：保持与原有系统的兼容性

### 2. 技术架构
- **设计模式**：采用组合模式（Composite Pattern）
- **数据结构**：统一的节点表存储所有条件和条件组
- **前端框架**：Vue.js + Ant Design Vue
- **后端框架**：Spring Boot + MyBatis Plus

## 新功能特性

### 1. 多层条件构建器
```
条件集 (OR)
├── 条件组A (AND)
│   ├── 条件1: 用户年龄 > 18
│   └── 条件2: 用户性别 = 男
├── 条件组B (OR)
│   ├── 条件3: 用户等级 = VIP
│   └── 条件组C (AND)
│       ├── 条件4: 订单金额 > 1000
│       └── 条件5: 是否新用户 = true
```

### 2. 智能逻辑运算符切换
- 点击条件组的逻辑标签可快速切换AND/OR
- 实时预览逻辑关系变化
- 支持拖拽调整条件顺序

### 3. 在线条件测试
- 输入JSON格式的测试参数
- 实时查看执行结果和详细日志
- 性能监控（执行时间统计）

## 数据库变更

### 新增表结构
```sql
-- 统一条件节点表
CREATE TABLE rule_engine_condition_node (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rule_id INT NOT NULL,
    parent_id INT NULL,
    node_type VARCHAR(20) NOT NULL, -- LEAF/COMPOSITE
    logical_operator VARCHAR(10) NULL, -- AND/OR
    order_no INT NOT NULL DEFAULT 0,
    -- 叶子节点字段（具体条件）
    condition_name VARCHAR(100) NULL,
    condition_description VARCHAR(500) NULL,
    left_type TINYINT NULL,
    left_value VARCHAR(2000) NULL,
    left_value_type VARCHAR(20) NULL,
    symbol VARCHAR(20) NULL,
    right_type TINYINT NULL,
    right_value VARCHAR(2000) NULL,
    right_value_type VARCHAR(20) NULL,
    -- 组合节点字段
    group_name VARCHAR(100) NULL,
    group_description VARCHAR(500) NULL,
    -- 通用字段
    workspace_id INT NULL,
    create_user_id INT NULL,
    create_user_name VARCHAR(50) NULL,
    create_time TIMESTAMP NULL,
    update_time TIMESTAMP NULL,
    deleted TINYINT DEFAULT 0
);
```

### 数据迁移
执行 `multilayer-condition-upgrade.sql` 脚本进行数据迁移：
1. 为每个规则创建根组合节点
2. 迁移现有条件组为组合节点
3. 迁移具体条件为叶子节点

## API接口

### 新增接口
```
GET    /api/condition/node/tree/{ruleId}     # 获取条件树
POST   /api/condition/node/tree/save        # 保存条件树
POST   /api/condition/node/create           # 创建条件节点
POST   /api/condition/node/update           # 更新条件节点
POST   /api/condition/node/delete           # 删除条件节点
POST   /api/condition/node/move             # 移动条件节点
POST   /api/condition/node/test             # 测试条件
```

### 数据格式示例
```json
{
  "ruleId": 123,
  "rootNode": {
    "nodeType": "COMPOSITE",
    "logicalOperator": "OR",
    "groupName": "条件集",
    "children": [
      {
        "nodeType": "COMPOSITE",
        "logicalOperator": "AND",
        "groupName": "用户基本条件",
        "children": [
          {
            "nodeType": "LEAF",
            "conditionName": "年龄检查",
            "config": {
              "leftValue": {
                "type": 0,
                "valueType": "NUMBER",
                "value": "166",
                "valueName": "用户年龄"
              },
              "symbol": "GT",
              "rightValue": {
                "type": 2,
                "valueType": "NUMBER",
                "value": "18"
              }
            }
          }
        ]
      }
    ]
  }
}
```

## 前端组件

### 主要组件
1. **MultiLayerConditionBuilder.vue** - 主条件构建器
2. **ConditionNode.vue** - 条件节点组件
3. **ConditionNodeModal.vue** - 节点编辑模态框
4. **ConditionConfigForm.vue** - 条件配置表单
5. **ConditionTestModal.vue** - 条件测试模态框

### 使用示例
```vue
<template>
  <multi-layer-condition-builder
    :rule-id="ruleId"
    ref="conditionBuilder"
  />
</template>

<script>
import MultiLayerConditionBuilder from '@/pages/components/condition/MultiLayerConditionBuilder.vue'

export default {
  components: {
    MultiLayerConditionBuilder
  },
  data() {
    return {
      ruleId: 123
    }
  }
}
</script>
```

## 核心类结构

### 后端核心类
1. **ConditionNode** - 抽象节点基类
2. **ConditionLeaf** - 叶子节点（具体条件）
3. **ConditionComposite** - 组合节点（条件组）
4. **ConditionTreeBuilder** - 条件树构建器
5. **MultiLayerConditionSet** - 多层条件集

### 设计模式应用
```java
// 组合模式实现
public abstract class ConditionNode implements ConditionCompare {
    public abstract boolean compare(Input input, RuleEngineConfiguration configuration);
}

public class ConditionLeaf extends ConditionNode {
    // 具体条件逻辑
}

public class ConditionComposite extends ConditionNode {
    private List<ConditionNode> children;
    private String logicalOperator; // AND/OR

    @Override
    public boolean compare(Input input, RuleEngineConfiguration configuration) {
        // 根据logicalOperator执行AND或OR逻辑
    }
}
```

## 升级步骤

### 1. 数据库升级
```bash
# 1. 备份现有数据
mysqldump -u username -p database_name > backup.sql

# 2. 执行升级脚本
mysql -u username -p database_name < multilayer-condition-upgrade.sql
```

### 2. 代码部署
```bash
# 1. 更新后端代码
mvn clean package -DskipTests

# 2. 更新前端代码
npm run build

# 3. 部署应用
# 按照现有部署流程进行
```

### 3. 配置更新
- 更新路由配置，添加新版本页面路由
- 更新权限配置，确保用户可以访问新接口
- 更新监控配置，添加新接口的监控

## 兼容性说明

### 向后兼容
- 原有的API接口保持不变
- 原有的数据结构继续支持
- 用户可以选择使用新版本或传统版本

### 渐进式升级
1. **阶段一**：部署新系统，保持传统版本作为默认
2. **阶段二**：逐步引导用户使用新版本
3. **阶段三**：收集反馈，优化新版本
4. **阶段四**：新版本成为默认，传统版本作为备选

## 性能优化

### 数据库优化
- 添加适当的索引
- 优化查询语句
- 考虑分页加载大量条件

### 前端优化
- 虚拟滚动处理大量节点
- 懒加载子节点
- 防抖处理用户操作

### 内存优化
- 条件树构建时的内存管理
- 大量条件执行时的优化策略

## 测试方案

### 单元测试
- 条件节点逻辑测试
- 条件树构建测试
- API接口测试

### 集成测试
- 前后端联调测试
- 数据迁移验证
- 性能压力测试

### 用户测试
- 界面易用性测试
- 功能完整性测试
- 兼容性测试

## 监控和告警

### 关键指标
- API响应时间
- 条件执行性能
- 错误率统计
- 用户使用情况

### 告警设置
- 条件执行超时告警
- API错误率过高告警
- 数据库连接异常告警

## 常见问题

### Q1: 如何从传统版本迁移到新版本？
A: 系统会自动迁移数据，用户只需要点击"切换到新版本"即可。

### Q2: 新版本是否支持所有原有功能？
A: 是的，新版本完全兼容原有功能，并提供了更多高级特性。

### Q3: 条件执行性能是否有影响？
A: 新版本进行了性能优化，在复杂条件场景下性能更优。

### Q4: 如何回退到传统版本？
A: 可以通过界面上的"传统版本"按钮快速回退。

## 联系方式

如有问题或建议，请联系：
- 开发团队：dev@company.com
- 技术支持：support@company.com
- 项目经理：pm@company.com

---

**版本信息**
- 当前版本：v2.0.0
- 发布日期：2024-01-19
- 兼容性：向后兼容v1.x所有版本