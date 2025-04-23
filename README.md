# 🚀 MountainTopLiquors Project

# How to Clone, Build, and Run This Java Project

## 📦 Prerequisites

Make sure you have the following installed:

- **Java JDK** (version 8 or above)  
  [Install Java JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- **Maven** (for dependency management and build)  
  [Install Maven](https://maven.apache.org/install.html)
- **Git** (to clone the repository)  
  [Install Git](https://git-scm.com/downloads)

---

## 📁 Step 1: Clone the Repository

Open your terminal or command prompt and run:

```bash
git clone https://github.com/NaveenGangadhara121/Gangadhara_Naveen_COMP_699_C.git
cd Gangadhara_Naveen_COMP_699_C
```

---

## 🛠️ Step 2: Fix Maven Errors (Optional but Important)

If you face issues with the `pom.xml` file:
- Make sure the **parent POM** uses `<packaging>pom</packaging>`
- Make sure no submodule references itself as a dependency

---

## 🔨 Step 3: Build the Project

Use Maven to clean and build:

```bash
mvn clean install
```

This will download dependencies and compile the source code.

---

## 🚀 Step 4: Run the Spring Boot Application

If your project has a `main()` class with Spring Boot, such as:

```java
@SpringBootApplication
public class EcommLiquorApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommLiquorApplication.class, args);
    }
}
```

You can run it with:

```bash
cd Ecomm-Liquor-SpringBoot
mvn spring-boot:run
```

Alternatively, run the `.jar` file after building:

```bash
java -jar target/your-artifact-name.jar
```

---

## 🌐 Access the App (If Web-Based)

Once it runs successfully, open your browser and go to:

```
http://localhost:8080
```

Or the port configured in your `application.properties`.

---

## ✅ Done!

Your Java Spring Boot project is now running. You can start testing or developing further.
