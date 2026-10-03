package sia;

import java.util.ArrayList;
import sia.excepciones.RecursoDuplicadoException;
import java.util.Collections;
import java.util.List;
/**
 * Asignatura de un colegio. Es el elemento de la 1ª colección (HashMap del
 * Sistema) y contiene la 2ª colección anidada: la lista de recursos digitales
 * (más la lista de alumnos inscritos).
 *
 * @author Vicho
 */
public class Asignatura {
    //VARIABLES DE INSTANCIA
    private String codigo;
    private String nombre;
    private char letra;
    private int curso;
    private String ciclo;
    private Profesor docente;
    
    //Lista de alumnos que cursan tal asignatura
    private ArrayList<Alumno> listaAlumnos;
    //LISTA DE RECURSOS DIGITALES!
    private ArrayList<RecursoDigital> listaRecursos;
    
    /**
     * Crea una asignatura vacía (sin alumnos ni recursos) y avisa al docente que la dicta.
     *
     * @param codigo código único (se normaliza a mayúsculas)
     * @param nombre nombre de la asignatura
     * @param letra letra del curso
     * @param curso nivel del curso
     * @param ciclo ciclo educativo
     * @param docente profesor que la dicta (puede ser null)
     */
    public Asignatura(String codigo, String nombre, char letra, int curso, String ciclo, Profesor docente) {
        // toUpperCase().trim() normaliza el código (ej. " mat-101 " -> "MAT-101")
        // para que buscar por código nunca falle por mayúsculas o espacios de más.
        this.codigo = codigo.toUpperCase().trim();
        this.nombre = nombre;
        this.letra = letra;
        this.curso = curso;
        this.ciclo = ciclo;
        this.docente = docente;
        // colecciones inicializadas vacías en el constructor.
        this.listaAlumnos = new ArrayList<>();
        this.listaRecursos = new ArrayList<>();

        // Asociación optimizada (Profesor <-> Asignatura): al crear la
        // asignatura, avisamos al profesor que ahora dicta este código.
        if (docente != null) {
            docente.agregarCodigoAsignatura(this.codigo);
        }
    }

    /**
     * Agrega un recurso digital a la asignatura.
     *
     * @param nuevoRecurso recurso a agregar (si es null no hace nada)
     * @throws RecursoDuplicadoException si ya existe un recurso con el mismo ID
     */
    public void agregarRecurso(RecursoDigital nuevoRecurso) throws RecursoDuplicadoException {
        if (nuevoRecurso == null) return;
        if (buscarRecurso(nuevoRecurso.getNumeroMaterial()) != null) {
            throw new RecursoDuplicadoException("Ya existe un recurso con el ID "
                    + nuevoRecurso.getNumeroMaterial() + " en la asignatura " + nombre + ".");
        }
        listaRecursos.add(nuevoRecurso);
    }
    /**
     * Imprime por consola todos los recursos digitales de la asignatura.
     */
    public void mostrarRecursos() {
        if (listaRecursos.isEmpty()) {
            System.out.println("  [Sin recursos digitales registrados en " + nombre + "]");
            return;
        }
        System.out.println("\n  --- RECURSOS DE " + nombre + " (" + codigo + ") ---");
        for (RecursoDigital r : listaRecursos) {
            r.mostrarDetalle();
        }
    }

    /**
     * Sobrecarga : busca un recurso por su ID.
     *
     * @param numeroMaterial ID del recurso
     * @return el recurso, o null si no existe
     */
    public RecursoDigital buscarRecurso(int numeroMaterial) {
        for (RecursoDigital r : listaRecursos) {
            if (r.getNumeroMaterial() == numeroMaterial) {
                return r;
            }
        }
        return null;
    }
    /**
     * Sobrecarga : busca un recurso por su título (sin distinguir mayúsculas).
     *
     * @param titulo título exacto del recurso
     * @return el recurso, o null si no existe
     */
    public RecursoDigital buscarRecurso(String titulo) {
        if (titulo == null) return null;
        for (RecursoDigital r : listaRecursos) {
            if (r.getTitulo().equalsIgnoreCase(titulo.trim())) {
                return r;
            }
        }
        return null;
    }
    /**
     * Elimina un recurso de la asignatura.
     *
     * @param numeroMaterial ID del recurso
     * @return true si se eliminó; false si no existía
     */
    public boolean eliminarRecurso(int numeroMaterial) {
        RecursoDigital r = buscarRecurso(numeroMaterial);
        if (r != null) {
            return listaRecursos.remove(r);
        }
        return false;
    }

