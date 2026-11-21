plugins {
    id("java")
    id("com.xuncorp.spw.workshop") version "0.1.0-dev22"
}

group = "org.balinhui"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly(libs.spw.workshop.api)
    annotationProcessor(libs.spw.workshop.api)
}

tasks.test {
    useJUnitPlatform()
}

spmod {
    PluginClass = "org.balinhui.stayAwakeSpwmod.MainPlugin"
    PluginId = "org.balinhui.stayAwake"
    PluginVersion = "v0.1.0-dev"
    PluginProvider = "Balinhui"
    PluginName = "防息屏"
    PluginDescription = "防止SPW播放时息屏"
}