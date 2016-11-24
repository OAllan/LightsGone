package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by allanruiz on 07/10/16.
 */


public abstract class Enemigo {
    protected Sprite sprite;
    protected float xInicial, yInicial;
    private static AssetManager manager = new AssetManager();
    boolean ataco = false;
    protected boolean muerte;
    long startTime;

    public Enemigo() {

    }

    public Enemigo(Texture texture, float x, float y) {
        xInicial = x;
        yInicial = y;
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);

    }

    public Enemigo(TextureRegion texture, float x, float y) {
        xInicial = x;
        yInicial = y;
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
    }

    public abstract void attack();

    public abstract void setEstado(Estado estado);

    public abstract void draw(SpriteBatch batch);

    public abstract void stop();

    public boolean colisiona(Proyectil p) {
        return sprite.getBoundingRectangle().overlaps(p.getRectangle());
    }

    public abstract boolean muerte();

    public void setPos(float x, float y) {
        sprite.setPosition(x, y);
    }

    public void setMuerte(boolean muerte) {
        this.muerte = muerte;
    }

    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    public void dispose(){
        manager.dispose();
    }

    public enum Estado {
        NEUTRAL,
        ATAQUE,
        DANO,
        ESPERANDO
    }


    static class Sopa extends Enemigo {
        private static Texture neutral1, neutral2, neutral3, ataque1, ataque2, low, mid;
        private Estado estado;
        private static Animation neutral, ataque, dano;
        private float timerA, timer, timerD;
        private Abner abner;
        private Mapa mapa;
        private int vida = 1;
        private float time;
        private Rectangle rec;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Sopa(float x, float y, Abner abner, Mapa mapa) {
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa = mapa;

        }


        private static void cargarAnimacion() {
            neutral = new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(neutral2), new TextureRegion(neutral3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2), new TextureRegion(neutral1));
            dano = new Animation(0.4f, new TextureRegion(mid), new TextureRegion(low));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 5);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            if (Math.abs(abner.getX() - sprite.getX()) < 200) {
                estado = Estado.ATAQUE;
            } else {
                estado = Estado.NEUTRAL;
            }

            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(getRectangle())) {
                if (!ataco && estado == Estado.ATAQUE) {
                    if (time == 0) {
                        attack();
                    }
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }
            if(estado==Estado.ATAQUE){
                rec=sprite.getBoundingRectangle();
            }else{
                if(!sprite.isFlipX()) {
                    rec = new Rectangle(sprite.getX() + 145, sprite.getY(), sprite.getWidth() - 145, sprite.getHeight());
                }else{
                    rec = new Rectangle(sprite.getX() - 290, sprite.getY(), sprite.getWidth() + 145, sprite.getHeight());
                }
            }

            if (!abner.getProyectiles().isEmpty() && sprite.getTexture() != low) {
                if (rec.overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida = 0;
                    abner.borrarProyectiles();
                }
            }


            if (vida <= 0) {
                //sprite.setTexture(low);
                estado = Estado.DANO;
                time += Gdx.graphics.getDeltaTime();
            }

            if ((time >= 2)) {

                estado = Estado.NEUTRAL;
                vida = 1;
                time = 0;
            }


            float distancia = sprite.getX() - abner.getX();
            if (estado != Estado.DANO) {
                if (distancia > 0 && sprite.isFlipX()) {
                    sprite.flip(true, false);

                } else if (distancia < 0 && !sprite.isFlipX())
                    sprite.flip(true, false);


            }


            switch (estado) {
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral.getKeyFrame(timer).getTexture());

                    break;
                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());

                    estado = Estado.NEUTRAL;

                    break;
                case DANO:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(dano.getKeyFrame(timerA).getTexture());

                    break;

            }

        }

        private static void cargarTexturas() {
            manager.load("SopaAtaque1.png", Texture.class);
            manager.load("SopaAtaque2.png", Texture.class);
            manager.load("SopaNeutral1.png", Texture.class);
            manager.load("SopaNeutral2.png", Texture.class);
            manager.load("SopaNeutral3.png", Texture.class);
            manager.load("SopaLow.png", Texture.class);
            manager.load("SopaMid.png", Texture.class);
            manager.finishLoading();

            ataque1 = manager.get("SopaAtaque1.png");
            ataque2 = manager.get("SopaAtaque2.png");
            neutral1 = manager.get("SopaNeutral1.png");
            neutral2 = manager.get("SopaNeutral2.png");
            neutral3 = manager.get("SopaNeutral3.png");
            mid = manager.get("SopaMid.png");
            low = manager.get("SopaLow.png");
        }

        @Override
        public String toString() {
            return "Sopa";
        }


        public Rectangle getRectangle() {
            return new Rectangle(sprite.getX() + 71, sprite.getY(), 262, 153);
        }
    }

    static class Brocoli extends Enemigo {
        private static Texture walk1, walk2, walk3, walk4, neutral1, ataque1, ataque2;
        private static Animation caminar, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private Mapa mapa;


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

        public Brocoli(float x, float y, Abner abner, Mapa mapa) {
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            vida = 3;
            this.mapa = mapa;
        }


        public Rectangle getRectangle() {
            return new Rectangle(sprite.getX() + 60, sprite.getY() + 102, sprite.getWidth(), 120);
        }

        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 5);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            float distancia = sprite.getX() - abner.getX();
            if (estado != Estado.DANO) {
                if (Math.abs(distancia) <= 500) {
                    if (distancia > 0 && (!sprite.isFlipX()))
                        estado = Estado.ATAQUE;
                    else if (distancia < 0 && sprite.isFlipX())
                        estado = Estado.ATAQUE;
                } else
                    estado = Estado.NEUTRAL;
            }
            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {
            //Colisiones con abner
            if (abner.getBoundingRectangle().overlaps(getRectangle())) {
                if (!ataco && estado == Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            //Colisiones con proyectiles
            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if (vida <= 0) {
                AdministradorMapa.quitarEnemigo(this);
            }
            if (sprite.getY() != 1620 && sprite.getY() != 495) {
                if (sprite.getX() >= xInicial + 400)

                    right = false;
                if (sprite.getX() < xInicial - 400)

                    right = true;
            } else {
                if (sprite.getX() >= xInicial + 100)

                    right = false;
                if (sprite.getX() < xInicial - 100)

                    right = true;
            }


            switch (estado) {
                case NEUTRAL:
                    caminar.setFrameDuration(0.2f);
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                    if (right) {
                        if (!sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(velocidad, 0);
                        if (sprite.getX() > (xInicial + 200))
                            right = false;
                    } else {
                        if (sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(-velocidad, 0);
                        if (sprite.getX() < (xInicial - 200))
                            right = true;
                    }
                    break;
                case ATAQUE:
                    if (!ata) {
                        caminar.setFrameDuration(0.1f);
                        timer += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                        if (right) {
                            sprite.translate(velocidad * 2, 0);
                            if (!sprite.isFlipX())
                                sprite.flip(true, false);
                        } else {
                            sprite.translate(-velocidad * 2, 0);
                            if (sprite.isFlipX())
                                sprite.flip(true, false);
                        }
                        if (Math.abs(sprite.getX() - abner.getX()) <= 10)
                            ata = true;
                    } else {
                        timerA += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                        if (timerA > ataque.getAnimationDuration() && Math.abs(sprite.getX() - abner.getX()) > 10) {
                            timerA = 0;
                            ata = false;
                        }
                    }
                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
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

    static class Tostadora extends Enemigo {
        private SpriteBatch batch;
        private static Texture neutraltost, ataquetost1, ataquetost2, ataquetost3, pan;
        private Proyectil proy;
        private Estado estado;
        private static Animation neutral, ataque;
        private float timerA, timer, timerD;
        private Abner abner;
        private Mapa mapa;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Tostadora(float x, float y, Abner abner, Mapa mapa) {
            super(neutraltost, x, y);
            estado = Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            batch = new SpriteBatch();
            this.mapa = mapa;

        }


        private static void cargarAnimacion() {
            neutral = new Animation(0.1f, new TextureRegion(neutraltost));
            ataque = new Animation(0.2f, new TextureRegion(neutraltost), new TextureRegion(ataquetost1), new TextureRegion(ataquetost2), new TextureRegion(ataquetost3));
            ataque.setPlayMode(Animation.PlayMode.LOOP);
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
            float distancia = sprite.getX() - abner.getX();
            if (estado != Estado.DANO) {
                if (Math.abs(distancia) <= 600) {
                    estado = Estado.ATAQUE;
                } else {
                    estado = Estado.NEUTRAL;
                }
            }
            actualizar();
        }

        @Override
        public void stop() {

        }


        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {

            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(neutraltost);
                    break;

                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                    if (ataque.getKeyFrame(timerA).getTexture().equals(ataquetost2)) {

                    }

                    break;
                case DANO:
                    timerD += Gdx.graphics.getDeltaTime();
                    if (timerD > 5) {
                        timerD = 0;
                        estado = Estado.NEUTRAL;
                    }

            }
        }

        private static void cargarTexturas() {

            manager.load("TostadorAtaque1.png", Texture.class);
            manager.load("TostadorAtaque2.png", Texture.class);
            manager.load("TostadorAtaque3.png", Texture.class);
            manager.load("TostadorNeutral.png", Texture.class);
            manager.finishLoading();
            neutraltost = manager.get("TostadorNeutral.png");
            ataquetost1 = manager.get("TostadorAtaque1.png");
            ataquetost2 = manager.get("TostadorAtaque2.png");
            ataquetost3 = manager.get("TostadorAtaque3.png");

        }

        @Override
        public String toString() {
            return "Tostador";
        }

    }

    static class PanTostadora extends Enemigo {
        private static Texture pan;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private String direccion = "arriba";
        private float posicionInicial;

        static {
            cargarTexturas();
        }

        public PanTostadora(float x, float y, Abner abner, Mapa mapa) {
            super(pan, x, y);
            this.abner = abner;
            this.mapa = mapa;
            posicionInicial = sprite.getY();


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 3);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 79, sprite.getY() + 84, sprite.getWidth() - 165, sprite.getHeight() - 114))) {
                if (!ataco) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            direccion = "arriba";

            if (sprite.getY() >= posicionInicial + 1800) {
                sprite.setY(posicionInicial);
            }


            if (direccion.equalsIgnoreCase("arriba"))
                sprite.setY(sprite.getY() + 16);
            if (direccion.equalsIgnoreCase("abajo"))
                sprite.setY(sprite.getY() - 16);


        }

        private static void cargarTexturas() {
            manager.load("Pan.png", Texture.class);


            manager.finishLoading();
            pan = manager.get("Pan.png");


        }

    }

    static class Mosca extends Enemigo {
        private static Texture mosca1, mosca2, mosca3;
        private static Animation caminar, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        private int direccion;
        private float posXOriginal;
        private float posYOriginal;
        private float time;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            caminar = new Animation(0.3f, new TextureRegion(mosca1), new TextureRegion(mosca2), new TextureRegion(mosca3));
            ataque = new Animation(0.001f, new TextureRegion(mosca1), new TextureRegion(mosca2), new TextureRegion(mosca3));
            caminar.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Mosca(float x, float y, Abner abner, Mapa mapa) {
            super(mosca1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            vida = 1;
            this.mapa = mapa;
            posXOriginal = sprite.getX();
            posYOriginal = sprite.getY();
            sprite.setSize(10, 10);
            time = 0;

        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 3);
                ataco = true;
                abner.setDano(true);
            }


        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {

                sprite.draw(batch);
                if (Math.abs(abner.getY() - posYOriginal) < 250) {
                    float distancia = sprite.getX() - abner.getX();
                    if (estado != Estado.DANO) {
                        if (Math.abs(distancia) <= 700) {
                            estado = Estado.ATAQUE;
                            ataq = true;
                        } else if (!ataq)
                            estado = Estado.NEUTRAL;
                        if (ataq && Math.abs(distancia) >= 1000) {

                        }
                    }
                }
                actualizar();

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        public Abner getAbner() {
            return abner;
        }

        public Mapa getMapa() {
            return mapa;
        }

        private void actualizar() {
            if (abner.getX() > posXOriginal) {
                direccion = -1;
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            } else {
                direccion = 1;
                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }

            if (abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())) {
                if (!ataco) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            if (!abner.getProyectiles().isEmpty()) {
                if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida -= 1;
                    LightsGone.agregarItem(this);
                    abner.borrarProyectiles();
                }
            }

            if (Math.abs(abner.getX()-sprite.getX())>1200) {
                sprite.setPosition(20000, 20000);
            }

            if (vida <= 0) {
                sprite.setPosition(20000, 20000);
            }

            if (Math.abs(sprite.getX() - 20000) < 200) {
                time += Gdx.graphics.getDeltaTime();
            }

            if ((time >= 3)) {
                AdministradorMapa.crearNuevaMosca(xInicial, yInicial, this);
                AdministradorMapa.quitarEnemigo(this);
                time = 0;
            }

            switch (estado) {
                case NEUTRAL:
                    sprite.setSize(10, 10);
                    break;
                case ATAQUE:
                    if (tama <= 100)
                        sprite.setSize(tama += 5, tama);
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());

                    sprite.setX(sprite.getX() - 8 * direccion);


                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
            }
        }


        private static void cargarTexturas() {
            manager.load("mosca.png", Texture.class);
            manager.load("mosca2.png", Texture.class);
            manager.load("mosca3.png", Texture.class);

            manager.finishLoading();
            mosca1 = manager.get("mosca.png");
            mosca2 = manager.get("mosca2.png");
            mosca3 = manager.get("mosca3.png");

        }

        @Override
        public String toString() {
            return "mosca";
        }
    }

    static class Fuego extends Enemigo {
        private static Texture fuegoAtaque1, fuegoAtaque2, fuegoAtaque3, fuegoPrepara1, fuegoPrepara2, fuegoTransicion;
        private static Animation neutral, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        long contador = 0;
        long contador1 = 0;
        float posXOriginal;
        int direccion;


        static {
            cargarTexturas();
            cargarAnimacion();
        }


        private static void cargarAnimacion() {
            neutral = new Animation(0.3f, new TextureRegion(fuegoPrepara1), new TextureRegion(fuegoPrepara2), new TextureRegion(fuegoTransicion));
            ataque = new Animation(0.2f, new TextureRegion(fuegoAtaque1), new TextureRegion(fuegoAtaque2), new TextureRegion(fuegoAtaque3));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);


        }

        public Fuego(float x, float y, Abner abner, Mapa mapa) {
            super(fuegoPrepara1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa = mapa;
            vida = 3;
            posXOriginal = sprite.getX();
            //sprite.setSize(250,400);

        }

       /* Timer timerFuego = new Timer (2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                   if(contador%2==0) {
                       estado = Estado.NEUTRAL;
                       contador++;
                   }
                    else {
                       estado = Estado.ATAQUE;
                       contador++;
                   }
            }
        });*/


        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 1);
                abner.setDano(true);
            }

        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            contador++;
            if (contador % 120 == 0) {
                contador1++;
                if (contador1 % 2 == 0) {
                    estado = Estado.ATAQUE;
                } else {
                    estado = Estado.NEUTRAL;
                }
            }
            actualizar();

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return vida <= 0;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 100, sprite.getY(), sprite.getWidth() - 100, sprite.getHeight()))) {
                if (estado == Estado.ATAQUE) {
                    attack();
                    if (abner.getX() > posXOriginal) {
                        direccion = -1;
                    } else {
                        direccion = 1;
                    }
                    abner.setX(abner.getX() - (float) 10 * direccion);
                    abner.ajusteCamara(direccion);
                }
            }

            switch (estado) {
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral.getKeyFrame(timer).getTexture());
                    sprite.setSize(250, 100);
                    break;
                case ATAQUE:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());
                    sprite.setSize(250, 400);

                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
            }
        }


        private static void cargarTexturas() {
            manager.load("FuegoAtaque1.png", Texture.class);
            manager.load("FuegoAtaque2.png", Texture.class);
            manager.load("FuegoAtaque3.png", Texture.class);
            manager.load("FuegoPrepara1.png", Texture.class);
            manager.load("FuegoPrepara2.png", Texture.class);
            manager.load("FuegoTransicion.png", Texture.class);

            manager.finishLoading();
            fuegoAtaque1 = manager.get("FuegoAtaque1.png");
            fuegoAtaque2 = manager.get("FuegoAtaque2.png");
            fuegoAtaque3 = manager.get("FuegoAtaque3.png");
            fuegoPrepara1 = manager.get("FuegoPrepara1.png");
            fuegoPrepara2 = manager.get("FuegoPrepara2.png");
            fuegoTransicion = manager.get("FuegoTransicion.png");

        }

        @Override
        public String toString() {
            return "llamas";
        }
    }

    static class Lata extends Enemigo {
        private static Texture neutral;
        private EstadoLata estadoLata;
        private float alto, alturaOriginal;
        private float MOVX = 7f, rotation = 10f;
        private final float MOVCAIDA = 18f, MOVRODANDO = 0.2125f * MOVX;
        private Mapa mapa;
        private Music rodando;

        static {
            cargarTexturas();
        }

        private float mov;

        private static void cargarTexturas() {
            manager.load("Lata.png", Texture.class);
            manager.load("Rolling.mp3", Music.class);
            manager.finishLoading();
            neutral = manager.get("Lata.png");
        }

        public Lata(float x, float y, Mapa mapa) {
            super(neutral, x, y);
            estadoLata = EstadoLata.CAYENDO;
            alto = alturaOriginal = sprite.getHeight();
            this.mapa = mapa;
            rodando = manager.get("Rolling.mp3");
            rodando.setLooping(true);
        }

        public Lata(float x, float y, Mapa mapa, String string) {
            super(neutral, x, y);
            estadoLata = EstadoLata.RODANDOPISO;
            this.mapa = mapa;
        }

        public Rectangle getBoundingRectangle() {
            if (estadoLata == EstadoLata.RODANDOPISO) {
                return new Rectangle(sprite.getX() + MOVX, sprite.getY(), sprite.getWidth(), sprite.getHeight());
            }
            return sprite.getBoundingRectangle();
        }

        @Override
        public void attack() {

        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            actualizar();
        }

        @Override
        public void stop() {
            if (rodando != null && rodando.isPlaying()) {
                rodando.stop();
            }
        }

        private void actualizar() {
            switch (estadoLata) {
                case CAYENDO:
                    sprite.setRegion(0, 0, (int) sprite.getWidth(), (int) alto);
                    sprite.setSize(sprite.getWidth(), alto);
                    if (mapa.colisionLata(sprite.getX() + sprite.getWidth() / 2, sprite.getY())) {
                        estadoLata = EstadoLata.DESAPARECIENDO;
                        sprite.translate(-40, -30);
                    } else if (!mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + MOVCAIDA)) {
                        sprite.translate(0, -MOVCAIDA);
                    } else {
                        estadoLata = EstadoLata.RODANDO;
                    }
                    break;
                case RODANDO:
                    if (!mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + MOVCAIDA)) {
                        estadoLata = EstadoLata.CAYENDO;
                        if (rodando.isPlaying())
                            rodando.stop();
                    } else {
                        sprite.translate(-MOVX, -MOVRODANDO);
                        sprite.rotate(10);
                        if (!rodando.isPlaying() && LightsGone.musica) {
                            rodando.play();
                        } else if (rodando.isPlaying() && !LightsGone.musica) {
                            rodando.pause();
                        }
                    }
                    break;
                case DESAPARECIENDO:
                    sprite.setRotation(0);
                    alto -= 5;
                    if (alto <= 0) {
                        estadoLata = EstadoLata.CAYENDO;
                        sprite.setPosition(4871, mapa.getHeight());
                        alto = alturaOriginal;
                    }
                    sprite.setRegion(0, 0, (int) sprite.getWidth(), (int) alto);
                    sprite.setSize(sprite.getWidth(), alto);
                    break;
                case RODANDOPISO:
                    sprite.translate(MOVX, 0);
                    sprite.rotate(rotation);
                    if (mapa.colisionX(sprite.getX() + sprite.getWidth(), sprite.getY() + sprite.getHeight()) || mapa.colisionX(sprite.getX(), sprite.getY() + sprite.getHeight())) {
                        MOVX *= -1;
                        rotation *= -1;
                    }
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        public boolean piso() {
            return estadoLata == EstadoLata.RODANDOPISO;
        }

        public float getMov() {
            return MOVX;
        }

        private enum EstadoLata {
            CAYENDO,
            RODANDO,
            DESAPARECIENDO,
            RODANDOPISO
        }

        @Override
        public String toString() {
            return "Lata";
        }
    }

    static class Serpiente extends Enemigo {
        private static Texture walk1, walk2, walk3, ataque1, ataque2;
        private static Animation caminar, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private Mapa mapa;
        private Rectangle rec;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            caminar = new Animation(0.2f, new TextureRegion(walk1), new TextureRegion(walk2), new TextureRegion(walk3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2));
            caminar.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Serpiente(float x, float y, Abner abner, Mapa mapa) {
            super(walk1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            vida = 3;
            this.mapa = mapa;
        }


        public Rectangle getRectangle() {
            return rec;
        }

        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 8);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            float distancia = sprite.getX() - abner.getX();
            if (estado != Estado.DANO) {
                if (Math.abs(distancia) <= 500) {
                    if (distancia > 0 && (!sprite.isFlipX()))
                        estado = Estado.ATAQUE;
                    else if (distancia < 0 && sprite.isFlipX())
                        estado = Estado.ATAQUE;
                } else
                    estado = Estado.NEUTRAL;
            }
            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {
            rec=new Rectangle(sprite.getX()+47,sprite.getY(),sprite.getWidth()-86,sprite.getHeight());

            //Colisiones con abner
            if (abner.getBoundingRectangle().overlaps(getRectangle())) {
                if (ataco == false && estado == Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            //Colisiones con proyectiles
            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if (vida <= 0) {
                xInicial = sprite.getX() + sprite.getWidth() / 2;
                yInicial = sprite.getY();
                muerte = true;
                AdministradorMapa.quitarEnemigo(this);
            }
            if (sprite.getY() != 1620 && sprite.getY() != 495) {
                if (sprite.getX() >= xInicial + 400)

                    right = false;
                if (sprite.getX() < xInicial - 400)

                    right = true;
            } else {
                if (sprite.getX() >= xInicial + 100)

                    right = false;
                if (sprite.getX() < xInicial - 100)

                    right = true;
            }


            switch (estado) {
                case NEUTRAL:
                    caminar.setFrameDuration(0.2f);
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                    if (right) {
                        if (!sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(velocidad, 0);
                        if (sprite.getX() > (xInicial + 200))
                            right = false;
                    } else {
                        if (sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(-velocidad, 0);
                        if (sprite.getX() < (xInicial - 200))
                            right = true;
                    }
                    break;
                case ATAQUE:
                    if (!ata) {
                        caminar.setFrameDuration(0.1f);
                        timer += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                        if (right) {
                            sprite.translate(velocidad * 2, 0);
                            if (!sprite.isFlipX())
                                sprite.flip(true, false);
                        } else {
                            sprite.translate(-velocidad * 2, 0);
                            if (sprite.isFlipX())
                                sprite.flip(true, false);
                        }
                        if (Math.abs(sprite.getX() - abner.getX()) <= 10)
                            ata = true;
                    } else {
                        timerA += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                        if (timerA > ataque.getAnimationDuration() && Math.abs(sprite.getX() - abner.getX()) > 10) {
                            timerA = 0;
                            ata = false;
                        }
                    }
                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
            }

        }


        private static void cargarTexturas() {
            manager.load("manguera mordida 1.png", Texture.class);
            manager.load("manguera mordida 2.png", Texture.class);
            manager.load("manguera neutral.png", Texture.class);
            manager.load("manguera neutral 2.png", Texture.class);
            manager.load("manguera neutral 3.png", Texture.class);
            manager.finishLoading();
            ataque1 = manager.get("manguera mordida 1.png");
            ataque2 = manager.get("manguera mordida 2.png");
            walk1 = manager.get("manguera neutral.png");
            walk2 = manager.get("manguera neutral 2.png");
            walk3 = manager.get("manguera neutral 3.png");

        }

        @Override
        public String toString() {
            return "Manguera";
        }
    }

    static class Hongo extends Enemigo {
        private SpriteBatch batch;
        private static Texture neutralH, ataque1, ataque2;
        private Proyectil proy;
        private Estado estado;
        private int vida;
        private static Animation ataque;
        private float timerA, timer, timerD;
        private Abner abner;
        private Mapa mapa;
        private Rectangle rec;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Hongo(float x, float y, Abner abner, Mapa mapa) {
            super(neutralH, x, y);
            estado = Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            vida = 4;
            batch = new SpriteBatch();
            this.mapa = mapa;

        }


        private static void cargarAnimacion() {
            ataque = new Animation(0.4f, new TextureRegion(ataque1), new TextureRegion(ataque2));
            //ataque.setPlayMode(Animation.PlayMode.LOOP);
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
            float distancia = sprite.getX() - abner.getX();
            if (estado != Estado.DANO) {
                if (Math.abs(distancia) <= 600) {
                    estado = Estado.ATAQUE;
                } else {
                    estado = Estado.NEUTRAL;
                }

            }
            actualizar();
        }

        @Override
        public void stop() {

        }


        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            rec=new Rectangle(sprite.getX()+32,sprite.getY(),sprite.getWidth()-32,sprite.getHeight());

            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (rec.overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (rec.overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if (vida <= 0) {
                muerte = true;
                AdministradorMapa.quitarEnemigo(this);
            }

            if (abner.getX() > sprite.getX()) {
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            } else {

                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }

            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(neutralH);
                    break;

                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                    if (ataque.getKeyFrame(timerA).getTexture().equals(ataque)) {
                        estado = Estado.NEUTRAL;
                    }
                    break;
                case DANO:
                    timerD += Gdx.graphics.getDeltaTime();
                    if (timerD > 5) {
                        timerD = 0;
                        estado = Estado.NEUTRAL;
                    }

            }
        }

        private static void cargarTexturas() {

            manager.load("hongo neutral.png", Texture.class);
            manager.load("hongo ataque 1.png", Texture.class);
            manager.load("hongo ataque 2.png", Texture.class);

            manager.finishLoading();
            neutralH = manager.get("hongo neutral.png");
            ataque1 = manager.get("hongo ataque 1.png");
            ataque2 = manager.get("hongo ataque 2.png");


        }

        @Override
        public String toString() {
            return "Hongo";
        }

    }

    static class ProyectilHongo extends Enemigo {
        private static Texture pan;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private int direccion;
        private float posicionInicial;
        private float posXOriginal;
        private float posYOriginal;
        private Estado estado;
        private Color color;
        private boolean disparado = false;


        static {
            cargarTexturas();
        }

        public ProyectilHongo(float x, float y, Abner abner, Mapa mapa) {
            super(pan, x, y);
            this.abner = abner;
            this.mapa = mapa;
            posicionInicial = sprite.getY();
            posXOriginal = sprite.getX();
            posYOriginal = sprite.getY();
            color = sprite.getColor();


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 6);
                ataco = true;
                abner.setDano(true);
            }
        }


        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);


            actualizar();
        }

        @Override
        public void stop() {

        }


        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 40, sprite.getY() + 20, sprite.getWidth() - 80, sprite.getHeight() - 40))) {
                if (ataco == false && estado == Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            if (disparado == false) {
                if (abner.getX() > posXOriginal) {
                    direccion = -1;
                    if (!sprite.isFlipX()) {
                        sprite.flip(true, false);
                    }
                } else {
                    direccion = 1;
                    if (sprite.isFlipX()) {
                        sprite.flip(true, false);
                    }
                }
            }

            if (Math.abs(abner.getX() - posXOriginal) < 660) {
                estado = Estado.ATAQUE;

            }

            if (estado == Estado.ATAQUE) {
                sprite.setX(sprite.getX() - 8 * direccion);
                disparado = true;
            }

            if (Math.abs(abner.getX() - posXOriginal) > 660 && Math.abs(sprite.getX() - posXOriginal) < 60) {
                estado = Estado.NEUTRAL;
                //sprite.setX(posXOriginal);

            }

            if (Math.abs(sprite.getX() - posXOriginal) > 70) {
                sprite.setColor(color);

            } else {
                sprite.setColor(0);
            }

            if (Math.abs(sprite.getX() - posXOriginal) > 660) {
                sprite.setColor(0);
                sprite.setX(posXOriginal);
                disparado = false;
            }


        }

        private static void cargarTexturas() {
            manager.load("proyectil venenoso.png", Texture.class);


            manager.finishLoading();
            pan = manager.get("proyectil venenoso.png");


        }

        @Override
        public String toString() {
            return "proyectil venenoso";
        }


    }

    static class Gnomo extends Enemigo {
        private static Texture neutralG, advertencia, lanzamiento, caida, muerte;
        private static Animation advertenciaA, muerteG;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private String direccion = "arriba";
        private float posicionInicial;
        private Estado estado;
        private float timer, timerA;
        private int vida = 1;
        private boolean lanzado = false;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            advertenciaA = new Animation(0.3f, new TextureRegion(neutralG), new TextureRegion(advertencia));
            muerteG = new Animation(0.3f, new TextureRegion(muerte));
            advertenciaA.setPlayMode(Animation.PlayMode.LOOP);
            muerteG.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Gnomo(float x, float y, Abner abner, Mapa mapa) {
            super(neutralG, x, y);
            this.abner = abner;
            this.mapa = mapa;
            posicionInicial = sprite.getY();
            timer = timerA = 0;
            estado = Estado.NEUTRAL;


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 8);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle()) && timerA == 0) {
                if (ataco == false) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if (vida <= 0) {
                //muerte = true;
                AdministradorMapa.quitarEnemigo(this);
            }

            if (Math.abs(abner.getX() - sprite.getX()) <= 700 && Math.abs(abner.getX() - sprite.getX()) > 600 && lanzado == false) {
                timer += Gdx.graphics.getDeltaTime();
                sprite.setTexture(advertenciaA.getKeyFrame(timer).getTexture());


            }

            if (Math.abs(abner.getX() - sprite.getX()) <= 600) {
                estado = Estado.ATAQUE;
            }


            if (sprite.getY() >= posicionInicial + 1300) {
                direccion = "abajo";
                sprite.setTexture(caida);
                sprite.setX(abner.getX());
            }
            if (sprite.getY() < posicionInicial && lanzado == true) {
                timer += Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer * 2).getTexture());
                //sprite.setTexture(muerte);

            }

            if (timerA > 1) {
                AdministradorMapa.quitarEnemigo(this);
            }

            if (estado == Estado.ATAQUE) {
                if (direccion == "arriba") {
                    sprite.setTexture(lanzamiento);
                    sprite.setY(sprite.getY() + 16);

                }
                if (direccion == "abajo" && timerA == 0) {
                    sprite.setY(sprite.getY() - 16);
                    lanzado = true;
                }
            }

        }

        private static void cargarTexturas() {
            manager.load("gnomo1.png", Texture.class);
            manager.load("gnomo2.png", Texture.class);
            manager.load("gnomo3.png", Texture.class);
            manager.load("gnomo4.png", Texture.class);
            manager.load("muerte.png", Texture.class);


            manager.finishLoading();
            neutralG = manager.get("gnomo1.png");
            advertencia = manager.get("gnomo2.png");
            lanzamiento = manager.get("gnomo3.png");
            caida = manager.get("gnomo4.png");
            muerte = manager.get("muerte.png");

        }

        @Override
        public String toString() {
            return "Gnomo";
        }


    }

    static class GnomoL extends Enemigo {
        private static Texture neutralG, advertencia, lanzamiento, caida, muerte;
        private static Animation advertenciaA, muerteG;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private String direccion = "arriba";
        private float posicionInicial;
        private Estado estado;
        private float timer, timerA;
        private int vida = 1;
        private boolean lanzado = false;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            advertenciaA = new Animation(0.3f, new TextureRegion(neutralG), new TextureRegion(advertencia));
            muerteG = new Animation(0.3f, new TextureRegion(muerte));
            advertenciaA.setPlayMode(Animation.PlayMode.LOOP);
            muerteG.setPlayMode(Animation.PlayMode.LOOP);

        }

        public GnomoL(float x, float y, Abner abner, Mapa mapa) {
            super(lanzamiento, x, y);
            this.abner = abner;
            this.mapa = mapa;
            posicionInicial = sprite.getY();
            timer = timerA = 0;
            estado = Estado.NEUTRAL;


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 8);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle()) && timerA == 0) {
                if (ataco == false) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if (vida <= 0) {
                //muerte = true;
                AdministradorMapa.quitarEnemigo(this);
            }


            if (!sprite.isFlipY()) {
                sprite.flip(false, true);
            }


            if (abner.getX() > 19750 && abner.getX() < 20610) {
                estado = Estado.ATAQUE;

            }


            if (sprite.getY() < abner.getY() && lanzado == true && !abner.isJumping()) {
                timer += Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer * 2).getTexture());
                if (sprite.isFlipY())
                    sprite.flip(false, true);
                //sprite.setTexture(muerte);

            }
            if (sprite.getY() < abner.getY() - Abner.SALTOMAX && lanzado == true && abner.isJumping()) {
                timer += Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer * 2).getTexture());
                if (sprite.isFlipY())
                    sprite.flip(false, true);
                //sprite.setTexture(muerte);

            }

            if (timerA > 1) {
                //AdministradorMapa.invocarLluviaDeGnomos(abner,mapa);
                AdministradorMapa.quitarEnemigo(this);

            }

            if (estado == Estado.ATAQUE && timerA == 0) {


                sprite.setY(sprite.getY() - 16);
                lanzado = true;

            }

        }

        private static void cargarTexturas() {
            manager.load("gnomo1.png", Texture.class);
            manager.load("gnomo2.png", Texture.class);
            manager.load("gnomo3.png", Texture.class);
            manager.load("gnomo4.png", Texture.class);
            manager.load("muerte.png", Texture.class);


            manager.finishLoading();
            neutralG = manager.get("gnomo1.png");
            advertencia = manager.get("gnomo2.png");
            lanzamiento = manager.get("gnomo3.png");
            caida = manager.get("gnomo4.png");
            muerte = manager.get("muerte.png");

        }

        @Override
        public String toString() {
            return "Gnomo";
        }


    }

    static class Cucarachon extends Enemigo {
        private static Texture neutral1, ataque1, ataque2;
        private static Animation ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private Mapa mapa;
        private int direccion;
        private float xInicial;
        private float time;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {

            ataque = new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(ataque1), new TextureRegion(ataque2));

            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Cucarachon(float x, float y, Abner abner, Mapa mapa) {
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            vida = 3;
            this.mapa = mapa;
            xInicial = sprite.getX();
        }


        public Rectangle getRectangle() {
            return new Rectangle(sprite.getX() + 30, sprite.getY(), sprite.getWidth() - 60, sprite.getHeight());
        }

        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 5);
                ataco = true;
                abner.setDano(true);
            }
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

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(getRectangle())) {
                if (ataco == false && estado == Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }

            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        //vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }
            }

            if (vida <= 0) {

                muerte = true;
                ///sprite.setPosition(20000,20000);
                AdministradorMapa.quitarEnemigo(this);
            }

            if (abner.getX() > sprite.getX() && Math.abs(abner.getX() - sprite.getX()) > 400) {
                direccion = -1;
                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }

            if (abner.getX() <= sprite.getX() && Math.abs(abner.getX() - sprite.getX()) > 400) {
                direccion = 1;
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }


            if (Math.abs(sprite.getX() - abner.getX()) <= 1400 && Math.abs(sprite.getY() - abner.getY()) < 400) {
                estado = Estado.ATAQUE;
                time += Gdx.graphics.getDeltaTime();
            } else {
                estado = Estado.NEUTRAL;
            }

            if (sprite.getX() < xInicial) {
                sprite.setX(sprite.getX() + 1);
            }


            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(neutral1);
                    break;
                case ATAQUE:

                    if (time >= 1) {
                        timer += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(ataque.getKeyFrame(timer).getTexture());

                        if (sprite.getX() <= xInicial + 4200 && sprite.getX() >= xInicial) {
                            sprite.setX(sprite.getX() - 8 * direccion);
                        }
                    }
                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
            }

        }


        private static void cargarTexturas() {
            manager.load("CucarachaNeutro.png", Texture.class);
            manager.load("CucarachaPaso1.png", Texture.class);
            manager.load("CucarachaPaso2.png", Texture.class);

            manager.finishLoading();
            ataque1 = manager.get("CucarachaPaso1.png");
            ataque2 = manager.get("CucarachaPaso2.png");
            neutral1 = manager.get("CucarachaNeutro.png");

        }

        @Override
        public String toString() {
            return "Cucarachon";
        }
    }

    static class Scarecrow extends Enemigo {
        private static Texture scarecrow1, scarecrow2, scarecrow3;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        long contador = 0;
        long contador1 = 0;
        float posXOriginal;
        int direccion;


        static {
            cargarTexturas();

        }


        public Scarecrow(float x, float y, Abner abner, Mapa mapa) {
            super(scarecrow1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa = mapa;
            vida = 3;
            posXOriginal = sprite.getX();
            //sprite.setSize(250,400);

        }

       /* Timer timerFuego = new Timer (2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                   if(contador%2==0) {
                       estado = Estado.NEUTRAL;
                       contador++;
                   }
                    else {
                       estado = Estado.ATAQUE;
                       contador++;
                   }
            }
        });*/


        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 1);
                abner.setDano(true);
            }

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

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return vida <= 0;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 100, sprite.getY(), sprite.getWidth() - 100, sprite.getHeight()))) {
                if (estado == Estado.ATAQUE) {
                    attack();
                    if (abner.getX() > posXOriginal) {
                        direccion = -1;
                    } else {
                        direccion = 1;
                    }
                    abner.setX(abner.getX() - (float) 10 * direccion);
                    abner.ajusteCamara(direccion);
                }
            }

            if (abner.getX() > sprite.getX()) {
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            } else {

                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }

            if (Math.abs(abner.getX() - sprite.getX()) > 400)
                estado = Estado.NEUTRAL;
            if (Math.abs(abner.getX() - sprite.getX()) <= 400 && Math.abs(abner.getX() - sprite.getX()) >= 200)
                estado = Estado.DANO;
            if (Math.abs(abner.getX() - sprite.getX()) < 200)
                estado = Estado.ATAQUE;

            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(scarecrow1);
                    break;
                case ATAQUE:
                    sprite.setTexture(scarecrow3);
                    break;
                case DANO:
                    sprite.setTexture(scarecrow2);
                    break;
            }
        }


        private static void cargarTexturas() {
            manager.load("scarecrow.png", Texture.class);
            manager.load("scarecrow1.png", Texture.class);
            manager.load("scarecrow2.png", Texture.class);


            manager.finishLoading();
            scarecrow1 = manager.get("scarecrow.png");
            scarecrow2 = manager.get("scarecrow1.png");
            scarecrow3 = manager.get("scarecrow2.png");


        }

        @Override
        public String toString() {
            return "scarecrow";
        }
    }

    static class Espinas extends Enemigo {
        private static Texture neutral, ataque;
        private Estado estado;
        private boolean bandera = true;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private float time;

        private boolean ataq = false;
        private Mapa mapa;

        long contador1 = 0;
        float posXOriginal;
        int direccion;
        private long contador = 0;


        static {
            cargarTexturas();

        }


        public Espinas(float x, float y, Abner abner, Mapa mapa) {
            super(neutral, x, y);
            estado = Estado.NEUTRAL;
            this.abner = abner;
            this.mapa = mapa;
            posXOriginal = sprite.getX();


        }


        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 1);
                abner.setDano(true);
            }

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

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return vida <= 0;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 100, sprite.getY(), sprite.getWidth() - 100, sprite.getHeight()))) {
                if (estado == Estado.ATAQUE) {
                    attack();
                    if (abner.getX() > posXOriginal) {
                        direccion = -1;
                    } else {
                        direccion = 1;
                    }
                    abner.setX(abner.getX() - (float) 10 * direccion);
                    abner.ajusteCamara(direccion);
                }
            }


            if (abner.getX() > 19750 && abner.getX() < 20610 && bandera == true) {
                estado = Estado.ATAQUE;


            }


            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    break;
                case ATAQUE:
                    // if(time>2) {
                    sprite.setTexture(ataque);
                    contador++;
                    if (contador % 120 == 0)
                        AdministradorMapa.invocarLluviaDeGnomos(abner, mapa);
                    if (contador % 600 == 0) {
                        bandera = false;
                        estado = Estado.NEUTRAL;
                    }
                    //}
                    break;
            }


        }


        private static void cargarTexturas() {
            manager.load("TierraEspinasUAdelante.png", Texture.class);
            manager.load("EspinasU.png", Texture.class);


            manager.finishLoading();
            neutral = manager.get("TierraEspinasUAdelante.png");
            ataque = manager.get("EspinasU.png");


        }

        @Override
        public String toString() {
            return "espinas";
        }

    }

    static class EspinasD extends Enemigo {
        private static Texture neutral, ataque;
        private Estado estado;
        private boolean bandera = true;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private float time;

        private boolean ataq = false;
        private Mapa mapa;

        long contador1 = 0;
        float posXOriginal;
        int direccion;
        private long contador = 0;


        static {
            cargarTexturas();

        }


        public EspinasD(float x, float y, Abner abner, Mapa mapa) {
            super(neutral, x, y);
            estado = Estado.NEUTRAL;
            this.abner = abner;
            this.mapa = mapa;
            posXOriginal = sprite.getX();


        }


        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 1);
                abner.setDano(true);
            }

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

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return vida <= 0;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 100, sprite.getY(), sprite.getWidth() - 100, sprite.getHeight()))) {
                if (estado == Estado.ATAQUE) {
                    attack();
                    if (abner.getX() > posXOriginal) {
                        direccion = -1;
                    } else {
                        direccion = 1;
                    }
                    abner.setX(abner.getX() - (float) 10 * direccion);
                    abner.ajusteCamara(direccion);
                }
            }
            if (!sprite.isFlipY()) {
                sprite.flip(false, true);
            }

            time += Gdx.graphics.getDeltaTime();

            if (time > 3) {
                if (estado == Estado.ATAQUE) {
                    estado = Estado.NEUTRAL;
                } else {
                    estado = Estado.ATAQUE;
                }

                time = 0;
            }


            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    break;
                case ATAQUE:
                    // if(time>2) {
                    sprite.setTexture(ataque);
                    contador++;
                    if (contador % 600 == 0) {
                        bandera = false;
                        estado = Estado.NEUTRAL;
                    }
                    //}
                    break;
            }


        }


        private static void cargarTexturas() {
            manager.load("TierraEspinasUAdelante.png", Texture.class);
            manager.load("EspinasU.png", Texture.class);


            manager.finishLoading();
            neutral = manager.get("TierraEspinasUAdelante.png");
            ataque = manager.get("EspinasU.png");


        }

        @Override
        public String toString() {
            return "espinas";
        }

    }

    static class Sandwich extends Enemigo {
        private static Texture walk1, walk2, walk3, neutral1, ataque1;
        private static Animation caminar, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private Mapa mapa;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            caminar = new Animation(0.2f, new TextureRegion(walk1), new TextureRegion(walk2), new TextureRegion(walk3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1),new TextureRegion(neutral1));
            caminar.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Sandwich(float x, float y, Abner abner, Mapa mapa){
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            vida = 3;
            this.mapa=mapa;
        }


        public Rectangle getRectangle(){
            return new Rectangle(sprite.getX()+30,sprite.getY(),sprite.getWidth()-60,sprite.getHeight());
        }

        @Override
        public void attack() {

            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-5);
                ataco=true;
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            float distancia = sprite.getX()-abner.getX();
            if(estado!= Estado.DANO){
                if(Math.abs(distancia)<=500){
                    if(distancia>0&&(!sprite.isFlipX()))
                        estado = Estado.ATAQUE;
                    else if(distancia<0&&sprite.isFlipX())
                        estado = Estado.ATAQUE;
                }
                else
                    estado = Estado.NEUTRAL;
            }
            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(getRectangle())){
                if(ataco==false && estado== Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }
            //Colisiones con proyectiles
            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                AdministradorMapa.quitarEnemigo(this);
                xInicial = sprite.getX()+sprite.getWidth()/2;
                yInicial = sprite.getY();
                muerte = true;
                ///sprite.setPosition(20000,20000);

            }
            if(sprite.getY()!=1620 && sprite.getY()!=495) {
                if (sprite.getX() >= xInicial + 400)

                    right = false;
                if (sprite.getX() < xInicial - 400)

                    right = true;
            }
            else{
                if (sprite.getX() >= xInicial + 100)

                    right = false;
                if (sprite.getX() < xInicial - 100)

                    right = true;
            }


            switch (estado) {
                case NEUTRAL:
                    caminar.setFrameDuration(0.2f);
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                    if (right) {
                        if (!sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(velocidad, 0);
                        if (sprite.getX() > (xInicial + 200))
                            right = false;
                    } else {
                        if (sprite.isFlipX())
                            sprite.flip(true, false);
                        sprite.translate(-velocidad, 0);
                        if (sprite.getX() < (xInicial - 200))
                            right = true;
                    }
                    break;
                case ATAQUE:
                    if (!ata) {
                        caminar.setFrameDuration(0.1f);
                        timer += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                        if (right) {
                            sprite.translate(velocidad * 2, 0);
                            if(!sprite.isFlipX())
                                sprite.flip(true, false);
                        }
                        else {
                            sprite.translate(-velocidad * 2, 0);
                            if(sprite.isFlipX())
                                sprite.flip(true, false);
                        }
                        if (Math.abs(sprite.getX() - abner.getX()) <= 10)
                            ata = true;
                    } else {
                        timerA += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                        if (timerA > ataque.getAnimationDuration() && Math.abs(sprite.getX() - abner.getX()) > 10) {
                            timerA = 0;
                            ata = false;
                        }
                    }
                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
            }

        }


        private static void cargarTexturas() {
            manager.load("SandwichAttack.png", Texture.class);
            manager.load("SandwichNeutral.png", Texture.class);
            manager.load("SandwichWalk1.png", Texture.class);
            manager.load("SandwichWalk2.png", Texture.class);
            manager.load("SandwichWalk3.png", Texture.class);

            manager.finishLoading();
            ataque1 = manager.get("SandwichAttack.png");
            neutral1 = manager.get("SandwichNeutral.png");
            walk1 = manager.get("SandwichWalk1.png");
            walk2 = manager.get("SandwichWalk2.png");
            walk3 = manager.get("SandwichWalk3.png");

        }

        @Override
        public String toString() {
            return "Sandwich";
        }
    }

    static class PlantaCarnivora extends Enemigo {
        private static Texture neutral2, neutral1, ataque1, ataque2;
        private static Animation neutral, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private Mapa mapa;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            neutral = new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(neutral2));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2),new TextureRegion(neutral2));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public PlantaCarnivora(float x, float y, Abner abner, Mapa mapa){
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            vida = 3;
            this.mapa=mapa;
        }


        public Rectangle getRectangle(){
            return new Rectangle(sprite.getX()+30,sprite.getY(),sprite.getWidth()-60,sprite.getHeight());
        }

        @Override
        public void attack() {

            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-5);
                ataco=true;
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            float distancia = sprite.getX()-abner.getX();
            if(estado!= Estado.DANO) {
                if (Math.abs(distancia) <= 200) {
                    estado = Estado.ATAQUE;
                }
                else{ estado= Estado.NEUTRAL;}

            }

            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(getRectangle())){
                if(!ataco && estado== Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            //Colisiones con proyectiles
            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                } else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                AdministradorMapa.quitarEnemigo(this);
                xInicial = sprite.getX()+sprite.getWidth()/2;
                yInicial = sprite.getY();
                muerte = true;
                ///sprite.setPosition(20000,20000);

            }

            if(abner.getX()>sprite.getX()){
                if(!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }
            else {

                if(sprite.isFlipX()){
                    sprite.flip(true,false);
                }
            }

            switch (estado) {
                case NEUTRAL:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral.getKeyFrame(timerA).getTexture());

                    break;
                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                    if(ataque.getKeyFrame(timerA).getTexture().equals(ataque)){
                        estado= Estado.NEUTRAL;
                    }


                    break;
                case DANO:
                    Gdx.app.log("Vida", vida + "");
                    vida -= 1;
                    estado = Estado.NEUTRAL;
            }

        }


        private static void cargarTexturas() {
            manager.load("plantaneutral.png", Texture.class);
            manager.load("plantaneutralbalanceo.png", Texture.class);
            manager.load("plantamordida1.png", Texture.class);
            manager.load("plantamordida2.png", Texture.class);
            manager.finishLoading();
            ataque1 = manager.get("plantamordida1.png");
            ataque2 = manager.get("plantamordida2.png");
            neutral1 = manager.get("plantaneutral.png");
            neutral2 = manager.get("plantaneutralbalanceo.png");

        }

        @Override
        public String toString() {
            return "PlantaCarnivora";
        }
    }



    private static class CajaPayaso extends Enemigo {
        public static final int ANCHOCAJA = 223;
        private Abner abner;
        private Mapa mapa;
        private static Texture neutral;
        private EstadoCaja estadoCaja;
        private float timer;
        private Music sonidoCaja, risaPayaso, explosion;
        private static Animation cajaMovil, cajaAtaque, cajaPayaso;
        private static TextureRegion[] cajaMovilTex, cajaAtaqueTex, cajaPayasoTex;

        static {
            cargarTexturas();
            cargarAnimaciones();
        }

        private static void cargarAnimaciones() {
            cajaMovil = new Animation(0.22f, cajaMovilTex);
            cajaMovil.setPlayMode(Animation.PlayMode.LOOP);
            cajaAtaque = new Animation(0.1f, cajaAtaqueTex);
            cajaAtaque.setPlayMode(Animation.PlayMode.NORMAL);
            cajaPayaso = new Animation(0.1f, cajaPayasoTex);
        }

        private static void cargarTexturas() {
            manager.load("cajapayaso1.png", Texture.class);
            manager.load("cajapayaso2.png", Texture.class);
            manager.load("cajapayaso3.png", Texture.class);
            manager.load("Cajapayaso4.png", Texture.class);
            manager.load("cajapayasosaliendo1.png", Texture.class);
            manager.load("cajapayasosaliendo2-min.png", Texture.class);
            manager.load("cajapayasosaliendo3.png", Texture.class);
            manager.load("cajapayasosaliendo4.png", Texture.class);
            manager.load("payasoexplotando1.png", Texture.class);
            manager.load("payasoexplotando2.png", Texture.class);
            manager.load("payasoexplotando4.png", Texture.class);
            manager.load("payasoexplotando5.png", Texture.class);
            manager.load("payasoexplotando6.png", Texture.class);
            manager.load("Music Box.mp3", Music.class);
            manager.load("risa de payaso.mp3", Music.class);
            manager.load("explosion .mp3", Music.class);
            manager.finishLoading();
            neutral = manager.get("cajapayaso1.png");
            cajaMovilTex = new TextureRegion[]{new TextureRegion((Texture) manager.get("cajapayaso1.png")), new TextureRegion((Texture) manager.get("cajapayaso2.png")),
                    new TextureRegion((Texture) manager.get("cajapayaso3.png")), new TextureRegion((Texture) manager.get("Cajapayaso4.png"))};
            cajaPayasoTex = new TextureRegion[]{new TextureRegion((Texture) manager.get("cajapayasosaliendo1.png")), new TextureRegion((Texture) manager.get("cajapayasosaliendo2-min.png")),
                    new TextureRegion((Texture) manager.get("cajapayasosaliendo3.png")), new TextureRegion((Texture) manager.get("cajapayasosaliendo4.png"))
            };
            cajaAtaqueTex = new TextureRegion[]{
                    new TextureRegion((Texture) manager.get("payasoexplotando1.png")),
                    new TextureRegion((Texture) manager.get("payasoexplotando2.png")), new TextureRegion((Texture) manager.get("payasoexplotando4.png")), new TextureRegion((Texture) manager.get("payasoexplotando5.png")),
                    new TextureRegion((Texture) manager.get("payasoexplotando6.png"))
            };


        }

        public CajaPayaso(float x, float y, Abner abner, Mapa mapa) {
            super(neutral, x, y);
            this.mapa = mapa;
            this.abner = abner;
            this.estadoCaja = EstadoCaja.NEUTRAL;
            timer = 0;
            sonidoCaja = manager.get("Music Box.mp3");
            risaPayaso = manager.get("risa de payaso.mp3");
            explosion = manager.get("explosion .mp3");
        }

        @Override
        public void attack() {
            if (abner.getBoundingRectangle().overlaps(getAttackRectangle()) && isAttacking()) {
                if (!abner.getDano()&&!LightsGone.getCapa()) {
                    abner.setDano(true);
                    abner.setCantVida(abner.getcantVida() - 10);
                }
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            actualizar();
        }

        @Override
        public void stop() {
            if (sonidoCaja.isPlaying()) {
                sonidoCaja.stop();
            }
            if (risaPayaso.isPlaying()) {
                risaPayaso.stop();
            }
            if (explosion.isPlaying()) {
                explosion.stop();
            }
        }

        private void actualizar() {
            switch (estadoCaja) {
                case NEUTRAL:
                    if (Math.abs(abner.getX() - sprite.getX()) <= 530) {
                        estadoCaja = EstadoCaja.CAJAMOVIL;
                    }
                    break;
                case CAJAMOVIL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(cajaMovil.getKeyFrame(timer).getTexture());
                    if (!sonidoCaja.isPlaying()&&LightsGone.musica) {
                        sonidoCaja.play();
                    }
                    if (timer >= 5) {
                        estadoCaja = EstadoCaja.PAYASOFUERA;
                        timer = 0;
                        sonidoCaja.stop();
                    }
                    break;
                case PAYASOFUERA:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(cajaPayaso.getKeyFrame(timer).getTexture());
                    if (timer >= cajaPayaso.getAnimationDuration()) {
                        estadoCaja = EstadoCaja.NEUTRALPAYASO;
                        timer = 0;
                    }
                    break;
                case NEUTRALPAYASO:
                    timer += Gdx.graphics.getDeltaTime();
                    if (!risaPayaso.isPlaying()&&LightsGone.musica) {
                        risaPayaso.play();
                    }
                    if (timer >= 4) {
                        estadoCaja = EstadoCaja.CAJAATAQUE;
                        timer = 0;
                        risaPayaso.stop();
                        if(LightsGone.musica)
                            explosion.play();
                    }
                    break;
                case CAJAATAQUE:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(cajaAtaque.getKeyFrame(timer).getTexture());
                    if (timer >= cajaAtaque.getAnimationDuration()) {
                        muerte = true;
                        timer = 0;
                    }
            }
        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        public boolean isAttacking() {
            return estadoCaja == EstadoCaja.CAJAATAQUE && timer >= 0.4f;
        }

        public boolean floor(float x, float y) {
            if (estadoCaja == EstadoCaja.CAJAMOVIL)
                return getBoxRectangle().contains(x, y);
            return false;
        }

        public void move(float x, float y, boolean right, float velocidad) {
            if (estadoCaja == EstadoCaja.CAJAMOVIL) {
                if (getBoxRectangle().contains(x, y)) {
                    if (!mapa.colisionX(sprite.getX() + 45, sprite.getY() + 30) && !mapa.colisionX(sprite.getX() + 45 + ANCHOCAJA, sprite.getY() + 30)) {
                        if (right) {
                            sprite.translate(velocidad, 0);
                        } else {
                            sprite.translate(-velocidad, 0);
                        }
                    }
                }
                if (!mapa.colisionCaja(sprite.getX() + sprite.getWidth() / 2, sprite.getY(), true, -1, false)) {
                    caer();
                }
            }
        }

        private void caer() {
            sprite.translate(0, -18f);
        }

        Rectangle getBoxRectangle() {
            return new Rectangle(sprite.getX() + 35, sprite.getY(), 199, 165);
        }

        public Rectangle getAttackRectangle() {
            return new Rectangle(sprite.getX(), sprite.getY(), ANCHOCAJA * 2, sprite.getHeight());
        }

        private enum EstadoCaja {
            NEUTRAL,
            CAJAMOVIL,
            CAJAATAQUE,
            NEUTRALPAYASO,
            PAYASOFUERA
        }

        @Override
        public String toString() {
            return "CajaPayaso";
        }
    }

    static class GeneradorCajasPayaso extends Enemigo {
        private CajaPayaso cajaPayaso;
        private float x, y;
        private Abner abner;
        private Mapa mapa;
        private float timer;

        public GeneradorCajasPayaso(float x, float y, Mapa mapa, Abner abner) {
            super();
            this.x = x;
            this.y = y;
            this.mapa = mapa;
            this.abner = abner;
            cajaPayaso = new CajaPayaso(x, y, abner, mapa);
            timer = 0;
        }

        @Override
        public void attack() {
            if (cajaPayaso != null)
                cajaPayaso.attack();
        }

        @Override
        public void setEstado(Estado estado) {

        }


        public boolean isAttacking() {
            if (cajaPayaso != null) {
                return cajaPayaso.isAttacking();
            }
            return false;
        }

        @Override
        public void draw(SpriteBatch batch) {
            if (cajaPayaso != null && cajaPayaso.muerte()) {
                cajaPayaso = null;
            }

            if (cajaPayaso != null) {
                cajaPayaso.draw(batch);
            } else {
                actualizar();
            }

        }

        @Override
        public void stop() {
            if (cajaPayaso != null) {
                cajaPayaso.stop();
            }
        }

        private void actualizar() {
            timer += Gdx.graphics.getDeltaTime();
            if (timer >= 10) {
                cajaPayaso = new CajaPayaso(x, y, abner, mapa);
                timer = 0;
            }

        }

        @Override
        public boolean muerte() {
            return false;
        }

        public void move(float x, float y, boolean right, float velocidad) {
            if (cajaPayaso != null) {
                cajaPayaso.move(x, y, right, velocidad);
            }
        }

        public Rectangle getAttackRectangle() {
            return cajaPayaso.getAttackRectangle();
        }

        public Rectangle getBoxRectangle() {
            return cajaPayaso.getBoxRectangle();
        }

        public boolean floor(float x, float y) {
            if (cajaPayaso != null) {
                return cajaPayaso.floor(x, y);
            }
            return false;
        }

        public String toString() {
            return "CajaPayaso";
        }
    }

    static class Robot extends Enemigo {
        private static Texture neutral, proyectil;
        private static Animation ataque;
        private static Array<TextureRegion> textureRegions;
        private float timer, timerOn;
        private Abner abner;
        private Array<Proyectil.Bola> proyectiles;
        private EstadoRobot estadoRobot;
        private boolean right;
        private float vida;

        static {
            cargarTexturas();
            cargarAnimacion();
        }


        private static void cargarAnimacion() {
            ataque = new Animation(0.2f, textureRegions);
            ataque.setPlayMode(Animation.PlayMode.NORMAL);
        }


        private static void cargarTexturas() {
            manager.load("R_A1.png", Texture.class);
            manager.load("R_A2.png", Texture.class);
            manager.load("R_A3.png", Texture.class);
            manager.load("R_A4.png", Texture.class);
            manager.load("R_ball.png", Texture.class);
            manager.load("R_Off.png", Texture.class);
            manager.load("R_On.png", Texture.class);
            manager.finishLoading();
            textureRegions = new Array<TextureRegion>(5);
            textureRegions.add(new TextureRegion((Texture) manager.get("R_On.png")));
            textureRegions.add(new TextureRegion((Texture) manager.get("R_A1.png")));
            textureRegions.add(new TextureRegion((Texture) manager.get("R_A2.png")));
            textureRegions.add(new TextureRegion((Texture) manager.get("R_A3.png")));
            textureRegions.add(new TextureRegion((Texture) manager.get("R_A4.png")));
            proyectil = manager.get("R_ball.png");
            neutral = manager.get("R_Off.png");

        }


        public Robot(float x, float y, Abner abner) {
            super(neutral, x, y);
            estadoRobot = EstadoRobot.APAGADO;
            this.abner = abner;
            this.proyectiles = new Array<Proyectil.Bola>();
            this.vida = 3;
            timerOn = 0;
        }

        @Override
        public void attack() {

            for (int i = 0; i < proyectiles.size; i++) {
                Proyectil.Bola proyectil = proyectiles.get(i);
                if (proyectil.getRectangle().overlaps(abner.getBoundingRectangle()) && !abner.getDano()&&!LightsGone.getCapa()) {
                    abner.setCantVida(abner.getcantVida() - 10);
                    abner.setDano(true);
                    proyectiles.removeIndex(i--);
                }
            }

        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            if (right && sprite.isFlipX()) {
                sprite.flip(true, false);
            } else if (!right && !sprite.isFlipX()) {
                sprite.flip(true, false);
            }
            actualizar();
            attack();
            for (Proyectil.Bola bola : proyectiles) {
                if (bola.out()) {
                    proyectiles.removeValue(bola, true);
                } else
                    bola.draw(batch);
            }
        }

        @Override
        public void stop() {

        }


        private void actualizar() {
            switch (estadoRobot) {
                case APAGADO:
                    if (Math.abs(sprite.getX() - abner.getX()) <= 530) {
                        timerOn += Gdx.graphics.getDeltaTime();
                        if (timerOn >= 2) {
                            estadoRobot = EstadoRobot.ATACANDO;
                            timerOn = 0;
                        }
                    }
                    break;
                case ATACANDO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());
                    if (timer >= 0.4f && timer <= 0.4f + Gdx.graphics.getDeltaTime()) {
                        proyectiles.add(new Proyectil.Bola(proyectil, right ? sprite.getX() + 242 : sprite.getX() + 94, sprite.getY() + 92, right));
                    } else if (timer >= 0.6f && timer <= 0.6f + Gdx.graphics.getDeltaTime()) {
                        proyectiles.add(new Proyectil.Bola(proyectil, right ? sprite.getX() + 270 : sprite.getX() + 68, sprite.getY() + 92, right));
                    }
                    if (timer >= ataque.getAnimationDuration()) {
                        estadoRobot = EstadoRobot.APAGADO;
                        timer = 0;
                    }
                    break;
            }

            if (sprite.getX() - abner.getX() < 0) {
                right = true;
            } else {
                right = false;
            }
            ArrayList<Proyectil> proyectils = abner.getProyectiles();
            for (int i = 0; i < proyectils.size(); i++) {
                Proyectil proyectil = proyectils.get(i);
                if (proyectil.getRectangle().overlaps(sprite.getBoundingRectangle()) && proyectil.toString().equalsIgnoreCase("Papa")) {
                    vida--;
                    proyectils.remove(i--);
                }
            }

            if (vida <= 0) {
                AdministradorMapa.quitarEnemigo(this);
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private enum EstadoRobot {
            APAGADO,
            ATACANDO
        }
    }

    static class MonstruoRopa extends Enemigo {

        private static Texture neutral;
        private static Animation respirando, levantando, atrapar, atrapado;
        private static Array<TextureRegion> respirandoTex, levantandoTex, atraparTex, atrapadoTex;
        private final Abner abner;
        private EstadoRopa estadoRopa;
        private float timer, timerAtaque;
        private boolean right;
        private static Music mordida;

        static {
            cargarTexturas();
            cargarAnimaciones();
        }

        private static void cargarAnimaciones() {
            respirando = new Animation(0.4f, respirandoTex);
            respirando.setPlayMode(Animation.PlayMode.LOOP);
            levantando = new Animation(0.2f, levantandoTex);
            levantando.setPlayMode(Animation.PlayMode.NORMAL);
            atrapar = new Animation(0.1f, atraparTex);
            atrapar.setPlayMode(Animation.PlayMode.NORMAL);
            atrapado = new Animation(0.2f, atrapadoTex);
            atrapado.setPlayMode(Animation.PlayMode.LOOP);
        }

        private static void cargarTexturas() {
            manager.load("monstruoropa1.png", Texture.class);
            manager.load("monstruoropa2.png", Texture.class);
            manager.load("monstruoropa3.png", Texture.class);
            manager.load("monstruoropa4.png", Texture.class);
            manager.load("monstruoropa5.png", Texture.class);
            manager.load("monstruoropa6.png", Texture.class);
            manager.load("monstruoropa7.png", Texture.class);
            manager.load("monstruoropa8.png", Texture.class);
            manager.load("monstruoropa9.png", Texture.class);
            manager.load("monstruoropa10.png", Texture.class);
            manager.load("monstruoropa11.png", Texture.class);
            manager.load("Bite 2.mp3", Music.class);
            manager.finishLoading();
            respirandoTex = new Array<TextureRegion>();
            levantandoTex = new Array<TextureRegion>();
            atraparTex = new Array<TextureRegion>();
            atrapadoTex = new Array<TextureRegion>();
            neutral = manager.get("monstruoropa1.png");
            levantandoTex.add(new TextureRegion((Texture) manager.get("monstruoropa2.png")));
            levantandoTex.add(new TextureRegion((Texture) manager.get("monstruoropa3.png")));
            respirandoTex.add(new TextureRegion((Texture) manager.get("monstruoropa4.png")));
            respirandoTex.add(new TextureRegion((Texture) manager.get("monstruoropa5.png")));
            atraparTex.add(new TextureRegion((Texture) manager.get("monstruoropa6.png")));
            atraparTex.add(new TextureRegion((Texture) manager.get("monstruoropa7.png")));
            atraparTex.add(new TextureRegion((Texture) manager.get("monstruoropa8.png")));
            atraparTex.add(new TextureRegion((Texture) manager.get("monstruoropa9.png")));
            atrapadoTex.add(new TextureRegion((Texture) manager.get("monstruoropa10.png")));
            atrapadoTex.add(new TextureRegion((Texture) manager.get("monstruoropa11.png")));
            mordida = manager.get("Bite 2.mp3");

        }

        public MonstruoRopa(float x, float y, Abner abner) {
            super(neutral, x, y);
            estadoRopa = EstadoRopa.CAIDO;
            this.abner = abner;
            this.timerAtaque = 0;
        }

        @Override
        public void attack() {
            if (timer >= 0.3f && abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle()) && !abner.getDano()&&!LightsGone.getCapa()) {
                abner.setAtrapado(true);
                estadoRopa = EstadoRopa.ATRAPADO;
                timer = 0;
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            actualizar();
        }

        @Override
        public void stop() {
            if (mordida.isPlaying()) {
                mordida.stop();
            }
        }

        private void actualizar() {
            switch (estadoRopa) {
                case CAIDO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral);
                    if (Math.abs(sprite.getX() - abner.getX()) <= 500) {
                        if (timer >= 3) {
                            estadoRopa = EstadoRopa.LEVANTANDO;
                            timer = 0;
                        }
                    }
                    break;
                case LEVANTANDO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(levantando.getKeyFrame(timer).getTexture());
                    if (timer >= levantando.getAnimationDuration()) {
                        timer = 0;
                        estadoRopa = EstadoRopa.ESPERANDO;
                    }
                    break;
                case ESPERANDO:
                    right = abner.getX() - sprite.getX() > 0;
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(respirando.getKeyFrame(timer).getTexture());
                    if (Math.abs(sprite.getX() - abner.getX()) <= 100) {
                        timer = 0;
                        estadoRopa = EstadoRopa.ATACANDO;
                    } else if (Math.abs(sprite.getX() - abner.getX()) >= 500) {
                        estadoRopa = EstadoRopa.CAIDO;
                        timer = 0;
                    }
                    break;
                case ATACANDO:
                    attack();
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(atrapar.getKeyFrame(timer).getTexture());
                    if (timer >= atrapar.getAnimationDuration()) {
                        timer = 0;
                        estadoRopa = EstadoRopa.ESPERANDO;
                    }
                    break;
                case ATRAPADO:
                    timer += Gdx.graphics.getDeltaTime();
                    timerAtaque += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(atrapado.getKeyFrame(timer).getTexture());
                    if (timerAtaque >= 1) {
                        abner.setCantVida(abner.getcantVida() - 7);
                        timerAtaque = 0;
                        Gdx.input.vibrate(10);
                        mordida.play();
                    }
                    if (timer >= 6 || !abner.isAtrapado()) {
                        estadoRopa = EstadoRopa.CAIDO;
                        abner.setX(right ? abner.getX() - 150 : abner.getX() + 150);
                        abner.setDano(true);
                        abner.setAtrapado(false);
                        timer = 0;

                    }
                    break;
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private enum EstadoRopa {
            CAIDO,
            LEVANTANDO,
            ESPERANDO,
            ATACANDO,
            ATRAPADO
        }
    }

    static class Alfombra extends Enemigo {
        private static Texture neutral, esperando, ataque;
        private Estado estadoAlfombra;
        private float timer;
        private Abner abner;
        private boolean right;
        private float timerAtaque, timerAnimation;
        private float rotation;
        private static Music mordida;

        static {
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("AlfombraAtaque1.png", Texture.class);
            manager.load("AlfombraAtaque2.png", Texture.class);
            manager.load("AlfombraNeutro.png", Texture.class);
            manager.load("Bite 2.mp3", Music.class);
            manager.finishLoading();
            neutral = manager.get("AlfombraNeutro.png");
            esperando = manager.get("AlfombraAtaque1.png");
            ataque = manager.get("AlfombraAtaque2.png");
            mordida = manager.get("Bite 2.mp3");
        }

        public Alfombra(float x, float y, Abner abner) {
            super(neutral, x, y);
            this.abner = abner;
            estadoAlfombra = Estado.NEUTRAL;
            rotation = 0;
        }


        @Override
        public void attack() {
            abner.setAtrapado(true);
        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            sprite.setRotation(rotation);
            actualizar();
            right = abner.getX() - sprite.getX() > 0;
            if (right && sprite.isFlipX()) {
                sprite.flip(true, false);
            } else if (!right && !sprite.isFlipX()) {
                sprite.flip(true, false);
            }
        }

        @Override
        public void stop() {
            if (mordida.isPlaying()) {
                mordida.stop();
            }
        }

        private void actualizar() {
            switch (estadoAlfombra) {
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    if (Math.abs(sprite.getX() - abner.getX()) <= 500) {
                        estadoAlfombra = Estado.ESPERANDO;
                    }
                    break;
                case ESPERANDO:
                    sprite.setTexture(esperando);
                    if (abner.getBoundingRectangle().overlaps(getRectangle()) && !abner.getDano()&&!LightsGone.getCapa()) {
                        attack();
                        estadoAlfombra = Estado.ATAQUE;
                        rotation = 5;
                    }
                    break;
                case ATAQUE:
                    sprite.setTexture(ataque);
                    float delta = Gdx.graphics.getDeltaTime();
                    timer += delta;
                    timerAtaque += delta;
                    timerAnimation += delta;
                    if (timer >= 6 || !abner.isAtrapado()) {
                        estadoAlfombra = Estado.NEUTRAL;
                        abner.setX(right ? abner.getX() - 450 : abner.getX() + 450);
                        abner.setDano(true);
                        abner.setAtrapado(false);
                        timer = 0;
                        rotation = 0;
                    }
                    if (timerAnimation >= 0.3) {
                        timerAnimation = 0;
                        rotation *= -1;
                    }
                    if (timerAtaque >= 1) {
                        abner.setCantVida(abner.getcantVida() - 7);
                        timerAtaque = 0;
                        Gdx.input.vibrate(10);
                        mordida.play();
                    }
                    break;
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        public Rectangle getRectangle() {
            return new Rectangle(sprite.getX()+181, sprite.getY()+57, 300, 127);
        }
    }

    static class Fantasma extends Enemigo {
        private static Texture fantasma1, fantasma2, fantasma3, muerte1, muerte2, muerte3, muerte4;
        private static Animation caminar, muerte;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        private int direccion;
        private float posXOriginal;
        private float posYOriginal;
        private float time;


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            caminar = new Animation(0.3f, new TextureRegion(fantasma1), new TextureRegion(fantasma2), new TextureRegion(fantasma3), new TextureRegion(fantasma2));
            muerte = new Animation(0.2f, new TextureRegion(muerte1), new TextureRegion(muerte2), new TextureRegion(muerte3), new TextureRegion(muerte4));
            caminar.setPlayMode(Animation.PlayMode.LOOP);
            muerte.setPlayMode(Animation.PlayMode.NORMAL);

        }

        public Fantasma(float x, float y, Abner abner, Mapa mapa) {
            super(fantasma1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            vida = 1;
            this.mapa = mapa;
            posXOriginal = sprite.getX();
            posYOriginal = sprite.getY();

            time = 0;
        }

        @Override
        public void attack() {
            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(abner.getcantVida() - 3);
                ataco = true;
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {

            sprite.draw(batch);

            float distancia = sprite.getX() - abner.getX();

            if (Math.abs(distancia) <= 900 && vida > 0 && Math.abs(sprite.getY() - abner.getY()) < 500) {
                estado = Estado.ATAQUE;
                ataq = true;
            }


            actualizar();
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return true;
        }

        public Abner getAbner() {
            return abner;
        }

        public Mapa getMapa() {
            return mapa;
        }

        private void actualizar() {
            if (abner.getX() > posXOriginal) {
                direccion = -1;
                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            } else {
                direccion = 1;
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }

            if (abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())) {
                if (!ataco && estado == Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if (System.currentTimeMillis() - startTime > 2000) {
                ataco = false;
                startTime = 0;
            }


            if (LightsGone.getEstadoLampara().equals(LightsGone.Lampara.ENCENDIDA) && Math.abs(sprite.getX() - abner.getX()) < 500 && Math.abs(sprite.getY() - abner.getY()) < 150) {

                vida -= 1;

            }


            if (vida <= 0) {
                estado = Estado.NEUTRAL;
                timer += Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerte.getKeyFrame(timerA).getTexture());
                if (timer >= 1.5)
                    AdministradorMapa.quitarEnemigo(this);
            }


            switch (estado) {
                case NEUTRAL:

                    break;
                case ATAQUE:

                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                    sprite.setX(sprite.getX() - 8 * direccion);
                    sprite.setY(abner.getY() + (abner.getHeight() / 2) - 50);

                    break;

            }
        }


        private static void cargarTexturas() {
            manager.load("FantasmaN1.png", Texture.class);
            manager.load("FantasmaN2y4.png", Texture.class);
            manager.load("FantasmaN3.png", Texture.class);
            manager.load("FantasmaMuerte1.png", Texture.class);
            manager.load("FantasmaMuerte2.png", Texture.class);
            manager.load("FantasmaMuerte3.png", Texture.class);
            manager.load("FantasmaMuerte4.png", Texture.class);

            manager.finishLoading();
            fantasma1 = manager.get("FantasmaN1.png");
            fantasma2 = manager.get("FantasmaN2y4.png");
            fantasma3 = manager.get("FantasmaN3.png");
            muerte1 = manager.get("FantasmaMuerte1.png");
            muerte2 = manager.get("FantasmaMuerte2.png");
            muerte3 = manager.get("FantasmaMuerte3.png");
            muerte4 = manager.get("FantasmaMuerte4.png");


        }

        @Override
        public String toString() {
            return "mosca";
        }
    }

    static class OndaCoco extends Enemigo{

        private static Texture ondaGrande;
        private Abner abner;
        private EstadoOnda estadoOnda;
        private final float CAIDA = -20f;
        private static Music rugido;
        private static Animation animation;
        private float timer;
        private boolean right;
        private Mapa mapa;

        static {
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("CocoAtaqueOndaGrande.png", Texture.class);
            manager.load("CocoAtaqueOnda1.png",Texture.class);
            manager.load("CocoAtaqueOnda2.png", Texture.class);
            manager.load("CocoAtaqueOnda3.png", Texture.class);
            manager.load("rugido.mp3", Music.class);
            manager.finishLoading();
            ondaGrande = manager.get("CocoAtaqueOndaGrande.png");
            animation = new Animation(0.2f, new TextureRegion((Texture)manager.get("CocoAtaqueOnda1.png")),new TextureRegion((Texture)manager.get("CocoAtaqueOnda2.png")),new TextureRegion((Texture)manager.get("CocoAtaqueOnda3.png")));
            rugido = manager.get("rugido.mp3");
        }

        public OndaCoco(float x, float y, Abner abner) {
            super(ondaGrande, x, y);
            this.abner = abner;
            estadoOnda = EstadoOnda.ESPERANDO;
        }

        public OndaCoco(float x, float y, Abner abner, boolean right, Mapa mapa){
            super((Texture)manager.get("CocoAtaqueOnda1.png"),x,y);
            this.abner = abner;
            timer = 0;
            this.right = right;
            rugido.play();
            if(!right){
                sprite.flip(true, false);
            }
            this.mapa = mapa;
            estadoOnda = EstadoOnda.AVAZANDO;
        }

        @Override
        public void attack() {
            if(estadoOnda == EstadoOnda.BAJANDO)
                abner.setCayendo(true);
            if(estadoOnda == EstadoOnda.AVAZANDO){
                if(getRectangle().overlaps(abner.getBoundingRectangle())&&!abner.getDano()&&!LightsGone.getCapa()){
                    abner.setCantVida(abner.getcantVida()-15);
                    abner.setDano(true);
                }
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        private Rectangle getRectangle(){
            return new Rectangle(sprite.getX()+375, sprite.getY(),194,sprite.getHeight()-100);
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            actualizar();
            ArrayList<Proyectil> proyectils = abner.getProyectiles();
            for(int i=0;i<proyectils.size();i++){
                Proyectil proyectil = proyectils.get(i);
                if(proyectil.getRectangle().overlaps(getRectangle())){
                    proyectils.remove(i--);
                    proyectil.play();
                }
            }
        }

        @Override
        public void stop() {
            if (rugido.isPlaying()) {
                rugido.stop();
            }
        }

        private void actualizar() {
            switch (estadoOnda) {
                case ESPERANDO:
                    if (abner.getY() > 1500) {
                        estadoOnda = EstadoOnda.BAJANDO;
                        rugido.play();
                    }
                    break;
                case BAJANDO:
                    sprite.translate(0, CAIDA);
                    if (abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle()) && !LightsGone.getCapa()) {
                        attack();
                    }
                    break;
                case AVAZANDO:
                    attack();
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(animation.getKeyFrame(timer).getTexture());
                    if(right){
                        sprite.translate(-CAIDA, 0);
                    }
                    else{
                        sprite.translate(CAIDA, 0);
                    }
                    break;
            }
        }

        @Override
        public boolean muerte() {
            return right?mapa.getWidth()<sprite.getX():sprite.getX()<0;
        }

        private enum EstadoOnda {
            ESPERANDO,
            BAJANDO,
            AVAZANDO
        }
    }

    static class Telarana extends Enemigo {
        private static Texture neutralT, rota1, rota2, rota3;
        private Estado estado;
        private static Animation destruccion;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        long contador = 0;
        long contador1 = 0;
        float posXOriginal;
        int direccion;
        boolean primera = true;


        static {
            cargarTexturas();
            cargarAnimacion();


        }

        private static void cargarAnimacion() {
            destruccion = new Animation(0.2f, new TextureRegion(rota1), new TextureRegion(rota2), new TextureRegion(rota3));
            //destruccion.setPlayMode(Animation.PlayMode.NORMAL);

        }


        public Telarana(float x, float y, Abner abner, Mapa mapa) {
            super(neutralT, x, y);
            estado = Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa = mapa;
            vida = 3;
            posXOriginal = sprite.getX();
            vida = 1;
            AdministradorMapa.crearArana(this);

        }


        public Rectangle getRectangle() {
            return new Rectangle(sprite.getX() + 200, sprite.getY() + 200, sprite.getWidth() - 400, sprite.getHeight() - 200);
        }

        public Abner getAbner() {
            return abner;
        }

        public Mapa getMapa() {
            return mapa;
        }

        @Override
        public void attack() {

            if (!abner.getDano()) {
                abner.setCantVida(abner.getcantVida() - 1);
                abner.setDano(true);
            }

        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            /*if(primera) {
                AdministradorMapa.crearArana(this);
            }*/
            actualizar();


        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return vida <= 0;
        }

        private void actualizar() {
            primera = false;

            //Colisiones con proyectiles
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 100, sprite.getY(), sprite.getWidth() - 100, sprite.getHeight()))) {
                if (abner.getX() > posXOriginal) {
                    direccion = -1;
                } else {
                    direccion = 1;
                }
                abner.setX(abner.getX() - (float) 10 * direccion);
                abner.ajusteCamara(direccion);

            }

            if (!abner.getProyectiles().isEmpty()) {
                if (abner.getProyectiles().get(0).toString().equals("Canica")) {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        abner.borrarProyectiles();
                    }
                } else {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }
            }


            if (vida <= 0) {
                timer += Gdx.graphics.getDeltaTime();
                sprite.setTexture(destruccion.getKeyFrame(timer).getTexture());
                if (timer >= .5)
                    AdministradorMapa.quitarEnemigo(this);
            }


        }


        private static void cargarTexturas() {
            manager.load("TelaranaNeutra.png", Texture.class);
            manager.load("TelaranaRota1.png", Texture.class);
            manager.load("TelaranaRota2.png", Texture.class);
            manager.load("TelaranaRota3.png", Texture.class);


            manager.finishLoading();
            neutralT = manager.get("TelaranaNeutra.png");
            rota1 = manager.get("TelaranaRota1.png");
            rota2 = manager.get("TelaranaRota2.png");
            rota3 = manager.get("TelaranaRota3.png");


        }

        @Override
        public String toString() {
            return "scarecrow";
        }
    }

    static class Arana extends Enemigo {
        private static Texture arana1, arana2;
        private static Animation caminar;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        private int direccionX = 1;
        private float posXOriginal;
        private float posYOriginal;
        private float time;
        private Rectangle rec;
        private int movimiento;
        private Random rnd = new Random();


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            caminar = new Animation(0.1f, new TextureRegion(arana1), new TextureRegion(arana2));
            caminar.setPlayMode(Animation.PlayMode.LOOP);


        }

        public Arana(float x, float y, Abner abner, Mapa mapa, Rectangle rect) {
            super(arana1, x, y);
            estado = Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            vida = 1;
            this.mapa = mapa;
            rec = rect;
            time = 0;
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

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        public Abner getAbner() {
            return abner;
        }

        public Mapa getMapa() {
            return mapa;
        }

        private void actualizar() {
            if (!sprite.getBoundingRectangle().overlaps(rec)) {
                direccionX *= -1;
            }


            timer += Gdx.graphics.getDeltaTime();
            sprite.setTexture(caminar.getKeyFrame(timer).getTexture());

            timerA += Gdx.graphics.getDeltaTime();

            if (timerA > 2) {
                timerA = 0;
                movimiento = rnd.nextInt(2);
            }


            if (vida <= 0) {
                //AdministradorMapa.crearNuevaMosca(xInicial,yInicial,this);
                //AdministradorMapa.quitarEnemigo(this);
            }


            if (movimiento == 0) {
                sprite.setX(sprite.getX() - 1 * direccionX);
            } else {
                sprite.setY(sprite.getY() - 1 * direccionX);
            }


        }


        private static void cargarTexturas() {
            manager.load("Arana1.png", Texture.class);
            manager.load("Arana2.png", Texture.class);


            manager.finishLoading();
            arana1 = manager.get("Arana1.png");
            arana2 = manager.get("Arana2.png");


        }

        @Override
        public String toString() {
            return "Arana";
        }
    }

    static class Oso extends Enemigo{
        private final Mapa mapa;
        private Abner abner;
        private static Texture neutral, salto1, salto2;
        private static Animation caminar;
        private EstadoOso estadoOso;
        private SaltoOso saltoOso;
        private boolean right;
        private float timer, timerSalto, alturaMax;
        private final float SALTOMAX = 285;

        static {
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("OsoJump1.png", Texture.class);
            manager.load("OsoJump2.png", Texture.class);
            manager.load("OsoNeutral.png", Texture.class);
            manager.load("OsoWalk1.png", Texture.class);
            manager.load("OsoWalk2.png", Texture.class);
            manager.load("OsoWalk3.png", Texture.class);
            manager.finishLoading();
            neutral = manager.get("OsoJump1.png");
            caminar = new Animation(0.3f,new TextureRegion(manager.get("OsoWalk1.png", Texture.class)),new TextureRegion(manager.get("OsoWalk2.png", Texture.class)),new TextureRegion(manager.get("OsoWalk3.png", Texture.class)));
            caminar.setPlayMode(Animation.PlayMode.LOOP);
            salto1 = manager.get("OsoJump1.png");
            salto2 = manager.get("OsoJump2.png");
        }

        public Oso(float x, float y, Abner abner, Mapa mapa){
            super(neutral,x,y);
            this.abner = abner;
            this.mapa = mapa;
            estadoOso = EstadoOso.NEUTRAL;
            saltoOso = SaltoOso.SUBIENDO;
            timer = 0;
            alturaMax = y+SALTOMAX;
        }

        @Override
        public void attack() {
            if(!abner.getDano()&&getRectangle().overlaps(abner.getBoundingRectangle())&&!LightsGone.getCapa()){
                abner.setCantVida(abner.getcantVida()-8);
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        private Rectangle getRectangle(){
            return new Rectangle(sprite.getX()+119, sprite.getY(),114,304);
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            actualizar();
            if(sprite.getY()<0){
                AdministradorMapa.quitarEnemigo(this);
            }
            attack();
        }

        private void actualizar() {
            if((abner.getX() - sprite.getX()) < 0){
                right = false;
            }
            else{
                right = true;
            }
            if((!right&&sprite.isFlipX())||(right&&!sprite.isFlipX())){
                sprite.flip(true, false);
            }
            switch (estadoOso){
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    if(Math.abs(abner.getX()-sprite.getX())<=400){
                        estadoOso = EstadoOso.CAMINADO;
                    }
                    break;
                case CAMINADO:
                    timer+= Gdx.graphics.getDeltaTime();
                    sprite.setTexture(caminar.getKeyFrame(timer).getTexture());
                    if(!mapa.colisionX(right?sprite.getX()+sprite.getWidth():sprite.getX(), sprite.getY()+30))
                        sprite.translate(right?3:-3,0);
                    if(abner.getY()>sprite.getY()+100&&Math.abs(abner.getX()-sprite.getX())<100&&!abner.isJumping()){
                        estadoOso = EstadoOso.SALTANDO;
                        saltoOso = SaltoOso.SUBIENDO;
                    }
                    if(mapa.colisionY(sprite.getX()+sprite.getWidth()/2,sprite.getY()-20)==-1){
                        estadoOso = EstadoOso.SALTANDO;
                        saltoOso = SaltoOso.BAJANDO;
                    }
                    break;
                case SALTANDO:
                    switch (saltoOso){
                        case SUBIENDO:
                            timerSalto+= Gdx.graphics.getDeltaTime();
                            if(timerSalto>=2){
                                timerSalto = 2;
                                sprite.setTexture(salto2);
                                sprite.translate(0,7);
                                if(sprite.getY()>=alturaMax){
                                    sprite.setY(alturaMax);
                                    saltoOso = SaltoOso.BAJANDO;
                                    timerSalto = 0;
                                }
                            }
                            else{
                                sprite.setTexture(salto1);
                            }
                            break;
                        case BAJANDO:
                            sprite.translate(0,-15);
                            sprite.setTexture(salto2);
                            float nuevaY = mapa.colisionY(sprite.getX()+sprite.getWidth()/2,sprite.getY());
                            if(nuevaY!=-1){
                                sprite.setY(nuevaY);
                                estadoOso = EstadoOso.CAMINADO;
                                alturaMax = nuevaY + SALTOMAX;
                            }
                            break;
                    }
                    break;
            }
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return false;
        }
        private enum EstadoOso{
            NEUTRAL,
            CAMINADO,
            SALTANDO
        }
        private enum SaltoOso{
            SUBIENDO,
            BAJANDO
        }
    }

    static class ParedPicos extends Enemigo {
        private static Texture pared;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama = 10;
        private boolean ataq = false;
        private Mapa mapa;
        long contador = 0;
        long contador1 = 0;
        float posXOriginal;
        int direccion;


        static {
            cargarTexturas();

        }


        public ParedPicos(float x, float y, Abner abner, Mapa mapa) {
            super(pared, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa = mapa;
            vida = 3;
            posXOriginal = sprite.getX();
            //sprite.setSize(250,400);

        }


        @Override
        public void attack() {

            if (!abner.getDano()&&!LightsGone.getCapa()) {
                abner.setCantVida(0);
                abner.setDano(true);
            }

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

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return vida <= 0;
        }

        private void actualizar() {
            if (abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX() + 100, sprite.getY(), sprite.getWidth() - 100, sprite.getHeight()))) {
                attack();
            }
            if (Math.abs(sprite.getX() - posXOriginal) < 4230) {

                if (abner.getX() < sprite.getX()) {

                    if (Math.abs(abner.getX() - sprite.getX()) < 800 && Math.abs(abner.getY() - sprite.getY()) < 500) {
                        estado = Estado.ATAQUE;

                    }
                }
            } else {
                estado = Estado.NEUTRAL;
            }

            switch (estado) {
                case NEUTRAL:

                    break;
                case ATAQUE:
                    sprite.setX(sprite.getX() - 8);
                    break;

            }
        }


        private static void cargarTexturas() {
            manager.load("Pared Picos.png", Texture.class);


            manager.finishLoading();
            pared = manager.get("Pared Picos.png");


        }

        @Override
        public String toString() {
            return "Pared Picos";
        }
    }

    private static class Garra extends Enemigo{
        private static Animation animation;
        private final Abner abner;
        private float timer;

        static {
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("CocoGarra1.png", Texture.class);
            manager.load("CocoGarra2.png", Texture.class);
            manager.load("CocoGarra3.png", Texture.class);
            manager.load("CocoGarra4.png", Texture.class);
            manager.load("CocoGarra5.png", Texture.class);
            manager.finishLoading();
            animation = new Animation(0.2f, new TextureRegion((Texture)manager.get("CocoGarra1.png")),new TextureRegion((Texture)manager.get("CocoGarra2.png")),new TextureRegion((Texture)manager.get("CocoGarra3.png")), new TextureRegion((Texture)manager.get("CocoGarra4.png")), new TextureRegion((Texture)manager.get("CocoGarra5.png")));
        }

        public Garra(float x, float y, Abner abner, boolean right){
            super((Texture)manager.get("CocoGarra1.png"),x,y);
            if((right&&!sprite.isFlipX())||(!right&&sprite.isFlipX())){
                sprite.flip(true, false);
            }
            this.abner = abner;

        }

        @Override
        public void attack() {
            if(sprite.getBoundingRectangle().overlaps(abner.getBoundingRectangle())&&!abner.getDano()&&!LightsGone.getCapa()){
                abner.setCantVida(abner.getcantVida()-15);
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {

        }

        public void draw(SpriteBatch batch, float x, float y) {
            ArrayList<Proyectil> proyectils = abner.getProyectiles();
            for(int i=0;i<proyectils.size();i++){
                Proyectil proyectil = proyectils.get(i);
                if(proyectil.getRectangle().overlaps(sprite.getBoundingRectangle())){
                    proyectils.remove(i--);
                    proyectil.play();
                }
            }
            sprite.setPosition(x,y);
            sprite.draw(batch);
            timer+=Gdx.graphics.getDeltaTime();
            sprite.setTexture(animation.getKeyFrame(timer).getTexture());
            attack();
        }

        public void reiniciar(){
            timer = 0;
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return false;
        }

        public float getTime() {
            return animation.getAnimationDuration();
        }
    }

    private static class Cocos extends Enemigo{
        private static final float CAIDA = -10;
        private static Texture coco;
        private Abner abner;

        static{
            cargarTexturas();
        }

        private static void cargarTexturas() {
            manager.load("CocoAtaqueProyectil.png", Texture.class);
            manager.finishLoading();
            coco = manager.get("CocoAtaqueProyectil.png");
        }

        public Cocos(float x, Mapa mapa, Abner abner){
            super(coco, x, mapa.getHeight());
            sprite.setY(new Random().nextInt(LightsGone.ANCHO_MUNDO/2)+(3*mapa.getHeight()/4));
            this.abner = abner;
        }

        @Override
        public void attack() {
            if(sprite.getBoundingRectangle().overlaps(abner.getBoundingRectangle())&&!abner.getDano()&&!LightsGone.getCapa()){
                abner.setCantVida(abner.getcantVida()-10);
                abner.setDano(true);
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            sprite.translate(0,CAIDA);
            attack();
            ArrayList<Proyectil> proyectils = abner.getProyectiles();
            for(int i=0;i<proyectils.size();i++){
                Proyectil proyectil = proyectils.get(i);
                if(proyectil.getRectangle().overlaps(sprite.getBoundingRectangle())){
                    proyectils.remove(i--);
                    proyectil.play();
                }
            }
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return sprite.getY()<0||muerte;
        }
    }

    static class Coco extends Enemigo{
        private static TextureRegion caminar, ataques, dano, lluvia, salto1, salto2;
        private static TextureRegion muerteCoco;
        private static TextureRegion neutral;
        private Sprite cascaron, barra;
        private Animation camina, grito, garrazo;
        private static final float X = 495, Y= 180;
        private static final int ANCHO = 547, ALTO = 582;
        private EstadoCoco estadoCoco;
        private FaseCoco faseCoco;
        private Ataque ataque;
        private float timer, vida, timerC, timerD, timerItem, timerG;
        private final float VELOCIDAD = 8, CAIDA = 18, SALTO = 11, SALTOMAX = 400;
        private float alturaMax;
        private boolean right, garraB;
        private Mapa mapa;
        private Abner abner;
        private Array<OndaCoco> ondaCocos;
        private Garra garra;
        private Array<Cocos> cocos;
        private Abner.Salto salto;
        private EstadoCoco estadoTemp;
        private float alpha;


        static {
            cargarTexturas();
        }

        private float timerA;
        private float cont;

        private void cargarAnimaciones() {
            TextureRegion[][] textureRegions = caminar.split(ANCHO, ALTO);
            camina = new Animation(0.25f,textureRegions[0][1],textureRegions[0][2],textureRegions[0][3], textureRegions[0][4]);
            camina.setPlayMode(Animation.PlayMode.LOOP);
            salto1 = textureRegions[0][5];
            salto2 = textureRegions[0][6];
            textureRegions = ataques.split(ANCHO,ALTO);
            dano = textureRegions[0][0];
            garrazo = new Animation(0.4f, textureRegions[0][1], textureRegions[0][2]);
            garrazo.setPlayMode(Animation.PlayMode.NORMAL);
            grito = new Animation(0.5f, textureRegions[0][3], textureRegions[0][4]);
            grito.setPlayMode(Animation.PlayMode.NORMAL);
            lluvia = textureRegions[0][5];
            ondaCocos = new Array<OndaCoco>();
            cocos = new Array<Cocos>(7);
        }

        private static void cargarTexturas() {
            manager.load("CocoSprites1.png",Texture.class);
            manager.load("CocoSprites2.png", Texture.class);
            manager.load("CocoMuerte.png", Texture.class);
            manager.load("BarraVidaCoco1.png", Texture.class);
            manager.load("BarraVidaCoco2.png", Texture.class);
            manager.finishLoading();
            caminar = new TextureRegion((Texture)manager.get("CocoSprites1.png"));
            ataques = new TextureRegion((Texture)manager.get("CocoSprites2.png"));
            neutral = caminar.split(ANCHO,ALTO)[0][0];
            muerteCoco = new TextureRegion(manager.get("CocoMuerte.png", Texture.class));
        }

        public Coco(Mapa mapa, Abner abner){
            super(neutral, X,Y);
            cargarAnimaciones();
            estadoCoco = EstadoCoco.NEUTRAL;
            faseCoco = FaseCoco.TERCERA;
            ataque = Ataque.ONDA;
            vida = 12;
            right = true;
            timer = 2;
            this.mapa = mapa;
            this.abner = abner;
            timerA = 0;
            salto = Abner.Salto.SUBIENDO;
            alturaMax = sprite.getY()+SALTOMAX;
            cont = 0.1f;
            timerD = 0;
            timerItem = 0;
            garra = new Garra(right?sprite.getX()+sprite.getWidth()-50:sprite.getX(), sprite.getY()+100, abner, right);
            cascaron = new Sprite(manager.get("BarraVidaCoco1.png", Texture.class));
            barra = new Sprite(manager.get("BarraVidaCoco2.png", Texture.class));
            cascaron.setPosition(LightsGone.ANCHO_MUNDO/2-cascaron.getWidth()/2+120, LightsGone.ALTO_MUNDO-cascaron.getHeight());
            barra.setPosition(cascaron.getX()+112,cascaron.getY()+43);
            alpha = 1;
        }

        public Rectangle getRectangle(){
            return new Rectangle(sprite.getX()+170,sprite.getY()+100, 133, 433);
        }

        @Override
        public void attack() {
            switch (ataque){
                case ONDA:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setRegion(grito.getKeyFrame(timerA));
                    if(!right){
                        sprite.flip(true, false);
                    }
                    if(timerA>=0.5f&&timerA<=0.5f+Gdx.graphics.getDeltaTime()){
                        ondaCocos.add(new OndaCoco(sprite.getX(), sprite.getY(), abner, right,mapa));
                    }
                    if(timerA>=grito.getAnimationDuration()){
                        estadoCoco = EstadoCoco.NEUTRAL;
                        timerA = 0;
                        timer = 2;
                    }
                    break;
                case GARRA:
                    if(((Math.abs(sprite.getX()-abner.getX())>200&&!right)||(Math.abs(sprite.getX()-abner.getX())>500&&right))&&timerA==0){
                        for(Proyectil proyectil: abner.getProyectiles()){
                            if((Math.abs(proyectil.getRectangle().getX()-sprite.getX())<800&&right)||(Math.abs(proyectil.getRectangle().getX()-sprite.getX())<800-ANCHO&&!right)){
                                estadoCoco = EstadoCoco.SALTANDO;
                                salto = Abner.Salto.SUBIENDO;
                                estadoTemp = EstadoCoco.ATACANDO;
                            }
                        }
                        timerC += Gdx.graphics.getDeltaTime();
                        sprite.setRegion(camina.getKeyFrame(timerC));
                        walk();

                    }
                    else{
                        timerA +=Gdx.graphics.getDeltaTime();
                        sprite.setRegion(garrazo.getKeyFrame(timerA));
                        if(!right){
                            sprite.flip(true, false);
                        }
                        if(timerA>=0.4f&&timerA<=0.4f+Gdx.graphics.getDeltaTime()){
                            garraB = true;
                        }
                        if(timerA>=garrazo.getAnimationDuration()){
                            estadoCoco = EstadoCoco.NEUTRAL;
                            timerA = 0;
                            timer = 2;
                        }
                    }
                    break;
                case LLUVIA:
                    if(cocos.size == 0){
                        float x = abner.getX()-730;
                        Random rnd = new Random();
                        for(int i =0;i<7;i++){
                            cocos.add(new Cocos(x+rnd.nextInt(LightsGone.ANCHO_MUNDO+300),mapa, abner));
                        }
                    }
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setRegion(lluvia);
                    if(timerA>=2){
                        estadoCoco = EstadoCoco.NEUTRAL;
                        timerA = 0;
                        timer = 2;
                    }
                    break;
            }
        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            timerItem += Gdx.graphics.getDeltaTime();
            if(timerItem>=15){
                Random rnd = new Random();
                float x = abner.getX()-730;
                LightsGone.agregarItem(x+rnd.nextInt(LightsGone.ANCHO_MUNDO+300), mapa.getHeight());
                timerItem = 0;
            }
            for(OndaCoco ondaCoco:ondaCocos){
                if(!ondaCoco.muerte()){
                    ondaCoco.draw(batch);
                }
                else{
                    ondaCocos.removeValue(ondaCoco, true);
                }
            }

            if(getRectangle().overlaps(abner.getBoundingRectangle())&&!abner.getDano()&&!LightsGone.getCapa()){
                abner.setCantVida(abner.getcantVida()-10);
                abner.setDano(true);
            }

            if(garraB){
                timerG += Gdx.graphics.getDeltaTime();
                garra.draw(batch, right?sprite.getX()+sprite.getWidth()-50:sprite.getX(), sprite.getY()+100);
                if(timerG>=garra.getTime()){
                    garraB = false;
                    garra.reiniciar();
                    timerG = 0;
                }
            }

            if(cocos.size!= 0){
                for(int i=0;i<cocos.size;i++){
                    Cocos coco = cocos.get(i);
                    if(coco.muerte()){
                        cocos.removeIndex(i--);
                    }
                    else{
                        coco.draw(batch);
                    }

                }
            }

            ArrayList<Proyectil> proyectils = abner.getProyectiles();
            for(int i =0;i<proyectils.size();i++){
                Proyectil proyectil = proyectils.get(i);
                if(proyectil.toString().equalsIgnoreCase("Papa")&& proyectil.getRectangle().overlaps(getRectangle())&&estadoCoco!=EstadoCoco.DANO){
                    vida--;
                    proyectil.play();
                    proyectils.remove(i);
                    estadoTemp = estadoCoco;
                    estadoCoco = EstadoCoco.DANO;
                }
                else if(proyectil.toString().equalsIgnoreCase("Caninca")&& proyectil.getRectangle().overlaps(getRectangle())&&estadoCoco!=EstadoCoco.DANO){
                    vida -= 0.5;
                }

            }

            if(vida<=0){
                muerte = true;
                estadoCoco = EstadoCoco.AGONIZANDO;

            }
            else if(vida<=4){
                faseCoco = FaseCoco.TERCERA;
            }
            else if(vida<=8){
                faseCoco = FaseCoco.SEGUNDA;
            }

            actualizar();
        }

        private void actualizar() {
            if(abner.getX()-sprite.getX()<0){
                this.right = false;
            }
            else{
                this.right = true;
            }
            switch (estadoCoco){
                case NEUTRAL:
                    for(Proyectil proyectil: abner.getProyectiles()){
                        if((Math.abs(proyectil.getRectangle().getX()-sprite.getX())<1200&&right)||(Math.abs(proyectil.getRectangle().getX()-sprite.getX())<1200-ANCHO&&!right)){
                            estadoCoco = EstadoCoco.SALTANDO;
                            salto = Abner.Salto.SUBIENDO;
                            estadoTemp = EstadoCoco.NEUTRAL;
                        }
                    }
                    if(Math.abs(abner.getX()-sprite.getX())<=1500&&timer==0){
                        estadoCoco = EstadoCoco.ATACANDO;
                        ataque = getAtaque();
                    }
                    else if (Math.abs(abner.getX()-sprite.getX())>=1500) {
                        timerC += Gdx.graphics.getDeltaTime();
                        sprite.setRegion(camina.getKeyFrame(timerC));
                        walk();
                    }
                    else if(timer!=0){
                        timer-=Gdx.graphics.getDeltaTime();
                        if(timer<=0){
                            timer=0;
                        }
                    }

                    sprite.setRegion(neutral);
                    if(!right){
                        sprite.flip(true, false);
                    }
                    break;
                case ATACANDO:
                    attack();
                    break;
                case SALTANDO:
                    if(cont>0){
                        cont-=Gdx.graphics.getDeltaTime();
                        sprite.setRegion(salto1);
                    }
                    else if(cont<=0){
                        cont = 0;
                        sprite.setRegion(salto2);

                    }

                    if(!right){
                        sprite.flip(true, false);
                    }
                    jump();
                    break;
                case DANO:
                    timerD += Gdx.graphics.getDeltaTime();
                    sprite.setRegion(dano);
                    if(!right){
                        sprite.flip(true, false);
                    }
                    if(timerD>=1){
                        estadoCoco = estadoTemp;
                        timerD = 0;
                    }
                    break;
                case AGONIZANDO:
                    sprite.setAlpha(alpha);
                    sprite.setRegion(muerteCoco);
                    alpha-=Gdx.graphics.getDeltaTime()/2;
                    if(!right){
                        sprite.flip(true, false);
                    }
                    if(alpha<=0){
                        alpha = 0;
                    }
                    break;
            }
        }

        @Override
        public void stop() {

        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        private void walk(){
            if(right){
                if(sprite.isFlipX()){
                    sprite.flip(true, false);
                }
                sprite.translate(VELOCIDAD, 0);
            }
            else {
                if(!sprite.isFlipX()){
                    sprite.flip(true, false);
                }
                sprite.translate(-VELOCIDAD, 0);
            }
        }

        private void jump(){
            switch (salto){
                case SUBIENDO:
                    sprite.translate(right?3:-3, SALTO);
                    if(sprite.getY()>= alturaMax){
                        sprite.setY(alturaMax);
                        salto = Abner.Salto.BAJANDO;
                    }
                    break;
                case BAJANDO:
                    sprite.translate(3,-CAIDA);
                    float nuevaY = mapa.colisionY(sprite.getX(), sprite.getY()-CAIDA);
                    if(nuevaY!= -1){
                        sprite.setY(nuevaY);
                        if(estadoTemp!= EstadoCoco.SALTANDO)
                            estadoCoco = estadoTemp;
                        else
                            estadoCoco = EstadoCoco.NEUTRAL;
                        alturaMax = sprite.getY()+SALTOMAX;
                        cont = 0.1f;
                    }
                    break;
            }

        }

        private Ataque getAtaque(){
            if(faseCoco==FaseCoco.PRIMERA){
                return Ataque.ONDA;
            }
            else if(faseCoco == FaseCoco.SEGUNDA){
                Random rnd = new Random();
                switch (rnd.nextInt(2)){
                    case 0:
                        return Ataque.GARRA;
                    case 1:
                        return Ataque.ONDA;
                }
            }
            else{
                Random rnd = new Random();
                switch (rnd.nextInt(3)){
                    case 0:
                        return Ataque.GARRA;
                    case 1:
                        return Ataque.ONDA;
                    default:
                        return Ataque.LLUVIA;
                }
            }
            return Ataque.ONDA;
        }

        public float getVida() {
            return vida;
        }

        public Sprite getCascaron(){
            return cascaron;
        }

        public Sprite getBarra(){
            return barra;
        }

        private enum EstadoCoco{
            ATACANDO,
            SALTANDO,
            NEUTRAL,
            DANO,
            AGONIZANDO
        }

        private enum FaseCoco{
            PRIMERA,
            SEGUNDA,
            TERCERA
        }

        private enum Ataque{
            GARRA,
            ONDA,
            LLUVIA
        }

    }

}
