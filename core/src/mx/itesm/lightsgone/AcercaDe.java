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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.xml.soap.Text;


/**
 * Created by allanruiz on 08/09/16.
 */
public class AcercaDe implements Screen, InputProcessor {
    private Juego juego;
    private OrthographicCamera camara, camaraHUD;
    private Texture texFondoL, texFondoSL, regresar,hector,rafa,allan,erick,luis;
    private Viewport vista;
    private final int ANCHO_MUNDO = 1280, ALTO_MUNDO = 800;
    private Boton btnRegresar,btnAllan,btnErick,btnLuis,btnRafa,btnHector, btnFacebook;
    private AssetManager assetManager = new AssetManager();
    private Music luz;
    private Animation animation;
    private Sprite fondo;
    private SpriteBatch batch;
    private Estado estado;
    private float timer;

    public AcercaDe(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        //Cargando assets
        cargarTexturas();
        //Creando camara
        iniciarCamara();
        //Iniciar escena
        iniciarEscena();
    }

    private void iniciarEscena() {
        fondo = new Sprite(texFondoSL);
        btnRegresar = new Boton(regresar, 0,0,false);
        btnAllan = new Boton(639,241,170,150);
        btnErick = new Boton(604, 451,165,146);
        btnHector = new Boton(219,272,148,173);
        btnLuis = new Boton(364,420,174,147);
        btnRafa = new Boton(417,262,154,144);
        btnFacebook = new Boton(165, 622, 88,88);
        animation = new Animation(0.3f, new TextureRegion(texFondoSL), new TextureRegion(texFondoL));
        animation.setPlayMode(Animation.PlayMode.LOOP);
        estado = Estado.INICIO;
        timer = 0;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        luz.play();
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

    private void cargarTexturas() {
        assetManager.load("acercaDeL.jpg", Texture.class);
        assetManager.load("acercaDeSL.jpg", Texture.class);
        assetManager.load("back.png", Texture.class);
        assetManager.load("foco.mp3", Music.class);
        assetManager.load("erick.png", Texture.class);
        assetManager.load("hector.png", Texture.class);
        assetManager.load("luis.png", Texture.class);
        assetManager.load("oscar.png", Texture.class);
        assetManager.load("rafa.png",Texture.class);
        assetManager.finishLoading();
        texFondoSL = assetManager.get("acercaDeSL.jpg");
        texFondoL = assetManager.get("acercaDeL.jpg");
        luz = assetManager.get("foco.mp3");
        regresar = assetManager.get("back.png");
        erick = assetManager.get("erick.png");
        hector = assetManager.get("hector.png");
        luis = assetManager.get("luis.png");
        allan = assetManager.get("oscar.png");
        rafa = assetManager.get("rafa.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.app.log("Estado: ", estado+"");
        Gdx.app.log("Boton ", btnAllan.isPressed()+"");
        switch (estado){
            case INICIO:
                timer+= Gdx.graphics.getDeltaTime();
                fondo.setTexture(animation.getKeyFrame(timer).getTexture());
                if(timer>=3){
                    fondo.setTexture(texFondoL);
                    estado = Estado.PRINCIPAL;
                }
                break;
            case PRINCIPAL:
                fondo.setTexture(texFondoL);
                if(btnAllan.isPressed()){
                    estado = Estado.ALLAN;
                    btnAllan.setEstado(Boton.Estado.NOPRESIONADO);
                }
                else if(btnHector.isPressed()){
                    estado = Estado.HECTOR;
                    btnHector.setEstado(Boton.Estado.NOPRESIONADO);
                }
                else if(btnRafa.isPressed()){
                    estado = Estado.RAFA;
                    btnRafa.setEstado(Boton.Estado.NOPRESIONADO);
                }
                else if(btnLuis.isPressed()){
                    estado = Estado.LUIS;
                    btnLuis.setEstado(Boton.Estado.NOPRESIONADO);
                }
                else if(btnErick.isPressed()){
                    estado = Estado.ERICK;
                    btnErick.setEstado(Boton.Estado.NOPRESIONADO);
                }
                else if(btnFacebook.isPressed()){
                    btnFacebook.setEstado(Boton.Estado.NOPRESIONADO);
                    Gdx.net.openURI("https://www.facebook.com/lightsgone1/");
                }
                if(btnRegresar.isPressed()){
                    juego.setScreen(new MenuPrincipal(juego, true));
                }
                break;
            case ALLAN:
                fondo.setTexture(allan);
                if(btnRegresar.isPressed()){
                    estado = Estado.PRINCIPAL;
                }
                break;
            case HECTOR:
                fondo.setTexture(hector);
                if(btnRegresar.isPressed()){
                    estado = Estado.PRINCIPAL;
                }
                break;
            case RAFA:
                fondo.setTexture(rafa);
                if(btnRegresar.isPressed()){
                    estado = Estado.PRINCIPAL;
                }
                break;
            case LUIS:
                fondo.setTexture(luis);
                if(btnRegresar.isPressed()){
                    estado = Estado.PRINCIPAL;
                }
                break;
            case ERICK:
                fondo.setTexture(erick);
                if(btnRegresar.isPressed()){
                    estado = Estado.PRINCIPAL;
                }
                break;
        }

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        fondo.draw(batch);
        batch.end();

        if(estado!=Estado.INICIO){
            batch.setProjectionMatrix(camaraHUD.combined);
            batch.begin();
            btnRegresar.draw(batch);
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
        luz.dispose();
        texFondoL.dispose();
        texFondoSL.dispose();
        regresar.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            juego.setScreen(new MenuPrincipal(juego));
        }
        return true;
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
        if(estado != Estado.INICIO){
            if(btnRegresar.contiene(x,y)){
                btnRegresar.setEstado(Boton.Estado.PRESIONADO);
            }
        }
        if(estado == Estado.PRINCIPAL){
            if(btnAllan.contiene(x,y)){
                btnAllan.setEstado(Boton.Estado.PRESIONADO);
            }
            else if(btnHector.contiene(x,y)){
                btnHector.setEstado(Boton.Estado.PRESIONADO);
            }
            else if(btnRafa.contiene(x,y)){
                btnRafa.setEstado(Boton.Estado.PRESIONADO);
            }
            else if(btnLuis.contiene(x,y)){
                btnLuis.setEstado(Boton.Estado.PRESIONADO);
            }
            else if(btnErick.contiene(x,y)){
                btnErick.setEstado(Boton.Estado.PRESIONADO);
            }
            else if(btnFacebook.contiene(x,y)){
                btnFacebook.setEstado(Boton.Estado.PRESIONADO);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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

    private enum Estado{
        INICIO,
        PRINCIPAL,
        HECTOR,
        LUIS,
        RAFA,
        ERICK,
        ALLAN
    }
}
