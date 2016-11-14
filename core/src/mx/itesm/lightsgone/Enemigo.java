package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;


/**
 * Created by allanruiz on 07/10/16.
 */


public abstract class Enemigo {
    protected Sprite sprite;
    protected float xInicial, yInicial;
    private static AssetManager manager = new AssetManager();
    boolean ataco=false;
    protected boolean muerte;
    long startTime;
    long startTime1;

    public Enemigo(){

    }

    public Enemigo(Texture texture, float x, float y){
        xInicial = x;
        yInicial = y;
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        muerte = false;

    }

    public abstract void attack();
    public abstract void setEstado(Estado estado);
    public abstract void draw(SpriteBatch batch);

    public boolean colisiona(Proyectil p){
        return sprite.getBoundingRectangle().overlaps(p.getRectangle());
    }

    public abstract boolean muerte();

    public void setPos(float x,float y){
        sprite.setPosition(x,y);
    }

    public void setMuerte(boolean muerte){
        this.muerte = muerte;
    }

    public float getX(){
        return sprite.getX();
    }
    public float getY(){
        return sprite.getY();
    }

    public enum Estado{
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
        private int vida=1;
        private float time;




        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Sopa(float x, float y, Abner abner, Mapa mapa){
            super(neutral1, x, y);
            estado= Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa=mapa;

        }


