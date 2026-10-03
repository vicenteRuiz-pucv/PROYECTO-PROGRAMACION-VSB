Proyecto SIA — Sistema de Gestión de Recursos Educativos Digitales.

Autor: Benjamín Alucema, Sebastian Pino, Vicente Ruiz.

Fecha: 02-10-2026.

---

RECORDAR: el programa arranca siempre con datos de ejemplo precargados (asignaturas MAT-101 y CIE-201), así que se puede probar cualquier funcionalidad apenas se ejecuta, sin cargar nada a mano. Si ya existe una carpeta `datos_sia/` de una sesión anterior, esos datos de ejemplo se reemplazan por lo último que se guardó (ver sección de Persistencia).

---

# SIA  

SIA es el motor invisible detrás de una plataforma tipo "aula virtual" de colegio: no tiene botones bonitos ni una base de datos real detrás, es el conjunto de clases en **Java** que decide cómo se guardan y se relacionan asignaturas, profesores, alumnos y los materiales digitales que suben. Toda la información vive en memoria mientras el programa corre; al cerrar (o al apretar "Guardar y Salir") se vuelca a disco en CSV y se recupera la próxima vez que se abre.

El programa se puede usar de **dos formas completamente equivalentes**: por consola (con menús numerados y `Scanner`) o por una ventana Swing con pestañas. Las dos llaman exactamente a los mismos métodos de `Sistema`, `Asignatura` y `Alumno`, así que ninguna funcionalidad quedó exclusiva de un modo u otro.

---

## Estructura del proyecto y paquetes

```
PROYECTO-PROGRAMACION-VSB/
 ├── src/
 │    └── sia/
 │         ├── sia.java                  -> clase principal (main), menús de consola
 │         ├── Sistema.java               -> controlador general (HashMap de asignaturas)
 │         ├── Asignatura.java             -> "carpeta" de un curso (2 listas anidadas)
 │         ├── Alumno.java
 │         ├── Profesor.java
 │         ├── RecursoDigital.java         -> clase abstracta, padre de los 3 recursos
 │         ├── RecursoVideo.java
 │         ├── RecursoDocumento.java
 │         ├── RecursoEnlaceWeb.java
 │         ├── excepciones/
 │         │    ├── RecursoDuplicadoException.java
 │         │    └── NotaInvalidaException.java
 │         ├── gui/
 │         │    └── VentanaPrincipal.java  -> interfaz gráfica Swing (SIA-10)
 │         ├── persistencia/
 │         │    └── PersistenciaCSV.java   -> guardado/carga en archivos CSV (SIA-11)
 │         └── exportacion/
 │              └── ExportadorPlanilla.java -> boletín en planilla Excel .xlsx (SIA-O2)
 ├── lib/            -> .jar de Apache POI 5.2.3 y sus dependencias (necesarios para la planilla)
 ├── datos_sia/      -> se crea al ejecutar: CSV de persistencia + boletin_notas.xlsx
 ├── docs/           -> documentación Javadoc (abrir docs/index.html)
 ├── nbproject/, build.xml, manifest.mf -> archivos del proyecto NetBeans
 └── .gitignore
```

**Colecciones usadas (SIA-4):** un `HashMap<String, Asignatura>` en `Sistema` (llave = código del curso, ej. `"MAT-101"`), y dentro de cada `Asignatura` dos `ArrayList` anidados: uno de `Alumno` y otro de `RecursoDigital`. Es la "colección dentro de la colección" que pide la pauta: el diccionario grande contiene asignaturas, y cada asignatura contiene sus propias listas.

---

## Instrucciones de compilación y ejecución

### Requisitos

- **JDK 8 u 11** (el código no usa nada más nuevo: sin `switch` con flecha, sin `var`, sin lambdas).
- Las librerías **Apache POI** (`.jar`) dentro de la carpeta `lib/` (el proyecto ya las trae, versión 5.2.3). Solo se usan para la planilla Excel (SIA-O2); el resto del programa no las necesita, pero como `ExportadorPlanilla` forma parte del proyecto, hay que incluirlas al compilar.
- Para NetBeans, ver la sección siguiente.

### Compilar y ejecutar en NetBeans (recomendado)

