package proyectojavavsb;
import java.io.*;
/**
 * @author Vicho
 */
public class ProyectoJavaVSB {
    public static void main(String[] args) throws IOException{
        String nombre;
        BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ingrese un nombre");
        nombre = lector.readLine();
        System.out.println("EL nombre ingresado fue " + nombre);
    }

}
