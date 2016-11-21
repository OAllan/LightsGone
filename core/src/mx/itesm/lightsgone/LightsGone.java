package mx.itesm.lightsgone;

import com.badlogic.gdx.Application;
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
import java.util.Random;

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
    public static final float ANCHOBOTON = 137;
    private static final float ALTOBOTON = 134;
    private float velocidadTransicion = TRANSICIONNEUTRAL;
    public static final int LATAX = 4871;
    private final float VIDACOCO = 12;
    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private Music ambiente, gameover, leche, recarga;
    private Juego juego;
    private AssetManager assetManager = new AssetManager();
    public static Texture plataforma;
    private Texture  capa,capa25,capa50,capa0,capa75,municion, habilidadLanzaPapas, transicionCocina,transicionJardin, transicionArmario, transicionSotano, transicionCoco, transicionNeutral,  dano, nivelVida, gameOver,habilidadDes, habilidadPogo,save,pausaTex,quitTex, opciones,  botonSalto, JFondo, botonVida, habilidad, texPausa;
    private Sprite transicionNivel, pausaActual, fondoCielo;
    private static Abner abner;
    private Texto vida, municionTex;
    private Pad pad;
    private static Texture malteada,papa;
    private Sprite imgVida, menuGameOver, imgMunicion;
    private boolean right, saveB;
    public static boolean musica;
    private Boton botonBack, botonOn, botonOff, botonTry, botonMain,botonSaltar, botonArma, pausa, botonResume, botonOpciones,botonQuit, botonYes,botonNo, botonSave, botonHabilidad;
    private float alpha = 0;
    private Array<String> mapas;
    private static Mapa mapa;
    static int mapaActual;
    private ArrayList<Proyectil> proyectiles;
    private Transicion transicion;
    private Estado estado;
    private EstadoPausa estadoPausa;
    private GameInfo gameInfo;
    private float alphaGame;
    private MapManager mapManager;
    public static Habilidad habilidadActual;
    private Array<Sprite> vidas;
    private Arma estadoArma;
    private Sprite flechasArma, flechasHabilidad, cascaronBarraCoco, barraCoco;
    private boolean switchAtaque, switchHabilidad, switchSalto, switchedSalto,switchedAtaque, switchedHabilidad, cinematicaPelea;
    private static boolean capaActiva;
    public static boolean cinematicaInicio;
    private static Array<Sprite> malteadas, papas;
    private int leftPointer, rightPointer;
    private float timer, timerR, timerG;
    private static Capa estadoCapa;
    private Salto saltoActual;
    private Cinematica cinematica;
    private int contador = 1;
    private float anchBarra;


    Array<Enemigo> enemigos;


    private static Lampara estadoLampara;
    private Texture lamparaOff, lamparaOn;
    private Sprite flechasSalto;

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

    public static boolean getLampara() {
        return abner.getLampara();
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
        mapas = new Array<String>(18);
        mapManager = new MapManager(camara, batch);
        abner = new Abner(camara, null, gameInfo);
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
        mapas.add("Sotano3.tmx");
        mapas.add("CaminoAlCoco.tmx");
        mapas.add("Atico1.tmx");
        mapas.add("Atico2.tmx");
        mapaActual = gameInfo.getMapa();
        mapa = mapManager.getNewMapa(mapas.get(mapaActual),mapaActual,abner, gameInfo);
        abner.setMapa(mapa);
        transicion = Transicion.DISMINUYENDO;
        enemigos = mapa.getEnemigos();
        musica = true;
        switchAtaque = false;
        switchHabilidad = false;
        cinematicaInicio = gameInfo.isCinematicaInicio();
        cinematicaPelea = false;
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
        flechasArma.setPosition(botonArma.getX() + ANCHOBOTON-flechasArma.getWidth()-10, botonArma.getY()+ALTOBOTON-flechasArma.getHeight()-8);
        pausa = new Boton(texPausa, ANCHO_MUNDO- texPausa.getWidth(), ALTO_MUNDO - texPausa.getHeight(), false);
        imgVida = new Sprite(botonVida);
        malteadas = new Array<Sprite>();
        papas = new Array<Sprite>();
        imgVida.setPosition(0, 780 - imgVida.getHeight());
        imgMunicion = new Sprite(municion);
        imgMunicion.setPosition(MUNICIONX, MUNICIONY);
        vida = new Texto("tipo.fnt", imgVida.getWidth(),690);
        municionTex = new Texto("tipo.fnt", imgMunicion.getX(), MUNICIONY+60);
        transicionNivel = new Sprite(transicionNeutral);
        estadoLampara = Lampara.APAGADA;
        if(gameInfo.isLampara())
            habilidadActual = Habilidad.LAMPARA;
        else
            habilidadActual = Habilidad.VACIA;
        saltoActual = Salto.NORMAL;
        transicionNivel.setSize(LightsGone.ANCHO_MUNDO, LightsGone.ALTO_MUNDO);
        enemigos = mapa.getEnemigos();
        gameInfo.setAbner(abner);
        estado = Estado.JUGANDO;
        botonHabilidad = new Boton(habilidadDes, ANCHO_MUNDO-habilidadDes.getWidth()-10,YBOTON,false);
        flechasHabilidad.setPosition(botonHabilidad.getX()+10, botonHabilidad.getY()+ALTOBOTON-flechasHabilidad.getHeight()+10);
        flechasSalto.setPosition(botonSaltar.getX()+10, botonSaltar.getY()+ALTOBOTON-flechasSalto.getHeight()-10);
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
        alphaGame = 0;
        estadoArma = Arma.RESORTERA;
        ambiente.setVolume(0.5f);
        leftPointer = -1;
        rightPointer = -1;
        estadoCapa = Capa.CAPA100;
        capaActiva = false;
        timer = 8;
        timerR = 0;
        timerG = 0;

    }



    private void cargarTexturas() {
        assetManager.load("BotonSalto.png", Texture.class);
        assetManager.load("JoystickBoton.png", Texture.class);
        assetManager.load("BotonPausa.png", Texture.class);
        assetManager.load("BotonVida.png", Texture.class);
        assetManager.load("BotonResortera.png", Texture.class);
        assetManager.load("nivel.png", Texture.class);
        assetManager.load("PlataformaInclinada.png", Texture.class);
        assetManager.load("menuPausa.jpg", Texture.class);
        assetManager.load("menuPausaQuit.jpg", Texture.class);
        assetManager.load("menuPausaAudio.jpg", Texture.class);
        assetManager.load("Save6.png", Texture.class);
        assetManager.load("BotonHabilidadDesactivado.png", Texture.class);
        assetManager.load("BotonHabPogo.png", Texture.class);
        assetManager.load("gameOver.png", Texture.class);
        assetManager.load("BotonNivelVida.png", Texture.class);
        assetManager.load("MalteadaMundo.png", Texture.class);
        assetManager.load("PapaMundo.png", Texture.class);
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
        assetManager.load("FlechasArmas.png", Texture.class);
        assetManager.load("FlechasHabilidades.png",Texture.class);
        assetManager.load("FlechasSaltos.png", Texture.class);
        assetManager.load("BotonHabCapa100.png", Texture.class);
        assetManager.load("BotonHabCapa0.png", Texture.class);
        assetManager.load("BotonHabCapa25.png", Texture.class);
        assetManager.load("BotonHabCapa50.png", Texture.class);
        assetManager.load("BotonHabCapa75.png", Texture.class);
        assetManager.load("ambiente.mp3", Music.class);
        assetManager.load("risa.mp3", Music.class);
        assetManager.load("leche.mp3", Music.class);
        assetManager.load("recargar papas.mp3", Music.class);
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
        pausaTex = assetManager.get("menuPausa.jpg");
        opciones = assetManager.get("menuPausaAudio.jpg");
        quitTex = assetManager.get("menuPausaQuit.jpg");
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
        lamparaOff = assetManager.get("BotonHabLamparaOff.png");
        lamparaOn = assetManager.get("BotonHabLamparaOn.png");
        flechasArma = new Sprite((Texture)assetManager.get("FlechasArmas.png"));
        flechasHabilidad = new Sprite((Texture)assetManager.get("FlechasHabilidades.png"));;
        flechasSalto = new Sprite((Texture)assetManager.get("FlechasSaltos.png"));
        leche = assetManager.get("leche.mp3");
        papa = assetManager.get("PapaMundo.png");
        recarga = assetManager.get("recargar papas.mp3");
        capa = assetManager.get("BotonHabCapa100.png");
        capa0 = assetManager.get("BotonHabCapa0.png");
        capa25 = assetManager.get("BotonHabCapa25.png");
        capa50 = assetManager.get("BotonHabCapa50.png");
        capa75 = assetManager.get("BotonHabCapa75.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(abner.getLampara()&&habilidadActual == Habilidad.VACIA)
            habilidadActual = Habilidad.LAMPARA;


        estado = abner.isDead()?Estado.MUERTE:estado;

        if(estado == Estado.GANO){
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            batch.begin();
            abner.draw(batch, right);
            batch.end();
            timerG+=Gdx.graphics.getDeltaTime();
            if(timerG>=6){
                batch.setProjectionMatrix(camaraHUD.combined);
                batch.begin();
                cinematica.play(batch);
                batch.end();
            }
            if(cinematica.finished()){
                stop();
                juego.setScreen(new MenuPrincipal(juego));
            }
        }

        else if(estado == Estado.CINEMATICA){
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            cinematica.play(batch);
            batch.end();
            if(cinematica.finished()){
                estado = Estado.CAMBIO;
                transicion = Transicion.AUMENTANDO;
            }
            if(cinematicaPelea){
                Array<Enemigo> enemigos = new Array<Enemigo>();
                enemigos.add(new Enemigo.Coco(mapa, abner));
                mapa.setEnemigos(enemigos);
                Enemigo.Coco coco = (Enemigo.Coco)enemigos.get(0);
                cascaronBarraCoco = coco.getCascaron();
                barraCoco = coco.getBarra();
                this.enemigos = mapa.getEnemigos();
                anchBarra = barraCoco.getWidth();
            }
        }
        else if(estado != Estado.PAUSA&&estado!=Estado.MUERTE){

            switch(estadoArma){
                case LANZAPAPAS:
                    botonArma.setTexture(habilidadLanzaPapas);
                    break;
                case RESORTERA:
                    botonArma.setTexture(habilidad);
                    break;
            }

            switch (habilidadActual){
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
                case CAPITA:
                    switch (estadoCapa){
                        case APAGADA:
                            if(capaActiva){
                                capaActiva = false;
                                timer = 8;
                            }
                            timerR += Gdx.graphics.getDeltaTime();
                            botonHabilidad.setTexture(capa0);
                            if(timerR>=2.5){
                                estadoCapa = Capa.CAPA25;
                            }
                            break;
                        case CAPA25:
                            botonHabilidad.setTexture(capa25);
                            if(capaActiva){
                                timer -= Gdx.graphics.getDeltaTime();
                                if(timer<=0){
                                    estadoCapa = Capa.APAGADA;
                                }
                            }
                            else{
                                timerR+=Gdx.graphics.getDeltaTime();
                                if(timerR>=5){
                                    estadoCapa = Capa.CAPA50;
                                }
                            }
                            break;
                        case CAPA50:
                            botonHabilidad.setTexture(capa50);
                            if(capaActiva){
                                timer-=Gdx.graphics.getDeltaTime();
                                if(timer<=2){
                                    estadoCapa = Capa.CAPA25;
                                }
                            }
                            else {
                                timerR += Gdx.graphics.getDeltaTime();
                                if(timerR>=7.5){
                                    estadoCapa = Capa.CAPA75;
                                }
                            }
                            break;
                        case CAPA75:
                            botonHabilidad.setTexture(capa75);
                            if(capaActiva){
                                timer-=Gdx.graphics.getDeltaTime();
                                if(timer<=4){
                                    estadoCapa = Capa.CAPA50;
                                }
                            }
                            else {
                                timerR += Gdx.graphics.getDeltaTime();
                                if(timerR>=10){
                                    estadoCapa = Capa.CAPA100;
                                    timerR = 0;
                                }
                            }
                            break;
                        case CAPA100:
                            botonHabilidad.setTexture(capa);
                            if(capaActiva){
                                timer-= Gdx.graphics.getDeltaTime();
                                if(timer<=6){
                                    estadoCapa = Capa.CAPA75;
                                }
                            }
                        break;
                    }
                    break;
                case VACIA:
                    botonHabilidad.setTexture(habilidadDes);
                    break;
            }

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



            if(timerR!=0&&habilidadActual!=Habilidad.CAPITA){
                timerR+=Gdx.graphics.getDeltaTime();
                if(timerR>=10){
                    estadoCapa = Capa.CAPA100;
                    timerR = 0;
                }
                else if(timerR>=7.5){
                    estadoCapa = Capa.CAPA75;
                }
                else if (timerR>=5){
                    estadoCapa = Capa.CAPA50;
                }
                else if(timerR>=2.5){
                    estadoCapa = Capa.CAPA25;
                }
            }

            switch (saltoActual){
                case NORMAL:
                    botonSaltar.setTexture(botonSalto);
                    break;
                case POGO:
                    botonSaltar.setTexture(habilidadPogo);
                    break;
            }

            //Inicio de los controles del teclado, si quieren habilitarlos quiten el /* al inicio y el */ al final

            if(Gdx.app.getType()== Application.ApplicationType.Desktop){
                if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT))
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
                }
            }
            else {
                if(!Gdx.input.isTouched()){
                    abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
                    pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                    pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                }
            }


            //Fin de los controles del teclado


            if(botonSaltar.isPressed()) {
                switch (saltoActual){
                    case POGO:
                        abner.setEstadoVertical(Abner.Vertical.POGO);
                        abner.setSalto(Abner.Salto.SUBIENDO);
                        break;
                    case NORMAL:
                        abner.setEstadoVertical(Abner.Vertical.ACTIVADO);
                        abner.setSalto(Abner.Salto.SUBIENDO);
                        break;
                }
            }


            if(botonHabilidad.isPressed()){
                switch (habilidadActual){
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
                        break;
                    case CAPITA:
                        if(!capaActiva&&estadoCapa==Capa.CAPA100){
                            capaActiva=true;
                        }
                        break;
                }
            }

            if(botonArma.isPressed()){
                if(!capaActiva){
                    switch(estadoArma){
                        case LANZAPAPAS:
                            abner.setEstadoAtaque(Abner.Ataque.LANZAPAPAS);
                            break;
                        case RESORTERA:
                            abner.setEstadoAtaque(Abner.Ataque.ACTIVADO);
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


            if(mapaActual==1&&abner.getX()>=1300&&!cinematicaInicio){
                estado = Estado.CINEMATICA;
                cinematica = new Cinematica.Inicio(abner);
                cinematicaInicio = true;
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
                stop();
            }

            if(mapaActual==mapas.size-1&&abner.getX()<=1500&&!cinematicaPelea){
                estado = Estado.CINEMATICA;
                cinematica = new Cinematica.Pelea(abner);
                cinematicaPelea = true;
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
                stop();
            }

            if(mapaActual==mapas.size-1&&cinematicaPelea&&enemigos!=null){
                Enemigo.Coco coco = (Enemigo.Coco)mapa.getEnemigos().get(0);
                float vidaCoco = coco.getVida();
                float porcentaje = vidaCoco/VIDACOCO;
                if(vidaCoco<=0){
                    cinematica = new Cinematica.Final();
                    estado = Estado.GANO;
                }
                barraCoco.setSize(anchBarra*porcentaje,barraCoco.getHeight());

            }
            if(enemigos!=null){
                Gdx.app.log("Enemigos: ", enemigos.size+"");
            }

            int cambio= abner.cambioNivel();
            if(cambio>=0){
                if(mapaActual<=6||mapaActual>15)
                    abner.setEstadoVertical(Abner.Vertical.DESACTIVADO);
                int tempMapa = mapaActual;
                transicion = Transicion.AUMENTANDO;
                mapaActual = cambio;
                malteadas = new Array<Sprite>();
                papas = new Array<Sprite>();
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
                    case 13:
                        if(tempMapa == 2){
                            transicionNivel.setTexture(transicionSotano);
                            velocidadTransicion = TRANSICIONNIVEL;
                        }
                        break;
                    case 16:
                        if(tempMapa==1){
                            transicionNivel.setTexture(transicionCoco);
                            velocidadTransicion = TRANSICIONNIVEL;
                        }
                        break;
                    default:
                        transicionNivel.setTexture(transicionNeutral);
                        velocidadTransicion = TRANSICIONNEUTRAL;
                        break;
                }
                mapa.stop();
                mapa.dispose();
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
                }
            }
            proyectiles = abner.getProyectiles();
            if(jardin()){
                batch.setProjectionMatrix(camaraHUD.combined);
                batch.begin();
                fondoCielo.draw(batch);
                batch.end();
            }
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            batch.begin();
            abner.draw(batch, right);
            for(Sprite sprite:malteadas) {
                if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                    abner.setCantVida(abner.getcantVida()+10);
                    leche.play();
                    malteadas.removeIndex(malteadas.indexOf(sprite, true));
                }
                else {
                    if(mapa.colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY()-10)==-1)
                        sprite.translate(0,-10);
                    sprite.draw(batch);
                }
            }

            for(Sprite sprite:papas) {
                if(abner.getBoundingRectangle().overlaps(sprite.getBoundingRectangle())){
                    abner.setPapas(abner.getPapas()+4);
                    recarga.play();
                    papas.removeIndex(papas.indexOf(sprite, true));
                }
                else {
                    if(mapa.colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY()-10)==-1)
                        sprite.translate(0,-10);
                    sprite.draw(batch);
                }
            }

            for (int i=0;i<proyectiles.size();i++) {
                Proyectil proyectil = proyectiles.get(i);
                if(proyectil instanceof Proyectil.Papa&&mapa.colisionPuertaCerrada(proyectil.getRectangle().getX()-proyectil.getRectangle().getWidth(), proyectil.getRectangle().getY())){
                    mapa.remove("PuertaCerrada");
                    proyectiles.remove(i);
                    abner.setArmario(true);
                    gameInfo.actualizarDatosTemp();
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
            if(cinematicaPelea&&barraCoco!=null){
                barraCoco.draw(batch);
                cascaronBarraCoco.draw(batch);
            }
            botonSaltar.draw(batch);
            pad.draw(batch);
            botonHabilidad.draw(batch);
            botonSave.draw(batch);
            botonArma.draw(batch);

            if(abner.getCapita()){
                flechasHabilidad.draw(batch);
            }
            if(abner.getLanzapapas()){
                flechasArma.draw(batch);
            }
            if(abner.getPogo()){
                flechasSalto.draw(batch);
            }
            pausa.draw(batch);
            imgVida.draw(batch);
            if(estadoArma == Arma.LANZAPAPAS){
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

    private boolean jardin() {
        return mapaActual == 6 || mapaActual ==7||mapaActual==8;
    }

    public static boolean sotano() {
        return (abner.getLampara()&&mapaActual==12)||mapaActual == 13 || mapaActual ==14||mapaActual == 15;
    }

    private void saltoSiguiente(){
        if(abner.getPogo()){
            if(saltoActual==Salto.POGO){
                saltoActual = Salto.NORMAL;
            }
            else if(saltoActual == Salto.NORMAL){
                saltoActual = Salto.POGO;
            }
        }
    }

    private void habilidadSiguiente() {
        if(habilidadActual!= Habilidad.VACIA){
            switch (habilidadActual){
                case LAMPARA:
                    if(abner.capita){
                        estadoLampara = Lampara.APAGADA;
                        abner.setLampara(estadoLampara);
                        habilidadActual = Habilidad.CAPITA;
                    }
                    break;
                case CAPITA:
                    if(!capaActiva)
                        habilidadActual = Habilidad.LAMPARA;
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

    public static Lampara getEstadoLampara(){
        return estadoLampara;
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        abner.dispose();

    }

    static void agregarItem(float x, float y){
        Random rnd = new Random();
        switch (rnd.nextInt(2)){
            case 0:
                Sprite sprite = new Sprite(malteada);
                sprite.setPosition(x,y);
                malteadas.add(sprite);
                break;
            case 1:
                Sprite sprite1 = new Sprite(papa);
                sprite1.setPosition(x,y);
                papas.add(sprite1);
                break;
        }
    }

    static void agregarItem(Enemigo enemigo){
        if(!abner.getLanzapapas()){
            Random rnd = new Random();
            int i = rnd.nextInt(2);
            if(i==0){
                Sprite sprite = new Sprite(malteada);
                sprite.setPosition(enemigo.getX(), enemigo.getY()+100);
                malteadas.add(sprite);
            }
        }
        else {
            Random rnd = new Random();
            int i = rnd.nextInt(3);
            if(i==0){
                Sprite sprite = new Sprite(malteada);
                sprite.setPosition(enemigo.getX(), enemigo.getY()+100);
                malteadas.add(sprite);
            }
            else if(i==1){
                Sprite sprite = new Sprite(papa);
                sprite.setPosition(enemigo.getX(), enemigo.getY()+100);
                papas.add(sprite);
            }
        }

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
            if (pad.getLeft().contiene(x, y)){
                pad.getLeft().setEstado(Boton.Estado.PRESIONADO);
                leftPointer = pointer;
            }
            if (pad.getRight().contiene(x, y)){
                pad.getRight().setEstado(Boton.Estado.PRESIONADO);
                leftPointer = pointer;
            }
            if (botonSaltar.contiene(x, y)){
                switchedSalto = false;
                switchSalto = true;
                rightPointer = pointer;
            }
            if (botonArma.contiene(x, y)){
                switchAtaque = true;
                rightPointer = pointer;
                switchedAtaque = false;
            }
            if(pausa.contiene(x,y))
                estado = Estado.PAUSA;
            if(saveB) {
                if(botonSave.contiene(x,y)) {
                    gameInfo.guardarJuego();
                    botonSave.setEstado(Boton.Estado.PRESIONADO);
                }
            }

            if(abner.getLampara()){
                if(botonHabilidad.contiene(x,y)) {
                    switchHabilidad = true;
                    rightPointer = pointer;
                    switchedHabilidad = false;
                }
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
                        mapa.stop();
                    }
                    break;
                case OPCIONES:
                    if(botonOn.contiene(x,y)){
                        musica = true;
                    }
                    else if(botonOff.contiene(x,y)){
                        musica = false;
                        stop();
                    }
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
                mapa.stop();
            }
            if(botonTry.contiene(x,y)){
                reiniciarEscena();
                gameover.stop();
                estado = Estado.JUGANDO;
                alphaGame = 0;
                mapa.stop();
            }
        }

        return false;
    }

    private void reiniciarEscena() {
        abner.reiniciar(gameInfo);
        mapaActual = gameInfo.getMapa();
        alpha = 0;
        mapa.stop();
        mapa = mapManager.getNewMapa(mapas.get(mapaActual),mapaActual, abner, gameInfo);
        abner.setMapa(mapa);
        enemigos = mapa.getEnemigos();
        if(cinematicaPelea){
            cinematicaPelea = false;
        }
        if(!gameInfo.isLampara()){
            habilidadActual = Habilidad.VACIA;
        }
        if(!gameInfo.isLanzapapas()){
            estadoArma = Arma.RESORTERA;
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
            if(pad.getLeft().contiene(x,y)&&pointer==leftPointer) {
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
                leftPointer =-1;
            }
            if(pad.getRight().contiene(x,y)&&pointer==leftPointer) {
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
                leftPointer =-1;
            }
            if(botonArma.contiene(x,y)&&(!abner.isAttacking())&&pointer==rightPointer&&!switchedAtaque){
                botonArma.setEstado(Boton.Estado.PRESIONADO);
                switchAtaque = false;
                rightPointer = -1;
            }
            if(abner.getLampara()&&botonHabilidad.contiene(x,y)&&!abner.isAttacking()&&!switchedHabilidad&&pointer==rightPointer){
                botonHabilidad.setEstado(Boton.Estado.PRESIONADO);
                switchHabilidad = false;
                rightPointer = -1;
            }
            if(botonSaltar.contiene(x,y)&&(!abner.isJumping())&&(!abner.isAttacking())&&!switchedSalto&&pointer==rightPointer){
                switchSalto = false;
                botonSaltar.setEstado(Boton.Estado.PRESIONADO);
                rightPointer = -1;
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
            if(pad.getLeft().contiene(x,y)&&leftPointer==pointer) {
                pad.getLeft().setEstado(Boton.Estado.PRESIONADO);
            }
            else if(leftPointer==pointer){
                pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
            if(pad.getRight().contiene(x,y)&&leftPointer==pointer)
                pad.getRight().setEstado(Boton.Estado.PRESIONADO);
            else if (leftPointer==pointer){
                pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
                abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
            }
            if(!botonArma.contiene(x,y)&&switchAtaque&&rightPointer==pointer){
                ataqueSiguiente();
                switchAtaque = false;
                switchedAtaque= true;
            }

            if(!botonSaltar.contiene(x,y)&switchSalto&&rightPointer == pointer){
                saltoSiguiente();
                switchSalto = false;
                switchedSalto = true;
            }

            if(!botonHabilidad.contiene(x,y)&&switchHabilidad&&rightPointer==pointer){
                habilidadSiguiente();
                switchHabilidad = false;
                switchedHabilidad = true;
            }
        }

        return false;
    }

    public static boolean getCapa(){
        return capaActiva;
    }

    private void ataqueSiguiente() {
        switch(estadoArma){
            case LANZAPAPAS:
                estadoArma = Arma.RESORTERA;
                break;
            case RESORTERA:
                estadoArma = abner.lanzapapas? Arma.LANZAPAPAS: estadoArma;
                break;
        }
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void stop(){
        if(ambiente.isPlaying()){
            ambiente.stop();
        }
        if(gameover.isPlaying()){
            gameover.stop();
        }
        if(leche.isPlaying()){
            leche.stop();
        }
        if(recarga.isPlaying()){
            recarga.stop();
        }
        mapa.stop();
        abner.stop();
    }

    private enum EstadoPausa{
        PRINCIPAL,
        OPCIONES,
        QUIT
    }

    private enum Estado{
        JUGANDO,
        CINEMATICA,
        CAMBIO,
        PAUSA,
        MUERTE,
        GANO
    }

    public enum Transicion{
        AUMENTANDO,
        DISMINUYENDO
    }

    public enum Habilidad{
        VACIA,
        LAMPARA,
        CAPITA
    }

    public enum Arma{
        RESORTERA,
        LANZAPAPAS
    }

    public enum Salto{
        POGO,
        NORMAL
    }

    public enum Lampara{
        ENCENDIDA,
        ENCENDIDALUZ,
        APAGADA
    }

    public enum Capa{
        APAGADA,
        CAPA25,
        CAPA50,
        CAPA75,
        CAPA100
    }

}
