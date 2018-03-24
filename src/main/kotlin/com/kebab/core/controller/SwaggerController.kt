package com.kebab.core.controller

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kebab.core.exception.UnknownUrlException
import com.kebab.core.util.lazyLogger
import com.kebab.core.util.mapper
import com.kebab.core.util.toJson
import io.swagger.annotations.Api
import org.apache.commons.lang3.StringUtils.EMPTY
import org.apache.commons.lang3.StringUtils.appendIfMissing
import org.apache.commons.lang3.StringUtils.defaultString
import org.apache.commons.lang3.StringUtils.prependIfMissing
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.util.MimeTypeUtils.TEXT_HTML_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

@RestController
@Api(hidden = true)
class SwaggerController(private val resourceLoader: ResourceLoader) {

    private val log by lazyLogger()

    @GetMapping("/", produces = [TEXT_HTML_VALUE])
    fun swaggerIndexPage() = resourceLoader.getResource(SWAGGER_UI_PAGE)!!
            .also { if (!it.exists()) throw UnknownUrlException(UnknownUrlException.REASON) }

    @Throws(IOException::class)
    @GetMapping(value = ["/swagger.json", "/swagger/swagger.json"], produces = [APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE])
    fun swaggerJson(webRequest: ServletWebRequest, @RequestParam(value = "basePath", required = false, defaultValue = EMPTY) basePath: String) =
            resourceLoader.getResource(SWAGGER_JSON_RESOURCE)?.run {
                if (!exists()) throw UnknownUrlException(UnknownUrlException.REASON)

                getBasePath(basePath, webRequest.request.requestURL.toString()).run {
                    (mapper().readTree(inputStream) as ObjectNode)
                            .also {
                                it.put(HOST, "$host:${if (this.port == -1) defaultPort else this.port}")

                                val contextPath = path.removeSuffix(SWAGGER_JSON).removeSuffix(SWAGGER_HOME)

                                it.put(BASE_PATH, prependIfMissing(appendIfMissing(contextPath, SLASH), SLASH))
                            }.toJson()
                }
            }

    @Throws(MalformedURLException::class)
    private fun getBasePath(basePath: String, remoteAddress: String) = try {
        URL(defaultString(basePath, remoteAddress))
    } catch (exception: Exception) {
        log.warn("System can't parse `basePath` parameter and uses Request's remote address instead", exception)
                .let { URL(remoteAddress) }
    }

    companion object {

        private const val BASE_PATH = "basePath"

        private const val HOST = "host"

        private const val SWAGGER_JSON = "/swagger.json"

        private const val SWAGGER_HOME = "/swagger"

        private const val SWAGGER_JSON_RESOURCE = "classpath:/public/$SWAGGER_HOME$SWAGGER_JSON"

        private const val SWAGGER_UI_PAGE = "classpath:/public/index.html"

        private const val SLASH = "/"

    }
}