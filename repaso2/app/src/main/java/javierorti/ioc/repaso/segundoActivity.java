package javierorti.ioc.repaso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public  class segundoActivity extends AppCompatActivity {
    Button atras;
    Button salir;
    EditText et_nombre,et_apellidos,et_tlf,et_categoria,et_dni;
    DataBase admin=new DataBase(this,"gestion_usuarios",null,1);
    SQLiteDatabase base_de_datos;
    String nombre,apellidos,categoria,dni,tlf,nombreAdministrador,contraAdministrador;
    int dni2,tlf2;
    RequestQueue RequestQ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo);
         atras=(Button) findViewById(R.id.btnAtras);

          nombreAdministrador=getIntent().getExtras().getString("nomAdmin");
          contraAdministrador=getIntent().getExtras().getString("pass");
         //TODO: pasar a segunda vista
         atras.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //onBackPressed(); // seria para vover atras también
                 Intent pantallaInicio=new Intent(segundoActivity.this,administrador.class);
                 pantallaInicio.putExtra("nomAdmin",nombreAdministrador);
                 pantallaInicio.putExtra("pass",contraAdministrador);
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

    public void Registrar(View v){

        base_de_datos= admin.getWritableDatabase();//modo escritura
        et_nombre=(EditText) findViewById(R.id.editText2);
        et_apellidos=(EditText)findViewById(R.id.editText3);
        et_tlf=(EditText)findViewById(R.id.editText5);
        et_categoria=(EditText)findViewById(R.id.editText4);
        et_dni=(EditText)findViewById(R.id.editText6);
        nombre=et_nombre.getText().toString();
        apellidos=et_apellidos.getText().toString();
        tlf=et_tlf.getText().toString();
        categoria=et_categoria.getText().toString();
        dni=et_dni.getText().toString();


        if(!nombre.isEmpty()&& !apellidos.isEmpty()&& !tlf.isEmpty()&&!categoria.isEmpty()&& !dni.isEmpty()){
            guardarUsuario("https://kimor2010sl.000webhostapp.com/insertarUsuario.php");


        }else {

            Toast.makeText(this, "Debes rellenar los campos vacios",Toast.LENGTH_SHORT).show();

        }

    }

    //Todo:guardar usuario en mysql

    public void guardarUsuario(String URL){
        StringRequest registroUsuario=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DataBase consulta= new DataBase(segundoActivity.this,"gestion_usuarios",null,1);
                segundoActivity n=new segundoActivity();
                n.base_de_datos=consulta.getWritableDatabase();
                if(!dni.isEmpty()){
                    Cursor fila=n.base_de_datos.rawQuery
                            ("SELECT * FROM usuarios where dni ="+dni2,null);

                    if(fila.moveToFirst()){
                        Toast.makeText(segundoActivity.this,"Ya existe el usuario número: "+dni,Toast.LENGTH_SHORT).show();
                        fila.close();
                        n.base_de_datos.close();
                    }else{
                        dni2=Integer.parseInt(dni);
                        tlf2=Integer.parseInt(tlf);
                        ContentValues registroUsuario= new ContentValues();
                        registroUsuario.put("nombre",nombre);
                        registroUsuario.put("apellidos",apellidos);
                        registroUsuario.put("categoria",categoria);
                        registroUsuario.put("tlf",tlf2);
                        registroUsuario.put("dni", dni2);
                        registroUsuario.put("tipo",0);
                        base_de_datos.insert("usuarios",null,registroUsuario);
                        base_de_datos.close();
                        et_nombre.setText("");
                        et_apellidos.setText("");
                        et_categoria.setText("");
                        et_tlf.setText("");
                        et_dni.setText("");

                    }
                }

                Toast.makeText(getApplicationContext(), "REGISTRO CORRECTO de "+nombre, Toast.LENGTH_SHORT).show();



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
                para.put("nombre",nombre);
                para.put("apellidos",apellidos);
                para.put("categoria",categoria);
                para.put("tlf",tlf);
                para.put("dni",dni);
                para.put("tipo","0");
                para.put("idAdmin",getIntent().getExtras().getString("idAdmin"));
                return para;
            }
        };

        RequestQueue RequestQ= Volley.newRequestQueue(this);
        RequestQ.add(registroUsuario);

    }




}