1. **Abrir el proyecto:** *File → Open Project…* y elegir la carpeta del proyecto (la que contiene `src/`, `lib/` y `nbproject/`). NetBeans la reconoce como proyecto.
2. **Revisar el JDK:** clic derecho en el proyecto → *Properties → Libraries* → *Java Platform* debe ser JDK 8 u 11. En *Sources* dejar la codificación en **UTF-8** (para que se vean bien las tildes).
3. **Revisar las librerías:** en *Properties → Libraries → Compile* deben aparecer los `.jar` de la carpeta `lib/` (Apache POI y sus dependencias). Si falta alguno, usar *Add JAR/Folder* y seleccionarlos desde `lib/`.
4. **Clase principal:** en *Properties → Run* el *Main Class* debe ser `sia.sia`. Si al ejecutar aparece `no se ha encontrado o cargado la clase principal proyectojavavsb.ProyectoJavaVSB`, es que quedó el nombre antiguo: en *Properties → Run → Main Class* elegir `sia.sia` (equivale a la línea `main.class=sia.sia` de `nbproject/project.properties`).
5. **Compilar:** *Run → Clean and Build Project* (Mayús+F11).
6. **Ejecutar:** *Run → Run Project* (F6). El menú de consola aparece en la ventana *Output* de NetBeans; si se elige la opción `2`, se abre la ventana gráfica.
7. **Javadoc (SIA-O3):** *Run → Generate Javadoc*. NetBeans lo deja en su propia carpeta de salida; la versión entregada está en `docs/index.html`.

Si aparece una advertencia en la línea `package sia;`, es solo una advertencia y no impide compilar ni ejecutar.

### Compilar por línea de comandos (alternativa)

Desde la carpeta raíz del proyecto (la que contiene `src/` y `lib/`):

```bash
mkdir bin
javac -encoding UTF-8 -cp "lib/*" -d bin src/sia/*.java src/sia/excepciones/*.java src/sia/gui/*.java src/sia/persistencia/*.java src/sia/exportacion/*.java
```

### Ejecutar por línea de comandos

La clase principal es `sia.sia` (paquete `sia`, clase `sia`):

```bash
# Windows
java -cp "bin;lib/*" sia.sia

# Linux / macOS
java -cp "bin:lib/*" sia.sia
```

Al iniciar, el programa pregunta lo primero de todo si se quiere modo consola o modo ventana:

```
=== SIA - Selección de modo de uso ===
1. Consola
2. Ventana (interfaz gráfica)
Seleccione una opción: 1
```

Si eligen `2`, se abre `VentanaPrincipal` (Swing) con las mismas 4 áreas de trabajo que la consola. Si ya existe una carpeta `datos_sia/` de una ejecución anterior en el mismo directorio, antes de preguntar el modo el programa avisa: `Datos cargados desde la ultima sesión guardada`

---

## Las clases del sistema, por encima

| Clase | Qué guarda / qué hace |
|---|---|
| `RecursoDigital` (abstracta) | ID, título, formato y URL de cualquier material. Nadie puede instanciarla directo — obliga a cada hija a definir su propia ficha técnica y su propio tiempo de consumo. |
| `RecursoVideo` | Agrega duración en minutos y calidad (720p/1080p). El tiempo de consumo es, literalmente, su duración. |
| `RecursoDocumento` | Agrega páginas y si es editable o de solo lectura. El tiempo de consumo se calcula a 2 minutos por página, redondeando siempre hacia arriba. |
| `RecursoEnlaceWeb` | Representa simuladores o páginas externas; indica si hay que salir del sitio del colegio. Tiempo de consumo fijo (5 min de exploración). |
| `Alumno` | Nombre, RUT, curso, letra, ciclo. Guarda solo los **códigos** de sus asignaturas (no el objeto completo) y un `HashMap<String, List<Double>>` con sus notas por curso. |
| `Profesor` | Nombre, RUT, profesión, y (igual que el Alumno) solo los códigos de las asignaturas que dicta. |
| `Asignatura` | Código, nombre, letra, curso, ciclo, su `Profesor`, y las dos listas anidadas (alumnos y recursos). Ahí viven los CRUD de ambas colecciones. |
| `Sistema` | El `Map` de asignaturas y las operaciones que cruzan varios cursos: buscar un alumno en todo el colegio, o generar el reporte de alumnos en riesgo. |
| `sia` | Clase principal (`main`). Dibuja los menús (consola) o levanta la ventana, y traduce lo que el usuario escribe en llamadas a `Sistema`/`Asignatura`/`Alumno`. No tiene lógica de negocio propia. |
| `ExportadorPlanilla` | Clase de utilidades (solo métodos estáticos). Recorre el `Sistema` y escribe el boletín de cada alumno en un archivo Excel (SIA-O2). |

