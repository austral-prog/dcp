package com.university.dcp

object ClasificadorAlarmas {

    data class Umbral(
        val minNormal: Double,
        val maxNormal: Double,
        val deltaGrave: Double
    )

    private val umbrales = mapOf(
        "temperatura" to Umbral(18.0, 27.0, 3.0),
        "humedad" to Umbral(30.0, 70.0, 10.0),
        "presion" to Umbral(980.0, 1030.0, 15.0)
    )

    fun umbralPara(tipo: String): Umbral? = umbrales[tipo]

    fun evaluar(lectura: Lectura, valorCalibrado: Double): Alarma? {
        val umbral = umbrales[lectura.tipo] ?: return null

        val fueraDeRango = when (lectura.tipo) {
            "temperatura" -> valorCalibrado < umbral.minNormal || valorCalibrado >= umbral.maxNormal
            else -> valorCalibrado < umbral.minNormal || valorCalibrado > umbral.maxNormal
        }
        if (!fueraDeRango) return null

        val esGrave = valorCalibrado < (umbral.minNormal - umbral.deltaGrave) ||
            valorCalibrado > (umbral.maxNormal + umbral.deltaGrave)

        val severidad = if (esGrave) Severidad.GRAVE else Severidad.LEVE

        return Alarma(
            severidad = severidad,
            idSensor = lectura.idSensor,
            timestamp = lectura.timestamp,
            tipo = lectura.tipo,
            valor = valorCalibrado,
            unidadBase = ConversorUnidades.unidadBase(lectura.tipo),
            rangoMin = umbral.minNormal,
            rangoMax = umbral.maxNormal
        )
    }
}
