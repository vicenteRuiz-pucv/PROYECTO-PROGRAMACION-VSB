package sia;

import java.util.ArrayList;
import java.util.HashMap;
import sia.excepciones.NotaInvalidaException;
import java.util.Collections;
import java.util.List;

/**
 * Entidad Alumno inscrita en asignaturas.
 *
 * @author Vicho
 *
 *  (Funcionalidad estrella "Boletín Académico"):
 * se agregó un HashMap interno que guarda para cada codigo de
 * asignatura en la que el alumno está inscrito, una lista de notas
 * (escala 1.0 a 7.0). Con esto se puede calcular promedios y ademas
 * mostrar el avance hacia la aprobación y sugerir la nota que falta.
 */

public class Alumno {
    
    //CONSTANTES
    /**
     * Nota mínima permitida en la escala chilena (1.0).
     */
    public static final double NOTA_MINIMA = 1.0;
    /**
     * Nota máxima permitida en la escala chilena (7.0).
     */
    public static final double NOTA_MAXIMA = 7.0;
    /**
     * Nota mínima para aprobar (4.0).
     */
    public static final double NOTA_APROBACION = 4.0;

    //Variables de instancia

    private String nombre;
    private String rut;
    private char letra;
    private int curso;
    private String ciclo;

    //LISTA QUE CONTIENE LAS ASIGNATURAS QUE DA EL ALUMNO
    //CON SU CODIGO!
    private ArrayList<String> codigosAsignatura;
    
    //MAPA DE NOTAS POR ASIGNATURA!
    //K= CODIGO ASIGNATURA | V= Lista de notas!    
    private HashMap<String,ArrayList<Double>> notasPorAsignatura;
    
    //CONSTRUCTOR
    /**
     * Crea un alumno sin asignaturas ni notas.
     *
     * @param nombre nombre completo
     * @param rut RUT del alumno
     * @param letra letra del curso
     * @param ciclo ciclo educativo (Basica o Media)
     * @param curso nivel del curso
     */
    public Alumno(String nombre, String rut, char letra, String ciclo, int curso){
        //PODRIAMOS AGREGAR VERIFICACIONES PARA QUE NO INGRESEN DATOS MAL A PROPOSITO?-sugerencia-
        this.nombre = nombre;
        this.rut = rut;
        this.letra = letra;
        this.curso = curso;
        this.ciclo = ciclo;
        codigosAsignatura = new ArrayList<>();
        notasPorAsignatura = new HashMap<>();
    }
    
    //SECCION DE GETTERS Y SETTER
    /**
     * Modifica el dato: nombre completo.
     *
     * @param nombre nuevo valor: nombre completo
     */
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    /**
     * Obtiene el dato: nombre completo.
     *
     * @return nombre completo
     */
    public String getNombre(){
        return nombre;
    }
    /**
     * Modifica el dato: RUT.
     *
     * @param rut nuevo valor: RUT
     */
    public void setRut(String rut){
        this.rut = rut;
    }
    /**
     * Obtiene el dato: RUT.
     *
     * @return RUT
     */
    public String getRut(){
        return rut;
    }
    /**
     * Modifica el dato: letra del curso (A, B, C...).
     *
     * @param letra nuevo valor: letra del curso (A, B, C...)
     */
    public void setLetra(char letra){
        this.letra = letra;
    }
    /**
     * Obtiene el dato: letra del curso (A, B, C...).
     *
     * @return letra del curso (A, B, C...)
     */
    public char getLetra(){
        return letra;
    }
    /**
     * Modifica el dato: nivel del curso (1 a 4).
     *
     * @param curso nuevo valor: nivel del curso (1 a 4)
     */
    public void setCurso(int curso){
        this.curso = curso;
    }
    /**
     * Obtiene el dato: nivel del curso (1 a 4).
     *
     * @return nivel del curso (1 a 4)
     */
    public int getCurso(){
        return curso;
    }
    /**
     * Modifica el dato: ciclo educativo (Basica o Media).
     *
     * @param ciclo nuevo valor: ciclo educativo (Basica o Media)
     */
    public void setCiclo(String ciclo){
        this.ciclo = ciclo;
    }
    /**
     * Obtiene el dato: ciclo educativo (Basica o Media).
     *
     * @return ciclo educativo (Basica o Media)
     */
    public String getCiclo(){
        return ciclo;
    }
    
