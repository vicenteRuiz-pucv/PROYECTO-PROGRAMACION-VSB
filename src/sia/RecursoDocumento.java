package sia;

/**
 * SIA-6 (Herencia): especialización de RecursoDigital para documentos
 * descargables (PDF, Guías en Word, etc.).
 */
public class RecursoDocumento extends RecursoDigital {
    private static final double MINUTOS_POR_PAGINA = 2.0;

    private int cantidadPaginas;
    private boolean esEditable;

    public RecursoDocumento(int numeroMaterial, String titulo, String formato, String url,
                             int cantidadPaginas, boolean esEditable) {
        // A diferencia de RecursoVideo, aquí el formato SÍ se recibe como parámetro (puede ser "PDF" o "Word")
        super(numeroMaterial, titulo, formato, url);
        this.cantidadPaginas = cantidadPaginas;
        this.esEditable = esEditable;
    }

    public int getCantidadPaginas() { return cantidadPaginas; }
    public void setCantidadPaginas(int cantidadPaginas) { this.cantidadPaginas = cantidadPaginas; }

    public boolean isEsEditable() { return esEditable; }
    public void setEsEditable(boolean esEditable) { this.esEditable = esEditable; }

    // SIA-6: Sobreescritura -> ficha propia de un documento, distinta a la
    // de un video aunque el método se llame igual (obtenerFichaTecnica()).
    @Override
    public String obtenerFichaTecnica() {
        String tipo = esEditable ? "Documento Editable/Guía" : "Documento Solo Lectura/PDF";
        return "DOCUMENTO -> " + getTitulo() + " | Páginas: " + cantidadPaginas + " | Tipo: " + tipo;
    }

    // SIA-6: Polimorfismo: a diferencia del video (que devuelve su duración tal cual), aquí se CALCULA un tiempo estimado
    // en base a la cantidad de páginas.
    @Override
    public int estimarTiempoConsumoMinutos() {
        return (int) Math.ceil(cantidadPaginas * MINUTOS_POR_PAGINA);
    }
}
