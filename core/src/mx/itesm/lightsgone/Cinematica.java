package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


/**
 * Created by allanruiz on 19/11/16.
 */

public abstract class Cinematica {
    protected float timer;
    protected Sprite sprite,transicionFundido;
    protected AssetManager manager;
    protected LightsGone.Transicion transicion;
    protected float alpha, velocidadTransicion;

    public Cinematica(){
        timer = 0;
        velocidadTransicion = LightsGone.TRANSICIONNEUTRAL;
        transicion = LightsGone.Transicion.AUMENTANDO;
    }

    public abstract void play(SpriteBatch batch);
    public abstract boolean finished();
    public abstract void dispose();
    protected void start(){
        switch (transicion){
            case AUMENTANDO:
                alpha = 1;
                transicion = LightsGone.Transicion.DISMINUYENDO;
                break;
            case DISMINUYENDO:
                alpha-= velocidadTransicion;
                if (alpha<=0) {
                    alpha = 0;
                }
                break;
        }
    }

    private void cargarTexturas() {
        manager = new AssetManager();
        manager.load("nivel.png", Texture.class);
        manager.finishLoading();
        transicionFundido = new Sprite((Texture) manager.get("nivel.png"));
        transicionFundido.setSize(LightsGone.ANCHO_MUNDO, LightsGone.ALTO_MUNDO);

    }

    static class Inicio extends Cinematica{
        private Abner abner;
        private Texture inicio, inicio6, inicio7, inicio11;
        private Animation corriendo, escapando;
        private Escena escena;
        private boolean finished;
        private Texto texto;
        private Music grito, pasos;


        {
            super.cargarTexturas();
            cargarTexturas();
        }

        private void cargarTexturas() {
            manager.load("CinematicaInicio1.png",Texture.class);
            manager.load("CinematicaInicio2.png", Texture.class);
            manager.load("CinematicaInicio3.png", Texture.class);
            manager.load("CinematicaInicio4.png", Texture.class);
            manager.load("CinematicaInicio5.png", Texture.class);
            manager.load("CinematicaInicio6.png", Texture.class);
            manager.load("CinematicaInicio7.png", Texture.class);
            manager.load("CinematicaInicio8.png", Texture.class);
            manager.load("CinematicaInicio9.png", Texture.class);
            manager.load("CinematicaInicio10.png", Texture.class);
            manager.load("CinematicaInicio11.png", Texture.class);
            manager.load("rugido.mp3", Music.class);
            manager.load("Run Grass.mp3", Music.class);
            manager.finishLoading();
            inicio = manager.get("CinematicaInicio1.png");
            corriendo = new Animation(0.1f, new TextureRegion((Texture)manager.get("CinematicaInicio2.png")),new TextureRegion((Texture)manager.get("CinematicaInicio3.png")),new TextureRegion((Texture)manager.get("CinematicaInicio4.png")),new TextureRegion((Texture)manager.get("CinematicaInicio5.png")));
            inicio6 = manager.get("CinematicaInicio6.png");
            inicio7 = manager.get("CinematicaInicio7.png");
            escapando = new Animation(0.3f, new TextureRegion((Texture)manager.get("CinematicaInicio8.png")),new TextureRegion((Texture)manager.get("CinematicaInicio9.png")), new TextureRegion((Texture)manager.get("CinematicaInicio10.png")));
            inicio11 = manager.get("CinematicaInicio11.png");
            grito = manager.get("rugido.mp3");
            pasos = manager.get("Run Grass.mp3");
            pasos.setLooping(true);

        }

        public Inicio(Abner abner){
            this.abner = abner;
            this.sprite = new Sprite(inicio);
            escena = Escena.FUNDIDO;
            finished = false;
            alpha = 1;
            texto = new Texto("font.fnt", LightsGone.ANCHO_MUNDO/2, 100);
        }

