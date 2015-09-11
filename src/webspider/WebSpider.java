package webspider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
 * @author Diego Barrionuevo, Germán Parisi
 */
public class WebSpider {

    private HashMap<String, ArrayList<URL>> sitiosVisitados;
    private File directorio;

    public void espiar(URL sitio) {
        this.directorio = new File("datos/");
        sitiosVisitados = new HashMap<>();
        espiarSitio(sitio, 1, 1000);
    }

    /**
     * Este método espía desde un determinado sitio y va recorriendo todos sus
     * enlaces.
     *
     * @param sitioActual es el sitio que estoy analizando.
     * @param nivel es el nivel de profundidad.
     * @param maxNivel es el nivel máximo.
     * @return false si llegó a un sitio que no tiene links o que tienen links
     * pero todos esos ya han sido visitados.
     */
    private boolean espiarSitio(URL sitioActual, int nivel, int maxNivel) {
        if (nivel > maxNivel) {
            return false;
        }
        System.out.println("Descargando sitio: " + sitioActual + "(" + nivel + ")");
        try {
            // Agrego el sitio actual a los visitados en caso que no haya sido visitado.
            String host = sitioActual.getHost();
            ArrayList<URL> sitios = sitiosVisitados.get(host);
            if (sitios == null) {
                sitios = new ArrayList<>();
                sitios.add(sitioActual);
            } else {
                if (sitios.indexOf(sitioActual) < 0) {
                    sitios.add(sitioActual);
                } else {
                    // El sitio ya ha sido visitado.
                    return false;
                }
            }
            sitiosVisitados.put(host, sitios);

            // Descargo archivo
            String nombreSitio = obtenerNombreSitio(sitioActual.toString());
            String dir = directorio + "/" + host + "/";
            File fileDirectorio = new File(dir);
            fileDirectorio.mkdir();
            File archivoDescargado = SystemIO.downloadFile(sitioActual.toString(), dir + nombreSitio + ".txt");

            String contenido = SystemIO.readFile(archivoDescargado);
            String exp = "<a\\s+href=[\"|']([^\"|']+)[\"|']";
            Matcher matcher = Pattern.compile(exp).matcher(contenido);
            int cantidad = 0;
            while (matcher.find()) {
                String enlaceEncontrado = (matcher.group(1));
                cantidad++;
                // Ir a enlace
                if (enlaceEncontrado.contains("http") || enlaceEncontrado.contains("https")) {
                    // Url absoluta
                    URL nuevaUrl = new URL(enlaceEncontrado);
                    espiarSitio(nuevaUrl, nivel + 1, maxNivel);
                } else {
                    String protocolo = sitioActual.getProtocol();
                    URL nuevaUrl = new URL(protocolo + "://" + host + "/" + enlaceEncontrado);
                    espiarSitio(nuevaUrl, nivel + 1, maxNivel);
                }
            }
            return cantidad != 0;
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    private String obtenerNombreSitio(String sitio) {
        sitio = sitio.replaceAll("[^a-zA-Z_0-9]", "_");
        return sitio;
    }

    public static void main(String[] args) throws MalformedURLException {
        WebSpider spider = new WebSpider();
        URL sitioInicial = new URL("http://www.openwebspider.org/");
        spider.espiar(sitioInicial);
    }

}