---

## Las 4 relaciones UML del proyecto

| Relación | Entre | La pregunta que responde | Cómo se programó |
|---|---|---|---|
| **Agregación** (◇) | `Sistema` — `Asignatura` | ¿El todo sobrevive sin la parte? Sí: una asignatura sigue teniendo sentido aunque el Sistema se apague. | `HashMap<String, Asignatura>` |
| **Composición** (♦) | `Asignatura` — `RecursoDigital` | ¿La parte sobrevive sin el todo? No: una guía de Matemática no debería flotar sin el curso de Matemática. | `ArrayList<RecursoDigital>` dentro de `Asignatura` |
| **Asociación optimizada** | `Asignatura` — `Alumno`/`Profesor` | ¿Cómo se relacionan sin generar un ciclo infinito al imprimir? | La Asignatura guarda el objeto `Alumno`/`Profesor` completo; el Alumno/Profesor solo guarda el **código de texto** de la asignatura. |
| **Herencia** (◁) | `RecursoDigital` — sus 3 hijas | ¿Es un tipo de...? Un `RecursoVideo` ES-UN `RecursoDigital`. | `extends RecursoDigital` |

La asociación optimizada es el punto más delicado del diseño: si tanto la Asignatura como el Alumno guardaran el objeto completo del otro, recorrer o imprimir esos datos terminaría en un ciclo "la asignatura conoce al alumno, que conoce a la asignatura, que conoce al alumno..." — un `StackOverflowError` real. Rompiendo la relación en un solo sentido se evita el ciclo sin perder la información (siempre se puede volver a buscar la asignatura por su código en el `HashMap` de `Sistema`).

---

## Funcionalidades principales

### 1 · Gestión de Asignaturas (Colección 1 — el HashMap)

Listar, agregar, buscar por código, editar y eliminar asignaturas. Al agregar una asignatura nueva, el menú también pide los datos del profesor y crea el `Profesor` en el mismo paso.

```
--- GESTIÓN DE ASIGNATURAS ---
1. Listar Asignaturas
2. Agregar Asignatura
3. Buscar Asignatura por Código
4. Editar Asignatura
5. Eliminar Asignatura
Opción: 1

============== LISTADO GENERAL DE ASIGNATURAS ==============
 * [MAT-101] Matemática (1°A Media) | Docente: Roberto Morales (RUT: 11.222.333-4, Profesor de Matemáticas) | Alumnos: 2 | Recursos: 3
 * [CIE-201] Física (2°B Media) | Docente: Carla Fuentes (RUT: 15.666.777-8, Profesora de Ciencias Naturales) | Alumnos: 1 | Recursos: 2
=============================================================
```

### 2 · Gestión de Recursos Digitales (Colección 2, anidada)

Cada operación pide primero el código de la asignatura (búsqueda por `HashMap`) y luego trabaja sobre su `ArrayList<RecursoDigital>`. Al agregar, el menú pregunta el tipo (Video / Documento / Enlace) y arma el objeto de la subclase correspondiente, aunque después se guarda simplemente como `RecursoDigital` gracias a la herencia.

```
--- RECURSOS DE Matemática ---
1. Listar Recursos
2. Agregar Recurso
3. Buscar Recurso por ID o Título (Sobrecarga)
4. Editar Recurso
5. Eliminar Recurso
Opción: 1

  --- RECURSOS DE Matemática (MAT-101) ---
   [ID:1] DOCUMENTO -> Guía de Funciones Cuadráticas | Páginas: 12 | Tipo: Documento Solo Lectura/PDF
   [ID:2] VIDEO -> Clase Grabada: Parábolas y Vértice | Duración: 35 min | Calidad: 1080p
   [ID:3] ENLACE WEB -> Simulador GeoGebra | Acceso: requiere sitio externo
```

