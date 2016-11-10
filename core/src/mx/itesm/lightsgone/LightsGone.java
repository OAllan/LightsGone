package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

/**
 * Created by allanruiz on 19/09/16.
 */
public class LightsGone implements Screen, InputProcessor{

    public static final int ANCHO_MUNDO = 1280;
    public static final int ALTO_MUNDO = 800;
    public static final int YBOTON = 30;
    public static final float YCOCINA3 = 361.0f;
    public static final float YCOCINA2 = 1950;
    public static final float YCOCINA1 = 2039.0f;
    public static final float YJARDIN1 = 1320;
    public static final float YJARDIN2 = 2200;
    public static final float YJARDIN3 = 700;
    public static final float YSALA = 1142;
    public static final float TRANSICIONNIVEL = 0.005f;
    public static final float TRANSICIONNEUTRAL = 0.01f;
    public static final int MUNICIONX = 264;
    public static final int MUNICIONY = 706;
    public static final float YBAJA = 135f, YMEDIA = 800f, YALTA =1100f;
    public static final float YARMARIO = 450;
    private float velocidadTransicion = TRANSICIONNEUTRAL;
    public static final int LATAX = 4871;
    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private Music ambiente, gameover;
    private Juego juego;
    private AssetManager assetManager = new AssetManager();
    public static Texture plataforma;
    private Texture  municion, habilidadLanzaPapas, transicionCocina,transicionJardin, transicionArmario, transicionSotano, transicionCoco, transicionNeutral,  malteada, dano, nivelVida, gameOver,habilidadDes, habilidadPogo,save,pausaTex,quitTex, opciones,  botonSalto, JFondo, botonVida, habilidad, texPausa;
    private Sprite transicionNivel, pausaActual, fondoCielo;
    private static Abner abner;
    private Texto vida, municionTex;
    private Pad pad;
    private Sprite imgVida, menuGameOver, imgMunicion;
    private boolean right, saveB;
    public static boolean musica;
    private Boton botonCambioUp, botonCambioDown, botonBack, botonOn, botonOff, botonTry, botonMain,botonSaltar, botonArma, pausa, botonResume, botonOpciones,botonQuit, botonYes,botonNo, botonSave, botonHabilidad;
    private float alpha = 0;
    private Array<String> mapas;
    private static Mapa mapa;
    static int mapaActual;
    private ArrayList<Proyectil> proyectiles;
    private Transicion transicion;
    private Estado estado;
    private EstadoPausa estadoPausa;
    private GameInfo gameInfo;
    private Texture pPogo1, pPogo2;
    private float alphaGame;
    private MapManager mapManager;
    public static Habilidad habilidadActual;
    private Array<Sprite> vidas;
    private Array<Sprite> malteadas;

    static Array<Enemigo> enemigosPrueba= new Array<Enemigo>(9);

    Array<Enemigo> enemigos = new Array<Enemigo>(3);

    private Texture caja;
    private Lampara estadoLampara;
    private Texture lamparaOff, lamparaOn;

    public LightsGone(Juego juego) {
        this.juego = juego;
        right = true;
        saveB = false;
        gameInfo = new GameInfo();
    }

    public LightsGone(Juego juego, String nombre){
        this.juego = juego;
        right = true;
        saveB = false;
        gameInfo = new GameInfo(nombre);
    }




    @Override
    public void show() {
        cargarTexturas();
        iniciarCamara();
        crearMapas();
        crearEscena();
        Gdx.input.setInputProcessor(this);
    }

    private void crearMapas() {
        mapas = new Array<String>(6);
        mapManager = new MapManager(camara, batch);
        abner = new Abner(camara, mapa, gameInfo);
        mapas.add("CuartoAbner.tmx");
        mapas.add("Pasillo.tmx");
        mapas.add("Sala.tmx");
        mapas.add("Cocina1.tmx");
        mapas.add("Cocina2.tmx");
        mapas.add("Cocina3.tmx");
        mapas.add("Jardin1.tmx");
        mapas.add("Jardin2.tmx");
        mapas.add("Jardin3.tmx");
        mapas.add("Armario1.tmx");
        mapas.add("Armario2.tmx");
        mapas.add("Armario3.tmx");
        mapas.add("Armario4.tmx");
        mapas.add("Sotano1.tmx");
        mapas.add("Sotano2.tmx");
        mapaActual = gameInfo.getMapa();
        mapa = mapManager.getMapa(mapas.get(mapaActual),mapaActual,abner, gameInfo);
        transicion = Transicion.DISMINUYENDO;
        musica = true;
        musica = true;
    }

