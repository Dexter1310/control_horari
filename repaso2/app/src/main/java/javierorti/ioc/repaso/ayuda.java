package javierorti.ioc.repaso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ayuda extends AppCompatActivity {
    PhotoViewAttacher zoom;
    TextView titulo;
    ListView indice;
    ArrayList<String>in;
    ArrayAdapter ay;
    ImageView imagenAyuda,imagenAyuda2;
    ImageView atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);
        titulo=(TextView)findViewById(R.id.tituloAyuda);
        indice=(ListView)findViewById(R.id.indice);
        atras=(ImageView)findViewById(R.id.atrasAyuda);
        imagenAyuda=(ImageView)findViewById(R.id.imagenAyuda);
        imagenAyuda2=(ImageView)findViewById(R.id.imagenAyuda2);
        titulo.setText("Manual del usuario");
        in=new ArrayList<>();
        in.add("Agregar Empresa Administrador");
        in.add("Acceder a manual del usuario");
        in.add("Acceso");
        in.add("Agregar Empleado desde perfil administrador");

        ay=new ArrayAdapter(ayuda.this,android.R.layout.simple_list_item_1,in);
        indice.setAdapter(ay);
        //Todo: seleccionar lista

        indice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ayuda.this,in.get(position),Toast.LENGTH_SHORT).show();
                indice.setVisibility(View.GONE);
                titulo.setText(in.get(position));titulo.setGravity(Gravity.CENTER);

                if(in.get(position)=="Agregar Empresa Administrador"){
                    imagenAyuda.setImageResource(R.drawable.ayuda1);
                    zoom=new PhotoViewAttacher(imagenAyuda);//hacer zoom a la imagen
                    imagenAyuda2.setImageResource(R.drawable.ayuda2);
                    zoom=new PhotoViewAttacher(imagenAyuda2);
                }
                //volver al listado:
                atras.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       finish();
                       startActivity(getIntent());
                    }
                });

            }
        });
        //Todo: retroceder al menu principal:
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent atras=new Intent(ayuda.this,MainActivity.class);
                startActivity(atras);
            }
        });


    }
}
