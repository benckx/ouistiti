plugins {
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    idea
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://jitpack.io")
}

dependencies {
    api(libs.jme.core)
    api(libs.jme.desktop)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.chimp.utils.basics)
    implementation(libs.chimp.utils.jme3)

    testImplementation(libs.jme.lwjgl3)
}
