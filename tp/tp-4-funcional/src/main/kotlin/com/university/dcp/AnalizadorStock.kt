package com.university.dcp

data class ItemStock(
    val sku: String,
    val categoria: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val stockMinimo: Int
)

sealed class EstadoReposicion {
    data class StockSuficiente(val sku: String) : EstadoReposicion()
    data class ReposicionUrgente(val sku: String, val faltante: Int, val costoEstimado: Double) : EstadoReposicion()
    data class ExcesoStock(val sku: String, val excedente: Int) : EstadoReposicion()
}

object AnalizadorStock {

    fun evaluarInventario(items: List<ItemStock>): List<EstadoReposicion> {
        val resultado = mutableListOf<EstadoReposicion>()
        for (item in items) {
            if (item.cantidad < item.stockMinimo) {
                val faltante = item.stockMinimo - item.cantidad
                resultado.add(
                    EstadoReposicion.ReposicionUrgente(
                        sku = item.sku,
                        faltante = faltante,
                        costoEstimado = redondear(item.precioUnitario)
                    )
                )
            } else {
                resultado.add(EstadoReposicion.StockSuficiente(item.sku))
            }
        }
        return resultado
    }

    fun costoTotalReposicionPorCategoria(items: List<ItemStock>, categoria: String): Double {
        val costos = items
            .filter { it.categoria == categoria && it.cantidad < it.stockMinimo }
            .map { (it.stockMinimo - it.cantidad) * it.precioUnitario }
        return redondear(costos.reduce { acumulado, costo -> acumulado + costo })
    }

    fun aplicarAjustePrecios(
        items: List<ItemStock>,
        porcentajesPorCategoria: Map<String, Double>
    ): List<ItemStock> {
        val ajustados = mutableListOf<ItemStock>()
        for (item in items) {
            val pct = porcentajesPorCategoria[item.categoria]
            if (pct != null) {
                ajustados.add(
                    item.copy(precioUnitario = redondear(item.precioUnitario + pct))
                )
            }
        }
        return ajustados
    }

    private fun redondear(valor: Double): Double =
        Math.round(valor * 100.0) / 100.0
}
