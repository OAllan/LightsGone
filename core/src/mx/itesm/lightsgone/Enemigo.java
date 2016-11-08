package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


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

    public void setMuerte(boolean muerte){
        this.muerte = muerte;
    }

    public float getX(){
        return yInicial;
    }
    public float getY(){
        return xInicial;
    }

    public enum Estado{
        NEUTRAL,
        ATAQUE,
        DANO
    }




    static class Sopa extends Enemigo {
        private static Texture neutral1, neutral2, neutral3, ataque1, ataque2, low;
        private Estado estado;
        private static Animation neutral, ataque, dano;
        private float timerA, timer, timerD;
        private Abner abner;
        private Mapa mapa;
        private int vida=1;




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
            neutral = new Animation(0.1f, new TextureRegion(neutral1), new TextureRegion(neutral2), new TextureRegion(neutral3));
            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2));
            dano=new Animation(0.2f, new TextureRegion(neutral1), new TextureRegion(low));
            neutral.setPlayMode(Animation.PlayMode.LOOP);
            ataque.setPlayMode(Animation.PlayMode.LOOP);
            dano.setPlayMode(Animation.PlayMode.LOOP);
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
            if(vida==1) {
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

            if(!abner.getProyectiles().isEmpty()&& sprite.getTexture()!=low) {
                if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida=0;
                    abner.borrarProyectiles();
                }
            }



            if(vida<=0){
                sprite.setTexture(low);
                estado= Estado.NEUTRAL;
            }





            float distancia = sprite.getX() - abner.getX();
            if(distancia>0&& sprite.isFlipX()) {
                sprite.flip(true, false);

            }
            else if(distancia<0&&!sprite.isFlipX())
                sprite.flip(true, false);

            if(Math.abs(distancia)>650){
                vida=1;
            }

            if(vida==1) {
                switch (estado) {
                    case NEUTRAL:
                        timer += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(neutral.getKeyFrame(timer).getTexture());
                        if (timer > 5) {
                            estado = Estado.ATAQUE;
                            timer = 0;
                        }

                        break;
                    case ATAQUE:
                        timerA += Gdx.graphics.getDeltaTime();
                        sprite.setTexture(ataque.getKeyFrame(timerA).getTexture());
                        if (ataque.getAnimationDuration() < timerA) {
                            estado = Estado.NEUTRAL;
                            timerA = 0;
                        }
                        break;


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
                if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida -= 1;
                    abner.borrarProyectiles();
                }
            }

            if(vida<=0) {
                MapManager.quitarEnemigo(this);
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

        private void actualizar() {
            if(sprite.getX()<0){
                sprite.setPosition(20000,20000);
                vida=1;
            }
            if(sprite.getX()>22000 || sprite.getX()<18000 && sprite.getX()>16000){
                MapManager.quitarEnemigo(this);
               // Nivel0.crearNuevaMosca(xInicial,yInicial,this);

            }

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

            if(vida<=0) {
                xInicial = sprite.getX();
                yInicial = sprite.getY();
                muerte = true;
                MapManager.quitarEnemigo(this);
                //sprite.setPosition(20000,20000);
                vida=1;
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
                if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida -= 1;
                    abner.borrarProyectiles();
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
                if (sprite.getBoundingRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    vida -= 1;
                    abner.borrarProyectiles();
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
            if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                if(ataco==false && estado== Estado.ATAQUE) {
                    attack();
                    startTime = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis()-startTime>2000) {
                ataco=false;
                startTime=0;
            }


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
            if(Math.abs(abner.getX()-posXOriginal)<660) {
                estado= Estado.ATAQUE;

            }

            if(estado== Estado.ATAQUE){
                sprite.setX(sprite.getX() - 8 * direccion);
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
        private int vida;
        private int muerto=0;

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
                //muerte = true;
                MapManager.quitarEnemigo(this);
            }

            if (Math.abs(abner.getX()-sprite.getX()) <= 700 && Math.abs(abner.getX()-sprite.getX()) > 600) {
                timer += Gdx.graphics.getDeltaTime();
                sprite.setTexture(advertenciaA.getKeyFrame(timer).getTexture());

            }

            if (Math.abs(abner.getX()-sprite.getX()) <= 600) {
                estado = Estado.ATAQUE;
            }
            else{ estado= Estado.NEUTRAL;}




            if (sprite.getY()>=posicionInicial+1300) {
                direccion="abajo";
                sprite.setTexture(caida);
                sprite.setX(abner.getX());
            }
            if(sprite.getY()<posicionInicial){
                muerto=1;
                timer += Gdx.graphics.getDeltaTime();
                sprite.setTexture(muerteG.getKeyFrame(timer*2).getTexture());
                //sprite.setTexture(muerte);
                MapManager.quitarEnemigo(this);
            }

            if(estado== Estado.ATAQUE && muerto==0) {
                if (direccion == "arriba") {
                    sprite.setTexture(lanzamiento);
                    sprite.setY(sprite.getY() + 16);
                }
                if (direccion == "abajo") {
                    sprite.setY(sprite.getY() - 16);
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


        static {
            cargarTexturas();
            cargarAnimacion();
        }

        private static void cargarAnimacion() {

            ataque = new Animation(0.2f, new TextureRegion(ataque1), new TextureRegion(ataque2));

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
                if (getRectangle().overlaps(abner.getProyectiles().get(0).getRectangle())) {
                    //vida -= 1;
                    abner.borrarProyectiles();
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




            if (Math.abs(sprite.getX() - abner.getX()) <= 900 && Math.abs(sprite.getY()-abner.getY())<400) {
                estado = Estado.ATAQUE;
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
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(ataque.getKeyFrame(timer).getTexture());

                        if (sprite.getX() <= xInicial + 4200 && sprite.getX() >= xInicial) {
                            sprite.setX(sprite.getX() - 8 * direccion);
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



            if(abner.getX()>19215 && abner.getX()<20610 && bandera==true) {
                estado = Estado.ATAQUE;

            }
            else
                estado= Estado.NEUTRAL;

            switch (estado){
                case NEUTRAL:
                    sprite.setTexture(neutral);
                    break;
                case ATAQUE:
                    sprite.setTexture(ataque);
                    contador++;
                    if(contador%600==0)
                        bandera=false;
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

    }
