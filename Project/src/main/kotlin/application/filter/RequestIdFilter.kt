package application.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class RequestIdFIlter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            MDC.put("requestId", UUID.randomUUID().toString())
            logger.debug("Was received ${request.method} request on uri: ${request.requestURI}")
            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }
}