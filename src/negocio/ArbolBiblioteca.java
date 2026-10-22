package negocio;

import enums.TipoUnidad;
import java.util.List;
import java.util.Locale;
import modelo.UnidadBiblioteca;

/**
 * Árbol general con una raíz BIBLIOTECA permanente.
 * Cada operación delega en sobrecargas privadas recursivas.
 * También se recorren recursivamente los hermanos, sin bucles ni streams.
 */
public class ArbolBiblioteca {
    private final NodoUnidad raiz;

    public ArbolBiblioteca() {
        this(new UnidadBiblioteca("BIB", "Biblioteca FISEI", TipoUnidad.BIBLIOTECA));
    }

    public ArbolBiblioteca(UnidadBiblioteca biblioteca) {
        if (biblioteca == null || biblioteca.getTipo() != TipoUnidad.BIBLIOTECA) {
            throw new IllegalArgumentException("La raíz debe ser una BIBLIOTECA.");
        }
        raiz = new NodoUnidad(biblioteca);
    }

    public NodoUnidad getRaiz() { return raiz; }

    /** Retorna false ante padre inexistente, código repetido o jerarquía inválida. */
    public boolean agregar(String codigoPadre, UnidadBiblioteca nueva) {
        if (nueva == null || buscar(nueva.getCodigo()) != null) {
            return false;
        }
        return agregar(raiz, normalizar(codigoPadre), nueva);
    }

    private boolean agregar(NodoUnidad nodo, String padre, UnidadBiblioteca nueva) {
        if (nodo.getDato().getCodigo().equals(padre)) {
            if (!nodo.getDato().getTipo().puedeTenerHijo(nueva.getTipo())) {
                return false;
            }
            nodo.agregarHijo(new NodoUnidad(nueva));
            return true;
        }
        return agregar(nodo.getHijos(), 0, padre, nueva);
    }

    private boolean agregar(List<NodoUnidad> hijos, int i, String padre,
                            UnidadBiblioteca nueva) {
        if (i == hijos.size()) { return false; }
        return agregar(hijos.get(i), padre, nueva) || agregar(hijos, i + 1, padre, nueva);
    }

    /** Busca sin distinguir mayúsculas; null y vacío se consideran inexistentes. */
    public NodoUnidad buscar(String codigo) {
        return buscar(raiz, normalizar(codigo));
    }

    private NodoUnidad buscar(NodoUnidad nodo, String codigo) {
        if (nodo.getDato().getCodigo().equals(codigo)) { return nodo; }
        return buscar(nodo.getHijos(), 0, codigo);
    }

    private NodoUnidad buscar(List<NodoUnidad> hijos, int i, String codigo) {
        if (i == hijos.size()) { return null; }
        NodoUnidad encontrado = buscar(hijos.get(i), codigo);
        return encontrado != null ? encontrado : buscar(hijos, i + 1, codigo);
    }

    /** Altura en aristas: una raíz sin hijos tiene altura cero. */
    public int altura() { return altura(raiz); }

    private int altura(NodoUnidad nodo) {
        return 1 + altura(nodo.getHijos(), 0);
    }

    private int altura(List<NodoUnidad> hijos, int i) {
        if (i == hijos.size()) { return -1; }
        return Math.max(altura(hijos.get(i)), altura(hijos, i + 1));
    }

    /** Cuenta unidades sin hijos, incluidas áreas sin estantes. */
    public int contarHojas() { return contarHojas(raiz); }

    private int contarHojas(NodoUnidad nodo) {
        if (nodo.getHijos().isEmpty()) { return 1; }
        return contarHojas(nodo.getHijos(), 0);
    }

    private int contarHojas(List<NodoUnidad> hijos, int i) {
        if (i == hijos.size()) { return 0; }
        return contarHojas(hijos.get(i)) + contarHojas(hijos, i + 1);
    }

    /** Raíz = 0; unidad inexistente = -1. */
    public int nivel(String codigo) { return nivel(raiz, normalizar(codigo), 0); }

    private int nivel(NodoUnidad nodo, String codigo, int profundidad) {
        if (nodo.getDato().getCodigo().equals(codigo)) { return profundidad; }
        return nivel(nodo.getHijos(), 0, codigo, profundidad + 1);
    }

    private int nivel(List<NodoUnidad> hijos, int i, String codigo, int profundidad) {
        if (i == hijos.size()) { return -1; }
        int encontrado = nivel(hijos.get(i), codigo, profundidad);
        return encontrado != -1 ? encontrado : nivel(hijos, i + 1, codigo, profundidad);
    }

    /** Preorden n-ario con exactamente dos espacios por nivel y salto LF por unidad. */
    public String mostrarJerarquia() {
        StringBuilder salida = new StringBuilder();
        mostrarJerarquia(raiz, "", salida);
        return salida.toString();
    }

    private void mostrarJerarquia(NodoUnidad nodo, String sangria, StringBuilder salida) {
        salida.append(sangria).append(nodo.getDato()).append('\n');
        mostrarJerarquia(nodo.getHijos(), 0, sangria + "  ", salida);
    }

    private void mostrarJerarquia(List<NodoUnidad> hijos, int i, String sangria,
                                 StringBuilder salida) {
        if (i == hijos.size()) { return; }
        mostrarJerarquia(hijos.get(i), sangria, salida);
        mostrarJerarquia(hijos, i + 1, sangria, salida);
    }

    public int contarLibrosTotal() { return contarLibrosTotal(raiz); }

    private int contarLibrosTotal(NodoUnidad nodo) {
        int propios = nodo.getCatalogo() == null ? 0 : nodo.getCatalogo().contar();
        return propios + contarLibrosTotal(nodo.getHijos(), 0);
    }

    private int contarLibrosTotal(List<NodoUnidad> hijos, int i) {
        if (i == hijos.size()) { return 0; }
        return contarLibrosTotal(hijos.get(i)) + contarLibrosTotal(hijos, i + 1);
    }

    /** Normaliza un dato escalar; no recorre el árbol. */
    private static String normalizar(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
    }
}
