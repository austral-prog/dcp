package com.university.dcp

object RankingSensores {

    fun generarRanking(alarmas: List<Alarma>): List<RankingItem> {
        if (alarmas.isEmpty()) return emptyList()

        val conteo = mutableMapOf<String, Int>()
        for (a in alarmas) {
            conteo[a.idSensor] = conteo.getOrDefault(a.idSensor, 0) + 1
        }

        val items = conteo.map { (id, cantidad) -> RankingItem(id, cantidad) }

        return items.sortedWith(compareBy({ it.alarmas }, { it.idSensor }))
    }
}
