package mx.itesm.lightsgone;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by allanruiz on 22/09/16.
 */
public class Proyectil {
    private Sprite proyectil;
    private float x;
    private final float VELOCIDAD = 20f;
    private boolean right;
    public Proyectil(Texture proyectil, float x, float y, boolean right){
        this.proyectil = new Sprite(proyectil);
        this.x = x;
        this.proyectil.setPosition(x, y);
        this.right = right;
    }

    public void draw(SpriteBatch batch){
        proyectil.draw(batch);
    }

    public void update(){

        if(right)
            proyectil.translate(VELOCIDAD, 0);
        else
            proyectil.translate(-VELOCIDAD, 0);
    }

    public boolean out(){
        if (right)return proyectil.getX() >= x+1000;
        else return proyectil.getX() <= x-1000;
    }

    public void setX(float x){
        proyectil.setX(x);
    }

    public void setY(float y){
        proyectil.setY(y);
    }



    public Rectangle getRectangle(){
        return proyectil.getBoundingRectangle();
    }


}
