package mx.itesm.lightsgone;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by allanruiz on 21/09/16.
 */
public class Pantalla {
    private OrthographicCamera camara;
    private Pad pad;
    private Boton botonSaltar;
    private Boton botonHabilidad;
    private Sprite vida;
    private Texto indicadorVida;
    private Boton pausa;

    public Pantalla(OrthographicCamera camara, Pad pad, Boton botonSaltar, Boton botonHabilidad, Sprite vida, Texto indicadorVida, Boton pausa){
        this.camara = camara;
        this.pad = pad;
        this.botonSaltar = botonSaltar;
        this.botonHabilidad = botonHabilidad;
        this.vida = vida;
        this.indicadorVida = indicadorVida;
        this.pausa = pausa;
    }

    public void update(float mov){
        camara.translate(mov, 0);
        pad.update(mov);
        botonSaltar.update(mov);
        botonHabilidad.update(mov);
        vida.translate(mov, 0);
        indicadorVida.setX(indicadorVida.getX() + mov);
        pausa.update(mov);
        camara.update();
    }
}
