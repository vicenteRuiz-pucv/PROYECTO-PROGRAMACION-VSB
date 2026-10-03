package sia;

/**
 * Clase Padre (abstracta)
 *
 * (Herencia/Polimorfismo): esta es la clase base de la que "nacen"
 * RecursoVideo, RecursoDocumento y RecursoEnlaceWeb (todas con "extends
 * RecursoDigital")
 */
public abstract class RecursoDigital {

    // ---  Encapsulamiento ---
    private int numeroMaterial;
    private String titulo;
    private String formato;
    private String url;

    /**
     * Crea un recurso digital (solo lo usan las subclases mediante super).
     *
     * @param numeroMaterial ID del recurso
     * @param titulo título
     * @param formato formato
     * @param url dirección del recurso
     */
    public RecursoDigital(int numeroMaterial, String titulo, String formato, String url) {
        this.numeroMaterial = numeroMaterial;
        this.titulo = titulo;
        this.formato = formato;
        this.url = url;
    }

    // --- Getters y Setters ---
    /**
     * Obtiene el dato: número (ID) que identifica al recurso dentro de su asignatura.
     *
     * @return número (ID) que identifica al recurso dentro de su asignatura
     */
    public int getNumeroMaterial() { return numeroMaterial; }
    /**
     * Modifica el dato: número (ID) que identifica al recurso dentro de su asignatura.
     *
     * @param numeroMaterial nuevo valor: número (ID) que identifica al recurso dentro de su asignatura
     */
    public void setNumeroMaterial(int numeroMaterial) { this.numeroMaterial = numeroMaterial; }

    /**
     * Obtiene el dato: título del recurso.
     *
     * @return título del recurso
     */
    public String getTitulo() { return titulo; }
    /**
     * Modifica el dato: título del recurso.
     *
     * @param titulo nuevo valor: título del recurso
     */
    public void setTitulo(String titulo) { this.titulo = titulo; }

    /**
     * Obtiene el dato: formato del recurso (por ejemplo PDF o Video MP4).
     *
     * @return formato del recurso (por ejemplo PDF o Video MP4)
     */
    public String getFormato() { return formato; }
    /**
     * Modifica el dato: formato del recurso (por ejemplo PDF o Video MP4).
     *
     * @param formato nuevo valor: formato del recurso (por ejemplo PDF o Video MP4)
     */
    public void setFormato(String formato) { this.formato = formato; }

    /**
     * Obtiene el dato: dirección (URL) donde está alojado el recurso.
     *
     * @return dirección (URL) donde está alojado el recurso
     */
    public String getUrl() { return url; }
    /**
     * Modifica el dato: dirección (URL) donde está alojado el recurso.
     *
     * @param url nuevo valor: dirección (URL) donde está alojado el recurso
     */
    public void setUrl(String url) { this.url = url; }

    /**
     * SIA-6: Método abstracto. No tiene cuerpo aquí porque cada clase Hija
     * lo implementa de forma distinta (usando @Override). Esto es lo que
     * permite el polimorfismo.
     *
     * @return texto con la ficha del recurso
     */
    public abstract String obtenerFichaTecnica();

    /**
     * SIA-6: Segundo metodo abstracto. Estima cuántos minutos le toma a un alumno consumir
     * este recurso. Cada subclase lo calcula de forma diferente:
     * - RecursoVideo: devuelve directamente su duración.
     * - RecursoDocumento: calcula minutos de lectura según sus páginas.
     * - RecursoEnlaceWeb: devuelve un tiempo fijo de exploración.
     *
     * @return minutos estimados de uso
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

    /**
     * Sobrecarga (SIA-5): muestra el recurso; si se pide detalle completo agrega la URL y el tiempo estimado.
     *
     * @param incluirDetalleCompleto true para incluir URL y tiempo estimado de uso
     */
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
