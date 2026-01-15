package de.antrophos.spring.demo;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Transformer that converts a {@link File} into a Spring Integration {@link Message}
 * with the file name stored in the "file_name" header and the file content as the
 * message payload.
 */
public class FileToMessageTransformer {

    /**
     * Transforms the given {@code File} into a message.
     *
     * @param file the incoming file
     * @return a message containing the file's UTF-8 content and a "file_name" header
     */
    public Message<String> transform(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return MessageBuilder.withPayload(content)
                    .setHeader("file_name", file.getName())
                    .build();
        } catch (Exception e) {
            // Re-throw as unchecked to keep signature simple
            throw new RuntimeException("Failed to transform file: " + file.getAbsolutePath(), e);
        }
    }
}