package rencontre.dating.looveyou.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestWeb {

    private int codigoRespuesta;

    private URL url;
    private StringBuilder resultado;
    private HttpURLConnection conexion;
    private OutputStream escritor;
    private InputStream lector;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    public RequestWeb(String url, String metodo, boolean output) {
        try {
            this.url = new URL("https://looveyou.com/api/" + url);
            conexion = (HttpURLConnection) this.url.openConnection();

            conexion.setDoOutput(output);
            conexion.setUseCaches(false);
            conexion.setConnectTimeout(2000);
            conexion.setRequestMethod(metodo);
            conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviar(JSONObject valores) {

        try {
            byte[] bytes = valores.toString().getBytes("UTF-8");
            conexion.setChunkedStreamingMode(0);

            escritor = new BufferedOutputStream(conexion.getOutputStream());
            escritor.write(bytes);
            escritor.flush();
            escritor.close();
            obtenerRespuesta();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviar(JSONArray valores) {

        try {
            byte[] bytes = valores.toString().getBytes("UTF-8");
            conexion.setChunkedStreamingMode(0);

            escritor = new BufferedOutputStream(conexion.getOutputStream());
            escritor.write(bytes);
            escritor.flush();
            escritor.close();
            obtenerRespuesta();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obtenerRespuesta() {
        try {
            String linea = "";
            String respuesta = conexion.getResponseMessage();
            codigoRespuesta = conexion.getResponseCode();

            resultado = new StringBuilder();
            if (codigoRespuesta == HttpURLConnection.HTTP_OK) {

                lector = new BufferedInputStream(conexion.getInputStream());
                BufferedReader bufferLectura = new BufferedReader(new InputStreamReader(lector));

                while ((linea = bufferLectura.readLine()) != null) {
                    resultado.append(linea);
                }

                Object json = new JSONTokener(resultado.toString()).nextValue();
                if (json instanceof JSONArray) {
                    jsonArray = new JSONArray(resultado.toString());
                    jsonObject = null;
                } else if (json instanceof JSONObject) {
                    jsonArray = null;
                    jsonObject = new JSONObject(resultado.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }
}

