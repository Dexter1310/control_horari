package javierorti.ioc.repaso;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fichar extends AppCompatActivity {
    RequestQueue requestQueue;
    private final int respuest=0;
    private FusedLocationProviderClient fusedLocationClient;
    TextView hola,fecha_dia,hora_entrada,hora_salida,total_h,direcEntrada,direcSalida,informacion,total_trabajadas,informaVacas;
    Button entra,sale,salir,atras,calendari,history,vacaciones;
    String dni,ape,nombre,fecha,diEntra,diSale,reloje,relojs,horaTotalT,hora_entran,hora_sali,diaR,fech;
    CalendarView calendar;
    SQLiteDatabase horario;
    ArrayAdapter adregi;
    ArrayList<String> registros;
    ListView re;
    Cursor fila2;
    int totalHorasTrabajadas,id,dn2,identificador,suma;
    ContentValues registroSalida = new ContentValues();
   // private LocationManager locManager;private Location loc;

    Toast toast1;
    DataBase consulta1= new DataBase(this,"gestion_usuarios",null,1);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    SimpleDateFormat data = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
    Date date = new Date();
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);
        hola=(TextView)findViewById(R.id.bienvenida);
        fecha_dia=(TextView)findViewById(R.id.data);
        MainActivity dat=new MainActivity();
        nombre= getIntent().getExtras().getString("nombre");
        ape= getIntent().getExtras().getString("ape");
        fecha = dateFormat.format(date);
        hola.setText(nombre+" "+ape);
        fecha_dia.setText(fecha);
        entra=(Button)findViewById(R.id.btn_entra);
        sale=(Button)findViewById(R.id.btn_sale);
        atras=(Button)findViewById(R.id.button5);
        salir=(Button)findViewById(R.id.button4);
        history=(Button)findViewById(R.id.history);
        vacaciones=(Button)findViewById(R.id.btn_vacas);
        calendari=(Button)findViewById(R.id.btn_calendario_usu);
        calendar=(CalendarView) findViewById(R.id.calendario_usu);
        informaVacas=(TextView)findViewById(R.id.inforVacaciones);
        hora_entrada=(TextView)findViewById(R.id.hora_entrada);
        hora_salida=(TextView)findViewById(R.id.hora_salida);
        total_h=(TextView)findViewById(R.id.total_horas);
        direcEntrada=(TextView)findViewById(R.id.direccion_entrada);
        direcSalida=(TextView)findViewById(R.id.direccion_salida);
        informacion=(TextView)findViewById(R.id.informacion);
        total_trabajadas=(TextView)findViewById(R.id.total_horas_trabajas);
        re=(ListView)findViewById(R.id.lista_usu);
        informacion.setEnabled(false);
        sale.setEnabled(false);
        calendar.setVisibility(View.GONE);
        total_trabajadas.setVisibility(View.GONE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);//inicializamos LocationServices


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},respuest);


        }else{

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess( Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
//                                diEntra="latitud: "+location.getLatitude() +"longitud: "+location.getLongitude();
//                                diSale="latitud: "+location.getLatitude() +"longitud: "+location.getLongitude();


                                Geocoder geo;
                                List<Address> direccion;
                                geo = new Geocoder(getApplicationContext());
                                try {
                                  direccion=  geo.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    String address = direccion.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = direccion.get(0).getLocality();
                                    String state = direccion.get(0).getAdminArea();
                                    String country = direccion.get(0).getCountryName();
                                    String postalCode = direccion.get(0).getPostalCode();
                                    diEntra=address;
                                    diSale=address;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }else{
                                total_h.setText("No ha sido posible la ubicación");

                            }
                        }
                    });
        }

        consultaRegistros("http://kimor2010sl.000webhostapp.com/registros.json");

        //Todo: Si la hora de entrada ya existe habilitar salida y deshabilitar entrada;

//Todo:solicitar vacaciones:
        vacaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vacaciones=new Intent(Fichar.this,Vacas.class);
                vacaciones.putExtra("nombre",nombre);
                vacaciones.putExtra("ape",ape);
                vacaciones.putExtra("dni",dni);
                startActivity(vacaciones);
            }
        });


