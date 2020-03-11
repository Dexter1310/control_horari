package javierorti.ioc.repaso;

import androidx.annotation.NonNull;
import  androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
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
import java.util.List;
import java.util.Map;

public class administrador extends AppCompatActivity {

    Button atras,registro,lista,si,no,solicitud;
    ListView lv,re,usuVacas;
    ArrayList<String> listNombre;
    List<GestorVacaciones> list= new ArrayList<>();
    ArrayList<String>registros;
    String persona;
    CalendarView calendario;
    TextView personal,informacion;
    SQLiteDatabase dato;
    String nombre_usu,ape,dni,tipo,nombr,apell,ident;
    String nom,cognom,nomApe,diaR,nomAdmin,pass,idAdmin,dniUsu;
    RequestQueue requestQueue;

    ArrayAdapter ad,vc;

    DataBase listado=new DataBase(this,"gestion_usuarios",null,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        //TODO: salir de la aplicación:
        personal=(TextView)findViewById(R.id.texto2);
        informacion=(TextView)findViewById(R.id.informacionAd);
        informacion.setEnabled(false);
        atras=(Button) findViewById(R.id.btnAdminAtras);
        lista=(Button) findViewById(R.id.btnSalirAdmin);
        solicitud=(Button)findViewById(R.id.solicitudes);
        si=(Button)findViewById(R.id.btn_si);
        no=(Button)findViewById(R.id.btn_no);
        registro=(Button)findViewById(R.id.bt2);
        lv=(ListView) findViewById(R.id.lista);
        re=(ListView)findViewById(R.id.lista_registros);
        usuVacas=(ListView)findViewById(R.id.usuVacas);
        calendario=(CalendarView) findViewById(R.id.calendario);
        si.setVisibility(View.GONE);no.setVisibility(View.GONE);
        calendario.setVisibility(View.GONE);//no mostrar calendario
        nomAdmin= getIntent().getExtras().getString("nomAdmin");
        pass= getIntent().getExtras().getString("pass");
        solicitud.setVisibility(View.GONE);
        //Todo: Comprobamos si hay solicitud pendiente
        comprobarVacas("http://kimor2010sl.000webhostapp.com/vacaciones.json");

        listaAdministradores("https://kimor2010sl.000webhostapp.com/administradores.json");



       listNombre = listado.llenar_lv();

        //Todo: Eliminar el usuario manteniendo pulsado:
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id) {

                persona=listNombre.get(position);
                String [] nombreCompleto=persona.split(" ");
                nombre_usu=nombreCompleto[0];ape=nombreCompleto[1];dni=nombreCompleto[2];
                Toast.makeText(administrador.this, "Eliminar:"+nombre_usu+"#"+ape, Toast.LENGTH_LONG).show();
                personal.setTextSize(16);
                personal.setTextColor(Color.parseColor("#F50517"));
                personal.setText("\nDesea eliminar a "+persona+"?\n\n");
                si.setVisibility(View.VISIBLE);
                no.setVisibility(View.VISIBLE);
                atras.setVisibility(View.GONE);
                lista.setVisibility(View.GONE);
                registro.setVisibility(View.GONE);
                si.setOnClickListener(new View.OnClickListener() {// si el administrador elige SI:
                    @Override
                public void onClick(View v) {
                personal.setText("Se acaba de eliminar a "+nombre_usu);
                si.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                atras.setVisibility(View.VISIBLE);
                eliminar_usu("https://kimor2010sl.000webhostapp.com/eliminarUsuario.php?nombre="+nombre_usu+"&ape="+ape+"&dni="+dni);

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {//si el administrador elige No:
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                });

                return true;
            }
        });

        //Todo: Pulsar un usuario y ver registros:
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                solicitud.setVisibility(View.GONE);lv.setVisibility(View.GONE);
                persona=listNombre.get(position);
                personal.setText(persona);
                personal.setTextColor(Color.parseColor("#05A9F5"));

