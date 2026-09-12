package sia.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

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
import sia.persistencia.PersistenciaCSV;

/**
 * SIA-10 (parte "ventana"): interfaz gráfica construida con Swing.
 *
 * IMPORTANTE: esta clase NO vuelve a programar la lógica del sistema. Cada
 * botón llama exactamente a los mismos métodos de Sistema, Asignatura y
 * Alumno que ya usa el menú de consola en Main.java (menuAsignaturas(),
 * menuRecursos(), etc.). Lo único que cambia es CÓMO se piden los datos:
 * en la consola se usa Scanner + System.out.println, y aquí se usan
 * JOptionPane (cuadros de diálogo) y JTextArea (áreas de texto) para
 * mostrar resultados. La pauta pide que "todas las funcionalidades" estén
 * disponibles en ambos modos, y por eso esta ventana repite, una por una,
 * las mismas opciones de los 5 menús de consola.
 *
 * Para simplificar la interfaz (y no tener que enseñar componentes Swing
 * más avanzados como JTable), cada botón pide sus datos con una secuencia
 * de cuadros de diálogo (JOptionPane.showInputDialog), igual que la
 * consola pide sus datos línea por línea. El resultado de cada operación
 * se muestra en el área de texto grande de cada pestaña.
 */
public class VentanaPrincipal extends JFrame {

    private Sistema sistema;

    // Un JTextArea por pestaña, donde se van mostrando los listados y los
    // mensajes de resultado de cada operación (agregar, buscar, etc.).
    private JTextArea areaAsignaturas;
    private JTextArea areaRecursos;
    private JTextArea areaAlumnos;
    private JTextArea areaBoletin;

