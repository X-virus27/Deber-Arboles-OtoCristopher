package negocio;

import modelo.Libro;

/** Nodo binario con accesores para construir y comprobar árboles de prueba. */
public final class NodoLibro {
    private Libro dato;
    private NodoLibro izquierdo;
    private NodoLibro derecho;

    public NodoLibro(Libro dato) {
        setDato(dato);
    }

    public Libro getDato() { return dato; }
    public NodoLibro getIzquierdo() { return izquierdo; }
    public NodoLibro getDerecho() { return derecho; }

    public void setDato(Libro dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El libro del nodo es obligatorio.");
        }
        this.dato = dato;
    }

    public void setIzquierdo(NodoLibro izquierdo) { this.izquierdo = izquierdo; }
    public void setDerecho(NodoLibro derecho) { this.derecho = derecho; }
}
