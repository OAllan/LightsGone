package mx.itesm.lightsgone;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

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

    public abstract void play();

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
        public void play() {

        }

        @Override
        public String toString(){
            return "Canica";
        }
    }

    static class Papa extends Proyectil{
        private static Texture papa;
        private static Music splat;
        private static Animation estrellado;
        private static Array<TextureRegion> textureRegions;
        private Mapa mapa;
        private float timer;

        static{
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            estrellado = new Animation(0.1f, textureRegions);
            estrellado.setPlayMode(Animation.PlayMode.NORMAL);
        }

        private static void cargarTexturas() {
            manager.load("MunicionPapaLanzar.png", Texture.class);
            manager.load("MunicionPapaColision1.png", Texture.class);
            manager.load("MunicionPapaColision2.png", Texture.class);
            manager.load("MunicionPapaColision3.png", Texture.class);
            manager.load("Splat (papas, disparo de hongo).mp3", Music.class);
            manager.finishLoading();
            papa = manager.get("MunicionPapaLanzar.png");
            textureRegions = new Array<TextureRegion>(3);
            textureRegions.add(new TextureRegion((Texture)manager.get("MunicionPapaColision1.png")));
            textureRegions.add(new TextureRegion((Texture)manager.get("MunicionPapaColision2.png")));
            textureRegions.add(new TextureRegion((Texture)manager.get("MunicionPapaColision3.png")));
            splat = manager.get("Splat (papas, disparo de hongo).mp3");
        }

        public Papa(float x, float y, boolean right, Mapa mapa){
            super(papa, x, y, right);
            this.mapa = mapa;
            dano = 3;
            timer = 0;
        }

        @Override
        public boolean out() {
            if(mapa.colisionX(proyectil.getX(), proyectil.getY())){
                timer+=Gdx.graphics.getDeltaTime();
                proyectil.setTexture(estrellado.getKeyFrame(timer).getTexture());
                if(timer>=0.1f&&timer<=0.1+Gdx.graphics.getDeltaTime()){
                    splat.play();
                }
                if(timer>=estrellado.getAnimationDuration()){
                    timer=0;
                    return true;
                }
            }
            return right? x + 1000 <= proyectil.getX() : proyectil.getX() <= x - 1000;
        }

        @Override
        public void play() {
            splat.play();
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
        public void play() {

        }

        @Override
        public boolean out() {
            return right? x+1500<proyectil.getX():proyectil.getX()<=x-1500;
        }
    }
}