    public VentanaPrincipal(Sistema sistema) {
        super("SIA - Gestión de Recursos Educativos Digitales (Modo Ventana)");
        this.sistema = sistema;

        setSize(800, 560);
        setLocationRelativeTo(null); // centra la ventana en la pantalla
        // No usamos EXIT_ON_CLOSE directamente porque antes de cerrar
        // necesitamos guardar los datos (SIA-11). Se maneja a mano más
        // abajo con un WindowListener.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Asignaturas", crearPanelAsignaturas());
        pestañas.addTab("Recursos Digitales", crearPanelRecursos());
        pestañas.addTab("Alumnos", crearPanelAlumnos());
        pestañas.addTab("Boletín Académico (SIA-9)", crearPanelBoletin());

        add(pestañas, BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);

        // SIA-11: si el usuario cierra la ventana con la "X", igual se
        // guardan los datos antes de terminar el programa. Se usa una
        // clase anónima (WindowAdapter) en vez de una lambda, para
        // mantener el mismo estilo del resto del proyecto.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarYSalir();
            }
        });

        actualizarAsignaturas();
    }

    /** Botón visible siempre abajo, para guardar y salir manualmente. */
    private JPanel crearBarraInferior() {
        JPanel panel = new JPanel();
        JButton btnGuardarSalir = new JButton("Guardar y Salir");
        btnGuardarSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarYSalir();
            }
        });
        panel.add(btnGuardarSalir);
        return panel;
    }

    private void guardarYSalir() {
        PersistenciaCSV.guardar(sistema); // SIA-11: sistema batch, se graba al salir
        JOptionPane.showMessageDialog(this, "Datos guardados correctamente.");
        dispose();
        System.exit(0);
    }

    // Método de apoyo para no repetir la misma configuración de JTextArea
    // (letra monoespaciada, no editable) en cada pestaña.
    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    // ==========================================================
    //  PESTAÑA 1: ASIGNATURAS (Colección 1 - HashMap) - SIA-7/SIA-8
    // ==========================================================
    private JPanel crearPanelAsignaturas() {
        JPanel panel = new JPanel(new BorderLayout());
        areaAsignaturas = crearAreaTexto();
        panel.add(new JScrollPane(areaAsignaturas), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 5, 5, 5));
        JButton btnListar = new JButton("Listar");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnListar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { actualizarAsignaturas(); }
        });

        btnAgregar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { agregarAsignaturaGUI(); }
        });

        btnBuscar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { buscarAsignaturaGUI(); }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { editarAsignaturaGUI(); }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminarAsignaturaGUI(); }
        });

        botones.add(btnListar);
        botones.add(btnAgregar);
        botones.add(btnBuscar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private void actualizarAsignaturas() {
        StringBuilder texto = new StringBuilder("=== LISTADO DE ASIGNATURAS ===\n\n");
        if (sistema.getMapaAsignaturas().isEmpty()) {
            texto.append("No hay asignaturas registradas.\n");
        } else {
            for (Asignatura a : sistema.getMapaAsignaturas().values()) {
                texto.append(" * ").append(a).append("\n");
            }
        }
        areaAsignaturas.setText(texto.toString());
    }

    private void agregarAsignaturaGUI() {
        String cod = pedirTexto("Código (ej. MAT-101):");
        if (cod == null) return;
        String nom = pedirTexto("Nombre:");
        if (nom == null) return;
        String letraTxt = pedirTexto("Letra (A/B/C):");
        if (letraTxt == null || letraTxt.isEmpty()) return;
        Integer curso = pedirEntero("Nivel de curso (1 a 4):");
        if (curso == null) return;
        String ciclo = pedirTexto("Ciclo (Basica/Media):");
        if (ciclo == null) return;
        String nomDoc = pedirTexto("Nombre del docente:");
        if (nomDoc == null) return;
        String rutDoc = pedirTexto("RUT del docente:");
        if (rutDoc == null) return;
        String espDoc = pedirTexto("Especialidad del docente:");
        if (espDoc == null) return;

        Profesor prof = new Profesor(nomDoc, rutDoc, espDoc);
        Asignatura asig = new Asignatura(cod, nom, letraTxt.toUpperCase().charAt(0), curso, ciclo, prof);
        sistema.agregarAsignatura(asig);
        actualizarAsignaturas();
        JOptionPane.showMessageDialog(this, "Asignatura agregada correctamente.");
    }

    private void buscarAsignaturaGUI() {
        String cod = pedirTexto("Código a buscar:");
        if (cod == null) return;
        Asignatura a = sistema.buscarAsignatura(cod);
        if (a != null) {
            areaAsignaturas.setText("=== RESULTADO DE BÚSQUEDA ===\n\n" + a);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró la asignatura.");
        }
    }

    private void editarAsignaturaGUI() {
        String cod = pedirTexto("Código de la asignatura a modificar:");
        if (cod == null) return;
        if (sistema.buscarAsignatura(cod) == null) {
            JOptionPane.showMessageDialog(this, "No existe esa asignatura.");
            return;
        }
        String nNom = pedirTexto("Nuevo nombre:");
        if (nNom == null) return;
        String nLetraTxt = pedirTexto("Nueva letra:");
        if (nLetraTxt == null || nLetraTxt.isEmpty()) return;
        Integer nCurso = pedirEntero("Nuevo curso:");
        if (nCurso == null) return;
        String nCiclo = pedirTexto("Nuevo ciclo:");
        if (nCiclo == null) return;

        sistema.editarAsignatura(cod, nNom, nLetraTxt.toUpperCase().charAt(0), nCurso, nCiclo);
        actualizarAsignaturas();
        JOptionPane.showMessageDialog(this, "Asignatura editada correctamente.");
    }

    private void eliminarAsignaturaGUI() {
        String cod = pedirTexto("Código de asignatura a eliminar:");
        if (cod == null) return;
        if (sistema.eliminarAsignatura(cod)) {
            actualizarAsignaturas();
            JOptionPane.showMessageDialog(this, "Asignatura eliminada.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
        }
    }

    // ==========================================================
    //  PESTAÑA 2: RECURSOS DIGITALES (Colección 2 anidada) - SIA-7/SIA-8
    // ==========================================================
    private JPanel crearPanelRecursos() {
        JPanel panel = new JPanel(new BorderLayout());
        areaRecursos = crearAreaTexto();
        panel.add(new JScrollPane(areaRecursos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 5, 5, 5));
        JButton btnListar = new JButton("Listar");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnBuscar = new JButton("Buscar (Sobrecarga)");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnListar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { listarRecursosGUI(); }
        });
        btnAgregar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { agregarRecursoGUI(); }
        });
        btnBuscar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { buscarRecursoGUI(); }
        });
        btnEditar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { editarRecursoGUI(); }
        });
        btnEliminar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminarRecursoGUI(); }
        });

        botones.add(btnListar);
        botones.add(btnAgregar);
        botones.add(btnBuscar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    /** Pide el código de asignatura y valida que exista, reutilizado por los 5 botones de la pestaña Recursos. */
    private Asignatura pedirAsignaturaValida() {
        String cod = pedirTexto("Código de la asignatura:");
        if (cod == null) return null;
        Asignatura asig = sistema.buscarAsignatura(cod);
        if (asig == null) {
            JOptionPane.showMessageDialog(this, "Asignatura no encontrada.");
        }
        return asig;
    }

    private void listarRecursosGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;
        StringBuilder texto = new StringBuilder("=== RECURSOS DE " + asig.getNombre() + " ===\n\n");
        if (asig.getListaRecursos().isEmpty()) {
            texto.append("Sin recursos registrados.\n");
        }
        for (RecursoDigital r : asig.getListaRecursos()) {
            // SIA-6: cada recurso muestra SU PROPIA ficha técnica gracias
            // al polimorfismo (mismo mecanismo que en la consola).
            texto.append(" [ID:").append(r.getNumeroMaterial()).append("] ")
                 .append(r.obtenerFichaTecnica())
                 .append(" | Tiempo estimado: ").append(r.estimarTiempoConsumoMinutos()).append(" min\n");
        }
        areaRecursos.setText(texto.toString());
    }

    private void agregarRecursoGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;

        String[] tipos = {"Video MP4", "Documento PDF/Guía", "Enlace Web"};
        String tipo = (String) JOptionPane.showInputDialog(this, "Tipo de recurso:", "Agregar Recurso",
                JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
        if (tipo == null) return;

        Integer id = pedirEntero("ID / Número de material:");
        if (id == null) return;
        String tit = pedirTexto("Título:");
        if (tit == null) return;
        String url = pedirTexto("URL:");
        if (url == null) return;

        RecursoDigital nuevo = null;
        if (tipo.equals(tipos[0])) {
            Integer min = pedirEntero("Duración en minutos:");
            if (min == null) return;
            String cal = pedirTexto("Resolución (1080p, 720p):");
            if (cal == null) return;
            nuevo = new RecursoVideo(id, tit, url, min, cal);
        } else if (tipo.equals(tipos[1])) {
            Integer pags = pedirEntero("Cantidad de páginas:");
            if (pags == null) return;
            boolean editable = JOptionPane.showConfirmDialog(this, "¿Es editable?", "Documento",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            nuevo = new RecursoDocumento(id, tit, "PDF", url, pags, editable);
        } else {
            boolean externo = JOptionPane.showConfirmDialog(this, "¿Requiere conexión externa?", "Enlace Web",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            nuevo = new RecursoEnlaceWeb(id, tit, url, externo);
        }

        // SIA-12: mismo manejo de excepción propia que en la consola.
        try {
            asig.agregarRecurso(nuevo);
            listarRecursosGUI();
            JOptionPane.showMessageDialog(this, "Recurso agregado exitosamente.");
        } catch (RecursoDuplicadoException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo agregar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarRecursoGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;

        String[] modos = {"Por ID", "Por Título (Sobrecarga SIA-5)"};
        String modo = (String) JOptionPane.showInputDialog(this, "Buscar:", "Buscar Recurso",
                JOptionPane.QUESTION_MESSAGE, null, modos, modos[0]);
        if (modo == null) return;

        RecursoDigital r;
        if (modo.equals(modos[0])) {
            Integer id = pedirEntero("ID:");
            if (id == null) return;
            r = asig.buscarRecurso(id); // sobrecarga con int
        } else {
            String tit = pedirTexto("Título exacto:");
            if (tit == null) return;
            r = asig.buscarRecurso(tit); // sobrecarga con String
        }

        if (r != null) {
            areaRecursos.setText("=== RECURSO ENCONTRADO ===\n\n" + r.obtenerFichaTecnica()
                    + "\nURL: " + r.getUrl()
                    + "\nTiempo estimado: " + r.estimarTiempoConsumoMinutos() + " min");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el recurso.");
        }
    }

    private void editarRecursoGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;
        Integer id = pedirEntero("ID del recurso a editar:");
        if (id == null) return;
        if (asig.buscarRecurso(id) == null) {
            JOptionPane.showMessageDialog(this, "Recurso no encontrado.");
            return;
        }
        String nTit = pedirTexto("Nuevo título:");
        if (nTit == null) return;
        String nForm = pedirTexto("Nuevo formato:");
        if (nForm == null) return;
        String nUrl = pedirTexto("Nueva URL:");
        if (nUrl == null) return;

        asig.editarRecurso(id, nTit, nForm, nUrl);
        listarRecursosGUI();
        JOptionPane.showMessageDialog(this, "Recurso actualizado.");
    }

    private void eliminarRecursoGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;
        Integer id = pedirEntero("ID del recurso a eliminar:");
        if (id == null) return;
        if (asig.eliminarRecurso(id)) {
            listarRecursosGUI();
            JOptionPane.showMessageDialog(this, "Recurso eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
        }
    }

    // ==========================================================
    //  PESTAÑA 3: ALUMNOS
    // ==========================================================
    private JPanel crearPanelAlumnos() {
        JPanel panel = new JPanel(new BorderLayout());
        areaAlumnos = crearAreaTexto();
        panel.add(new JScrollPane(areaAlumnos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnListar = new JButton("Listar");
        JButton btnInscribir = new JButton("Inscribir");
        JButton btnEliminar = new JButton("Eliminar");

        btnListar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { listarAlumnosGUI(); }
        });
        btnInscribir.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { inscribirAlumnoGUI(); }
        });
        btnEliminar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminarAlumnoGUI(); }
        });

        botones.add(btnListar);
        botones.add(btnInscribir);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private void listarAlumnosGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;
        StringBuilder texto = new StringBuilder("=== ALUMNOS DE " + asig.getNombre() + " ===\n\n");
        if (asig.getListaAlumnos().isEmpty()) {
            texto.append("Sin alumnos inscritos.\n");
        }
        for (Alumno a : asig.getListaAlumnos()) {
            texto.append(" * ").append(a).append("\n");
        }
        areaAlumnos.setText(texto.toString());
    }

    private void inscribirAlumnoGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;
        String rut = pedirTexto("RUT del alumno:");
        if (rut == null) return;

        // MEJORA: igual que en la consola, se busca primero en TODO el
        // sistema para no duplicar a la misma persona en dos objetos
        // distintos si ya estaba inscrita en otra asignatura.
        Alumno existente = sistema.buscarAlumnoGlobal(rut);
        if (existente != null) {
            asig.agregarAlumno(existente);
            listarAlumnosGUI();
            JOptionPane.showMessageDialog(this, existente.getNombre() + " fue inscrito también en esta asignatura.");
            return;
        }

        String nom = pedirTexto("Nombre completo:");
        if (nom == null) return;
        Integer curso = pedirEntero("Curso:");
        if (curso == null) return;
        String letraTxt = pedirTexto("Letra:");
        if (letraTxt == null || letraTxt.isEmpty()) return;
        String ciclo = pedirTexto("Ciclo:");
        if (ciclo == null) return;

        asig.agregarAlumno(new Alumno(nom, rut, letraTxt.toUpperCase().charAt(0), ciclo, curso));
        listarAlumnosGUI();
        JOptionPane.showMessageDialog(this, "Alumno inscrito en la asignatura.");
    }

    private void eliminarAlumnoGUI() {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return;
        String rut = pedirTexto("RUT del alumno a desvincular:");
        if (rut == null) return;
        if (asig.eliminarAlumno(rut)) {
            listarAlumnosGUI();
            JOptionPane.showMessageDialog(this, "Alumno desvinculado con éxito.");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró el RUT.");
        }
    }

    // ==========================================================
    //  PESTAÑA 4: BOLETÍN ACADÉMICO (SIA-9, funcionalidad estrella)
    // ==========================================================
    private JPanel crearPanelBoletin() {
        JPanel panel = new JPanel(new BorderLayout());
        areaBoletin = crearAreaTexto();
        panel.add(new JScrollPane(areaBoletin), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(2, 3, 5, 5));
        JButton btnRegistrar = new JButton("Registrar Nota");
        JButton btnEditar = new JButton("Editar Nota");
        JButton btnEliminar = new JButton("Eliminar Nota");
        JButton btnVerBoletin = new JButton("Ver Boletín");
        JButton btnReporte = new JButton("Reporte: Alumnos en Riesgo");

        btnRegistrar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { registrarNotaGUI(); }
        });
        btnEditar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { editarNotaGUI(); }
        });
        btnEliminar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { eliminarNotaGUI(); }
        });
        btnVerBoletin.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { verBoletinGUI(); }
        });
        btnReporte.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { reporteRiesgoGUI(); }
        });

        botones.add(btnRegistrar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnVerBoletin);
        botones.add(btnReporte);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    /** Pide asignatura + alumno, validando ambos; reutilizado por los 4 primeros botones del boletín. */
    private Alumno pedirAlumnoDeAsignatura(Asignatura[] asigEncontrada) {
        Asignatura asig = pedirAsignaturaValida();
        if (asig == null) return null;
        String rut = pedirTexto("RUT del alumno:");
        if (rut == null) return null;
        Alumno alumno = asig.buscarAlumno(rut);
        if (alumno == null) {
            JOptionPane.showMessageDialog(this, "El alumno no está inscrito en esa asignatura.");
            return null;
        }
        asigEncontrada[0] = asig;
        return alumno;
    }

    private void registrarNotaGUI() {
        Asignatura[] asigEncontrada = new Asignatura[1];
        Alumno alumno = pedirAlumnoDeAsignatura(asigEncontrada);
        if (alumno == null) return;

        Double nota = pedirDouble("Nota a registrar (1.0 a 7.0):");
        if (nota == null) return;

        // SIA-12: try-catch obligatorio para la excepción propia.
        try {
            alumno.agregarNota(asigEncontrada[0].getCodigo(), nota);
            JOptionPane.showMessageDialog(this, "Nota registrada correctamente.");
        } catch (NotaInvalidaException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarNotaGUI() {
        Asignatura[] asigEncontrada = new Asignatura[1];
        Alumno alumno = pedirAlumnoDeAsignatura(asigEncontrada);
        if (alumno == null) return;

        ArrayList<Double> notas = alumno.obtenerNotas(asigEncontrada[0].getCodigo());
        if (notas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este alumno no tiene notas registradas en esta asignatura.");
            return;
        }
        Integer idx = pedirEntero("Notas actuales: " + notas + "\nÍndice de la nota a editar (0, 1, 2...):");
        if (idx == null) return;
        Double nueva = pedirDouble("Nueva nota:");
        if (nueva == null) return;

        try {
            if (alumno.editarNota(asigEncontrada[0].getCodigo(), idx, nueva)) {
                JOptionPane.showMessageDialog(this, "Nota actualizada.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró esa nota.");
            }
        } catch (NotaInvalidaException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo editar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarNotaGUI() {
        Asignatura[] asigEncontrada = new Asignatura[1];
        Alumno alumno = pedirAlumnoDeAsignatura(asigEncontrada);
        if (alumno == null) return;

        ArrayList<Double> notas = alumno.obtenerNotas(asigEncontrada[0].getCodigo());
        if (notas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este alumno no tiene notas registradas en esta asignatura.");
            return;
        }
        Integer idx = pedirEntero("Notas actuales: " + notas + "\nÍndice de la nota a eliminar (0, 1, 2...):");
        if (idx == null) return;

        if (alumno.eliminarNota(asigEncontrada[0].getCodigo(), idx)) {
            JOptionPane.showMessageDialog(this, "Nota eliminada.");
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró esa nota.");
        }
    }

    private void verBoletinGUI() {
        Asignatura[] asigEncontrada = new Asignatura[1];
        Alumno alumno = pedirAlumnoDeAsignatura(asigEncontrada);
        if (alumno == null) return;
        String cod = asigEncontrada[0].getCodigo();

        ArrayList<Double> notas = alumno.obtenerNotas(cod);
        if (notas.isEmpty()) {
            areaBoletin.setText("El alumno aún no tiene notas registradas en esta asignatura.");
            return;
        }

        double promedio = alumno.calcularPromedio(cod); // SIA-5: sobrecarga con String
        StringBuilder texto = new StringBuilder();
        texto.append("===== BOLETÍN DE ").append(alumno.getNombre()).append(" (").append(cod).append(") =====\n\n");
        texto.append("Notas registradas: ").append(notas).append("\n");
        texto.append(String.format("Promedio actual: %.2f%n", promedio));
        texto.append("Avance hacia la aprobación (4.0): ").append(generarBarraProgreso(promedio)).append("\n");

        if (promedio >= Alumno.NOTA_APROBACION) {
            texto.append("\nEstado: APROBANDO la asignatura.");
        } else {
            Integer restantes = pedirEntero("El alumno va bajo el 4.0.\n¿Cuántas evaluaciones le quedan en el semestre?");
            if (restantes != null && restantes > 0) {
                double necesaria = alumno.calcularNotaNecesaria(cod, restantes);
                if (necesaria > Alumno.NOTA_MAXIMA) {
                    texto.append("\nCon las evaluaciones que quedan ya no es matemáticamente posible ")
                         .append("llegar al 4.0. Se recomienda reforzamiento o evaluación diferenciada.");
                } else {
                    texto.append(String.format("%nNecesita un promedio de %.2f en las evaluaciones restantes para aprobar.", necesaria));
                }
            }
        }
        areaBoletin.setText(texto.toString());
    }

    private void reporteRiesgoGUI() {
        ArrayList<Alumno> enRiesgo = sistema.listarAlumnosEnRiesgo(); // SIA-9: reporte filtrado
        StringBuilder texto = new StringBuilder("=== ALUMNOS EN RIESGO (promedio general < "
                + Alumno.NOTA_APROBACION + ") ===\n\n");
        if (enRiesgo.isEmpty()) {
            texto.append("No hay alumnos en riesgo de reprobar por el momento.");
        } else {
            for (Alumno a : enRiesgo) {
                texto.append(String.format(" * %s -> Promedio general: %.1f%n", a.getNombre(), a.calcularPromedio()));
            }
        }
        areaBoletin.setText(texto.toString());
    }

    /** Misma barra de progreso ASCII usada en Main.java (consola), reutilizada aquí. */
    private String generarBarraProgreso(double promedio) {
        int total = 20;
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
    //  MÉTODOS DE APOYO PARA PEDIR DATOS CON JOptionPane
    // ==========================================================

    /** Pide un texto simple. Devuelve null si el usuario presiona "Cancelar". */
    private String pedirTexto(String mensaje) {
        String resultado = JOptionPane.showInputDialog(this, mensaje);
        return (resultado == null) ? null : resultado.trim();
    }

    /**
     * Pide un número entero, repitiendo la pregunta si el usuario escribe
     * algo que no es un número (SIA-12: manejo de NumberFormatException
     * con try-catch, igual que en la consola).
     */
    private Integer pedirEntero(String mensaje) {
        while (true) {
            String texto = pedirTexto(mensaje);
            if (texto == null) return null; // el usuario canceló
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un número entero válido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Igual que pedirEntero(), pero para números decimales (usado en las notas). */
    private Double pedirDouble(String mensaje) {
        while (true) {
            String texto = pedirTexto(mensaje);
            if (texto == null) return null;
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un número válido (ej. 5.5).",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
