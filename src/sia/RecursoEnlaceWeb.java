package sia;

/**
 * SIA-6 (Herencia): tercera especialización de RecursoDigital, para
 * enlaces externos (simuladores, páginas web, plataformas de terceros).
 */
public class RecursoEnlaceWeb extends RecursoDigital {
    private boolean requiereConexionExterna;

    public RecursoEnlaceWeb(int numeroMaterial, String titulo, String url, boolean requiereConexionExterna) {
        super(numeroMaterial, titulo, "Enlace Web", url);
        this.requiereConexionExterna = requiereConexionExterna;
    }

    public boolean isRequiereConexionExterna() { return requiereConexionExterna; }
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
