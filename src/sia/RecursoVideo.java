package sia;

/**
 * SIA-6 (Herencia): especialización de RecursoDigital para contenido audiovisual
 */
public class RecursoVideo extends RecursoDigital {
    private int duracionMinutos;
    private String resolucion;

    public RecursoVideo(int numeroMaterial, String titulo, String url, int duracionMinutos, String resolucion) {
        // "super(...)" llama al constructor de la clase Padre (RecursoDigital)
        // para que se encargue de guardar numeroMaterial, titulo y url
        super(numeroMaterial, titulo, "Video MP4", url);
        this.duracionMinutos = duracionMinutos;
        this.resolucion = resolucion;
    }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public String getResolucion() { return resolucion; }
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
