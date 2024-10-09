package br.luizlabs.flexiblefiledatatransfer;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;

public class FlexibleFileDataTransferServer {

    public record MessageData(String name, String cpf, String age, String message) {}

    public static void main(String[] args) {
        SocketManager socketManager = new SocketManager(12345);

        boolean receivedAllTypes = false;

        while (!receivedAllTypes) {
            SocketManager.ReceivedMessage receivedMessage = socketManager.receiveMessage();

            if (receivedMessage != null) {
                String dataType = receivedMessage.dataType();
                String messageContent = receivedMessage.completeMessage();
                MessageData messageData = null;

                switch (dataType.toUpperCase()) {
                    case "JSON" -> messageData = handleJsonMessage(messageContent);
                    case "CSV" -> messageData = handleCsvMessage(messageContent);
                    case "XML" -> messageData = handleXmlMessage(messageContent);
                    case "YAML" -> messageData = handleYamlMessage(messageContent);
                    case "TOML" -> {
                        messageData = handleTomlMessage(messageContent);
                        receivedAllTypes = true;
                    }
                    default -> System.out.println("Tipo de mensagem não suportado: " + dataType);
                }

                if (messageData != null) {
                    FileSaver.save("C:\\Users\\0049315\\output", dataType, messageContent);
                    printMessage(dataType, messageData);
                }
            }
        }

        socketManager.closeConnection();
        System.out.println("Lido todos os tipos!");
    }
    
    private static void printMessage(String dataType, MessageData messageData) {
       System.out.println("Recebido (" + dataType + "):");
       System.out.println("  Nome: " + messageData.name());
       System.out.println("  CPF: " + messageData.cpf());
       System.out.println("  Idade: " + messageData.age());
       System.out.println("  Mensagem: " + messageData.message());
   }

    private static MessageData handleJsonMessage(String messageContent) {
        Gson gson = new Gson();
        return gson.fromJson(messageContent, MessageData.class);
    }

    private static MessageData handleCsvMessage(String messageContent) {
        String[] fields = messageContent.split(",");
        if (fields.length == 4) {
            return new MessageData(fields[0], fields[1], fields[2], fields[3]);
        } else {
            System.out.println("Formato CSV inválido");
            return null;
        }
    }

    private static MessageData handleXmlMessage(String messageContent) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(messageContent, MessageData.class);
        } catch (Exception e) {
            System.out.println("Erro ao desserializar XML: " + e.getMessage());
            return null;
        }
    }

    private static MessageData handleYamlMessage(String messageContent) {
        try {
            YAMLMapper yamlMapper = new YAMLMapper();
            return yamlMapper.readValue(messageContent, MessageData.class);
        } catch (Exception e) {
            System.out.println("Erro ao desserializar YAML: " + e.getMessage());
            return null;
        }
    }

    private static MessageData handleTomlMessage(String messageContent) {
        try {
            TomlMapper tomlMapper = new TomlMapper();
            return tomlMapper.readValue(messageContent, MessageData.class);
        } catch (Exception e) {
            System.out.println("Erro ao desserializar TOML: " + e.getMessage());
            return null;
        }
    }
}