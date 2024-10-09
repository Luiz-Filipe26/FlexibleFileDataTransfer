package br.luizlabs.flexiblefiledatatransfer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {

    public static void save(String filePath, String format, String content) {
        File file = new File(filePath, "received_message." + format.toLowerCase());

        // Verificação se o diretório existe
        File parentDir = file.getParentFile();
        if (parentDir == null || !parentDir.exists()) {
            System.err.println("O diretório não existe: " + (parentDir != null ? parentDir.getAbsolutePath() : "Diretório inválido"));
            return;
        }

        // Escrita do conteúdo no arquivo
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("Arquivo salvo com sucesso: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}