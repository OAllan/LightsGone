package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by allanruiz on 22/11/16.
 */

public class Splash implements Screen{
    private final Juego juego;
    private OrthographicCamera camara;
    private Viewport vista;
    private Sprite tec, lights;
    private Texture lg;
    private SpriteBatch batch;
    private AssetManager assetManager = new AssetManager();
    private float timer, timerC;
    private Music lampara;
    private Estado estadoSplash;

    public Splash(Juego juego){
        this.juego = juego;
    }

    @Override
    public void show() {
        cargarTexturas();
        iniciarCamara();
        estadoSplash = Estado.TEC;
    }

    private void iniciarCamara() {
        camara = new OrthographicCamera(LightsGone.ANCHO_MUNDO, LightsGone.ALTO_MUNDO);
        camara.position.set(LightsGone.ANCHO_MUNDO / 2, LightsGone.ALTO_MUNDO / 2, 0);
        camara.update();
        vista = new StretchViewport(LightsGone.ANCHO_MUNDO, LightsGone.ALTO_MUNDO, camara);
        batch = new SpriteBatch();
    }

    private void cargarTexturas() {
        assetManager.load("splash 1.png", Texture.class);
        assetManager.load("nivel.png", Texture.class);
        assetManager.load("SplashLG.png",Texture.class);
        assetManager.load("lamparita.mp3", Music.class);
        assetManager.finishLoading();
        tec = new Sprite(assetManager.get("splash 1.png", Texture.class));
        lights = new Sprite(assetManager.get("nivel.png", Texture.class));
        lights.setSize(LightsGone.ANCHO_MUNDO, LightsGone.ALTO_MUNDO);
        lg = assetManager.get("SplashLG.png");
        lampara = assetManager.get("lamparita.mp3");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        switch (estadoSplash){
            case TEC:
                timer+=Gdx.graphics.getDeltaTime();
                if(timer>=2){
                    estadoSplash = Estado.APAGADO;
                    timer = 0;
                }
                tec.draw(batch);
                break;
            case APAGADO:
                timer+=Gdx.graphics.getDeltaTime();
                if(timer>=1){
                    estadoSplash = Estado.LIGHTSGONE;
                    timer =0;
                    lampara.play();
                }
                lights.draw(batch);
                break;
            case LIGHTSGONE:
                lights.setTexture(lg);
                timer+=Gdx.graphics.getDeltaTime();
                if(timer>=2.5){
                    timer=0;
                    juego.setScreen(new MenuPrincipal(juego));
                    assetManager.dispose();
                }
                lights.draw(batch);
                break;
        }
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

    }

    @Override
    public void dispose() {

    }

    private enum Estado{
        TEC,
        APAGADO,
        LIGHTSGONE
    }
}
