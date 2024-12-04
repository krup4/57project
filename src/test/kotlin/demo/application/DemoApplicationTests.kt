package demo.application

import demo.application.controller.ScoreController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    lateinit var scoreController: ScoreController

    //тут тесты
}