package negocio;

import enums.TipoRecorrido;
import java.util.ArrayList;
import java.util.List;
import modelo.Libro;

public class CatalogoABB {
    private NodoLibro raiz;

    public NodoLibro getRaiz() { return raiz; }

    /** Permite montar un árbol desordenado para comprobar esABBValido(). */
    public void setRaiz(NodoLibro raiz) { this.raiz = raiz; }

    /** Inserta en O(h); un duplicado conserva el libro original. */
    public boolean insertar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro es obligatorio.");
        }
        boolean[] insertado = {false};
        raiz = insertar(raiz, libro, insertado);
        return insertado[0];
    }

    private NodoLibro insertar(NodoLibro nodo, Libro libro, boolean[] insertado) {
        if (nodo == null) {
            insertado[0] = true;
            return new NodoLibro(libro);
        }
        int codigo = libro.getCodigo();
        int actual = nodo.getDato().getCodigo();
        if (codigo < actual) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), libro, insertado));
        } else if (codigo > actual) {
            nodo.setDerecho(insertar(nodo.getDerecho(), libro, insertado));
        }
        return nodo;
    }

    public Libro buscar(int codigo) { return buscar(raiz, codigo); }

    private Libro buscar(NodoLibro nodo, int codigo) {
        if (nodo == null) { return null; }
        int actual = nodo.getDato().getCodigo();
        if (codigo == actual) { return nodo.getDato(); }
        return codigo < actual ? buscar(nodo.getIzquierdo(), codigo)
                               : buscar(nodo.getDerecho(), codigo);
    }

    public int contar() { return contar(raiz); }

    private int contar(NodoLibro nodo) {
        if (nodo == null) { return 0; }
        return 1 + contar(nodo.getIzquierdo()) + contar(nodo.getDerecho());
    }

    /** Obtiene el recorrido directamente de la estructura, sin ordenar listas. */
    public List<Libro> recorrer(TipoRecorrido tipo) {
        validarTipo(tipo);
        List<Libro> resultado = new ArrayList<>();
        recorrer(raiz, tipo, resultado);
        return resultado;
    }

    private void recorrer(NodoLibro nodo, TipoRecorrido tipo, List<Libro> resultado) {
        if (nodo == null) { return; }
        if (tipo == TipoRecorrido.PREORDEN) { resultado.add(nodo.getDato()); }
        recorrer(nodo.getIzquierdo(), tipo, resultado);
        if (tipo == TipoRecorrido.INORDEN) { resultado.add(nodo.getDato()); }
        recorrer(nodo.getDerecho(), tipo, resultado);
        if (tipo == TipoRecorrido.POSORDEN) { resultado.add(nodo.getDato()); }
    }

    /**
     * Registra también cada hijo nulo. La secuencia usa un espacio entre códigos,
     * sin espacio final. La última línea no lleva salto de línea.
     */
    public String trazaRecorrido(TipoRecorrido tipo) {
        validarTipo(tipo);
        StringBuilder traza = new StringBuilder();
        StringBuilder secuencia = new StringBuilder();
        trazaRecorrido(raiz, tipo, "", traza, secuencia);
        traza.append("Secuencia ").append(tipo).append(':');
        if (secuencia.length() > 0) { traza.append(' ').append(secuencia); }
        return traza.toString();
    }

    private void trazaRecorrido(NodoLibro nodo, TipoRecorrido tipo, String sangria,
                                StringBuilder traza, StringBuilder secuencia) {
        if (nodo == null) {
            traza.append(sangria).append("null -> regresa\n");
            return;
        }
        int codigo = nodo.getDato().getCodigo();
        traza.append(sangria).append("entra ").append(codigo).append('\n');
        if (tipo == TipoRecorrido.PREORDEN) { visitar(codigo, sangria, traza, secuencia); }
        trazaRecorrido(nodo.getIzquierdo(), tipo, sangria + "  ", traza, secuencia);
        if (tipo == TipoRecorrido.INORDEN) { visitar(codigo, sangria, traza, secuencia); }
        trazaRecorrido(nodo.getDerecho(), tipo, sangria + "  ", traza, secuencia);
        if (tipo == TipoRecorrido.POSORDEN) { visitar(codigo, sangria, traza, secuencia); }
    }

    /** Procesa un dato escalar; la navegación está en trazaRecorrido. */
    private void visitar(int codigo, String sangria, StringBuilder traza,
                         StringBuilder secuencia) {
        traza.append(sangria).append("VISITA ").append(codigo).append('\n');
        if (secuencia.length() > 0) { secuencia.append(' '); }
        secuencia.append(codigo);
    }

    /**
     * Modifica título, autor y año conservando la identidad del libro.
     * Si algún dato es inválido, lanza excepción sin realizar cambios parciales.
     */
    public boolean modificar(int codigo, String titulo, String autor, int anio) {
        return modificar(raiz, codigo, titulo, autor, anio);
    }

    private boolean modificar(NodoLibro nodo, int codigo, String titulo,
                              String autor, int anio) {
        if (nodo == null) { return false; }
        int actual = nodo.getDato().getCodigo();
        if (codigo < actual) {
            return modificar(nodo.getIzquierdo(), codigo, titulo, autor, anio);
        }
        if (codigo > actual) {
            return modificar(nodo.getDerecho(), codigo, titulo, autor, anio);
        }
        Libro validado = new Libro(codigo, titulo, autor, anio);
        nodo.getDato().setTitulo(validado.getTitulo());
        nodo.getDato().setAutor(validado.getAutor());
        nodo.getDato().setAnio(validado.getAnio());
        return true;
    }

    /** Elimina en O(h), usando el mínimo derecho para el caso de dos hijos. */
    public boolean eliminar(int codigo) {
        boolean[] eliminado = {false};
        raiz = eliminar(raiz, codigo, eliminado);
        return eliminado[0];
    }

    private NodoLibro eliminar(NodoLibro nodo, int codigo, boolean[] eliminado) {
        if (nodo == null) { return null; }
        int actual = nodo.getDato().getCodigo();
        if (codigo < actual) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), codigo, eliminado));
        } else if (codigo > actual) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), codigo, eliminado));
        } else {
            eliminado[0] = true;
            if (nodo.getIzquierdo() == null) { return nodo.getDerecho(); }
            if (nodo.getDerecho() == null) { return nodo.getIzquierdo(); }
            NodoLibro sucesor = minimo(nodo.getDerecho());
            // Se reemplaza el Libro completo; su código no tiene setter.
            nodo.setDato(sucesor.getDato());
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getDato().getCodigo(),
                                     eliminado));
        }
        return nodo;
    }

    private NodoLibro minimo(NodoLibro nodo) {
        return nodo.getIzquierdo() == null ? nodo : minimo(nodo.getIzquierdo());
    }

    /** Intervalo inclusivo [a,b]; un intervalo invertido contiene cero códigos. */
    public int contarEnRango(int a, int b) { return contarEnRango(raiz, a, b); }

    private int contarEnRango(NodoLibro nodo, int a, int b) {
        if (nodo == null || a > b) { return 0; }
        int codigo = nodo.getDato().getCodigo();
        if (codigo < a) { return contarEnRango(nodo.getDerecho(), a, b); }
        if (codigo > b) { return contarEnRango(nodo.getIzquierdo(), a, b); }
        // Con claves únicas, en los extremos también se puede podar un lado.
        int izquierda = codigo > a ? contarEnRango(nodo.getIzquierdo(), a, b) : 0;
        int derecha = codigo < b ? contarEnRango(nodo.getDerecho(), a, b) : 0;
        return 1 + izquierda + derecha;
    }

    /** Si el código no existe, devuelve los nodos visitados hasta el hijo nulo. */
    public List<Integer> caminoBusqueda(int codigo) {
        List<Integer> camino = new ArrayList<>();
        caminoBusqueda(raiz, codigo, camino);
        return camino;
    }

    private void caminoBusqueda(NodoLibro nodo, int codigo, List<Integer> camino) {
        if (nodo == null) { return; }
        int actual = nodo.getDato().getCodigo();
        camino.add(actual);
        if (codigo < actual) { caminoBusqueda(nodo.getIzquierdo(), codigo, camino); }
        else if (codigo > actual) { caminoBusqueda(nodo.getDerecho(), codigo, camino); }
    }

    /** Comprueba límites heredados de TODOS los ancestros, no solo del padre. */
    public boolean esABBValido() { return esABBValido(raiz, 0L, Long.MAX_VALUE); }

    private boolean esABBValido(NodoLibro nodo, long minimo, long maximo) {
        if (nodo == null) { return true; }
        int codigo = nodo.getDato().getCodigo();
        return codigo > minimo && codigo < maximo
                && esABBValido(nodo.getIzquierdo(), minimo, codigo)
                && esABBValido(nodo.getDerecho(), codigo, maximo);
    }

    /**
     * Reconstruye directamente desde un preorden válido en O(n), con pila O(h).
     * El arreglo vacío produce un catálogo vacío; null, repetidos, códigos no
     * positivos y secuencias incompatibles con un ABB provocan excepción.
     * Como solo se reciben códigos, se crean metadatos de ejemplo:
     * título "Libro X", autor "Sin especificar", año 2026.
     */
    public static CatalogoABB desdePreorden(int[] preorden) {
        if (preorden == null) {
            throw new IllegalArgumentException("El preorden no puede ser null.");
        }
        CatalogoABB catalogo = new CatalogoABB();
        int[] indice = {0};
        catalogo.raiz = desdePreorden(preorden, indice, 0L, Long.MAX_VALUE);
        if (indice[0] != preorden.length) {
            throw new IllegalArgumentException("El arreglo no es un preorden de ABB válido.");
        }
        return catalogo;
    }

    private static NodoLibro desdePreorden(int[] preorden, int[] indice,
                                           long minimo, long maximo) {
        if (indice[0] == preorden.length) { return null; }
        int codigo = preorden[indice[0]];
        if (codigo <= minimo || codigo >= maximo) { return null; }
        indice[0]++;
        NodoLibro nodo = new NodoLibro(new Libro(codigo, "Libro " + codigo,
                                                "Sin especificar", 2026));
        nodo.setIzquierdo(desdePreorden(preorden, indice, minimo, codigo));
        nodo.setDerecho(desdePreorden(preorden, indice, codigo, maximo));
        return nodo;
    }

    private static void validarTipo(TipoRecorrido tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de recorrido es obligatorio.");
        }
    }
}
