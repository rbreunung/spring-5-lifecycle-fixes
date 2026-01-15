package de.antrophos.spring.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;

/**
 * Writes the file name and content received via Spring Integration to an H2 database.
 */
public class FileMessageWriter {

    private JdbcTemplate jdbcTemplate;

    /**
     * Sets the {@link JdbcTemplate} to be used for DB operations.
     *
     * @param jdbcTemplate the template to set
     */
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Callback method invoked by Spring Integration when a file is received.
     * <p>
     * The message is expected to carry the file name in a header named {@code file_name}
     * and the file content as its payload (as a {@link String}).
     *
     * @param message the incoming integration message
     */
    public void write(Message<?> message) {
        // Extract file name from header; fallback to "unknown" if missing
        String fileName = (String) message.getHeaders().get("file_name");
        if (fileName == null || fileName.isEmpty()) {
            fileName = "unknown";
        }

        // Payload is expected to be a String containing the file content
        String content = (String) message.getPayload();

        // Insert the data into the FILE_DATA table
        jdbcTemplate.update(
                "INSERT INTO file_data (filename, content) VALUES (?, ?)",
                fileName,
                content);
    }
}