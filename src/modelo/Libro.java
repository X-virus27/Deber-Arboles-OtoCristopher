package modelo;

/** Libro cuyo código es inmutable para preservar el orden del ABB. */
public final class Libro {
    private final int codigo;
    private String titulo;
    private String autor;
    private int anio;

    /** @throws IllegalArgumentException si cualquier dato es inválido. */
    public Libro(int codigo, String titulo, String autor, int anio) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("El código debe ser mayor que cero.");
        }
        this.codigo = codigo;
        setTitulo(titulo);
        setAutor(autor);
        setAnio(anio);
    }

    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnio() { return anio; }

    public void setTitulo(String titulo) {
        this.titulo = validarTexto(titulo, "título");
    }

    public void setAutor(String autor) {
        this.autor = validarTexto(autor, "autor");
    }

    public void setAnio(int anio) {
        // Intervalo fijo exigido por el enunciado, no por el reloj del sistema.
        if (anio < 1900 || anio > 2026) {
            throw new IllegalArgumentException("El año debe estar entre 1900 y 2026.");
        }
        this.anio = anio;
    }

    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + campo + " no puede estar vacío.");
        }
        return texto.trim();
    }

    @Override
    public String toString() {
        return codigo + " | " + titulo + " | " + autor + " | " + anio;
    }
}
