package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by allanruiz on 08/09/16.
 */
public class CargarJuego implements Screen {
    private final Juego juego;
    private OrthographicCamera camara;
    private Viewport vista;
    private Stage escena;
    private AssetManager assetManager = new AssetManager();
    private final int ANCHO_MUNDO = 1280, ALTO_MUNDO = 800;
    private Texture texFondo, regresar, juego1Tex, juego2Tex, empty;
    private Texto fecha;
    private GameInfo gameInfo;
    //private Music audio;
    private ImageButton btnRegresar, juego1, juego2;
    private SpriteBatch batch;

    public CargarJuego(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        cargarTexturas();
        //Cargando assets
        cargarTexturas();
        //Creando escena
        int numeroJuegos = juegos();
        batch = new SpriteBatch();
        escena = new Stage();
        fecha = new Texto("tipo.fnt",0,0);
        Gdx.input.setInputProcessor(escena);
        //Creando camara
        camara = new OrthographicCamera(ANCHO_MUNDO, ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2, ALTO_MUNDO / 2, 0);
        camara.update();
        vista = new StretchViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        //Agregando fondo
        Image fondo = new Image(texFondo);
        escena.addActor(fondo);
        /*audio.play();
        audio.setLooping(true);
        audio.setVolume(0.5f);*/

        TextureRegionDrawable trBtnRegresar = new TextureRegionDrawable(new TextureRegion(regresar));
        btnRegresar = new ImageButton(trBtnRegresar);
        btnRegresar.setPosition(0, 0);
        escena.addActor(btnRegresar);

        final TextureRegionDrawable trBtnJuego1 = new TextureRegionDrawable(new TextureRegion(numeroJuegos!=1?empty:juego1Tex));
        juego1 = new ImageButton(trBtnJuego1);
        juego1.setPosition(693, 455);

        final TextureRegionDrawable trBtnJuego2 = new TextureRegionDrawable(new TextureRegion(numeroJuegos!=2?empty:juego2Tex));
        juego2 = new ImageButton(trBtnJuego2);
        juego2.setPosition(693,216);



        btnRegresar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new MenuPrincipal(juego, false));
            }
        });

        juego1.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!trBtnJuego1.getRegion().getTexture().equals(empty)){
                    juego.setScreen(new Nivel0(juego,"Juego1.txt"));
                }
            }
        });

        juego2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!trBtnJuego2.getRegion().getTexture().equals(empty)){
                    juego.setScreen(new Nivel0(juego,"Juego2.txt"));
                }
            }
        });

    }

    private void cargarTexturas() {
        assetManager.load("cargarMenu.png", Texture.class);
        assetManager.load("Sonido2.wav", Music.class);
        assetManager.load("back.png", Texture.class);
        assetManager.finishLoading();
        texFondo = assetManager.get("cargarMenu.png");
        //audio = assetManager.get("Sonido2.wav");
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

    private int juegos(){
        ArrayList<File> files = null;
        try {
            File archivo = new File(".");
            File directorio = new File(archivo.getCanonicalPath());
            files = new ArrayList<File>(directorio.listFiles().length);
            for(File file: directorio.listFiles())
                files.add(file);
            Iterator<File> iterator = files.iterator();
            while(iterator.hasNext()){
                File file = iterator.next();
                String name = file.getName();
                if(name.length()>3)
                    if(!name.substring(name.length()-3, name.length()).equalsIgnoreCase("txt")){
                        iterator.remove();
                    }
            }

        }catch (Exception e){

        }
        if(files != null){
            if(files.size()==0)
                return 0;
            if(files.size()==1)
                return Integer.parseInt(files.get(0).getName().charAt(5) + "");
            else if(files.size()==2)
                return 3;
        }
        return 0;
    }

    @Override
    public void dispose() {
        texFondo.dispose();
        //audio.dispose();
        regresar.dispose();
        escena.dispose();

    }
}
