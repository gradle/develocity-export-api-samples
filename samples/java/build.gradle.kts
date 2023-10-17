plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-sse")
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.15.2"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.google.guava:guava:32.1.2-jre")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

application {
    mainClass.set("com.gradle.enterprise.export.ExportApiJavaExample")
}