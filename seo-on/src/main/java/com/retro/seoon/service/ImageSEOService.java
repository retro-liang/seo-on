package com.retro.seoon.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.web.multipart.MultipartFile;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;

public class ImageSEOService
{
	private static final Logger logger = Logger.getLogger(ImageSEOService.class.getName());
	
	private ImagesService imagesService = ImagesServiceFactory.getImagesService();
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	public String uploadFile(MultipartFile file) throws IOException
	{
		Image image = ImagesServiceFactory.makeImage(file.getBytes());
		String key = saveFile(image).getKeyString(); 
		logger.info("uploaded image key: " + key);
		return key;
	}

	
	public BlobKey seoImage(String[] keys) throws IOException
	{
        Anchor anchor = Composite.Anchor.TOP_LEFT;

        Image newImage = null;
        int newImageLength = 0;
        int newImageHeight = 0;
        List<Composite> composites = new ArrayList<Composite>();
        
        for(String key : keys)
        {
        	logger.info("loading image: " + key);
        	BlobKey blobKey =  new BlobKey(key);
            BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
            byte[] bytes = blobstoreService.fetchData(blobKey, 0, blobInfo.getSize());
            Image image = ImagesServiceFactory.makeImage(bytes);
        	logger.info("found image: " + image.getImageData());
        	Composite composite = ImagesServiceFactory.makeComposite(image, 0, newImageHeight, 1, anchor);
        	composites.add(composite);
        	newImageLength = image.getWidth() > newImageLength ? image.getWidth() : newImageLength;
        	newImageHeight += image.getHeight();
        }
        OutputEncoding outputEncoding = ImagesService.OutputEncoding.PNG;
		OutputSettings outputSettings = new OutputSettings(outputEncoding);
		newImage = imagesService.composite(composites, newImageLength, newImageHeight, 0, outputSettings);
        BlobKey key = saveFile(newImage);
		logger.info("seoed image key: " + key.getKeyString());
		return key;
	}
	
	public BlobKey saveFile(Image image) throws IOException
	{
		// Get a file service
		  FileService fileService = FileServiceFactory.getFileService();

		  // Create a new Blob file with mime-type "text/plain"
		  AppEngineFile file = fileService.createNewBlobFile("image/png");

		  // Open a channel to write to it
		  boolean lock = true;
		  FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
		  
		  try { 
		        writeChannel.write(ByteBuffer.wrap(image.getImageData())); 
		    } catch (IOException e) { 
		        e.printStackTrace(); 
		    } 

		  
		  // Now finalize
		  writeChannel.closeFinally();
		  // Now read from the file using the Blobstore API
		  BlobKey blobKey = fileService.getBlobKey(file);
		  logger.info("Saved key: " + blobKey.getKeyString());
		  
		  return blobKey;
	}
}