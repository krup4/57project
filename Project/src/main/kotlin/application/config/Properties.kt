package application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("spring.config")
@RefreshScope
open class Properties {
    open val secret: String = "test"
}