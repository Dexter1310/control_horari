package javierorti.ioc.repaso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Vacas extends AppCompatActivity {

CalendarView inicio,fin;
TextView ini,fi,titulo;
String diaI,diaF,dni,pass;
Button solicitar,atras,salir;
RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacas);
        inicio=(CalendarView)findViewById(R.id.VacacionesIniciales);
        fin=(CalendarView)findViewById(R.id.VacionesFinales);
        ini=(TextView)findViewById(R.id.vacasIni);
        fi=(TextView)findViewById(R.id.vacasFin);
        titulo=(TextView)findViewById(R.id.titulo_vacas);
        solicitar=(Button)findViewById(R.id.solicitarVacas);
        atras=(Button)findViewById(R.id.atrasVacas);
        salir=(Button)findViewById(R.id.salirVacas);
        solicitar.setVisibility(View.GONE);
        fin.setVisibility(View.GONE);
        dni=getIntent().getExtras().getString("dni");
        ini.setText("Indique desde que fecha quiere solicitar vacaciones");
        ini.setTextColor(Color.parseColor("#092B73"));

        //Todo: Comprobamos si hay solicitud pendiente
        comprobarVacas("http://kimor2010sl.000webhostapp.com/vacaciones.json");

//TODO: seleccionar fechas de solicitud
        inicio.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dia= String.valueOf(dayOfMonth); String mes= String.valueOf(month+1); String anio= String.valueOf(year);
                if(dia.length()>0 && dia.length()<2){
                    dia="0"+dia;
                }
                if(mes.length()>0 && mes.length()<2){
                    mes="0"+mes;
                }
                diaI=dia+"."+mes+"."+anio;
                ini.setText("Desde el día : "+diaI);
                inicio.setVisibility(View.GONE);
                fin.setVisibility(View.VISIBLE);
                fi.setText("Indique hasta que fecha quiere solicitar vacaciones");
                fi.setTextColor(Color.parseColor("#092B73"));

            }
        });


        fin.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dia= String.valueOf(dayOfMonth); String mes= String.valueOf(month+1); String anio= String.valueOf(year);
                if(dia.length()>0 && dia.length()<2){
                    dia="0"+dia;
                }
                if(mes.length()>0 && mes.length()<2){
                    mes="0"+mes;
                }
                diaF=dia+"."+mes+"."+anio;
                fi.setText("Hasta el día : "+ diaF);
                fin.setVisibility(View.GONE);
                solicitar.setVisibility(View.VISIBLE);

            }
        });


        //Todo:volver hacia atras y recargar activity de solicitud de vacaciones:
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sortir=new Intent(Vacas.this,Fichar.class);
                startActivity(sortir);
            }
        });

        //TODO:salir de Solicitud de vacaciones:
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salir=new Intent(Intent.ACTION_MAIN);
                salir.addCategory(Intent.CATEGORY_HOME);
                salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(salir);
            }
        });

        //Todo:envio de solicitud de vacaciones al Administrador

        solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarVacas("http://kimor2010sl.000webhostapp.com/insertarVacas.php");


            }
        });

    }

    //Todo:insertar registro de  vacaciones:
    public void insertarVacas(String URL){
        StringRequest registroUsuario=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Solicitud enviada con exito,espere a tener respuesta de su administrador", Toast.LENGTH_SHORT).show();

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
                para.put("inicio",diaI);
                para.put("final",diaF);
                para.put("dniEmpleado",dni);
                para.put("resultado","0");
                return para;
            }
        };

        RequestQueue RequestQ= Volley.newRequestQueue(this);
        RequestQ.add(registroUsuario);
    }


    //Todo: Informacion de vacaciones del usuario la base de datos MYSQL
    private   void comprobarVacas(String URL){

        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject=null ;
                ArrayList<String> list= new ArrayList<String>();
                ArrayList<Integer> total= new ArrayList<Integer>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String dniUsu=jsonObject.getString("dniEmpleado");
                        String Fechainicio=jsonObject.getString("inicio");
                        String FechaFinal=jsonObject.getString("final");
                        String result=jsonObject.getString("resultado");

                        if( dniUsu.equals(dni)) {

                            if(result.equals("0")) {
                                titulo.setText("\nEXISTE UNA SOLICITUD PENDIENTE DE APROBAR");
                                ini.setText("\n  -Inicio: "+Fechainicio+"\n-Fin: "+FechaFinal);
                                inicio.setVisibility(View.GONE);
                                fi.setVisibility(View.GONE);
                                fin.setVisibility(View.GONE);
                                solicitar.setVisibility(View.GONE);

                            }

                        }

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "No se pudo obtener los registros", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}
