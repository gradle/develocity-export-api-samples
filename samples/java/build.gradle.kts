plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.1.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp-sse")
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.19.1"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("com.google.guava:guava:33.4.8-jre")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

application {
    mainClass.set("com.develocity.export.ExportApiJavaExample")
}
