package com.retro.seoon.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/spring")
public class SeoOnController
{
	@RequestMapping(method = RequestMethod.GET)
    public String welcomeHandler()
	{
		return "welcome";
    }
}