package sia.excepciones;

/**
 *  Excepción propia (checked, es decir, "extends Exception" y no
 * "extends RuntimeException"). Al ser checked, Java OBLIGA a que cualquier
 * método que la pueda lanzar la declare con "throws", y a que quien la
 * llame la envuelva en un try-catch. Esto evita que el error "recurso
 * repetido" pase inadvertido.
 */
public class RecursoDuplicadoException extends Exception {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param mensaje descripción del error
     */
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