        @Override
        public void play(SpriteBatch batch) {
            sprite.draw(batch);
            transicionFundido.draw(batch);
            transicionFundido.setAlpha(alpha);
            switch (escena){
                case FUNDIDO:
                    start();
                    if(alpha<=0){
                        escena = Escena.INICIO;
                    }

                    break;
                case INICIO:
                    timer += Gdx.graphics.getDeltaTime();
                    if(timer>=1.5){
                        escena = Escena.CORRIENDO;
                        timer=0;
                        pasos.play();
                    }
                    break;
                case CORRIENDO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(corriendo.getKeyFrame(timer).getTexture());
                    if(timer>=corriendo.getAnimationDuration()){
                        escena = Escena.NEUTRAL;
                        timer = 0;
                        pasos.stop();
                    }
                    break;
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(inicio6);
                    texto.mostrarMensaje(batch,"Mom?...Dad?");
                    if(timer>=2){
                        escena = Escena.GRITO;
                        timer = 0;
                        grito.play();
                    }
                    break;
                case GRITO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(inicio7);
                    if(timer>=2.5){
                        escena = Escena.ESCAPE;
                        timer = 0;
                    }
                    break;
                case ESCAPE:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(escapando.getKeyFrame(timer).getTexture());
                    if(timer>=escapando.getAnimationDuration()){
                        escena = Escena.FIN;
                        timer = 0;
                    }
                    break;
                case FIN:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(inicio11);
                    if(timer>=1.5){
                        finished = true;
                        timer = 0;
                        abner.setX(1845);
                    }
                    break;
            }

        }

        @Override
        public boolean finished() {
            return finished;
        }

        @Override
        public void dispose() {
            inicio.dispose();
            inicio6.dispose();
            inicio7.dispose();
            inicio11.dispose();
            manager.dispose();
            grito.dispose();
            pasos.dispose();
        }

        private enum Escena{
            FUNDIDO,
            INICIO,
            CORRIENDO,
            NEUTRAL,
            GRITO,
            ESCAPE,
            FIN
        }
    }

    static class Pelea extends Cinematica{
        private Texture antes5, antes8, antes11, antes12;
        private Animation inicio, volteando, lanzando,saltando;
        private Escena escena;
        private Abner abner;
        private boolean finished;
        private Music grito, caldera;


        {
            super.cargarTexturas();
            cargarTexturas();
        }

        private void cargarTexturas(){
            manager.load("CinematicaAntesCoco1.png", Texture.class);
            manager.load("CinematicaAntesCoco2.png", Texture.class);
            manager.load("CinematicaAntesCoco3.png", Texture.class);
            manager.load("CinematicaAntesCoco4.png", Texture.class);
            manager.load("CinematicaAntesCoco5.png", Texture.class);
            manager.load("CinematicaAntesCoco6.png", Texture.class);
            manager.load("CinematicaAntesCoco7.png", Texture.class);
            manager.load("CinematicaAntesCoco8.png", Texture.class);
            manager.load("CinematicaAntesCoco9.png", Texture.class);
            manager.load("CinematicaAntesCoco10.png", Texture.class);
            manager.load("CinematicaAntesCoco11.png", Texture.class);
            manager.load("CinematicaAntesCoco12.png", Texture.class);
            manager.load("rugido.mp3", Music.class);
            manager.load("caldera.mp3", Music.class);
            manager.finishLoading();
            inicio = new Animation(0.4f, new TextureRegion((Texture)manager.get("CinematicaAntesCoco1.png")),new TextureRegion((Texture)manager.get("CinematicaAntesCoco2.png")));
            inicio.setPlayMode(Animation.PlayMode.LOOP);
            volteando = new Animation(0.15f, new TextureRegion((Texture)manager.get("CinematicaAntesCoco3.png")),new TextureRegion((Texture)manager.get("CinematicaAntesCoco4.png")));
            antes5 = manager.get("CinematicaAntesCoco5.png");
            lanzando = new Animation(0.25f, new TextureRegion((Texture)manager.get("CinematicaAntesCoco6.png")), new TextureRegion((Texture)manager.get("CinematicaAntesCoco7.png")));
            antes8 = manager.get("CinematicaAntesCoco8.png");
            saltando = new Animation(0.2f, new TextureRegion((Texture)manager.get("CinematicaAntesCoco9.png")), new TextureRegion((Texture)manager.get("CinematicaAntesCoco10.png")));
            antes11 = manager.get("CinematicaAntesCoco11.png");
            antes12 = manager.get("CinematicaAntesCoco12.png");
            grito = manager.get("rugido.mp3");
            caldera = manager.get("caldera.mp3");
            caldera.setLooping(true);

        }

