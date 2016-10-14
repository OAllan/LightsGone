package mx.itesm.lightsgone;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by allanruiz on 13/10/16.
 */
public class Mapa {
    private TiledMap mapa;
    private TiledMapRenderer renderer;
    private Array<TiledMapTileLayer> plataformaY, plataformaX,encima, items;
    private static AssetManager manager = new AssetManager();
    private OrthographicCamera camara;

    public Mapa(String mapa, SpriteBatch batch, OrthographicCamera camara){
        this.mapa = cargarMapa(mapa);
        this.camara = camara;
        renderer = new OrthogonalTiledMapRenderer(this.mapa, batch);
    }

    private TiledMap cargarMapa(String mapa) {
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.load(mapa, TiledMap.class);
        manager.finishLoading();
        return manager.get(mapa);
    }


    public void setPlataformaY(String... capas){
        plataformaY = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas)
            plataformaY.add((TiledMapTileLayer)mapa.getLayers().get(s));

    }

    public void setPlataformaX(String... capas){
        plataformaX = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas)
            plataformaX.add((TiledMapTileLayer)mapa.getLayers().get(s));

    }

    public void setEncima(String... capas){
        encima = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas){
            encima.add((TiledMapTileLayer)mapa.getLayers().get(s));
        }
    }

    public void setItem(String... capas){
        items = new Array<TiledMapTileLayer>(capas.length);
        for(String s: capas){
            items.add((TiledMapTileLayer) mapa.getLayers().get(s));
        }
    }

    public boolean colisionX(float x, float y){
        return colision(x,y,plataformaX);
    }

    public boolean colisionY(float x, float y){
        return colision(x,y,plataformaY);
    }

    public boolean colision(float x, float y, Array<TiledMapTileLayer> capas){
        for(TiledMapTileLayer capa: capas){
            TiledMapTileLayer.Cell cell = capa.getCell((int)(x/capa.getTileWidth()), (int)(y/capa.getTileHeight()));
            if(cell != null) return true;
        }
        return false;
    }

    public boolean colisionItem(float x, float y){
        return colision(x,y,items);
    }

    public void draw(){
        renderer.setView(camara);
        renderer.render();
    }

    public void drawE(){
        for(TiledMapTileLayer layer: encima)
            renderer.renderTileLayer(layer);
    }

    public void remove(String layer){
        mapa.getLayers().remove(mapa.getLayers().get(layer));
    }
}
