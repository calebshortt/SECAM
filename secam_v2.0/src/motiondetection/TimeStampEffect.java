package motiondetection;


/**
 * Copyright:    Copyright (c) 2002 by Konrad Rzeszutek
 *
 *
 *
 *
 *    This file is part of Motion Detection toolkit.
 *
 *    Motion Detection toolkit is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    Motion Detection toolkit is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Foobar; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */


import javax.media.*;
import javax.media.format.*;
import java.awt.*;

public class TimeStampEffect implements Effect {

    Format inputFormat;
    Format outputFormat;
    Format[] inputFormats;
    Format[] outputFormats;
    java.text.SimpleDateFormat sdf;
    
    String motion = "";

    public TimeStampEffect() {

        sdf = new java.text.SimpleDateFormat("hh:mm:ss MM/dd/yy");
        inputFormats = new Format[] {
            new RGBFormat(null,
                          Format.NOT_SPECIFIED,
                          Format.byteArray,
                          Format.NOT_SPECIFIED,
                          24,
                          3, 2, 1,
                          3, Format.NOT_SPECIFIED,
                          Format.TRUE,
                          Format.NOT_SPECIFIED)
        };

        outputFormats = new Format[] {
            new RGBFormat(null,
                          Format.NOT_SPECIFIED,
                          Format.byteArray,
                          Format.NOT_SPECIFIED,
                          24,
                          3, 2, 1,
                          3, Format.NOT_SPECIFIED,
                          Format.TRUE,
                          Format.NOT_SPECIFIED)
        };

    }

    // methods for interface Codec
    public Format[] getSupportedInputFormats() {
	return inputFormats;
    }

    public Format [] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return outputFormats;
        }

        if (matches(input, inputFormats) != null) {
            return new Format[] { outputFormats[0].intersects(input) };
        } else {
            return new Format[0];
        }
    }

    public Format setInputFormat(Format input) {
	inputFormat = input;
	return input;
    }

    public Format setOutputFormat(Format output) {

        if (output == null || matches(output, outputFormats) == null)
            return null;
        RGBFormat incoming = (RGBFormat) output;

        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = incoming.getFrameRate();
        int flipped = incoming.getFlipped();
        int endian = incoming.getEndian();

        if (size == null)
            return null;
        if (maxDataLength < size.width * size.height * 3)
            maxDataLength = size.width * size.height * 3;
        if (lineStride < size.width * 3)
            lineStride = size.width * 3;
        if (flipped != Format.FALSE)
            flipped = Format.FALSE;

        outputFormat = outputFormats[0].intersects(new RGBFormat(size,
                                                        maxDataLength,
                                                        null,
                                                        frameRate,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED,
                                                        lineStride,
                                                        Format.NOT_SPECIFIED,
                                                        Format.NOT_SPECIFIED));

        //System.out.println("final outputformat = " + outputFormat);
        return outputFormat;
    }


    public int process(Buffer inBuffer, Buffer outBuffer) {

        int outputDataLength = ((VideoFormat)outputFormat).getMaxDataLength();
        validateByteArraySize(outBuffer, outputDataLength);

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);
        outBuffer.setFlags(inBuffer.getFlags());

        byte [] inData = (byte[]) inBuffer.getData();
        byte [] outData = (byte[]) outBuffer.getData();

        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();

        int pixStrideIn = vfIn.getPixelStride();
        int lineStrideIn = vfIn.getLineStride();

        if ( outData.length < sizeIn.width*sizeIn.height*3 ) {
            System.out.println("the buffer is not full");
            return BUFFER_PROCESSED_FAILED;
        }
        
        if(MotionFlag.FLAG) {
        	motion = " <Motion Detected> ";
        } else {
        	motion = "";
        }
        
        System.arraycopy(inData,0,outData,0,inData.length);
        Font.println(sdf.format(new java.util.Date()) + motion, Font.FONT_6x11, 10, 20, (byte)255,(byte)255,(byte)255, outBuffer);

    return BUFFER_PROCESSED_OK;
    }

    // methods for interface PlugIn
    public String getName() {
        return "TimeStamp Effect";
    }

    public void open() {
    }

    public void close() {
    }

    public void reset() {
    }

    // methods for interface javax.media.Controls
    public Object getControl(String controlType) {
	return null;
    }

    public Object[] getControls() {
	return null;
    }


    // Utility methods.
    Format matches(Format in, Format outs[]) {
	for (int i = 0; i < outs.length; i++) {
	    if (in.matches(outs[i]))
		return outs[i];
	}

	return null;
    }


    byte[] validateByteArraySize(Buffer buffer,int newSize) {
        Object objectArray=buffer.getData();
        byte[] typedArray;

        if (objectArray instanceof byte[]) {     // is correct type AND not null
            typedArray=(byte[])objectArray;
            if (typedArray.length >= newSize ) { // is sufficient capacity
                return typedArray;
            }

            byte[] tempArray=new byte[newSize];  // re-alloc array
            System.arraycopy(typedArray,0,tempArray,0,typedArray.length);
            typedArray = tempArray;
        } else {
            typedArray = new byte[newSize];
        }

        buffer.setData(typedArray);
        return typedArray;
    }

}