    /**
     * Entrega una vista de solo lectura de los códigos de las asignaturas en que está inscrito.
     *
     * @return lista no modificable de códigos
     */
    public List<String> getCodigosAsignatura(){
        return Collections.unmodifiableList(codigosAsignatura);
    }
    /**
     * Registra que el alumno está inscrito en una asignatura (si no estaba ya).
     *
     * @param codigo código de la asignatura
     */
    public void agregarCodigoAsignatura(String codigo){
        if(!codigosAsignatura.contains(codigo)){
            codigosAsignatura.add(codigo);
        }
    }
    /**
     * Quita la asignatura del alumno y también las notas que tenía en ella.
     *
     * @param codigo código de la asignatura
     */
    public void removerCodigoAsignatura(String codigo){
        //PODRIAMOS AGREGAR ALGUNA CORRECCION O SEGURIDAD POR SI 
        //NO EXISTE ESE CODIGO..
        codigosAsignatura.remove(codigo);
        notasPorAsignatura.remove(codigo);
    }
     /**
      * Agrega una nota al alumno en una asignatura.
      *
      * @param codigoAsignatura código de la asignatura
      * @param nota nota entre 1.0 y 7.0
      * @throws NotaInvalidaException si la nota está fuera de la escala
      */
     public void agregarNota(String codigoAsignatura, double nota) throws NotaInvalidaException {
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new NotaInvalidaException("La nota " + nota + " está fuera de la escala permitida ("
                    + NOTA_MINIMA + " a " + NOTA_MAXIMA + ").");
        }
        if (!notasPorAsignatura.containsKey(codigoAsignatura)) {
            notasPorAsignatura.put(codigoAsignatura, new ArrayList<>());
        }
        notasPorAsignatura.get(codigoAsignatura).add(nota);
    }
    /**
     * Reemplaza una nota ya registrada.
     *
     * @param codigoAsignatura código de la asignatura
     * @param indice posición de la nota en la lista (desde 0)
     * @param nuevaNota nueva nota entre 1.0 y 7.0
     * @return true si se editó; false si la asignatura o el índice no existen
     * @throws NotaInvalidaException si la nueva nota está fuera de la escala
     */
    public boolean editarNota(String codigoAsignatura, int indice, double nuevaNota) throws NotaInvalidaException {
        if (nuevaNota < NOTA_MINIMA || nuevaNota > NOTA_MAXIMA) {
            throw new NotaInvalidaException("La nota " + nuevaNota + " está fuera de la escala permitida.");
        }
        ArrayList<Double> notas = notasPorAsignatura.get(codigoAsignatura);
        // Si la asignatura no tiene notas registradas (notas == null) o el
        // índice no existe en la lista, no se puede editar: se devuelve
        // false para que Main.java le avise al usuario.
        if (notas == null || indice < 0 || indice >= notas.size()) return false;
        notas.set(indice, nuevaNota);
        return true;
    }
    /**
     * Elimina una nota de una asignatura.
     *
     * @param codigoAsignatura código de la asignatura
     * @param indice posición de la nota en la lista (desde 0)
     * @return true si se eliminó; false si la asignatura o el índice no existen
     */
    public boolean eliminarNota(String codigoAsignatura, int indice) {
        ArrayList<Double> notas = notasPorAsignatura.get(codigoAsignatura);
        if (notas == null || indice < 0 || indice >= notas.size()) return false;
        notas.remove(indice);
        return true;
    }
    /**
     * Entrega una COPIA de las notas del alumno en una asignatura, de modo que
     * quien la reciba no pueda alterar los datos internos.
     *
     * @param codigoAsignatura código de la asignatura
     * @return copia de la lista de notas (vacía si no tiene)
     */
    public ArrayList<Double> obtenerNotas(String codigoAsignatura) {
        ArrayList<Double> notas = notasPorAsignatura.get(codigoAsignatura);
        if (notas == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(notas);
    }
    /**
     * Sobrecarga : promedio del alumno en UNA asignatura.
     *
     * @param codigoAsignatura código de la asignatura
     * @return promedio, o 0.0 si no tiene notas
     */
    public double calcularPromedio(String codigoAsignatura) {
        ArrayList<Double> notas = obtenerNotas(codigoAsignatura);
        if (notas.isEmpty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }
    /**
     * Sobrecarga : promedio general considerando las notas de TODAS las asignaturas.
     *
     * @return promedio general, o 0.0 si no tiene notas
     */
    public double calcularPromedio() {
        double suma = 0;
        int total = 0;
        // Recorremos TODAS las listas de notas del HashMap (una por cada
        // asignatura en la que el alumno tiene notas) y las sumamos todas
        // juntas para sacar un promedio general.
        for (ArrayList<Double> notas : notasPorAsignatura.values()) {
            for (double n : notas) {
                suma += n;
                total++;
            }
        }
        return total == 0 ? 0.0 : suma / total;
    }
    /**
     * Calcula el promedio que necesita en las evaluaciones restantes para llegar a la nota de aprobación.
     *
     * @param codigoAsignatura código de la asignatura
     * @param evaluacionesRestantes cantidad de evaluaciones que faltan
     * @return nota necesaria (puede superar 7.0 si ya es imposible aprobar), o -1 si no quedan evaluaciones
     */
    public double calcularNotaNecesaria(String codigoAsignatura, int evaluacionesRestantes) {
        if (evaluacionesRestantes <= 0) return -1;
        ArrayList<Double> notas = obtenerNotas(codigoAsignatura);
        double suma = 0;
        for (double n : notas) suma += n;
        int totalEvaluaciones = notas.size() + evaluacionesRestantes;
        return (NOTA_APROBACION * totalEvaluaciones - suma) / evaluacionesRestantes;
    }
    @Override
    public String toString() {
        return nombre + " [RUT: " + rut + " | " + curso + "°" + letra + " " + ciclo + "]";
    }
}