//Todo: añadir horario y datos de ubicación en la tabla del dia cuando pulsamos entra
entra.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {


                direcEntrada.setText(diEntra);
                SimpleDateFormat hora_entra= new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date datee= new Date() ;
                reloje = hora_entra.format(datee);
                hora_entrada.setText("Hora de entrada: "+reloje);
                dni= getIntent().getExtras().getString("id");
                dn2=Integer.parseInt(dni);
                identificador=Integer.parseInt(data.format(date));//convertimos fecha a numero entero
                id= identificador+dn2;
                fecha = dateFormat.format(date);
                guardarEntrada("https://kimor2010sl.000webhostapp.com/entrada.php");
                entra.setEnabled(false);
                sale.setEnabled(true);


            }

        });
sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                direcSalida.setText(diSale);
                SimpleDateFormat hora_entra = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date datee = new Date();
                relojs = hora_entra.format(datee);
                hora_salida.setText("Hora de salida: " + relojs);
                dni = getIntent().getExtras().getString("id");
                int dn2 = Integer.parseInt(dni);
                int identificador = Integer.parseInt(data.format(date));//convertimos fecha a numero entero
                int id = identificador + dn2;
                fecha = dateFormat.format(date);
                guardarSalida("https://kimor2010sl.000webhostapp.com/exit.php");
                if(horaTotalT==null){
                    horaTotalT="0";
                }
                total_h.setText("Total de horas trabajadas: "+horaTotalT);
                toast1 = Toast.makeText(getApplicationContext(), "Se acaba de registrar la hora de salida: "+relojs + "dni:"+id+" con fecha :"+fecha, Toast.LENGTH_SHORT);
                toast1.show();
                sale.setEnabled(false);



            }
        });
atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed(); // seria para vover atras también
                Intent pantallaInicio=new Intent(Fichar.this,MainActivity.class);
                startActivity(pantallaInicio);

            }
        });
salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salir=new Intent(Intent.ACTION_MAIN);
                salir.addCategory(Intent.CATEGORY_HOME);
                salir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(salir);
            }
        });

//Todo: MUESTRA CALENDARIO DE HORAS
calendari.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
                re.setVisibility(View.GONE);
                entra.setVisibility(View.GONE);
                sale.setVisibility(View.GONE);
                calendari.setVisibility(View.GONE);
                hora_entrada.setVisibility(View.GONE);direcEntrada.setVisibility(View.GONE);
                hora_salida.setVisibility(View.GONE);direcSalida.setVisibility(View.GONE);total_h.setVisibility(View.GONE);
                history.setVisibility(View.GONE);
                informacion.setVisibility(View.GONE);
                total_trabajadas.setVisibility(View.GONE);
                atras.setOnClickListener(new View.OnClickListener() {// TODO : si pulsamos ATRAS volvemos al registro del día
                    @Override
                    public void onClick(View v) {
                        finish();
                        startActivity(getIntent());
                    }
                });
            calendar.setVisibility(View.VISIBLE);
                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        re.setVisibility(View.VISIBLE);
                        String dia= String.valueOf(dayOfMonth); String mes= String.valueOf(month+1); String anio= String.valueOf(year);
                        if(dia.length()>0 && dia.length()<2){
                            dia="0"+dia;
                        }
                        if(mes.length()>0 && mes.length()<2){
                            mes="0"+mes;
                        }

                        diaR=dia+"."+mes+"."+anio;
                        verDia("http://kimor2010sl.000webhostapp.com/registros.json");
                        calendar.setVisibility(View.GONE);
                        calendari.setVisibility(View.VISIBLE);
                        informacion.setVisibility(View.VISIBLE);
                        history.setVisibility(View.GONE);

                    }
                });
            }
        });

