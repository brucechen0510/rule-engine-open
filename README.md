# 📌 规则引擎 RuleEngine 📌

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Percentage of issues still open](http://isitmaintained.com/badge/open/dingqianwen/rule-engine-v2.svg)](https://github.com/dingqianwen/rule-engine-v2/issues "Percentage of issues still open")

### 业务逻辑实现不再依赖于代码开发，可零代码实现复杂业务逻辑。

### 我正在参加 Gitee 2025 最受欢迎的开源软件投票活动，快来给我投票吧！https://gitee.com/activity/2025opensource?ident=IBB9GM

### 仅对当前项目中普通规则、参数、变量、函数开源，任何单位或个人不得对本软件商业版（体验环境）进行抄袭、复制、修改、分发、逆向工程、商业用途等任何形式的非法使用。违者将面临人民币 20 万元的法定罚款及可能的法律追责。举报侵权行为可获得实际罚款金额 40% 的现金奖励。

### 最新产品（ https://gitee.com/shaiwz/data-platform-open ）：大数据集成平台可视化拖拽式大数据集成平台后端、包含数据流、数据源、数据对齐、查询模板、完善的监控等。

前端代码地址：https://gitee.com/aizuda/rule-engine-front-open  
项目展示地址：http://ruleengine.cn/

### 毕业认证

<img src="https://oss-xuxin.oss-accelerate.aliyuncs.com/dp/img/c85f4b02c8fde9663abad3718524682c.jpg" alt="none" style="width: 60%;height: 60%;border-radius: 6px;">

##### 联系方式：

- QQ：761945125
- Email：761945125@qq.com

### 关于提出Bug奖励

如果项目在使用过程中，发现项目缺陷或者漏洞等问题，可以在以下地址中提出问题反馈：  
Gitee：https://gitee.com/aizuda/rule-engine-open/issues  
Github：https://github.com/rule-engine/rule-engine-open/issues

### 技术文档

简单使用：...  
详细文档：http://ruleengine.cn/doc

### 技术栈/版本介绍：

- 所涉及的相关的技术有：
    - SpringBoot 2.2.11
    - RabbitMQ
    - Redis、Redisson
    - MySQL 5.7.9
    - Mybatis-plus
    - Maven 3.3
    - Swagger

### 规则如何调用

通过在 http://ruleengine.cn 配置规则，配置完成后我们就可以调用接口来执行引擎中的规则了

```
POST http://ruleserver.cn/ruleEngine/generalRule/execute
Content-Type: application/json

{
      "code": "phoneRuletest",
      "workspaceCode": "default",
      "accessKeyId": "略", 
      "accessKeySecret": "略",
      "param": {
            "phone": "13400000000"
      }
}
```

现在我们让此使用方式更加简单易用！ 调用规则方项目pom.xml文件引入以下依赖

```pom
    <dependency>
        <groupId>cn.ruleengine</groupId>
        <artifactId>rule-engine-client</artifactId>
        <version>2.0</version>
    </dependency>
```

SpringBoot项目application.yml配置文件配置如下：

```yml
rule.engine:
  baseUrl: http://ruleserver.cn
  workspaceCode: default
  accessKeyId: root
  accessKeySecret: 123456
  # 可选配置
  feignConfig:
    request:
      connectTimeoutMillis: 3000
      readTimeoutMillis: 3500
    retryer:
      period: 2000
      maxPeriod: 2000
      maxAttempts: 3
```

然后编写如下代码进行测试：

```java

@EnableRuleEngine
@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleTest {

    @Resource
    private RuleEngineClient ruleEngineClient;

    @Test
    public void test() {
        // 构建规则请求参数
        PhoneTestRule phoneTestRule = new PhoneTestRule();
        phoneTestRule.setPhone("134000000000");
        // 调用执行引擎中的规则
        GeneralRule generalRule = this.ruleEngineClient.generalRule();
        Output output = generalRule.execute(phoneTestRule);
        System.out.println(output);
    }

}

@Data
@Model(code = "phoneRuletest")
public class PhoneTestRule {

    /**
     * 可选，默认code为属性name
     */
    @InputParameter(code = "phone")
    private String phone;

}
```

我们默认使用Feign请求，当然你也可以自定义，只需要在项目中配置如下代码：

```java

@Component
@Import({RestTemplate.class})
public class RuleEngineClientConfig {

    @Resource
    private RestTemplate restTemplate;

    @Bean
    public GeneralRuleInterface generalRuleInterface() {
        return new GeneralRuleInterface() {

            @Override
            public ExecuteResult execute(ExecuteParam executeParam) {
                return restTemplate.postForObject("http://ruleserver.cn/ruleEngine/generalRule/execute", executeParam, ExecuteResult.class);
            }

            @Override
            public IsExistsResult isExists(IsExistsParam existsParam) {
                // TODO: 2020/12/30  
                return null;
            }

            @Override
            public BatchExecuteResult batchExecute(BatchParam batchParam) {
                // TODO: 2020/12/30  
                return null;
            }
        };
    }

}
```

现在你就已经学会了如何使用，更多使用方式敬请期待！

### 下一步进展

- 优化代码，提高配置体验（进行中）
- 评分卡（待开发）
- 决策树（待开发）

目前忙于工作，功能待完善，欢迎有兴趣伙伴加入我们！

### 捐助霸王洗发水

作者为了此项目经常熬夜到凌晨才设计完成核心部分以及整体运转，头发掉很多，后来见到了传说中的霸王洗发水，看下自己的口袋，含下买不起的泪水，辛苦捐助一瓶霸王洗发水吧！

| 支付宝                                                                                   | 微信                                                                   |
|---------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| ![支付宝](https://boot-rule.oss-cn-beijing.aliyuncs.com/%E6%94%AF%E4%BB%98%E5%AE%9D.png) | ![微信](https://boot-rule.oss-cn-beijing.aliyuncs.com/WechatIMG2.jpeg) |

### 如果觉得本项目对您有任何一点帮助，请点右上角 "Star" 支持一下， 并向您的基友、同事们宣传一下吧，谢谢！

