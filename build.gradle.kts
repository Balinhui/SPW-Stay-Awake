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
    compileOnly(libs.spw.workshop.api)
    annotationProcessor(libs.spw.workshop.api)
}

spmod {
    PluginClass = "org.balinhui.stayAwakeSpwmod.MainPlugin"
    PluginId = "org.balinhui.stayAwake-spwmod"
    PluginVersion = "dev"
    PluginProvider = "Balinhui"
    PluginName = "防息屏"
    PluginDescription = "防止SPW播放时息屏"
}