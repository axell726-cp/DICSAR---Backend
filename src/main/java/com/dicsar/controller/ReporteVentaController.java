package com.dicsar.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.dicsar.entity.ReporteVenta;
import com.dicsar.service.ReporteVentaService;

@RestController
@RequestMapping("/api/reportes-ventas")
public class ReporteVentaController {

    private final ReporteVentaService reporteVentaService;

    public ReporteVentaController(ReporteVentaService reporteVentaService) {
        this.reporteVentaService = reporteVentaService;
    }

    @GetMapping
    public List<ReporteVenta> listar() {
        return reporteVentaService.listar();
    }

    @GetMapping("/rango-fechas")
    public List<ReporteVenta> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return reporteVentaService.obtenerPorRangoFechas(inicio, fin);
    }

    @GetMapping("/productos-mas-vendidos")
    public List<Object[]> obtenerProductosMasVendidos() {
        return reporteVentaService.obtenerProductosMasVendidos();
    }

    @GetMapping("/ventas-por-cliente")
    public List<Object[]> obtenerVentasPorCliente() {
        return reporteVentaService.obtenerVentasPorCliente();
    }

    @GetMapping("/totales-mensuales")
    public List<Object[]> obtenerTotalesMensuales() {
        return reporteVentaService.obtenerTotalesMensuales();
    }
}
