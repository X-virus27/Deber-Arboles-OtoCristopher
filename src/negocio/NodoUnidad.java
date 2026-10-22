package negocio;

import enums.TipoUnidad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.UnidadBiblioteca;

/** Nodo n-ario: únicamente los estantes poseen un catálogo. */
public final class NodoUnidad {
    private final UnidadBiblioteca dato;
    private final List<NodoUnidad> hijos;
    private final List<NodoUnidad> vistaHijos;
    private final CatalogoABB catalogo;

    public NodoUnidad(UnidadBiblioteca dato) {
        if (dato == null) {
            throw new IllegalArgumentException("La unidad es obligatoria.");
        }
        this.dato = dato;
        this.hijos = new ArrayList<>();
        // Vista de solo lectura; no ordena ni copia los hijos.
        this.vistaHijos = Collections.unmodifiableList(hijos);
        this.catalogo = dato.getTipo() == TipoUnidad.ESTANTE ? new CatalogoABB() : null;
    }

    public UnidadBiblioteca getDato() { return dato; }
    public List<NodoUnidad> getHijos() { return vistaHijos; }
    public CatalogoABB getCatalogo() { return catalogo; }

    /** Uso interno: ArbolBiblioteca verifica también la unicidad global. */
    void agregarHijo(NodoUnidad hijo) {
        if (hijo == null || !dato.getTipo().puedeTenerHijo(hijo.dato.getTipo())) {
            throw new IllegalArgumentException("Relación padre-hijo inválida.");
        }
        hijos.add(hijo);
    }
}
