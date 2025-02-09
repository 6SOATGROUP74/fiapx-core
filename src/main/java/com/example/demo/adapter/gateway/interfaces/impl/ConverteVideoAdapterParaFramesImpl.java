package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ConverteVideoFrameAdapter;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class ConverteVideoAdapterParaFramesImpl implements ConverteVideoFrameAdapter {

    @Value("${diretorio.saida.frames}")
    String sourceDirPath;

    @SneakyThrows
    @Override
    public void execute(MultipartFile arquivo) {

        // Salva o vídeo temporariamente
        File tempFile = File.createTempFile("uploaded_", ".mp4");
        arquivo.transferTo(tempFile);

        // Define o diretório de saída para os frames
        new File(sourceDirPath).mkdirs();

        // ############################## Conversão de frames ##############################

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile.getAbsolutePath());
        grabber.start();

        Java2DFrameConverter converter = new Java2DFrameConverter();
        int frameNumber = 0;

        while (true) {
            Frame frame = grabber.grabImage(); // Captura apenas os frames de imagem (ignora áudio)
            if (frame == null) break;

            BufferedImage bufferedImage = converter.convert(frame);
            if (bufferedImage != null) {
                File output = new File(sourceDirPath + "/frame_" + frameNumber + ".png");
                ImageIO.write(bufferedImage, "png", output);
                frameNumber++;
            }
        }

        grabber.stop();
        grabber.release();
        System.out.println("Frames extraídos com sucesso para: " + sourceDirPath);
    }
}
