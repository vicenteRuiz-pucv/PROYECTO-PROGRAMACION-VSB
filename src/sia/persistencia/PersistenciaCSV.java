package sia.persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import sia.Alumno;
import sia.Asignatura;
import sia.Profesor;
import sia.RecursoDigital;
import sia.RecursoDocumento;
import sia.RecursoEnlaceWeb;
import sia.RecursoVideo;
import sia.Sistema;
import sia.excepciones.NotaInvalidaException;
import sia.excepciones.RecursoDuplicadoException;

/**
 * SIA-11: Persistencia de datos mediante archivos de texto (CSV), con
 * sistema "batch": los datos se cargan una sola vez al iniciar la
 * aplicación (método cargar) y se graban una sola vez al salir (método
 * guardar). No se lee ni se escribe disco constantemente mientras el
 * programa está en uso, solo al principio y al final.
 *
 * ¿Por qué una clase aparte y no meter esta lógica dentro de Sistema?
 * Para mantener el código modularizado (SIA-3): Sistema se encarga de la
 * lógica de negocio (asignaturas, alumnos, recursos), y esta clase se
 * encarga exclusivamente de "traducir" esos objetos a texto y de vuelta.
 * Si mañana se quisiera cambiar de CSV a, por ejemplo, una base de datos
 * MySQL, solo habría que reescribir esta clase, sin tocar Sistema.
 *
 * Se generan 5 archivos dentro de la carpeta "datos_sia/", uno por cada
 * "tabla" de información:
 *   - asignaturas.csv    -> una fila por asignatura
 *   - recursos.csv       -> una fila por recurso digital
 *   - alumnos.csv        -> una fila por alumno (sin repetir, aunque esté
 *                            inscrito en varias asignaturas)
 *   - inscripciones.csv  -> une asignaturas con alumnos (relación muchos
 *                            a muchos: un alumno puede estar en varios
 *                            cursos, y un curso tiene varios alumnos)
 *   - notas.csv          -> una fila por cada nota registrada (SIA-9)
 *
 * NOTA IMPORTANTE: se usa el caracter ";" como separador de columnas.
 * Por simplicidad, esta versión asume que los nombres, títulos y URLs
 * ingresados no contienen ";". Es una limitación conocida y razonable
 * para el alcance de este proyecto (evitarla del todo requeriría un
 * formato más robusto, como CSV con comillas o JSON).
 */
public class PersistenciaCSV {

    private static final String CARPETA = "datos_sia";
    private static final String ARCHIVO_ASIGNATURAS = CARPETA + "/asignaturas.csv";
    private static final String ARCHIVO_RECURSOS = CARPETA + "/recursos.csv";
    private static final String ARCHIVO_ALUMNOS = CARPETA + "/alumnos.csv";
    private static final String ARCHIVO_INSCRIPCIONES = CARPETA + "/inscripciones.csv";
    private static final String ARCHIVO_NOTAS = CARPETA + "/notas.csv";
    private static final String SEP = ";";

    /**
     * Indica si ya existe una sesión guardada anteriormente. Se usa en
     * Main.java para decidir si hay que cargar datos desde archivo o si
     * el programa debe arrancar solo con los datos de ejemplo (SIA-3).
     */
    public static boolean existenDatosGuardados() {
        File f = new File(ARCHIVO_ASIGNATURAS);
        return f.exists() && f.length() > 0;
    }

