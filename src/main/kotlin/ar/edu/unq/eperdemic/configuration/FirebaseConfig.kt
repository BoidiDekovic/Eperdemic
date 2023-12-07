package ar.edu.unq.eperdemic.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.FileInputStream

@Component
class FirebaseConfig {

    @Bean
    fun initializeFirebaseApp(): FirebaseApp {

        val appName = "epersbluelabel"

        // Verificar si ya existe una instancia con el mismo nombre
        val existingApp = FirebaseApp.getApps().find { it.name == appName }
        if (existingApp != null) {
            return existingApp
        }

        FileInputStream("src/main/resources/serviceAcountKey.json").use { serviceAccount ->
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://epersbluelabel-default-rtdb.firebaseio.com/")
                .build()
            return FirebaseApp.initializeApp(options, appName)
        }
    }

    @Bean
    fun firebaseDbRef(@Autowired appFirebase: FirebaseApp): DatabaseReference {
        return FirebaseDatabase.getInstance(appFirebase).reference
    }
}