**Buscar Recurso** demuestra la sobrecarga (SIA-5) en vivo: si el usuario elige "1. Por ID" se llama a `asig.buscarRecurso(id)`; si elige "2. Por Título" se llama a `asig.buscarRecurso(titulo)`. Java decide sola cuál de las dos versiones ejecutar según el tipo de dato que reciba.

Si se intenta agregar un recurso con un ID que ya existe en la asignatura, no se agrega en silencio: se lanza `RecursoDuplicadoException` y el menú avisa el problema:

```
No se pudo agregar: Ya existe un recurso con el ID 2 en la asignatura Matemática.
```

### 3 · Gestión de Alumnos

Listar, inscribir y desvincular alumnos de una asignatura puntual. Lo interesante ocurre al inscribir: antes de crear un `Alumno` nuevo, el sistema busca ese RUT **en todo el colegio** (no solo en el curso actual) con `buscarAlumnoGlobal()`. Si la persona ya existía en otra asignatura, se reutiliza el mismo objeto en vez de duplicarla.

```
--- ALUMNOS DE Física ---
1. Listar Alumnos
2. Inscribir Alumno
3. Eliminar Alumno
Opción: 2
RUT Alumno: 20.123.456-7
Benjamin Alucema fue inscrito también en esta asignatura.
```

### 4 · Boletín Académico — la funcionalidad estrella (SIA-9)

Es el módulo con más peso propio del proyecto. Permite registrar, editar y eliminar notas (escala chilena 1.0 a 7.0) de un alumno en una asignatura, y arma un boletín con promedio, una barra de progreso hacia el 4.0, y la nota que le falta para aprobar.

```
--- BOLETÍN ACADÉMICO ---
1. Registrar nota a un alumno
2. Editar una nota existente
3. Eliminar una nota
4. Ver boletín de un alumno en una asignatura
5. Reporte: alumnos en riesgo de reprobar (todo el sistema)
Opción: 4
Código de la asignatura: MAT-101
RUT del alumno: 21.987.654-3

===== BOLETÍN DE Sofia Contreras (MAT-101) =====
Notas registradas: [3.2, 3.5]
Promedio actual: 3.35
Avance hacia la aprobación (4.0): [█████████░░░░░░░░░░░] 48%
¿Cuántas evaluaciones le quedan en el semestre? 2
Necesita un promedio de 4.65 en las evaluaciones restantes para aprobar.
=========================================================
```

Si la nota necesaria resulta matemáticamente imposible (más de 7.0), el sistema no inventa un número: avisa directamente que ya no se puede llegar al 4.0 solo con lo que queda del semestre y sugiere reforzamiento.

La opción 5 es el reporte filtrado que exige la pauta: recorre **todas** las asignaturas del sistema y junta, sin repetir a nadie, a los alumnos con promedio general bajo 4.0.

```
--- ALUMNOS EN RIESGO (promedio general < 4.0) ---
  * Sofia Contreras -> Promedio general: 3.4
```

**Otras ideas que quedaron con la base ya preparada** (reutilizando las mismas colecciones): un generador de playlist de estudio a partir de `filtrarVideos()`, que ya recorre la lista de recursos con `instanceof` y devuelve solo los `RecursoVideo`; un ranking de mejores promedios por asignatura; y una alerta de recursos "pesados" usando `estimarTiempoConsumoMinutos()`.

### 5 · Demostración explícita de SIA-5 y SIA-6

Es un menú aparte (opción 5 del menú principal) pensado para la defensa oral: en un solo lugar, muestra la sobrecarga de `mostrarDetalle()` / `mostrarDetalle(true)` en `RecursoDigital`, la sobrecarga de `calcularPromedio()` / `calcularPromedio(String)` en `Alumno`, y el polimorfismo recorriendo la lista de recursos de MAT-101 sin preguntarle a ninguno "¿y tú qué tipo eres?".

