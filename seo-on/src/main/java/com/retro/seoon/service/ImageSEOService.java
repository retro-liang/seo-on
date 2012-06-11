package com.retro.seoon.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;

public class ImageSEOService
{
	private static final Logger logger = Logger.getLogger(ImageSEOService.class.getName());
	
	public static void seoImage()
	{
		File file1 = new File("favicon.ico");
		File file2 = new File("Google-G-Logo.png");
		
		byte[] oldImageData1 = null;
		byte[] oldImageData2 = null;
		try
		{
			oldImageData1 = getBytesFromFile(file1);
			oldImageData2 = getBytesFromFile(file2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		

        ImagesService imagesService = ImagesServiceFactory.getImagesService();

        Image oldImage1 = ImagesServiceFactory.makeImage(oldImageData1);
        Composite composit1 = ImagesServiceFactory.makeComposite(oldImage1, 0, 0, 1, null);
        
        Image oldImage2 = ImagesServiceFactory.makeImage(oldImageData2);
        Composite composit2 = ImagesServiceFactory.makeComposite(oldImage2, 0, 0, 1, null);
        //Transform resize = ImagesServiceFactory.makeResize(200, 300);

        //Image newImage = imagesService.applyTransform(resize, oldImage);

        //byte[] newImageData = newImage.getImageData();
        
		List<Composite> composites = new ArrayList<Composite>();
		composites.add(composit1);
		composites.add(composit2);
		OutputEncoding outputEncoding = ImagesService.OutputEncoding.PNG;
		OutputSettings outputSettings = new OutputSettings(outputEncoding);
		Image newImage = imagesService.composite(composites, oldImage1.getWidth(), 1000, 0l, outputSettings);
		logger.info("new image:" +newImage);
		/*File path = ""; // base path of the images

		// load source images
		BufferedImage image = ImageIO.read(new File(path, "image.png"));
		BufferedImage overlay = ImageIO.read(new File(path, "overlay.png"));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		// Save as new image
		ImageIO.write(combined, "PNG", new File(path, "combined.png"));*/
	}
	
	public static byte[] getBytesFromFile(File file) throws IOException
	{
	    InputStream is = new FileInputStream(file);

	    // Get the size of the file
	    long length = file.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
	}
}