package cn.ruleengine.compute.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gaopo
 */
@Controller
public class HealthController {

	@GetMapping("hs")
	@ResponseBody
	public String hs() {
		return "OK";
	}
}
