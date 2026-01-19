package cn.ruleengine.web.controller;

import cn.ruleengine.common.vo.PlainResult;
import cn.ruleengine.web.service.ConditionNodeService;
import cn.ruleengine.web.vo.base.response.BaseResult;
import cn.ruleengine.web.vo.condition.group.condition.SaveConditionAndBindGroupResponse;
import cn.ruleengine.web.vo.condition.node.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 多层条件节点控制器
 *
 * @author dqw
 * @since 2024-01-19
 */
@Slf4j
@Api(tags = "多层条件节点管理")
@RestController
@RequestMapping("/api/condition/node")
public class ConditionNodeController {

    @Resource
    private ConditionNodeService conditionNodeService;

    @ApiOperation("获取条件树")
    @GetMapping("/tree/{ruleId}")
    public PlainResult<ConditionTreeResponse> getConditionTree(@PathVariable Integer ruleId) {
        PlainResult<ConditionTreeResponse> plainResult = new PlainResult<>();
        ConditionTreeResponse tree = conditionNodeService.getConditionTree(ruleId);
        plainResult.setData(tree);
        return plainResult;
    }

    @ApiOperation("保存条件树")
    @PostMapping("/tree/save")
    public PlainResult<Boolean> saveConditionTree(@Valid @RequestBody SaveConditionTreeRequest request) {
        Boolean result = conditionNodeService.saveConditionTree(request);
        PlainResult<Boolean> plainResult = new PlainResult<>();
        plainResult.setData(result);
        return plainResult;
    }

    @ApiOperation("创建条件节点")
    @PostMapping("/create")
    public BaseResult<Integer> createConditionNode(@Valid @RequestBody CreateConditionNodeRequest request) {
        Integer nodeId = conditionNodeService.createConditionNode(request);
        return BaseResult.ok(nodeId);
    }

    @ApiOperation("更新条件节点")
    @PostMapping("/update")
    public BaseResult<Boolean> updateConditionNode(@Valid @RequestBody UpdateConditionNodeRequest request) {
        Boolean result = conditionNodeService.updateConditionNode(request);
        return BaseResult.ok(result);
    }

    @ApiOperation("删除条件节点")
    @PostMapping("/delete")
    public BaseResult<Boolean> deleteConditionNode(@Valid @RequestBody DeleteConditionNodeRequest request) {
        Boolean result = conditionNodeService.deleteConditionNode(request);
        return BaseResult.ok(result);
    }

    @ApiOperation("移动条件节点")
    @PostMapping("/move")
    public BaseResult<Boolean> moveConditionNode(@Valid @RequestBody MoveConditionNodeRequest request) {
        Boolean result = conditionNodeService.moveConditionNode(request);
        return BaseResult.ok(result);
    }

    @ApiOperation("测试条件")
    @PostMapping("/test")
    public BaseResult<ConditionTestResponse> testCondition(@Valid @RequestBody ConditionTestRequest request) {
        ConditionTestResponse result = conditionNodeService.testCondition(request);
        return BaseResult.ok(result);
    }
}