    /**
     * Modifica los datos comunes de un recurso.
     *
     * @param numeroMaterial ID del recurso
     * @param nuevoTitulo nuevo título
     * @param nuevoFormato nuevo formato
     * @param nuevaUrl nueva URL
     * @return true si se editó; false si no existía
     */
    public boolean editarRecurso(int numeroMaterial, String nuevoTitulo, String nuevoFormato, String nuevaUrl) {
        RecursoDigital r = buscarRecurso(numeroMaterial);
        if (r != null) {
            r.setTitulo(nuevoTitulo);
            r.setFormato(nuevoFormato);
            r.setUrl(nuevaUrl);
            return true;
        }
        return false;
    }
    

    /**
     * Filtra los recursos y deja solo los videos.
     *
     * @return lista nueva con los videos de la asignatura
     */
    public ArrayList<RecursoVideo> filtrarVideos() {
        ArrayList<RecursoVideo> resultado = new ArrayList<>();
        for (RecursoDigital r : listaRecursos) {
            if (r instanceof RecursoVideo) {
                resultado.add((RecursoVideo) r);
            }
        }
        return resultado;
    }

    /**
     * Filtra los recursos y deja solo los documentos.
     *
     * @return lista nueva con los documentos de la asignatura
     */
    public ArrayList<RecursoDocumento> filtrarDocumentos() {
        ArrayList<RecursoDocumento> resultado = new ArrayList<>();
        for (RecursoDigital r : listaRecursos) {
            if (r instanceof RecursoDocumento) {
                resultado.add((RecursoDocumento) r);
            }
        }
        return resultado;
    }
    /**
     * Inscribe un alumno en la asignatura (si su RUT no estaba ya inscrito).
     *
     * @param nuevoAlumno alumno a inscribir (si es null no hace nada)
     */
    public void agregarAlumno(Alumno nuevoAlumno) {
        if (nuevoAlumno == null) return;
        if (buscarAlumno(nuevoAlumno.getRut()) == null) {
            listaAlumnos.add(nuevoAlumno);
            // Asociación optimizada: la Asignatura guarda al Alumno completo,
            // pero el Alumno solo guarda el código de texto de la Asignatura
            // (ver Alumno.agregarCodigoAsignatura). Así se evita el ciclo
            // infinito Asignatura -> Alumno -> Asignatura -> ...
            nuevoAlumno.agregarCodigoAsignatura(this.codigo);
        }
    }

    /**
     * Imprime por consola los alumnos inscritos en la asignatura.
     */
    public void mostrarAlumnos() {
        if (listaAlumnos.isEmpty()) {
            System.out.println("  [Sin alumnos inscritos en " + nombre + "]");
            return;
        }
        System.out.println("\n  --- ALUMNOS INSCRITOS EN " + nombre + " ---");
        for (Alumno a : listaAlumnos) {
            System.out.println("   * " + a);
        }
    }

    /**
     * Busca un alumno inscrito por su RUT.
     *
     * @param rut RUT del alumno
     * @return el alumno, o null si no está inscrito
     */
    public Alumno buscarAlumno(String rut) {
        if (rut == null) return null;
        for (Alumno a : listaAlumnos) {
            if (a.getRut().equalsIgnoreCase(rut.trim())) {
                return a;
            }
        }
        return null;
    }

    /**
     * Desvincula a un alumno de la asignatura.
     *
     * @param rut RUT del alumno
     * @return true si se desvinculó; false si no estaba inscrito
     */
    public boolean eliminarAlumno(String rut) {
        Alumno a = buscarAlumno(rut);
        if (a != null) {
            a.removerCodigoAsignatura(this.codigo);
            return listaAlumnos.remove(a);
        }
        return false;
    }

