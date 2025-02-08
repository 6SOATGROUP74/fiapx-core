package com.example.demo.core.usecase;

import org.springframework.stereotype.Service;
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

@Service
public class ZipUtilsService {

    public MultipartFile zipFrames(String sourceDirPath, String outputDirPath, String zipFileName) throws IOException {
        Path sourceDir = Paths.get(sourceDirPath);
        Path outputDir = Paths.get(outputDirPath);
        Files.createDirectories(outputDir); // Garante que o diretório de saída existe

        Path zipFilePath = outputDir.resolve(zipFileName); // Define o caminho do ZIP

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


    //Funciona
//    public MultipartFile zipFrames(String sourceDirPath, String outputDirPath, String zipFileName) throws IOException {
//        Path sourceDir = Paths.get(sourceDirPath);
//        Path outputDir = Paths.get(outputDirPath);
//        Files.createDirectories(outputDir); // Garante que o diretório de saída existe
//
//        Path zipFilePath = outputDir.resolve(zipFileName); // Define o caminho do ZIP
//
//
//        //UPLOAD no S3
//        // Cria um ByteArrayOutputStream para armazenar o conteúdo ZIP na memória
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
//            Files.walk(sourceDir)
//                    .filter(path -> !Files.isDirectory(path)) // Apenas arquivos, ignorando diretórios
//                    .forEach(path -> {
//                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
//                        try (InputStream fis = Files.newInputStream(path)) {
//                            zipOut.putNextEntry(zipEntry);
//                            fis.transferTo(zipOut); // Copia os bytes para o ZIP
//                            zipOut.closeEntry();
//                        } catch (IOException e) {
//                            throw new RuntimeException("Erro ao compactar arquivo: " + path, e);
//                        }
//                    });
//        }
//        System.out.println("Arquivo ZIP criado em: " + zipFilePath);
//
//        // Converte os bytes do arquivo ZIP em um MultipartFile
//        byte[] zipBytes = byteArrayOutputStream.toByteArray();
//        String filename = "arquivos.zip";
//        String contentType = "application/zip";
//
//        // Cria um MultipartFile com a implementação customizada
//        MultipartFile multipartFile = new ByteArrayMultipartFile("file", filename, contentType, zipBytes);
//        return multipartFile;
//    }



//    public void zipFrames(String framesDirPath, String outputZipPath) throws IOException {
//        File framesDir = new File(framesDirPath);
//        File[] files = framesDir.listFiles(); // Lista os arquivos da pasta
//
//        if (files == null || files.length == 0) {
//            throw new IOException("Nenhum frame encontrado para compactar.");
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
//             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
//
//            for (File file : files) {
//                try (FileInputStream fis = new FileInputStream(file)) {
//                    ZipEntry zipEntry = new ZipEntry(file.getName());
//                    zipOut.putNextEntry(zipEntry);
//
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = fis.read(buffer)) > 0) {
//                        zipOut.write(buffer, 0, length);
//                    }
//                }
//            }
//        }
//        System.out.println("Frames compactados com sucesso em: " + outputZipPath);
//    }
}