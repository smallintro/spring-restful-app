package io.github.smallintro.springboot.controller;

import io.github.smallintro.springboot.utils.ApplicationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("hello")
public class DefaultController {

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	private ApplicationUtils appUtils;

	@GetMapping
	public String getHealthStatus(@RequestHeader(name = "Accept-Language", required = false) String locale) {
		return appUtils.getI18nMessage("message.hello.world", locale) + " Welcome to " + applicationName;
	}

}
