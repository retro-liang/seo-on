package com.retro.seoon.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.retro.seoon.service.ImageSEOService;

@Controller
@RequestMapping("/spring")
public class SeoOnController
{
	private static final Logger logger = Logger.getLogger(SeoOnController.class.getName());
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@RequestMapping(method = RequestMethod.GET)
    public String welcomeHandler(HttpServletRequest request)
	{
		logger.info("request: " + request.getAttributeNames());
		return "welcome";
    }
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
    public String welcomePOSTHandler(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("request: " + request);
		try
		{
			String key = new ImageSEOService().uploadFile(file);	        
			
			logger.info("return uploaded key: " + key);
			return key;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return "";
		}
    }
	
	@RequestMapping("image")
    public void handler(@RequestParam("blob-key") BlobKey key, HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("key: " + key);
        try
        {
			blobstoreService.serve(key, response);
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}
    }
	
	@RequestMapping("seo-image")
	@ResponseBody
    public String seoImage(@RequestParam("fileKeys") String fileKeys, HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("keyNames: " + fileKeys);
		String[] keyNames = fileKeys.split(",");
		try
		{
			String seoedImageKey = new ImageSEOService().seoImage(keyNames).getKeyString();
			return "/spring/image?blob-key=" + seoedImageKey;
		}
		catch (IOException e)
		{
			return "";
		}
    }
}