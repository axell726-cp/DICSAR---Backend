package com.dicsar.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.dicsar.entity.ReporteVenta;
import com.dicsar.repository.ReporteVentaRepository;

@Service
public class ReporteVentaService {

    private final ReporteVentaRepository reporteVentaRepository;

    public ReporteVentaService(ReporteVentaRepository reporteVentaRepository) {
        this.reporteVentaRepository = reporteVentaRepository;
    }

    public List<ReporteVenta> listar() {
        return reporteVentaRepository.findAll();
    }

    public List<ReporteVenta> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return reporteVentaRepository.findByFechaVentaBetween(inicio, fin);
    }

    public List<Object[]> obtenerProductosMasVendidos() {
        return reporteVentaRepository.obtenerProductosMasVendidos();
    }

    public List<Object[]> obtenerVentasPorCliente() {
        return reporteVentaRepository.obtenerVentasPorCliente();
    }

    public List<Object[]> obtenerTotalesMensuales() {
        return reporteVentaRepository.obtenerTotalesMensuales();
    }
}
