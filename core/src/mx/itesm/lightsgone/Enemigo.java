package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;


/**
 * Created by allanruiz on 07/10/16.
 */


public abstract class Enemigo  {
    protected Sprite sprite;
    protected float xInicial, yInicial;
    private static AssetManager manager = new AssetManager();
    boolean ataco=false;
    long startTime;
    long startTime1;





    public Enemigo(Texture texture, float x, float y){
        xInicial = x;
        yInicial = y;
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);

    }

    public abstract void attack();
    public abstract void setEstado(Estado estado);
    public abstract void draw(SpriteBatch batch);

    public boolean colisiona(Proyectil p){
        return sprite.getBoundingRectangle().overlaps(p.getRectangle());
    }

    public abstract boolean muerte();


    public enum Estado{
        NEUTRAL,
        ATAQUE,
        DANO
    }




    static class Sopa extends Enemigo{
        private static Texture neutral1, neutral2, neutral3, ataque1, ataque2, low;
        private Estado estado;
        private static Animation neutral, ataque, dano;
        private float timerA, timer, timerD;
        private Abner abner;
        private Mapa mapa;




        static {
            cargarTexturas();
            cargarAnimacion();
        }

        public Sopa(float x, float y, Abner abner,Mapa mapa){
            super(neutral1, x, y);
            estado= Estado.NEUTRAL;
            timer = timerA = 0;
            this.abner = abner;
            this.mapa=mapa;

        }


        private static void cargarAnimacion() {
            neutral = new Animation(0.1f, new TextureRegion(neutral1), new TextureRegion(neutral2), new TextureRegion(neutral3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2));
            dano=new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(low));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);
            dano.setPlayMode(Animation.PlayMode.LOOP);
        }

        @Override
        public void attack() {
            abner.setCantVida(abner.getcantVida()-5);
            ataco=true;

        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            if(System.currentTimeMillis()-startTime>=2000) {
                if (abner.getX() > 400)
                    estado = Estado.ATAQUE;
                else
                    estado = Estado.NEUTRAL;
            }
            actualizar();
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                if(ataco==false && estado==Estado.ATAQUE) {
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
                    estado=Estado.DANO;
                    abner.borrarProyectiles();
                }
            }





            float distancia = sprite.getX() - abner.getX();
            if(distancia>0&& sprite.isFlipX()) {
                sprite.flip(true, false);

            }
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
                case DANO:
                    timerD += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(dano.getKeyFrame(timerA).getTexture());
                    if(timerD>5){
                        timerD = 0;
                        estado = Estado.NEUTRAL;
                    }

            }
        }

        private static void cargarTexturas(){
            manager.load("SopaAtaque1.png", Texture.class);
            manager.load("SopaAtaque2.png", Texture.class);
            manager.load("SopaNeutral1.png", Texture.class);
            manager.load("SopaNeutral2.png", Texture.class);
            manager.load("SopaNeutral3.png", Texture.class);
            manager.load("SopaLow.png",Texture.class);
            manager.finishLoading();
            neutral1 = manager.get("SopaNeutral1.png");
            ataque1 = manager.get("SopaAtaque1.png");
            ataque2 = manager.get("SopaAtaque2.png");
            neutral2 = manager.get("SopaNeutral2.png");
            neutral3 = manager.get("SopaNeutral3.png");
            low=manager.get("SopaLow.png");
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

        public Brocoli(float x, float y, Abner abner,Mapa mapa){
            super(neutral1, x, y);
            estado = Estado.NEUTRAL;
            right = true;
            timer = timerA=0;
            this.abner = abner;
            vida = 3;
            this.mapa=mapa;
        }

        @Override
        public void attack() {

            abner.setCantVida(abner.getcantVida()-5);
            ataco=true;
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
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                if(ataco==false && estado==Estado.ATAQUE) {
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

            if(vida<=0) {
                sprite.setX(10000);
                sprite.setY(10000);
            }
            if(sprite.getY()!=1620) {
                if (sprite.getX() >= xInicial + 400)

                    right = false;
                if (sprite.getX() < xInicial - 400)

                    right = true;
            }
            else{
                if (sprite.getX() >= xInicial + 150)

                    right = false;
                if (sprite.getX() < xInicial - 150)

                    right = true;
            }

            /*if(!mapa.colisionY(sprite.getX(),sprite.getY())){

                if(sprite.getX()<xInicial) {
                    right=true;
                }
                if(sprite.getX()>xInicial) {
                    right = false;
                }
            }*/

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
                            if (right)
                                sprite.translate(velocidad * 2, 0);

                            else
                                sprite.translate(-velocidad * 2, 0);

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

    static class Tostadora extends Enemigo{
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
                else{ estado=Estado.NEUTRAL;}

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

    static class PanTostadora extends Enemigo{
        private static Texture pan;
        private final float velocidad = 2f;
        private Abner abner;
        private Mapa mapa;
        private String direccion="arriba";
        private float posicionInicial;

        static {
            cargarTexturas();
        }

        public PanTostadora(float x, float y,Abner abner,Mapa mapa){
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

            abner.setCantVida(abner.getcantVida()-3);
            ataco=true;
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
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
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




    static class Mosca extends Enemigo{
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
        }

        @Override
        public void attack() {

            abner.setCantVida(abner.getcantVida()-3);
            ataco=true;
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
                if(Math.abs(distancia)<=700){
                    estado = Estado.ATAQUE;
                    ataq=true;
                }
                else
                    if (ataq==false)
                    estado = Estado.NEUTRAL;
                if(ataq==true&&Math.abs(distancia)>=1000){

                }
            }
            actualizar();
        }

        @Override
        public boolean muerte() {
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
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
                if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida -= 1;
                    abner.borrarProyectiles();
                }
            }

            if(vida<=0) {
                sprite.setX(10000);
                sprite.setY(10000);
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
                    sprite.setX(sprite.getX()-4);
                    sprite.setY(sprite.getY()+MathUtils.random(-4,4));

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




    static class Fuego extends Enemigo{
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
        int contador=1;


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

            abner.setCantVida(abner.getcantVida()-1);

        }

        @Override
        public void setEstado(Estado estado) {
            this.estado = estado;
        }

        @Override
        public void draw(SpriteBatch batch) {
            sprite.draw(batch);
            //timerFuego.start();


                actualizar();

        }

        @Override
        public boolean muerte() {
            return vida <=0;
        }

        private void actualizar() {
            if(abner.getBoundingRectangle().overlaps(new Rectangle(sprite.getX()+100,sprite.getY(),sprite.getWidth()-100,sprite.getHeight()))){
                if(estado==Estado.ATAQUE) {
                    attack();
                    if (abner.getX()<=sprite.getX())
                        abner.impactoFuegoX();
                    else
                        abner.impactoFuegoX();

                }
            }

            switch (estado){
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(neutral.getKeyFrame(timer).getTexture());
                    break;
                case ATAQUE:
                   timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());


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

    static class Lata extends Enemigo{
        private static Texture neutral;
        private EstadoLata estadoLata;
        private float alto, alturaOriginal;
        private final float MOVCAIDA = 18f, MOVX = 7f,MOVRODANDO = 0.2125f*MOVX;
        private Mapa mapa;

        static {
            cargarTexturas();
        }

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

        public Rectangle getBoundingRectangle(){
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
            }
        }

        @Override
        public boolean muerte() {
            return false;
        }

        private enum EstadoLata{
            CAYENDO,
            RODANDO,
            DESAPARECIENDO
        }

        @Override
        public String toString(){
            return "Lata";
        }
    }



    }
