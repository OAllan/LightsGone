package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    private OrthographicCamera camara;
    private OrthographicCamera camaraHUD;
    private Viewport vista;
    private SpriteBatch batch;
    private final Juego juego;
    private AssetManager assetManager = new AssetManager();
    private Texture fondoTex, neutral, salto1, salto2, correr1, correr2, botonSalto, JLeft, JRight, JFondo, botonVida, habilidad, texPausa, resortera1, resortera2, resortera3, pResortera;
    private Fondo fondo;
    private Abner abner;
    private Texto vida;
    private int cantVida;
    private Pad pad;
    private Sprite imgVida;
    private boolean right;
    private Boton botonSaltar, botonHabilidad, pausa;
    private float mov = 1f;
    private Array<Proyectil> proyectiles;
    private Enemigo.Brocoli brocoli;
    private Enemigo sopa;
    private Array<Enemigo> enemigos;

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
        cantVida = 99;
        abner = new Abner(neutral, correr1, correr2, salto1, salto2, resortera1, resortera2, resortera3, pResortera,camara);
        brocoli = new Enemigo.Brocoli(1300, 100, abner);
        sopa = new Enemigo.Sopa(400, 100, abner);
        enemigos = new Array<Enemigo>(30);
        enemigos.add(brocoli);
        enemigos.add(sopa);

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
        assetManager.load("MunicionResortera.png",Texture.class);
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

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(pad.getLeft().isPressed()&botonHabilidad.isPressed()&(!abner.isJumping())&!abner.isAttacking()){
            right = false;
            abner.setEstado(Abner.Estado.ATAQUECAMINANDO);
        }
        if(pad.getRight().isPressed()&botonHabilidad.isPressed()&(!abner.isJumping())&!abner.isAttacking()){
            right = true;
            abner.setEstado(Abner.Estado.ATAQUECAMINANDO);
        }
        else if(pad.getLeft().isPressed()&botonHabilidad.isPressed()&abner.isJumping()&!abner.isAttacking()){
            right = false;
            abner.setEstado(Abner.Estado.ATAQUESALTANDOAVANCE);
        }
        else if(pad.getRight().isPressed()&botonHabilidad.isPressed()&abner.isJumping()&!abner.isAttacking()){
            right = true;
            abner.setEstado(Abner.Estado.ATAQUESALTANDOAVANCE);
        }
        if(pad.getLeft().isPressed()&botonSaltar.isPressed()&(!abner.isJumping())&&!abner.isAttacking()){
            right = false;
            abner.setEstado(Abner.Estado.SALTANDOAVANCE);
            abner.setSalto(Abner.Salto.SUBIENDO);
        }
        else if(pad.getRight().isPressed()&botonSaltar.isPressed()&(!abner.isJumping())&&!abner.isAttacking()){
            right = true;
            abner.setEstado(Abner.Estado.SALTANDOAVANCE);
            abner.setSalto(Abner.Salto.SUBIENDO);
        }
        else if(pad.getRight().isPressed()&&(!abner.isJumping())&&!abner.isAttacking()) {
            right = true;
            abner.setEstado(Abner.Estado.CAMINANDO);
        }
        else if(pad.getLeft().isPressed()&&(!abner.isJumping())&&!abner.isAttacking()) {
            right = false;
            abner.setEstado(Abner.Estado.CAMINANDO);
        }
        else if(botonHabilidad.isPressed()&abner.isJumping()&!abner.isAttacking()){
            abner.setEstado(Abner.Estado.ATAQUESALTANDO);
        }
        else if(botonHabilidad.isPressed()&(!abner.isJumping())&&!abner.isAttacking()) {
            abner.setEstado(Abner.Estado.ATAQUE);
        }
        else if(botonSaltar.isPressed()&(!abner.isJumping())){
            abner.setEstado(Abner.Estado.SALTANDO);
            abner.setSalto(Abner.Salto.SUBIENDO);
        }
        else if(!abner.isJumping()&& !abner.isAttacking())
            abner.setEstado(Abner.Estado.NEUTRAL);


        proyectiles = abner.getProyectiles();
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        fondo.draw(batch);
        for(int i=0;i<enemigos.size;i++) {
            if (enemigos.get(i).muerte())
                enemigos.removeIndex(i);
            else
                enemigos.get(i).draw(batch);
        }

        abner.draw(batch, right);

        loop: for (int i=0;i<proyectiles.size;i++) {
            for (Enemigo e: enemigos)
                if(e.colisiona(proyectiles.get(i))){
                    e.setEstado(Enemigo.Estado.DANO);
                    proyectiles.removeIndex(i);
                    continue loop;
                }

            if(proyectiles.get(i).out())
                proyectiles.removeIndex(i);
            else{
                proyectiles.get(i).draw(batch);
                proyectiles.get(i).update();
            }
        }
        batch.end();

        batch.setProjectionMatrix(camaraHUD.combined);
        batch.begin();
        botonSaltar.draw(batch);
        pad.draw(batch);
        botonHabilidad.draw(batch);
        pausa.draw(batch);
        imgVida.draw(batch);
        vida.mostrarMensaje(batch, "" + cantVida);
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
        if(botonSaltar.contiene(x,y)&(!abner.isJumping()))
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
        if(pad.getLeft().contiene(x,y))
            pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
        if(pad.getRight().contiene(x,y))
            pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
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
        else
            pad.getLeft().setEstado(Boton.Estado.NOPRESIONADO);
        if(pad.getRight().contiene(x,y))
            pad.getRight().setEstado(Boton.Estado.PRESIONADO);
        else
            pad.getRight().setEstado(Boton.Estado.NOPRESIONADO);
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
