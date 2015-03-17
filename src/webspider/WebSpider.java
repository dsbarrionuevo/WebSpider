package webspider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.SystemIO;

/**
 *
 * LOGICA PLANTEADA Dada la url de un sitio, 1) Descargo el codigo fuente y lo
 * guardo localmente 2) Si posee links hijos continúo, sino regreso al padre, de
 * ser el padre termino. 3) Creo un array de links del sitio, evitando repetidos
 * 4) Por cada hijo, repito el paso 1)
 *
 * @author Diego Barrionuevo
 * @version 1.0
 */
public class WebSpider {

    //Sería bueno crear una estrucutra de arbol que se almacene en disco con carpetas y subcarpetas, 
    //creando así un mapa de la web.
    //Se debería, también, guardar todos los fuentes recavados en una bd.
    private ArrayList<String> sitiosVisitados;

    public void espiar(String sitio) {
        sitiosVisitados = new ArrayList<String>();
        espiarSitio(sitio, "", "");
    }

    private boolean espiarSitio(String sitioActual, String sitioPadre, String tab) {
        try {
            //verifico si el sitio ya existe en la lista de visitados
            for (String sitioVisitado : sitiosVisitados) {
                if (sitioActual.equalsIgnoreCase(sitioVisitado)) {
                    //ya existe asi que no lo guardo ni recorro sus hijos
                    return false;
                }
            }
            //no existe, recorro los hijos
            sitiosVisitados.add(sitioActual);
            //obtengo el nombre del archivo de texto que contendrá el codigo fuente dle sitio
            String sitio;
            if (sitioPadre.equals("")) {
                sitio = sitioActual;//url absoluta
            } else {
                sitio = sitioPadre + "/" + sitioActual;
            }
            String nombreSitio = obtenerNombreSitio(sitioActual);
            System.out.println(tab + "Descargando sitio " + sitioActual);
            //descargo el codigo fuente del sitio
            File sitioArchivo = SystemIO.downloadFile(sitio, nombreSitio + ".txt");
            //obtendo el contenido del sitio como un string
            String contenido = SystemIO.readFile(sitioArchivo);
            String exp = "<a\\s+href=[\"|']([^\"|']+)[\"|']";
            //obtengo las ocurrencias con mi patron (los links del sitio)
            Matcher matcher = Pattern.compile(exp).matcher(contenido);
            int cantidad = 0;
            //mientras existan ocurrencias
            while (matcher.find()) {
                //Nota: hay veces en la que lanza la excepción de MalFormedURLException 
                //porque la url del sitio encontrado es relativa!, por ser por ejemplo 
                //"/ejemplos.php", por lo que lo que se haría en esos casos es: 
                //al capturar la expcetión, tratar de encontrar el sitio con la url del 
                //padre agregada al principio, por ejemplo "www.ejemplos.com/ejemplos.php" 
                //para eso es necesario ahcer una estructura de arbol y recordar la jerarquía
                String sitioEncontrado = (matcher.group(1));
                System.out.println(tab + "Sitio encontrado " + sitioEncontrado);
                cantidad++;
                //recorrer hijo
                if (sitioEncontrado.contains("http") || sitioEncontrado.contains("https")) {
                    //url absoluta
                    espiarSitio(sitioEncontrado, "", tab + "    ");
                } else {
                    espiarSitio(sitioEncontrado, sitioActual, tab + "    ");
                }
            }
            System.out.println(tab + "Encontré " + cantidad + " links");
            return cantidad != 0;

        } catch (FileNotFoundException ex) {
            //NO SE DEBEN IGNORAR LAS EXCEPCIONES!
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            //NO SE DEBEN IGNORAR LAS EXCEPCIONES!
            System.err.println(ex.getMessage());
        }
        return false;
    }

    private String obtenerNombreSitio(String sitio) {
        sitio = sitio.replaceAll("[^a-zA-Z_0-9]", "_");
        return sitio;
    }

    public static void main(String[] args) {
        WebSpider spider = new WebSpider();
        spider.espiar("http://www.openwebspider.org/");
    }

}