    // ==========================================================
    //  GUARDAR (se ejecuta una sola vez, al salir del programa)
    // ==========================================================
    public static void guardar(Sistema sistema) {
        // mkdirs() crea la carpeta "datos_sia" si todavía no existe (por
        // ejemplo, la primera vez que se guarda algo).
        new File(CARPETA).mkdirs();

        // El "try-with-resources" (los PrintWriter declarados dentro del
        // paréntesis del try) cierra automáticamente los 5 archivos al
        // terminar el bloque, incluso si ocurre un error a mitad de camino.
        try (
                PrintWriter wAsig = new PrintWriter(new FileWriter(ARCHIVO_ASIGNATURAS));
                PrintWriter wRec = new PrintWriter(new FileWriter(ARCHIVO_RECURSOS));
                PrintWriter wAl = new PrintWriter(new FileWriter(ARCHIVO_ALUMNOS));
                PrintWriter wIns = new PrintWriter(new FileWriter(ARCHIVO_INSCRIPCIONES));
                PrintWriter wNotas = new PrintWriter(new FileWriter(ARCHIVO_NOTAS))
        ) {
            // Un alumno puede estar inscrito en varias asignaturas, pero
            // debe aparecer UNA sola vez en alumnos.csv. Este HashSet
            // guarda los RUT ya escritos para no duplicarlo.
            HashSet<String> alumnosYaGuardados = new HashSet<>();

            for (Asignatura asig : sistema.getMapaAsignaturas().values()) {
                Profesor prof = asig.getDocente();
                String nomDoc = (prof != null) ? prof.getNombre() : "";
                String rutDoc = (prof != null) ? prof.getRut() : "";
                String profDoc = (prof != null) ? prof.getProfesion() : "";

                wAsig.println(asig.getCodigo() + SEP + asig.getNombre() + SEP + asig.getLetra() + SEP
                        + asig.getCurso() + SEP + asig.getCiclo() + SEP + nomDoc + SEP + rutDoc + SEP + profDoc);

                // SIA-6: aquí se recorre la lista polimórfica de recursos.
                // Como cada tipo (Video/Documento/Enlace) guarda columnas
                // distintas, se usa "instanceof" para saber cuál es cuál
                // antes de escribir su fila — el mismo patrón que ya se
                // usa en Asignatura.filtrarVideos()/filtrarDocumentos().
                for (RecursoDigital r : asig.getListaRecursos()) {
                    if (r instanceof RecursoVideo) {
                        RecursoVideo v = (RecursoVideo) r;
                        wRec.println(asig.getCodigo() + SEP + "VIDEO" + SEP + v.getNumeroMaterial() + SEP
                                + v.getTitulo() + SEP + v.getFormato() + SEP + v.getUrl() + SEP
                                + v.getDuracionMinutos() + SEP + v.getResolucion());
                    } else if (r instanceof RecursoDocumento) {
                        RecursoDocumento d = (RecursoDocumento) r;
                        wRec.println(asig.getCodigo() + SEP + "DOCUMENTO" + SEP + d.getNumeroMaterial() + SEP
                                + d.getTitulo() + SEP + d.getFormato() + SEP + d.getUrl() + SEP
                                + d.getCantidadPaginas() + SEP + d.isEsEditable());
                    } else if (r instanceof RecursoEnlaceWeb) {
                        RecursoEnlaceWeb e = (RecursoEnlaceWeb) r;
                        wRec.println(asig.getCodigo() + SEP + "ENLACE" + SEP + e.getNumeroMaterial() + SEP
                                + e.getTitulo() + SEP + e.getFormato() + SEP + e.getUrl() + SEP
                                + e.isRequiereConexionExterna() + SEP + "");
                    }
                }

                for (Alumno al : asig.getListaAlumnos()) {
                    if (!alumnosYaGuardados.contains(al.getRut())) {
                        wAl.println(al.getRut() + SEP + al.getNombre() + SEP + al.getCurso() + SEP
                                + al.getLetra() + SEP + al.getCiclo());
                        alumnosYaGuardados.add(al.getRut());
                    }
                    // Relación muchos-a-muchos: una fila por cada pareja
                    // (asignatura, alumno).
                    wIns.println(asig.getCodigo() + SEP + al.getRut());

                    // SIA-9: se guarda cada nota del alumno EN ESA asignatura.
                    for (double nota : al.obtenerNotas(asig.getCodigo())) {
                        wNotas.println(al.getRut() + SEP + asig.getCodigo() + SEP + nota);
                    }
                }
            }
        } catch (IOException e) {
            // Si por algún motivo no se pudo escribir en disco (por
            // ejemplo, sin permisos de escritura en la carpeta), se avisa
            // en vez de que el programa se caiga sin explicación.
            System.out.println("No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    // ==========================================================
    //  CARGAR (se ejecuta una sola vez, al iniciar el programa)
    // ==========================================================
    public static void cargar(Sistema sistema) {
        // Mapa auxiliar temporal: mientras se leen los archivos, se
        // necesita poder encontrar rápidamente a un Alumno por su RUT
        // (para inscribirlo en sus asignaturas y cargarle sus notas).
        // No es una de las colecciones "oficiales" del diseño (SIA-4);
        // es solo una herramienta de trabajo interna de este método.
        HashMap<String, Alumno> alumnosPorRut = new HashMap<>();

        cargarAsignaturas(sistema);
        cargarRecursos(sistema);
        cargarAlumnos(alumnosPorRut);
        cargarInscripciones(sistema, alumnosPorRut);
        cargarNotas(alumnosPorRut);
    }

    private static void cargarAsignaturas(Sistema sistema) {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_ASIGNATURAS))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                // "-1" en split() conserva las columnas vacías al final
                // (por ejemplo, si una asignatura no tiene profesor).
                String[] p = linea.split(SEP, -1);
                Profesor prof = null;
                if (!p[6].isEmpty()) { // p[6] = RUT del docente
                    prof = new Profesor(p[5], p[6], p[7]);
                }
                Asignatura asig = new Asignatura(p[0], p[1], p[2].charAt(0),
                        Integer.parseInt(p[3]), p[4], prof);
                sistema.agregarAsignatura(asig);
            }
        } catch (IOException e) {
            System.out.println("No se encontraron asignaturas guardadas (" + e.getMessage() + ").");
        }
    }

    private static void cargarRecursos(Sistema sistema) {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_RECURSOS))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split(SEP, -1);
                Asignatura asig = sistema.buscarAsignatura(p[0]);
                if (asig == null) continue; // fila huérfana, se ignora

                String tipo = p[1];
                int id = Integer.parseInt(p[2]);
                String titulo = p[3];
                String formato = p[4];
                String url = p[5];

                // SIA-6: según el texto guardado ("VIDEO", "DOCUMENTO" o
                // "ENLACE"), se reconstruye el objeto de la subclase
                // correcta. Es el mismo tipo de decisión que ya se hace
                // en Main.menuRecursos() al crear un recurso nuevo.
                RecursoDigital nuevo = null;
                if (tipo.equals("VIDEO")) {
                    nuevo = new RecursoVideo(id, titulo, url, Integer.parseInt(p[6]), p[7]);
                } else if (tipo.equals("DOCUMENTO")) {
                    nuevo = new RecursoDocumento(id, titulo, formato, url,
                            Integer.parseInt(p[6]), Boolean.parseBoolean(p[7]));
                } else if (tipo.equals("ENLACE")) {
                    nuevo = new RecursoEnlaceWeb(id, titulo, url, Boolean.parseBoolean(p[6]));
                }

                if (nuevo != null) {
                    // SIA-12: se reutiliza la misma excepción propia. Si
                    // dos filas del archivo tuvieran, por error, el mismo
                    // ID, se avisa en vez de romper la carga completa.
                    try {
                        asig.agregarRecurso(nuevo);
                    } catch (RecursoDuplicadoException e) {
                        System.out.println("Recurso duplicado al cargar datos: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se encontraron recursos guardados (" + e.getMessage() + ").");
        }
    }

    private static void cargarAlumnos(HashMap<String, Alumno> alumnosPorRut) {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_ALUMNOS))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split(SEP, -1);
                Alumno al = new Alumno(p[1], p[0], p[3].charAt(0), p[4], Integer.parseInt(p[2]));
                alumnosPorRut.put(al.getRut(), al);
            }
        } catch (IOException e) {
            System.out.println("No se encontraron alumnos guardados (" + e.getMessage() + ").");
        }
    }

    private static void cargarInscripciones(Sistema sistema, HashMap<String, Alumno> alumnosPorRut) {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_INSCRIPCIONES))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split(SEP, -1);
                Asignatura asig = sistema.buscarAsignatura(p[0]);
                Alumno al = alumnosPorRut.get(p[1]);
                // La Asignatura vuelve a "adoptar" al mismo objeto Alumno
                // que ya se creó en cargarAlumnos(), en vez de crear uno
                // nuevo. Esto respeta la misma idea de
                // Sistema.buscarAlumnoGlobal(): nunca duplicar personas.
                if (asig != null && al != null) {
                    asig.agregarAlumno(al);
                }
            }
        } catch (IOException e) {
            System.out.println("No se encontraron inscripciones guardadas (" + e.getMessage() + ").");
        }
    }

    private static void cargarNotas(HashMap<String, Alumno> alumnosPorRut) {
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_NOTAS))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split(SEP, -1);
                Alumno al = alumnosPorRut.get(p[0]);
                if (al != null) {
                    try {
                        al.agregarNota(p[1], Double.parseDouble(p[2]));
                    } catch (NotaInvalidaException e) {
                        System.out.println("Nota inválida al cargar datos: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se encontraron notas guardadas (" + e.getMessage() + ").");
        }
    }
}
