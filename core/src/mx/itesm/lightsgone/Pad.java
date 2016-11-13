package mx.itesm.lightsgone;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by allanruiz on 21/09/16.
 */
public class Pad {
    private Boton left;
    private Boton right;
    private Sprite fondo;
    public Pad(Texture fondo){
        this.fondo = new Sprite(fondo);
        this.left = new Boton(100,30,100,188, true);
        this.right = new Boton(239,30,100,188, true);
        this.fondo.setPosition(100,30);
    }

    public Boton getLeft(){
        return left;
    }

    public Boton getRight(){
        return right;
    }

    public void draw(SpriteBatch batch){
        fondo.draw(batch);
    }


}