    private void iniciarCamara() {
        camara = new OrthographicCamera(ANCHO_MUNDO,ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2, ALTO_MUNDO/2, 0);
        camara.update();
        vista = new StretchViewport(ANCHO_MUNDO,ALTO_MUNDO, camara);
        camaraHUD = new OrthographicCamera(ANCHO_MUNDO,ALTO_MUNDO);
        camaraHUD.position.set(ANCHO_MUNDO/2,ALTO_MUNDO/2,0);
        camaraHUD.update();
        batch = new SpriteBatch();
    }

    private void crearEscena() {
        botonSaltar = new Boton(botonSalto, ANCHO_MUNDO - habilidadDes.getWidth()- botonSalto.getWidth()-20, YBOTON, false);
        pad = new Pad(JFondo);
        vidas = new Array<Sprite>(3);
        for (int i=0;i<3;i++) {
            Sprite sprite = new Sprite(nivelVida);
            sprite.setPosition(30, ALTO_MUNDO-35-(40*(i+1)));
            vidas.add(sprite);
        }
        botonArma = new Boton(habilidad, ANCHO_MUNDO - habilidadDes.getWidth()- botonSalto.getWidth()-habilidad.getWidth()-30, YBOTON, false);
        pausa = new Boton(texPausa, ANCHO_MUNDO- texPausa.getWidth(), ALTO_MUNDO - texPausa.getHeight(), false);
        imgVida = new Sprite(botonVida);
        malteadas = new Array<Sprite>();
        imgVida.setPosition(0, 780 - imgVida.getHeight());
        imgMunicion = new Sprite(municion);
        imgMunicion.setPosition(MUNICIONX, MUNICIONY);
        vida = new Texto("tipo.fnt", imgVida.getWidth(),690);
        municionTex = new Texto("tipo.fnt", imgMunicion.getX(), MUNICIONY+60);
        transicionNivel = new Sprite(transicionNeutral);
        estadoLampara = Lampara.APAGADA;
        if(gameInfo.isPogo())
            habilidadActual = Habilidad.POGO;
        else if(gameInfo.isLanzapapas())
            habilidadActual = Habilidad.LANZAPAPAS;
        else
            habilidadActual = Habilidad.VACIA;
        transicionNivel.setSize(LightsGone.ANCHO_MUNDO, LightsGone.ALTO_MUNDO);
        abner = new Abner(camara, mapa, gameInfo);

        gameInfo.setAbner(abner);
        estado = Estado.JUGANDO;
        botonHabilidad = new Boton(habilidadDes, ANCHO_MUNDO-habilidadDes.getWidth()-10,YBOTON,false);
        estadoPausa = EstadoPausa.PRINCIPAL;
        menuGameOver = new Sprite(gameOver);
        pausaActual = new Sprite(pausaTex);
        botonResume = new Boton(502,405,295,75);
        botonOpciones= new Boton(502,247,295,75);
        botonQuit = new Boton(538,93,202,79);
        botonYes = new Boton(402,106,141,79);
        botonNo = new Boton(714, 106,141,79);
        botonSave = new Boton(save, ANCHO_MUNDO/2-(save.getWidth()/2), ALTO_MUNDO - save.getHeight(), false);
        botonMain = new Boton(195,91,355,65);
        botonTry = new Boton(195,224,355,65);
        botonOn = new Boton(378,166,134,67);
        botonOff = new Boton(757,166,134,67);
        botonBack = new Boton(526, 53,209, 81);
        botonCambioUp = new Boton(175,214,111,30);
        botonCambioDown = new Boton(175, 30, 111,30);
        alphaGame = 0;
        enemigos = mapa.getEnemigos();
        ambiente.setVolume(0.5f);

    }

    public static void crearNuevaMosca(float x,float y,Enemigo ene){
        if(enemigosPrueba.contains(ene,true)){
            enemigosPrueba.add(new Enemigo.Mosca(x,y,abner,mapa));
        }
    }

