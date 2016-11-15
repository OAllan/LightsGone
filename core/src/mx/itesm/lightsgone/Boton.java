package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by allanruiz on 21/09/16.
 */
public class Boton {
    private Sprite sprite;
    private Rectangle rectangle;
    private Estado estado;
    private float timer = 0.2f;
    private boolean pad;
    private Animacion animacion;
    private float x;

    public Boton(float x, float y, float width, float height){
        rectangle = new Rectangle(x,y,width,height);
    }

    public Boton(Texture boton, float x, float y, boolean pad) {
        sprite = new Sprite(boton);
        sprite.setPosition(x,y);
        estado = Estado.NOPRESIONADO;
        this.pad = pad;
        this.animacion = Animacion.DESACTIVADA;
    }

    public Boton(float x, float y, float width, float height, boolean pad) {
        this(x,y,width,height);
        this.pad = pad;
    }

    public void draw(SpriteBatch batch){
        switch (estado){
            case NOPRESIONADO:
                press();
                break;
            case PRESIONADO:
                animacion = Animacion.ACTIVADA;
                press();
                if(!pad)
                    estado = Estado.NOPRESIONADO;
                break;
        }
        sprite.draw(batch);
    }

    public boolean isPressed() {
        return estado == Estado.PRESIONADO;
    }

    public boolean contiene(float x, float y) {
        return sprite!=null?sprite.getBoundingRectangle().contains(x,y):rectangle.contains(x,y);
    }

    public void press(){
        switch (animacion){
            case ACTIVADA:
                sprite.setAlpha(1f);
                if(!pad){
                    timer -= Gdx.graphics.getDeltaTime();
                    if(timer <=0) {
                        animacion = Animacion.DESACTIVADA;
                        timer = 0.2f;
                    }
                }
                else{
                    if(estado == Estado.NOPRESIONADO)
                        animacion = Animacion.DESACTIVADA;
                }
                break;
            case DESACTIVADA:
                sprite.setAlpha(0.5f);

        }
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setTexture(Texture texture){
        sprite.setTexture(texture);
    }

    public void desaparecer(boolean flag){
        if(flag){
            sprite.setAlpha(0);
            estado = Estado.OCULTO;
        }
        else{
            if(estado != Estado.PRESIONADO)
                estado = Estado.NOPRESIONADO;
        }
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public enum Estado{
        PRESIONADO,
        NOPRESIONADO,
        OCULTO
    }

    public enum Animacion{
        ACTIVADA,
        DESACTIVADA
    }
}
