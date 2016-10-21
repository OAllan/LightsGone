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

/**
 * Created by allanruiz on 19/09/16.
 */
public class Nivel0 implements Screen, InputProcessor{

    public static final int ANCHO_MUNDO = 1280;
    public static final int ALTO_MUNDO = 800;
    private final int LATAX = 4871;
    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private final Juego juego;
    private AssetManager assetManager = new AssetManager();
    private Texture pausaTex,quitTex, opciones, neutral, salto1, salto2, correr1, correr2, botonSalto, JLeft, JRight, JFondo, botonVida, habilidad, texPausa, resortera1, resortera2, resortera3, pResortera, plataforma;
    private Sprite transicionNivel, pausaActual;
    private Abner abner;
    private Texto vida;
    private Pad pad;
    private Sprite imgVida;
    private boolean right;
    private Boton botonSaltar, botonHabilidad, pausa, botonResume, botonOpciones,botonQuit, botonYes,botonNo;
    private float alpha = 0;
    private Array<Mapa> mapas;
    private Mapa mapa;
    private int mapaActual;
    private Array<Proyectil> proyectiles;
    private Transicion transicion;
    private Estado estado;
    private EstadoPausa estadoPausa;
    private final float YBAJA = 135f, YMEDIA = 800f, YALTA =1100f;



    public Nivel0(Juego juego) {
        this.juego = juego;
        right = true;
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
        boolean[] cuartoAbnerB = {true, false}, pasilloB = {true, false}, salaB={true, true, false, false}, cocina1B = {true, true},
                cocina2B = {false,false}, cocina3B = {true};
        int[] cuartoAbner = {0,1}, pasillo = {0,2}, sala = {1,0,0,3}, cocina1 = {2,4}, cocina2 = {3,5}, cocina3 = {4};
        mapas.add(new Mapa("CuartoAbner.tmx", batch, camara, cuartoAbner, cuartoAbnerB, YBAJA, YBAJA));
        mapas.add(new Mapa("Pasillo.tmx", batch, camara, pasillo, pasilloB, YBAJA, YBAJA));
        mapas.add(new Mapa("Sala.tmx", batch, camara, sala, salaB, YALTA, YBAJA, YALTA, YMEDIA));
        mapas.add(new Mapa("Cocina1.tmx", batch, camara, cocina1, cocina1B, YBAJA, YALTA));
        mapas.add(new Mapa("Cocina2.tmx", batch, camara, cocina2, cocina2B, YBAJA, YALTA));
        mapas.add(new Mapa("Cocina3.tmx", batch, camara, cocina3, cocina3B, YBAJA));
        Sprite sprite = new Sprite(plataforma);
        sprite.setRotation(12);
        sprite.setPosition(ANCHO_MUNDO + 470, ALTO_MUNDO * 3 - 850);
        mapas.get(4).setPlataformasInclinada(sprite);
        mapaActual = 0;
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
        botonSaltar = new Boton(botonSalto, 1000,10, false);
        pad = new Pad(JFondo, JLeft, JRight);
        botonHabilidad = new Boton(habilidad, 995-habilidad.getWidth(), 10, false);
        pausa = new Boton(texPausa, ANCHO_MUNDO- texPausa.getWidth(), ALTO_MUNDO - texPausa.getHeight(), false);
        imgVida = new Sprite(botonVida);
        imgVida.setPosition(0,780-imgVida.getHeight());
        vida = new Texto("tipo.fnt", imgVida.getWidth(),690);
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3, pResortera,camara, mapa);
        estado = Estado.JUGANDO;
        estadoPausa = EstadoPausa.PRINCIPAL;
        pausaActual = new Sprite(pausaTex);
        botonResume = new Boton(502,405,295,75);
        botonOpciones= new Boton(502,247,295,75);
        botonQuit = new Boton(538,93,202,79);
        botonYes = new Boton(402,106,141,79);
        botonNo = new Boton(714, 106,141,79);


    }

    private void cargarTexturas() {
        assetManager.load("PCorrer1.png",Texture.class);
        assetManager.load("PCorrer2.png", Texture.class );
        assetManager.load("PNeutral.png", Texture.class);
        assetManager.load("PSalto1.png", Texture.class);
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

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if(estado != Estado.PAUSA){
            if(botonSaltar.isPressed()) {
                abner.setEstadoVertical(Abner.Vertical.ACTIVADO);
                abner.setSalto(Abner.Salto.SUBIENDO);
            }

            if(botonHabilidad.isPressed()){
                abner.setEstadoAtaque(Abner.Ataque.ACTIVADO);
            }

            if(pad.getRight().isPressed()) {
                right = true;
                abner.setEstadoHorizontal(Abner.Horizontal.ACTIVADO);
            }

            else if(pad.getLeft().isPressed()) {
                right = false;
                abner.setEstadoHorizontal(Abner.Horizontal.ACTIVADO);
            }

            if(abner.colisionMalteada()){
                mapa.remove("Malteada");
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
            }

            proyectiles = abner.getProyectiles();
            batch.setProjectionMatrix(camara.combined);
            mapa.draw();
            batch.begin();
            abner.draw(batch, right);

            for (int i=0;i<proyectiles.size;i++) {
                if(proyectiles.get(i).out())
                    proyectiles.removeIndex(i);
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
            batch.begin();
            botonSaltar.draw(batch);
            pad.draw(batch);
            botonHabilidad.draw(batch);
            pausa.draw(batch);
            imgVida.draw(batch);
            vida.mostrarMensaje(batch, "" + abner.getcantVida());
            transicionNivel.draw(batch);
            batch.end();
        }

        else if(estado == Estado.PAUSA){
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
            if (botonHabilidad.contiene(x, y) & (!abner.isAttacking()))
                botonHabilidad.setEstado(Boton.Estado.PRESIONADO);
            if(pausa.contiene(x,y))
                estado = Estado.PAUSA;
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

        return false;
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
        PAUSA
    }

    private enum Transicion{
        AUMENTANDO,
        DISMINUYENDO
    }

}
