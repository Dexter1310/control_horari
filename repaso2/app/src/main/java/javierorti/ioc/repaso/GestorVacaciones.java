package javierorti.ioc.repaso;

import android.view.View;
import android.widget.Button;

import java.util.Comparator;

public class GestorVacaciones  {
administrador n=new administrador();
    String informe;
    Button aceptar,denegar;


    public void setInforme(String informe) {
        this.informe = informe;
    }

    public void setAceptar(Button aceptar) {
        this.aceptar = aceptar;
    }

    public void setDenegar(Button denegar) {
        this.denegar = denegar;
    }

    public String getInforme() {
        return informe;
    }

    public Button getAceptar() {
        return aceptar;
    }

    public Button getDenegar() {
        return denegar;
    }

@Override

    public String toString() {

        return informe ;
    }



    public GestorVacaciones(String informe, Button aceptar, Button denegar) {
        this.informe = informe;
        this.aceptar = aceptar;
        this.denegar = denegar;
    }
}
