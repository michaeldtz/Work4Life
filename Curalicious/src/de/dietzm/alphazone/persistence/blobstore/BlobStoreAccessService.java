package de.dietzm.alphazone.persistence.blobstore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.LockException;

public class BlobStoreAccessService {

	public String storeStream(InputStream is) throws IOException {

		FileService fileService = FileServiceFactory.getFileService();
		AppEngineFile file = fileService.createNewBlobFile("text/plain");

		// Create and write to filechannel
		FileWriteChannel fileCh = fileService.openWriteChannel(file, true);
		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) >= 0) {
			if (len < 0)
				break;
			fileCh.write(ByteBuffer.wrap(buf, 0, len), null);
		}
		is.close();
		fileCh.closeFinally();

		// Get the filepath and encode base64 to look nicer
		String blobKey = file.getNamePart();
		blobKey = blobKey.replace("writable:", "");
		
		return blobKey;
	}

	
	public void fetchToStream(String blobkey, OutputStream os) throws FileNotFoundException, LockException, IOException {

		// Init fileService and create file object
		FileService fileService = FileServiceFactory.getFileService();
		AppEngineFile file = new AppEngineFile("/blobstore/writable:" + blobkey);

		// Open and read channel
		FileReadChannel fileCh = fileService.openReadChannel(file, false);
		ByteBuffer buf = ByteBuffer.allocate(1024);

		int len;
		while ((len = fileCh.read(buf)) >= 0) {
			if (len < 0)
				break;
			os.write(buf.array());
			buf.clear();
		}
		
		fileCh.close();
	}


}
