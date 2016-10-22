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
    public Pad(Texture fondo, Texture left, Texture right){
        this.fondo = new Sprite(fondo);
        this.left = new Boton(left, 100,30, true);
        this.right = new Boton(right, 270,30, true);
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
        left.draw(batch);
        right.draw(batch);
    }


}
