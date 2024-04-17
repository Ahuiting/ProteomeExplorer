package ProteomeExplorer.moleculeShow;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class PdbAPI {
    public static String getSecondaryInfo(String code) throws IOException {
        var string = "";

        try {
            code = code.toLowerCase();
            var url = new URL("https://files.rcsb.org/download/" + code + ".pdb");
            string = new String(getFromURL(url).readAllBytes());
        } catch (IOException e) {
            throw new IOException("Usage:PDBWebClient[code]");
        }
        return string;
    }

    public static InputStream getFromURL(URL url) throws IOException {
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getInputStream();
    }
}
