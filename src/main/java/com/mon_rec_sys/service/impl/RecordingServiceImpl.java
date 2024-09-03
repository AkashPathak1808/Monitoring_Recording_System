package com.mon_rec_sys.service.impl;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;
import org.springframework.stereotype.Service;

import com.mon_rec_sys.service.RecordingService;

@Service
public class RecordingServiceImpl implements RecordingService {

	private volatile boolean recording;
	private String outputFilePath;
	private VideoWriter videoWriter;

	static {
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startRecording(String username) {
		recording = true;
		outputFilePath = "E:\\Railworld India\\Screentshot\\" + System.currentTimeMillis() + ".mp4";

//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		System.out.println("Starting video recording...");

		int fourcc = VideoWriter.fourcc('m', 'p', '4', 'v');
		Size frameSize = new Size(1280, 720); // Frame size for 720p
		double fps = 30.0;

		try {
			videoWriter = new VideoWriter(outputFilePath, fourcc, fps, frameSize, true);
			if (!videoWriter.isOpened()) {
				throw new RuntimeException("Failed to open video writer.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return; // Exit if there's an error setting up the video writer
		}

		// Start a new thread for recording
		new Thread(() -> {
			try {
				while (recording) {
					Mat screenshot = captureScreen(); // Ensure captureScreen() returns a Mat with the correct
														// dimensions
					if (screenshot.empty()) {
						System.err.println("Warning: Captured screenshot is empty. Skipping frame.");
						continue;
					}
					videoWriter.write(screenshot);
					Thread.sleep((long) (1000 / fps)); // Maintain the specified FPS
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (videoWriter != null) {
					videoWriter.release();
					System.out.println("Recording stopped and video file saved.");
				}
			}
		}).start();
	}

	@Override
	public void stopRecording() {
		recording = false;
	}

	private Mat captureScreen() {
	    try {
	        if (GraphicsEnvironment.isHeadless()) {
	            System.err.println("Warning: Running in a headless environment. Screen capture is not supported.");
	            return new Mat();  // Return an empty Mat in headless environments
	        }

	        // Capture the screen using Robot
	        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	        BufferedImage screenImage = new Robot().createScreenCapture(screenRect);

	        // Convert BufferedImage to Mat
	        Mat mat = new Mat(screenImage.getHeight(), screenImage.getWidth(), CvType.CV_8UC3);

	        // Check the type of BufferedImage and convert accordingly
	        if (screenImage.getType() == BufferedImage.TYPE_INT_RGB || screenImage.getType() == BufferedImage.TYPE_INT_ARGB) {
	            int[] data = ((DataBufferInt) screenImage.getRaster().getDataBuffer()).getData();
	            byte[] bytes = new byte[data.length * 3];
	            
	            for (int i = 0; i < data.length; i++) {
	                bytes[i * 3] = (byte) ((data[i] >> 16) & 0xFF); // Red
	                bytes[i * 3 + 1] = (byte) ((data[i] >> 8) & 0xFF);  // Green
	                bytes[i * 3 + 2] = (byte) (data[i] & 0xFF);  // Blue
	            }
	            mat.put(0, 0, bytes);
	        } else if (screenImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
	            byte[] data = ((DataBufferByte) screenImage.getRaster().getDataBuffer()).getData();
	            mat.put(0, 0, data);
	        } else {
	            System.err.println("Unsupported BufferedImage type: " + screenImage.getType());
	            return new Mat();  // Return an empty Mat for unsupported types
	        }
	        
	        return mat;
	    } catch (HeadlessException e) {
	        e.printStackTrace();
	        System.err.println("Cannot capture screen in a headless environment.");
	        return new Mat();  // Return an empty Mat on failure
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new Mat();  // Return an empty Mat on failure
	    }
	}


	@Override
	public String getOutputFilePath() {
		return outputFilePath;
	}

}
