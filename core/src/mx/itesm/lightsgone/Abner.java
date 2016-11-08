package mx.itesm.lightsgone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Created by allanruiz on 19/09/16.
 */
public class Abner {
    public static final int LAMPARAX = 1250;
    public static final int LAMPARAY = 1220;
    public static final int LAMPARAXLEFT = 1750;
    public static final int LAMPARAANCHO = 195;
    private static AssetManager assetManager;
    public static final int X = 450;
    public static final int SALTOMAX = 500;
    private final int ANCHO = 224, ALTO = 321;
    private Sprite sprite, luz;
    private int cont = 8;
    private float y = 135f, saltoMov = 8f, gravedad = 13f, alturaMax, alpha = 1;
    private float mov = 40;
    private final float MOVY = (0.2125f)*mov;
    private Salto salto;
    private int cantVida, vidas;
    private Animation caminar, atacar, atacarLanzaPapas, caminarLampara;
    private OrthographicCamera camara;
    private ArrayList<Proyectil> proyectiles;
    private Mapa mapa;
    private float timerAnimation, timerAnimationA, timerDano, timerDanoAlpha;
    private Ataque estadoAtaque;
    private Vertical estadoSalto;
    private Horizontal estadoHorizontal;
    private Rectangle ProyectilRectangulo=new Rectangle();
    public boolean pogo, capita, lanzapapas, danoA, danoB, pisoLata,lampara;
    private boolean muerte, escaleras;
    private boolean arrastrado, arrastradoPiso;
    private float movPiso;
    private int papas;
    private GameInfo gameInfo;
    private Nivel0.Lampara estadoLampara;
    private static TextureRegion caminarTex, saltoTex, pogoTex, caminarLamparaTex, lanzapapasTex, resorteraTex, capaTex,neutral, dano, neutralLampara, neutralCapa, saltar1,saltar2,saltarLampara1,saltarLampara2,saltoCapa, saltarPogo1, saltarPogo2;
    private static Texture encendida, encendidaOscuridad;

    static {
        assetManager = new AssetManager();
        cargarTexturas();
    }

    private static void cargarTexturas() {
        assetManager.load("PCapa.png",Texture.class);
        assetManager.load("PLampara.png",Texture.class);
        assetManager.load("PLanzaPapa.png",Texture.class);
        assetManager.load("PNeutralDañoCorrer.png",Texture.class);
        assetManager.load("PPogo.png",Texture.class);
        assetManager.load("PResortera.png",Texture.class);
        assetManager.load("PSalto.png",Texture.class);
        assetManager.load("OscuridadConLampara.png", Texture.class);
        assetManager.load("LuzConLampara.png",Texture.class);
        assetManager.finishLoading();
        capaTex = new TextureRegion((Texture)assetManager.get("PCapa.png"));
        caminarLamparaTex = new TextureRegion((Texture) assetManager.get("PLampara.png"));
        lanzapapasTex = new TextureRegion((Texture)assetManager.get("PLanzaPapa.png"));
        caminarTex = new TextureRegion((Texture)assetManager.get("PNeutralDañoCorrer.png"));
        pogoTex = new TextureRegion((Texture)assetManager.get("PPogo.png"));
        resorteraTex = new TextureRegion((Texture)assetManager.get("PResortera.png"));
        saltoTex = new TextureRegion((Texture)assetManager.get("PSalto.png"));
        encendida = assetManager.get("LuzConLampara.png");
        encendidaOscuridad = assetManager.get("OscuridadConLampara.png");
    }

