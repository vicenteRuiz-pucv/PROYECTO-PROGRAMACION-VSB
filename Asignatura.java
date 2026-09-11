package sia;

import java.util.ArrayList;
import sia.excepciones.RecursoDuplicadoException;
/**
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
    
    public Asignatura(String codigo, String nombre, char letra, int curso, String ciclo, Profesor docente) {
        // toUpperCase().trim() normaliza el código (ej. " mat-101 " -> "MAT-101")
        // para que buscar por código nunca falle por mayúsculas o espacios de más.
        this.codigo = codigo.toUpperCase().trim();
        this.nombre = nombre;
        this.letra = letra;
        this.curso = curso;
        this.ciclo = ciclo;
        this.docente = docente;
        // SIA-3: colecciones inicializadas vacías en el constructor.
        this.listaAlumnos = new ArrayList<>();
        this.listaRecursos = new ArrayList<>();

        // Asociación optimizada (Profesor <-> Asignatura): al crear la
        // asignatura, avisamos al profesor que ahora dicta este código.
        if (docente != null) {
            docente.agregarCodigoAsignatura(this.codigo);
        }
    }

    public void agregarRecurso(RecursoDigital nuevoRecurso) throws RecursoDuplicadoException {
        if (nuevoRecurso == null) return;
        if (buscarRecurso(nuevoRecurso.getNumeroMaterial()) != null) {
            throw new RecursoDuplicadoException("Ya existe un recurso con el ID "
                    + nuevoRecurso.getNumeroMaterial() + " en la asignatura " + nombre + ".");
        }
        listaRecursos.add(nuevoRecurso);
    }
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

    public RecursoDigital buscarRecurso(int numeroMaterial) {
        for (RecursoDigital r : listaRecursos) {
            if (r.getNumeroMaterial() == numeroMaterial) {
                return r;
            }
        }
        return null;
    }
    public RecursoDigital buscarRecurso(String titulo) {
        if (titulo == null) return null;
        for (RecursoDigital r : listaRecursos) {
            if (r.getTitulo().equalsIgnoreCase(titulo.trim())) {
                return r;
            }
        }
        return null;
    }
    public boolean eliminarRecurso(int numeroMaterial) {
        RecursoDigital r = buscarRecurso(numeroMaterial);
        if (r != null) {
            return listaRecursos.remove(r);
        }
        return false;
    }

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
    

    public ArrayList<RecursoVideo> filtrarVideos() {
        ArrayList<RecursoVideo> resultado = new ArrayList<>();
        for (RecursoDigital r : listaRecursos) {
            if (r instanceof RecursoVideo) {
                resultado.add((RecursoVideo) r);
            }
        }
        return resultado;
    }

    public ArrayList<RecursoDocumento> filtrarDocumentos() {
        ArrayList<RecursoDocumento> resultado = new ArrayList<>();
        for (RecursoDigital r : listaRecursos) {
            if (r instanceof RecursoDocumento) {
                resultado.add((RecursoDocumento) r);
            }
        }
        return resultado;
    }
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

    public Alumno buscarAlumno(String rut) {
        if (rut == null) return null;
        for (Alumno a : listaAlumnos) {
            if (a.getRut().equalsIgnoreCase(rut.trim())) {
                return a;
            }
        }
        return null;
    }

    public boolean eliminarAlumno(String rut) {
        Alumno a = buscarAlumno(rut);
        if (a != null) {
            a.removerCodigoAsignatura(this.codigo);
            return listaAlumnos.remove(a);
        }
        return false;
    }

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
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public char getLetra() { return letra; }
    public void setLetra(char letra) { this.letra = letra; }

    public int getCurso() { return curso; }
    public void setCurso(int curso) { this.curso = curso; }

    public String getCiclo() { return ciclo; }
    public void setCiclo(String ciclo) { this.ciclo = ciclo; }

    public Profesor getDocente() { return docente; }
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

    public ArrayList<Alumno> getListaAlumnos() { return listaAlumnos; }
    public ArrayList<RecursoDigital> getListaRecursos() { return listaRecursos; }

    // @Override de Object.toString(): define cómo se ve una Asignatura al
    // imprimirla directamente, por ejemplo en Sistema.mostrarAsignaturas().
    @Override
    public String toString() {
        String nomDoc = (docente != null) ? docente.getNombre() : "Sin docente";
        return "[" + codigo + "] " + nombre + " (" + curso + "°" + letra + " " + ciclo + ") | Docente: " + nomDoc
                + " | Alumnos: " + listaAlumnos.size() + " | Recursos: " + listaRecursos.size();
    }
}
