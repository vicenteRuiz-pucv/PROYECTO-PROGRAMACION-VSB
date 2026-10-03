package sia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Entidad Profesor que dicta asignaturas en el sistema.
 * 
 * Cumple con  (encapsulamiento estricto) y mantiene
 * solo los códigos de asignaturas para evitar referencias
 * circulares (StackOverflowError).
 */
public class Profesor {
    private String nombre;
    private String rut;
    private String profesion;
    private ArrayList<String> codigosAsignaturas;

    /**
     * Crea un profesor sin asignaturas asociadas.
     *
     * @param nombre nombre completo
     * @param rut RUT del profesor
     * @param profesion especialidad o profesión
     */
    public Profesor(String nombre, String rut, String profesion) {
        this.nombre = nombre;
        this.rut = rut;
        this.profesion = profesion;
        this.codigosAsignaturas = new ArrayList<>();
    }

    // Getters y Setters 
    /**
     * Obtiene el dato: nombre completo.
     *
     * @return nombre completo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el dato: nombre completo.
     *
     * @param nombre nuevo valor: nombre completo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el dato: RUT.
     *
     * @return RUT
     */
    public String getRut() {
        return rut;
    }

    /**
     * Modifica el dato: RUT.
     *
     * @param rut nuevo valor: RUT
     */
    public void setRut(String rut) {
        this.rut = rut;
    }

    /**
     * Obtiene el dato: especialidad o profesión.
     *
     * @return especialidad o profesión
     */
    public String getProfesion() {
        return profesion;
    }

    /**
     * Modifica el dato: especialidad o profesión.
     *
     * @param profesion nuevo valor: especialidad o profesión
     */
    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    /**
     * Entrega una vista de solo lectura de los códigos de las asignaturas que dicta.
     *
     * @return lista no modificable de códigos
     */
    public List<String> getCodigosAsignaturas() {
        return Collections.unmodifiableList(codigosAsignaturas);
    }

    // Métodos de gestión de asignaturas asociadas
    /**
     * Registra una asignatura que dicta el profesor (si no estaba ya).
     *
     * @param codigo código de la asignatura
     */
    public void agregarCodigoAsignatura(String codigo) {
        if (!codigosAsignaturas.contains(codigo)) {
            codigosAsignaturas.add(codigo);
        }
    }

    /**
     * Quita una asignatura de las que dicta el profesor.
     *
     * @param codigo código de la asignatura
     */
    public void removerCodigoAsignatura(String codigo) {
        codigosAsignaturas.remove(codigo);
    }

    @Override
    public String toString() {
        return nombre + " (RUT: " + rut + ", " + profesion + ")";
    }
}