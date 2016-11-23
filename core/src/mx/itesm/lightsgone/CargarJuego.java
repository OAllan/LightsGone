package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by allanruiz on 08/09/16.
 */
public class CargarJuego implements Screen, InputProcessor {
    private final Juego juego;
    private OrthographicCamera camara;
    private Viewport vista;
    private AssetManager assetManager = new AssetManager();
    private final int ANCHO_MUNDO = 1280, ALTO_MUNDO = 800;
    private Texture texFondo, regresar, load, transicion;
    private Texto nombre, fecha;
    private String nombre1, nombre2, fecha1, fecha2;
    private InfoJuego gameInfo;
    private Boton btnRegresar, juego1, juego2;
    private SpriteBatch batch;
    private Sprite fondo;
    private int numeroJuegos = juegos();
    private boolean transicionB, juegoUno;
    private float timer;

    public CargarJuego(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        //Cargando assets
        cargarTexturas();
        //Creando escena
        numeroJuegos = juegos();
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        //Creando camara
        camara = new OrthographicCamera(ANCHO_MUNDO, ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2, ALTO_MUNDO / 2, 0);
        camara.update();
        vista = new StretchViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        //Agregando fondo
        fondo = new Sprite(texFondo);
        btnRegresar = new Boton(regresar,0,0, false);
        juego1 = new Boton(load,693,455,false);
        juego2 = new Boton(load, 693, 216, false);
        nombre = new Texto("font.fnt", 0,0);
        fecha = new Texto("font1.fnt", 0,0);
        if(numeroJuegos==1||numeroJuegos==3){
            gameInfo = new InfoJuego("Juego1.txt");
            nombre1 = getNombre(gameInfo.getMapa());
            fecha1 = gameInfo.getFecha();
        }
        else{
            nombre1 = "Empty";
        }
        if(numeroJuegos==2||numeroJuegos ==3){
            gameInfo = new InfoJuego("Juego2.txt");
            nombre2 = getNombre(gameInfo.getMapa());
            fecha2 = gameInfo.getFecha();
        }
        else{
            nombre2 = "Empty";
        }

    }

    private void cargarTexturas() {
        assetManager.load("cargarMenu.png", Texture.class);
        assetManager.load("Sonido2.wav", Music.class);
        assetManager.load("back.png", Texture.class);
        assetManager.load("boton load.png", Texture.class);
        assetManager.load("transicionMenuLoad.png", Texture.class);
        assetManager.finishLoading();
        texFondo = assetManager.get("cargarMenu.png");
        //audio = assetManager.get("Sonido2.wav");
        regresar = assetManager.get("back.png");
        load = assetManager.get("boton load.png");
        transicion = assetManager.get("transicionMenuLoad.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camara.combined);

        if(transicionB){
            timer += Gdx.graphics.getDeltaTime();
            batch.begin();
            fondo.draw(batch);
            batch.end();
        }
        else {
            batch.begin();
            fondo.draw(batch);
            btnRegresar.draw(batch);
            juego1.draw(batch);
            juego2.draw(batch);
            if(numeroJuegos==1||numeroJuegos==3){
                nombre.setPosition(693+load.getWidth()/2,560);
                fecha.setPosition(693+load.getWidth()/2,499);
                fecha.mostrarMensaje(batch, fecha1);
            }
            else {
                nombre.setPosition(693+load.getWidth()/2,539);
            }
            nombre.mostrarMensaje(batch, nombre1);
            if(numeroJuegos==2||numeroJuegos==3){
                nombre.setPosition(693+load.getWidth()/2,321);
                fecha.setPosition(693+load.getWidth()/2,260);
                fecha.mostrarMensaje(batch, fecha2);
            }
            else {
                nombre.setPosition(693+load.getWidth()/2,300);
            }
            nombre.mostrarMensaje(batch, nombre2);
            batch.end();
        }

        if(timer>=1){
            if(juegoUno){
                juego.setScreen(new LightsGone(juego, "Juego1.txt"));
            }
            else{
                juego.setScreen(new LightsGone(juego, "Juego2.txt"));
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

    public static int juegos(){
        ArrayList<FileHandle> files = null;
        try {
            String fileHandle = Gdx.files.getLocalStoragePath();
            FileHandle directorio = new FileHandle(fileHandle);
            files = new ArrayList<FileHandle>(directorio.list().length);
            for(FileHandle file: directorio.list())
                files.add(file);

            Iterator<FileHandle> iterator = files.iterator();
            while(iterator.hasNext()){
                FileHandle file = iterator.next();
                String extension = file.extension();
                if(!"txt".equalsIgnoreCase(extension))
                    iterator.remove();
            }

        }catch (Exception e){

        }
        if(files != null){
            if(files.size()==0)
                return 0;
            if(files.size()==1)
                return Integer.parseInt(files.get(0).name().charAt(5) + "");
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


    }

    private void borrarJuegos(){
        try {
            ArrayList<FileHandle> files;
            String fileHandle = Gdx.files.getLocalStoragePath();
            FileHandle directorio = new FileHandle(fileHandle);
            files = new ArrayList<FileHandle>(directorio.list().length);
            for(FileHandle file: directorio.list()) {
                if(file.name().equalsIgnoreCase("Juego1.txt")||file.name().equalsIgnoreCase("Juego2.txt")){
                    file.delete();
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            juego.setScreen(new MenuPrincipal(juego, false));
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
        camara.unproject(v);
        float x = v.x;
        float y = v.y;
        if(btnRegresar.contiene(x,y)){
            juego.setScreen(new MenuPrincipal(juego, false));
        }
        if(juego1.contiene(x,y) && (numeroJuegos==1||numeroJuegos==3)){
            Juego.audio.stop();
            Juego.audio.dispose();
            fondo.setTexture(transicion);
            juegoUno = true;
            transicionB = true;
        }
        if(juego2.contiene(x,y) && (numeroJuegos==2||numeroJuegos==3)){
            Juego.audio.stop();
            Juego.audio.dispose();
            fondo.setTexture(transicion);
            juegoUno = false;
            transicionB = true;
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

    public String getNombre(int i) {
        switch (i){
            case 0:case 1:case 2:
                return "Living Room";
            case 3:case 4:case 5:
                return "The Kitchen";
            case 6:case 7:case 8:
                return "The Garden";
            case 9:case 10:case 11:case 12:
                return "The Wardrobe";
            case 13:case 14:case 15:
                return "The Basement";
            default:
                return "El Coco";
        }
    }
}
