package sia;

/**
 * (Herencia): especialización de RecursoDigital para contenido audiovisual
 */
public class RecursoVideo extends RecursoDigital {
    private int duracionMinutos;
    private String resolucion;

    /**
     * Crea un video (el formato siempre es "Video MP4").
     *
     * @param numeroMaterial ID del recurso
     * @param titulo título
     * @param url dirección del video
     * @param duracionMinutos duración en minutos
     * @param resolucion resolución (1080p, 720p...)
     */
    public RecursoVideo(int numeroMaterial, String titulo, String url, int duracionMinutos, String resolucion) {
        // "super(...)" llama al constructor de la clase Padre (RecursoDigital)
        // para que se encargue de guardar numeroMaterial, titulo y url
        super(numeroMaterial, titulo, "Video MP4", url);
        this.duracionMinutos = duracionMinutos;
        this.resolucion = resolucion;
    }

    /**
     * Obtiene el dato: duración del video en minutos.
     *
     * @return duración del video en minutos
     */
    public int getDuracionMinutos() { return duracionMinutos; }
    /**
     * Modifica el dato: duración del video en minutos.
     *
     * @param duracionMinutos nuevo valor: duración del video en minutos
     */
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    /**
     * Obtiene el dato: resolución del video (por ejemplo 1080p).
     *
     * @return resolución del video (por ejemplo 1080p)
     */
    public String getResolucion() { return resolucion; }
    /**
     * Modifica el dato: resolución del video (por ejemplo 1080p).
     *
     * @param resolucion nuevo valor: resolución del video (por ejemplo 1080p)
     */
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }

    // SIA-6: Sobreescritura (@Override) -> reemplaza la versión abstracta
    // del Padre por una ficha propia de un video
    @Override
    public String obtenerFichaTecnica() {
        return "VIDEO -> " + getTitulo() + " | Duración: " + duracionMinutos + " min | Calidad: " + resolucion;
    }

    // SIA-6: Polimorfismo, un video "dura" exactamente lo que dura, así que se retorna su propia duración
    @Override
    public int estimarTiempoConsumoMinutos() {
        return duracionMinutos;
    }
}