    public Abner(OrthographicCamera camara, Mapa mapa, GameInfo gameInfo){
        TextureRegion[][] textureRegions = caminarTex.split(ANCHO, ALTO);
        neutral = textureRegions[0][0];
        dano = textureRegions[0][1];
        this.caminar = new Animation(0.15f,textureRegions[0][2],textureRegions[0][3],textureRegions[0][4],textureRegions[0][5]);
        textureRegions = caminarLamparaTex.split(ANCHO, ALTO);
        neutralLampara = textureRegions[0][0];
        this.caminarLampara = new Animation(0.15f,textureRegions[0][1],textureRegions[0][2],textureRegions[0][3],textureRegions[0][4]);
        caminar.setPlayMode(Animation.PlayMode.LOOP);
        caminarLampara.setPlayMode(Animation.PlayMode.LOOP);
        saltarLampara1 = textureRegions[0][5];
        saltarLampara2 = textureRegions[0][6];
        textureRegions = resorteraTex.split(ANCHO, ALTO);
        this.atacar = new Animation(0.2f, textureRegions[0][0],textureRegions[0][1],textureRegions[0][2]);
        this.atacar.setPlayMode(Animation.PlayMode.NORMAL);
        textureRegions = lanzapapasTex.split(ANCHO, ALTO);
        this.atacarLanzaPapas = new Animation(0.2f, textureRegions[0][0],textureRegions[0][1]);
        textureRegions = saltoTex.split(ANCHO, ALTO);
        saltar1 = textureRegions[0][0];
        saltar2 = textureRegions[0][1];
        textureRegions = pogoTex.split(ANCHO, ALTO);
        saltarPogo1 = textureRegions[0][0];
        saltarPogo2 = textureRegions[0][1];
        sprite = new Sprite(neutral);
        sprite.setPosition(gameInfo.getX(), gameInfo.getY());
        timerAnimation = 0;
        timerAnimationA = 0;
        salto = Salto.BAJANDO;
        this.camara = camara;
        this.camara.position.set(gameInfo.getCamaraX(), gameInfo.getCamaraY(), 0);
        this.proyectiles = new ArrayList<Proyectil>(50);
        this.mapa =mapa;
        alturaMax = sprite.getY() + SALTOMAX;
        estadoHorizontal = Horizontal.DESACTIVADO;
        estadoAtaque = Ataque.DESACTIVADO;
        estadoSalto = Vertical.DESACTIVADO;
        cantVida = gameInfo.getVida();
        this.pogo = gameInfo.isPogo();
        this.capita = gameInfo.isCapita();
        this.lanzapapas = gameInfo.isLanzapapas();
        arrastrado = false;
        this.vidas = gameInfo.getVidas();
        danoA = false;
        timerDano = 0.5f;
        timerDanoAlpha =3;
        this.papas = 10;
        lampara = gameInfo.isLampara();
        estadoLampara = Nivel0.Lampara.APAGADA;
        this.luz = new Sprite(encendida);
        this.luz.setPosition(sprite.getX()+ LAMPARAANCHO - LAMPARAX,sprite.getY()- LAMPARAY +120);
        this.gameInfo = gameInfo;
    }


    public void draw(SpriteBatch batch, boolean right){
        if(Nivel0.habilidadActual== Nivel0.Habilidad.LAMPARA){
            luz.draw(batch);
        }
        sprite.draw(batch);
        actualizar(right);
    }

