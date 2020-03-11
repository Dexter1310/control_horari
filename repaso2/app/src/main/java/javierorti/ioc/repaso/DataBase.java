package javierorti.ioc.repaso;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.ImageViewCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataBase  extends SQLiteOpenHelper{

TextView nombre_apellidos;

RequestQueue requestQueue;

    String consulta="create table usuarios( nombre text , apellidos text , categoria text ,tlf int ,dni int primary key ,tipo int)";
    String consulta2="create table diaE( iden int primary key, dni int ,hora_salida text , hora_entrada text , fecha text ,lugar_entrada text,lugar_salida text,total_hora_dia int )";

    public DataBase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase baseD) {

        baseD.execSQL(consulta);
        baseD.execSQL(consulta2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS diaE");

        //Se crea la nueva versión de la tabla

        db.execSQL(consulta);
        db.execSQL(consulta2);
    }


    //TODO: listado de personas

    public ArrayList llenar_lv(){
        ArrayList<String>lista=new ArrayList<>();

        return lista ;
    }

    public ArrayList llenar_registros(String person,String fecha_selec){

        ArrayList<String>movimient=new ArrayList<>();
        String nombre;
        String apell;
        String []  nom_ape = person.split(" ");
        nombre=nom_ape[0];apell=nom_ape[1];

        SQLiteDatabase database =this.getWritableDatabase();
        String q="SELECT * FROM usuarios where nombre= '"+nombre+"' and apellidos='"+apell+"'";
        Cursor registros=database.rawQuery(q,null);

        if(registros.move(1)){
            do{

                String e="SELECT * FROM diaE where dni= "+registros.getInt(4)+ " and fecha= '"+fecha_selec+"'";
                Cursor datos=database.rawQuery(e,null);
                if(datos.moveToNext()){
                    String horario=datos.getString(2);
                    String lugar=datos.getString(6);
                    if(datos.getString(2)==null||datos.getString(6)==null){
                        horario="";
                        lugar="";
                    }

                    movimient.add("\n\nDía : "+datos.getString(4) + "\t\t\tH. TOTAL :" +datos.getInt(7)+"\n\n"+
                            "INICIO: "+datos.getString(3)+" \t "  +datos.getString(5)+
                            "\n\nFIN: "+horario +" \t "+lugar+" \n");
                }else{
                    movimient.add("\nSin datos en fecha: "+fecha_selec+".\n\n Pulse botón ATRAS para volver al listado.");
                }

            }while(registros.moveToNext());
        }
        return movimient;
    }



}