    private void cargarTexturas() {
        assetManager.load("BotonSalto.png", Texture.class);
        assetManager.load("JoystickBoton.png", Texture.class);
        assetManager.load("BotonPausa.png", Texture.class);
        assetManager.load("BotonVida.png", Texture.class);
        assetManager.load("BotonResortera.png", Texture.class);
        assetManager.load("nivel.png", Texture.class);
        assetManager.load("PlataformaInclinada.png", Texture.class);
        assetManager.load("menuPausa.png", Texture.class);
        assetManager.load("menuPausaQuit.png", Texture.class);
        assetManager.load("opciones.png", Texture.class);
        assetManager.load("Save6.png", Texture.class);
        assetManager.load("BotonHabilidadDesactivado.png", Texture.class);
        assetManager.load("BotonHabPogo.png", Texture.class);
        assetManager.load("gameOver.png", Texture.class);
        assetManager.load("BotonNivelVida.png", Texture.class);
        assetManager.load("MalteadaMundo.png", Texture.class);
        assetManager.load("FondoCielo.jpg", Texture.class);
        assetManager.load("cocina.jpg", Texture.class);
        assetManager.load("coco.jpg", Texture.class);
        assetManager.load("jardin.jpg", Texture.class);
        assetManager.load("ropero.jpg", Texture.class);
        assetManager.load("sotano.jpg", Texture.class);
        assetManager.load("BotonHabLanzaPapa.png", Texture.class);
        assetManager.load("BotonMunicion.png", Texture.class);
        assetManager.load("CajaMovilDer.png", Texture.class);
        assetManager.load("BotonHabLamparaOff.png", Texture.class);
        assetManager.load("BotonHabLamparaOn.png", Texture.class);
        assetManager.load("LuzConLampara.png", Texture.class);
        assetManager.load("OscuridadConLampara.png", Texture.class);
        assetManager.load("ambiente.mp3", Music.class);
        assetManager.load("risa.mp3", Music.class);
        assetManager.finishLoading();
        botonSalto = assetManager.get("BotonSalto.png");
        JFondo = assetManager.get("JoystickBoton.png");
        texPausa = assetManager.get("BotonPausa.png");
        habilidad = assetManager.get("BotonResortera.png");
        botonVida  =assetManager.get("BotonVida.png");
        transicionNeutral = assetManager.get("nivel.png");
        transicionCocina = assetManager.get("cocina.jpg");
        transicionCoco = assetManager.get("coco.jpg");
        transicionJardin = assetManager.get("jardin.jpg");
        transicionArmario = assetManager.get("ropero.jpg");
        transicionSotano = assetManager.get("sotano.jpg");
        plataforma = assetManager.get("PlataformaInclinada.png");
        pausaTex = assetManager.get("menuPausa.png");
        opciones = assetManager.get("opciones.png");
        quitTex = assetManager.get("menuPausaQuit.png");
        save = assetManager.get("Save6.png");
        habilidadDes = assetManager.get("BotonHabilidadDesactivado.png");
        habilidadPogo = assetManager.get("BotonHabPogo.png");
        gameOver = assetManager.get("gameOver.png");
        nivelVida = assetManager.get("BotonNivelVida.png");
        ambiente = assetManager.get("ambiente.mp3");
        gameover = assetManager.get("risa.mp3");
        malteada = assetManager.get("MalteadaMundo.png");
        fondoCielo = new Sprite((Texture)assetManager.get("FondoCielo.jpg"));
        fondoCielo.setPosition(0,0);
        habilidadLanzaPapas = assetManager.get("BotonHabLanzaPapa.png");
        municion = assetManager.get("BotonMunicion.png");
        caja = assetManager.get("CajaMovilDer.png");
        lamparaOff = assetManager.get("BotonHabLamparaOff.png");
        lamparaOn = assetManager.get("BotonHabLamparaOn.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(abner.getPogo()&&habilidadActual == Habilidad.VACIA)
            habilidadActual = Habilidad.POGO;

        estado = abner.isDead()?Estado.MUERTE:estado;

        if(musica){
            switch(mapaActual){
                case 0:case 1:case 2:default:
                    if(!ambiente.isPlaying()){
                        ambiente.play();
                        ambiente.setLooping(true);
                    }
                    break;

            }
        }
        else
            ambiente.pause();

        if(estado != Estado.PAUSA&&estado!=Estado.MUERTE){

            switch (habilidadActual){
                case POGO:
                    botonHabilidad.setTexture(habilidadPogo);
                    break;
                case LANZAPAPAS:
                    botonHabilidad.setTexture(habilidadLanzaPapas);
                    break;
                case LAMPARA:
                    switch (estadoLampara){
                        case ENCENDIDA:
                            if(!sotano()){
                                abner.setLampara(Lampara.ENCENDIDALUZ);
                                estadoLampara = Lampara.ENCENDIDALUZ;
                            }
                            botonHabilidad.setTexture(lamparaOn);
                            break;
                        case ENCENDIDALUZ:
                            if(sotano()){
                                abner.setLampara(Lampara.ENCENDIDA);
                                estadoLampara = Lampara.ENCENDIDA;
                            }
                            botonHabilidad.setTexture(lamparaOn);
                            break;
                        case APAGADA:
                            botonHabilidad.setTexture(lamparaOff);
                            break;
                    }
                    break;
                case VACIA:
                    botonHabilidad.setTexture(habilidadDes);
                    break;
            }


            /*if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
                pad.getLeft().setEstado(Boton.Estado.PRESIONADO);
            else{
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT))
                pad.getRight().setEstado(Boton.Estado.PRESIONADO);
            else {
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.DPAD_UP)||Gdx.input.isKeyJustPressed(Input.Keys.DPAD_DOWN)){
                habilidadSiguiente();
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.A)&&!abner.isJumping()&&!abner.isAttacking())
                botonSaltar.setEstado(Boton.Estado.PRESIONADO);
            if(Gdx.input.isKeyJustPressed(Input.Keys.S)&&!abner.isAttacking())
                botonArma.setEstado(Boton.Estado.PRESIONADO);

            if(Gdx.input.isKeyJustPressed(Input.Keys.D)&&!abner.isJumping()&&!abner.isAttacking()&&abner.getPogo()){
                botonHabilidad.setEstado(Boton.Estado.PRESIONADO);
            }*/


            if(botonSaltar.isPressed()) {
                abner.setEstadoVertical(Abner.Vertical.ACTIVADO);
                abner.setSalto(Abner.Salto.SUBIENDO);
            }

            if(botonArma.isPressed()){
                abner.setEstadoAtaque(Abner.Ataque.ACTIVADO);
            }

            if(botonHabilidad.isPressed()){
                switch (habilidadActual){
                    case POGO:
                        abner.setEstadoVertical(Abner.Vertical.POGO);
                        abner.setSalto(Abner.Salto.SUBIENDO);
                        break;
                    case LANZAPAPAS:
                        abner.setEstadoAtaque(Abner.Ataque.LANZAPAPAS);
                        break;
                    case LAMPARA:
                        switch (estadoLampara){
                            case ENCENDIDA: case ENCENDIDALUZ:
                                estadoLampara = Lampara.APAGADA;
                                abner.setLampara(Lampara.APAGADA);
                                break;
                            case APAGADA:
                                abner.setLampara(sotano()?Lampara.ENCENDIDA:Lampara.ENCENDIDALUZ);
                                estadoLampara = sotano()?Lampara.ENCENDIDA:Lampara.ENCENDIDALUZ;
                                break;
                        }
                }
            }

            if(pad.getRight().isPressed()) {
                right = true;
                abner.setEstadoHorizontal(Abner.Horizontal.ACTIVADO);
            }

            else if(pad.getLeft().isPressed()) {
                right = false;
                abner.setEstadoHorizontal(Abner.Horizontal.ACTIVADO);
            }

            int cambio= abner.cambioNivel();
            if(cambio>=0){
                if(mapaActual<=6)
                    abner.setEstadoVertical(Abner.Vertical.DESACTIVADO);
                int tempMapa = mapaActual;
                transicion = Transicion.AUMENTANDO;
                mapaActual = cambio;
                switch (mapaActual){
                    case 3:
                        if(tempMapa==2){
                            transicionNivel.setTexture(transicionCocina);
                            velocidadTransicion = TRANSICIONNIVEL;
                        }
                        break;
                    case 6:
                        if(tempMapa==2){
                            transicionNivel.setTexture(transicionJardin);
                            velocidadTransicion = TRANSICIONNIVEL;
                        }
                        break;
                    case 9:
                        if(tempMapa ==0){
                            transicionNivel.setTexture(transicionArmario);
                            velocidadTransicion = TRANSICIONNIVEL;
                        }
                        break;
                    default:
                        transicionNivel.setTexture(transicionNeutral);
                        velocidadTransicion = TRANSICIONNEUTRAL;
                }
                mapa = mapManager.getMapa(mapas.get(mapaActual), mapaActual, abner, gameInfo);
                abner.setMapa(mapa);
                abner.setInitialPosition(tempMapa);
                estado = Estado.CAMBIO;
                mapa.draw();
                enemigos = mapa.getEnemigos();
            }

            if(enemigos!=null) {
                for (Enemigo enemigo : enemigos) {
                    if (abner.colisionEnemigo(enemigo))
                        break;
                    if(enemigo.muerte()){
                        Sprite sprite = new Sprite(malteada);
                        sprite.setPosition(enemigo.getX(), enemigo.getY());
                        malteadas.add(sprite);
                    }
                }
            }
            proyectiles = abner.getProyectiles();
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            fondoCielo.draw(batch);
            batch.end();
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            batch.begin();
            abner.draw(batch, right);
            /*for(Sprite sprite:malteadas) {
                if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                    abner.setCantVida(abner.getcantVida()+10);
                    malteadas.removeIndex(malteadas.indexOf(sprite, true));
                }
                else {
                    if(!mapa.colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY()-10))
                        sprite.translate(0,-10);
                    sprite.draw(batch);
                }
            }*/

            for (int i=0;i<proyectiles.size();i++) {
                Proyectil proyectil = proyectiles.get(i);
                if(proyectil instanceof Proyectil.Papa&&mapa.colisionPuertaCerrada(proyectil.getRectangle().getX()-proyectil.getRectangle().getWidth(), proyectil.getRectangle().getY())){
                    mapa.remove("PuertaCerrada");
                    proyectiles.remove(i);

                }
                else if(proyectil.out()) {
                    proyectiles.remove(i);
                }
                else{
                    proyectil.draw(batch);
                }

            }



            mapa.drawE();


            batch.end();

            if(estado ==Estado.CAMBIO){
                switch (transicion){
                    case AUMENTANDO:
                        pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                        pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                        abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
                        alpha = 1;
                        transicion = Transicion.DISMINUYENDO;
                        break;
                    case DISMINUYENDO:
                        alpha-= velocidadTransicion;
                        if (alpha<=0) {
                            estado = Estado.JUGANDO;
                            alpha = 0;
                        }
                        break;
                }
            }


            transicionNivel.setAlpha(alpha);
            batch.setProjectionMatrix(camaraHUD.combined);
            saveB = abner.guardar();
            if(saveB)
                botonSave.desaparecer(!saveB);
            else
                botonSave.desaparecer(!saveB);
            batch.begin();
            botonSaltar.draw(batch);
            pad.draw(batch);
            botonHabilidad.draw(batch);
            botonSave.draw(batch);
            botonArma.draw(batch);
            pausa.draw(batch);
            imgVida.draw(batch);
            if(habilidadActual == Habilidad.LANZAPAPAS){
                imgMunicion.draw(batch);
                municionTex.mostrarMensaje(batch, abner.getMunicion()+"");
            }
            vida.mostrarMensaje(batch, "" + abner.getcantVida());
            for(int i = 0;i<abner.getVidas();i++)
                vidas.get(i).draw(batch);
            transicionNivel.draw(batch);
            batch.end();
        }

        else if(estado == Estado.PAUSA){
            pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
            pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
            abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            switch (estadoPausa){
                case PRINCIPAL:
                    pausaActual.setTexture(pausaTex);
                    break;
                case QUIT:
                    pausaActual.setTexture(quitTex);
                    break;
                case OPCIONES:
                    pausaActual.setTexture(opciones);
                    break;
            }

            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            pausaActual.draw(batch);
            batch.end();
        }
        else if(estado == Estado.MUERTE){
            if(ambiente.isPlaying())
                ambiente.stop();
            if(alphaGame<1&&musica)
                gameover.play();
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            alphaGame+= TRANSICIONNEUTRAL;
            if(alphaGame>=1)
                alphaGame=1;
            menuGameOver.setAlpha(alphaGame);
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            menuGameOver.draw(batch);
            batch.end();
        }




    }

    public static boolean sotano() {
        return mapaActual == 13 || mapaActual ==14||mapaActual == 15;
    }

    private void habilidadSiguiente() {
        if(habilidadActual!= Habilidad.VACIA){
            switch (habilidadActual){
                case POGO:
                    if(abner.lanzapapas)
                        habilidadActual = Habilidad.LANZAPAPAS;
                    break;
                case LANZAPAPAS:
                    if(abner.lampara)
                        habilidadActual = Habilidad.LAMPARA;
                    else
                        habilidadActual = Habilidad.POGO;
                    break;
                case LAMPARA:
                    estadoLampara = Lampara.APAGADA;
                    abner.setLampara(estadoLampara);
                    habilidadActual = Habilidad.POGO;
                    break;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        vista.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();

    }



    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        camaraHUD.unproject(v);
        float x = v.x;
        float y = v.y;
        if(estado == Estado.JUGANDO) {
            if (pad.getLeft().contiene(x, y))
                pad.getLeft().setEstado(Boton.Estado.PRESIONADO);
            if (pad.getRight().contiene(x, y))
                pad.getRight().setEstado(Boton.Estado.PRESIONADO);
            if (botonSaltar.contiene(x, y) & (!abner.isJumping()) & (!abner.isAttacking()))
                botonSaltar.setEstado(Boton.Estado.PRESIONADO);
            if (botonArma.contiene(x, y) & (!abner.isAttacking()))
                botonArma.setEstado(Boton.Estado.PRESIONADO);
            if(pausa.contiene(x,y))
                estado = Estado.PAUSA;
            if(saveB) {
                if(botonSave.contiene(x,y)) {
                    gameInfo.guardarJuego();
                    botonSave.setEstado(Boton.Estado.PRESIONADO);
                }
            }
            if(botonCambioDown.contiene(x,y)||botonCambioUp.contiene(x,y)){
                habilidadSiguiente();
            }
            if(abner.getPogo()){
                if(botonHabilidad.contiene(x,y)&&!abner.isJumping()&&!abner.isAttacking())
                    botonHabilidad.setEstado(Boton.Estado.PRESIONADO);
            }


        }

        if(estado == Estado.PAUSA){
            switch (estadoPausa){
                case PRINCIPAL:
                    if(botonResume.contiene(x,y))
                        estado = Estado.JUGANDO;
                    if(botonOpciones.contiene(x,y))
                        estadoPausa = EstadoPausa.OPCIONES;
                    if(botonQuit.contiene(x,y))
                        estadoPausa = EstadoPausa.QUIT;
                    break;
                case QUIT:
                    if(botonNo.contiene(x,y))
                        estadoPausa = EstadoPausa.PRINCIPAL;
                    if(botonYes.contiene(x,y)){
                        juego.setScreen(new MenuPrincipal(juego));
                        ambiente.stop();
                    }
                    break;
                case OPCIONES:
                    if(botonOn.contiene(x,y))
                        musica = true;
                    else if(botonOff.contiene(x,y))
                        musica = false;
                    else if(botonBack.contiene(x,y))
                        estadoPausa = EstadoPausa.PRINCIPAL;
                    break;
            }
        }

        if(estado == Estado.MUERTE){
            if(botonMain.contiene(x,y)){
                juego.setScreen(new MenuPrincipal(juego));
                if(gameover.isPlaying())
                    gameover.stop();
            }
            if(botonTry.contiene(x,y)){
                reiniciarEscena();
                estado = Estado.JUGANDO;
                alphaGame = 0;
            }
        }

        return false;
    }

    private void reiniciarEscena() {
        abner.reiniciar(gameInfo);
        mapaActual = gameInfo.getMapa();
        mapa = mapManager.getNewMapa(mapas.get(mapaActual),mapaActual, abner, gameInfo);
        abner.setMapa(mapa);
        if(!gameInfo.isPogo()){
            habilidadActual = Habilidad.VACIA;
        }
        botonHabilidad.setEstado(Boton.Estado.NOPRESIONADO);
        botonSaltar.setEstado(Boton.Estado.NOPRESIONADO);
        pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
        pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
        botonArma.setEstado(Boton.Estado.NOPRESIONADO);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        camaraHUD.unproject(v);
        float x = v.x;
        float y = v.y;
        if(estado == Estado.JUGANDO){
            if(pad.getLeft().contiene(x,y)) {
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
            if(pad.getRight().contiene(x,y)) {
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        camaraHUD.unproject(v);
        float x = v.x;
        float y = v.y;
        if(estado == Estado.JUGANDO){
            if(pad.getLeft().contiene(x,y))
                pad.getLeft().setEstado(Boton.Estado.PRESIONADO);
            else{
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
            if(pad.getRight().contiene(x,y))
                pad.getRight().setEstado(Boton.Estado.PRESIONADO);
            else{
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }



    private enum EstadoPausa{
        PRINCIPAL,
        OPCIONES,
        QUIT
    }

    private enum Estado{
        JUGANDO,
        CAMBIO,
        PAUSA,
        MUERTE
    }

    private enum Transicion{
        AUMENTANDO,
        DISMINUYENDO
    }

    public enum Habilidad{
        VACIA,
        POGO,
        LANZAPAPAS,
        LAMPARA
    }

    public enum Lampara{
        ENCENDIDA,
        ENCENDIDALUZ,
        APAGADA
    }

}
