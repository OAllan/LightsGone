package mx.itesm.lightsgone;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by allanruiz on 22/09/16.
 */
public abstract class Proyectil {
    protected Sprite proyectil;
    protected float x;
    protected final float VELOCIDAD = 20f;
    protected boolean right;
    protected static AssetManager manager = new AssetManager();
    protected float dano;

    public Proyectil(Texture proyectil, float x, float y, boolean right){
        this.proyectil = new Sprite(proyectil);
        this.x = x;
        this.proyectil.setPosition(x, y);
        this.right = right;
        if(right)
            this.proyectil.flip(true, false);

    }

    public void draw(SpriteBatch batch){
        proyectil.draw(batch);
        update();
    }

    public void update(){
        if(right)
            proyectil.translate(VELOCIDAD, 0);
        else
            proyectil.translate(-VELOCIDAD, 0);
    }

    public float getDano(){
        return dano;
    }

    public abstract boolean out();
    public void setX(float x){
        proyectil.setX(x);
    }

    public void setY(float y){
        proyectil.setY(y);
    }

    public Rectangle getRectangle(){
        return proyectil.getBoundingRectangle();
    }

    static class Canica extends Proyectil{
        private static Texture canica;

        static{
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("MunicionResortera.png", Texture.class);
            manager.finishLoading();
            canica = manager.get("MunicionResortera.png", Texture.class);
        }

        public Canica(float x, float y, boolean right){
            super(canica, x, y, right);
            dano = 1;
        }

        public boolean out(){
            if (right)return proyectil.getX() >= x+1000;
            else return proyectil.getX() <= x-1000;
        }

        @Override
        public String toString(){
            return "Canica";
        }
    }

    static class Papa extends Proyectil{
        private static Texture papa;
        private Mapa mapa;

        static{
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("MunicionPapaLanzar.png", Texture.class);
            manager.finishLoading();
            papa = manager.get("MunicionPapaLanzar.png");
        }

        public Papa(float x, float y, boolean right, Mapa mapa){
            super(papa, x, y, right);
            this.mapa = mapa;
            dano = 3;
        }

        @Override
        public boolean out() {
            if(mapa.colisionX(proyectil.getX(), proyectil.getY()))
                return true;
            return right? x + 1000 <= proyectil.getX() : proyectil.getX() <= x - 1000;
        }

        @Override
        public String toString(){
            return "Papa";
        }

    }

    static class Bola extends Proyectil{

        public Bola(Texture texture, float x, float y, boolean right){
            super(texture, x, y,right);
        }

        public Rectangle getRectangle(){
            return proyectil.getBoundingRectangle();
        }

        @Override
        public boolean out() {
            return right? x+1500<proyectil.getX():proyectil.getX()<=x-1500;
        }
    }
}
