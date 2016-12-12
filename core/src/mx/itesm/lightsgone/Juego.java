package mx.itesm.lightsgone;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by allanruiz on 05/09/16.
 */
public class Juego extends Game {
    public static Music audio;
    private String version;

    public Juego(String version) {
        this.version = version;
    }

    @Override
    public void create() {
        FileHandle fileHandle = new FileHandle(Gdx.files.getLocalStoragePath());
        boolean found = false;
        for(FileHandle file:fileHandle.list()){
            if(file.name().equals("LightsGoneSettings.txt")){
                found = true;
                break;
            }
        }
        if(!found){
            FileHandle file = Gdx.files.local("LightsGoneSettings.txt");
            file.writeString("3", false);
        }
        else{
            FileHandle file = Gdx.files.local("LightsGoneSettings.txt");
            int ejecuciones = Integer.parseInt(file.readString());
            if(ejecuciones>=1){
                file.writeString((ejecuciones-1)+"", false);
            }
        }
        setScreen(new Splash(this, version));
    }

    public String getVersion(){
        return version;
    }
}
