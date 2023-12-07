package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.dao.helper.service.DataService
import ar.edu.unq.eperdemic.modelo.Distrito
import ar.edu.unq.eperdemic.modelo.Tribu
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.vector.Vector
import ar.edu.unq.eperdemic.modelo.vector.VectorAnimal
import ar.edu.unq.eperdemic.modelo.vector.VectorHumano
import ar.edu.unq.eperdemic.modelo.vector.VectorInsecto
import ar.edu.unq.eperdemic.persistencia.dao.DistritoDAO
import ar.edu.unq.eperdemic.services.DistritoService
import ar.edu.unq.eperdemic.services.TribuService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.impl.helper.RNGImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TribuDemo {

    @Autowired
    private lateinit var rng : RNGImpl
    @Autowired
    private lateinit var dataService: DataService
    @Autowired
    private lateinit var tribuService: TribuService
    @Autowired
    private lateinit var vectorService: VectorService
    @Autowired
    private lateinit var ubicacionService: UbicacionService
    @Autowired
    private lateinit var distritoService: DistritoService
    @Autowired
    private lateinit var distritoDAO: DistritoDAO

    private lateinit var tribuHermandadMutarachil: Tribu
    private lateinit var tribuAmantesDeLaRadiacion: Tribu
    private lateinit var tribuLiberadoresDeKotlin: Tribu
    private lateinit var tribuMutardos: Tribu
    private lateinit var tribuNucleologos: Tribu
    private lateinit var tribuOrdenDelNucleo: Tribu
    private lateinit var tribuDefensoresDeReact: Tribu

    private lateinit var distritoKotlin: Distrito
    private lateinit var ubicacionYermo: Ubicacion

    private lateinit var vectorMutaracha0: Vector
    private lateinit var vectorAbeja0: Vector
    private lateinit var vectorTochomosca0: Vector
    private lateinit var vectorHombrePinza0: Vector
    private lateinit var vectorMutardo: Vector
    private lateinit var vectorNecrofago: Vector
    private lateinit var vectorCrocosaurio: Vector

    @BeforeEach
    fun setUp() {
        distritoKotlin = Distrito("Continente de Kotlin",
            GeoJsonPolygon(listOf(
                GeoJsonPoint(0.0, 0.0),
                GeoJsonPoint(3.0, 6.0),
                GeoJsonPoint(6.0, 1.0),
                GeoJsonPoint(0.0, 0.0)
            ))
        )
        ubicacionYermo = Ubicacion("Yermo")

        vectorMutaracha0 = VectorInsecto("Mutaracha 0", ubicacionYermo)
        vectorAbeja0 = VectorInsecto("Abeja Radioactiva 0", ubicacionYermo)
        vectorTochomosca0 = VectorInsecto("Tochomosca Gigante 0", ubicacionYermo)
        vectorHombrePinza0 = VectorHumano("Hombre Pinza 0", ubicacionYermo)
        vectorMutardo = VectorHumano("Mutardo 0", ubicacionYermo)
        vectorNecrofago = VectorHumano("Necrofago 0", ubicacionYermo)
        vectorCrocosaurio = VectorAnimal("Crocosaurio 0", ubicacionYermo)

        tribuHermandadMutarachil = Tribu("Hermandad Mutarachil", vectorMutaracha0)
        tribuAmantesDeLaRadiacion = Tribu("Amantes de la Radiacion", vectorAbeja0)
        tribuLiberadoresDeKotlin = Tribu("Liberadores de Kotlin", vectorTochomosca0)
        tribuMutardos = Tribu("Los Mutardos", vectorMutardo)
        tribuNucleologos = Tribu("Nucleologos", vectorHombrePinza0)
        tribuOrdenDelNucleo = Tribu("La Orden del Nucleo", vectorNecrofago)
        tribuDefensoresDeReact = Tribu("Defensores de React", vectorCrocosaurio)
    }

    @Test
    fun demoTearDown() {
        tribuService.deleteAll()
        dataService.cleanAll()
        distritoDAO.deleteAll()
    }

    @Test
    fun setUpDemo() {
        distritoService.crearDistrito(distritoKotlin)
        ubicacionService.crearUbicacion(ubicacionYermo, GeoJsonPoint(0.0, 0.0))

        vectorService.crearVector(vectorMutaracha0)
        vectorService.crearVector(vectorAbeja0)
        vectorService.crearVector(vectorTochomosca0)
        vectorService.crearVector(vectorHombrePinza0)
        vectorService.crearVector(vectorMutardo)
        vectorService.crearVector(vectorNecrofago)
        vectorService.crearVector(vectorCrocosaurio)

        for (i in 1..50) {
            val nombreMutaracha = "Mutaracha $i"
            vectorService.crearVector(VectorInsecto(nombreMutaracha, ubicacionYermo))
            tribuHermandadMutarachil.agregarIntegrante(nombreMutaracha)
        }
        tribuService.crearTribu(tribuHermandadMutarachil)

        for (i in 1..45) {
            val nombreAbeja = "Abeja Radioactivas $i"
            vectorService.crearVector(VectorInsecto(nombreAbeja, ubicacionYermo))
            tribuAmantesDeLaRadiacion.agregarIntegrante(nombreAbeja)
        }
        tribuService.crearTribu(tribuAmantesDeLaRadiacion)

        for (i in 1..48) {
            val nombreTochomosca = "Tochomosca Gigante $i"
            vectorService.crearVector(VectorInsecto(nombreTochomosca, ubicacionYermo))
            tribuLiberadoresDeKotlin.agregarIntegrante(nombreTochomosca)
        }
        tribuService.crearTribu(tribuLiberadoresDeKotlin)

        for (i in 1..32) {
            val nombreMutardo = "Mutardo $i"
            vectorService.crearVector(VectorHumano(nombreMutardo, ubicacionYermo))
            tribuMutardos.agregarIntegrante(nombreMutardo)
        }
        tribuService.crearTribu(tribuMutardos)

        for (i in 1..43) {
            val nombreHombrePinza = "Hombre Pinza $i"
            vectorService.crearVector(VectorHumano(nombreHombrePinza, ubicacionYermo))
            tribuNucleologos.agregarIntegrante(nombreHombrePinza)
        }
        tribuService.crearTribu(tribuNucleologos)

        for (i in 1..56) {
            val nombreNecrofago = "Necrofago $i"
            vectorService.crearVector(VectorHumano(nombreNecrofago, ubicacionYermo))
            tribuOrdenDelNucleo.agregarIntegrante(nombreNecrofago)
        }
        tribuService.crearTribu(tribuOrdenDelNucleo)

        for (i in 1..73) {
            val nombreCrocosaurio = "Crocosaurio $i"
            vectorService.crearVector(VectorAnimal(nombreCrocosaurio, ubicacionYermo))
            tribuDefensoresDeReact.agregarIntegrante(nombreCrocosaurio)
        }
        tribuService.crearTribu(tribuDefensoresDeReact)
    }

    @Test
    fun demo1() {
        tribuService.pelearEntreTribus(tribuHermandadMutarachil.nombre, tribuDefensoresDeReact.nombre)
    }

    @Test
    fun demo2() {
        tribuService.pelearEntreTribus(tribuOrdenDelNucleo.nombre, tribuLiberadoresDeKotlin.nombre)
    }
}