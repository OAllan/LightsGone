package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Abner {
    private Sprite sprite;
    private boolean flag;
    private int transicion, cont = 8;
    private float y = 135f, saltoMov = 8f, gravedad = 10f, alturaMax;
    private Texture neutral, saltar1, saltar2, pResortera;
    private float mov = 4f;
    private Estado estado;
    private Salto salto;
    private Animation caminar, atacar;
    private OrthographicCamera camara;
    private Array<Proyectil> proyectiles;
    private Enemigo.Sopa sopa;
    private TiledMap mapa;
    private TiledMapTileLayer pared;
    private Array<TiledMapTileLayer> plataformas;

    private float timerAnimation, timerAnimationA;

    public Abner(Texture texture, Texture correr1, Texture correr2, Texture saltar1, Texture saltar2, Texture resortera1,
                 Texture resortera2, Texture resortera3, Texture pResortera,OrthographicCamera camara, TiledMap mapa){
        this.neutral = texture;
        this.saltar1 = saltar1;
        this.saltar2 = saltar2;
        this.pResortera = pResortera;
        this.caminar = new Animation(0.15f, new TextureRegion(correr1), new TextureRegion(correr2));
        caminar.setPlayMode(Animation.PlayMode.LOOP);
        this.atacar = new Animation(0.2f, new TextureRegion(resortera1),new TextureRegion(resortera2),new TextureRegion(resortera3));
        this.atacar.setPlayMode(Animation.PlayMode.NORMAL);
        sprite = new Sprite(neutral);
        sprite.setPosition(530, y);
        timerAnimation = 0;
        timerAnimationA =0;
        estado = Estado.NEUTRAL;
        salto = Salto.BAJANDO;
        this.camara = camara;
        transicion = 0;
        this.proyectiles = new Array<Proyectil>(50);
        this.mapa =mapa;
        alturaMax = y + 270;
        iniciarPlataformas();
    }

    private void iniciarPlataformas() {
        plataformas = new Array<TiledMapTileLayer>(3);
        String[] nombres = {"PlataformaPiso", "Plataformas", "Plataformas2"};
        for(String s: nombres){
            plataformas.add((TiledMapTileLayer)mapa.getLayers().get(s));
        }
        pared = (TiledMapTileLayer)mapa.getLayers().get("ParedPuerta");
    }

    public void draw(SpriteBatch batch, boolean right){
        sprite.draw(batch);
        actualizar(right);
    }

    private void actualizar(boolean right) {
        switch (estado){
            case NEUTRAL:
                neutral(right);
                break;
            case SALTANDO:
                if(cont>=0){
                    sprite.setTexture(saltar1);
                    cont--;
                }
                else {
                    sprite.setTexture(saltar2);
                    jump();
                }
                break;
            case CAMINANDO:
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setTexture(caminar.getKeyFrame(timerAnimation).getTexture());
                walk(right);
                break;
            case SALTANDOAVANCE:
                if(cont>=0){
                    sprite.setTexture(saltar1);
                    cont--;
                }
                else {
                    sprite.setTexture(saltar2);
                    jumpForward(right);
                }
                break;
            case ATAQUE:
                attack(right);
                if (atacar.getAnimationDuration()<= timerAnimationA){
                    estado = Estado.NEUTRAL;
                    timerAnimationA = 0;
                }
                break;
            case ATAQUECAMINANDO:
                attack(right);
                walk(right);
                if (atacar.getAnimationDuration() <= timerAnimationA){
                    estado = Estado.CAMINANDO;
                    timerAnimationA = 0;
                }
                break;
            case ATAQUESALTANDO:
                attack(right);
                jump();
                if (atacar.getAnimationDuration()<= timerAnimationA){
                    estado = Estado.SALTANDO;
                    timerAnimationA = 0;
                }
                break;
            case ATAQUESALTANDOAVANCE:
                attack(right);
                jump();
                walk(right);
                if (atacar.getAnimationDuration()<= timerAnimationA){
                    estado = Estado.SALTANDOAVANCE;
                    timerAnimationA = 0;
                }
                break;
        }
    }

    private void attack(boolean right){
        timerAnimationA += Gdx.graphics.getDeltaTime();
        sprite.setTexture(atacar.getKeyFrame(timerAnimationA).getTexture());
        if(timerAnimationA>=0.1&&timerAnimationA<(0.1+Gdx.graphics.getDeltaTime())){
            proyectiles.add(new Proyectil(pResortera, sprite.getX()+142, sprite.getY()+138, right));
            Gdx.app.debug("Ataque", "" + Gdx.graphics.getDeltaTime());
        }
    }

    public void walk(boolean right){


        if (right){
            if (!colisionX((sprite.getX()+(sprite.getWidth()/2))+mov, sprite.getY())){
                if(colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY() - (saltoMov + gravedad))){
                    if(sprite.isFlipX()){
                        sprite.flip(true, false);
                    }
                    sprite.translate(mov, 0);
                    if(sprite.getX()>530&&sprite.getX()<2100)
                        camara.translate(mov,0);
                }
                else {
                    estado = Estado.SALTANDO;
                    salto = Salto.BAJANDO;
                }
            }
        }
        else {
            if (!colisionX((sprite.getX()+(sprite.getWidth()/2)) - mov, sprite.getY())) {
                if (colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY() - (saltoMov + gravedad))){
                    if(!sprite.isFlipX()){
                        sprite.flip(true, false);
                    }
                    sprite.translate(-mov, 0);

                    if(sprite.getX()>530&&sprite.getX()<2100)
                        camara.translate(-mov,0);
                }
                else {
                    estado = Estado.SALTANDO;
                    salto = Salto.BAJANDO;
                }
            }
        }

        camara.update();


    }

    private void jumpForward(boolean right){

        if (right){
            if (!colisionX((sprite.getX()+(sprite.getWidth()/2)) + mov, sprite.getY())) {
                if (sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
                sprite.translate(mov * 2, 0);
                if (sprite.getX() > 530 && sprite.getX() < 2100)
                    camara.translate(mov * 2, 0);
            }
        }
        else {
            if (!colisionX((sprite.getX()+(sprite.getWidth()/2)) - mov, sprite.getY())) {
                if (!sprite.isFlipX()) {
                    sprite.flip(true, false);
                }
                sprite.translate(-mov * 2, 0);
                if (sprite.getX() > 530 && sprite.getX() < 2100)
                    camara.translate(-mov * 2, 0);
            }
        }
        camara.update();

        switch (salto){
            case SUBIENDO:
                sprite.setY(sprite.getY()+saltoMov);
                if(limiteCamara())
                    camara.translate(0, saltoMov);
                else if(sprite.getY() == y)
                    camara.position.y = 400;
                camara.update();
                if(sprite.getY()>= alturaMax){
                    sprite.setY(alturaMax);
                    salto = Salto.BAJANDO;
                }
                break;
            case BAJANDO:

                if(colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY() - (saltoMov + gravedad))) {
                    estado = Estado.NEUTRAL;
                    alturaMax = sprite.getY() +270;
                }
                else {
                    sprite.setY(sprite.getY() - (saltoMov + gravedad));
                    if(limiteCamara())
                        camara.translate(0,- (saltoMov + gravedad));
                    else if(sprite.getY() == y)
                        camara.position.y = 400;
                    camara.update();
                }
                break;
        }
    }

    public void neutral(boolean right) {
        sprite.setTexture(neutral);
        sprite.setSize(neutral.getWidth(), neutral.getHeight());
        if (right&&sprite.isFlipX())
            sprite.flip(true, false);
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean isJumping(){
        return (estado == Estado.SALTANDO|| estado == Estado.SALTANDOAVANCE||estado == Estado.ATAQUESALTANDO||
        estado == Estado.ATAQUESALTANDOAVANCE);
    }

    private boolean colisionX(float x, float y){
        Cell cell = pared.getCell((int)(x/pared.getTileWidth()), (int)(y/pared.getTileHeight()));
        if(cell!= null)
            return true;
        return false;
    }

    public void jump(){

        switch (salto){
            case SUBIENDO:
                sprite.setY(sprite.getY()+saltoMov);
                if(limiteCamara())
                    camara.translate(0,saltoMov);
                else if(sprite.getY() == y)
                    camara.position.y = 400;
                camara.update();
                if(sprite.getY()>= alturaMax){
                    sprite.setY(alturaMax);
                    salto = Salto.BAJANDO;
                }
                break;
            case BAJANDO:

                if(colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY() - (saltoMov + gravedad))) {
                    estado = Estado.NEUTRAL;
                    alturaMax = sprite.getY() +270;
                } else {
                    sprite.setY(sprite.getY() - (saltoMov + gravedad));
                    if(limiteCamara())
                        camara.translate(0,- (saltoMov + gravedad));
                    else if(sprite.getY() == y)
                        camara.position.y = 400;

                    camara.update();
                }
                break;

        }
    }

    private boolean colisionY(float x, float y){
        for (TiledMapTileLayer layer: plataformas) {
            Cell cell = layer.getCell((int)(x/layer.getTileWidth()), (int)(y/layer.getTileHeight()));
            if(cell!= null)
                return true;
        }
        return false;
    }

    public void setSalto(Salto salto) {
        this.salto = salto;
    }

    public float getX(){
        return sprite.getX();
    }

    private boolean limiteCamara(){
        return sprite.getY()<=480 && sprite.getY()>y;
    }

    public boolean isAttacking() {
        return (estado == Estado.ATAQUE|| estado == Estado.ATAQUECAMINANDO||estado == Estado.ATAQUESALTANDO||
        estado ==Estado.ATAQUESALTANDOAVANCE);
    }

    public Array<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public enum Salto{
        SUBIENDO,
        BAJANDO
    }

    public enum Estado{
        SALTANDO,
        CAMINANDO,
        NEUTRAL,
        SALTANDOAVANCE,
        ATAQUE,
        ATAQUECAMINANDO,
        ATAQUESALTANDO,
        ATAQUESALTANDOAVANCE;
    }


}
