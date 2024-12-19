package demo.application

import demo.application.controller.ScoreController
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DemoApplicationTests {

    @Autowired
    lateinit var scoreController: ScoreController

    @Test
    fun firstTest() {
        val request = 1L
        val result = scoreController.score(request)
        Assertions.assertEquals(2L, result)
    }

    @Test
    fun secondTest() {
        val request = 52L
        val result = scoreController.score(request)
        Assertions.assertEquals(53L, result)
    }

    @Test
    fun thirdTest() {
        val request = 220L
        val result = scoreController.score(request)
        Assertions.assertEquals(221L, result)
    }
}

//
