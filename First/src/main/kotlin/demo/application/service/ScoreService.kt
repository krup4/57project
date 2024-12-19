package demo.application.service

import demo.application.client.CrmClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScoreService (
    private val crmClient: CrmClient
){

    fun score(number: Long) : Long {
        println(number)
        if (number < 200) {
            crmClient.score(number + 1)
        }
        return number + 1
    }
}
