package com.university.dcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AnalizadorStockTest {

    @Test
    fun `ItemStock protege sus invariantes al construirse y al copiarse`() {
        assertFailsWith<IllegalArgumentException>("Debe rechazar SKU en blanco") {
            ItemStock(sku = "   ", categoria = "Sensores", cantidad = 5, precioUnitario = 100.0, stockMinimo = 2)
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar cantidad negativa") {
            ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = -1, precioUnitario = 100.0, stockMinimo = 2)
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar precio unitario menor o igual a cero") {
            ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = 5, precioUnitario = 0.0, stockMinimo = 2)
        }
        assertFailsWith<IllegalArgumentException>("Debe rechazar stock minimo negativo") {
            ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = 5, precioUnitario = 100.0, stockMinimo = -3)
        }

        val valido = ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = 5, precioUnitario = 100.0, stockMinimo = 2)
        assertFailsWith<IllegalArgumentException>("copy tambien debe validar las invariantes") {
            valido.copy(cantidad = -10)
        }
    }

    @Test
    fun `evaluarInventario clasifica faltantes, suficiencia y exceso de stock`() {
        val items = listOf(
            ItemStock(sku = "SEN-TEMP", categoria = "Sensores", cantidad = 2, precioUnitario = 15.5, stockMinimo = 5),
            ItemStock(sku = "BAT-LIPO", categoria = "Energia", cantidad = 10, precioUnitario = 20.0, stockMinimo = 5),
            ItemStock(sku = "CAB-UTP", categoria = "Conectividad", cantidad = 20, precioUnitario = 5.0, stockMinimo = 5)
        )

        val estados = AnalizadorStock.evaluarInventario(items)
        assertEquals(3, estados.size)
        assertEquals(
            EstadoReposicion.ReposicionUrgente(sku = "SEN-TEMP", faltante = 3, costoEstimado = 46.5),
            estados[0],
            "El costo estimado de reposicion urgente debe ser faltante * precioUnitario"
        )
        assertEquals(
            EstadoReposicion.StockSuficiente(sku = "BAT-LIPO"),
            estados[1]
        )
        assertEquals(
            EstadoReposicion.ExcesoStock(sku = "CAB-UTP", excedente = 5),
            estados[2],
            "Cuando la cantidad supera 3 veces el stockMinimo debe clasificarse como ExcesoStock"
        )
    }

    @Test
    fun `costoTotalReposicionPorCategoria suma los faltantes de la categoria indicada`() {
        val items = listOf(
            ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = 1, precioUnitario = 50.0, stockMinimo = 4), // faltan 3 -> 150.0
            ItemStock(sku = "SEN-02", categoria = "Sensores", cantidad = 5, precioUnitario = 80.0, stockMinimo = 3), // suficiente -> 0.0
            ItemStock(sku = "SEN-03", categoria = "Sensores", cantidad = 2, precioUnitario = 25.5, stockMinimo = 4), // faltan 2 -> 51.0
            ItemStock(sku = "BAT-01", categoria = "Energia", cantidad = 0, precioUnitario = 100.0, stockMinimo = 2)  // otra categoria
        )

        val totalSensores = AnalizadorStock.costoTotalReposicionPorCategoria(items, "Sensores")
        assertEquals(201.0, totalSensores, 0.01)
    }

    @Test
    fun `costoTotalReposicionPorCategoria devuelve 0 cuando no hay items a reponer`() {
        val items = listOf(
            ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = 10, precioUnitario = 50.0, stockMinimo = 4)
        )

        val totalSensores = AnalizadorStock.costoTotalReposicionPorCategoria(items, "Sensores")
        assertEquals(0.0, totalSensores, 0.01, "Si ningún ítem requiere reposición debe devolver 0.0 sin lanzar excepción")

        val totalInexistente = AnalizadorStock.costoTotalReposicionPorCategoria(emptyList(), "Energia")
        assertEquals(0.0, totalInexistente, 0.01)
    }

    @Test
    fun `aplicarAjustePrecios aplica porcentajes de forma inmutable conservando todos los items`() {
        val originales = listOf(
            ItemStock(sku = "SEN-01", categoria = "Sensores", cantidad = 4, precioUnitario = 100.0, stockMinimo = 2),
            ItemStock(sku = "CAB-01", categoria = "Cables", cantidad = 10, precioUnitario = 50.0, stockMinimo = 5)
        )

        val ajustados = AnalizadorStock.aplicarAjustePrecios(
            items = originales,
            porcentajesPorCategoria = mapOf("Sensores" to 15.0)
        )

        assertEquals(2, ajustados.size, "No deben descartarse los items cuya categoria no tenga ajuste")
        assertEquals(115.0, ajustados[0].precioUnitario, 0.01, "El ajuste del 15% sobre 100.0 debe dar 115.0")
        assertEquals(50.0, ajustados[1].precioUnitario, 0.01, "Los items sin ajuste en el mapa deben conservar su precio")
        assertTrue(originales[0].precioUnitario == 100.0, "La lista original no debe mutarse")
    }
}
