package connection;

/*
 * @(#)JpegImagesToMovie.java   1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.Dimension;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

public class ImageSourceStream implements PullBufferStream {
	
	Vector images;
	int width, height;
	VideoFormat format;
	
	int nextImage = 0; // index of the next image to be read.
	boolean ended = false;
	
	
	// ------------------------------------------------------------------------------------
	public ImageSourceStream(int width, int height, int frameRate, Vector images) {
		this.width = width;
		this.height = height;
		this.images = images;

		format = new VideoFormat(VideoFormat.JPEG, new Dimension(width, height), Format.NOT_SPECIFIED, Format.byteArray, (float) frameRate);
	}
	// ------------------------------------------------------------------------------------
	
	
	public Format getFormat() {
		return format;
	}
	
	
	public void read(Buffer buf) throws IOException {
		
		// Check if we've finished all the frames.
		if (nextImage >= images.size()) {
			// We are done. Set EndOfMedia.
			System.err.println("Done reading all images.");
			buf.setEOM(true);
			buf.setOffset(0);
			buf.setLength(0);
			ended = true;
			return;
		}

		String imageFile = (String) images.elementAt(nextImage);
		nextImage++;

		System.err.println("  - reading image file: " + imageFile);

		// Open a random access file for the next image.
		RandomAccessFile raFile;
		raFile = new RandomAccessFile(imageFile, "r");

		byte data[] = null;

		// Check the input buffer type & size.

		if (buf.getData() instanceof byte[])
			data = (byte[]) buf.getData();

		// Check to see the given buffer is big enough for the frame.
		if (data == null || data.length < raFile.length()) {
			data = new byte[(int) raFile.length()];
			buf.setData(data);
		}

		// Read the entire JPEG image from the file.
		raFile.readFully(data, 0, (int) raFile.length());

		System.err.println("    read " + raFile.length() + " bytes.");

		buf.setOffset(0);
		buf.setLength((int) raFile.length());
		buf.setFormat(format);
		buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);

		// Close the random access file.
		raFile.close();
	}

	public boolean willReadBlock() {
		return false;
	}

	public boolean endOfStream() {
		return ended;
	}

	public ContentDescriptor getContentDescriptor() {
		return new ContentDescriptor(ContentDescriptor.RAW);
	}

	public long getContentLength() {
		return 0;
	}

	public Object getControl(String arg0) {
		return null;
	}

	public Object[] getControls() {
		return new Object[0];
	}
}
