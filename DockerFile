# STAGE 1: Build ứng dụng bằng Maven
# Sử dụng một image có sẵn Java 17 và Maven
FROM maven:3.8.5-openjdk-17 AS build

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Copy các file cần thiết cho Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Tải dependencies (bước này sẽ được cache lại, giúp build nhanh hơn)
RUN ./mvnw dependency:go-offline

# Copy toàn bộ code source
COPY src ./src

# Build ứng dụng và bỏ qua test
RUN ./mvnw package -DskipTests

# STAGE 2: Tạo image chạy ứng dụng (nhẹ hơn)
# Sử dụng một image Java 17 tối giản
FROM openjdk:17-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Copy file .jar đã được build từ STAGE 1
# (Đảm bảo tên file .jar là chính xác)
COPY --from=build /app/target/ideax-0.0.1-SNAPSHOT.jar app.jar

# Lệnh để chạy ứng dụng của bạn
ENTRYPOINT ["java", "-jar", "app.jar"]