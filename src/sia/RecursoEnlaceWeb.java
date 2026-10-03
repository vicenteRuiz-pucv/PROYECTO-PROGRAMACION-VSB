package sia;

/**
 * (Herencia): tercera especialización de RecursoDigital, para
 * enlaces externos (simuladores, páginas web, plataformas de terceros).
 */
public class RecursoEnlaceWeb extends RecursoDigital {
    private boolean requiereConexionExterna;

    /**
     * Crea un enlace web (el formato siempre es "Enlace Web").
     *
     * @param numeroMaterial ID del recurso
     * @param titulo título
     * @param url dirección del enlace
     * @param requiereConexionExterna si necesita un sitio externo al colegio
     */
    public RecursoEnlaceWeb(int numeroMaterial, String titulo, String url, boolean requiereConexionExterna) {
        super(numeroMaterial, titulo, "Enlace Web", url);
        this.requiereConexionExterna = requiereConexionExterna;
    }

    /**
     * Obtiene el dato: si el enlace requiere un sitio externo al colegio.
     *
     * @return si el enlace requiere un sitio externo al colegio
     */
    public boolean isRequiereConexionExterna() { return requiereConexionExterna; }
    /**
     * Modifica el dato: si el enlace requiere un sitio externo al colegio.
     *
     * @param requiereConexionExterna nuevo valor: si el enlace requiere un sitio externo al colegio
     */
    public void setRequiereConexionExterna(boolean requiereConexionExterna) {
        this.requiereConexionExterna = requiereConexionExterna;
    }

    // SIA-6: Sobreescritura -> tercera versión distinta de la ficha técnica
    @Override
    public String obtenerFichaTecnica() {
        String acceso = requiereConexionExterna ? "requiere sitio externo" : "disponible dentro del colegio";
        return "ENLACE WEB -> " + getTitulo() + " | Acceso: " + acceso;
    }

    // SIA-6: Polimorfismo -> a diferencia del video y el documento (que calculan un valor distinto cada vez), un enlace web siempre se
    // estima con un tiempo fijo de exploración.
    @Override
    public int estimarTiempoConsumoMinutos() {
        return 5;
    }
}