        public Pelea(Abner abner){
            this.abner = abner;
            this.sprite = new Sprite(inicio.getKeyFrame(0).getTexture());
            escena = Escena.FUNDIDO;
            finished = false;
            alpha = 1;
            caldera.play();
        }

        @Override
        public void play(SpriteBatch batch) {
            sprite.draw(batch);
            transicionFundido.draw(batch);
            transicionFundido.setAlpha(alpha);
            switch(escena){
                case FUNDIDO:
                    start();
                    if(alpha<=0){
                        alpha = 0;
                        escena = Escena.INICIO;
                    }
                    break;
                case INICIO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(inicio.getKeyFrame(timer).getTexture());
                    if(timer>=5){
                        timer = 0;
                        escena = Escena.CORRIENDO;
                    }
                    break;
                case CORRIENDO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(volteando.getKeyFrame(timer).getTexture());
                    if(timer>=volteando.getAnimationDuration()){
                        timer = 0;
                        escena = Escena.VUELTA;
                    }
                    break;
                case VUELTA:
                    timer+=Gdx.graphics.getDeltaTime();
                    sprite.setTexture(antes5);
                    if(timer>=2){
                        timer = 0;
                        escena = Escena.ENOJADO;
                        caldera.stop();
                    }
                    break;
                case ENOJADO:
                    timer+=Gdx.graphics.getDeltaTime();
                    sprite.setTexture(lanzando.getKeyFrame(timer).getTexture());
                    if(timer>=lanzando.getAnimationDuration()){
                        timer = 0;
                        escena = Escena.SALTO;
                    }
                    break;
                case SALTO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(antes8);
                    if(timer>=0.5){
                        timer = 0;
                        escena = Escena.SALTANDO;
                    }
                    break;
                case SALTANDO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(saltando.getKeyFrame(timer).getTexture());
                    if(timer>=saltando.getAnimationDuration()){
                        timer = 0;
                        escena = Escena.NEUTRAL;
                    }
                    break;
                case NEUTRAL:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(antes11);
                    if(timer>=1){
                        timer = 0;
                        escena = Escena.GRITO;
                        grito.play();
                    }
                    break;
                case GRITO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(antes12);
                    if(timer>=2.5){
                        finished = true;
                        abner.setX(1035);
                    }
                    break;
            }
        }

        @Override
        public boolean finished() {
            return finished;
        }

        @Override
        public void dispose() {
            antes5.dispose();
            antes8.dispose();
            antes11.dispose();
            antes12.dispose();
            manager.dispose();
            grito.dispose();
        }

        private enum Escena{
            FUNDIDO,
            INICIO,
            CORRIENDO,
            VUELTA,
            ENOJADO,
            SALTO,
            SALTANDO,
            NEUTRAL,
            GRITO
        }
    }

    static class Final extends Cinematica{
        private Texture final1, final2, final3, final4, final5, final6, lightsgone, erick, hector, luis,allan,rafa, fin;
        private boolean finished;
        private Escena escena;
        private int imagenActual;
        private Array<Texture> creditos;
        private Music musicFinal;

        {
            super.cargarTexturas();
            cargarTexturas();
        }

        private void cargarTexturas(){
            manager.load("CinematicaFinal1.png", Texture.class);
            manager.load("CinematicaFinal2.png", Texture.class);
            manager.load("CinematicaFinal3.png", Texture.class);
            manager.load("CinematicaFinal4.png", Texture.class);
            manager.load("CinematicaFinal5.png", Texture.class);
            manager.load("CinematicaFinal6.png", Texture.class);
            manager.load("CinematicaFinal7.png", Texture.class);
            manager.load("CinematicaFinal8.png", Texture.class);
            manager.load("CinematicaFinal9.png", Texture.class);
            manager.load("CinematicaFinal10.png", Texture.class);
            manager.load("CinematicaFinal11.png", Texture.class);
            manager.load("CinematicaFinal12.png", Texture.class);
            manager.load("CinematicaFinal13.png", Texture.class);
            manager.load("CocoMuerteFondo.png", Texture.class);
            manager.load("final.mp3", Music.class);
            manager.finishLoading();
            transicionFundido.setTexture((Texture)manager.get("CocoMuerteFondo.png"));
            final1 = manager.get("CinematicaFinal1.png", Texture.class);
            final2 = manager.get("CinematicaFinal2.png", Texture.class);
            final3 = manager.get("CinematicaFinal3.png", Texture.class);
            final4 = manager.get("CinematicaFinal4.png", Texture.class);
            final5 = manager.get("CinematicaFinal5.png", Texture.class);
            final6 = manager.get("CinematicaFinal6.png", Texture.class);
            lightsgone = manager.get("CinematicaFinal7.png", Texture.class);
            erick = manager.get("CinematicaFinal8.png", Texture.class);
            hector = manager.get("CinematicaFinal9.png", Texture.class);
            luis = manager.get("CinematicaFinal10.png", Texture.class);
            allan = manager.get("CinematicaFinal11.png", Texture.class);
            rafa = manager.get("CinematicaFinal12.png", Texture.class);
            fin = manager.get("CinematicaFinal13.png", Texture.class);
            creditos = new Array<Texture>();
            creditos.add(erick);
            creditos.add(hector);
            creditos.add(luis);
            creditos.add(allan);
            creditos.add(rafa);
            creditos.add(fin);
            musicFinal = manager.get("final.mp3");
            musicFinal.setLooping(true);

        }