```
1. SOBRECARGA DE MÉTODOS (SIA-5) en RecursoDigital:
Llamada a mostrarDetalle() [resumido]:
   [ID:1] DOCUMENTO -> Guía de Funciones Cuadráticas | Páginas: 12 | Tipo: Documento Solo Lectura/PDF
Llamada a mostrarDetalle(true) [con URL y tiempo estimado]:
   [ID:1] DOCUMENTO -> Guía de Funciones Cuadráticas | Páginas: 12 | Tipo: Documento Solo Lectura/PDF
       URL: https://colegio.cl/mat/guia1.pdf | Tiempo estimado de uso: 24 min.

1b. SOBRECARGA DE MÉTODOS (SIA-5) en Alumno:
calcularPromedio("MAT-101") -> 4.65
calcularPromedio() [general, todas las asignaturas] -> 4.65

2. SOBREESCRITURA Y POLIMORFISMO (SIA-6):
Ficha -> DOCUMENTO -> Guía de Funciones Cuadráticas | Páginas: 12 | Tipo: Documento Solo Lectura/PDF | Tiempo estimado: 24 min
Ficha -> VIDEO -> Clase Grabada: Parábolas y Vértice | Duración: 35 min | Calidad: 1080p | Tiempo estimado: 35 min
Ficha -> ENLACE WEB -> Simulador GeoGebra | Acceso: requiere sitio externo | Tiempo estimado: 5 min
```

---

## Excepciones propias (SIA-12)

Se crearon dos excepciones *checked* (extienden `Exception`, no `RuntimeException`), lo que obliga a que quien las pueda lanzar las declare con `throws` y a que quien las llame las envuelva en try-catch:

- **`RecursoDuplicadoException`**: se lanza en `Asignatura.agregarRecurso()` cuando el ID (`numeroMaterial`) ya existe en esa asignatura. Antes este caso se ignoraba en silencio; ahora el usuario recibe un mensaje claro.
- **`NotaInvalidaException`**: se lanza en `Alumno.agregarNota()` y `Alumno.editarNota()` cuando la nota está fuera de la escala 1.0–7.0, protegiendo los cálculos de promedio del Boletín Académico.

Además, cada punto del menú donde el usuario escribe un número está protegido contra `NumberFormatException`, para que escribir texto donde se espera un número no cierre el programa de golpe (por ejemplo, si alguien presiona Enter sin escribir nada donde se espera una letra de curso, se atrapa también `StringIndexOutOfBoundsException`).

---

## SIA-10 — Consola y Ventana, el mismo sistema por dos caminos

Desde el principio, `sia` (la clase principal) nunca tuvo lógica de negocio propia: solo mostraba texto y llamaba a métodos de `Sistema`, `Asignatura` y `Alumno`. Gracias a eso, se pudo agregar `VentanaPrincipal` sin duplicar ni una sola regla de negocio — la ventana llama exactamente a los mismos métodos que ya usaba la consola.

`VentanaPrincipal` es una ventana Swing con 4 pestañas (Asignaturas, Recursos Digitales, Alumnos, Boletín Académico), cada una con los mismos botones que las opciones de consola: Listar, Agregar, Buscar, Editar, Eliminar (y en el Boletín: Registrar/Editar/Eliminar Nota, Ver Boletín, Reporte de Riesgo). En vez de `Scanner` + `System.out.println`, cada botón pide sus datos con `JOptionPane.showInputDialog(...)` y muestra los resultados en un `JTextArea` grande — el equivalente gráfico exacto de "preguntar por consola e imprimir la respuesta".

Ejemplo concreto de que es el mismo código en ambos modos: **Buscar Recurso** en la ventana pregunta con un cuadro de diálogo "Por ID" o "Por Título" y, según la respuesta, llama a la misma sobrecarga de `buscarRecurso()` que usa la consola. La demostración de SIA-5 funciona igual en los dos modos porque es literalmente el mismo método el que decide qué versión ejecutar.

Los botones usan clases anónimas (`ActionListener`, `WindowAdapter`, `Runnable`) en vez de expresiones lambda, manteniendo la misma decisión de nivel que ya se tomó en el resto del proyecto: nada de herramientas más avanzadas de lo esperado para segundo año.

---

## SIA-11 — Persistencia de datos en CSV (esquema batch)

La clase `sia.persistencia.PersistenciaCSV` es la única encargada de traducir los objetos del sistema hacia archivos de texto y de vuelta. Ninguna clase del dominio (`Sistema`, `Asignatura`, `Alumno`, etc.) tuvo que modificarse para esto: `PersistenciaCSV` solo usa los getters que ya tenían desde el principio.

**"Batch" significa** que la persistencia no toca el disco mientras el programa está en uso (la única escritura durante el uso es la planilla Excel que el usuario pide a propósito, ver SIA-O2): se carga una vez al iniciar y se graba una vez al salir (por consola con la opción "0. Salir", o en la ventana con "Guardar y Salir" / cerrar con la X).

