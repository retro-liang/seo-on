package com.retro.seoon.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.retro.seoon.service.ImageSEOService;

@Controller
@RequestMapping("/spring")
public class SeoOnController
{
	private static final Logger logger = Logger.getLogger(SeoOnController.class.getName());
	
	private @Autowired HttpServletRequest request;	

	@RequestMapping(method = RequestMethod.GET)
    public String welcomeHandler()
	{
		logger.info("request: " + request.getAttributeNames());
		return "welcome";
    }
	
	@RequestMapping(method = RequestMethod.POST)
    public void welcomePOSTHandler(@RequestParam("file") MultipartFile[] imageFiles, HttpServletResponse response)
	{
		logger.info("f: " + imageFiles);
		try {
			BlobKey key =  new ImageSEOService().seoImage(request, imageFiles[0].getBytes());
			ImagesService imagesService = ImagesServiceFactory.getImagesService();
			String url = imagesService.getServingUrl(key);
			logger.info("url: " + url);
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			blobstoreService.serve(key, response);
			logger.info("image served: " + key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return "welcome";
    }
	
	@RequestMapping("image")
    public String handler(@RequestParam("blob-key") BlobKey key, HttpServletResponse response)
	{
		logger.info("key: " + key);
		/*BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();		
        try {
			blobstoreService.serve(key, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        String url = imagesService.getServingUrl(key);
        logger.info("url: " + url);
		return url;
    }
}