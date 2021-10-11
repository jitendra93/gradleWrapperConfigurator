package io.github.jitendra93.wrapperconfigurator.gradle

import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.kotlin.dsl.named
import java.io.IOException
import java.util.*
import javax.inject.Inject

open class WrapperPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {

            val configuration = extensions.create("wrapper", WrapperConfigurationExtension::class.java, this)
            val wrapper = tasks.named<Wrapper>("wrapper").get()

            tasks.create(
                    "wrapperConfiguration",
                    WrapperConfigurationTask::class.java,
                    configuration,
                    wrapper
            )
        }
    }
}


open class WrapperConfigurationTask @Inject constructor(
        @Inject val configuration: WrapperConfigurationExtension,
        @Inject val wrapper: Wrapper
) : DefaultTask() {
    init {
        group = "build setup"
        wrapper.dependsOn(this.path)
    }

    @TaskAction
    fun run() {
        val prop = configuration.properties()
        println("From plugin")
        wrapper.run {
            distributionUrl = prop.getProperty("distributionUrl")
            distributionBase = Wrapper.PathBase.valueOf(prop.getProperty("distributionBase"))
            distributionPath = prop.getProperty("distributionPath")
            archiveBase = Wrapper.PathBase.valueOf(prop.getProperty("zipStoreBase"))
            archivePath = prop.getProperty("zipStorePath")
        }
    }
}


open class WrapperConfigurationExtension @Inject constructor(@Inject val project: Project) {

    private var tag: String? = "v0.0.1"
    private var url: String = "https://raw.githubusercontent.com/jitendra93/configuration.common/$tag/wrapper.properties"
    private val client = OkHttpClient()

    fun withTag(tag: String) {
        this.tag = tag
    }

    fun from(url: String) {
        this.url = url
    }

    internal fun properties(): Properties {
        val properties = Properties()
        project.logger.lifecycle("Fetching gradle wrapper properties from $url")
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful && response.body != null) {
            properties.load(response.body?.byteStream())
            project.logger.lifecycle("Loaded gradle wrapper properties")
            project.logger.lifecycle(properties.toString())
        } else {
            throw IOException("Failed to read Gradle Wrapper properties file at $url")
        }
        return properties
    }

}
