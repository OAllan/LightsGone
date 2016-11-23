package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by allanruiz on 08/09/16.
 */
public class Instrucciones implements Screen {
    private final Juego juego;
    private Stage escena;
    private OrthographicCamera camara;
    private Viewport vista;
    private final int ANCHO_MUNDO = 1280, ALTO_MUNDO = 800;
    private AssetManager assetManager = new AssetManager();
    private Texture texFondo;
    private Texture regresar;
    private ImageButton btnRegresar;

    public Instrucciones(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        //Cargando assets
        cargarTexturas();
        //Creando escena
        escena = new Stage();
        Gdx.input.setInputProcessor(escena);
        Gdx.input.setCatchBackKey(true);
        //Creando camara
        camara = new OrthographicCamera(ANCHO_MUNDO, ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2, ALTO_MUNDO / 2, 0);
        camara.update();
        vista = new StretchViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        //Agregando fondo
        Image fondo = new Image(texFondo);
        escena.addActor(fondo);
        //Boton regresar
        TextureRegionDrawable trBtnRegresar = new TextureRegionDrawable(new TextureRegion(regresar));
        btnRegresar = new ImageButton(trBtnRegresar);
        btnRegresar.setPosition(0, 0);
        escena.addActor(btnRegresar);

        btnRegresar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new MenuPrincipal(juego, false));
            }
        });


    }

    private void cargarTexturas() {
        assetManager.load("instructionsFondo.png", Texture.class);
        assetManager.load("back.png", Texture.class);
        assetManager.finishLoading();
        texFondo = assetManager.get("instructionsFondo.png");
        regresar = assetManager.get("back.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escena.setViewport(vista);
        escena.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            juego.setScreen(new MenuPrincipal(juego, false));
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
        texFondo.dispose();
        regresar.dispose();
        escena.dispose();
    }
}
