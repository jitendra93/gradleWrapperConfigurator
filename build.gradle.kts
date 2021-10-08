import java.net.URI
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.jitendraalekar"
version = "1.0"


repositories {
    mavenCentral()
}

gradlePlugin{
    plugins {
        register("com.jitendraalekar.wrapperconfigurator"){
            id ="com.jitendraalekar.wrapperconfigurator"
            implementationClass = "com.jitendraalekar.wrapperconfigurator.gradle.WrapperPlugin"
        }
    }
}

publishing{
    publications{
        register<MavenPublication>("pluginMaven"){
            artifactId = "gradle-wrapper-configurator"
        }
    }
    repositories {
        mavenLocal()/*
        maven{
            url = URI.create("URL_TO_ARTIFACTORY")
            credentials{
                username = "TODO_REPLACE_USERNAME"
                password = "TODO_REPLACE_KEY"
            }
        }*/
    }
}



dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


/*
tasks.named<Copy>("processResources"){
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}*/
