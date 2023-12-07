package ar.edu.unq.eperdemic.dao.helper.service

import ar.edu.unq.eperdemic.dao.helper.dao.DataDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class DataServiceImpl (): DataService {

    @Autowired private lateinit var dataDAO: DataDAO

    override fun cleanAll() {
        dataDAO.clear()
    }
}