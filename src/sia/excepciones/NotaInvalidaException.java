package sia.excepciones;

/**
 *  Segunda excepción propia (checked). Protege la escala de notas
 * chilena (1.0 a 7.0) usada en la funcionalidad estrella  Boletín
 * Académico).
 *
 * ¿Cuándo se lanza? En Alumno.agregarNota() y Alumno.editarNota(), cuando
 * el valor ingresado está fuera del rango permitido.
 */
public class NotaInvalidaException extends Exception {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param mensaje descripción del error
     */
    public NotaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