        calendario.setVisibility(View.VISIBLE);//mostrar calendario
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dia= String.valueOf(dayOfMonth); String mes= String.valueOf(month+1); String anio= String.valueOf(year);
                if(dia.length()>0 && dia.length()<2){
                    dia="0"+dia;
                }
                    if(mes.length()>0 && mes.length()<2){
                        mes="0"+mes;
                    }
                diaR=dia+"."+mes+"."+anio;

                String [] nombreApe=persona.split(" ");
                nombr=nombreApe[0];
                apell=nombreApe[1];
                dni=nombreApe[2];


                verDia("http://kimor2010sl.000webhostapp.com/registros.json");
                calendario.setVisibility(View.GONE);
                lv.setVisibility(View.GONE);
            }
        });

            }
        });


        //Todo:Gestionar vacaciones pendientes
        usuVacas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                si.setVisibility(View.VISIBLE);no.setVisibility(View.VISIBLE);lista.setVisibility(View.GONE);
                usuVacas.setVisibility(View.GONE);solicitud.setVisibility(View.GONE);
                String dniUs=list.get(position).toString();
                final String[] parts = dniUs.split(" ");
                personal.setText("Seleccione propuesta");
                informacion.setVisibility(View.VISIBLE); g=list.get(position);informacion.setTextSize(17);informacion.setGravity(Gravity.CENTER);
                informacion.setText("\n"+g.informe);
                registro.setVisibility(View.GONE);
                si.setText("Admitir");si.setBackgroundColor(Color.parseColor("#BCF5A9"));
                no.setText("Denegar");no.setBackgroundColor(Color.parseColor("#F78181"));
                atras.setBackgroundColor(Color.parseColor("#F5F6CE"));
                atras.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                });
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        si.setVisibility(View.GONE);no.setVisibility(View.GONE);
                        informacion.setTextColor(Color.parseColor("#21610B"));
                        informacion.setText("\nAcaba de aceptar la solicitud de vacaciones, en breve se notificara al empleado.");
                        actualizarVacaciones("https://kimor2010sl.000webhostapp.com/insertarVacas.php","1",parts[1]);
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        si.setVisibility(View.GONE);no.setVisibility(View.GONE);
                        informacion.setTextColor(Color.parseColor("#8A0808"));
                        informacion.setText("\nAcaba de denegar la solicitud de vacaciones, en breve se notificara al empleado.");
                        actualizarVacaciones("https://kimor2010sl.000webhostapp.com/insertarVacas.php","2",parts[1]);
                    }
                });

            }
        });



        //TODO:registrar usuario nuevo
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pagina1=new Intent(administrador.this,segundoActivity.class);
                pagina1.putExtra("idAdmin",idAdmin);
                pagina1.putExtra("nomAdmin",nomAdmin);
                pagina1.putExtra("pass",pass);
                startActivity(pagina1);
            }
        });

        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });

        //TODO: Pasar a segunda vista
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed(); // seria para vover atras también
                Intent pantallaInicio=new Intent(administrador.this,MainActivity.class);
                startActivity(pantallaInicio);

            }
        });

    }
    //Todo: eliminar al usuario de la base de datos MYSQL
    public void eliminar_usu(String URL){
        StringRequest delUsuario=new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "USUARIO "+nombre_usu+" ELIMINADO CON EXITO", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString()+"NO HA SIDO POSIBLE ELIMINAR A "+nombre_usu,Toast.LENGTH_SHORT).show();
            }
        }){

        };

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(delUsuario);
    }


    //Todo: listado de  usuarios de la base de datos MYSQL
    public    void listaUsu(String URL){
         JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,URL,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {
                         jsonObject = response.getJSONObject(i);

                        nom = jsonObject.getString("nombre");
                        ape = jsonObject.getString("apellidos");
                        dni= jsonObject.getString("dni");
                        String idAdministrador=jsonObject.getString("idAdmin");

                        String tipo=jsonObject.getString("tipo");
                        nomApe=nom+" "+ape+" "+dni;

                        if(tipo.equals("0") && idAdministrador.equals(idAdmin)) {
                            listNombre.add(nomApe);
                            ad = new ArrayAdapter(administrador.this, android.R.layout.simple_list_item_1, listNombre);
                            lv.setAdapter(ad);
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
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



    //Todo: listado de  usuarios de la base de datos MYSQL
    private   void listaAdministradores(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,URL,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String nombreAdminis = jsonObject.getString("nombre");
                        String contraAdmin= jsonObject.getString("contra");
                        if(nombreAdminis.equals(nomAdmin)&&contraAdmin.equals(pass)) {
                            personal.setText(nomAdmin);
                            idAdmin=jsonObject.getString("id");
                            listaUsu("https://kimor2010sl.000webhostapp.com/clientes.json");

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
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



    //Todo: Ver el día de trabajo seleccionado
    private   void verDia(String URL){


        final JsonArrayRequest jsonDiaEncontrado=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String fechaR=jsonObject.getString("fecha");
                        String hora_entran=jsonObject.getString("hora_entrada");
                        String hora_sali=jsonObject.getString("hora_salida");
                        String identificador=jsonObject.getString("iden");
                        String totales=jsonObject.getString("total_hora_dia");
                        String dniUsu=jsonObject.getString("dni");
                        String lugar_entrada =jsonObject.getString("lugar_entrada");
                        String lugar_salida=jsonObject.getString("lugar_salida");

                        if(fechaR.equals(diaR) && dniUsu.equals(dni)){
                                informacion(fechaR,totales,hora_entran,lugar_entrada,hora_sali,lugar_salida);
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
        requestQueue.add(jsonDiaEncontrado);
    }


public void informacion(String diaR,String totales,String hora_entran,String lugar_entrada,String hora_sali,String lugar_salida){
    informacion.setEnabled(true);
    informacion.setText("\n\nDÍA:"+diaR+"\t\t\tTOTAL HORAS: "+totales+"\n\nHORA ENTRADA: "
            +hora_entran+"\n\nLugar: "+lugar_entrada+"\n\n\nHORA SALIDA:" +
            hora_sali+"\n\nLugar:"+lugar_salida);
}


    //Todo: Informacion de vacaciones del usuario la base de datos MYSQL


    GestorVacaciones g;
    private   void comprobarVacas(String URL){

        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {

                        jsonObject = response.getJSONObject(i);
                        dniUsu=jsonObject.getString("dniEmpleado");
                        String Fechainicio=jsonObject.getString("inicio");
                        String FechaFinal=jsonObject.getString("final");
                        String result=jsonObject.getString("resultado");

                            if(result.equals("0")) {
                                listaUsuVacas("https://kimor2010sl.000webhostapp.com/clientes.json",dniUsu,Fechainicio,FechaFinal);
                            }
                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                solicitud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv.setVisibility(View.GONE);
                        vc=new ArrayAdapter(administrador.this,android.R.layout.simple_list_item_1,list);
                        usuVacas.setAdapter(vc);
                    }
                });


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



    //Todo: listado de  usuarios Vacaciones de la base de datos MYSQL

    public    void listaUsuVacas(String URL, final String idUsuVaca, final String Finicial, final String Ffinal){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,URL,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        String nombreUsusarioVacas = jsonObject.getString("nombre");
                        ape = jsonObject.getString("apellidos");
                        dni= jsonObject.getString("dni");
                        String idAdministrador=jsonObject.getString("idAdmin");

                        if(dni.equals(idUsuVaca)&& idAdministrador.equals(idAdmin)){
                            String informacion="ID: "+idUsuVaca+" "+nombreUsusarioVacas+ " solicita vacaciones:" +
                                    " \nInicio :"+Finicial+"\nFinal: "+Ffinal+"\n\n";
                            solicitud.setVisibility(View.VISIBLE);
                            solicitud.setTextColor(Color.parseColor("#1E1695"));
                            list.add(g=new GestorVacaciones(informacion,si,no));

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
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void actualizarVacaciones(String URL, final String resultado, final String idUs){
        StringRequest registroUsuario=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("resultadoVacas",resultado);
                para.put("dniEmpleado",idUs);
                return para;
            }
        };
        RequestQueue RequestQ= Volley.newRequestQueue(this);
        RequestQ.add(registroUsuario);
    }




}
