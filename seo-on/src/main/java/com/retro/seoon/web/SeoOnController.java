package com.retro.seoon.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.retro.seoon.service.ImageSEOService;

@Controller
@RequestMapping("/spring")
public class SeoOnController
{
	@RequestMapping(method = RequestMethod.GET)
    public String welcomeHandler()
	{
		ImageSEOService.seoImage();
		return "welcome";
    }
	
	@RequestMapping("image")
    public String handler()
	{
		ImageSEOService.seoImage();
		return "";
    }
}