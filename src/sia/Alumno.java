package sia;

import java.util.ArrayList;
import java.util.HashMap;
import sia.excepciones.NotaInvalidaException;

/**
 *
 * @author Vicho
 */
/**
 * Entidad Alumno inscrita en asignaturas.
 *
 * SIA-9 (Funcionalidad estrella "Boletín Académico"):
 * se agregó un HashMap interno que guarda para cada codigo de
 * asignatura en la que el alumno está inscrito, una lista de notas
 * (escala 1.0 a 7.0). Con esto se puede calcular promedios y ademas
 * mostrar el avance hacia la aprobación y sugerir la nota que falta.
 */

public class Alumno {
    
    //CONSTANTES
    public static final double NOTA_MINIMA = 1.0;
    public static final double NOTA_MAXIMA = 7.0;
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
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public String getNombre(){
        return nombre;
    }
    public void setRut(String rut){
        this.rut = rut;
    }
    public String getRut(){
        return rut;
    }
    public void setLetra(char letra){
        this.letra = letra;
    }
    public char getLetra(){
        return letra;
    }
    public void setCurso(int curso){
        this.curso = curso;
    }
    public int getCurso(){
        return curso;
    }
    public void setCiclo(String ciclo){
        this.ciclo = ciclo;
    }
    public String getCiclo(){
        return ciclo;
    }
    
    public ArrayList<String> getCodigosAsignatura(){
        return codigosAsignatura;
    }
    public void agregarCodigoAsignatura(String codigo){
        if(!codigosAsignatura.contains(codigo)){
            codigosAsignatura.add(codigo);
        }
    }
    public void removerCodigoAsignatura(String codigo){
        //PODRIAMOS AGREGAR ALGUNA CORRECCION O SEGURIDAD POR SI 
        //NO EXISTE ESE CODIGO..
        codigosAsignatura.remove(codigo);
        notasPorAsignatura.remove(codigo);
    }
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
    public boolean eliminarNota(String codigoAsignatura, int indice) {
        ArrayList<Double> notas = notasPorAsignatura.get(codigoAsignatura);
        if (notas == null || indice < 0 || indice >= notas.size()) return false;
        notas.remove(indice);
        return true;
    }
    public ArrayList<Double> obtenerNotas(String codigoAsignatura) {
        return notasPorAsignatura.getOrDefault(codigoAsignatura, new ArrayList<>());
    }
    public double calcularPromedio(String codigoAsignatura) {
        ArrayList<Double> notas = obtenerNotas(codigoAsignatura);
        if (notas.isEmpty()) return 0.0;
        double suma = 0;
        for (double n : notas) suma += n;
        return suma / notas.size();
    }
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
