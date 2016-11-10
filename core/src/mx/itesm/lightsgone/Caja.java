package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by allanruiz on 09/11/16.
 */

public class Caja {

    private Sprite sprite;
    private static AssetManager manager;
    private static Animation rompiendo;
    private static Texture neutral;
    private boolean romper;
    private float timer;
    private boolean rota;

    private static Array<TextureRegion> textureRegions;

    static{
        cargarTexturas();
        cargarAnimacion();
    }



    private static void cargarAnimacion() {
        rompiendo = new Animation(0.05f,textureRegions);
    }

    private static void cargarTexturas() {
        manager = new AssetManager();
        manager.load("caja1.png", Texture.class);
        manager.load("Cexplotando1.png", Texture.class);
        manager.load("cexplotando2.png", Texture.class);
        manager.load("cexplotando3.png", Texture.class);
        manager.load("cexplotando4.png", Texture.class);
        manager.load("cexplotando5.png", Texture.class);
        manager.load("cexplotando6.png", Texture.class);
        manager.finishLoading();
        neutral = manager.get("caja1.png");
        textureRegions = new Array<TextureRegion>();
        textureRegions.add(new TextureRegion((Texture)manager.get("Cexplotando1.png")));
        textureRegions.add(new TextureRegion((Texture)manager.get("cexplotando2.png")));
        textureRegions.add(new TextureRegion((Texture)manager.get("cexplotando3.png")));
        textureRegions.add(new TextureRegion((Texture)manager.get("cexplotando4.png")));
        textureRegions.add(new TextureRegion((Texture)manager.get("cexplotando5.png")));
        textureRegions.add(new TextureRegion((Texture)manager.get("cexplotando6.png")));
    }

    public Caja(float x, float y){
        this.sprite = new Sprite(neutral);
        this.sprite.setPosition(x,y);
        this.romper = false;
        this.rota = false;
    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch);
        actualizar();
    }

    public void romper(){
        this.romper = true;
    }

    public boolean rota(){
        return this.rota;
    }

    public Rectangle getBoundingRectangle(){
        return sprite.getBoundingRectangle();
    }

    public Rectangle getColisionRectangle(){
        return new Rectangle(sprite.getX()+71, sprite.getY(), 220,170);
    }

    private void actualizar() {
        if(romper){
            timer += Gdx.graphics.getDeltaTime();
            sprite.setTexture(rompiendo.getKeyFrame(timer).getTexture());
            if(timer>=rompiendo.getAnimationDuration()){
                rota = true;
            }
        }
    }
}
