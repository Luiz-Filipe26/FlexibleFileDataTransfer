package br.luizlabs.flexiblefiledatatransfer;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.Gson;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FlexibleFileDataTransferClient {
    
    record MessageData(String name, String cpf, String age, String message) {}

    public static void main(String[] args) {
        
        KeyboardHandler keyboardHandler = KeyboardHandler.getInstance();
        
        SocketManager socketManager = new SocketManager("localhost", 12345);
        
        String name = keyboardHandler.getInput(input -> !input.isBlank(), "Por favor, digite seu nome: ", "[!] Nome em branco, digite novamente: ");
        String cpf  = keyboardHandler.getInput(input -> input.matches("\\d{11}"), "Por favor, digite seu CPF (somente números): ", "[!] CFP inválido, digite novamente: ");
        String age = "" + keyboardHandler.getIntInput(input -> input >= 0, "Por favor, digite sua idade: ", "[!] Idade inválida, digite novamente: ");
        String message = keyboardHandler.getInput(input -> !input.isBlank(), "Digite uma mensagem: ", "[!] Mensagem vazia, digite novamente: ");
        
        MessageData messageData = new MessageData(name, cpf, age, message);
        
        String jsonMessage = convertToJson(messageData);
        
        String csvMessage  = convertToCSV(messageData);
        
        String xmlMessage  = convertToXML(messageData);
        
        String yamlMessage = convertToYaml(messageData);
        
        String tomlMessage = convertToToml(messageData);
        socketManager.sendMessage(jsonMessage, "JSON");
        socketManager.sendMessage(csvMessage, "CSV");
        socketManager.sendMessage(xmlMessage, "XML");
        socketManager.sendMessage(yamlMessage, "YAML");
        socketManager.sendMessage(tomlMessage, "TOML");
        
        socketManager.closeConnection();
        
    }
    
    public static String convertToJson(MessageData messageData) {
        Gson gson = new Gson();
        
        return gson.toJson(messageData);
    }
    
    public static String convertToCSV(MessageData messageData) {
        return String.join(",", messageData.name(), messageData.cpf(), messageData.age(), messageData.message());
    }
    
    public static String convertToXML(MessageData messageData) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.writeValueAsString(messageData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String convertToYaml(MessageData messageData) {
        try {
            YAMLMapper yamlMapper = new YAMLMapper();
            return yamlMapper.writeValueAsString(messageData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String convertToToml(MessageData messageData) {
        try {
            TomlMapper tomlMapper = new TomlMapper();
            return tomlMapper.writeValueAsString(messageData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}