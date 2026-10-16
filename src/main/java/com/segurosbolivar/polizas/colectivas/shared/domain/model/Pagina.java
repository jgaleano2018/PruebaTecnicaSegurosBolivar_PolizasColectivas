package com.segurosbolivar.polizas.colectivas.shared.domain.model;

import java.util.List;
import java.util.function.Function;

/** Página de resultados, agnóstica de Spring Data. */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {

    public <R> Pagina<R> map(Function<T, R> mapper) {
        return new Pagina<>(contenido.stream().map(mapper).toList(), pagina, tamano, totalElementos, totalPaginas);
    }
}