    private void actualizar(boolean right) {

        if(sprite.getY()<= 0||mapa.colisionMuerte(sprite.getX(),sprite.getY())) {
            muerte = true;
        }

        if(cantVida<=0&&vidas<=0){
            muerte =true;
        }

        else if(cantVida<=0&&vidas>=1) {
            vidas--;
            cantVida = 99;
        }


        switch (estadoLampara){
            case ENCENDIDA:
                luz.setAlpha(1);
                luz.setTexture(encendidaOscuridad);
                break;
            case APAGADA:
                luz.setAlpha(0);
                break;
            case ENCENDIDALUZ:
                luz.setAlpha(1);
                luz.setTexture(encendida);
                break;
        }

        mapa.colisionCaja(sprite.getX()+sprite.getWidth()/2, sprite.getY()+35, right, mov);

        if(mapa.colisionEscalera(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov))&&salto != Salto.SUBIENDO)
            escaleras = true;
        else if(mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov))!=-1)
            escaleras = false;

        if(danoA){
            sprite.setRegion(dano);
            if(!right)
                sprite.flip(true,false);
            timerDano-= Gdx.graphics.getDeltaTime();
            if(timerDano<=0){
                timerDano=0.5f;
                danoA = false;
                danoB = true;
            }
        }

        if(danoB){
            timerDanoAlpha -= Gdx.graphics.getDeltaTime();
            for(float time = 0;time<3;time+=0.3f) {
                if (timerDanoAlpha<time){
                    if(alpha==0.7f)
                        alpha = 1;
                    else if(alpha==1)
                        alpha = 0.7f;
                }
            }
            sprite.setAlpha(alpha);
            if(timerDanoAlpha<=0){
                timerDanoAlpha = 3;
                danoB = false;
                sprite.setAlpha(1);
            }
        }

        if(mapa.colisionDano(sprite.getX()+sprite.getWidth()/2, sprite.getY()+20)&&!danoA&!danoB){
            cantVida-=5;
            danoA = true;
        }


        if(mapa.colisionItem(sprite.getX()+sprite.getWidth(),sprite.getY(),"VidaExtra")){
            mapa.remove("VidaExtra");
            if(vidas<3)
                vidas++;
        }

        if(mapa.colisionItem(sprite.getX()+sprite.getWidth(), sprite.getY(), "Malteada")){
            mapa.remove("Malteada");
            if((cantVida + 10)<=99) cantVida+=10;
            else cantVida = 99;
        }

        if(mapa.colisionItem(sprite.getX()+(3*sprite.getWidth()/4), sprite.getY()+20,"Pogo")) {
            mapa.remove("Pogo");
            pogo = true;
            gameInfo.actualizarDatosTemp();
        }

        if(mapa.colisionItem(sprite.getX()+(3*sprite.getWidth()/4), sprite.getY()+20,"LanzaPapa")){
            mapa.remove("LanzaPapa");
            lanzapapas = true;
            gameInfo.actualizarDatosTemp();
        }

        if(mapa.colisionItem(sprite.getX()+(3*sprite.getWidth()/4), sprite.getY()+20,"Lampara")){
            mapa.remove("Lampara");
            lampara = true;
            gameInfo.actualizarDatosTemp();
        }

        if(mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))&&estadoHorizontal == Horizontal.ACTIVADO)
            estadoHorizontal = Horizontal.INCLINADO;



        if(arrastrado){
            sprite.translate(-mov,-MOVY);
            camara.translate(-mov,0);
        }
        if(arrastradoPiso){
            if(!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY()+20)&&!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) - mov, sprite.getY()+20)){
                sprite.translate(movPiso, 0);
                if(limiteCamaraX())
                    camara.translate(movPiso,0);
            }
            else
                arrastradoPiso =false;
        }

        if(estadoHorizontal == Horizontal.INCLINADO){
            sprite.setRotation(12);
            if(estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO&&!danoA&&Nivel0.habilidadActual== Nivel0.Habilidad.LAMPARA){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setRegion(caminarLampara.getKeyFrame(timerAnimation));
                walk(right);
            }
            else if(estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO&&!danoA){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setRegion(caminar.getKeyFrame(timerAnimation));
                walk(right);
            }
            else
                walk(right);
            if(!arrastrado){
                sprite.translate(0, right?MOVY:-MOVY);
                camara.translate(0, right ? MOVY : -MOVY);
            }
            alturaMax = sprite.getY() +SALTOMAX;
        }


        else if(estadoHorizontal == Horizontal.ACTIVADO){
            sprite.setRotation(0);
            if((estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO&&!danoA&&Nivel0.habilidadActual== Nivel0.Habilidad.LAMPARA)||(escaleras&&Nivel0.habilidadActual== Nivel0.Habilidad.LAMPARA)){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setRegion(caminarLampara.getKeyFrame(timerAnimation));
                walk(right);
            }
            else if((estadoSalto==Vertical.DESACTIVADO&&estadoAtaque!= Ataque.ACTIVADO&&!danoA)||escaleras){
                timerAnimation += Gdx.graphics.getDeltaTime();
                sprite.setRegion(caminar.getKeyFrame(timerAnimation));
                walk(right);
            }
            else
                walk(right);
        }

        if(estadoSalto==Vertical.POGO) {
            if (estadoAtaque != Ataque.ACTIVADO) {
                if (cont >= 0) {
                    sprite.setRegion(saltarPogo1);
                    cont--;
                } else {
                    sprite.setRegion(saltarPogo2);
                    jump(alturaMax + SALTOMAX);
                }
                if(!right){
                    sprite.flip(true, false);
                }
            }
        }

        else if(estadoSalto == Vertical.ACTIVADO){

            if(estadoAtaque != Ataque.ACTIVADO&&!danoA){
                if(Nivel0.habilidadActual== Nivel0.Habilidad.LAMPARA){
                    if(cont>=0&&escaleras){
                        sprite.setRegion(saltarLampara1);
                        cont--;
                    }
                    else {
                        if(!escaleras)
                            sprite.setRegion(saltarLampara2);
                        jump(alturaMax);
                    }
                }
                else {
                    if(cont>=0&escaleras){
                        sprite.setRegion(saltar1);
                        cont--;
                    }
                    else {
                        if(!escaleras)
                            sprite.setRegion(saltar2);
                        jump(alturaMax);
                    }
                }
                if(!right&&!escaleras)
                    sprite.flip(true,false);
            }
            else
                jump(alturaMax);
        }


        if(estadoAtaque == Ataque.ACTIVADO&&estadoSalto!= Vertical.POGO){
            if(!danoA)
                attack(right);
            else
                estadoAtaque = Ataque.DESACTIVADO;
        }
        else if(estadoAtaque==Ataque.LANZAPAPAS&&estadoSalto==Vertical.DESACTIVADO){
            if(!danoA)
                attack(right);
            else
                estadoAtaque = Ataque.DESACTIVADO;
        }
        else{
            estadoAtaque = Ataque.DESACTIVADO;
        }

        if(estadoAtaque == Ataque.DESACTIVADO && estadoSalto == Vertical.DESACTIVADO && estadoHorizontal==Horizontal.DESACTIVADO&&!danoA){
            if((mapa.colisionY(sprite.getX()+sprite.getWidth()/2,sprite.getY()-(saltoMov))!=-1||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||pisoLata)&& Nivel0.habilidadActual== Nivel0.Habilidad.LAMPARA){
                sprite.setRegion(neutralLampara);
                if(!right)
                    sprite.flip(true, false);
            }
            else if(mapa.colisionY(sprite.getX()+sprite.getWidth()/2,sprite.getY()-(saltoMov))!=-1||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||pisoLata){
                sprite.setRegion(neutral);
                if(!right)
                    sprite.flip(true, false);
            }
            else{
                estadoSalto = Vertical.ACTIVADO;
                salto = Salto.BAJANDO;
            }
        }

        if(limiteCamara()){
            camara.position.set(camara.position.x, 265 + sprite.getY(), 0);
        }
        camara.update();
    }

    private void attack(boolean right){
        switch (estadoAtaque){
            case ACTIVADO:
                timerAnimationA += Gdx.graphics.getDeltaTime();
                sprite.setRegion(atacar.getKeyFrame(timerAnimationA));
                if(!right){
                    sprite.flip(true,false);
                }
                if(timerAnimationA>=0.1&&timerAnimationA<(0.1+Gdx.graphics.getDeltaTime())){
                    proyectiles.add(new Proyectil.Canica(sprite.getX() + 142, sprite.getY()+138, right));
                }
                else if (timerAnimationA>atacar.getAnimationDuration()) {
                    estadoAtaque = Ataque.DESACTIVADO;
                    timerAnimationA = 0;
                }
                break;
            case LANZAPAPAS:
                timerAnimationA += Gdx.graphics.getDeltaTime();
                sprite.setRegion(atacarLanzaPapas.getKeyFrame(timerAnimationA));
                if(!right){
                    sprite.flip(true,false);
                }
                if(timerAnimationA>0.2f&&timerAnimationA<=(0.2f+Gdx.graphics.getDeltaTime())&&papas>0){
                    proyectiles.add(new Proyectil.Papa(!right?sprite.getX():sprite.getX()+sprite.getWidth(), sprite.getY()+100, right, this.mapa));
                    papas--;
                }
                else if(timerAnimationA>atacar.getAnimationDuration()){
                    estadoAtaque = Ataque.DESACTIVADO;
                    timerAnimationA = 0;
                }
                break;
        }
    }


    public void walk(boolean right){

        if (right){
            if(luz.isFlipX()){
                luz.flip(true,false);
                this.luz.setPosition(sprite.getX()+ LAMPARAANCHO - LAMPARAX,sprite.getY()- LAMPARAY +120);
            }
            if(sprite.isFlipX()){
                sprite.flip(true, false);

            }
            if (!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY()+20)&&!mapa.colisionInclinada((sprite.getX() + (sprite.getWidth() / 2)) + mov, sprite.getY())&&!arrastrado&&!arrastradoPiso){
                if(mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov))!=-1 || estadoSalto != Vertical.DESACTIVADO|| mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov + gravedad))||pisoLata){
                    sprite.translate(mov, 0);
                    luz.translate(mov, 0);
                    if(limiteCamaraX()){
                        camara.translate(mov,0);
                    }
                    else if(sprite.getX()<=530)
                        camara.position.x = Nivel0.ANCHO_MUNDO/2;
                }
                else {
                    estadoSalto = Vertical.ACTIVADO;
                    salto = Salto.BAJANDO;
                }
            }
        }
        else {
            if(!luz.isFlipX()){
                luz.flip(true,false);
                this.luz.setPosition(sprite.getX()- LAMPARAXLEFT,sprite.getY()- LAMPARAY +120);
            }
            if(!sprite.isFlipX()){
                sprite.flip(true, false);
            }
            if (!mapa.colisionX((sprite.getX() + (sprite.getWidth() / 2)) - mov, sprite.getY()+20)&&!mapa.colisionInclinada((sprite.getX() + (sprite.getWidth() / 2))- mov, sprite.getY())&&!arrastrado&&!arrastradoPiso) {
                if (mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov))!=-1 || estadoSalto != Vertical.DESACTIVADO|| mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (saltoMov))||pisoLata){
                    sprite.translate(-mov, 0);
                    luz.translate(-mov, 0);
                    if(limiteCamaraX()){
                        camara.translate(-mov,0);
                    }
                }
                else {
                    estadoSalto = Vertical.ACTIVADO;
                    salto = Salto.BAJANDO;
                }
            }
        }

        camara.update();


    }

    private boolean limiteCamaraX() {
        return sprite.getX()>530&&sprite.getX()<mapa.getWidth()-770;
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

    public boolean colisionEnemigo(Enemigo enemigo){
        if(enemigo.toString().equalsIgnoreCase("Lata")){
            Enemigo.Lata lata= (Enemigo.Lata)enemigo;
            if(lata.getBoundingRectangle().contains(sprite.getX() + (3 * sprite.getWidth() / 4), sprite.getY() + 10)&&!lata.piso()) {
                arrastrado = true;
            }
            if((lata.getBoundingRectangle().contains(sprite.getX()+(sprite.getWidth()/2)-mov, sprite.getY()+10)||lata.getBoundingRectangle().contains(sprite.getX()+(3*sprite.getWidth()/4)+mov, sprite.getY()+10))&&lata.piso()){
                arrastradoPiso = true;
                movPiso = lata.getMov();
            }
            if(lata.getBoundingRectangle().contains(sprite.getX()+(3*sprite.getWidth()/6), sprite.getY()-(saltoMov))){
                pisoLata = true;
            }
        }
        else{
            pisoLata = false;
            arrastradoPiso = false;
            arrastrado = false;
        }
        return arrastrado||pisoLata;

    }

    public boolean isJumping(){
        return (estadoSalto != Vertical.DESACTIVADO);
    }


    public void jump(float alturaMax){

        switch (salto){
            case SUBIENDO:
                escaleras = false;
                sprite.setY(sprite.getY() + saltoMov);
                luz.translate(0,saltoMov);
                if(sprite.getY()>= alturaMax){
                    sprite.setY(alturaMax);
                    salto = Salto.BAJANDO;
                }
                if(mapa.colisionTecho(sprite.getX()+sprite.getWidth()/2, sprite.getY()+getBoundingRectangle().getHeight())){
                    salto = Salto.BAJANDO;
                }
                break;
            case BAJANDO:
                float nuevaY = mapa.colisionY(sprite.getX() + sprite.getWidth() / 2, sprite.getY()-saltoMov);
                if (nuevaY!=-1||mapa.colisionInclinada(sprite.getX() + sprite.getWidth() / 2, sprite.getY() - (estadoSalto==Vertical.POGO?saltoMov + gravedad+10:saltoMov + gravedad))||pisoLata) {
                    estadoSalto = Vertical.DESACTIVADO;
                    if(nuevaY!=-1){
                        sprite.setY(nuevaY);
                        luz.setY(sprite.getY()- LAMPARAY +120);
                    }
                    this.alturaMax = sprite.getY() + SALTOMAX;
                }
                else{
                    luz.translate(0,-(saltoMov+gravedad));
                    sprite.setY(sprite.getY() - (saltoMov + gravedad));
                }
                break;

        }
    }

    public void setDano(boolean dano){
        this.danoA = dano;
    }

    public boolean getDano(){
        return this.danoA||this.danoB;
    }

    public void setSalto(Salto salto) {
        this.salto = salto;
    }

    public float getX(){
        return sprite.getX();
    }

    private boolean limiteCamara(){
        if(mapa.getHeight()<= 1260)
            return false;
        return sprite.getY()<mapa.getHeight()-810 && sprite.getY()>=135;
    }

    public boolean isAttacking() {
        return (estadoAtaque != Ataque.DESACTIVADO);
    }

    public Rectangle getBoundingRectangle(){
        return new Rectangle(sprite.getX()+70,sprite.getY(),sprite.getWidth()-44,sprite.getHeight()-59);
    }

    public ArrayList<Proyectil> getProyectiles() {
        return proyectiles;
    }

    public void setMapa(Mapa mapa){
        this.mapa = mapa;
    }

    public float getY() {
        return sprite.getY();
    }

    public int getcantVida(){
        return cantVida;
    }

    public boolean guardar(){
        return mapa.colisionGuardado(sprite.getX()+sprite.getWidth()/2, sprite.getY()+sprite.getHeight()/2);
    }

    public void setCantVida(int vida){
        if(vida>99)
            cantVida=99;
        else
            cantVida = vida;
    }

    public int cambioNivel() {

        return mapa.colisionPuerta(sprite.getX()+3*(sprite.getWidth()/4) + mov, sprite.getY());
    }

    public boolean isDead(){
        return muerte;
    }

    public Rectangle getProyectilRectangulo(){
        return ProyectilRectangulo;
    }

    public void borrarProyectiles(){
        if(!proyectiles.isEmpty())
            proyectiles.remove(0);
    }

    public void ajusteCamara(int direccion){

            //sprite.setX(sprite.getX() - (float).1);
            camara.position.set(camara.position.x - (float) 10 * direccion, sprite.getY(), 0);

    }

    public void setX(float x){
        sprite.setX(x);
    }

    public void setInitialPosition(int i) {
        float y = mapa.getPositionY(i);
        float x = mapa.getPositionX(i);
        sprite.setPosition(x,y);
        luz.setPosition(sprite.getX()+sprite.getWidth()- LAMPARAX,sprite.getY()- LAMPARAY +120);
        if(x<530){
            camara.position.set(Nivel0.ANCHO_MUNDO/2, camara.position.y, 0);
        }
        else if(x>mapa.getWidth()-770) {
            camara.position.set(mapa.getWidth() - Nivel0.ANCHO_MUNDO / 2, camara.position.y, 0);
        }
        else {
            camara.position.set(110 + x, camara.position.y, 0);
        }

        if(y>mapa.getHeight()-810){
            camara.position.set(camara.position.x, mapa.getHeight()-Nivel0.ALTO_MUNDO/2, 0);
        }
        else if(y<=135) {
            camara.position.set(camara.position.x, Nivel0.ALTO_MUNDO / 2, 0);
        }
        else{
            camara.position.set(camara.position.x, 265+y, 0);
        }
        alturaMax = sprite.getY() + SALTOMAX;
        this.y = y;
    }

    public boolean getPogo() {
        return pogo;
    }

    public boolean getCapita() {
        return capita;
    }

    public boolean getLanzapapas() {
        return lanzapapas;
    }

    public float getCamaraX() {
        return camara.position.x;
    }

    public float getCamaraY(){
        return camara.position.y;
    }

    public float getHeight(){return sprite.getHeight();}

    public float getWidth(){return sprite.getWidth();}

    public void reiniciar(GameInfo gameInfo) {
        sprite.setPosition(gameInfo.getX(), gameInfo.getY());
        luz.setPosition(sprite.getX()+sprite.getWidth()- LAMPARAX,sprite.getY()- LAMPARAY +120);
        camara.position.set(gameInfo.getCamaraX(), gameInfo.getCamaraY(), gameInfo.getY());
        alturaMax = sprite.getY() + SALTOMAX;
        estadoHorizontal = Horizontal.DESACTIVADO;
        estadoAtaque = Ataque.DESACTIVADO;
        estadoSalto = Vertical.DESACTIVADO;
        pogo = gameInfo.isPogo();
        capita = gameInfo.isCapita();
        lanzapapas = gameInfo.isLanzapapas();
        if(muerte){
            if(vidas>0)
                vidas--;
            else{
                cantVida = 30;
            }
        }
        muerte = false;
        danoB = false;
        danoA = false;
    }

    public int getVidas() {
        return vidas;
    }

    public int getMunicion() {
        return papas;
    }

    public void dispose(){
        caminarTex.getTexture().dispose();
        saltoTex.getTexture().dispose();
        pogoTex.getTexture().dispose();
        caminarLamparaTex.getTexture().dispose();
        lanzapapasTex.getTexture().dispose();
        resorteraTex.getTexture().dispose();
        capaTex.getTexture().dispose();
        neutral.getTexture().dispose();
        dano.getTexture().dispose();
        neutralLampara.getTexture().dispose();
        neutralCapa.getTexture().dispose();
        saltar1.getTexture().dispose();
        saltar2.getTexture().dispose();
        saltarLampara1.getTexture().dispose();
        saltarLampara2.getTexture().dispose();
        saltoCapa.getTexture().dispose();
        saltarPogo1.getTexture().dispose();
        saltarPogo2.getTexture().dispose();
        encendida.dispose();
        encendidaOscuridad.dispose();
    }


    public void setLampara(Nivel0.Lampara lampara) {
        this.estadoLampara = lampara;
    }

    public boolean getLampara() {
        return lampara;
    }

    public enum Salto{
        SUBIENDO,
        BAJANDO
    }

    public enum Horizontal{
        ACTIVADO,
        INCLINADO,
        DESACTIVADO
    }

    public enum Ataque{
        ACTIVADO,
        LANZAPAPAS,
        DESACTIVADO
    }

    public enum Vertical{
        ACTIVADO,
        DESACTIVADO,
        POGO
    }


}