Al guardar se crea una carpeta `datos_sia/` con 5 archivos separados por `;`:

| Archivo | Qué guarda | Ejemplo de fila |
|---|---|---|
| `asignaturas.csv` | Una fila por asignatura, con los datos de su profesor | `MAT-101;Matemática;A;1;Media;Roberto Morales;11.222.333-4;Profesor` |
| `recursos.csv` | Una fila por recurso, con su tipo (VIDEO/DOCUMENTO/ENLACE) y sus columnas propias | `MAT-101;VIDEO;2;Clase Grabada;Video MP4;url;35;1080p` |
| `alumnos.csv` | Una fila por alumno, sin repetir aunque esté en varios cursos | `20.123.456-7;Benjamin Alucema;1;A;Media` |
| `inscripciones.csv` | Relación muchos-a-muchos entre asignaturas y alumnos | `MAT-101;20.123.456-7` |
| `notas.csv` | Una fila por cada nota registrada | `20.123.456-7;MAT-101;5.5` |

**Al cargar**, hay que reconstruir las relaciones en un orden específico: primero las `Asignatura` (con su `Profesor`), luego los `RecursoDigital` (usando el texto "VIDEO"/"DOCUMENTO"/"ENLACE" para crear el objeto de la subclase correcta), luego los `Alumno` sueltos en un `HashMap<String, Alumno>` temporal por RUT, después las inscripciones (reutilizando ese mismo objeto `Alumno` en vez de crear a la persona de nuevo), y por último las notas.

**Limitación conocida:** como el separador es `;`, nombres, títulos o URLs no deberían contener ese carácter. Resolverlo de forma completamente robusta pediría comillas tipo CSV o JSON, algo fuera del alcance esperado para este proyecto.

---

## Encapsulamiento de colecciones (SIA-3)

Ningún getter entrega la colección original de una clase, para que nadie desde afuera pueda agregar o quitar elementos saltándose las validaciones (`RecursoDuplicadoException`, `NotaInvalidaException`, etc.):

| Método | Qué devuelve |
|---|---|
| `Sistema.getMapaAsignaturas()` | `Collections.unmodifiableMap(...)` |
| `Asignatura.getListaAlumnos()` / `getListaRecursos()` | `Collections.unmodifiableList(...)` |
| `Alumno.getCodigosAsignatura()` / `Profesor.getCodigosAsignaturas()` | `Collections.unmodifiableList(...)` |
| `Alumno.obtenerNotas(codigo)` | una **copia** de la lista de notas |
| `Asignatura.filtrarVideos()` / `filtrarDocumentos()`, `Sistema.listarAlumnosEnRiesgo()` | una lista **nueva** armada en el momento |

Tampoco existen setters que permitan reemplazar una colección completa. Para modificarlas hay que usar los métodos propios de la clase (`agregarRecurso`, `agregarNota`, `eliminarAlumno`, etc.), que son los que validan.

---

## SIA-O2 — Generación de planilla de cálculo (Excel)

La clase `sia.exportacion.ExportadorPlanilla` genera el boletín completo del colegio en un archivo **`.xlsx`** usando la librería **Apache POI**.

- **Cómo se usa:** en consola, opción `6. Exportar boletín a Excel` del menú principal; en la ventana, botón `Exportar a Excel` de la barra inferior.
- **Dónde queda:** `datos_sia/boletin_notas.xlsx` (la carpeta se crea sola si no existe). Cada vez que se exporta, el archivo se sobrescribe.
- **Qué contiene:** una hoja llamada `Boletín` con una fila por cada pareja (asignatura, alumno) y las columnas `Asignatura`, `RUT`, `Alumno`, `Cantidad de notas`, `Promedio` y `Estado`. El estado es `Sin notas`, `Aprobado` (promedio ≥ 4.0) o `En riesgo` (promedio < 4.0), con la misma regla del Boletín Académico.
- **Errores:** si no se puede escribir el archivo (por ejemplo, está abierto en Excel), `ExportadorPlanilla` atrapa la `IOException`, avisa y devuelve `false`; el programa no se cae.

---

## SIA-O3 — Documentación con Javadoc

