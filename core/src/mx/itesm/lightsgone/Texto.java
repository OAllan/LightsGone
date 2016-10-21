package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by allanruiz on 20/09/16.
 */
public class Texto {

    private BitmapFont font;
    private float x;
    private float y;

    public Texto(String fontName, float x, float y) {
        font = new BitmapFont(Gdx.files.internal(fontName));
        this.x = x;
        this.y = y;

    }

    public void mostrarMensaje(Batch batch, String mensaje) {
        GlyphLayout glyp = new GlyphLayout();
        glyp.setText(font, mensaje);
        float anchoTexto = glyp.width;
        font.draw(batch,glyp,x-anchoTexto/2,y);
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }
    public float getX(){
        return this.x;
    }


    public void setX(float x) {
        this.x = x;
    }
}
