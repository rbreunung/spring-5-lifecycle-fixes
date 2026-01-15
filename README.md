# Spring 5 Lifecycle Fixes – File to DB Integration Demo

This project demonstrates a complete Spring Integration workflow that:

* **Polls a directory** for incoming files using the Spring Integration file adapter.  
* **Transforms each file** into a message that includes the original filename as a header (`file_name`).  
* **Writes the file content** to a database table (`FILE_DATA`) using Spring’s `JdbcTemplate` and HikariCP connection pooling.  
* **Initializes the database schema** automatically from `src/main/resources/db-schema.sql`.

---

## Project Structure

```plain
.
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ de/antrophos/spring/demo
│  │  │        ├─ FileToMessageTransformer.java
│  │  │        └─ FileMessageWriter.java
│  │  └─ resources
│  │        ├─ app.properties          # placeholder properties (empty)
│  │        ├─ demo-service.xml         # Spring XML configuration
│  │        └─ db-schema.sql             # DDL that creates FILE_DATA table
│  └─ test
└─ pom.xml                               # Maven build file
```

---

## Key Components

| Bean / Class | Purpose |
| -------------- | --------- |
| **`fileToMessageTransformer`** (`de.antrophos.spring.demo.FileToMessageTransformer`) | Extracts the filename from the inbound `File` and adds it as a header named `file_name`. |
| **`fileMessageWriter`** (`de.antrophos.spring.demo.FileMessageWriter`) | Receives the transformed message and writes the file content to the database using `JdbcTemplate`. |
| **`databaseInitializer`** (defined in `demo-service.xml`) | Uses Spring’s `ResourceDatabasePopulator` to execute `db-schema.sql` at startup, creating the `FILE_DATA` table if it does not exist. |
| **HikariCP DataSource** | Provides pooled connections to an H2 database file located at `${user.home}/demo-db`. |

---

## Configuration Highlights

* **Property placeholders** – External properties are loaded from `classpath:app.properties`.  
  (The file can be used to externalize values such as `hikari.username`, `hikari.password`, etc.)

* **File polling adapter** – Defined in `demo-service.xml`:

  ```xml
  <int-file:inbound-channel-adapter id="filePoller"
      directory="${input.dir}"
      filename-pattern="${file.pattern}"
      channel="fileInputChannel"
      auto-create-directory="true">
      <int:poller ref="fileToMessageTransformer"/>
  </int-file:inbound-channel-adapter>
  ```

* **Service activator** – Routes the transformed message to `fileMessageWriter`:

  ```xml
  <int:service-activator input-channel="fileInputChannel"
      ref="fileMessageWriter"
      method="write"/>
  ```

---

## Building & Running

1. **Compile the project**

   ```bash
   mvn clean compile
   ```

2. **Run the application** (requires an active Spring context)

   ```bash
   mvn spring-boot:run
   ```

   *If you prefer a plain Maven run without the Spring Boot plugin, you can start the XML configuration via a test class or a manual `ClassPathXmlApplicationContext` bootstrap.*

3. **Execute the database schema script manually** (optional)

   ```bash
   java -cp target/classes org.h2.tools.RunScript -url jdbc:h2:file:${user.home}/demo-db -script src/main/resources/db-schema.sql
   ```

---

## Customization

* **Change polling directory** – Edit `${input.dir}` in `demo-service.xml` or set it via a property placeholder.  
* **Adjust file pattern** – Modify `${file.pattern}` to match specific file extensions (e.g., `*.txt`).  
* **Database settings** – Update the HikariCP properties (`hikari.username`, `hikari.password`, etc.) in `app.properties` or directly in the XML bean definition.

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
