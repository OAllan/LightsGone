package mx.itesm.lightsgone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;

/**
 * Created by allanruiz on 05/09/16.
 */
public class Juego extends Game {
    public static Music audio;
    @Override
    public void create() {
        Preferences preferences = Gdx.app.getPreferences("LightsGoneSettings");
        if(!preferences.contains("ejecuciones")){
            preferences.putInteger("ejecuciones", 3);
            Gdx.app.log("ejecuciones", preferences.getInteger("ejecuciones")+"");
        }
        else{
            if(preferences.getInteger("ejecuciones")>=1){
                preferences.putInteger("ejecuciones", preferences.getInteger("ejecuciones")-1);
            }
        }
        setScreen(new Splash(this));
    }
}