Todas las clases y métodos del proyecto están documentados con comentarios Javadoc (`@param`, `@return`, `@throws`). La documentación ya generada en HTML está en **`docs/index.html`** (abrir con cualquier navegador).

Para regenerarla (por ejemplo, si se cambia el código), desde la carpeta raíz del proyecto:

```bash
javadoc -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -cp "lib/*" -d docs -sourcepath src -subpackages sia
```

En NetBeans también se puede generar con *Run → Generate Javadoc*.

---

## Aviso conocido al exportar la planilla

Al ejecutar la exportación a Excel puede aparecer en la consola este mensaje:

```
ERROR StatusLogger Log4j2 could not find a logging implementation. Please add log4j-core to the classpath. Using SimpleLogger to log to the console...
```

**No afecta en nada:** es solo un aviso de la librería Apache POI (que usa Log4j2 para sus mensajes internos y no encuentra una implementación de logging). La planilla se genera igual y el programa sigue funcionando normal.

**Advertencia en `package sia;`:** el editor puede marcar una advertencia en la línea `package sia;` de las clases. Es solo una advertencia, no un error: no impide compilar, ejecutar ni generar el Javadoc, y el programa funciona igual.

---

## Justificación de las colecciones utilizadas

**`HashMap<String, Asignatura>` (en `Sistema`):** se eligió porque la operación más frecuente del sistema es "buscar una asignatura por su código", y un mapa resuelve eso en tiempo prácticamente constante, sin importar si hay 5 o 5.000 cursos guardados. Además es el requisito explícito de SIA-4 (al menos una colección debe ser un mapa).

**`ArrayList<Alumno>` y `ArrayList<RecursoDigital>` (dentro de cada `Asignatura`):** se usó lista y no otro mapa porque dentro de un curso no se necesita una búsqueda por llave tan agresiva como en el `Sistema` general — son colecciones más chicas que se recorren completas casi siempre que se listan (mostrar todos los alumnos, mostrar todos los recursos), y un `ArrayList` permite además mantener el orden de inscripción/carga, algo que un `HashMap` no garantiza.

**`HashMap<String, ArrayList<Double>>` (en `Alumno`, para las notas):** se necesitaba separar las notas por asignatura para poder calcular tanto el promedio de un ramo puntual como el promedio general — un mapa de listas es la forma más directa de modelar "una lista de notas por cada código de curso".

---

## Checklist de cumplimiento de la pauta

| Criterio | Estado |
|---|---|
| SIA-3: Encapsulamiento, atributos privados, datos iniciales | Cumplido (las colecciones se devuelven de solo lectura o como copia) |
| SIA-4: 2 colecciones, la segunda anidada, con al menos un mapa | Cumplido (HashMap + 2 ArrayList anidados) |
| SIA-5: Sobrecarga en 2 clases distintas | Cumplido (`RecursoDigital.mostrarDetalle` y `Alumno.calcularPromedio`, más `Asignatura.buscarRecurso`) |
| SIA-6: Sobreescritura en 2 clases | Cumplido (`RecursoVideo` y `RecursoDocumento`, más `RecursoEnlaceWeb`) |
| SIA-7 / SIA-8: CRUD completo por colección, en menús separados | Cumplido |
| SIA-9: Funcionalidad propia con reporte filtrado | Cumplido (Boletín Académico) |
| SIA-10: Consola y ventanas, con selección al iniciar | Cumplido (`VentanaPrincipal`, Swing) |
| SIA-11: Persistencia con sistema batch (carga/graba) | Cumplido (`PersistenciaCSV`, 5 archivos CSV) |
| SIA-12: 2 excepciones propias con try-catch | Cumplido |
| SIA-13: Uso de GitHub | Cumplido (repositorio: https://github.com/vicenteRuiz-pucv/PROYECTO-PROGRAMACION-VSB.git) |
| SIA-O2 (opcional): Generación de planilla de cálculo | Cumplido (`ExportadorPlanilla`, `datos_sia/boletin_notas.xlsx`) |
| SIA-O3 (opcional): Documentación con Javadoc | Cumplido (`docs/index.html`) |

Con esto el proyecto cubre SIA-3 a SIA-13 completo, más los opcionales SIA-O2 (planilla) y SIA-O3 (Javadoc). No se implementaron SIA-O1 (componente gráfico estadístico) ni SIA-O4 (MVC).

---
