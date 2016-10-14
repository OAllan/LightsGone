package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Nivel0 implements Screen, InputProcessor{

    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private final Juego juego;
    private AssetManager assetManager = new AssetManager();
    private Texture fondoTex, neutral, salto1, salto2, correr1, correr2, botonSalto, JLeft, JRight, JFondo, botonVida, habilidad, texPausa, resortera1, resortera2, resortera3, pResortera;
    private Fondo fondo;
    private Abner abner;
    private Enemigo.Brocoli brocoli;
    private Enemigo.Sopa sopa;
    private Enemigo.Tostadora tosta;
    private Enemigo.Mosca mosca;
    private Enemigo.Mosca mosca1;
    private Enemigo.Mosca mosca2;
    private Enemigo.Fuego fuego;
    private Texto vida;
    private Pad pad;
    private Sprite imgVida;
    private boolean right;
    private Boton botonSaltar, botonHabilidad, pausa;
    private float mov = 1f;
    private Array<Proyectil> proyectiles;
    private TiledMapRenderer renderer;
    private TiledMap mapa;
    private TiledMapTileLayer encima;
    private TiledMapTileLayer malteada;

    public Nivel0(Juego juego) {
        this.juego = juego;
        right = true;
    }

    @Override
    public void show() {
        cargarTexturas();
        iniciarCamara();
        crearEscena();
        Gdx.input.setInputProcessor(this);
    }

    private void iniciarCamara() {
        camara = new OrthographicCamera(1280,800);
        camara.position.set(640, 400, 0);
        camara.update();
        vista = new StretchViewport(1280, 800, camara);

        camaraHUD = new OrthographicCamera(1280,800);
        camaraHUD.position.set(640,400,0);
        camaraHUD.update();

    }

    private void crearEscena() {
        batch = new SpriteBatch();
        fondo = new Fondo(fondoTex);
        botonSaltar = new Boton(botonSalto, 1000,10, false);
        pad = new Pad(JFondo, JLeft, JRight);
        botonHabilidad = new Boton(habilidad, 995-habilidad.getWidth(), 10, false);
        pausa = new Boton(texPausa, 1280- texPausa.getWidth(), 800 - texPausa.getHeight(), false);
        imgVida = new Sprite(botonVida);
        imgVida.setPosition(0,780-imgVida.getHeight());
        vida = new Texto("tipo.fnt", imgVida.getWidth(),690);

        renderer = new OrthogonalTiledMapRenderer(mapa, batch);
        renderer.setView(camara);
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3, pResortera,camara, mapa);
        sopa=new Enemigo.Sopa(900,120,abner);
        brocoli=new Enemigo.Brocoli(900,100,abner);
        tosta=new Enemigo.Tostadora(1400,120,abner);
        mosca=new Enemigo.Mosca(1400,150,abner);
        mosca1=new Enemigo.Mosca(1500,150,abner);
        mosca2=new Enemigo.Mosca(1600,150,abner);
        fuego=new Enemigo.Fuego(1500,150,abner);
        encima = (TiledMapTileLayer)mapa.getLayers().get("CapaEncima");
        malteada = (TiledMapTileLayer)mapa.getLayers().get("Malteada");
    }

    private void cargarTexturas() {
        assetManager.load("PCorrer1.png",Texture.class);
        assetManager.load("PCorrer2.png", Texture.class );
        assetManager.load("PNeutral.png", Texture.class);
        assetManager.load("PSalto1.png", Texture.class);
        assetManager.load("PSalto2.png", Texture.class);
        assetManager.load("FondoPrueba.png",Texture.class);
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
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("CuartoAbner.tmx", TiledMap.class);
        assetManager.finishLoading();
        neutral = assetManager.get("PNeutral.png");
        salto1 = assetManager.get("PSalto1.png");
        salto2 = assetManager.get("PSalto2.png");
        correr1 = assetManager.get("PCorrer1.png");
        correr2 = assetManager.get("PCorrer2.png");
        fondoTex = assetManager.get("FondoPrueba.png");
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
        mapa = assetManager.get("CuartoAbner.tmx");


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);





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
            mapa.getLayers().remove(malteada);
        }


        renderer.setView(camara);
        renderer.render();
        proyectiles = abner.getProyectiles();
        batch.setProjectionMatrix(camara.combined);
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
        renderer.setView(camara);

        if(abner.getY()>=480)
            renderer.renderTileLayer(encima);
        batch.end();

        batch.begin();
        //brocoli.draw(batch);
        //sopa.draw(batch);
        //tosta.draw(batch);
        //mosca.draw(batch);
        //mosca1.draw(batch);
        //mosca2.draw(batch);
        //fuego.draw(batch);
        batch.end();

        batch.setProjectionMatrix(camaraHUD.combined);
        batch.begin();
        botonSaltar.draw(batch);
        pad.draw(batch);
        botonHabilidad.draw(batch);
        pausa.draw(batch);
        imgVida.draw(batch);
        vida.mostrarMensaje(batch, "" + abner.getcantVida());
        batch.end();

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
        fondoTex.dispose();
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
        if(pad.getLeft().contiene(x,y))
            pad.getLeft().setEstado(Boton.Estado.PRESIONADO);
        if(pad.getRight().contiene(x,y))
            pad.getRight().setEstado(Boton.Estado.PRESIONADO);
        if(botonSaltar.contiene(x,y)&(!abner.isJumping())&(!abner.isAttacking()))
            botonSaltar.setEstado(Boton.Estado.PRESIONADO);
        if(botonHabilidad.contiene(x,y)&(!abner.isAttacking()))
            botonHabilidad.setEstado(Boton.Estado.PRESIONADO);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        camaraHUD.unproject(v);
        float x = v.x;
        float y = v.y;
        if(pad.getLeft().contiene(x,y)) {
            pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
            abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
        }
        if(pad.getRight().contiene(x,y)) {
            pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
            abner.setEstadoHorizontal(Abner.Horizontal.DESACTIVADO);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        camaraHUD.unproject(v);
        float x = v.x;
        float y = v.y;
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
}
