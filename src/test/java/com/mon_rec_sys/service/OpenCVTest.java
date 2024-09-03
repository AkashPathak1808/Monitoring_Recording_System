package com.mon_rec_sys.service;

import org.junit.jupiter.api.Test;
import org.opencv.core.Core;
import org.opencv.videoio.VideoWriter;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OpenCVTest {
	@Test
	void test() {
		System.out.println("Hii");
//		System.setProperty("java.library.path", "C:\\opencv\\build\\bin");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Mar Gaya");
		try {
			int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
			System.out.println("FOURCC: " + fourcc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
