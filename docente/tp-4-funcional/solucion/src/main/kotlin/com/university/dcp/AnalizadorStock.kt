package com.university.dcp

data class ItemStock(
    val sku: String,
    val categoria: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val stockMinimo: Int
) {
    init {
        require(sku.isNotBlank()) { "El SKU no puede estar en blanco" }
        require(categoria.isNotBlank()) { "La categoria no puede estar en blanco" }
        require(cantidad >= 0) { "La cantidad no puede ser negativa" }
        require(precioUnitario > 0.0) { "El precio unitario debe ser positivo" }
        require(stockMinimo >= 0) { "El stock minimo no puede ser negativo" }
    }
}

sealed class EstadoReposicion {
    data class StockSuficiente(val sku: String) : EstadoReposicion()
    data class ReposicionUrgente(val sku: String, val faltante: Int, val costoEstimado: Double) : EstadoReposicion()
    data class ExcesoStock(val sku: String, val excedente: Int) : EstadoReposicion()
}

object AnalizadorStock {

    fun evaluarInventario(items: List<ItemStock>): List<EstadoReposicion> =
        items.map { item ->
            when {
                item.cantidad < item.stockMinimo -> {
                    val faltante = item.stockMinimo - item.cantidad
                    EstadoReposicion.ReposicionUrgente(
                        sku = item.sku,
                        faltante = faltante,
                        costoEstimado = redondear(faltante * item.precioUnitario)
                    )
                }
                item.stockMinimo > 0 && item.cantidad > item.stockMinimo * 3 -> {
                    EstadoReposicion.ExcesoStock(
                        sku = item.sku,
                        excedente = item.cantidad - item.stockMinimo * 3
                    )
                }
                else -> EstadoReposicion.StockSuficiente(item.sku)
            }
        }

    fun costoTotalReposicionPorCategoria(items: List<ItemStock>, categoria: String): Double =
        redondear(
            items
                .filter { it.categoria == categoria && it.cantidad < it.stockMinimo }
                .map { (it.stockMinimo - it.cantidad) * it.precioUnitario }
                .fold(0.0) { acumulado, costo -> acumulado + costo }
        )

    fun aplicarAjustePrecios(
        items: List<ItemStock>,
        porcentajesPorCategoria: Map<String, Double>
    ): List<ItemStock> =
        items.map { item ->
            val pct = porcentajesPorCategoria[item.categoria] ?: 0.0
            val nuevoPrecio = redondear(item.precioUnitario * (1.0 + pct / 100.0))
            item.copy(precioUnitario = nuevoPrecio)
        }

    private fun redondear(valor: Double): Double =
        Math.round(valor * 100.0) / 100.0
}