        public Final(){
            this.sprite = new Sprite(final1);
            this.escena = Escena.FUNDIDO;
            finished = false;
            alpha = 0;
        }


        public void start(SpriteBatch batch){
            switch (transicion){
                case AUMENTANDO:
                    alpha+=velocidadTransicion/2;
                    if(alpha>=1){
                        alpha=1;
                        transicion = LightsGone.Transicion.DISMINUYENDO;
                    }
                    break;
                case DISMINUYENDO:
                    alpha -= velocidadTransicion;
                    sprite.draw(batch);
                    if(alpha<=0){
                        alpha = 0;
                        escena = Escena.INICIO;
                        musicFinal.play();
                    }
            }
            transicionFundido.setAlpha(alpha);
            transicionFundido.draw(batch);
        }

        @Override
        public void play(SpriteBatch batch) {
            switch (escena){
                case FUNDIDO:
                    start(batch);
                    break;
                case INICIO:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(final1);
                    if(timer>=2){
                        sprite.setTexture(final2);
                        escena = Escena.ESCENA1;
                        timer = 0;
                    }
                    break;
                case ESCENA1:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(final3);
                    if(timer>=1){
                        escena = Escena.ESCENA2;
                        timer = 0;
                    }
                    break;
                case ESCENA2:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(final4);
                    if(timer>=1){
                        escena = Escena.ESCENA3;
                        timer = 0;
                    }
                    break;
                case ESCENA3:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(final5);
                    if(timer>=1){
                        escena = Escena.ESCENA4;
                        timer = 0;
                    }
                    break;
                case ESCENA4:
                    timer += Gdx.graphics.getDeltaTime();
                    sprite.setTexture(final6);
                    if(timer>=3){
                        escena = Escena.LIGHTSGONE;
                        timer = 0;
                        transicionFundido.setTexture(lightsgone);
                    }
                    break;
                case LIGHTSGONE:
                    sprite.draw(batch);
                    alpha+=Gdx.graphics.getDeltaTime();
                    if(alpha>=1){
                        alpha =1;
                        timer += Gdx.graphics.getDeltaTime();
                        if(timer>=2){
                            timer = 0;
                            escena = Escena.CREDITOS;
                        }
                    }
                    transicionFundido.setAlpha(alpha);
                    transicionFundido.draw(batch);
                    break;
                case CREDITOS:
                    timer += Gdx.graphics.getDeltaTime();
                    transicionFundido.setTexture(creditos.get(imagenActual));
                    if(timer>=4){
                        if(imagenActual==creditos.size-1){
                            finished = true;
                            musicFinal.stop();
                        }
                        else{
                            imagenActual++;
                        }
                        timer = 0;
                    }
                    transicionFundido.draw(batch);
                    break;
            }
            if(escena!=Escena.FUNDIDO&&escena != Escena.CREDITOS&&escena!=Escena.LIGHTSGONE){
                sprite.draw(batch);
            }
        }

        @Override
        public boolean finished() {
            return finished;
        }

        @Override
        public void dispose() {

        }

        private enum Escena{
            FUNDIDO,
            INICIO,
            ESCENA1,
            ESCENA2,
            ESCENA3,
            ESCENA4,
            LIGHTSGONE,
            CREDITOS
        }
    }
}
