package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ConverteVideoZipAdapter;
import com.example.demo.core.usecase.ByteArrayMultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ConverteVideoZipAdapterParaZipImpl implements ConverteVideoZipAdapter {

    @Value("${diretorio.saida.frames}")
    String sourceDirPath;

    @Value("${diretorio.saida.zip}")
    String outputDirPath;

    @Override
    public MultipartFile execute(MultipartFile file) throws IOException {
        Path sourceDir = Paths.get(sourceDirPath);
        Path outputDir = Paths.get(outputDirPath);
        Files.createDirectories(outputDir); // Garante que o diretório de saída existe

        Path zipFilePath = outputDir.resolve(file.getName()); // Define o caminho do ZIP

        // Cria um ByteArrayOutputStream para armazenar o conteúdo ZIP na memória
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (
                FileOutputStream fileOut = new FileOutputStream(zipFilePath.toFile());  // Para gravar no sistema de arquivos
                ZipOutputStream zipOutToFile = new ZipOutputStream(fileOut);            // Para gravar o arquivo ZIP fisicamente
                ZipOutputStream zipOutToMemory = new ZipOutputStream(byteArrayOutputStream)  // Para gravar na memória
        ) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path)) // Apenas arquivos, ignorando diretórios
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try (InputStream fis = Files.newInputStream(path)) {
                            // Escreve o arquivo no arquivo físico
                            zipOutToFile.putNextEntry(zipEntry);
                            fis.transferTo(zipOutToFile);  // Copia os bytes para o arquivo físico
                            zipOutToFile.closeEntry();

                            // Escreve o arquivo na memória
                            zipOutToMemory.putNextEntry(zipEntry);
                            fis.transferTo(zipOutToMemory);  // Copia os bytes para a memória
                            zipOutToMemory.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao compactar arquivo: " + path, e);
                        }
                    });
        }

        // Converte os bytes do arquivo ZIP em um MultipartFile
        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        String filename = "arquivos.zip";
        String contentType = "application/zip";

        // Cria um MultipartFile com a implementação customizada
        MultipartFile multipartFile = new ByteArrayMultipartFile("file", filename, contentType, zipBytes);
        return multipartFile;
    }
}