        private static void cargarAnimacion() {
            neutral = new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(neutral2), new TextureRegion(neutral3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2),new TextureRegion(neutral1));
            dano=new Animation(0.4f, new TextureRegion(mid), new TextureRegion(low));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        @Override
        public void attack() {
            if(!abner.getDano()) {
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

                if (Math.abs(abner.getX()-sprite.getX()) < 200) {
                    estado = Estado.ATAQUE;
                }
                else {
                    estado = Estado.NEUTRAL;
                }

            actualizar();
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(getRectangle())){
                if(ataco==false && estado== Estado.ATAQUE) {
                    if(time==0) {
                       attack();
                    }
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            if(!abner.getProyectiles().isEmpty()&& sprite.getTexture()!=low) {
                if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida=0;
                    abner.borrarProyectiles();
                }
            }



            if(vida<=0){
                //sprite.setTexture(low);
                estado= Estado.DANO;
                time += Gdx.graphics.getDeltaTime();
            }

            if((time>=2)){

                estado=Estado.NEUTRAL;
                vida=1;
                time=0;
            }



            float distancia = sprite.getX() - abner.getX();
            if(estado!=Estado.DANO) {
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
        private static void cargarTexturas(){
            manager.load("SopaAtaque1.png", Texture.class);
            manager.load("SopaAtaque2.png", Texture.class);
            manager.load("SopaNeutral1.png", Texture.class);
            manager.load("SopaNeutral2.png", Texture.class);
            manager.load("SopaNeutral3.png", Texture.class);
            manager.load("SopaLow.png",Texture.class);
            manager.load("SopaMid.png",Texture.class);
            manager.finishLoading();

            ataque1 = manager.get("SopaAtaque1.png");
            ataque2 = manager.get("SopaAtaque2.png");
            neutral1=manager.get("SopaNeutral1.png");
            neutral2 = manager.get("SopaNeutral2.png");
            neutral3 = manager.get("SopaNeutral3.png");
            mid=manager.get("SopaMid.png");
            low=manager.get("SopaLow.png");
        }

        @Override
        public String toString() {
            return "Sopa";
        }


        public Rectangle getRectangle() {
            return new Rectangle(sprite.getX()+71,sprite.getY(), 262,153);
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

        public Brocoli(float x, float y, Abner abner, Mapa mapa){
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
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {
            //Colisiones con abner
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
            if(!abner.getProyectiles().isEmpty()) {
                if(abner.getProyectiles().get(0).toString().equals("Canica")){
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                MapManager.quitarEnemigo(this);
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
        private static Texture neutraltost, ataquetost1, ataquetost2, ataquetost3,pan;
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

        public Tostadora(float x, float y, Abner abner, Mapa mapa){
            super(neutraltost, x, y);
            estado= Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            batch = new SpriteBatch();
            this.mapa=mapa;

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
            float distancia = sprite.getX()-abner.getX();
            if(estado!= Estado.DANO) {
                if (Math.abs(distancia) <= 600) {
                    estado = Estado.ATAQUE;
                }
                else{ estado= Estado.NEUTRAL;}

            }
            actualizar();
        }


        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {

            switch(estado){
                case NEUTRAL:
                    sprite.setTexture(neutraltost);
                    break;

                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                    if(ataque.getKeyFrame(timerA).getTexture().equals(ataquetost2)){


                    }

                    break;
                case DANO:
                    timerD += Gdx.graphics.getDeltaTime();
                    if(timerD>5){
                        timerD = 0;
                        estado = Estado.NEUTRAL;
                    }

            }
        }

        private static void cargarTexturas(){

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
        private String direccion="arriba";
        private float posicionInicial;

        static {
            cargarTexturas();
        }

        public PanTostadora(float x, float y, Abner abner, Mapa mapa){
            super(pan, x, y);
            this.abner = abner;
            this.mapa=mapa;
            posicionInicial=sprite.getY();


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-3);
                ataco=true;
                abner.setDano(true);
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            actualizar();
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+100,sprite.getY()+20,sprite.getWidth()-100,sprite.getHeight()-40))){
                if(ataco==false) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            direccion = "arriba";

            if (sprite.getY()>=posicionInicial+900) {
                sprite.setY(posicionInicial);
            }


            if (direccion=="arriba")
                sprite.setY(sprite.getY()+16);
            if (direccion=="abajo")
                sprite.setY(sprite.getY()-16);


            }

        private static void cargarTexturas() {
            manager.load("Pan.png", Texture.class);


            manager.finishLoading();
            pan = manager.get("Pan.png");



        }

        }

    static class Mosca extends Enemigo {
        private static Texture mosca1,mosca2,mosca3;
        private static Animation caminar, ataque;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama=10;
        private boolean ataq=false;
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

        public Mosca(float x, float y, Abner abner, Mapa mapa){
            super(mosca1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            vida = 1;
            this.mapa=mapa;
            posXOriginal=sprite.getX();
            posYOriginal=sprite.getY();
            sprite.setSize(10,10);

            time=0;
        }

        @Override
        public void attack() {
            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-3);
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
            if(Math.abs(abner.getY()-posYOriginal)<400) {
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
        public boolean muerte() {
            return muerte;
        }

        public Abner getAbner(){
            return abner;
        }

        public Mapa getMapa(){
            return mapa;
        }

        private void actualizar() {
            if(abner.getX()>posXOriginal){
                direccion=-1;
                if(!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }
            else {
                direccion=1;
                if(sprite.isFlipX()){
                    sprite.flip(true,false);
                }
            }

            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                if(!ataco) {
                    attack();
                   startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            if(!abner.getProyectiles().isEmpty()) {
                if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida -= 1;
                    abner.borrarProyectiles();
                }
            }

            if(Math.abs(sprite.getX()-xInicial)>1500){
                sprite.setPosition(xInicial,yInicial);
            }

            if(vida<=0) {
                sprite.setPosition(20000,20000);
                muerte = true;

            }

          if(Math.abs(sprite.getX()-20000)<200) {
                time += Gdx.graphics.getDeltaTime();
            }

            if((time>=2)){

                MapManager.crearNuevaMosca(xInicial,yInicial,this);
                MapManager.quitarEnemigo(this);
                time=0;
            }

            switch (estado){
                case NEUTRAL:
                    sprite.setSize(10,10);
                    break;
                case ATAQUE:
                    if(tama<=100)
                        sprite.setSize(tama+=5,tama);
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());

                    sprite.setX(sprite.getX()-8*direccion);
                    sprite.setY(abner.getY()+(abner.getHeight()/2)-50);

                    break;
                case DANO:
                    Gdx.app.log("Vida", vida+"");
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
        private int tama=10;
        private boolean ataq=false;
        private Mapa mapa;
        long contador=0;
        long contador1=0;
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

        public Fuego(float x, float y, Abner abner, Mapa mapa){
            super(fuegoPrepara1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            this.mapa=mapa;
            vida = 3;
            posXOriginal=sprite.getX();
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

            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-1);
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
            if(contador%120==0){
                contador1++;
                if(contador1%2==0){
                    estado= Estado.ATAQUE;
                }
                else{
                    estado= Estado.NEUTRAL;
                }
            }
                actualizar();

        }

        @Override
        public boolean muerte() {
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+100,sprite.getY(),sprite.getWidth()-100,sprite.getHeight()))){
                if(estado== Estado.ATAQUE) {
                    attack();
                    if(abner.getX()>posXOriginal){
                        direccion=-1;
                    }
                    else {
                        direccion=1;
                    }
                    abner.setX(abner.getX()-(float)10*direccion);
                    abner.ajusteCamara(direccion);
                }
            }

            switch (estado){
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral.getKeyFrame(timer).getTexture());
                    sprite.setSize(250,100);
                    break;
                case ATAQUE:
                   timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());
                    sprite.setSize(250,400);

                    break;
                case DANO:
                    Gdx.app.log("Vida", vida+"");
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
        private final float MOVCAIDA = 18f, MOVRODANDO = 0.2125f*MOVX;
        private Mapa mapa;

        static {
            cargarTexturas();
        }

        private float mov;

        private static void cargarTexturas() {
            manager.load("Lata.png", Texture.class);
            manager.finishLoading();
            neutral = manager.get("Lata.png");
        }

        public Lata(float x, float y, Mapa mapa){
            super(neutral, x, y);
            estadoLata = EstadoLata.CAYENDO;
            alto = alturaOriginal = sprite.getHeight();
            this.mapa = mapa;
        }

        public Lata(float x, float y, Mapa mapa, String string){
            super(neutral, x, y);
            estadoLata = EstadoLata.RODANDOPISO;
            this.mapa = mapa;
        }

        public Rectangle getBoundingRectangle(){
            if(estadoLata== EstadoLata.RODANDOPISO){
                return new Rectangle(sprite.getX()+MOVX, sprite.getY(), sprite.getWidth(), sprite.getHeight());
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

        private void actualizar() {
            switch (estadoLata){
                case CAYENDO:
                    sprite.setRegion(0, 0, (int)sprite.getWidth(), (int)alto);
                    sprite.setSize(sprite.getWidth(), alto);
                    if (mapa.colisionLata(sprite.getX() + sprite.getWidth() / 2, sprite.getY())) {
                        estadoLata = EstadoLata.DESAPARECIENDO;
                        sprite.translate(-40,-30);
                    }
                    else if(!mapa.colisionInclinada(sprite.getX()+sprite.getWidth()/2, sprite.getY()+MOVCAIDA)){
                        sprite.translate(0,-MOVCAIDA);
                    }
                    else{
                        estadoLata = EstadoLata.RODANDO;
                    }
                    break;
                case RODANDO:
                    if(!mapa.colisionInclinada(sprite.getX()+sprite.getWidth()/2, sprite.getY()+MOVCAIDA)){
                        estadoLata = EstadoLata.CAYENDO;
                    }
                    else{
                        sprite.translate(-MOVX, -MOVRODANDO);
                        sprite.rotate(10);
                    }
                    break;
                case DESAPARECIENDO:
                    sprite.setRotation(0);
                    alto -= 5;
                    if(alto<=0){
                        estadoLata = EstadoLata.CAYENDO;
                        sprite.setPosition(4871, mapa.getHeight());
                        alto = alturaOriginal;
                    }
                    sprite.setRegion(0, 0, (int)sprite.getWidth(), (int)alto);
                    sprite.setSize(sprite.getWidth(), alto);
                    break;
                case RODANDOPISO:
                    sprite.translate(MOVX, 0);
                    sprite.rotate(rotation);
                    if(mapa.colisionX(sprite.getX()+sprite.getWidth(), sprite.getY()+sprite.getHeight())||mapa.colisionX(sprite.getX(), sprite.getY()+sprite.getHeight())){
                        MOVX*=-1;
                        rotation*= -1;
                    }
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        public boolean piso(){
            return estadoLata == EstadoLata.RODANDOPISO;
        }

        public float getMov() {
            return MOVX;
        }

        private enum EstadoLata{
            CAYENDO,
            RODANDO,
            DESAPARECIENDO,
            RODANDOPISO
        }

        @Override
        public String toString(){
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

        public Serpiente(float x, float y, Abner abner, Mapa mapa){
            super(walk1, x, y);
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
                abner.setCantVida(abner.getcantVida()-8);
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
        public boolean muerte() {
            return muerte;
        }

        private void actualizar() {

            //Colisiones con abner
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
            if(!abner.getProyectiles().isEmpty()) {
                if(abner.getProyectiles().get(0).toString().equals("Canica")){
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                xInicial = sprite.getX()+sprite.getWidth()/2;
                yInicial = sprite.getY();
                muerte = true;
                MapManager.quitarEnemigo(this);
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

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Hongo(float x, float y, Abner abner, Mapa mapa){
            super(neutralH, x, y);
            estado= Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            vida=4;
            batch = new SpriteBatch();
            this.mapa=mapa;

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
            float distancia = sprite.getX()-abner.getX();
            if(estado!= Estado.DANO) {
                if (Math.abs(distancia) <= 600) {
                    estado = Estado.ATAQUE;
                }
                else{ estado= Estado.NEUTRAL;}

            }
            actualizar();
        }


        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {

            if(!abner.getProyectiles().isEmpty()) {
                if(abner.getProyectiles().get(0).toString().equals("Canica")){
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }else {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                muerte = true;
                MapManager.quitarEnemigo(this);
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

            switch(estado){
                case NEUTRAL:
                    sprite.setTexture(neutralH);
                    break;

                case ATAQUE:
                    timerA += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                    if(ataque.getKeyFrame(timerA).getTexture().equals(ataque)){
                        estado= Estado.NEUTRAL;
                    }
                    break;
                case DANO:
                    timerD += Gdx.graphics.getDeltaTime();
                    if(timerD>5){
                        timerD = 0;
                        estado = Estado.NEUTRAL;
                    }

            }
        }

        private static void cargarTexturas(){

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
        private boolean disparado=false;



        static {
            cargarTexturas();
        }

        public ProyectilHongo(float x, float y, Abner abner, Mapa mapa){
            super(pan, x, y);
            this.abner = abner;
            this.mapa=mapa;
            posicionInicial=sprite.getY();
            posXOriginal=sprite.getX();
            posYOriginal=sprite.getY();
            color=sprite.getColor();


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-6);
                ataco=true;
                abner.setDano(true);
            }
        }


        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);


            actualizar();
        }



        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+40,sprite.getY()+20,sprite.getWidth()-80,sprite.getHeight()-40))){
                if(ataco==false && estado== Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            if(disparado==false) {
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

            if(Math.abs(abner.getX()-posXOriginal)<660) {
                estado= Estado.ATAQUE;

            }

            if(estado== Estado.ATAQUE){
                sprite.setX(sprite.getX() - 8 * direccion);
                disparado=true;
            }

            if(Math.abs(abner.getX()-posXOriginal)>660 && Math.abs(sprite.getX()-posXOriginal)<60){
                estado= Estado.NEUTRAL;
                //sprite.setX(posXOriginal);

            }

            if(Math.abs(sprite.getX()-posXOriginal)>70){
                sprite.setColor(color);

            }else{
                sprite.setColor(0);
            }

            if(Math.abs(sprite.getX()-posXOriginal)>660){
                sprite.setColor(0);
                sprite.setX(posXOriginal);
                disparado=false;
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
        private static Texture neutralG,advertencia,lanzamiento,caida,muerte;
        private static Animation advertenciaA, muerteG;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private String direccion="arriba";
        private float posicionInicial;
        private Estado estado;
        private float timer, timerA;
        private int vida=1;
        private boolean lanzado=false;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            advertenciaA = new Animation(0.3f, new TextureRegion(neutralG), new TextureRegion(advertencia));
            muerteG= new Animation(0.3f, new TextureRegion(muerte));
            advertenciaA.setPlayMode(Animation.PlayMode.LOOP);
            muerteG.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Gnomo(float x, float y, Abner abner, Mapa mapa){
            super(neutralG, x, y);
            this.abner = abner;
            this.mapa=mapa;
            posicionInicial=sprite.getY();
            timer = timerA=0;
            estado=Estado.NEUTRAL;


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-8);
                ataco=true;
                abner.setDano(true);
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            actualizar();
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())&& timerA==0){
                if(ataco==false) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            if(!abner.getProyectiles().isEmpty()) {
                if(abner.getProyectiles().get(0).toString().equals("Canica")){
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }else {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                //muerte = true;
                MapManager.quitarEnemigo(this);
            }

            if (Math.abs(abner.getX()-sprite.getX()) <= 700 && Math.abs(abner.getX()-sprite.getX()) > 600 && lanzado==false) {
                timer += Gdx.graphics.getDeltaTime();
                sprite.setTexture(advertenciaA.getKeyFrame(timer).getTexture());


            }

            if (Math.abs(abner.getX()-sprite.getX()) <= 600) {
                estado = Estado.ATAQUE;
            }





            if (sprite.getY()>=posicionInicial+1300) {
                direccion="abajo";
                sprite.setTexture(caida);
                sprite.setX(abner.getX());
            }
            if(sprite.getY()<posicionInicial && lanzado==true){
                timer+=Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer*2).getTexture());
                //sprite.setTexture(muerte);

            }

            if(timerA>1){
                MapManager.quitarEnemigo(this);
            }

            if(estado== Estado.ATAQUE) {
                if (direccion == "arriba") {
                    sprite.setTexture(lanzamiento);
                    sprite.setY(sprite.getY() + 16);

                }
                if (direccion == "abajo" && timerA==0) {
                    sprite.setY(sprite.getY() - 16);
                    lanzado=true;
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
            muerte= manager.get("muerte.png");

        }

        @Override
        public String toString() {
            return "Gnomo";
        }



    }

    static class GnomoL extends Enemigo {
        private static Texture neutralG,advertencia,lanzamiento,caida,muerte;
        private static Animation advertenciaA, muerteG;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private String direccion="arriba";
        private float posicionInicial;
        private Estado estado;
        private float timer, timerA;
        private int vida=1;
        private boolean lanzado=false;

        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {
            advertenciaA = new Animation(0.3f, new TextureRegion(neutralG), new TextureRegion(advertencia));
            muerteG= new Animation(0.3f, new TextureRegion(muerte));
            advertenciaA.setPlayMode(Animation.PlayMode.LOOP);
            muerteG.setPlayMode(Animation.PlayMode.LOOP);

        }

        public GnomoL(float x, float y, Abner abner, Mapa mapa){
            super(lanzamiento, x, y);
            this.abner = abner;
            this.mapa=mapa;
            posicionInicial=sprite.getY();
            timer = timerA=0;
            estado=Estado.NEUTRAL;


        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void attack() {
            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-8);
                ataco=true;
                abner.setDano(true);
            }
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);

            actualizar();
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())&& timerA==0){
                if(ataco==false) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }

            if(!abner.getProyectiles().isEmpty()) {
                if(abner.getProyectiles().get(0).toString().equals("Canica")){
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }else {
                    if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 4;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {
                //muerte = true;
                MapManager.quitarEnemigo(this);
            }



            if(!sprite.isFlipY()){
                sprite.flip(false,true);
            }


            if(abner.getX()>19750 && abner.getX()<20610) {
                estado = Estado.ATAQUE;

            }



            if(sprite.getY()<abner.getY() && lanzado==true && !abner.isJumping()){
                timer+=Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer*2).getTexture());
                if(sprite.isFlipY())
                    sprite.flip(false,true);
                //sprite.setTexture(muerte);

            }
            if(sprite.getY()<abner.getY()-Abner.SALTOMAX && lanzado==true && abner.isJumping()){
                timer+=Gdx.graphics.getDeltaTime();
                timerA += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer*2).getTexture());
                if(sprite.isFlipY())
                    sprite.flip(false,true);
                //sprite.setTexture(muerte);

            }

            if(timerA>1){
                //MapManager.invocarLluviaDeGnomos(abner,mapa);
                MapManager.quitarEnemigo(this);

            }

            if(estado== Estado.ATAQUE && timerA==0) {


                    sprite.setY(sprite.getY() - 16);
                    lanzado=true;

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
            muerte= manager.get("muerte.png");

        }

        @Override
        public String toString() {
            return "Gnomo";
        }



    }

    static class Cucarachon extends Enemigo {
        private static Texture neutral1, ataque1, ataque2;
        private static Animation  ataque;
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

            ataque = new Animation(0.2f,new TextureRegion(neutral1), new TextureRegion(ataque1), new TextureRegion(ataque2));

            ataque.setPlayMode(Animation.PlayMode.LOOP);

        }

        public Cucarachon (float x, float y, Abner abner, Mapa mapa){
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            vida = 3;
            this.mapa=mapa;
            xInicial=sprite.getX();
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

            actualizar();
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

            if(!abner.getProyectiles().isEmpty()) {
                if(abner.getProyectiles().get(0).toString().equals("Canica")){
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        //vida -= 1;
                        abner.borrarProyectiles();
                    }
                }else {
                    if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                        vida -= 1;
                        abner.borrarProyectiles();
                    }
                }
            }

            if(vida<=0) {

                muerte = true;
                ///sprite.setPosition(20000,20000);
                MapManager.quitarEnemigo(this);
            }

            if(abner.getX()>sprite.getX()&& Math.abs(abner.getX()-sprite.getX())>400){
                direccion=-1;
                if(sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }

            if(abner.getX()<=sprite.getX()&& Math.abs(abner.getX()-sprite.getX())>400){
                direccion=1;
                if(!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
            }




            if (Math.abs(sprite.getX() - abner.getX()) <= 1400 && Math.abs(sprite.getY()-abner.getY())<400) {
                estado = Estado.ATAQUE;
                time += Gdx.graphics.getDeltaTime();
            }else{
                estado= Estado.NEUTRAL;
            }

            if(sprite.getX()<xInicial){
                sprite.setX(sprite.getX()+1);
            }


            switch (estado) {
                case NEUTRAL:
                    sprite.setTexture(neutral1);
                    break;
                case ATAQUE:

                    if(time>=1) {
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
        private static Texture scarecrow1,scarecrow2,scarecrow3;
        private Estado estado;
        private boolean right, ata;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private int tama=10;
        private boolean ataq=false;
        private Mapa mapa;
        long contador=0;
        long contador1=0;
        float posXOriginal;
        int direccion;


        static {
            cargarTexturas();

        }





        public Scarecrow(float x, float y, Abner abner, Mapa mapa){
            super(scarecrow1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            this.mapa=mapa;
            vida = 3;
            posXOriginal=sprite.getX();
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

            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-1);
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
        public boolean muerte() {
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+100,sprite.getY(),sprite.getWidth()-100,sprite.getHeight()))){
                if(estado== Estado.ATAQUE) {
                    attack();
                    if(abner.getX()>posXOriginal){
                        direccion=-1;
                    }
                    else {
                        direccion=1;
                    }
                    abner.setX(abner.getX()-(float)10*direccion);
                    abner.ajusteCamara(direccion);
                }
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

            if(Math.abs(abner.getX()-sprite.getX())>400)
                estado= Estado.NEUTRAL;
            if(Math.abs(abner.getX()-sprite.getX())<=400 && Math.abs(abner.getX()-sprite.getX())>=200)
                estado= Estado.DANO;
            if(Math.abs(abner.getX()-sprite.getX())<200)
                estado= Estado.ATAQUE;

            switch (estado){
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
        private static Texture neutral,ataque;
        private Estado estado;
        private boolean bandera=true;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private float time;

        private boolean ataq=false;
        private Mapa mapa;

        long contador1=0;
        float posXOriginal;
        int direccion;
        private long contador=0;


        static {
            cargarTexturas();

        }





        public Espinas(float x, float y, Abner abner, Mapa mapa){
            super(neutral, x, y);
            estado = Estado.NEUTRAL;
            this.abner = abner;
            this.mapa=mapa;
            posXOriginal=sprite.getX();


        }



        @Override
        public void attack() {

            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-1);
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
        public boolean muerte() {
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+100,sprite.getY(),sprite.getWidth()-100,sprite.getHeight()))){
                if(estado== Estado.ATAQUE) {
                    attack();
                    if(abner.getX()>posXOriginal){
                        direccion=-1;
                    }
                    else {
                        direccion=1;
                    }
                    abner.setX(abner.getX()-(float)10*direccion);
                    abner.ajusteCamara(direccion);
                }
            }



            if(abner.getX()>19750 && abner.getX()<20610 && bandera==true) {
                estado = Estado.ATAQUE;


            }


            switch (estado){
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    break;
                case ATAQUE:
                    // if(time>2) {
                    sprite.setTexture(ataque);
                    contador++;
                    if(contador % 120==0)
                        MapManager.invocarLluviaDeGnomos(abner,mapa);
                    if (contador % 600 == 0) {
                        bandera = false;
                        estado=Estado.NEUTRAL;
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
        private static Texture neutral,ataque;
        private Estado estado;
        private boolean bandera=true;
        private final float velocidad = 2f;
        private float timer, timerA;
        private Abner abner;
        private int vida;
        private float time;

        private boolean ataq=false;
        private Mapa mapa;

        long contador1=0;
        float posXOriginal;
        int direccion;
        private long contador=0;


        static {
            cargarTexturas();

        }


        public EspinasD(float x, float y, Abner abner, Mapa mapa){
            super(neutral, x, y);
            estado = Estado.NEUTRAL;
            this.abner = abner;
            this.mapa=mapa;
            posXOriginal=sprite.getX();


        }



        @Override
        public void attack() {

            if(!abner.getDano()){
                abner.setCantVida(abner.getcantVida()-1);
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
        public boolean muerte() {
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+100,sprite.getY(),sprite.getWidth()-100,sprite.getHeight()))){
                if(estado== Estado.ATAQUE) {
                    attack();
                    if(abner.getX()>posXOriginal){
                        direccion=-1;
                    }
                    else {
                        direccion=1;
                    }
                    abner.setX(abner.getX()-(float)10*direccion);
                    abner.ajusteCamara(direccion);
                }
            }
            if(!sprite.isFlipY()) {
                sprite.flip(false, true);
            }

            time += Gdx.graphics.getDeltaTime();

            if(time>3){
                if(estado==Estado.ATAQUE){
                    estado=Estado.NEUTRAL;
                }else{
                    estado=Estado.ATAQUE;
                }

                time=0;
            }





            switch (estado){
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    break;
                case ATAQUE:
                    // if(time>2) {
                    sprite.setTexture(ataque);
                    contador++;
                    if (contador % 600 == 0) {
                        bandera = false;
                        estado=Estado.NEUTRAL;
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

    private static class CajaPayaso extends Enemigo {
        public static final int ANCHOCAJA = 223;
        private Abner abner;
        private Mapa mapa;
        private static Texture neutral;
        private EstadoCaja estadoCaja;
        private float timer;
        private static Animation cajaMovil, cajaAtaque, cajaPayaso;
        private static TextureRegion[] cajaMovilTex, cajaAtaqueTex, cajaPayasoTex;

        static{
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
            manager.load("cajapayaso1.png",Texture.class);
            manager.load("cajapayaso2.png", Texture.class);
            manager.load("cajapayaso3.png", Texture.class);
            manager.load("Cajapayaso4.png", Texture.class);
            manager.load("cajapayasosaliendo1.png", Texture.class);
            manager.load("cajapayasosaliendo2-min.png",Texture.class);
            manager.load("cajapayasosaliendo3.png", Texture.class);
            manager.load("cajapayasosaliendo4.png",Texture.class);
            manager.load("payasoexplotando1.png",Texture.class);
            manager.load("payasoexplotando2.png",Texture.class);
            manager.load("payasoexplotando4.png", Texture.class);
            manager.load("payasoexplotando5.png", Texture.class);
            manager.load("payasoexplotando6.png", Texture.class);
            manager.finishLoading();
            neutral = manager.get("cajapayaso1.png");
            cajaMovilTex = new TextureRegion[]{new TextureRegion((Texture) manager.get("cajapayaso1.png")),new TextureRegion((Texture) manager.get("cajapayaso2.png")),
                    new TextureRegion((Texture) manager.get("cajapayaso3.png")), new TextureRegion((Texture) manager.get("Cajapayaso4.png"))};
            cajaPayasoTex = new TextureRegion[]{new TextureRegion((Texture) manager.get("cajapayasosaliendo1.png")),new TextureRegion((Texture) manager.get("cajapayasosaliendo2-min.png")),
                    new TextureRegion((Texture) manager.get("cajapayasosaliendo3.png")),new TextureRegion((Texture) manager.get("cajapayasosaliendo4.png"))
            };
            cajaAtaqueTex = new TextureRegion[]{
                    new TextureRegion((Texture) manager.get("payasoexplotando1.png")),
                    new TextureRegion((Texture) manager.get("payasoexplotando2.png")),new TextureRegion((Texture) manager.get("payasoexplotando4.png")), new TextureRegion((Texture) manager.get("payasoexplotando5.png")),
                    new TextureRegion((Texture) manager.get("payasoexplotando6.png"))
            };



        }
        public CajaPayaso(float x, float y, Abner abner, Mapa mapa){
            super(neutral, x,y);
            this.mapa = mapa;
            this.abner = abner;
            this.estadoCaja = EstadoCaja.NEUTRAL;
            timer = 0;
        }

        @Override
        public void attack() {
            if(abner.getBoundingRectangle().overlaps(getAttackRectangle())&&isAttacking()){
                if(!abner.getDano()){
                    abner.setDano(true);
                    abner.setCantVida(abner.getcantVida()-10);
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

        private void actualizar(){
            switch (estadoCaja){
                case NEUTRAL:
                    if(Math.abs(abner.getX()-sprite.getX())<=530){
                        estadoCaja = EstadoCaja.CAJAMOVIL;
                    }
                    break;
                case CAJAMOVIL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(cajaMovil.getKeyFrame(timer).getTexture());
                    if(timer>=5){
                        estadoCaja = EstadoCaja.PAYASOFUERA;
                        timer = 0;
                    }
                    break;
                case PAYASOFUERA:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(cajaPayaso.getKeyFrame(timer).getTexture());
                    if(timer>=cajaPayaso.getAnimationDuration()){
                        estadoCaja = EstadoCaja.NEUTRALPAYASO;
                        timer = 0;
                    }
                    break;
                case NEUTRALPAYASO:
                    timer+= Gdx.graphics.getDeltaTime();
                    if(timer>=1){
                        estadoCaja = EstadoCaja.CAJAATAQUE;
                        timer = 0;
                    }
                    break;
                case CAJAATAQUE:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(cajaAtaque.getKeyFrame(timer).getTexture());
                    if(timer>=cajaAtaque.getAnimationDuration()){
                        muerte = true;
                        timer = 0;
                    }
            }
        }

        @Override
        public boolean muerte() {
            return muerte;
        }

        public boolean isAttacking(){
            return estadoCaja==EstadoCaja.CAJAATAQUE && timer>=0.4f;
        }

        public boolean floor(float x, float y){
            if(estadoCaja==EstadoCaja.CAJAMOVIL)
                return getBoxRectangle().contains(x,y);
            return false;
        }

        public void move(float x,float y, boolean right, float velocidad){
            if(estadoCaja==EstadoCaja.CAJAMOVIL){
                if(getBoxRectangle().contains(x,y)){
                    if(!mapa.colisionX(sprite.getX()+45, sprite.getY()+30)&&!mapa.colisionX(sprite.getX()+45+ANCHOCAJA, sprite.getY()+30)){
                        if(right){
                            sprite.translate(velocidad, 0);
                        }
                        else{
                            sprite.translate(-velocidad,0);
                        }
                    }
                }
                if(!mapa.colisionCaja(sprite.getX()+sprite.getWidth()/2, sprite.getY(), true, -1)){
                    caer();
                }
            }
        }

        private void caer() {
            sprite.translate(0,-18f);
        }

        Rectangle getBoxRectangle(){
            return new Rectangle(sprite.getX()+35, sprite.getY(), 199,165);
        }

        public Rectangle getAttackRectangle() {
            return new Rectangle(sprite.getX(), sprite.getY(), ANCHOCAJA*2, sprite.getHeight());
        }

        private enum EstadoCaja{
            NEUTRAL,
            CAJAMOVIL,
            CAJAATAQUE,
            NEUTRALPAYASO,
            PAYASOFUERA
        }

        @Override
        public String toString(){
            return "CajaPayaso";
        }
    }

    static class GeneradorCajasPayaso extends Enemigo{
        private CajaPayaso cajaPayaso;
        private float x,y;
        private Abner abner;
        private Mapa mapa;
        private float timer;

        public GeneradorCajasPayaso(float x, float y, Mapa mapa, Abner abner){
            super();
            this.x = x;
            this.y = y;
            this.mapa = mapa;
            this.abner = abner;
            cajaPayaso = new CajaPayaso(x,y, abner, mapa);
            timer = 0;
        }

        @Override
        public void attack() {
            if(cajaPayaso!=null)
                cajaPayaso.attack();
        }

        @Override
        public void setEstado(Estado estado) {

        }


        public boolean isAttacking(){
            if(cajaPayaso!=null){
                return cajaPayaso.isAttacking();
            }
            return false;
        }

        @Override
        public void draw(SpriteBatch batch) {
            if(cajaPayaso!= null && cajaPayaso.muerte()){
                cajaPayaso= null;
            }

            if(cajaPayaso!=null){
                cajaPayaso.draw(batch);
            }
            else {
                actualizar();
            }

        }

        private void actualizar() {
            timer += Gdx.graphics.getDeltaTime();
            if(timer>=10){
                cajaPayaso = new CajaPayaso(x,y,abner,mapa);
                timer = 0;
            }

        }

        @Override
        public boolean muerte() {
            return false;
        }

        public void move(float x, float y, boolean right, float velocidad) {
            if(cajaPayaso!=null){
                cajaPayaso.move(x,y,right,velocidad);
            }
        }

        public Rectangle getAttackRectangle(){
            return cajaPayaso.getAttackRectangle();
        }

        public Rectangle getBoxRectangle(){
            return cajaPayaso.getBoxRectangle();
        }

        public boolean floor(float x, float y) {
            if(cajaPayaso!=null){
                return cajaPayaso.floor(x,y);
            }
            return false;
        }

        public String toString(){
            return "CajaPayaso";
        }
    }

    static class Robot extends Enemigo{
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
            manager.load("R_A2.png",Texture.class);
            manager.load("R_A3.png", Texture.class);
            manager.load("R_A4.png",Texture.class);
            manager.load("R_ball.png", Texture.class);
            manager.load("R_Off.png", Texture.class);
            manager.load("R_On.png", Texture.class);
            manager.finishLoading();
            textureRegions = new Array<TextureRegion>(5);
            textureRegions.add(new TextureRegion((Texture)manager.get("R_On.png")));
            textureRegions.add(new TextureRegion((Texture)manager.get("R_A1.png")));
            textureRegions.add(new TextureRegion((Texture)manager.get("R_A2.png")));
            textureRegions.add(new TextureRegion((Texture)manager.get("R_A3.png")));
            textureRegions.add(new TextureRegion((Texture)manager.get("R_A4.png")));
            proyectil = manager.get("R_ball.png");
            neutral = manager.get("R_Off.png");

        }


        public Robot(float x, float y, Abner abner){
            super(neutral,x,y);
            estadoRobot = EstadoRobot.APAGADO;
            this.abner = abner;
            this.proyectiles = new Array<Proyectil.Bola>();
            this.vida = 3;
            timerOn = 0;
        }

        @Override
        public void attack() {

            for(int i =0;i<proyectiles.size;i++){
                Proyectil.Bola proyectil = proyectiles.get(i);
                if(proyectil.getRectangle().overlaps(abner.getBoundingRectangle())&&!abner.getDano()){
                    abner.setCantVida(abner.getcantVida()-10);
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
            if(right&&sprite.isFlipX()){
                sprite.flip(true, false);
            }
            else if(!right&&!sprite.isFlipX()){
                sprite.flip(true, false);
            }
            actualizar();
            attack();
            for(Proyectil.Bola bola:proyectiles){
                if(bola.out()){
                    proyectiles.removeValue(bola, true);
                }
                else
                    bola.draw(batch);
            }
        }


        private void actualizar() {
            switch (estadoRobot){
                case APAGADO:
                    if(Math.abs(sprite.getX() - abner.getX())<=530){
                        timerOn += Gdx.graphics.getDeltaTime();
                        if(timerOn>=2){
                            estadoRobot = EstadoRobot.ATACANDO;
                            timerOn = 0;
                        }
                    }
                    break;
                case ATACANDO:
                    timer +=Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());
                    if(timer>=0.4f && timer<=0.4f+Gdx.graphics.getDeltaTime()){
                        proyectiles.add(new Proyectil.Bola(proyectil,right?sprite.getX()+242:sprite.getX()+94, sprite.getY()+92, right));
                    }
                    else if(timer>=0.6f && timer<=0.6f+Gdx.graphics.getDeltaTime()){
                        proyectiles.add(new Proyectil.Bola(proyectil,right?sprite.getX()+270:sprite.getX()+68, sprite.getY()+92, right));
                    }
                    if(timer>=ataque.getAnimationDuration()){
                        estadoRobot = EstadoRobot.APAGADO;
                        timer = 0;
                    }
                    break;
            }

            if(sprite.getX()-abner.getX()<0){
                right = true;
            }
            else{
                right = false;
            }
            ArrayList<Proyectil> proyectils = abner.getProyectiles();
            for(int i=0;i<proyectils.size();i++){
                Proyectil proyectil = proyectils.get(i);
                if(proyectil.getRectangle().overlaps(sprite.getBoundingRectangle())&&proyectil.toString().equalsIgnoreCase("Papa")){
                    vida--;
                    proyectils.remove(i--);
                }
            }

            if(vida<=0){
                MapManager.quitarEnemigo(this);
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private enum EstadoRobot{
            APAGADO,
            ATACANDO
        }
    }

    static class MonstruoRopa extends Enemigo{

        private static Texture neutral;
        private static Animation respirando, levantando, atrapar, atrapado;
        private static Array<TextureRegion> respirandoTex, levantandoTex, atraparTex, atrapadoTex;
        private final Abner abner;
        private EstadoRopa estadoRopa;
        private float timer, timerAtaque;
        private boolean right;

        static{
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
            manager.load("monstruoropa6.png",Texture.class);
            manager.load("monstruoropa7.png", Texture.class);
            manager.load("monstruoropa8.png", Texture.class);
            manager.load("monstruoropa9.png", Texture.class);
            manager.load("monstruoropa10.png", Texture.class);
            manager.load("monstruoropa11.png", Texture.class);
            manager.finishLoading();
            respirandoTex = new Array<TextureRegion>();
            levantandoTex = new Array<TextureRegion>();
            atraparTex = new Array<TextureRegion>();
            atrapadoTex = new Array<TextureRegion>();
            neutral = manager.get("monstruoropa1.png");
            levantandoTex.add(new TextureRegion((Texture)manager.get("monstruoropa2.png")));
            levantandoTex.add(new TextureRegion((Texture)manager.get("monstruoropa3.png")));
            respirandoTex.add(new TextureRegion((Texture)manager.get("monstruoropa4.png")));
            respirandoTex.add(new TextureRegion((Texture)manager.get("monstruoropa5.png")));
            atraparTex.add(new TextureRegion((Texture)manager.get("monstruoropa6.png")));
            atraparTex.add(new TextureRegion((Texture)manager.get("monstruoropa7.png")));
            atraparTex.add(new TextureRegion((Texture)manager.get("monstruoropa8.png")));
            atraparTex.add(new TextureRegion((Texture)manager.get("monstruoropa9.png")));
            atrapadoTex.add(new TextureRegion((Texture)manager.get("monstruoropa10.png")));
            atrapadoTex.add(new TextureRegion((Texture)manager.get("monstruoropa11.png")));

        }

        public MonstruoRopa(float x, float y, Abner abner){
            super(neutral, x,y);
            estadoRopa = EstadoRopa.CAIDO;
            this.abner = abner;
            this.timerAtaque = 0;
        }

        @Override
        public void attack() {
            if(timer>=0.3f&&abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())&&!abner.getDano()){
                abner.setAtrapado(true);
                estadoRopa = EstadoRopa.ATRAPADO;
                timer=0;
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

        private void actualizar() {
            switch (estadoRopa){
                case CAIDO:
                    sprite.setTexture(neutral);
                    if(Math.abs(sprite.getX()-abner.getX())<=500){
                        estadoRopa = EstadoRopa.LEVANTANDO;
                    }
                    break;
                case LEVANTANDO:
                    timer+=Gdx.graphics.getDeltaTime();
                    sprite.setTexture(levantando.getKeyFrame(timer).getTexture());
                    if(timer>=levantando.getAnimationDuration()){
                        timer =0;
                        estadoRopa = EstadoRopa.ESPERANDO;
                    }
                    break;
                case ESPERANDO:
                    if(abner.getX()-sprite.getX()>0){
                        right = true;
                    }
                    else{
                        right = false;
                    }
                    timer+=Gdx.graphics.getDeltaTime();
                    sprite.setTexture(respirando.getKeyFrame(timer).getTexture());
                    if(Math.abs(sprite.getX()-abner.getX())<=100){
                        timer = 0;
                        estadoRopa = EstadoRopa.ATACANDO;
                    }
                    else if(Math.abs(sprite.getX()-abner.getX())>=500){
                        estadoRopa = EstadoRopa.CAIDO;
                    }
                    break;
                case ATACANDO:
                    attack();
                    timer+=Gdx.graphics.getDeltaTime();
                    sprite.setTexture(atrapar.getKeyFrame(timer).getTexture());
                    if(timer>=atrapar.getAnimationDuration()){
                        timer = 0;
                        estadoRopa = EstadoRopa.ESPERANDO;
                    }
                    break;
                case ATRAPADO:
                    timer += Gdx.graphics.getDeltaTime();
                    timerAtaque += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(atrapado.getKeyFrame(timer).getTexture());
                    if(timerAtaque>=1){
                        abner.setCantVida(abner.getcantVida()-7);
                        timerAtaque = 0;
                        Gdx.input.vibrate(10);
                    }
                    if(timer>=6||!abner.isAtrapado()){
                        estadoRopa = EstadoRopa.CAIDO;
                        abner.setX(right?abner.getX()-150:abner.getX()+150);
                        abner.setDano(true);
                        abner.setAtrapado(false);
                    }
                    break;
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private enum EstadoRopa{
            CAIDO,
            LEVANTANDO,
            ESPERANDO,
            ATACANDO,
            ATRAPADO
        }
    }

    static class Alfombra extends Enemigo {
        private static Texture neutral, esperando, ataque;

        static {
            cargarTexturas();
        }

        private static void cargarTexturas() {

        }

        public Alfombra(float x, float y, Abner abner){
            super(neutral, x,y);
        }


        @Override
        public void attack() {

        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {

        }

        @Override
        public boolean muerte() {
            return false;
        }
    }

    static class Fantasma extends Enemigo {

        @Override
        public void attack() {

        }

        @Override
        public void setEstado(Estado estado) {

        }

        @Override
        public void draw(SpriteBatch batch) {

        }

        @Override
        public boolean muerte() {
            return false;
        }
    }
}
