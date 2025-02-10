package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ConverteVideoZipAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ConverteVideoZipAdapterParaZipImpl implements ConverteVideoZipAdapter {

    @Value("${diretorio.saida.frames}")
    String sourceDirPath;

    @Value("${diretorio.saida.zip}")
    String outputDirPath;

    @Override
    public String execute(String nomeArquivo) throws IOException {

        final String nomeArquivoTratado = nomeArquivo.replace(".mp4", "");

        Path sourceDir = Paths.get(sourceDirPath);
        Path outputDir = Paths.get(outputDirPath);
        Files.createDirectories(outputDir);

        String zipFilename = "frames-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "-" + nomeArquivoTratado + ".zip";


        Path zipFilePath = outputDir.resolve(zipFilename);

        try (
                FileOutputStream fileOut = new FileOutputStream(zipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fileOut);
        ) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try{
                            zos.putNextEntry(new ZipEntry(path.toString()));
                            Files.copy(path, zos);
                            zos.closeEntry();

                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao compactar arquivo: " + path, e);
                        }
                    });
        }

        return zipFilePath.toAbsolutePath().toString();
    }
}
