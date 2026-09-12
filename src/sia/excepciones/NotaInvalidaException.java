package sia.excepciones;

/**
 * SIA-12: Segunda excepción propia (checked). Protege la escala de notas
 * chilena (1.0 a 7.0) usada en la funcionalidad estrella (SIA-9, Boletín
 * Académico).
 *
 * ¿Cuándo se lanza? En Alumno.agregarNota() y Alumno.editarNota(), cuando
 * el valor ingresado está fuera del rango permitido.
 */
public class NotaInvalidaException extends Exception {
    public NotaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
