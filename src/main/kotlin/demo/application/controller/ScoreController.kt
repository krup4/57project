package demo.application.controller

import demo.application.service.ScoreService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ScoreController(
    val scoreService : ScoreService
) {

    @GetMapping("/score")
    fun score(@RequestParam number: Long) = scoreService.score(number)
}