    /**
     * Modifica los datos de un alumno inscrito.
     *
     * @param rut RUT del alumno a editar
     * @param nuevoNombre nuevo nombre
     * @param nuevoCurso nuevo nivel de curso
     * @param nuevaLetra nueva letra
     * @param nuevoCiclo nuevo ciclo
     * @return true si se editó; false si no estaba inscrito
     */
    public boolean editarAlumno(String rut, String nuevoNombre, int nuevoCurso, char nuevaLetra, String nuevoCiclo) {
        Alumno a = buscarAlumno(rut);
        if (a != null) {
            a.setNombre(nuevoNombre);
            a.setCurso(nuevoCurso);
            a.setLetra(nuevaLetra);
            a.setCiclo(nuevoCiclo);
            return true;
        }
        return false;
    }
    //GETTERS Y SETTERS
    /**
     * Obtiene el dato: código único de la asignatura (siempre en mayúsculas y sin espacios sobrantes).
     *
     * @return código único de la asignatura (siempre en mayúsculas y sin espacios sobrantes)
     */
    public String getCodigo() { return codigo; }
    /**
     * Modifica el dato: código único de la asignatura (siempre en mayúsculas y sin espacios sobrantes).
     *
     * @param codigo nuevo valor: código único de la asignatura (siempre en mayúsculas y sin espacios sobrantes)
     */
    public void setCodigo(String codigo) { this.codigo = codigo.toUpperCase().trim(); }

    /**
     * Obtiene el dato: nombre completo.
     *
     * @return nombre completo
     */
    public String getNombre() { return nombre; }
    /**
     * Modifica el dato: nombre completo.
     *
     * @param nombre nuevo valor: nombre completo
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el dato: letra del curso (A, B, C...).
     *
     * @return letra del curso (A, B, C...)
     */
    public char getLetra() { return letra; }
    /**
     * Modifica el dato: letra del curso (A, B, C...).
     *
     * @param letra nuevo valor: letra del curso (A, B, C...)
     */
    public void setLetra(char letra) { this.letra = letra; }

    /**
     * Obtiene el dato: nivel del curso (1 a 4).
     *
     * @return nivel del curso (1 a 4)
     */
    public int getCurso() { return curso; }
    /**
     * Modifica el dato: nivel del curso (1 a 4).
     *
     * @param curso nuevo valor: nivel del curso (1 a 4)
     */
    public void setCurso(int curso) { this.curso = curso; }

    /**
     * Obtiene el dato: ciclo educativo (Basica o Media).
     *
     * @return ciclo educativo (Basica o Media)
     */
    public String getCiclo() { return ciclo; }
    /**
     * Modifica el dato: ciclo educativo (Basica o Media).
     *
     * @param ciclo nuevo valor: ciclo educativo (Basica o Media)
     */
    public void setCiclo(String ciclo) { this.ciclo = ciclo; }

    /**
     * Obtiene el dato: profesor que dicta la asignatura.
     *
     * @return profesor que dicta la asignatura
     */
    public Profesor getDocente() { return docente; }
    /**
     * Modifica el dato: profesor que dicta la asignatura.
     *
     * @param nuevoDocente nuevo valor: profesor que dicta la asignatura
     */
    public void setDocente(Profesor nuevoDocente) {
        // Antes de cambiar de profesor, avisamos al profesor ANTERIOR que
        // ya no dicta esta asignatura, para no dejar datos inconsistentes.
        if (this.docente != null) {
            this.docente.removerCodigoAsignatura(this.codigo);
        }
        this.docente = nuevoDocente;
        if (this.docente != null) {
            this.docente.agregarCodigoAsignatura(this.codigo);
        }
    }

    /**
     * Entrega una vista de solo lectura de los alumnos inscritos.
     *
     * @return lista no modificable de alumnos
     */
    public List<Alumno> getListaAlumnos() { return Collections.unmodifiableList(listaAlumnos); }
    /**
     * Entrega una vista de solo lectura de los recursos digitales.
     *
     * @return lista no modificable de recursos
     */
    public List<RecursoDigital> getListaRecursos() { return Collections.unmodifiableList(listaRecursos); }

    // @Override de Object.toString(): define cómo se ve una Asignatura al
    // imprimirla directamente, por ejemplo en Sistema.mostrarAsignaturas().
    @Override
    public String toString() {
        String nomDoc = (docente != null) ? docente.getNombre() : "Sin docente";
        return "[" + codigo + "] " + nombre + " (" + curso + "°" + letra + " " + ciclo + ") | Docente: " + nomDoc
                + " | Alumnos: " + listaAlumnos.size() + " | Recursos: " + listaRecursos.size();
    }
}
