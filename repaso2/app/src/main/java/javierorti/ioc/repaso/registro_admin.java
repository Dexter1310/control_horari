package javierorti.ioc.repaso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class registro_admin extends AppCompatActivity {
    Button atras;
    Button salir;
    Button ok;
    EditText nombre_admin,apellido_admin,acividad_admin,telefono_admin,contras_admin;
    DataBase admin=new DataBase(this,"gestion_usuarios",null,1);
    SQLiteDatabase base_de_datos;
    String nombreAdministrador;
    String apellidoAdministrador;
    String actividadAdministrador;
    String telefonoAdministrador;
    String contrasenya;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_admin);

        atras=(Button) findViewById(R.id.btnAtras);
        //TODO: pasar a segunda vista
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed(); // seria para vover atras también
                Intent pantallaInicio=new Intent(registro_admin.this,MainActivity.class);
                startActivity(pantallaInicio);

            }
        });
        //TODO: salir de la aplicación
        salir=(Button) findViewById(R.id.btnSalir);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent salir=new Intent(Intent.ACTION_MAIN);
                salir.addCategory(Intent.CATEGORY_HOME);
                salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(salir);
            }
        });

    }
    public void Registrar_admin(View v){

        nombre_admin=(EditText)findViewById(R.id.editText2);
        apellido_admin=(EditText)findViewById(R.id.editText3);
        acividad_admin=(EditText)findViewById(R.id.editText4);
        telefono_admin=(EditText)findViewById(R.id.editText5);
        contras_admin=(EditText)findViewById(R.id.editText6);
        nombreAdministrador=nombre_admin.getText().toString();
        apellidoAdministrador=apellido_admin.getText().toString();
        actividadAdministrador=acividad_admin.getText().toString();
        telefonoAdministrador=telefono_admin.getText().toString();
        contrasenya=contras_admin.getText().toString();
        int dni2=Integer.parseInt(contrasenya);
        int tlf2=Integer.parseInt(telefonoAdministrador);

        if(!nombreAdministrador.isEmpty()&& !apellidoAdministrador.isEmpty()&& !telefonoAdministrador.isEmpty()&&!actividadAdministrador.isEmpty()&& !contrasenya.isEmpty()){
            guardarAdmin("http://kimor2010sl.000webhostapp.com/insertarAdmin.php");

        }else {
            Toast.makeText(this, "Debes rellenar los campos vacios",Toast.LENGTH_SHORT).show();

        }

    }

    //TODO:Registrar administrador:
    public void guardarAdmin(String URL){
        StringRequest registroAdmin=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Toast.makeText(getApplicationContext(), "REGISTRO CORRECTO de "+nombreAdministrador, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString()+"NO HA SIDO POSIBLE",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("nombre",nombreAdministrador);
                para.put("apellidos",apellidoAdministrador);
                para.put("categoria",actividadAdministrador);
                para.put("tlf",telefonoAdministrador);
                para.put("dni",contrasenya);
                para.put("tipo","1");
                return para;
            }
        };

        RequestQueue RequestQ= Volley.newRequestQueue(this);
        RequestQ.add(registroAdmin);
    }















}
