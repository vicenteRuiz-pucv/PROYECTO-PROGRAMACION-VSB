package sia;

/**
 *
 * @author sebas
 */
import java.util.ArrayList;
import java.util.HashMap;
import sia.excepciones.NotaInvalidaException;
import sia.excepciones.RecursoDuplicadoException;

public class Sistema {
    private HashMap<String,Asignatura> mapaAsignaturas;

    public Sistema(){
        this.mapaAsignaturas = new HashMap<>();
        cargarDatosIniciales();
    }
    
    private void cargarDatosIniciales(){
        try{
            Profesor profMat = new Profesor("Roberto Morales","11.222.333-4", "Profesor de Matemáticas");
            Profesor profCie = new Profesor("Carla Fuentes","15.666.777-8", "Profesora de Ciencias Naturales");

            Asignatura mat = new Asignatura("MAT-101", "Matemática",'A', 1, "Media", profMat);
            Asignatura cie = new Asignatura("CIE-201", "Física",'B', 2, "Media", profCie);

            mat.agregarRecurso(new RecursoDocumento(1,"Guía de Funciones Cuadráticas", "PDF", "https://colegio.cl/mat/guia1.pdf",12,false));
            mat.agregarRecurso(new RecursoVideo(2, "Clase Grabada: Parábolas y Vértice", "https://colegio.cl/mat/video1.mp4", 35, "1080p"));
            mat.agregarRecurso(new RecursoEnlaceWeb(3,"Simulador GeoGebra", "https://geogebra.org/mat", true));
            cie.agregarRecurso(new RecursoDocumento(10, "Laboratorio Cinemática", "PDF","https://colegio.cl/cie/lab1.pdf", 8, true));
            cie.agregarRecurso(new RecursoVideo(11,"Video Leyes de Newton", "https://colegio.cl/cie/newton.mp4", 20, "720p"));

            Alumno al1 = new Alumno("Benjamin Alucema", "20.123.456-7", 1, 'A', "Media");
            Alumno al2 = new Alumno("Sofia Contreras", "21.987.654-3", 1, 'A', "Media");

            mat.agregarAlumno(al1);
            mat.agregarAlumno(al2);
            cie.agregarAlumno(al1);

            al1.agregarNota("MAT-101", 5.5);
            al1.agregarNota("MAT-101", 3.8);
            al2.agregarNota("MAT-101", 3.2);

            al2.agregarNota("MAT-101", 3.5);

            agregarAsignatura(mat);
            agregarAsignatura(cie);
        } catch (RecursoDuplicadoException | NotaInvalidaException e) {
            System.out.println("Aviso al cargar datos iniciales: " + e.getMessage());
           }
}
    public void agregarAsignatura(Asignatura nuevaAsignatura) {
        if (nuevaAsignatura != null && nuevaAsignatura.getCodigo() != null) {
            mapaAsignaturas.put(nuevaAsignatura.getCodigo().toUpperCase().trim(), nuevaAsignatura);
        }
    }
     public void mostrarAsignaturas() {
        if (mapaAsignaturas.isEmpty()) {
            System.out.println("No hay asignaturas registradas en el sistema.");
            return;
        }
        System.out.println("\n============== LISTADO GENERAL DE ASIGNATURAS ==============");
        // .values() devuelve solo los objetos Asignatura del HashMap, sin
        // sus llaves (códigos), que es lo que necesitamos para listarlas.
        for (Asignatura asig : mapaAsignaturas.values()) {
            System.out.println(" * " + asig);
        }
        System.out.println("=============================================================");
    }

    public Asignatura buscarAsignatura(String codigo) {
        if (codigo == null) return null;
        // get(llave) en un HashMap es una búsqueda casi instantánea
        // (por eso se eligió esta colección para SIA-4), a diferencia de
        // recorrer una lista completa comparando uno por uno.
        return mapaAsignaturas.get(codigo.toUpperCase().trim());
    }
    public boolean eliminarAsignatura(String codigo) {
        if (codigo == null) return false;
        Asignatura removida = mapaAsignaturas.remove(codigo.toUpperCase().trim());
        if (removida != null) {
            if (removida.getDocente() != null) {
                removida.getDocente().removerCodigoAsignatura(removida.getCodigo());
            }
            for (Alumno al : removida.getListaAlumnos()) {
                al.removerCodigoAsignatura(removida.getCodigo());
            }
            return true;
        }
        return false;
    }
    public boolean editarAsignatura(String codigo, String nuevoNombre, char nuevaLetra, int nuevoCurso, String nuevoCiclo) {
        Asignatura asig = buscarAsignatura(codigo);
        if (asig != null) {
            asig.setNombre(nuevoNombre);
            asig.setLetra(nuevaLetra);
            asig.setCurso(nuevoCurso);
            asig.setCiclo(nuevoCiclo);
            return true;
        }
        return false;
    }
     public HashMap<String, Asignatura> getMapaAsignaturas() {
        return mapaAsignaturas;
    }
    public Alumno buscarAlumnoGlobal(String rut) {
        if (rut == null) return null;
        for (Asignatura asig : mapaAsignaturas.values()) {
            Alumno encontrado = asig.buscarAlumno(rut);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    /**
     * SIA-9: reporte filtrado por un criterio (promedio bajo la nota de
     * aprobación). Recorre TODAS las asignaturas del sistema y junta, sin
     * repetir, a los alumnos cuyo promedio general está por debajo de 4.0.
     */
    public ArrayList<Alumno> listarAlumnosEnRiesgo() {
        ArrayList<Alumno> enRiesgo = new ArrayList<>();
        for (Asignatura asig : mapaAsignaturas.values()) {
            for (Alumno a : asig.getListaAlumnos()) {
                if (!enRiesgo.contains(a) && a.calcularPromedio() > 0
                        && a.calcularPromedio() < Alumno.NOTA_APROBACION) {
                    enRiesgo.add(a);
                }
            }
        }
        return enRiesgo;
    }

}
