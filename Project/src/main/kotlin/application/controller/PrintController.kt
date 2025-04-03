package application.controller

import application.service.PrintService
import application.request.PrintFileRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/printer")
class PrintController (
    val printService: PrintService
) {

    @PostMapping("/print")
    fun printFile(printFileRequest: PrintFileRequest, @RequestHeader("Authorization") token: String) = printService.print(printFileRequest, token)

    @GetMapping("/not_printed")
    fun getNotPrinted(@RequestHeader("Authorization") token: String) = printService.getNotPrinted(token)

    @GetMapping("/printed")
    fun getPrinted(@RequestHeader("Authorization") token: String) = printService.getPrinted(token)
}