history.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        atras.setOnClickListener(new View.OnClickListener() {// TODO : si pulsamos ATRAS volvemos al registro del día
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
        re.setVisibility(View.GONE);
        entra.setVisibility(View.GONE);
        sale.setVisibility(View.GONE);
        hora_entrada.setVisibility(View.GONE);direcSalida.setVisibility(View.GONE);
        hora_salida.setVisibility(View.GONE);direcSalida.setVisibility(View.GONE);total_h.setVisibility(View.GONE);total_trabajadas.setVisibility(View.VISIBLE);
        historialHoras("http://kimor2010sl.000webhostapp.com/registros.json");
        peticionesVacas("http://kimor2010sl.000webhostapp.com/vacaciones.json");
    }
});
    }



    //Todo:guardar Entrada en mysql

    public void guardarEntrada(String URL){
        StringRequest registroUsuario=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Toast.makeText(getApplicationContext(), "HORA DE ENTRADA REGISTRADA :"+reloje, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString()+"NO HA SIDO POSIBLE REGISTRAR HORA DE ENTRADA",Toast.LENGTH_SHORT).show();
            }
        }){
            String identidad = String.valueOf(id);String dniUsu = String.valueOf(dn2);//convertimos cadena
            @Override

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("iden",identidad);
                para.put("dni",dniUsu);
                para.put("hora_entrada",reloje);
                para.put("fecha",fecha);
                para.put("lugar_entrada",diEntra);

                return para;
            }
        };
        RequestQueue RequestQ= Volley.newRequestQueue(this);
        RequestQ.add(registroUsuario);
    }

    //Todo:guardar Entrada en mysql

    public void guardarSalida(String URL){
        StringRequest horaSalida=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "HORA DE SALIDA REGISTRADA "+relojs, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString()+"NO HA SIDO POSIBLE REGISTRAR HORA DE ENTRADA",Toast.LENGTH_SHORT).show();
            }
        }){
            String identidad = String.valueOf(id);String dniUsu = String.valueOf(dn2);//convertimos cadena
            @Override

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> para=new HashMap<String,String>();
                para.put("iden",identidad);
                para.put("hora_salida",relojs);
                para.put("lugar_salida",diSale);
                para.put("total_hora_dia",horaTotalT);
                return para;
            }
        };

        RequestQueue RequestQ= Volley.newRequestQueue(this);
        RequestQ.add(horaSalida);
    }




    //Todo: Registro de hora de entada del usuario la base de datos MYSQL
    private   void consultaRegistros(String URL){
        final SimpleDateFormat hora_actual= new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date datee= new Date() ;relojs = hora_actual.format(datee);
        dni= getIntent().getExtras().getString("id");
        dn2=Integer.parseInt(dni);
        identificador=Integer.parseInt(data.format(date));//convertimos fecha a numero entero
        id= identificador+dn2;

        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        String identidad = String.valueOf(id);
                        hora_entran=jsonObject.getString("hora_entrada");
                        hora_sali=jsonObject.getString("hora_salida");
                        String identificador=jsonObject.getString("iden");
                        String totales=jsonObject.getString("total_hora_dia");
                        String dniUsu=jsonObject.getString("dni");
                        String lugar_entrada =jsonObject.getString("lugar_entrada");
                        String lugar_salida=jsonObject.getString("lugar_salida");
                        if(identificador.equals(identidad)){
                           String[] separated = hora_entran.split(":");
                           String he=separated[0];
                           String[]separate_sa=relojs.split(":");
                           String hs=separate_sa[0];
                           int horE=Integer.parseInt(he);
                           int horS=Integer.parseInt(hs);
                           totalHorasTrabajadas=horS-horE;
                           horaTotalT=String.valueOf(totalHorasTrabajadas);

                            if(!hora_entran.isEmpty()){
                                hora_entrada.setText("Hora de entrada:  "+hora_entran+"\nLugar:  "+lugar_entrada);
                                entra.setEnabled(false);sale.setEnabled(true);
                            }
                            if(!hora_sali.isEmpty()){
                                hora_salida.setText("Hora de salida:  "+hora_sali +"\nLugar:  "+lugar_salida);
                                total_h.setText("Total horas :"+totales);
                                sale.setEnabled(false);

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

            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



    //Todo: Ver el día de trabajo seleccionado
    private   void verDia(String URL){
        final SimpleDateFormat hora_actual= new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date datee= new Date() ;relojs = hora_actual.format(datee);
        dni= getIntent().getExtras().getString("id");
        dn2=Integer.parseInt(dni);
        identificador=Integer.parseInt(data.format(date));//convertimos fecha a numero entero
        id= identificador+dn2;

        final JsonArrayRequest jsonDiaEncontrado=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject=null ;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String identidad = String.valueOf(id);
                        String fechaR=jsonObject.getString("fecha");
                        hora_entran=jsonObject.getString("hora_entrada");
                        hora_sali=jsonObject.getString("hora_salida");
                        String identificador=jsonObject.getString("iden");
                        String totales=jsonObject.getString("total_hora_dia");
                        String dniUsu=jsonObject.getString("dni");
                        String lugar_entrada =jsonObject.getString("lugar_entrada");
                        String lugar_salida=jsonObject.getString("lugar_salida");
                        if(dniUsu.equals(dni)&&diaR.equals(fechaR)){
                            informacion.setEnabled(true);
                            informacion.setText("\n\nDía:"+diaR+"\t\tHoras totales: "+totales+"\n\nHora de entrada: "
                                    +hora_entran+"\nLugar: "+lugar_entrada+"\n\nHora Salida:" +
                                    hora_sali+"\nLugar:"+lugar_salida);
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

    //Todo: Historial de horas del usuario la base de datos MYSQL
    private   void historialHoras(String URL){
        final SimpleDateFormat hora_actual= new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date datee= new Date() ;relojs = hora_actual.format(datee);
        dni= getIntent().getExtras().getString("id");
        dn2=Integer.parseInt(dni);
        identificador=Integer.parseInt(data.format(date));//convertimos fecha a numero entero
        id= identificador+dn2;

        final JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject=null ;
            ArrayList<String> list= new ArrayList<String>();
                ArrayList<Integer> total= new ArrayList<Integer>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        String identidad = String.valueOf(id);
                        fech=jsonObject.getString("fecha");
                        hora_entran=jsonObject.getString("hora_entrada");
                        hora_sali=jsonObject.getString("hora_salida");
                        String identificador=jsonObject.getString("iden");
                        String totales=jsonObject.getString("total_hora_dia");
                        String dniUsu=jsonObject.getString("dni");
                        String lugar_entrada =jsonObject.getString("lugar_entrada");
                        String lugar_salida=jsonObject.getString("lugar_salida");

                        if(dniUsu.equals(dni)) {

                            suma=Integer.parseInt(totales);
                            list.add("Día: "+fech+" \t\t\tHoras :"+totales+"\n");
                            total.add(suma);
                        }


                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                String resultados = "";
                for (int i = 0; i < list.size(); i++)
                    if(i + 1 < list.size())
                        resultados += list.get(i) ;
                    else
                        resultados += list.get(i);

                informacion.setText(resultados);

                int totalito=0;
                for (int i = 0; i < total.size(); i++)
                    if(i + 1 < total.size())
                        totalito += total.get(i) ;
                    else
                        totalito +=total.get(i);

                    String t=String.valueOf(totalito);

                informacion.setText(resultados);
                total_trabajadas.setText(" Total de horas trabajadas:"+t);




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






    //Todo: Informacion de vacaciones del usuario la base de datos MYSQL
    private   void peticionesVacas(String URL){

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


                        if(dniUsu.equals(dni)) {

                            if(result.equals("0")){
                                result="En proceso de validación";
                            }
                            if(result.equals("1")){
                                result="Aprobado";
                            }
                            if(result.equals("2")){
                                result="Denegado";
                            }

                        informaVacas.setText("\n\nSOLICITUD DE VACACIONES \n\n-Inicio:"+Fechainicio+"\n-Final: "+FechaFinal+"\n-Resultado de petición: "+result);

                        }


                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }








}
