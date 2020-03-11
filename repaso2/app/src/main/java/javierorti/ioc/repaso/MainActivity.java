package javierorti.ioc.repaso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Button fichar;
    Button salir,agrega_admin;
    EditText et_nom,et_dni;
    ImageView ayuda,creaAdmin;
    String nom;
    TextView notaInforma;
    String dni;
    private final int respuest=0;
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        salir=(Button)findViewById(R.id.salir_ap);
        fichar=(Button)findViewById(R.id.fichar_registro);
        ayuda=(ImageView)findViewById(R.id.ayuda);
        creaAdmin=(ImageView)findViewById(R.id.creaAdmin);
        notaInforma=(TextView)findViewById(R.id.mensajeInform);
        agrega_admin=(Button)findViewById(R.id.btn_agrega_admin);
        agrega_admin.setVisibility(View.GONE);
        et_nom=(EditText)findViewById(R.id.nombre_asig);
        et_dni=(EditText) findViewById(R.id.dni_asig);
        //no aparece el teclado al iniciar activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);//inicializamos LocationServices
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},respuest);

            Toast.makeText(this," Se ha permitido la ubicación",Toast.LENGTH_SHORT).show();
        }

        listaUsu("https://kimor2010sl.000webhostapp.com/listadoUsuario.php");

       // Todo: Agrega administrador:

            creaAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent agregarAdmin=new Intent(MainActivity.this,registro_admin.class);
                    startActivity(agregarAdmin);
                }
            });
            ayuda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ayuda=new Intent(MainActivity.this, ayuda.class);
                    startActivity(ayuda);
                }
            });



    //TODO:salir de la app
    salir.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent salir=new Intent(Intent.ACTION_MAIN);
            salir.addCategory(Intent.CATEGORY_HOME);
            salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(salir);


        }
    });
        //TODO: consulta el usuario registrado:
    fichar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dni=et_dni.getText().toString();nom=et_nom.getText().toString();

            if(!nom.isEmpty() && !dni.isEmpty()){
                 comprobarUsu("https://kimor2010sl.000webhostapp.com/clientes.json");


            }else{
                Toast.makeText(getApplicationContext(),"introduzca valores correctos",Toast.LENGTH_SHORT).show();
            }

        }
    });
    notaInforma.setVisibility(View.GONE);

    }


    //Todo: cargar listado en el archivo json
    public void listaUsu(String URL){
        StringRequest delUsuario=new StringRequest( URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "App fuera de servicio", Toast.LENGTH_SHORT).show();
            }
        }){

        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(delUsuario);
    }

    //Todo: listado de  usuarios de la base de datos MYSQL
    private   void comprobarUsu(String URL){

        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                Intent ficha=new Intent(MainActivity.this,Fichar.class);
                 Intent administrador=new Intent(MainActivity.this,administrador.class);
                 String noEncontrado="Registro incorrecto , revise los campos Nombre y DNI";
                 notaInforma.setText(noEncontrado);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                       String nombreE = jsonObject.getString("nombre");
                       String ape =jsonObject.getString("apellidos");
                       String  dniE = jsonObject.getString("dni");
                       String tipo = jsonObject.getString("tipo");

                        if(nombreE.equals(nom)&& dniE.equals(dni)&&tipo.equals("1")){
                            notaInforma.setVisibility(View.GONE);
                            administrador.putExtra("nomAdmin",nombreE);
                            administrador.putExtra("pass",dniE);
                            startActivity(administrador);

                        }if(nombreE.equals(nom)&& dniE.equals(dni)&&tipo.equals("0")){
                            notaInforma.setVisibility(View.GONE);
                            ficha.putExtra("nombre",nom);
                            ficha.putExtra("ape",ape);
                            ficha.putExtra("id",dni);
                            startActivity(ficha);

                        }


                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "No se pudo obtener listado"+nom, Toast.LENGTH_SHORT).show();
            }
        });
        notaInforma.setVisibility(View.VISIBLE);
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }






}


