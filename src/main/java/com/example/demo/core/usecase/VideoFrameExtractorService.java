package com.example.demo.core.usecase;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class VideoFrameExtractorService {

    @Autowired
    ZipUtilsService service;

    @Autowired
    S3Service s3Service;

    public void extractFrames(String videoPath, String outputDir, String outputDirZip, String zipFileName) throws IOException {

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
        grabber.start();

        Java2DFrameConverter converter = new Java2DFrameConverter();
        int frameNumber = 0;

        while (true) {
            Frame frame = grabber.grabImage(); // Captura apenas os frames de imagem (ignora áudio)
            if (frame == null) break;

            BufferedImage bufferedImage = converter.convert(frame);
            if (bufferedImage != null) {
                File output = new File(outputDir + "/frame_" + frameNumber + ".png");
                ImageIO.write(bufferedImage, "png", output);
                frameNumber++;
            }
        }

        grabber.stop();
        grabber.release();
        System.out.println("Frames extraídos com sucesso para: " + outputDir);


        // converte em zip
        MultipartFile multipartFile = service.zipFrames(outputDir, outputDirZip, zipFileName); // Compacta e salva no diretório especificado

        s3Service.uploadFile(outputDirZip, multipartFile);
    }
}