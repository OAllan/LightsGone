package mx.itesm.lightsgone;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
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

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.Random;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;

public class MenuPrincipal implements Screen {

	private final boolean flag;
	private Juego juego;
	private AssetManager assetManager = new AssetManager();
	private Texture texFondo, acercaDe, acercaDeP, nuevoJuego, nuevoJuegoP, cargar, cargarP, instructions;
	private Stage escena;
	private OrthographicCamera camara;
	private Viewport vista;
	private final int ANCHO_MUNDO = 1280, ALTO_MUNDO = 800;
	private ImageButton btnNuevo, btnAcercaDe, btnCargar, btnInstructions;
	//private Music audio;
	private Music rugido;
	private Texture texTransicion;
	private GDXButtonDialog buttonDialog;
	private SpriteBatch batch;
	private Texto version;

	public MenuPrincipal(Juego juego, boolean flag) {
		this.juego = juego;
		this.flag = flag;
		batch = new SpriteBatch();
	}
	public MenuPrincipal(Juego juego){
		this.juego = juego;
		this.flag = true;
		batch = new SpriteBatch();
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
		version = new Texto("version.fnt", ANCHO_MUNDO-80,50);
		vista = new StretchViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
		//Agregando fondo
		Image fondo = new Image(texFondo);
		escena.addActor(fondo);
		final FileHandle preferences = Gdx.files.local("LightsGoneSettings.txt");
		if(Integer.parseInt(preferences.readString())==0){
			GDXDialogs dialogs = GDXDialogsSystem.install();
			buttonDialog = dialogs.newDialog(GDXButtonDialog.class);
			buttonDialog.setTitle("Rate this app");
			buttonDialog.setMessage("Do you enjoy playing Lights Gone? Please, take a minute to rate it. \nThank you for your support! :)");
			buttonDialog.addButton("Rate now");
			buttonDialog.addButton("Remind me later");
			buttonDialog.addButton("No, thanks");
			buttonDialog.setClickListener(new ButtonClickListener() {
				@Override
				public void click(int button) {
					if(button == 0){
						if(Application.ApplicationType.Android == Gdx.app.getType()){
							Gdx.net.openURI("https://play.google.com/store/apps/details?id=mx.itesm.lightsgone");
						}
						else{
							Gdx.net.openURI("https://itunes.apple.com/us/app/id1179503765");
						}
					}
					else if(button==1){
						preferences.writeString("15", false);
					}
					else {
						preferences.writeString("-1", false);
					}
				}
			});
			buttonDialog.build().show();
		}

		//Iniciando audio
		if(flag) {
			Juego.audio.play();
			Juego.audio.setVolume(0.5f);
			Juego.audio.setLooping(true);
		}
		//Agregando botones
		//Nuevo Juego
		final TextureRegionDrawable trBtnNuevo = new TextureRegionDrawable(new TextureRegion(nuevoJuego));
		btnNuevo = new ImageButton(trBtnNuevo);
		btnNuevo.setPosition(ANCHO_MUNDO/2+180, ALTO_MUNDO/2+105);
		escena.addActor(btnNuevo);
		//Acerca De
		TextureRegionDrawable trBtnAcercaDe = new TextureRegionDrawable(new TextureRegion(acercaDe));
		btnAcercaDe = new ImageButton(trBtnAcercaDe);
		btnAcercaDe.setPosition(ANCHO_MUNDO/4+70, ALTO_MUNDO/2+60);
		escena.addActor(btnAcercaDe);
		//Cargar juego
		TextureRegionDrawable trBtnCargar = new TextureRegionDrawable(new TextureRegion(cargar));
		btnCargar = new ImageButton(trBtnCargar);
		btnCargar.setPosition(ANCHO_MUNDO / 2 - 100, ALTO_MUNDO / 2 + 75);
		escena.addActor(btnCargar);
		//Instrucciones
		TextureRegionDrawable trBtnSettings = new TextureRegionDrawable(new TextureRegion(instructions));
		btnInstructions = new ImageButton(trBtnSettings);
		btnInstructions.setPosition(ANCHO_MUNDO / 2 - 220, ALTO_MUNDO / 2 - 190);
		escena.addActor(btnInstructions);

		//Agregar listeners a los botones
		btnNuevo.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(CargarJuego.juegos()==3){
					Random rnd = new Random();
					InfoJuego.borrarJuego("Juego"+ (rnd.nextInt(2)+1) +".txt");
				}
				Image transicion = new Image(texTransicion);
				escena.addActor(transicion);
				Juego.audio.pause();
				Juego.audio.dispose();
				rugido.play();
				Timer.schedule(new Timer.Task() {
					@Override
					public void run() {
						juego.setScreen(new LightsGone(juego));
					}
				}, 2f);
			}
		});
		btnAcercaDe.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Juego.audio.pause();
				Juego.audio.dispose();
				juego.setScreen(new AcercaDe(juego));
			}
		});
		btnCargar.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				juego.setScreen(new CargarJuego(juego));
			}
		});
		btnInstructions.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				juego.setScreen(new Instrucciones(juego));
			}
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		escena.setViewport(vista);
		escena.draw();
		if(Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
			Gdx.app.exit();
		}
		batch.setProjectionMatrix(camara.combined);
		batch.begin();
		version.mostrarMensaje(batch, "ver. "+ juego.getVersion());

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
	public void dispose () {
		texFondo.dispose();
		acercaDe.dispose();
		acercaDeP.dispose();
		nuevoJuego.dispose();
		nuevoJuegoP.dispose();
		cargar.dispose();
		cargarP.dispose();
		//audio.dispose();
		instructions.dispose();
		rugido.dispose();
		texTransicion.dispose();
		escena.dispose();
	}

	private void cargarTexturas(){
		assetManager.load("FondoMenu.png", Texture.class);
		assetManager.load("cargar.png", Texture.class);
		assetManager.load("cargarP.png", Texture.class);
		assetManager.load("acercaDe.png", Texture.class);
		assetManager.load("acercaDeP.png", Texture.class);
		assetManager.load("nuevo.png", Texture.class);
		assetManager.load("nuevoP.png", Texture.class);
		assetManager.load("Sonido1.wav", Music.class);
		assetManager.load("rugido.mp3", Music.class);
		assetManager.load("instructions.png", Texture.class);
		assetManager.load("transicion.png", Texture.class);
		assetManager.finishLoading();
		rugido = assetManager.get("rugido.mp3");
		texTransicion = assetManager.get("transicion.png");
		texFondo = assetManager.get("FondoMenu.png");
		acercaDe = assetManager.get("acercaDe.png");
		acercaDeP = assetManager.get("acercaDeP.png");
		nuevoJuego = assetManager.get("nuevo.png");
		nuevoJuegoP = assetManager.get("nuevoP.png");
		cargar = assetManager.get("cargar.png");
		cargarP = assetManager.get("cargarP.png");
		if(flag)
			Juego.audio = assetManager.get("Sonido1.wav");
		instructions = assetManager.get("instructions.png");
	}


}
