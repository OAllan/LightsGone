package mx.itesm.lightsgone;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

/**
 * Created by allanruiz on 07/10/16.
 */
public abstract class Enemigo  {
    private Sprite sprite;
    private String nombre;
    private float xInicial, yInicial;
    private Texture neutral;

    public Enemigo(String nombre, Texture... animacion){
        Array<Texture> texturas = new Array<Texture>(animacion);
        neutral = texturas.removeIndex(0);
        this.sprite = new Sprite(neutral);
    }


    public abstract void attack();
    public abstract void idle();

    public enum Estado{

    }

    class Cucaracha extends Enemigo{

        public Cucaracha(String nombre, Texture... animacion){
            super(nombre, animacion);
        }

        @Override
        public void attack() {

        }

        @Override
        public void idle() {

        }
    }

    class Fantasma extends Enemigo{

        public Fantasma(String nombre, Texture... animacion){
            super(nombre, animacion);
        }


        @Override
        public void attack() {

        }

        @Override
        public void idle() {

        }
    }

}
