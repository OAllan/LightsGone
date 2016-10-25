package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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
public class Nivel0 implements Screen, InputProcessor{

    public static final int ANCHO_MUNDO = 1280;
    public static final int ALTO_MUNDO = 800;
    public static final int YBOTON = 30;
    public static final float YCOCINA3 = 361.0f;
    public static final int YCOCINA2 = 1950;
    private final int LATAX = 4871;
    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private Juego juego;
    private AssetManager assetManager = new AssetManager();
    private Texture gameOver,habilidadDes, habilidadPogo,save,pausaTex,quitTex, opciones, neutral, salto1, salto2, correr1, correr2, botonSalto, JLeft, JRight, JFondo, botonVida, habilidad, texPausa, resortera1, resortera2, resortera3, pResortera, plataforma;
    private Sprite transicionNivel, pausaActual;
    private Abner abner;
    private Texto vida;
    private Pad pad;
    private Sprite imgVida, menuGameOver;
    private boolean right, saveB;
    private Boton botonTry, botonMain,botonSaltar, botonArma, pausa, botonResume, botonOpciones,botonQuit, botonYes,botonNo, botonSave, botonHabilidad;
    private float alpha = 0;
    private Array<Mapa> mapas;
    private Mapa mapa;
    static int mapaActual;
    private ArrayList<Proyectil> proyectiles;
    private Transicion transicion;
    private Estado estado;
    private EstadoPausa estadoPausa;
    private GameInfo gameInfo;
    private final float YBAJA = 135f, YMEDIA = 800f, YALTA =1100f;
    private Texture pPogo1, pPogo2;
    private float alphaGame;
    Array<Enemigo> enemigos = new Array<Enemigo>(3);
    Array<Enemigo> enemigos1 = new Array<Enemigo>(3);

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
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3, pResortera,pPogo1,pPogo2,camara, mapa, gameInfo);
        boolean[] cuartoAbnerB = {true, false}, pasilloB = {true, false}, salaB={true, true, false, false}, cocina1B = {true, true},
                cocina2B = {false,false}, cocina3B = {true};
        int[] cuartoAbner = {0,1}, pasillo = {0,2}, sala = {1,0,0,3}, cocina1 = {2,4}, cocina2 = {3,5}, cocina3 = {4};
        mapas.add(new Mapa("CuartoAbner.tmx", batch, camara, cuartoAbner, cuartoAbnerB, YBAJA, YBAJA));
        mapas.add(new Mapa("Pasillo.tmx", batch, camara, pasillo, pasilloB, YBAJA, YBAJA));
        mapas.add(new Mapa("Sala.tmx", batch, camara, sala, salaB, YALTA, YBAJA, YALTA, YMEDIA));
        mapas.add(new Mapa("Cocina1.tmx", batch, camara, cocina1, cocina1B, YBAJA, YALTA));
        mapas.add(new Mapa("Cocina2.tmx", batch, camara, cocina2, cocina2B, YBAJA, YCOCINA2));
        mapas.add(new Mapa("Cocina3.tmx", batch, camara, cocina3, cocina3B, YCOCINA3));
        Sprite sprite = new Sprite(plataforma);
        sprite.setRotation(12);
        sprite.setPosition(ANCHO_MUNDO + 470, ALTO_MUNDO * 3 - 850);
        mapas.get(4).setPlataformasInclinada(sprite);
        mapaActual = gameInfo.getMapa();
        Array<Enemigo> enemigos = new Array<Enemigo>(3);
        mapa = mapas.get(mapaActual);
        enemigos.add(new Enemigo.Lata(LATAX,mapas.get(4).getHeight(),mapas.get(4)));
        enemigos.add(new Enemigo.Lata(LATAX, mapas.get(4).getHeight()+3300, mapas.get(4)));
        enemigos.add(new Enemigo.Lata(LATAX, mapas.get(4).getHeight()+6600, mapas.get(4)));
        mapas.get(4).setEnemigos(enemigos);
        transicion = Transicion.DISMINUYENDO;
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
        pad = new Pad(JFondo, JLeft, JRight);
        botonArma = new Boton(habilidad, ANCHO_MUNDO - habilidadDes.getWidth()- botonSalto.getWidth()-habilidad.getWidth()-30, YBOTON, false);
        pausa = new Boton(texPausa, ANCHO_MUNDO- texPausa.getWidth(), ALTO_MUNDO - texPausa.getHeight(), false);
        imgVida = new Sprite(botonVida);
        imgVida.setPosition(0,780-imgVida.getHeight());
        vida = new Texto("tipo.fnt", imgVida.getWidth(),690);
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3, pResortera,pPogo1,pPogo2,camara, mapa, gameInfo);
        //Moscas
        enemigos.add(new Enemigo.Mosca(2250,293,abner,mapa));
        enemigos.add(new Enemigo.Mosca(2295,360,abner,mapa));
        enemigos.add(new Enemigo.Mosca(2340,360,abner,mapa));

        enemigos.add(new Enemigo.Mosca(2475,2340,abner,mapa));
        enemigos.add(new Enemigo.Mosca(2565,2340,abner,mapa));
        enemigos.add(new Enemigo.Mosca(2655,2340,abner,mapa));

        enemigos.add(new Enemigo.Mosca(3465,2340,abner,mapa));
        enemigos.add(new Enemigo.Mosca(3555,2340,abner,mapa));
        enemigos.add(new Enemigo.Mosca(3645,2340,abner,mapa));

        //Flamas
        enemigos.add(new Enemigo.Fuego(7785,990,abner,mapa));
        enemigos.add(new Enemigo.Fuego(8415,990,abner,mapa));
        enemigos.add(new Enemigo.Fuego(8775,990,abner,mapa));
        enemigos.add(new Enemigo.Fuego(9135,990,abner,mapa));
        enemigos.add(new Enemigo.Fuego(9225,990,abner,mapa));
        enemigos.add(new Enemigo.Fuego(9315,990,abner,mapa));
        enemigos.add(new Enemigo.Fuego(9405,990,abner,mapa));

        //Panes tostadores
        enemigos.add(new Enemigo.PanTostadora(7635,2035,abner,mapa));
        enemigos.add(new Enemigo.PanTostadora(5873,1995,abner,mapa));
        enemigos.add(new Enemigo.PanTostadora(4703,1995,abner,mapa));

        //Tostadores
        enemigos.add(new Enemigo.Tostadora(7612,2025,abner,mapa));
        enemigos.add(new Enemigo.Tostadora(5850,1980,abner,mapa));
        enemigos.add(new Enemigo.Tostadora(4680,1980,abner,mapa));

        //Brocolis
        enemigos.add(new Enemigo.Brocoli(3285,500,abner,mapa));
        enemigos.add(new Enemigo.Brocoli(4410,90,abner,mapa));
        enemigos.add(new Enemigo.Brocoli(5085,90,abner,mapa));
        enemigos.add(new Enemigo.Brocoli(6435,900,abner,mapa));
        enemigos.add(new Enemigo.Brocoli(9900,1620,abner,mapa));
        enemigos.add(new Enemigo.Brocoli(8865,2025,abner,mapa));
        gameInfo.setAbner(abner);
        estado = Estado.JUGANDO;
        botonHabilidad = new Boton(abner.getPogo() ?habilidadPogo:habilidadDes, ANCHO_MUNDO-habilidadDes.getWidth()-10,YBOTON,false);
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
        alphaGame = 0;
        mapas.get(3).setEnemigos(enemigos);


    }

    private void cargarTexturas() {
        assetManager.load("PCorrer1.png",Texture.class);
        assetManager.load("PCorrer2.png", Texture.class );
        assetManager.load("PNeutral.png", Texture.class);
        assetManager.load("PSalto1.png", Texture.class);
        assetManager.load("PSalto2.png", Texture.class);
        assetManager.load("PSalto2.png", Texture.class);
        assetManager.load("BotonSalto.png", Texture.class);
        assetManager.load("JoystickLeft.png", Texture.class);
        assetManager.load("JoystickRight.png", Texture.class);
        assetManager.load("JoystickBoton.png", Texture.class);
        assetManager.load("BotonPausa.png", Texture.class);
        assetManager.load("BotonVida.png", Texture.class);
        assetManager.load("BotonResortera.png", Texture.class);
        assetManager.load("PResortera1.png", Texture.class);
        assetManager.load("PResortera2.png", Texture.class);
        assetManager.load("PResortera3.png", Texture.class);
        assetManager.load("MunicionResortera.png", Texture.class);
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
        assetManager.finishLoading();
        neutral = assetManager.get("PNeutral.png");
        salto1 = assetManager.get("PSalto1.png");
        salto2 = assetManager.get("PSalto2.png");
        correr1 = assetManager.get("PCorrer1.png");
        correr2 = assetManager.get("PCorrer2.png");
        botonSalto = assetManager.get("BotonSalto.png");
        JLeft = assetManager.get("JoystickLeft.png");
        JRight = assetManager.get("JoystickRight.png");
        JFondo = assetManager.get("JoystickBoton.png");
        texPausa = assetManager.get("BotonPausa.png");
        habilidad = assetManager.get("BotonResortera.png");
        botonVida  =assetManager.get("BotonVida.png");
        resortera1 =  assetManager.get("PResortera1.png");
        resortera2 =  assetManager.get("PResortera2.png");
        resortera3 =  assetManager.get("PResortera3.png");
        pResortera = assetManager.get("MunicionResortera.png");
        transicionNivel = new Sprite((Texture)assetManager.get("nivel.png"));
        transicionNivel.setSize(ANCHO_MUNDO,ALTO_MUNDO);
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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        botonHabilidad.setTexture(abner.getPogo() ? habilidadPogo : habilidadDes);
        estado = abner.isDead()?Estado.MUERTE:estado;

        if(estado != Estado.PAUSA&&estado!=Estado.MUERTE){
            if(botonSaltar.isPressed()) {
                abner.setEstadoVertical(Abner.Vertical.ACTIVADO);
                abner.setSalto(Abner.Salto.SUBIENDO);
            }

            if(botonArma.isPressed()){
                abner.setEstadoAtaque(Abner.Ataque.ACTIVADO);
            }

            if(botonHabilidad.isPressed()){
                abner.setEstadoVertical(Abner.Vertical.POGO);
                abner.setSalto(Abner.Salto.SUBIENDO);
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
                int tempMapa = mapaActual;
                transicion = Transicion.AUMENTANDO;
                mapaActual = cambio;
                mapa = mapas.get(mapaActual);
                abner.setMapa(mapa);
                abner.setInitialPosition(tempMapa);
                estado = Estado.CAMBIO;
                mapa.draw();
                enemigos = mapa.getEnemigos();
            }

            if(enemigos!=null) {
                for(Enemigo enemigo : enemigos)
                    if(abner.colisionEnemigo(enemigo))
                        break;
            }
            proyectiles = abner.getProyectiles();
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            batch.begin();
            abner.draw(batch, right);

            for (int i=0;i<proyectiles.size();i++) {
                if(proyectiles.get(i).out())
                    proyectiles.remove(i);
                else{
                    proyectiles.get(i).draw(batch);
                    proyectiles.get(i).update();
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
                        alpha-=0.01f;
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
            vida.mostrarMensaje(batch, "" + abner.getcantVida());
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
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            alphaGame+=0.01f;
            if(alphaGame>=1)
                alphaGame=1;
            menuGameOver.setAlpha(alphaGame);
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            menuGameOver.draw(batch);
            batch.end();
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
                    if(botonYes.contiene(x,y))
                        juego.setScreen(new MenuPrincipal(juego));
                    break;
                case OPCIONES:
                    break;
            }
        }

        if(estado == Estado.MUERTE){
            if(botonMain.contiene(x,y))
                juego.setScreen(new MenuPrincipal(juego));
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
        mapa = mapas.get(gameInfo.getMapa());
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

}
