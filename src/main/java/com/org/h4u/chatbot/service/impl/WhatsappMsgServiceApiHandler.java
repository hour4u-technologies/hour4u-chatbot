package com.org.h4u.chatbot.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


@Service
public class WhatsappMsgServiceApiHandler {
	
	@Value("${whatsapp-api-url}")
    private String whatsappApiUrl;
	
	@Value("${whatsapp-api-token}")
    private String token;
    
	public void postSessionMessage(String targetPhoneNumber, String paramName, String value) throws IOException {
        String charset = "UTF-8";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        
        String url = whatsappApiUrl+targetPhoneNumber;
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("Authorization", token);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
        ) {
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\""+paramName+"\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
            writer.append(CRLF).append(value).append(CRLF).flush();

            writer.append("--" + boundary + "--").append(CRLF).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int responseCode = ((HttpURLConnection) connection).getResponseCode();
        System.out.println(responseCode); 
    }

}
