package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_ADDPeer;

import org.omg.CORBA.portable.ValueInputStream;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Nivel0 implements Screen, InputProcessor{

    public static final int ANCHO_MUNDO = 1280;
    public static final int ALTO_MUNDO = 800;
    public static final int YBOTON = 30;
    public static final float YCOCINA3 = 361.0f;
    public static final int YCOCINA2 = 1950;
    public static final float YCOCINA1 = 2039.0f;
    public static final int YJARDIN1 = 1320;
    public static final int YJARDIN2 = 2200;
    public static final int YJARDIN3 = 700;
    public static final int YSALA = 1142;
    public static final float TRANSICIONNIVEL = 0.005f;
    public static final float TRANSICIONNEUTRAL = 0.01f;
    public static final int MUNICIONX = 264;
    public static final int MUNICIONY = 706;
    private float velocidadTransicion = TRANSICIONNEUTRAL;
    private final int LATAX = 4871;
    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private Music ambiente, gameover;
    private Juego juego;
    private AssetManager assetManager = new AssetManager();
    private Texture  municion,lanzapapas1, lanzapapas2, habilidadLanzaPapas, transicionCocina,transicionJardin, transicionArmario, transicionSotano, transicionCoco, transicionNeutral,  malteada, dano, nivelVida, gameOver,habilidadDes, habilidadPogo,save,pausaTex,quitTex, opciones, neutral, salto1, salto2, correr1, correr2, botonSalto, JFondo, botonVida, habilidad, texPausa, resortera1, resortera2, resortera3, plataforma;
    private Sprite transicionNivel, pausaActual, fondoCielo;
    private static Abner abner;
    private Texto vida, municionTex;
    private Pad pad;
    private Sprite imgVida, menuGameOver, imgMunicion;
    private boolean right, saveB;
    public static boolean musica;
    private Boton botonBack, botonOn, botonOff, botonTry, botonMain,botonSaltar, botonArma, pausa, botonResume, botonOpciones,botonQuit, botonYes,botonNo, botonSave, botonHabilidad;
    private float alpha = 0;
    private Array<Mapa> mapas;
    private static Mapa mapa;
    static int mapaActual;
    private ArrayList<Proyectil> proyectiles;
    private Transicion transicion;
    private Estado estado;
    private EstadoPausa estadoPausa;
    private GameInfo gameInfo;
    private final float YBAJA = 135f, YMEDIA = 800f, YALTA =1100f;
    private Texture pPogo1, pPogo2;
    private float alphaGame;
    private Habilidad habilidadActual;
    private Array<Sprite> vidas;
    private Array<Sprite> malteadas;
    static Array<Enemigo> enemigos = new Array<Enemigo>(3);
    static Array<Enemigo> enemigosC1 = new Array<Enemigo>(3);
    static Array<Enemigo> enemigosC2 = new Array<Enemigo>(9);
    static Array<Enemigo> enemigosPrueba= new Array<Enemigo>(9);


    public Nivel0(Juego juego) {
        this.juego = juego;
        right = true;
        saveB = false;
        gameInfo = new GameInfo();
    }

    public Nivel0(Juego juego, String nombre){
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
        mapas = new Array<Mapa>(6);
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3,pPogo1,pPogo2,dano, lanzapapas1,lanzapapas2,camara, mapa, gameInfo, fondoCielo);
        float[] cuartoAbnerX ={530, 2295}, pasilloX = {270, 4140}, salaX = {450,450,2280,2280}, cocina1X = {315,1305}, cocina2X = {5895,5895}, cocina3X = {630}, jardin1X = {1395, 20745,2170}, jardin2X ={540,18360}, jardin3X = {7740,495};
        boolean[] cuartoAbnerB = {true, false}, pasilloB = {true, false}, salaB={true, true, false, false}, cocina1B = {true, true},
                cocina2B = {false,false}, cocina3B = {true}, jardin1B = {true, false, false}, jardin2B = {true, true}, jardin3B = {true, true};
        int[] cuartoAbner = {0,1}, pasillo = {0,2}, sala = {1,0,6,3}, cocina1 = {2,4}, cocina2 = {3,5}, cocina3 = {4}, jardin1 = {2,7,8}, jardin2 = {6,8}, jardin3 = {6,7};
        mapas.add(new Mapa("CuartoAbner.tmx", batch, camara, cuartoAbner, cuartoAbnerB,cuartoAbnerX ,YBAJA, YBAJA));
        mapas.add(new Mapa("Pasillo.tmx", batch, camara, pasillo, pasilloB, pasilloX,YBAJA, YBAJA));
        mapas.add(new Mapa("Sala.tmx", batch, camara, sala, salaB, salaX,YALTA, YBAJA, YSALA, YMEDIA));
        mapas.add(new Mapa("Cocina1.tmx", batch, camara, cocina1, cocina1B,cocina1X, YBAJA, YCOCINA1));
        mapas.add(new Mapa("Cocina2.tmx", batch, camara, cocina2, cocina2B,cocina2X ,YBAJA, YCOCINA2));
        mapas.add(new Mapa("Cocina3.tmx", batch, camara, cocina3, cocina3B, cocina3X,YCOCINA3));
        mapas.add(new Mapa("Jardin1.tmx", batch, camara, jardin1, jardin1B, jardin1X,YCOCINA2, YJARDIN1,YBAJA));
        mapas.add(new Mapa("Jardin2.tmx", batch, camara, jardin2, jardin2B, jardin2X,YJARDIN2, YJARDIN2));
        mapas.add(new Mapa("Jardin3.tmx", batch, camara, jardin3, jardin3B, jardin3X,YBAJA, YJARDIN3));
        for(Mapa mapa: mapas)
            mapa.reiniciar(gameInfo);
        for(Mapa mapa: mapas)
            mapa.reiniciar(gameInfo);
        Sprite sprite = new Sprite(plataforma);
        sprite.setRotation(12);
        sprite.setPosition(ANCHO_MUNDO + 470, ALTO_MUNDO * 3 - 850);
        mapas.get(4).setPlataformasInclinada(sprite);
        mapaActual = gameInfo.getMapa();
        Array<Enemigo> enemigos = new Array<Enemigo>(3);
        Array<Enemigo> enemigosC2 = new Array<Enemigo>(3);
        mapa = mapas.get(mapaActual);
        enemigosC1.add(new Enemigo.Lata(LATAX,mapas.get(4).getHeight(),mapas.get(4)));
        enemigosC1.add(new Enemigo.Lata(LATAX, mapas.get(4).getHeight()+3300, mapas.get(4)));
        enemigosC1.add(new Enemigo.Lata(LATAX, mapas.get(4).getHeight()+6600, mapas.get(4)));
        enemigosC1.add(new Enemigo.Lata(2640, 160, mapas.get(4),""));
        enemigosC1.add(new Enemigo.Lata(1110, 160, mapas.get(4),""));
        mapas.get(4).setEnemigos(enemigos);
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
        if(gameInfo.isPogo())
            habilidadActual = Habilidad.POGO;
        else if(gameInfo.isLanzapapas())
            habilidadActual = Habilidad.LANZAPAPAS;
        else
            habilidadActual = Habilidad.VACIA;
        transicionNivel.setSize(Nivel0.ANCHO_MUNDO, Nivel0.ALTO_MUNDO);
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3,pPogo1,pPogo2,dano,lanzapapas1,lanzapapas2, camara, mapa, gameInfo,fondoCielo);

        //Moscas
        enemigos.add(new Enemigo.Mosca(2295, 293, abner, mapa));
        enemigos.add(new Enemigo.Mosca(2745, 2295, abner, mapa));
        enemigos.add(new Enemigo.Mosca(3420, 2295, abner, mapa));


        //Flamas
        enemigos.add(new Enemigo.Fuego(7650, 1010, abner, mapa));
        enemigos.add(new Enemigo.Fuego(8300, 1010, abner, mapa));
        enemigos.add(new Enemigo.Fuego(8660, 1010, abner, mapa));
        enemigos.add(new Enemigo.Fuego(9020, 1010, abner, mapa));
        enemigos.add(new Enemigo.Fuego(9110, 1010, abner, mapa));
        enemigos.add(new Enemigo.Fuego(9200, 1010, abner, mapa));
        enemigos.add(new Enemigo.Fuego(9290, 1010, abner, mapa));

        //Panes tostadores
        enemigos.add(new Enemigo.PanTostadora(7635, 2035, abner, mapa));
        enemigos.add(new Enemigo.PanTostadora(5873, 1995, abner, mapa));
        enemigos.add(new Enemigo.PanTostadora(4703, 1995, abner, mapa));

        //Tostadores
        enemigos.add(new Enemigo.Tostadora(7612, 2025, abner, mapa));
        enemigos.add(new Enemigo.Tostadora(5850, 1980, abner, mapa));
        enemigos.add(new Enemigo.Tostadora(4680, 1980, abner, mapa));

        //Brocolis
        enemigos.add(new Enemigo.Brocoli(3285, 500, abner, mapa));
        enemigos.add(new Enemigo.Brocoli(4410, 90, abner, mapa));
        enemigos.add(new Enemigo.Brocoli(5085, 90, abner, mapa));
        enemigos.add(new Enemigo.Brocoli(6435, 900, abner, mapa));
        enemigos.add(new Enemigo.Brocoli(9900, 1620, abner, mapa));
        enemigos.add(new Enemigo.Brocoli(8865, 2025, abner, mapa));


        //Enemigos cocina 2
        //Sopas
        enemigosC1.add(new Enemigo.Sopa(5265,180,abner,mapa));
        enemigosC1.add(new Enemigo.Sopa(4005,225,abner,mapa));
        enemigosC1.add(new Enemigo.Sopa(1395,1125,abner,mapa));

        //Brocolis
        enemigosC1.add(new Enemigo.Brocoli(765,495,abner,mapa));


        //Enemigos cocina 3
        //Moscas
        enemigosC2.add(new Enemigo.Mosca(3330,765,abner,mapa));
        enemigosC2.add(new Enemigo.Mosca(5355,1125,abner,mapa));


        //Sopas
        enemigosC2.add(new Enemigo.Sopa(2700,585,abner,mapa));
        enemigosC2.add(new Enemigo.Sopa(4635,945,abner,mapa));
        enemigosC2.add(new Enemigo.Sopa(5805,1530,abner,mapa));

        //Panes Tostadores
        enemigosC2.add(new Enemigo.PanTostadora(4298,780,abner,mapa));
        enemigosC2.add(new Enemigo.PanTostadora(6143,1320,abner,mapa));

        //Tostadores
        enemigosC2.add(new Enemigo.Tostadora(4275,765,abner,mapa));
        enemigosC2.add(new Enemigo.Tostadora(6120,1305,abner,mapa));

        //Enemigos prueba
        //enemigosPrueba.add(new Enemigo.Brocoli(450,150,abner,mapa));
        //enemigosPrueba.add(new Enemigo.Mosca(1100,150,abner,mapa));
        //enemigosPrueba.add(new Enemigo.Serpiente(800,150,abner,mapa));
        //enemigosPrueba.add(new Enemigo.ProyectilHongo(1360,220,abner,mapa));
        //enemigosPrueba.add(new Enemigo.Hongo(1300,150,abner,mapa));
        //enemigosPrueba.add(new Enemigo.Gnomo(1300,150,abner,mapa));
        //enemigosPrueba.add(new Enemigo.Cucarachon(1100,0,abner,mapa));



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
        alphaGame = 0;
        mapas.get(3).setEnemigos(enemigos);
        mapas.get(4).setEnemigos(enemigosC1);
        mapas.get(5).setEnemigos(enemigosC2);
        mapas.get(0).setEnemigos(enemigosPrueba);
        enemigos = mapa.getEnemigos();
        ambiente.setVolume(0.5f);

    }

    public static void crearNuevaMosca(float x,float y,Enemigo ene){
        if(enemigosPrueba.contains(ene,true)){
            enemigosPrueba.add(new Enemigo.Mosca(x,y,abner,mapa));
        }
    }

    private void cargarTexturas() {
        assetManager.load("PCorrer1.png",Texture.class);
        assetManager.load("PCorrer2.png", Texture.class );
        assetManager.load("PNeutral.png", Texture.class);
        assetManager.load("PSalto1.png", Texture.class);
        assetManager.load("PSalto2.png", Texture.class);
        assetManager.load("PSalto2.png", Texture.class);
        assetManager.load("BotonSalto.png", Texture.class);
        assetManager.load("JoystickBoton.png", Texture.class);
        assetManager.load("BotonPausa.png", Texture.class);
        assetManager.load("BotonVida.png", Texture.class);
        assetManager.load("BotonResortera.png", Texture.class);
        assetManager.load("PResortera1.png", Texture.class);
        assetManager.load("PResortera2.png", Texture.class);
        assetManager.load("PResortera3.png", Texture.class);
        assetManager.load("nivel.png", Texture.class);
        assetManager.load("PlataformaInclinada.png", Texture.class);
        assetManager.load("menuPausa.png", Texture.class);
        assetManager.load("menuPausaQuit.png", Texture.class);
        assetManager.load("opciones.png", Texture.class);
        assetManager.load("Save6.png", Texture.class);
        assetManager.load("BotonHabilidadDesactivado.png", Texture.class);
        assetManager.load("BotonHabPogo.png", Texture.class);
        assetManager.load("PPogo1.png", Texture.class);
        assetManager.load("PPogo2.png", Texture.class);
        assetManager.load("gameOver.png", Texture.class);
        assetManager.load("BotonNivelVida.png", Texture.class);
        assetManager.load("PDaño.png", Texture.class);
        assetManager.load("MalteadaMundo.png", Texture.class);
        assetManager.load("FondoCielo.png", Texture.class);
        assetManager.load("cocina.png", Texture.class);
        assetManager.load("coco.png", Texture.class);
        assetManager.load("jardin.png", Texture.class);
        assetManager.load("ropero.png", Texture.class);
        assetManager.load("sotano.png", Texture.class);
        assetManager.load("PLanzaPapa1.png", Texture.class);
        assetManager.load("PLanzaPapa2.png", Texture.class);
        assetManager.load("BotonHabLanzaPapa.png", Texture.class);
        assetManager.load("BotonMunicion.png", Texture.class);
        assetManager.load("ambiente.mp3", Music.class);
        assetManager.load("risa.mp3", Music.class);
        assetManager.finishLoading();
        neutral = assetManager.get("PNeutral.png");
        salto1 = assetManager.get("PSalto1.png");
        salto2 = assetManager.get("PSalto2.png");
        correr1 = assetManager.get("PCorrer1.png");
        correr2 = assetManager.get("PCorrer2.png");
        botonSalto = assetManager.get("BotonSalto.png");
        JFondo = assetManager.get("JoystickBoton.png");
        texPausa = assetManager.get("BotonPausa.png");
        habilidad = assetManager.get("BotonResortera.png");
        botonVida  =assetManager.get("BotonVida.png");
        resortera1 =  assetManager.get("PResortera1.png");
        resortera2 =  assetManager.get("PResortera2.png");
        resortera3 =  assetManager.get("PResortera3.png");
        transicionNeutral = assetManager.get("nivel.png");
        transicionCocina = assetManager.get("cocina.png");
        transicionCoco = assetManager.get("coco.png");
        transicionJardin = assetManager.get("jardin.png");
        transicionArmario = assetManager.get("ropero.png");
        transicionSotano = assetManager.get("sotano.png");
        plataforma = assetManager.get("PlataformaInclinada.png");
        pausaTex = assetManager.get("menuPausa.png");
        opciones = assetManager.get("opciones.png");
        quitTex = assetManager.get("menuPausaQuit.png");
        save = assetManager.get("Save6.png");
        habilidadDes = assetManager.get("BotonHabilidadDesactivado.png");
        habilidadPogo = assetManager.get("BotonHabPogo.png");
        pPogo1 = assetManager.get("PPogo1.png");
        pPogo2 = assetManager.get("PPogo2.png");
        gameOver = assetManager.get("gameOver.png");
        nivelVida = assetManager.get("BotonNivelVida.png");
        ambiente = assetManager.get("ambiente.mp3");
        gameover = assetManager.get("risa.mp3");
        dano = assetManager.get("PDaño.png");
        malteada = assetManager.get("MalteadaMundo.png");
        fondoCielo = new Sprite((Texture)assetManager.get("FondoCielo.png"));
        fondoCielo.setPosition(0,0);
        habilidadLanzaPapas = assetManager.get("BotonHabLanzaPapa.png");
        lanzapapas1 = assetManager.get("PLanzaPapa1.png");
        lanzapapas2 = assetManager.get("PLanzaPapa2.png");
        municion = assetManager.get("BotonMunicion.png");
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
                case VACIA:
                    botonHabilidad.setTexture(habilidadDes);
                    break;
            }

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
                        if(tempMapa ==2){
                            transicionNivel.setTexture(transicionSotano);
                            velocidadTransicion = TRANSICIONNIVEL;
                        }
                        break;
                    default:
                        transicionNivel.setTexture(transicionNeutral);
                        velocidadTransicion = TRANSICIONNEUTRAL;
                }
                mapa = mapas.get(mapaActual);
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
            batch.setProjectionMatrix(camara.combined);
            batch.begin();
            fondoCielo.draw(batch);
            batch.end();
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
                if(proyectiles.get(i).out())
                    proyectiles.remove(i);
                else{
                    proyectiles.get(i).draw(batch);
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
            if(abner.getLanzapapas()){
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

    private void habilidadSiguiente() {
        if(habilidadActual!= Habilidad.VACIA){
            switch (habilidadActual){
                case POGO:
                    if(abner.lanzapapas)
                        habilidadActual = Habilidad.LANZAPAPAS;
                    break;
                case LANZAPAPAS:
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
        neutral.dispose();
        salto1.dispose();
        salto2.dispose();
        correr1.dispose();
        correr2.dispose();
        for(Mapa mapa: mapas)
            mapa.dispose();
        batch.dispose();

    }

    public static void quitarEnemigo(Enemigo ene){
        if (enemigosPrueba.contains(ene,true)){
            if(ene.getClass().getSimpleName().equals("Hongo")){
                enemigosPrueba.removeIndex(enemigosPrueba.indexOf(ene,true)-1);
            }
            enemigosPrueba.removeIndex(enemigosPrueba.indexOf(ene,true));
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
        mapa = mapas.get(mapaActual);
        abner.setMapa(mapa);
        for(Mapa mapa: mapas)
            mapa.reiniciar(gameInfo);
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

    private enum Habilidad{
        VACIA,
        POGO,
        LANZAPAPAS
    }

}
