package app;

import enums.TipoUnidad;
import modelo.Libro;
import modelo.UnidadBiblioteca;
import negocio.ArbolBiblioteca;
import negocio.CatalogoABB;

/** Fábricas independientes: cada llamada crea datos nuevos, sin estado compartido. */
public final class DatosPrueba {
    private DatosPrueba() { }

    /** Metadatos de ejemplo para los códigos establecidos por la guía. */
    public static Libro libro(int codigo) {
        return new Libro(codigo, "Libro " + codigo, "Autor de ejemplo", 2026);
    }

    public static ArbolBiblioteca crearBiblioteca() {
        ArbolBiblioteca arbol = new ArbolBiblioteca();
        arbol.agregar("BIB", new UnidadBiblioteca("SW", "Software", TipoUnidad.AREA));
        arbol.agregar("SW", new UnidadBiblioteca("SW-E1", "Estante 1 de Software", TipoUnidad.ESTANTE));
        arbol.agregar("SW", new UnidadBiblioteca("SW-E2", "Estante 2 de Software", TipoUnidad.ESTANTE));
        arbol.agregar("BIB", new UnidadBiblioteca("TI", "Tecnologías de la Información", TipoUnidad.AREA));
        arbol.agregar("TI", new UnidadBiblioteca("TI-E1", "Estante 1 de TI", TipoUnidad.ESTANTE));
        arbol.agregar("BIB", new UnidadBiblioteca("TEL", "Telecomunicaciones", TipoUnidad.AREA));
        cargar(arbol.buscar("SW-E1").getCatalogo(), new int[]{60,35,80,20,45,70,90,40,75}, 0);
        cargar(arbol.buscar("SW-E2").getCatalogo(), new int[]{15,10,25}, 0);
        cargar(arbol.buscar("TI-E1").getCatalogo(), new int[]{50}, 0);
        return arbol;
    }

    public static CatalogoABB crearCatalogoPersonal() {
        CatalogoABB catalogo = crearBiblioteca().buscar("SW-E1").getCatalogo();
        // 0262 -> 02, 26 y 62: positivos, distintos y ausentes en SW-E1.
        cargar(catalogo, new int[]{2,26,62}, 0);
        return catalogo;
    }

    private static void cargar(CatalogoABB catalogo, int[] codigos, int i) {
        if (i == codigos.length) { return; }
        catalogo.insertar(libro(codigos[i]));
        cargar(catalogo, codigos, i + 1);
    }
}
