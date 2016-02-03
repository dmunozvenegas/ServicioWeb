package com.example.diegomunoz.servicioweb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button consultar;
    Button consultarID;
    Button insertar;
    Button borrar;
    Button actualizar;
    EditText identificador;
    EditText nombre;
    EditText direccion;
    TextView resultado;

    //IP de mi URL
    String IP = "http://androidtuto.esy.es";
    //Rutas de WebService
    String GET = IP + "/obtener_alumnos.php";
    String GET_BY_ID = IP + "/obtener_alumno_por_id.php";
    String INSERT = IP + "/insertar_alumno.php";
    String DELETE = IP + "/borrar_alumno.php";
    String UPDATE = IP + "/actualizar_alumno.php";

    obtenerWebService hiloConexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        consultar = (Button)findViewById(R.id.btnConsultar);
        consultarID = (Button)findViewById(R.id.btnConsultarID);
        insertar = (Button)findViewById(R.id.btnInsertar);
        borrar = (Button)findViewById(R.id.btnBorrar);
        actualizar = (Button)findViewById(R.id.btnActualizar);
        identificador = (EditText)findViewById(R.id.etID);
        nombre = (EditText)findViewById(R.id.etNombre);
        direccion = (EditText)findViewById(R.id.etDireccion);
        resultado = (TextView)findViewById(R.id.tvResultado);

        consultar.setOnClickListener(this);
        consultarID.setOnClickListener(this);
        insertar.setOnClickListener(this);
        borrar.setOnClickListener(this);
        actualizar.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConsultar:
                hiloConexion = new obtenerWebService();
                hiloConexion.execute(GET,"1");

                break;
            case R.id.btnConsultarID:
                hiloConexion = new obtenerWebService();
                String cadenaLlamada = GET_BY_ID + "?idalumno=" + identificador.getText().toString();
                hiloConexion.execute(cadenaLlamada,"2");

                break;
            case R.id.btnInsertar:
                hiloConexion = new obtenerWebService();
                hiloConexion.execute(INSERT,"3",nombre.getText().toString(),direccion.getText().toString()); //Parametro que recibe

                break;
            case R.id.btnBorrar:
                hiloConexion = new obtenerWebService();
                hiloConexion.execute(DELETE,"4",identificador.getText().toString());

                break;
            case R.id.btnActualizar:
                hiloConexion = new obtenerWebService();
                hiloConexion.execute(UPDATE,"5",identificador.getText().toString(),nombre.getText().toString(),direccion.getText().toString());

                break;
            default:
                break;
        }

    }


    public class obtenerWebService extends AsyncTask<String,Void,String> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            resultado.setText(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null; // URL de donde queremos obtener información
            String devuelve = "";

            //Consultar de todos los alumnos
            if ( params[1] == "1" ) {

                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    connection.setRequestProperty("User-Agent","Mozilla/5.0" +
                            " (Linux: Android 1.5; es-ES) Ejemplo HTTP");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        InputStream in = new BufferedInputStream(connection.getInputStream()); //Prepara la cadena de entrada

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //guarda en un BufferReader



                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject respuestaJSON = new JSONObject(result.toString());

                        String resultJSON = respuestaJSON.getString("estado"); //Estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")) {
                            JSONArray alumnosJSON = respuestaJSON.getJSONArray("alumnos");
                            for (int i=0 ; i<alumnosJSON.length() ; i++){
                                devuelve = devuelve + alumnosJSON.getJSONObject(i).getString("idAlumno") + " " +
                                                      alumnosJSON.getJSONObject(i).getString("nombre") + " " +
                                                      alumnosJSON.getJSONObject(i).getString("direccion") + "\n";

                            }
                        }
                        else if (resultJSON.equals("2")) {
                            devuelve = "No Hay Alumnos ";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }

            //Consultar ID
            else if ( params [1] == "2" ) {


                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    connection.setRequestProperty("User-Agent","Mozilla/5.0" +
                            " (Linux: Android 1.5; es-ES) Ejemplo HTTP");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        InputStream in = new BufferedInputStream(connection.getInputStream()); //Prepara la cadena de entrada

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //guarda en un BufferReader



                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject respuestaJSON = new JSONObject(result.toString());

                        String resultJSON = respuestaJSON.getString("estado"); //Estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")) {
                                devuelve = devuelve + respuestaJSON.getJSONObject("alumno").getString("idAlumno") + " " +
                                        respuestaJSON.getJSONObject("alumno").getString("nombre") + " " +
                                        respuestaJSON.getJSONObject("alumno").getString("direccion") + "\n";
                        }
                        else if (resultJSON.equals("2")) {
                            devuelve = "No Hay Alumnos ";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }
            //Insertar
            else if ( params [1] == "3") {

                try {

                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataInputStream input;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //Creo el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("nombre", params[2]);
                    jsonParam.put("direccion", params[3]);

                    //Envio los parametros post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();


                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream())); //guarda en un BufferReader
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject respuestaJSON = new JSONObject(result.toString());

                        String resultJSON = respuestaJSON.getString("estado"); //Estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")) {
                            devuelve = "Alumno Insertado Correctamente";
                        }
                        else if (resultJSON.equals("2")) {
                            devuelve = "No Se Inserto Alumnos ";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }
            //Borrar
            else if ( params [1] == "4") {

                try {

                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataInputStream input;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //Creo el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("idalumno", params[2]);
                    //Envio los parametros post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();


                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream())); //guarda en un BufferReader

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject respuestaJSON = new JSONObject(result.toString());

                        String resultJSON = respuestaJSON.getString("estado"); //Estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")) {
                            devuelve = "Sentencia Ejecutada Correctamente, Elemento Eliminado";
                        }
                        else if (resultJSON.equals("2")) {
                            devuelve = "No Hay Alumnos ";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }
            //Actualizar Alumnos
            else if ( params [1] == "5") {

                try {

                    HttpURLConnection urlConn;

                    DataOutputStream printout;
                    DataInputStream input;
                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //Creo el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("idalumno", params[2]);
                    jsonParam.put("nombre", params[3]);
                    jsonParam.put("direccion", params[4]);

                    //Envio los parametros post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();


                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream())); //guarda en un BufferReader
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject respuestaJSON = new JSONObject(result.toString());

                        String resultJSON = respuestaJSON.getString("estado"); //Estado es el nombre del campo en el JSON

                        if (resultJSON.equals("1")) {
                            devuelve = "Alumno Actualizado Correctamente";
                        }
                        else if (resultJSON.equals("2")) {
                            devuelve = "No Se Actualizo Alumnos ";
                        }

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return devuelve;

            }
            return null;
        }
    }
}
