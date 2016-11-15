package mx.itesm.lightsgone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;

/**
 * Created by allanruiz on 05/09/16.
 */
public class Juego extends Game {
    public static Music audio;
    @Override
    public void create() {
        setScreen(new MenuPrincipal(this));
    }
}
