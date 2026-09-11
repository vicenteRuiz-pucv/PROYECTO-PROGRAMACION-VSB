package sia;

import java.util.ArrayList;

/**
 * Entidad Profesor que dicta asignaturas en el sistema.
 * 
 * Cumple con SIA-3 (encapsulamiento estricto) y mantiene
 * solo los códigos de asignaturas para evitar referencias
 * circulares (StackOverflowError).
 */
public class Profesor {
    private String nombre;
    private String rut;
    private String profesion;
    private ArrayList<String> codigosAsignaturas;

    public Profesor(String nombre, String rut, String profesion) {
        this.nombre = nombre;
        this.rut = rut;
        this.profesion = profesion;
        this.codigosAsignaturas = new ArrayList<>();
    }

    // Getters y Setters (SIA-3)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public ArrayList<String> getCodigosAsignaturas() {
        return codigosAsignaturas;
    }

    // Métodos de gestión de asignaturas asociadas
    public void agregarCodigoAsignatura(String codigo) {
        if (!codigosAsignaturas.contains(codigo)) {
            codigosAsignaturas.add(codigo);
        }
    }

    public void removerCodigoAsignatura(String codigo) {
        codigosAsignaturas.remove(codigo);
    }

    @Override
    public String toString() {
        return nombre + " (RUT: " + rut + ", " + profesion + ")";
    }
}