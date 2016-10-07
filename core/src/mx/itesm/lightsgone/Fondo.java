package mx.itesm.lightsgone;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Fondo {
    private Sprite sprite;

    public Fondo(Texture fondo){
        sprite = new Sprite(fondo);
    }
    public void draw(SpriteBatch batch){
        sprite.draw(batch);
    }
}
