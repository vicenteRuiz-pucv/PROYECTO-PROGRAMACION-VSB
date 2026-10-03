package sia;
import sia.exportacion.ExportadorPlanilla;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import sia.excepciones.NotaInvalidaException;
import sia.excepciones.RecursoDuplicadoException;
import sia.gui.VentanaPrincipal;
import sia.persistencia.PersistenciaCSV;

/**
 * Clase principal del programa: pregunta si se usa consola o ventana, carga
 * los datos al iniciar (CSV) y contiene los menús del modo consola.
 *
 * @author Pc
 */
public class sia {

    private sia() {
        // clase de arranque: solo contiene métodos estáticos, no se instancia
    }
 
    private static Sistema sistema;
    private static Scanner scanner = new Scanner(System.in);
    /**
     * Punto de entrada: carga los datos guardados (si existen), pregunta si se
     * usa consola o ventana y lanza el modo elegido.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        // (Persistencia, sistema batch): 
        // Si hay datos de una sesión anterior, se crea el sistema vacío y se
        // cargan desde los archivos CSV. Si es la primera ejecución, se usan
        // los datos de ejemplo que trae el Sistema.
            
        boolean hayDatos = PersistenciaCSV.existenDatosGuardados();
        sistema = hayDatos ? new Sistema(false) : new Sistema();
        if (hayDatos){
            PersistenciaCSV.cargar(sistema);
            System.out.println("Datos cargados desde la ultima sesión guardada");
        }
        

       
        System.out.println("\n=== SIA - Selección de modo de uso ===");
        System.out.println("1. Consola");
        System.out.println("2. Ventana (interfaz gráfica)");
        System.out.print("Seleccione una opción: ");
        String modo = scanner.nextLine().trim();

        if (modo.equals("2")) {
 
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    VentanaPrincipal ventana = new VentanaPrincipal(sistema);
                    ventana.setVisible(true);
                }
            });
        } else {
            iniciarConsola();
        }
    }

    private static void iniciarConsola() {
        int opcion = -1;
        do {
            System.out.println("\n╔═══════════════════════════════════════════════════╗");
            System.out.println("║   SIA - GESTIÓN DE RECURSOS EDUCATIVOS DIGITALES   ║");
            System.out.println("╠═══════════════════════════════════════════════════╣");
            System.out.println("║ 1. Gestión de Asignaturas (Colección 1 - HashMap)  ║");
            System.out.println("║ 2. Gestión de Recursos Digitales (Colección 2)     ║");
            System.out.println("║ 3. Gestión de Alumnos                              ║");
            System.out.println("║ 4. Boletín Académico (Funcionalidad estrella SIA-9)║");
            System.out.println("║ 5. Demostración SIA-5 (Sobrecarga) & SIA-6         ║");
            System.out.println("║ 6. Exportar boletín a Excel                        ║");
            System.out.println("║ 0. Salir (guarda los datos automáticamente)        ║");
            System.out.println("╚═══════════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            // try-catch
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
                switch (opcion) {
                    case 1: menuAsignaturas(); break;
                    case 2: menuRecursos(); break;
                    case 3: menuAlumnos(); break;
                    case 4: menuBoletinAcademico(); break;
                    case 5: demoSobrecargaYSobreescritura(); break;
                    case 6:
                        if (ExportadorPlanilla.exportarBoletin(sistema)) {
                            System.out.println("Planilla creada en datos_sia/boletin_notas.xlsx");
                        } else {
                            System.out.println("No se pudo crear la planilla.");
                        }
                    break;
                    case 0:
                        // sistema "batch" -> se graban todos los
                        // datos en archivos justo antes de salir.
                        PersistenciaCSV.guardar(sistema);
                        System.out.println("Datos guardados. ¡Gracias por utilizar el sistema SIA!");
                        break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
            }
        } while (opcion != 0);
        scanner.close();
    }

    // ==========================================================
    // MENÚ DE LA COLECCIÓN 1 (ASIGNATURAS)
    // ==========================================================
    private static void menuAsignaturas() {
        System.out.println("\n--- GESTIÓN DE ASIGNATURAS ---");
        System.out.println("1. Listar Asignaturas");
        System.out.println("2. Agregar Asignatura");
        System.out.println("3. Buscar Asignatura por Código");
        System.out.println("4. Editar Asignatura");
        System.out.println("5. Eliminar Asignatura");
        System.out.print("Opción: ");

        try {
            int op = Integer.parseInt(scanner.nextLine().trim());
            switch (op) {
                case 1:
                    sistema.mostrarAsignaturas(); // mostrar listado
                    break;
                case 2: { // inserción manual
                    System.out.print("Código (ej. MAT-101): ");
                    String cod = scanner.nextLine().trim();
                    System.out.print("Nombre: ");
                    String nom = scanner.nextLine().trim();
                    System.out.print("Letra (A/B/C): ");
                    char letra = scanner.nextLine().trim().toUpperCase().charAt(0);
                    System.out.print("Nivel Curso (1 a 4): ");
                    int cur = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Ciclo (Basica/Media): ");
                    String ciclo = scanner.nextLine().trim();

                    System.out.print("Nombre Docente: ");
                    String nomDoc = scanner.nextLine().trim();
                    System.out.print("RUT Docente: ");
                    String rutDoc = scanner.nextLine().trim();
                    System.out.print("Especialidad Docente: ");
                    String espDoc = scanner.nextLine().trim();

                    Profesor prof = new Profesor(nomDoc, rutDoc, espDoc);
                    Asignatura asig = new Asignatura(cod, nom, letra, cur, ciclo, prof);
                    if (sistema.agregarAsignatura(asig)) {
                        System.out.println("Asignatura agregada correctamente.");
                    } else {
                        System.out.println("Ya existe una asignatura con el código " + asig.getCodigo() + ". No se agregó.");
                    }
                    break;
                }
                case 3: { // búsqueda de un elemento
                    System.out.print("Código a buscar: ");
                    String bCod = scanner.nextLine().trim();
                    Asignatura encontrada = sistema.buscarAsignatura(bCod);
                    if (encontrada != null) {
                        System.out.println("Encontrada: " + encontrada);
                    } else {
                        System.out.println("No se encontró la asignatura.");
                    }
                    break;
                }
                case 4: { // edición de un elemento
                    System.out.print("Código de la asignatura a modificar: ");
                    String mCod = scanner.nextLine().trim();
                    if (sistema.buscarAsignatura(mCod) != null) {
                        System.out.print("Nuevo Nombre: ");
                        String nNom = scanner.nextLine().trim();
                        System.out.print("Nueva Letra: ");
                        char nLetra = scanner.nextLine().trim().toUpperCase().charAt(0);
                        System.out.print("Nuevo Curso: ");
                        int nCur = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Nuevo Ciclo: ");
                        String nCiclo = scanner.nextLine().trim();

                        sistema.editarAsignatura(mCod, nNom, nLetra, nCur, nCiclo);
                        System.out.println("Asignatura editada correctamente.");
                    } else {
                        System.out.println("No existe dicha asignatura.");
                    }
                    break;
                }
                case 5: { // eliminación de un elemento
                    System.out.print("Código de asignatura a eliminar: ");
                    String eCod = scanner.nextLine().trim();
                    if (sistema.eliminarAsignatura(eCod)) {
                        System.out.println("Asignatura eliminada del mapa.");
                    } else {
                        System.out.println("No se pudo eliminar.");
                    }
                    break;
                }
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un número válido en los campos numéricos.");
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Error: la letra del curso no puede estar vacía.");
        }
    }

    // ==========================================================
    //  MENÚ DE LA COLECCIÓN 2 (RECURSOS DIGITALES)
    // ==========================================================
    private static void menuRecursos() {
        System.out.print("\nIngrese el código de la asignatura a gestionar: ");
        String cod = scanner.nextLine().trim();
        Asignatura asig = sistema.buscarAsignatura(cod); // usa el HashMap
        if (asig == null) {
            System.out.println("Asignatura no encontrada.");
            return;
        }

        System.out.println("\n--- RECURSOS DE " + asig.getNombre() + " ---");
        System.out.println("1. Listar Recursos");
        System.out.println("2. Agregar Recurso");
        System.out.println("3. Buscar Recurso por ID o Título (Sobrecarga)");
        System.out.println("4. Editar Recurso");
        System.out.println("5. Eliminar Recurso");
        System.out.print("Opción: ");

        try {
            int op = Integer.parseInt(scanner.nextLine().trim());
            switch (op) {
                case 1:
                    asig.mostrarRecursos();
                    break;
                case 2: {
                    System.out.println("Tipo: 1. Video MP4 | 2. Documento PDF/Guía | 3. Enlace Web");
                    int tipo = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("ID / Número de material: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Título: ");
                    String tit = scanner.nextLine().trim();
                    System.out.print("URL: ");
                    String url = scanner.nextLine().trim();

                    RecursoDigital nuevo;
                    if (tipo == 1) {
                        System.out.print("Duración en minutos: ");
                        int min = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Resolución (1080p, 720p): ");
                        String cal = scanner.nextLine().trim();
                        nuevo = new RecursoVideo(id, tit, url, min, cal);
                    } else if (tipo == 2) {
                        System.out.print("Cantidad de páginas: ");
                        int pags = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("¿Es editable? (true/false): ");
                        boolean edit = Boolean.parseBoolean(scanner.nextLine().trim());
                        nuevo = new RecursoDocumento(id, tit, "PDF", url, pags, edit);
                    } else {
                        System.out.print("¿Requiere conexión externa? (true/false): ");
                        boolean externo = Boolean.parseBoolean(scanner.nextLine().trim());
                        nuevo = new RecursoEnlaceWeb(id, tit, url, externo);
                    }


                    try {
                        asig.agregarRecurso(nuevo);
                        System.out.println("Recurso agregado exitosamente.");
                    } catch (RecursoDuplicadoException e) {
                        System.out.println("No se pudo agregar: " + e.getMessage());
                    }
                    break;
                }
                case 3: {
                    System.out.print("Buscar por: 1. ID | 2. Título (Sobrecarga SIA-5): ");
                    int mod = Integer.parseInt(scanner.nextLine().trim());
                    RecursoDigital rEnc;
                    if (mod == 1) {
                        System.out.print("ID: ");
                        int idB = Integer.parseInt(scanner.nextLine().trim());
                        rEnc = asig.buscarRecurso(idB); // llama a la versión con int
                    } else {
                        System.out.print("Título exacto: ");
                        String titB = scanner.nextLine().trim();
                        rEnc = asig.buscarRecurso(titB); // llama a la versión con String (sobrecarga)
                    }
                    if (rEnc != null) {
                        System.out.println("Recurso encontrado:");
                        rEnc.mostrarDetalle(true); // versión detallada (sobrecarga en RecursoDigital)
                    } else {
                        System.out.println("No se encontró el recurso.");
                    }
                    break;
                }
                case 4: {
                    System.out.print("ID del recurso a editar: ");
                    int idEd = Integer.parseInt(scanner.nextLine().trim());
                    if (asig.buscarRecurso(idEd) != null) {
                        System.out.print("Nuevo Título: ");
                        String nTit = scanner.nextLine().trim();
                        System.out.print("Nuevo Formato: ");
                        String nForm = scanner.nextLine().trim();
                        System.out.print("Nueva URL: ");
                        String nUrl = scanner.nextLine().trim();
                        asig.editarRecurso(idEd, nTit, nForm, nUrl);
                        System.out.println("Recurso actualizado.");
                    } else {
                        System.out.println("Recurso no encontrado.");
                    }
                    break;
                }
                case 5: {
                    System.out.print("ID del recurso a eliminar: ");
                    int idEl = Integer.parseInt(scanner.nextLine().trim());
                    if (asig.eliminarRecurso(idEl)) {
                        System.out.println("Recurso eliminado de la asignatura.");
                    } else {
                        System.out.println("No se pudo eliminar.");
                    }
                    break;
                }
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un número válido.");
        }
    }

    // ==========================================================
    //  MENÚ ALUMNOS
    // ==========================================================
    private static void menuAlumnos() {
        System.out.print("\nIngrese el código de la asignatura: ");
        String cod = scanner.nextLine().trim();
        Asignatura asig = sistema.buscarAsignatura(cod);
        if (asig == null) {
            System.out.println("Asignatura no encontrada.");
            return;
        }

        System.out.println("\n--- ALUMNOS DE " + asig.getNombre() + " ---");
        System.out.println("1. Listar Alumnos");
        System.out.println("2. Inscribir Alumno");
        System.out.println("3. Eliminar Alumno");
        System.out.print("Opción: ");

        try {
            int op = Integer.parseInt(scanner.nextLine().trim());
            switch (op) {
                case 1:
                    asig.mostrarAlumnos();
                    break;
                case 2: {
                    System.out.print("RUT Alumno: ");
                    String rut = scanner.nextLine().trim();

                    // se busca primero en TODO el sistema (no solo
                    // en esta asignatura) para no duplicar a la misma
                    // persona si ya está inscrita en otro curso.
                    Alumno existente = sistema.buscarAlumnoGlobal(rut);
                    if (existente != null) {
                        asig.agregarAlumno(existente);
                        System.out.println(existente.getNombre() + " fue inscrito también en esta asignatura.");
                    } else {
                        System.out.print("Nombre Completo: ");
                        String nom = scanner.nextLine().trim();
                        System.out.print("Curso: ");
                        int cur = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Letra: ");
                        char let = scanner.nextLine().trim().toUpperCase().charAt(0);
                        System.out.print("Ciclo: ");
                        String cic = scanner.nextLine().trim();

                        asig.agregarAlumno(new Alumno(nom, rut, let, cic, cur));
                        System.out.println("Alumno inscrito en la asignatura.");
                    }
                    break;
                }
                case 3: {
                    System.out.print("RUT del alumno a desvincular: ");
                    String rElim = scanner.nextLine().trim();
                    if (asig.eliminarAlumno(rElim)) {
                        System.out.println("Alumno desvinculado con éxito.");
                    } else {
                        System.out.println("No se encontró el RUT.");
                    }
                    break;
                }
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un número válido.");
        } catch (StringIndexOutOfBoundsException e){
            System.out.println("Error: la letra del curso no puede estar vacia");
        }
    }

    // ==========================================================
    //  MENÚ DE LA FUNCIONALIDAD ESTRELLA (BOLETÍN ACADÉMICO)
    // ==========================================================
    private static void menuBoletinAcademico() {
        System.out.println("\n--- BOLETÍN ACADÉMICO ---");
        System.out.println("1. Registrar nota a un alumno");
        System.out.println("2. Editar una nota existente");
        System.out.println("3. Eliminar una nota");
        System.out.println("4. Ver boletín de un alumno en una asignatura");
        System.out.println("5. Reporte: alumnos en riesgo de reprobar (todo el sistema)");
        System.out.print("Opción: ");

        try {
            int op = Integer.parseInt(scanner.nextLine().trim());

            if (op == 5) {
                
                ArrayList<Alumno> enRiesgo = sistema.listarAlumnosEnRiesgo();
                if (enRiesgo.isEmpty()) {
                    System.out.println("No hay alumnos en riesgo de reprobar por el momento.");
                } else {
                    System.out.println("\n--- ALUMNOS EN RIESGO (promedio general < " + Alumno.NOTA_APROBACION + ") ---");
                    for (Alumno a : enRiesgo) {
                        System.out.printf("  * %s -> Promedio general: %.1f%n", a.getNombre(), a.calcularPromedio());
                    }
                }
                return;
            }

            System.out.print("Código de la asignatura: ");
            String cod = scanner.nextLine().trim();
            Asignatura asig = sistema.buscarAsignatura(cod);
            if (asig == null) {
                System.out.println("Asignatura no encontrada.");
                return;
            }
            System.out.print("RUT del alumno: ");
            String rut = scanner.nextLine().trim();
            Alumno alumno = asig.buscarAlumno(rut);
            if (alumno == null) {
                System.out.println("El alumno no está inscrito en esa asignatura.");
                return;
            }

            switch (op) {
                case 1: { // inserción de una nota
                    System.out.print("Nota a registrar (1.0 a 7.0): ");
                    double nota = Double.parseDouble(scanner.nextLine().trim());
                    // try-catch obligatorio para la excepción propia.
                    try {
                        alumno.agregarNota(cod.toUpperCase().trim(), nota);
                        System.out.println("Nota registrada correctamente.");
                    } catch (NotaInvalidaException e) {
                        System.out.println("No se pudo registrar la nota: " + e.getMessage());
                    }
                    break;
                }
                case 2: { // edición de una nota
                    mostrarNotasConIndice(alumno, cod);
                    System.out.print("Índice de la nota a editar: ");
                    int idx = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Nueva nota: ");
                    double nueva = Double.parseDouble(scanner.nextLine().trim());
                    try {
                        if (alumno.editarNota(cod.toUpperCase().trim(), idx, nueva)) {
                            System.out.println("Nota actualizada.");
                        } else {
                            System.out.println("No se encontró esa nota.");
                        }
                    } catch (NotaInvalidaException e) {
                        System.out.println("No se pudo editar: " + e.getMessage());
                    }
                    break;
                }
                case 3: { // eliminación de una nota
                    mostrarNotasConIndice(alumno, cod);
                    System.out.print("Índice de la nota a eliminar: ");
                    int idx = Integer.parseInt(scanner.nextLine().trim());
                    if (alumno.eliminarNota(cod.toUpperCase().trim(), idx)) {
                        System.out.println("Nota eliminada.");
                    } else {
                        System.out.println("No se encontró esa nota.");
                    }
                    break;
                }
                case 4:
                    mostrarBoletin(alumno, cod);
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un número válido.");
        }
    }

    private static void mostrarNotasConIndice(Alumno alumno, String codigoAsignatura) {
        ArrayList<Double> notas = alumno.obtenerNotas(codigoAsignatura.toUpperCase().trim());
        if (notas.isEmpty()) {
            System.out.println("Este alumno no tiene notas registradas en esta asignatura.");
            return;
        }
        for (int i = 0; i < notas.size(); i++) {
            System.out.println("  [" + i + "] " + notas.get(i));
        }
    }

    /**
     *  genera el un detalle actual del alumno -> promedio, barra de
     * progreso visual y la nota necesaria para aprobar según cuántas
     * evaluaciones le quedan al alumno.
     */
    private static void mostrarBoletin(Alumno alumno, String codigoAsignatura) {
        String cod = codigoAsignatura.toUpperCase().trim();
        double promedio = alumno.calcularPromedio(cod); // sobrecarga con String
        ArrayList<Double> notas = alumno.obtenerNotas(cod);

        System.out.println("\n===== BOLETÍN DE " + alumno.getNombre() + " (" + cod + ") =====");
        if (notas.isEmpty()) {
            System.out.println("Aún no tiene notas registradas en esta asignatura.");
            return;
        }
        System.out.println("Notas registradas: " + notas);
        System.out.printf("Promedio actual: %.2f%n", promedio);
        System.out.println("Avance hacia la aprobación (4.0): " + generarBarraProgreso(promedio));

        if (promedio >= Alumno.NOTA_APROBACION) {
            System.out.println("Estado: APROBANDO la asignatura.");
        } else {
            System.out.print("¿Cuántas evaluaciones le quedan en el semestre? ");
            try {
                int restantes = Integer.parseInt(scanner.nextLine().trim());
                if (restantes > 0) {
                    double necesaria = alumno.calcularNotaNecesaria(cod, restantes);
                    if (necesaria > Alumno.NOTA_MAXIMA) {
                        System.out.println("Con las evaluaciones que quedan ya no es matemáticamente "
                                + "posible llegar al 4.0. Se recomienda reforzamiento o evaluación diferenciada.");
                    } else {
                        System.out.printf("Necesita un promedio de %.2f en las evaluaciones restantes para aprobar.%n", necesaria);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("No se ingresó un número válido; se omite el cálculo de nota necesaria.");
            }
        }
        System.out.println("=========================================================");
    }

    /**
     * Genera una barra de progreso por ejemplo:
     * [████████████░░░░░░░░] 60%
     */
    private static String generarBarraProgreso(double promedio) {
        int total = 20; // cantidad de "casilleros" de la barra
        double porcentaje = Math.min(promedio / Alumno.NOTA_MAXIMA, 1.0);
        int llenos = (int) Math.round(porcentaje * total);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < total; i++) {
            barra.append(i < llenos ? "█" : "░");
        }
        barra.append("] ").append(String.format("%.0f%%", porcentaje * 100));
        return barra.toString();
    }

    // ==========================================================
    //  DEMOSTRACIÓN EXPLÍCITA PARA LA DEFENSA DEL PROYECTO
    // ==========================================================
    private static void demoSobrecargaYSobreescritura() {
        System.out.println("\n=======================================================");
        System.out.println("  DEMOSTRACIÓN DE CRITERIOS SIA-5 Y SIA-6");
        System.out.println("=======================================================");

        Asignatura mat = sistema.buscarAsignatura("MAT-101");
        if (mat != null) {
            System.out.println("\n1. SOBRECARGA DE MÉTODOS (SIA-5) en RecursoDigital:");
            RecursoDigital r = mat.buscarRecurso(1);
            if (r != null) {
                System.out.println("Llamada a mostrarDetalle() [resumido]:");
                r.mostrarDetalle();
                System.out.println("Llamada a mostrarDetalle(true) [con URL y tiempo estimado]:");
                r.mostrarDetalle(true);
            }

            System.out.println("\n1b. SOBRECARGA DE MÉTODOS (SIA-5) en Alumno:");
            Alumno al1 = mat.buscarAlumno("20.123.456-7");
            if (al1 != null) {
                System.out.printf("calcularPromedio(\"MAT-101\") -> %.2f%n", al1.calcularPromedio("MAT-101"));
                System.out.printf("calcularPromedio() [general, todas las asignaturas] -> %.2f%n", al1.calcularPromedio());
            }

            System.out.println("\n2. SOBREESCRITURA Y POLIMORFISMO (SIA-6):");
            for (RecursoDigital rec : mat.getListaRecursos()) {
                // Polimorfismo en tiempo de ejecución: la variable "rec" es
                // de tipo RecursoDigital, pero cada objeto real (Video,
                // Documento o Enlace) responde con SU PROPIA versión de
                // obtenerFichaTecnica() y estimarTiempoConsumoMinutos().
                System.out.println("Ficha -> " + rec.obtenerFichaTecnica()
                        + " | Tiempo estimado: " + rec.estimarTiempoConsumoMinutos() + " min");
            }
        } else {
            System.out.println("No se encontró la asignatura MAT-101 para la demostración.");
        }
        System.out.println("=======================================================");
    }
}

