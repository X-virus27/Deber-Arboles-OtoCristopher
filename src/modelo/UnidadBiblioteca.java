package modelo;

import enums.TipoUnidad;
import java.util.Locale;

/** Unidad con identidad y tipo inmutables; su nombre puede modificarse. */
public final class UnidadBiblioteca {
    private final String codigo;
    private String nombre;
    private final TipoUnidad tipo;

    public UnidadBiblioteca(String codigo, String nombre, TipoUnidad tipo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de unidad no puede estar vacío.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de unidad es obligatorio.");
        }
        this.codigo = codigo.trim().toUpperCase(Locale.ROOT);
        this.tipo = tipo;
        setNombre(nombre);
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public TipoUnidad getTipo() { return tipo; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    @Override
    public String toString() {
        return codigo + " " + nombre;
    }
}
