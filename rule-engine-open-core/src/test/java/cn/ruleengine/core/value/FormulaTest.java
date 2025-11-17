package cn.ruleengine.core.value;

import cn.ruleengine.core.DefaultInput;
import cn.ruleengine.core.Input;
import org.junit.Test;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 丁乾文
 * @date 2021/2/6
 * @since 1.0.0
 */
public class FormulaTest {

    @Test
    public void test() {
        Formula formula = new Formula("(#input1 - #input2) * 3", ValueType.NUMBER);
        Input input = new DefaultInput();
        input.put("input1", 3);
        input.put("input2", 1);
        System.out.println(formula.getInputParameterCodes());
        Object value = formula.getValue(input, null);
        System.out.println(value);
    }

    @Test
    public void test2() {
        Formula formula = new Formula("#input1 + ' 你好'", ValueType.STRING);
        Input input = new DefaultInput();
        input.put("input1", "小丁");
        System.out.println(formula.getInputParameterCodes());
        Object value = formula.getValue(input, null);
        System.out.println(value);
    }

}
