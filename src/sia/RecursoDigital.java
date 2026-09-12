package sia;

/**
 * Clase Padre (abstracta)
 *
 * SIA-6 (Herencia/Polimorfismo): esta es la clase base de la que "nacen"
 * RecursoVideo, RecursoDocumento y RecursoEnlaceWeb (todas con "extends
 * RecursoDigital")
 */
public abstract class RecursoDigital {

    // --- SIA-3: Encapsulamiento ---
    private int numeroMaterial;
    private String titulo;
    private String formato;
    private String url;

    public RecursoDigital(int numeroMaterial, String titulo, String formato, String url) {
        this.numeroMaterial = numeroMaterial;
        this.titulo = titulo;
        this.formato = formato;
        this.url = url;
    }

    // --- Getters y Setters ---
    public int getNumeroMaterial() { return numeroMaterial; }
    public void setNumeroMaterial(int numeroMaterial) { this.numeroMaterial = numeroMaterial; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    /**
     * SIA-6: Método abstracto. No tiene cuerpo aquí porque cada clase Hija
     * lo implementa de forma distinta (usando @Override). Esto es lo que
     * permite el polimorfismo.
     */
    public abstract String obtenerFichaTecnica();

    /**
     * SIA-6: Segundo metodo abstracto. Estima cuántos minutos le toma a un alumno consumir
     * este recurso. Cada subclase lo calcula de forma diferente:
     * - RecursoVideo: devuelve directamente su duración.
     * - RecursoDocumento: calcula minutos de lectura según sus páginas.
     * - RecursoEnlaceWeb: devuelve un tiempo fijo de exploración.
     */
    public abstract int estimarTiempoConsumoMinutos();

    /**
     * SIA-5: Sobrecarga de métodos (PRIMERA clase que la usa; la segunda
     * es Alumno.calcularPromedio()).
     *
     * mostrarDetalle()        -> versión resumida (solo la ficha técnica).
     * mostrarDetalle(boolean) -> versión detallada: si el parámetro es
     *                            true, además muestra la URL y el tiempo
     *                            estimado de consumo.
     *
     */
    public void mostrarDetalle() {
        mostrarDetalle(false);
    }

    public void mostrarDetalle(boolean incluirDetalleCompleto) {
        // obtenerFichaTecnica() y estimarTiempoConsumoMinutos() son
        // polimórficos: aquí no sabemos si "this" es un Video, un
        // Documento o un Enlace, pero cada uno responde correctamente
        // solo porque implementó su propia versión (SIA-6).
        System.out.println("   [ID:" + numeroMaterial + "] " + obtenerFichaTecnica());
        if (incluirDetalleCompleto) {
            System.out.println("       URL: " + url + " | Tiempo estimado de uso: " + estimarTiempoConsumoMinutos() + " min.");
        }
    }

    // @Override de Object.toString(): personaliza cómo se ve un recurso al
    // imprimirlo directamente. No confundir con la sobreescritura de SIA-6
    // (esa ocurre en obtenerFichaTecnica() y estimarTiempoConsumoMinutos(),
    // que SÍ son métodos definidos por nosotros, no heredados)
    @Override
    public String toString() {
        return "[" + numeroMaterial + "] " + titulo + " (" + formato + ") - " + url;
    }
}
