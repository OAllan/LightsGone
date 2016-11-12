package mx.itesm.lightsgone;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by allanruiz on 01/11/16.
 */
public class CajaMovil {
    private static AssetManager manager;
    private static Texture cajaTex;
    private Sprite caja;
    private Mapa mapa;

    static{
        manager = new AssetManager();
        cargarTexturas();
    }

    private static void cargarTexturas() {
        manager.load("CajaMovilDer.png", Texture.class);
        manager.finishLoading();
        cajaTex = manager.get("CajaMovilDer.png");
    }

    public CajaMovil(float x, float y, Mapa mapa){
        this.caja = new Sprite(cajaTex);
        this.caja.setPosition(x,y);
        this.mapa = mapa;
    }

    public void draw(SpriteBatch batch){
        caja.draw(batch);
    }

    public void mover(float x, float y, boolean right, float velocidad, int index) {
        if(caja.getBoundingRectangle().contains(x,y)){
            if(!mapa.colisionX(caja.getX()+caja.getWidth(), caja.getY())&&!mapa.colisionX(caja.getX(), caja.getY())){
                if(right){
                    caja.translate(velocidad, 0);
                }
                else{
                    caja.translate(-velocidad,0);
                }
            }
        }
        if(!mapa.colisionCaja(caja.getX()+caja.getWidth()/2, caja.getY(), false,index)){
            caer();
        }
    }

    public Rectangle getRectangle(){
        return caja.getBoundingRectangle();
    }
    private void caer() {
        caja.translate(0,-18f);
    }
}
