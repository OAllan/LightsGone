package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.xml.soap.Text;


/**
 * Created by allanruiz on 08/09/16.
 */
public class AcercaDe implements Screen {
    private Juego juego;
    private OrthographicCamera camara;
    private Texture texFondoL, texFondoSL, regresar;
    private Viewport vista;
    private Stage escena;
    private final int ANCHO_MUNDO = 1280, ALTO_MUNDO = 800;
    private ImageButton btnRegresar;
    private AssetManager assetManager = new AssetManager();
    private Music luz;
    private Image fondo;

    public AcercaDe(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        //Cargando assets
        cargarTexturas();
        //Creando escena
        escena = new Stage();
        Gdx.input.setInputProcessor(escena);
        //Creando camara
        camara = new OrthographicCamera(ANCHO_MUNDO, ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2, ALTO_MUNDO / 2, 0);
        camara.update();
        vista = new StretchViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        //Agregando fondo
        fondo = new Image(texFondoSL);
        escena.addActor(fondo);
        luz.play();
        TextureRegionDrawable trBtnRegresar = new TextureRegionDrawable(new TextureRegion(regresar));
        btnRegresar = new ImageButton(trBtnRegresar);
        btnRegresar.setPosition(0, 0);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 0.2f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoSL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 0.5f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 0.8f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoSL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 1f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 1.2f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoSL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 1.5f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 1.9f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                fondo = new Image(texFondoSL);
                escena.clear();
                escena.addActor(fondo);
            }
        }, 2f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                luz.stop();
                fondo = new Image(texFondoL);
                escena.clear();
                escena.addActor(fondo);
                escena.addActor(btnRegresar);
            }
        }, 2.6f);

        btnRegresar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new MenuPrincipal(juego));
            }
        });

    }

    private void cargarTexturas() {
        assetManager.load("acercaDeL.jpg", Texture.class);
        assetManager.load("acercaDeSL.jpg", Texture.class);
        assetManager.load("back.png", Texture.class);
        assetManager.load("foco.mp3", Music.class);
        assetManager.finishLoading();
        texFondoSL = assetManager.get("acercaDeSL.jpg");
        texFondoL = assetManager.get("acercaDeL.jpg");
        luz = assetManager.get("foco.mp3");
        regresar = assetManager.get("back.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escena.setViewport(vista);
        escena.draw();
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
        luz.dispose();
        texFondoL.dispose();
        texFondoSL.dispose();
        regresar.dispose();
        escena.dispose();
    }
}
