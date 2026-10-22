package enums;

/** Tipos permitidos en la jerarquía de la biblioteca. */
public enum TipoUnidad {
    BIBLIOTECA, AREA, ESTANTE;

    /** Un tipo nulo o cualquier relación distinta de las indicadas es inválido. */
    public boolean puedeTenerHijo(TipoUnidad hijo) {
        return (this == BIBLIOTECA && hijo == AREA)
                || (this == AREA && hijo == ESTANTE);
    }
}
