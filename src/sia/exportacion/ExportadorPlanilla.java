package sia.exportacion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.Alumno;
import sia.Asignatura;
import sia.Sistema;

/**
 * Genera una planilla de cálculo (.xlsx) con el boletín de cada alumno
 * en cada una de sus asignaturas.
 */
public class ExportadorPlanilla {

    private static final String CARPETA = "datos_sia";
    private static final String ARCHIVO = CARPETA + "/boletin_notas.xlsx";

    /**
     * Exporta una fila por cada pareja (asignatura, alumno).
     *
     * @param sistema sistema con los datos a exportar
     * @return true si el archivo se generó correctamente
     */
    public static boolean exportarBoletin(Sistema sistema) {
        new File(CARPETA).mkdirs();
        try (Workbook libro = new XSSFWorkbook();
             FileOutputStream salida = new FileOutputStream(ARCHIVO)) {

            Sheet hoja = libro.createSheet("Boletín");
            CellStyle estiloCabecera = libro.createCellStyle();
            Font negrita = libro.createFont();
            negrita.setBold(true);
            estiloCabecera.setFont(negrita);

            String[] cabecera = {"Asignatura", "RUT", "Alumno", "Cantidad de notas", "Promedio", "Estado"};
            Row filaCab = hoja.createRow(0);
            for (int c = 0; c < cabecera.length; c++) {
                Cell celda = filaCab.createCell(c);
                celda.setCellValue(cabecera[c]);
                celda.setCellStyle(estiloCabecera);
            }

            int fila = 1;
            for (Asignatura asig : sistema.getMapaAsignaturas().values()) {
                for (Alumno al : asig.getListaAlumnos()) {
                    int cantidad = al.obtenerNotas(asig.getCodigo()).size();
                    double promedio = al.calcularPromedio(asig.getCodigo());
                    String estado = cantidad == 0 ? "Sin notas"
                            : (promedio >= Alumno.NOTA_APROBACION ? "Aprobado" : "En riesgo");
                    Row r = hoja.createRow(fila++);
                    r.createCell(0).setCellValue(asig.getCodigo());
                    r.createCell(1).setCellValue(al.getRut());
                    r.createCell(2).setCellValue(al.getNombre());
                    r.createCell(3).setCellValue(cantidad);
                    r.createCell(4).setCellValue(promedio);
                    r.createCell(5).setCellValue(estado);
                }
            }
            for (int c = 0; c < cabecera.length; c++) {
                hoja.autoSizeColumn(c);
            }
            libro.write(salida);
            return true;
        } catch (IOException e) {
            System.out.println("No se pudo generar la planilla: " + e.getMessage());
            return false;
        }
    }
}