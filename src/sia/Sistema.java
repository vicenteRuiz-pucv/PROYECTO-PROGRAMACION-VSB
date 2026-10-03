package sia;

import java.util.ArrayList;
import java.util.HashMap;
import sia.excepciones.NotaInvalidaException;
import sia.excepciones.RecursoDuplicadoException;
import java.util.Collections;
import java.util.Map;

/**
 * Clase central del dominio. Guarda la 1ª colección del proyecto (un HashMap
 * de asignaturas indexado por código) y ofrece las operaciones de inserción,
 * búsqueda, edición, eliminación y el reporte de alumnos en riesgo.
 *
 * @author sebas
 */
public class Sistema {
    private HashMap<String,Asignatura> mapaAsignaturas;
    
    /**
     * Crea el sistema con los datos de ejemplo cargados.
     */
    public Sistema(){
        this(true);
    }
    /**
     * Crea el sistema, con o sin datos de ejemplo.
     *
     * @param cargarEjemplos true para cargar los datos iniciales; false para partir vacío (se usa antes de cargar el CSV)
     */
    public Sistema(boolean cargarEjemplos){
        this.mapaAsignaturas = new HashMap<>();
        if (cargarEjemplos){
            cargarDatosIniciales();
        }
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

            Alumno al1 = new Alumno("Benjamin Alucema", "20.123.456-7", 'A', "Media", 1);
            Alumno al2 = new Alumno("Sofia Contreras", "21.987.654-3", 'A', "Media", 1);

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
    /**
     * Agrega una asignatura al mapa usando su código como llave.
     *
     * @param nuevaAsignatura asignatura a agregar
     * @return true si se agregó; false si es null o si ya existe una con ese código
     */
    public boolean agregarAsignatura(Asignatura nuevaAsignatura) {
        if (nuevaAsignatura == null || nuevaAsignatura.getCodigo() == null) {
            return false;
        }
        String clave = nuevaAsignatura.getCodigo().toUpperCase().trim();
        if (mapaAsignaturas.containsKey(clave)) {
            return false; // el código ya existe: no se sobreescribe la asignatura anterior
        }
        mapaAsignaturas.put(clave, nuevaAsignatura);
        return true;
    }
     /**
      * Imprime por consola el listado de todas las asignaturas.
      */
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

    /**
     * Busca una asignatura por su código (búsqueda directa en el HashMap).
     *
     * @param codigo código de la asignatura
     * @return la asignatura, o null si no existe
     */
    public Asignatura buscarAsignatura(String codigo) {
        if (codigo == null) return null;
        // get(llave) en un HashMap es una búsqueda casi instantánea
        // (por eso se eligió esta colección para , a diferencia de
        // recorrer una lista completa comparando uno por uno.
        return mapaAsignaturas.get(codigo.toUpperCase().trim());
    }
    /**
     * Elimina una asignatura y la desvincula del docente y de sus alumnos.
     *
     * @param codigo código de la asignatura
     * @return true si se eliminó; false si no existía
     */
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
    /**
     * Modifica los datos de una asignatura (el código no se cambia).
     *
     * @param codigo código de la asignatura a editar
     * @param nuevoNombre nuevo nombre
     * @param nuevaLetra nueva letra
     * @param nuevoCurso nuevo nivel de curso
     * @param nuevoCiclo nuevo ciclo
     * @return true si se editó; false si no existía
     */
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
     /**
      * Entrega una vista de solo lectura del mapa de asignaturas.
      *
      * @return mapa no modificable (código -> asignatura)
      */
     public Map<String, Asignatura> getMapaAsignaturas() {
        return Collections.unmodifiableMap(mapaAsignaturas);
    }
    /**
     * Busca un alumno por RUT en todas las asignaturas del sistema.
     *
     * @param rut RUT del alumno
     * @return el alumno, o null si no está en ninguna asignatura
     */
    public Alumno buscarAlumnoGlobal(String rut) {
        if (rut == null) return null;
        for (Asignatura asig : mapaAsignaturas.values()) {
            Alumno encontrado = asig.buscarAlumno(rut);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    /**
     * : reporte filtrado por un criterio (promedio bajo la nota de
     * aprobación). Recorre TODAS las asignaturas del sistema y junta, sin
     * repetir, a los alumnos cuyo promedio general está por debajo de 4.0.
     *
     * @return lista nueva con los alumnos en riesgo
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
