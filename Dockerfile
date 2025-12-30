# استخدام صورة JDK 21 الرسمية
FROM eclipse-temurin:21-jdk-alpine

# إعداد متغيرات البيئة
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE="production"

# تعيين مجلد العمل
WORKDIR /app

# نسخ ملف JAR
COPY build/libs/*.jar app.jar

# فتح المنفذ
EXPOSE 8080

# تشغيل التطبيق مع تحسينات JDK 21
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
