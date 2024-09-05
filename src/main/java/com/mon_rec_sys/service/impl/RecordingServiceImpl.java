package com.mon_rec_sys.service.impl;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.ConcurrentHashMap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.springframework.stereotype.Service;

import com.mon_rec_sys.service.RecordingService;

@Service
public class RecordingServiceImpl implements RecordingService {

	// Track active sessions by username
	private final ConcurrentHashMap<String, RecordingSession> activeSessions = new ConcurrentHashMap<>();

	static {
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startRecording(String username) {
		// Prevent starting a new recording if one is already active for the user
		if (activeSessions.containsKey(username)) {
			System.err.println("Recording already in progress for user: " + username);
			return;
		}

		// Create a new recording session for the user
		RecordingSession session = new RecordingSession(username);
		System.out.println(activeSessions);
		activeSessions.put(username, session);
		System.out.println(activeSessions);
		session.startRecording();
	}

	@Override
	public void stopRecording(String username) {
		// Find the active recording session for the user
		RecordingSession session = activeSessions.remove(username);
		if (session != null) {
			session.stopRecording(); // Stop the recording for this user only
		} else {
			System.err.println("No active recording session found for user: " + username);
		}
	}

	// Inner class representing a recording session
	private class RecordingSession {
		private volatile boolean recording;
		private String outputFilePath;
		private VideoWriter videoWriter;
		private VideoCapture camera;
		private Thread recordingThread;

		public RecordingSession(String username) {
			this.outputFilePath = "E:\\Railworld India\\Screentshot\\" + username + "_" + System.currentTimeMillis()
					+ ".mp4";
		}

		public void startRecording() {
			recording = true;

			int fourcc = VideoWriter.fourcc('m', 'p', '4', 'v');
			Size frameSize = new Size(1280, 720); // Frame size for 720p
			double fps = 6.0;

			try {
				videoWriter = new VideoWriter(outputFilePath, fourcc, fps, frameSize, true);
				if (!videoWriter.isOpened()) {
					throw new RuntimeException("Failed to open video writer.");
				}
				camera = new VideoCapture(0); // Open the default camera
				if (!camera.isOpened()) {
					throw new RuntimeException("Failed to open camera.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			recordingThread = new Thread(() -> {
				long frameTime = (long) (1000 / fps); // Time between frames in milliseconds
				try {
					while (recording) {
						long startTime = System.currentTimeMillis();
						Mat combinedFrame = captureAndCombine(); // Capture and combine screen and camera frames
						if (combinedFrame.empty()) {
							System.err.println("Warning: Combined frame is empty. Skipping frame.");
							continue;
						}
						videoWriter.write(combinedFrame);
						long elapsedTime = System.currentTimeMillis() - startTime;
						long sleepTime = frameTime - elapsedTime;
						if (sleepTime > 0) {
							Thread.sleep(sleepTime); // Maintain the specified FPS
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cleanup(); // Ensure cleanup is called
				}
			});

			recordingThread.start();
		}

		public void stopRecording() {
			recording = false;
			try {
				if (recordingThread != null) {
					recordingThread.join(); // Wait for the recording thread to finish
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				cleanup(); // Ensure cleanup happens even if thread join fails
			}
		}

		private void cleanup() {
			if (videoWriter != null && videoWriter.isOpened()) {
				videoWriter.release();
				System.out.println("VideoWriter released for session: " + outputFilePath);
			}
			if (camera != null) {
				if (camera.isOpened()) {
					camera.release();
					System.out.println("Camera released.");
				} else {
					System.err.println("Camera was already closed or failed to release.");
				}
			}
			camera = null; // Ensure the camera object is nullified to help with garbage collection
			this.recordingThread = null;
		}

		private Mat captureAndCombine() {
			Mat screenMat = captureScreen();
			Mat cameraMat = captureCamera();

			if (screenMat.empty() || cameraMat.empty()) {
				return new Mat(); // Return an empty Mat if either capture fails
			}

			// Resize the camera frame to match 1/4 of the screen frame size
			Mat resizedCameraMat = new Mat();
			Size cameraSize = new Size(screenMat.cols() / 4, screenMat.rows() / 4);
			org.opencv.imgproc.Imgproc.resize(cameraMat, resizedCameraMat, cameraSize);

			// Create a new Mat for the combined frame
			Mat combinedMat = screenMat.clone(); // Start with the full screenMat

			// Place the resized camera frame in the bottom right corner
			resizedCameraMat.copyTo(combinedMat.submat(screenMat.rows() - resizedCameraMat.rows(), screenMat.rows(),
					screenMat.cols() - resizedCameraMat.cols(), screenMat.cols()));

			return combinedMat;
		}

		private Mat captureScreen() {
			try {
				if (GraphicsEnvironment.isHeadless()) {
					System.err.println("Warning: Running in a headless environment. Screen capture is not supported.");
					return new Mat(); // Return an empty Mat in headless environments
				}

				// Capture the screen using Robot
				Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
				BufferedImage screenImage = new Robot().createScreenCapture(screenRect);

				// Convert BufferedImage to Mat
				Mat mat = new Mat(screenImage.getHeight(), screenImage.getWidth(), CvType.CV_8UC3);

				if (screenImage.getType() == BufferedImage.TYPE_INT_RGB
						|| screenImage.getType() == BufferedImage.TYPE_INT_ARGB) {
					int[] rgbData = screenImage.getRGB(0, 0, screenImage.getWidth(), screenImage.getHeight(), null, 0,
							screenImage.getWidth());
					byte[] bgrData = new byte[rgbData.length * 3];

					for (int i = 0; i < rgbData.length; i++) {
						bgrData[i * 3] = (byte) (rgbData[i] & 0xFF); // Blue
						bgrData[i * 3 + 1] = (byte) ((rgbData[i] >> 8) & 0xFF); // Green
						bgrData[i * 3 + 2] = (byte) ((rgbData[i] >> 16) & 0xFF); // Red
					}
					mat.put(0, 0, bgrData);
				} else if (screenImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
					byte[] data = ((DataBufferByte) screenImage.getRaster().getDataBuffer()).getData();
					mat.put(0, 0, data);
				} else {
					System.err.println("Unsupported BufferedImage type: " + screenImage.getType());
					return new Mat(); // Return an empty Mat for unsupported types
				}

				return mat;
			} catch (HeadlessException e) {
				e.printStackTrace();
				System.err.println("Cannot capture screen in a headless environment.");
				return new Mat(); // Return an empty Mat on failure
			} catch (Exception e) {
				e.printStackTrace();
				return new Mat(); // Return an empty Mat on failure
			}
		}

		private Mat captureCamera() {
			Mat frame = new Mat();
			if (camera != null && camera.isOpened()) {
				camera.read(frame);
			}
			return frame;
		}
	}
}
