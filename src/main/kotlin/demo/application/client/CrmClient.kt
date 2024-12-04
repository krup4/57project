package demo.application.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "crmClient", url = "http://localhost:8080/")
interface CrmClient {

    @GetMapping(value = ["/score"])
    fun score(@RequestParam number: Long) : Long
}
