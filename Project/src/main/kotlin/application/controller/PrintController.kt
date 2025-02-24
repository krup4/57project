package application.controller

import application.service.PrintService
import application.request.PrintFile
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/printer")
class PrintController (
    val printService: PrintService
) {

    @PostMapping("/print")
    fun printFile(@RequestBody printFile: PrintFile, @RequestHeader("Authorization") token: String) = printService.print(printFile, token)

    @GetMapping("/not_printed")
    fun getNotPrinted(@RequestHeader("Authorization") token: String) = printService.getNotPrinted(token)

    @GetMapping("/printed")
    fun getPrinted(@RequestHeader("Authorization") token: String) = printService.getPrinted(token)
}