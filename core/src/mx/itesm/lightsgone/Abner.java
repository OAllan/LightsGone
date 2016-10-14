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
    private Salto salto;
    private int cantVida;
    private Animation caminar, atacar;
    private OrthographicCamera camara;
    private Array<Proyectil> proyectiles;
    private Enemigo.Sopa sopa;
    private TiledMap mapa;
    private TiledMapTileLayer pared;
    private Array<TiledMapTileLayer> plataformas;

    private float timerAnimation, timerAnimationA;
    private TiledMapTileLayer malteada;
    private Ataque estadoAtaque;
    private Vertical estadoSalto;
    private Horizontal estadoHorizontal;

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
        salto = Salto.BAJANDO;
        this.camara = camara;
        transicion = 0;
        this.proyectiles = new Array<Proyectil>(50);
        this.mapa =mapa;
        alturaMax = y + 270;
        iniciarPlataformas();
        estadoHorizontal = Horizontal.DESACTIVADO;
        estadoAtaque = Ataque.DESACTIVADO;
        estadoSalto = Vertical.DESACTIVADO;
        cantVida = 99;

    }

    private void iniciarPlataformas() {
        plataformas = new Array<TiledMapTileLayer>(3);
        String[] nombres = {"PlataformaPiso", "Plataformas", "Plataformas2"};
        for(String s: nombres){
            plataformas.add((TiledMapTileLayer)mapa.getLayers().get(s));
        }
        pared = (TiledMapTileLayer)mapa.getLayers().get("ParedPuerta");
        malteada = (TiledMapTileLayer)mapa.getLayers().get("Malteada");
    }

    public void draw(SpriteBatch batch, boolean right){
        sprite.draw(batch);
        actualizar(right);
    }

    private void actualizar(boolean right) {

        if(estadoHorizontal == Horizontal.ACTIVADO){
            if(estadoSalto!=Vertical.ACTIVADO&&estadoAtaque!= Ataque.ACTIVADO){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setTexture(caminar.getKeyFrame(timerAnimation).getTexture());
                walk(right);
            }
            else
                walk(right);
        }

        if(estadoSalto == Vertical.ACTIVADO){
            if(estadoAtaque != Ataque.ACTIVADO){
                if(cont>=0){
                    sprite.setTexture(saltar1);
                    cont--;
                }
                else {
                    sprite.setTexture(saltar2);
                    jump();
                }
            }
            else
                jump();
        }

        if(estadoAtaque == Ataque.ACTIVADO){
            attack(right);
        }

        if(estadoAtaque != Ataque.ACTIVADO && estadoSalto != Vertical.ACTIVADO && estadoHorizontal != Horizontal.ACTIVADO)
            sprite.setTexture(neutral);


    }

    private void attack(boolean right){
        timerAnimationA += Gdx.graphics.getDeltaTime();
        sprite.setTexture(atacar.getKeyFrame(timerAnimationA).getTexture());
        if(timerAnimationA>=0.1&&timerAnimationA<(0.1+Gdx.graphics.getDeltaTime())){
            proyectiles.add(new Proyectil(pResortera, sprite.getX()+142, sprite.getY()+138, right));
            Gdx.app.debug("Ataque", "" + Gdx.graphics.getDeltaTime());
        }
        else if (timerAnimationA>atacar.getAnimationDuration()) {
            estadoAtaque = Ataque.DESACTIVADO;
            timerAnimationA = 0;
        }
    }

    public void walk(boolean right){

        if (right){
            if (!colisionX((sprite.getX()+(sprite.getWidth()/2))+mov, sprite.getY())){
                if(colisionY(sprite.getX()+sprite.getWidth()/2, sprite.getY() - (saltoMov + gravedad)) || estadoSalto == Vertical.ACTIVADO){
                    if(sprite.isFlipX()){
                        sprite.flip(true, false);
                    }
                    sprite.translate(mov, 0);
                    if(sprite.getX()>530&&sprite.getX()<2100)
                        camara.translate(mov,0);
                    else if(sprite.getX()<=530)
                        camara.position.x = 640;
                }
                else {
                    estadoSalto = Vertical.ACTIVADO;
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
                    estadoSalto = Vertical.ACTIVADO;
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
                    estadoSalto = Vertical.DESACTIVADO;
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

    public void setEstadoAtaque(Ataque estado) {
        this.estadoAtaque = estado;
    }
    public void setEstadoVertical(Vertical estado) {
        this.estadoSalto = estado;
    }

    public void setEstadoHorizontal(Horizontal estado) {
        this.estadoHorizontal = estado;
    }

    public boolean isJumping(){
        return (estadoSalto == Vertical.ACTIVADO);
    }

    private boolean colisionX(float x, float y){
        Cell cell = pared.getCell((int)(x/pared.getTileWidth()), (int)(y/pared.getTileHeight()));
        return cell!=null;
    }

    public boolean colisionMalteada(){
        Cell cell = malteada.getCell(((int)((sprite.getX()+sprite.getWidth())/malteada.getTileWidth())), (int)(sprite.getY()/malteada.getTileHeight()));
        return cell!=null;
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
                    estadoSalto = Vertical.DESACTIVADO;
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
        return (estadoAtaque == Ataque.ACTIVADO);
    }

    public Rectangle getBoundingRectangle(){

        return sprite.getBoundingRectangle();
    }

    public Array<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public float getY() {
        return sprite.getY();
    }

    public int getcantVida(){
        return cantVida;
    }

    public void setCantVida(int vida){
        cantVida=vida;
    }

    public enum Salto{
        SUBIENDO,
        BAJANDO
    }

    public enum Horizontal{
        ACTIVADO,
        DESACTIVADO
    }

    public enum Ataque{
        ACTIVADO,
        DESACTIVADO
    }

    public enum Vertical{
        ACTIVADO,
        DESACTIVADO
    }




}
