package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by allanruiz on 07/10/16.
 */
public abstract class Enemigo  {
    protected Sprite sprite;
    protected float xInicial, yInicial;
    private static AssetManager manager = new AssetManager();

    public Enemigo(Texture texture, float x, float y){
        xInicial = x;
        yInicial = y;
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
    }

    public abstract void attack();
    public abstract void setEstado(Estado estado);
    public abstract void draw(SpriteBatch batch);

    public enum Estado{
        NEUTRAL,
        ATAQUE
    }

    static class Sopa extends Enemigo{
        private static Texture neutral1, neutral2, neutral3, ataque1, ataque2;
        private Estado estado;
        private static Animation neutral, ataque;
        private float timerA, timer;
        private Abner abner;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Sopa(float x, float y, Abner abner){
            super(neutral1, x, y);
            estado= Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
        }


        private static void cargarAnimacion() {
            neutral = new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(neutral2), new TextureRegion(neutral3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
        }

        @Override
        public void attack() {

        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            actualizar();
        }

        private void actualizar() {
            float distancia = sprite.getX() - abner.getX();
            if(distancia>0&& sprite.isFlipX())
                sprite.flip(true, false);
            else if(distancia<0&&!sprite.isFlipX())
                sprite.flip(true, false);

            switch(estado){
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral.getKeyFrame(timer).getTexture());
                    if(timer >5){
                        estado = Estado.ATAQUE;
                        timer = 0;
                    }

                    break;
                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                    if(ataque.getAnimationDuration()<timerA){
                        estado = Estado.NEUTRAL;
                        timerA = 0;
                    }
                    break;

            }
        }

        private static void cargarTexturas(){
            manager.load("SopaAtaque1.png", Texture.class);
            manager.load("SopaAtaque2.png", Texture.class);
            manager.load("SopaNeutral1.png", Texture.class);
            manager.load("SopaNeutral2.png", Texture.class);
            manager.load("SopaNeutral3.png", Texture.class);
            manager.finishLoading();
            neutral1 = manager.get("SopaNeutral1.png");
            ataque1 = manager.get("SopaAtaque1.png");
            ataque2 = manager.get("SopaAtaque2.png");
            neutral2 = manager.get("SopaNeutral2.png");
            neutral3 = manager.get("SopaNeutral3.png");
        }

        @Override
        public String toString() {
            return "Sopa";
        }

    }

    static class Brocoli extends Enemigo{
        private static Texture walk1, walk2, walk3, walk4, neutral1, ataque1, ataque2;
        private static Animation caminar, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            caminar = new Animation(0.2f, new TextureRegion(walk1), new TextureRegion(walk2), new TextureRegion(walk3), new TextureRegion(walk4));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2));
            caminar.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Brocoli(float x, float y, Abner abner){
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
        }

        @Override
        public void attack() {
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            float distancia = sprite.getX()-abner.getX();
            if(Math.abs(distancia)<=500){
                if(distancia>0&&(!sprite.isFlipX()))
                    estado = Estado.ATAQUE;
                else if(distancia<0&&sprite.isFlipX())
                    estado = Estado.ATAQUE;
            }
            else
                estado = Estado.NEUTRAL;
            actualizar();
        }

        private void actualizar() {
            switch (estado){
                case NEUTRAL:
                    caminar.setFrameDuration(0.2f);
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                    if(right) {
                        if(!sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(velocidad, 0);
                        if(sprite.getX()>(xInicial+200))
                            right = false;
                    }
                    else{
                        if(sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(-velocidad, 0);
                        if(sprite.getX()<(xInicial-200))
                            right = true;
                    }
                    break;
                case ATAQUE:
                    if(!ata){
                        caminar.setFrameDuration(0.1f);
                        timer += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                        if(right)
                            sprite.translate(velocidad*2, 0);

                        else
                            sprite.translate(-velocidad*2, 0);

                        if(Math.abs(sprite.getX()-abner.getX())<=10)
                            ata = true;
                    }
                    else {
                        timerA += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                        if(timerA > ataque.getAnimationDuration() && Math.abs(sprite.getX()-abner.getX())>10){
                            timerA = 0;
                            ata = false;
                        }
                    }
                    break;
            }
        }


        private static void cargarTexturas() {
            manager.load("BroccoliAttack1.png", Texture.class);
            manager.load("BroccoliAttack2.png", Texture.class);
            manager.load("BroccoliNeutral.png", Texture.class);
            manager.load("BroccoliWalk1.png", Texture.class);
            manager.load("BroccoliWalk2.png", Texture.class);
            manager.load("BroccoliWalk3.png", Texture.class);
            manager.load("BroccoliWalk4.png", Texture.class);
            manager.finishLoading();
            ataque1 = manager.get("BroccoliAttack1.png");
            ataque2 = manager.get("BroccoliAttack2.png");
            neutral1 = manager.get("BroccoliNeutral.png");
            walk1 = manager.get("BroccoliWalk1.png");
            walk2 = manager.get("BroccoliWalk2.png");
            walk3 = manager.get("BroccoliWalk3.png");
            walk4 = manager.get("BroccoliWalk4.png");
        }

        @Override
        public String toString() {
            return "Brocoli";
        }
    }

}
