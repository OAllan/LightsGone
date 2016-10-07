package mx.itesm.lightsgone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

/**
 * Created by allanruiz on 05/09/16. (2)
 */
public class Juego extends Game {
    @Override
    public void create() {
        setScreen(new MenuPrincipal(this));
    }
}
