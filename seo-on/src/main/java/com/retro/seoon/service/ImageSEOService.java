package com.retro.seoon.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
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

	
	public BlobKey seoImage(HttpServletRequest request, byte[] fileContent) throws IOException
	{
        Anchor anchor = Composite.Anchor.TOP_LEFT;

        Image oldImage1 = ImagesServiceFactory.makeImage(fileContent);
        Composite composit1 = ImagesServiceFactory.makeComposite(oldImage1, 0, 0, 1, anchor);
        
        Image oldImage2 = ImagesServiceFactory.makeImage(fileContent);
        Composite composit2 = ImagesServiceFactory.makeComposite(oldImage2, 0, 0, 1, anchor);
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
		
		return saveFile(newImage);
		
		/*Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
		List<BlobKey> blobKey = blobs.get("file");

		if (blobKey == null)
		{
		    return "/";
		}
		else
		{
		    return "/image?blob-key=" + blobKey.get(0).getKeyString();
		}*/
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
	
	public BlobKey saveFile(Image image) throws IOException
	{
		// Get a file service
		  FileService fileService = FileServiceFactory.getFileService();

		  // Create a new Blob file with mime-type "text/plain"
		  AppEngineFile file = fileService.createNewBlobFile("multipart/form-data");

		  // Open a channel to write to it
		  boolean lock = false;
		  FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

		  // Different standard Java ways of writing to the channel
		  // are possible. Here we use a PrintWriter:
		  PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
		  out.println(image.getImageData());		  

		  // Close without finalizing and save the file path for writing later
		  out.close();
		  String path = file.getFullPath();

		  // Write more to the file in a separate request:
		  file = new AppEngineFile(path);

		  // This time lock because we intend to finalize
		  lock = true;
		  writeChannel = fileService.openWriteChannel(file, lock);

		  // This time we write to the channel directly
		  //writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep.".getBytes()));

		  // Now finalize
		  writeChannel.closeFinally();

		  // Later, read from the file using the file API
		  lock = false; // Let other people read at the same time
		  FileReadChannel readChannel = fileService.openReadChannel(file, false);

		  // Again, different standard Java ways of reading from the channel.
		  BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
		  String line = reader.readLine();
		  logger.info("Line: " + line);
		  // line = "The woods are lovely dark and deep."

		  readChannel.close();

		  // Now read from the file using the Blobstore API
		  BlobKey blobKey = fileService.getBlobKey(file);
		  logger.info("Saved key: " + blobKey.getKeyString());
		  
		  return blobKey;
		  //BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
		  //String segment = new String(blobStoreService.fetchData(blobKey, 30, 40));
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
	
	public static void main(String args[])
	{
		File file = new File("D:\\workspace_git\\seo-on\\seo-on\\src\\main\\resources\\favicon.ico");
		try {
			System.out.println(getBytesFromFile(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}