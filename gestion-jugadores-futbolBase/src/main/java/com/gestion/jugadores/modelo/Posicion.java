package com.gestion.jugadores.modelo;

/**
 * Enum para las posiciones válidas de jugadores de fútbol base
 * Define las posiciones estándar permitidas en el sistema
 */
public enum Posicion {
    PORTERO("POR", "Portero"),
    LATERAL_DERECHO("LD", "Lateral Derecho"),
    LATERAL_IZQUIERDO("LI", "Lateral Izquierdo"),
    CENTRAL("CEN", "Central"),
    MEDIOCENTRO("MC", "Mediocentro"),
    MEDIOCENTRO_OFENSIVO("MCO", "Mediocentro Ofensivo"),
    EXTREMO_DERECHO("EXD", "Extremo Derecho"),
    EXTREMO_IZQUIERDO("EXIZ", "Extremo Izquierdo"),
    DELANTERO_CENTRO("DC", "Delantero Centro");

    private final String codigo;
    private final String descripcion;

    Posicion(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el enum Posicion a partir de su código
     * @param codigo Código de la posición (ej: "POR", "LD", etc.)
     * @return Posicion correspondiente
     * @throws IllegalArgumentException si el código no es válido
     */
    public static Posicion fromCodigo(String codigo) {
        for (Posicion posicion : Posicion.values()) {
            if (posicion.getCodigo().equalsIgnoreCase(codigo)) {
                return posicion;
            }
        }
        throw new IllegalArgumentException("Posición inválida: " + codigo + ". Valores permitidos: POR, LD, LI, CEN, MC, MCO, EXD, EXIZ, DC");
    }

    /**
     * Valida si un código de posición es válido
     * @param codigo Código a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean esValido(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }
        try {
            fromCodigo(codigo);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return codigo;
    }
}
