package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.sun.corba.se.spi.activation.Server;

import java.awt.Menu;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sun.security.krb5.SCDynamicStoreConfig;

import static com.waynegames.motiondarts.MenuScreen.text;
import static com.waynegames.motiondarts.MenuScreen.textIndent;

/**
 * Handles all of the touch input and graphics output specifically for the game
 *
 * @author Michael Wayne
 * @version v0.1.0
 */
public class GameScreen extends ScreenAdapter {
    MotionDarts game;

    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;

    private ModelBatch modelBatch;
    private PerspectiveCamera perspectiveCamera;
    private Environment environment;

    private ShapeRenderer shapeRenderer;

    private ModelInstance[] dartModelInstances = new ModelInstance[6];
    private ModelInstance dartboardModelInst1;
    static ModelInstance dartboardBaseModelInst;
    private ModelInstance environmentModelInst1;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private Sprite popup;

    private BitmapFont scoreFont;
    private BitmapFont scoreFontSmall;
    private BitmapFont playerFont;
    private BitmapFont currentPlayerFont;
    private BitmapFont scoreFontCricket;
    private BitmapFont scoreFontCricketMarks;
    private BitmapFont scoreFontCricketOpen;
    private BitmapFont scoreFontCricketClosed;

    private BitmapFont descriptionFont;
    private BitmapFont popupFont;
    private BitmapFont onScreenFont;

    private FreeTypeFontGenerator freeTypeFontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter;

    private boolean loadMenu = false;
    private boolean loadLoop = false;

    private int screenWidth;
    private int screenHeight;

    static float sensitivityZ = 1.0f;

    private float aimX = 0.0f;
    private float aimY = 0.0f;

    private float distanceToBoard = 18700.0f;

    private float accelX, accelY, accelZ;
    private float velX, velY, velZ;
    private float rotX, rotY;

    private boolean inFlight = false;
    private boolean viewLock = false;

    static boolean dartsReset = false;

    private int resetTimer = 0;
    private int prevZ = 0;

    private double scaleConstant;

    private boolean menuPopup = false;

    static GameClass gameClass;

    static boolean endGame = false;

    private float[] velGraph = new float[50];
    private int velGraphHead = 0;

    private Color darkBlue = new Color(0, 0, 0.6f, 0.6f);
    private Color lightBlue = new Color(0, 0, 0.8f, 0.6f);

    /* Customisation Variables */
    static int selectedLocation = 0;
    static int selectedDart = 0;
    static int opponentSelectedDart = 0;

    static int tempSelectedLocation = 0;

    static String[] locationFiles = {"environment_01", "environment_02", "environment_03", "environment_ComingSoon"};
    static String[] dartFiles = {"dart_01", "dart_05", "dart_06", "dart_04", "dart_02", "dart_03"};
    static String[] dartboardFiles = {"dartboard_01", "dartboard_02", "dartboard_01", "dartboard_01"};

    /* AI Variables */
    private int animTimeCounter = 0;

    /* Animation Variables */
    private int translats = 0;
    private int translatCount = 0;

    static boolean dartboardHit = false;
    private boolean swoosh = false;
    private boolean oneeighty = false;
    static boolean wow = false;

    private Sound dartboardHitSound;
    private Sound oneeightySound;
    private Sound swooshSound;
    private Sound wowSound;

    /**
     * Game screen setup constructor<br>
     * Handles:<br>
     * <ul>
     *     <li>Viewpoint Setup</li>
     *     <li>3D Models Loading & Positioning</li>
     *     <li>Lighting Setup</li>
     *     <li>Input</li>
     * </ul>
     * @param game For changing the game screen
     */
    GameScreen (final MotionDarts game) {
        this.game = game;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        bitmapFont = new BitmapFont(Gdx.files.internal("consolas.fnt"), Gdx.files.internal("consolas.png"), false);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        scaleConstant = screenWidth / 720.0;

        endGame = false;

        /* Font Setup */
        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbbold.ttf"));
        freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        freeTypeFontParameter.color = new Color(255, 255, 255, 255);
        freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 255);
        freeTypeFontParameter.borderWidth = 2;

        // Current player font
        freeTypeFontParameter.size = (int) (40 * scaleConstant);
        currentPlayerFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        // Score Fonts
        freeTypeFontParameter.size = (int) (60 * scaleConstant);
        scoreFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.size = (int) (20 * scaleConstant);
        freeTypeFontParameter.borderWidth = 1;
        scoreFontCricket = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.color = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        scoreFontCricketClosed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.color = new Color(0.0f, 0.9f, 0.0f, 1.0f);
        scoreFontCricketOpen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.size = (int) (20 * scaleConstant);
        freeTypeFontParameter.color = new Color(255, 255, 255, 255);
        freeTypeFontParameter.borderWidth = 0;
        scoreFontCricketMarks = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));

        freeTypeFontParameter.size = (int) (30 * scaleConstant);
        freeTypeFontParameter.borderWidth = 2;
        scoreFontSmall = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        // Description and Player Fonts
        freeTypeFontParameter.size = (int) (40 * scaleConstant);
        playerFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        switch(MenuScreen.language) {
            case 0:
            case 3:
            case 4:
            case 5:
            case 9:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));
                freeTypeFontParameter.size = (int) (20 * scaleConstant);
                descriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                onScreenFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbbold.ttf"));
                freeTypeFontParameter.size = (int) (60 * scaleConstant);
                popupFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
            case 1:     // Bulgarian
            case 2:     // Russian
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbcyrillic.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 255);
                freeTypeFontParameter.borderWidth = 2;

                freeTypeFontParameter.characters = "ЕДИНГРАЧМУЛТПЙЪСЩОВК501ЯФЖ2З(WI-F)ШБремнотбилскапшвдяугз,ъщ.3жйх4'чЦцdartboulseyю\"9876фPHONEЮ?ХыёЫЁьЬ%";

                freeTypeFontParameter.size = (int) (20 * scaleConstant);
                descriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                onScreenFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (60 * scaleConstant);
                popupFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
            case 6:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/chinese.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 255);
                freeTypeFontParameter.borderWidth = 2;

                freeTypeFontParameter.characters = "%一个球员很多定制新游戏对手困难类型玩简单中501周围时钟蟋蟀英国实践家相同的设备全服务器通过2名之间电话反陌生人（WI-F）连接到你认识临称输入字可用其他正在使这，选择另存不.创建并将顶级框下面文本朋友还必须4们都按“”才能开始等候项飞镖位置语言怎么传感灵敏度标准比赛。根据落地得分如果它稀薄外环登陆是倍靠近心三靶绕公牛为每轮流投掷3支最后总成绩就被拿走了远离从该目让于击a来完双想例您剩那需要点取棋盘上数依次旦着号码以瞄第胜利板'打某些达高任何进步命赢积像往常样直然关闭再赚谁所有首先获或者两起涉及,9876和而且超会停止线值眼交换角色钱出请指放屏幕齐与快速移动机际但没释前抬作大效习惯尝试练去白方块播似乎自行问题重启返回主菜？摘统计结束抽奖功继续骰子平均二水售票窗口确性池糊失败无法找已满断";

                freeTypeFontParameter.size = (int) (20 * scaleConstant);
                descriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                onScreenFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (60 * scaleConstant);
                popupFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;

            case 7:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/japanese.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 255);
                freeTypeFontParameter.borderWidth = 2;

                freeTypeFontParameter.characters = "%1人のプレイヤー多くカスタマズ新しいゲム反対難さ種類遊びます簡単中50わりザ・クロッリケトギ練習同じデバグルサ2間で電話をかけなンジャ（WI-F）と戦接続るにあたが知って連絡一時的名前入力だ相手敵者は利用可能他誰こ使存在.作成、それボ下テキ友3彼ら4次[]押待オショダツ設定言語方セ感度標準。ポ着地点基づ得ば薄外輪土倍増心近ドブア取巻側雄牛各交互投げ終合計全体コ離目等思う歳残ヒ必要勝つめ二重上べ数字打順並陸ぐ番号完最初選特「開」高達せんウ以獲通常どお閉もや両ち関係,9876よ始ラご挙例え止価値換再稼指画面緒携帯急速実際解放停持大効果動きメニ慣 \"試み行生不自然飛見場問題性起戻ュ？NO概統功継平均DARTィ正確象失敗フ切断";

                freeTypeFontParameter.size = (int) (20 * scaleConstant);
                descriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                onScreenFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (60 * scaleConstant);
                popupFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
            case 8:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/hindi.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 255);
                freeTypeFontParameter.borderWidth = 2;

                freeTypeFontParameter.characters = "एक खिलाड़ीईअनुूतरेंयप्दवमशआसध501चोओघटगजभहइब2फै(-)थउ,षौ.औॉ3ठ4'।णछ9876ँ\"?%";

                freeTypeFontParameter.size = (int) (20 * scaleConstant);
                descriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                onScreenFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (60 * scaleConstant);
                popupFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
        }


        /* Viewpoint Setup */
        // PerspectiveCamera setup: Field of Vision, viewpoint width, viewpoint height
        perspectiveCamera = new PerspectiveCamera(70, screenWidth, screenHeight);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 100000.0f;
        setViewPosition(1);


        /* Models Setup */
        Model dartModel1 = MotionDarts.assetManager.get("models/" + dartFiles[selectedDart] + ".g3db", Model.class);
        Model dartModel2 = MotionDarts.assetManager.get("models/" + dartFiles[opponentSelectedDart] + ".g3db", Model.class);
        Model dartboardModel1 = MotionDarts.assetManager.get("models/" + dartboardFiles[selectedLocation] + ".g3db", Model.class);
        Model dartboardBaseModel = MotionDarts.assetManager.get("models/" + dartboardFiles[0] + ".g3db", Model.class);
        Model environmentModel1 = MotionDarts.assetManager.get("models/" + locationFiles[selectedLocation] + ".g3db", Model.class);

        // Assign Models to ModelInstances
        dartModelInstances[0] = new ModelInstance(dartModel1);
        dartModelInstances[1] = new ModelInstance(dartModel1);
        dartModelInstances[2] = new ModelInstance(dartModel1);
        dartModelInstances[3] = new ModelInstance(dartModel2);
        dartModelInstances[4] = new ModelInstance(dartModel2);
        dartModelInstances[5] = new ModelInstance(dartModel2);
        dartboardModelInst1 = new ModelInstance(dartboardModel1);
        dartboardBaseModelInst = new ModelInstance(dartboardBaseModel);
        environmentModelInst1 = new ModelInstance(environmentModel1);

        // Add ModelInstances to ModelInstance array
        instances.add(dartModelInstances[0]);
        instances.add(dartModelInstances[1]);
        instances.add(dartModelInstances[2]);
        instances.add(dartModelInstances[3]);
        instances.add(dartModelInstances[4]);
        instances.add(dartModelInstances[5]);
        instances.add(dartboardModelInst1);
        instances.add(environmentModelInst1);

        // Moving everything into the right place for setup
        // 100.0f = 1 cm
        // Game plays in the positive z axis (forwards)
        for(int i = 0; i < 6; i++) {
            dartModelInstances[i].transform.setToTranslation(4000.0f, -10000.0f, -1000.0f);
            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 225);
            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);

            dartModelInstances[i].materials.get(0).remove(ColorAttribute.Specular);
            dartModelInstances[i].materials.get(1).remove(ColorAttribute.Specular);
            dartModelInstances[i].materials.get(2).remove(ColorAttribute.Specular);
        }

        dartboardModelInst1.transform.setToTranslation(0.0f, 0.0f, distanceToBoard);
        dartboardModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);
        dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 180);

        switch(selectedLocation) {
            case 0:
            default:
                dartboardModelInst1.materials.get(0).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(1).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(2).remove(ColorAttribute.Specular);
                break;

            case 1:
                ModelInstance dartboardModelInst2 = new ModelInstance(dartboardModel1);
                ModelInstance dartboardModelInst3 = new ModelInstance(dartboardModel1);

                instances.add(dartboardModelInst2);
                instances.add(dartboardModelInst3);

                //dartboardModelInst1.materials.get(0).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(1).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(2).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(3).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(4).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(5).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(6).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(7).remove(ColorAttribute.Specular);
                dartboardModelInst1.materials.get(8).remove(ColorAttribute.Specular);

                dartboardModelInst2.transform.setToTranslation(-10000.0f, 0.0f, distanceToBoard);
                dartboardModelInst2.transform.rotate(0.0f, 1.0f, 0.0f, 90);
                dartboardModelInst2.transform.rotate(1.0f, 0.0f, 0.0f, 180);
                //dartboardModelInst2.materials.get(0).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(1).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(2).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(3).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(4).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(5).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(6).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(7).remove(ColorAttribute.Specular);
                dartboardModelInst2.materials.get(8).remove(ColorAttribute.Specular);

                dartboardModelInst3.transform.setToTranslation(10000.0f, 0.0f, distanceToBoard);
                dartboardModelInst3.transform.rotate(0.0f, 1.0f, 0.0f, 90);
                dartboardModelInst3.transform.rotate(1.0f, 0.0f, 0.0f, 180);
                //dartboardModelInst3.materials.get(0).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(1).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(2).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(3).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(4).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(5).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(6).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(7).remove(ColorAttribute.Specular);
                dartboardModelInst3.materials.get(8).remove(ColorAttribute.Specular);
                break;
        }

        environmentModelInst1.transform.setToTranslation(-100.0f, -4700.0f, distanceToBoard + 200.0f);
        environmentModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);
        switch(selectedLocation) {
            case 0:
            default:
                environmentModelInst1.materials.get(0).remove(ColorAttribute.Specular);
                environmentModelInst1.materials.get(1).remove(ColorAttribute.Specular);
                environmentModelInst1.materials.get(2).remove(ColorAttribute.Specular);
                break;
            case 1:
                break;
        }

        /* Textures */
        Texture popupTexture = MotionDarts.assetManager.get("textures/menuPopup.png", Texture.class);

        popup = new Sprite(popupTexture);

        /* Game Environment */
        environment = new Environment();

        switch (selectedLocation) {

            case 0:
            default:
                // Sets ambient lighting around the game environment
                environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1.0f));
                // Creates a new point light; first three parameters: color, next three: position, then intensity
                environment.add(new PointLight().set(1.0f, 1.0f, 0.7f, 12000.0f, 3000.0f, 1000.0f, 320000000.0f));
                environment.add(new PointLight().set(1.0f, 1.0f, 0.7f, -12000.0f, 3000.0f, 1000.0f, 320000000.0f));
                environment.add(new DirectionalLight().set(0.1f, 0.1f, 0.08f, 0.0f, 1.0f, 0.0f));
                environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.28f, 0.0f, -1.0f, 0.0f));
                break;

            case 1:
                // Sets ambient lighting around the game environment
                environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1.0f));
                // Creates a new point light; first three parameters: color, next three: position, then intensity
                environment.add(new PointLight().set(0.5f, 0.5f, 0.9f, 12000.0f, 3000.0f, 1000.0f, 370000000.0f));
                environment.add(new PointLight().set(0.5f, 0.5f, 0.9f, -12000.0f, 3000.0f, 1000.0f, 370000000.0f));
                environment.add(new DirectionalLight().set(0.1f, 0.1f, 0.3f, 0.0f, 1.0f, 0.0f));
                environment.add(new DirectionalLight().set(0.1f, 0.1f, 0.2f, 0.0f, -1.0f, 0.0f));
                break;
        }


        // Sounds
        dartboardHitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/darthit.mp3"));
        oneeightySound = Gdx.audio.newSound(Gdx.files.internal("sounds/oneeighty.mp3"));
        swooshSound = Gdx.audio.newSound(Gdx.files.internal("sounds/swoosh.mp3"));
        wowSound = Gdx.audio.newSound(Gdx.files.internal("sounds/wow.mp3"));

        /* Game Input */
        // InputMultiplexer for handling multiple input sources
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        // InputAdapter, receives touch input from phone screen
        inputMultiplexer.addProcessor(new InputAdapter() {

            Timer t;

            @Override
            public boolean touchDown(int touchX, int touchY, int pointer, int button) {

                /* End the game if necessary, return to main menu */
                if(endGame) {
                    endGame = false;
                    if(gameClass.getCompetitionType() > 0) {
                        MenuScreen.menuScreen = 8;
                        if(gameClass.getCompetitionType() == 5) {
                            ServerComms.disconnectFromServer();
                            ServerComms.disconnection = false;
                        }
                    } else{
                        MenuScreen.menuScreen = 1;
                        MenuScreen.selectedPracticeMode = false;
                    }
                    gameClass.writeSaveData();
                    loadMenu = true;
                }

                if(!inFlight && !viewLock && !gameClass.aiTurn && !gameClass.oppTurn) {
                    // Revert darts to be in hand
                    if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                        resetDartPositions();
                    }

                    // Places dart in right hand from left hand (avoids annoying graphical flash of the dart not being centred)
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(0.0f, 0.0f, -500.0f);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 89);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    setViewPosition(1);

                    /* Velocity Calculation */
                    // Reset velocity
                    velX = 0;
                    velY = 0;
                    velZ = 0;

                    resetTimer = 0;

                    if(t != null) {
                        t.cancel();
                        t.purge();
                    }
                    t = new Timer();

                    // Timer to receive acceleration readings 50 times a second
                    t.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {

                            // Accelerometer data
                            accelX = Gdx.input.getAccelerometerX();
                            accelY = Gdx.input.getAccelerometerY();
                            accelZ = Gdx.input.getAccelerometerZ();

                            // Rotation Data
                            rotX = Gdx.input.getPitch();
                            rotY = Gdx.input.getRoll();

                            // Rotation Error Solution; If Rotation sensors spontaneously stop working, put in a most likely rotation
                            rotX = (rotX == 0f) ? -70 : rotX;
                            rotY = (rotY == 0f) ? -90 : rotY;

                            // Gravity elimination
                            accelX = (int) -(accelX + (float) (9.80665 * Math.sin(Math.toRadians(rotY)) * Math.cos(Math.toRadians(rotX))));
                            accelY = (int) accelY + (float) (9.80665 * Math.sin(Math.toRadians(rotX)));
                            accelZ = (int) -(accelZ - (float) (9.80665 * Math.cos(Math.toRadians(rotX)) * Math.cos(Math.toRadians(rotY))));

                            // Velocity calculation
                            velX += accelX / 50;
                            velY += accelY / 50;
                            velZ += accelZ / 50;

                            // Resets
                            // Reset Timer (resets if the player shakes phone, then holds for a bit and releases)

                            if(resetTimer > 8) {
                                velX = 0;
                                velY = 0;
                                velZ = 0;
                                resetTimer = 0;
                            } else if(prevZ == (int) accelZ) {
                                resetTimer++;
                            } else{
                                if(resetTimer > 0) {
                                    resetTimer--;
                                }
                            }

                            prevZ = (int) accelZ;

                            // Ensures the player can't shake the device to spoof the velocity calculator into going faster than they are
                            if(velZ > 4) {
                                velX = 0;
                                velY = 0;
                                velZ = 0;
                            }

                            velGraph[velGraphHead] = velZ;
                            velGraph[velGraphHead] = (Math.abs(velGraph[velGraphHead]) * 25 < 50) ? Math.abs(velGraph[velGraphHead]) * 25 / 3 : Math.abs(velGraph[velGraphHead]) * 25 * 2.333333f - 100;
                            velGraphHead = (velGraphHead < velGraph.length - 1) ? ++velGraphHead : 0;

                        }
                    }, 0, 20);
                }

                if(menuPopup) {
                    if(touchX > (int) (160 * scaleConstant) && touchX < (int) (360 * scaleConstant) && touchY < screenHeight - (int) (490 * scaleConstant) && touchY > screenHeight - (int) (580 * scaleConstant)) {
                        if(gameClass.getCompetitionType() > 0) {
                            MenuScreen.menuScreen = 8;

                            if(gameClass.getCompetitionType() == 5) {
                                ServerComms.disconnectFromServer();
                                ServerComms.newThrow = false;
                            }
                        } else{
                            MenuScreen.menuScreen = 1;
                            MenuScreen.selectedPracticeMode = false;
                        }
                        gameClass.scoreSystem.overallScore[gameClass.scoreSystem.turn][0] = gameClass.scoreSystem.getScore()[0];
                        gameClass.scoreSystem.overallScore[gameClass.scoreSystem.turn][1] = gameClass.scoreSystem.getScore()[1];
                        loadMenu = true;
                    } else{
                        menuPopup = false;
                    }
                }

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {

                if(touchX < 156 && touchY < 75) {
                    // Return to main menu popup
                    viewLock = true;
                    menuPopup = true;
                }

                if(!inFlight && !viewLock && !gameClass.aiTurn && !gameClass.oppTurn) {
                    // Dart throwing
                    if(gameClass.scoreSystem.dartsThrown < 3) { // This avoids crash caused by misbehaving timer, occasionally running once more, even though it's been cancelled
                        if(velX > 0 || velY > 0 || velZ > 0) {
                            dartThrow();
                        }
                    }
                }

                // Prevents dart from being thrown when popup is closed
                if(viewLock && !inFlight && !menuPopup) {
                    viewLock = !viewLock;
                }

                // Stop velocity calculation
                if(t != null) {
                    t.cancel();
                    t.purge();
                }
                return true;
            }

            @Override
            public boolean keyDown(int keyCode) {

                if(keyCode == Input.Keys.BACK) {
                    dispose();
                }

                return false;
            }

        });

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void render (float delta) {

        final int CURRENT_FPS = (Gdx.graphics.getFramesPerSecond() > 10) ? Gdx.graphics.getFramesPerSecond() : 30;

        // Sets Viewport
        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
        // Clears Screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        // Draw 3D graphics to screen
        modelBatch.begin(perspectiveCamera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        /* Draw user interface to screen */
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        switch (gameClass.getGameMode()) {

            case 0:     // Practice
                drawUI_Practice();
                break;

            case 1:     // 501
                drawUI_501();
                break;

            case 2:     // Around the Clock
                drawUI_RTC();
                break;

            case 3:     // Cricket
                drawUI_Cricket();
                break;

            case 4:     // British Cricket
                drawUI_UKCricket();
                break;
        }

        // Velocity Graph
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(new Color(0.5f, 0.5f, 1.0f, 0.6f));

        for(int i = velGraphHead; i < velGraph.length; i++) {
            shapeRenderer.rect((int) ((310 + (i * (100 / velGraph.length) - velGraphHead * (100 / velGraph.length))) * scaleConstant), (int) (20 * scaleConstant), (int) ((100 / velGraph.length) * scaleConstant), (int) (velGraph[i] * scaleConstant));
        }

        for(int i = 0; i < velGraphHead; i++) {
            shapeRenderer.rect((int) ((310 + (i * (100 / velGraph.length) + 100 - velGraphHead * (100 / velGraph.length))) * scaleConstant), (int) (20 * scaleConstant), (int) ((100 / velGraph.length) * scaleConstant), (int) (velGraph[i] * scaleConstant));
        }

        shapeRenderer.setColor(new Color(1.0f, 0.5f, 0.0f, 0.6f));
        shapeRenderer.rect((int) (407 * scaleConstant), (int) ((20 + velGraph[(velGraphHead >= 1) ? velGraphHead - 1 : velGraph.length - 1] - 3) * scaleConstant), (int) (6 * scaleConstant), (int) (6 * scaleConstant));

        shapeRenderer.setColor(new Color(1.0f, 0.5f, 0.0f, 0.6f));
        for(int i = 0; i < 10; i++) {
            shapeRenderer.rect((int) ((310 + i * 10) * scaleConstant), (int) (94 * scaleConstant), (int) (5 * scaleConstant), (int) (scaleConstant));
        }

        shapeRenderer.end();

        // Timer
        if(gameClass.getCompetitionType() == 5 && gameClass.scoreSystem.currentPlayer == 0) {
            spriteBatch.begin();
            scoreFont.draw(spriteBatch, ServerComms.turnTimer + "", (int) ((700 - 20 * String.valueOf(ServerComms.turnTimer).length()) * scaleConstant), (int) (1260 * scaleConstant));
            spriteBatch.end();
        }

        // Game start text
        if(gameClass.scoreSystem.currentPlayer == 0 && !gameClass.gameStarted && gameClass.getGameMode() > 0 && gameClass.getGameMode() <= 3) {
            spriteBatch.begin();
            GlyphLayout onScreenLayout = new GlyphLayout(onScreenFont, text[163]);
            onScreenFont.draw(spriteBatch, onScreenLayout, (int) ((360) * scaleConstant) - onScreenLayout.width / 2, (int) (950 * scaleConstant));
            spriteBatch.end();
        }

        // Game end
        if(endGame) {
            spriteBatch.begin();

            switch (gameClass.scoreSystem.winner) {

                case 0:
                    scoreFont.draw(spriteBatch, text[145], (int) ((360 - text[145].length() * 12) * scaleConstant), (int) (800 * scaleConstant));
                    break;

                case 1:
                    scoreFont.draw(spriteBatch, gameClass.playerNames[0] + " " + text[146], (int) ((360 - (text[146].length() + 1 + gameClass.playerNames[0].length()) * 12) * scaleConstant), (int) (800 * scaleConstant));
                    break;

                case 2:
                    scoreFont.draw(spriteBatch, gameClass.playerNames[1] + " " +  text[146], (int) ((360 - (text[146].length() + 1 + gameClass.playerNames[0].length()) * 12) * scaleConstant), (int) (800 * scaleConstant));
                    break;

            }
            GlyphLayout onScreenLayout1 = new GlyphLayout(onScreenFont, text[147]);
            onScreenFont.draw(spriteBatch, onScreenLayout1, (int) ((360) * scaleConstant) - onScreenLayout1.width / 2, (int) (740 * scaleConstant));
            if(ServerComms.disconnection && (gameClass.scoreSystem.getScore()[0] > 0 && gameClass.scoreSystem.getScore()[0] > 0)) {
                GlyphLayout onScreenLayout2 = new GlyphLayout(onScreenFont, text[168]);
                onScreenFont.draw(spriteBatch, onScreenLayout2, (int) ((360) * scaleConstant) - onScreenLayout2.width / 2, (int) (700 * scaleConstant));
            }

            spriteBatch.end();
        }

        // Popup menu
        if(menuPopup) {
            spriteBatch.begin();

            spriteBatch.draw(popup, (int) (160 * scaleConstant), (int) (490 * scaleConstant), (int) (400 * scaleConstant), (int) (300 * scaleConstant));

            popupFont.draw(spriteBatch, text[135], (int) (170 * scaleConstant), (int) (778 * scaleConstant));
            popupFont.draw(spriteBatch, text[136], (int) (170 * scaleConstant), (int) (723 * scaleConstant));
            popupFont.draw(spriteBatch, text[137], (int) ((170 + textIndent[137]) * scaleConstant), (int) (566 * scaleConstant));
            popupFont.draw(spriteBatch, text[138], (int) ((170 + textIndent[138]) * scaleConstant), (int) (566 * scaleConstant));

            spriteBatch.end();
        }

        /* AI and Server Opponent */
        if((gameClass.aiTurn || ServerComms.newThrow) && !endGame && !viewLock && !inFlight) {

            if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                resetDartPositions();
            }

            float aimingX = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[2] : -(gameClass.ai.targetX - 500) * 5.6f;
            float aimingY = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[3] : -(gameClass.ai.targetY - 500) * 5.6f;

            float landingX = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[0] : gameClass.ai.landingPosX;
            float landingY = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[1] : gameClass.ai.landingPosY;

            if(gameClass.aiTurn && !gameClass.ai.throwing && !gameClass.oppTurn) {

                int target = 0;

                // Get target based on score and game mode
                switch (gameClass.getGameMode()) {
                    case 1:
                        target = gameClass.ai.targeting_501(gameClass.scoreSystem.getScore()[1]);
                        break;
                    case 2:
                        target = gameClass.ai.targeting_RTC(gameClass.scoreSystem.getScore()[1]);
                        break;
                    case 3:
                        target = gameClass.ai.targeting_Cricket(gameClass.scoreSystem.getScore(), gameClass.scoreSystem.getInnings());
                        break;
                    case 4:
                        target = gameClass.ai.targeting_UKCricket(gameClass.scoreSystem.getPlayerBatting() == 1);
                        break;
                }

                // Receive location of target from target position array
                gameClass.ai.targetX = gameClass.ai.TARGETS[target][0];
                gameClass.ai.targetY = gameClass.ai.TARGETS[target][1];

                // Overrides target to bullseye if game hasn't started
                if(!gameClass.gameStarted && gameClass.getGameMode() <= 3) {
                    gameClass.ai.targetX = 500;
                    gameClass.ai.targetY = 504;
                }

                // Generate randomised landing site around target
                gameClass.ai.aiThrow();

                gameClass.ai.throwing = true;
            }

            translats = CURRENT_FPS / 4;

            float animLength = CURRENT_FPS / 2;

            if(animTimeCounter < animLength) {
                animTimeCounter++;

                dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimingX / animLength * animTimeCounter, aimingY / animLength * animTimeCounter, -500.0f);
                dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 89);
                dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                perspectiveCamera.position.set(aimingX / animLength * animTimeCounter, aimingY / animLength * animTimeCounter, -2500.0f);
                perspectiveCamera.lookAt(aimingX / animLength * animTimeCounter, aimingY / animLength * animTimeCounter, 18000.0f);
                perspectiveCamera.update();
            } else if(animTimeCounter < animLength * 2) {
                animTimeCounter++;
            } else {

                // Throw dart
                float yPos = (1.65f * (30 / 1000.0f) * translatCount) + (-4.905f * ((30 / 1000.0f) * translatCount) * ((30 / 1000.0f) * translatCount));

                if (translatCount < translats) {
                    // Translate the dart to the calculate position where it would be at that point in flight
                    dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimingX + ((landingX - aimingX / 10000.0f) * 10000.0f) / translats * translatCount, aimingY + (yPos * 10000.0f), (distanceToBoard - 500.0f) / translats * translatCount);
                } else {
                    // Translate the dart to the exact position that it was calculated to land
                    dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.setToTranslation((landingX * 10000.0f), (landingY * 10000.0f), (distanceToBoard - 500.0f) / translats * translatCount);
                }

                // Correct the rotation
                dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                dartModelInstances[3+gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                // Check whether the animation should be ended
                if(translatCount >= translats  || yPos <= -1.73f) {

                    inFlight = false;
                    if(gameClass.aiTurn) {
                        gameClass.ai.throwing = false;
                    }

                    if(gameClass.getCompetitionType() == 5) {
                        if(ServerComms.backLog == 0) {
                            ServerComms.newThrow = false;
                        } else{
                            // Clear backlog

                            while(true) {
                                if(ServerComms.readsLeft == 4) {
                                    for (int i = 0; i < 4; i++) {
                                        ServerComms.serverIn[i] = ServerComms.serverInBuffer[0][i];
                                        ServerComms.serverInBuffer[0][i] = ServerComms.serverInBuffer[1][i];
                                    }
                                    ServerComms.backLog--;
                                    break;
                                }
                            }
                        }
                    }

                    translatCount = 0;
                    translats = 0;
                    animTimeCounter = 0;

                    if(!gameClass.gameStarted && gameClass.getGameMode() <= 3 && gameClass.getGameMode() > 0) {
                        gameClass.firstThrow(landingX, landingY);
                    } else{
                        gameClass.newThrow(landingX, landingY);
                    }

                    // Camera zoom in on dartboard
                    if(gameClass.scoreSystem.dartsThrown == 0 || endGame) {

                        viewLock = true;

                        setViewPosition(new Random().nextInt(3) + 3);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setViewPosition(1);
                                viewLock = false;
                            }
                        }, 2000);

                    }
                }

                translatCount++;
            }


        }


        /* Touch Down */
        if(Gdx.input.isTouched()) {

            // Dart aiming
            if(!inFlight && !viewLock && !gameClass.aiTurn && !gameClass.oppTurn) {

                // If player holds down finger during camera closeup, touchDown doesn't get called, but this does
                if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                    resetDartPositions();
                }

                if(gameClass.scoreSystem.dartsThrown < 3) {
                    aimX = -(Gdx.input.getX() - (screenWidth / 2f)) * 5.6f;
                    aimY = -(Gdx.input.getY() - (screenHeight / 2f)) * 5.6f;

                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimX, aimY, -500.0f);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 89);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    perspectiveCamera.position.set(aimX, aimY, -2500.0f);
                    perspectiveCamera.lookAt(aimX, aimY, 18000.0f);
                    perspectiveCamera.update();
                }
            }

        }

        if(loadMenu) {
            spriteBatch.begin();
            popupFont.draw(spriteBatch, text[169], (int) (260 * scaleConstant), (int) (230 * scaleConstant));
            spriteBatch.end();
            if(loadLoop) {
                loadMenu = false;
                loadLoop = false;
                game.setScreen(new MenuScreen(game));
                dispose();
            } else{
                loadLoop = true;
            }
        }

        // Sounds
        if(MenuScreen.sound) {
            if(dartboardHit) {
                dartboardHitSound.play();
                dartboardHit = false;
            }
            if(MenuScreen.lachie) {
                if(swoosh) {
                    swooshSound.play();
                    swoosh = false;
                }
                if(oneeighty) {
                    oneeightySound.play();
                    oneeighty = false;
                }
                if(wow) {
                    wowSound.play();
                    wow = false;
                }
            }
        }

    }

    @Override
    public void dispose () {
        // Prevents memory leaks
        modelBatch.dispose();
        spriteBatch.dispose();
        instances.clear();
        ServerComms.disconnectFromServer();
    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        perspectiveCamera.viewportWidth = width;
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.update();
    }

    /**
     * Converts velocity data into landing coordinates. The dart is then
     * smoothly animated as it flies through the air towards those coordinates.
     */
    private void dartThrow() {

        if(MenuScreen.lachie) {
            swoosh = true;
        }

        // Z Velocity exaggeration
        velZ = (velZ * velZ) / (0.5f) * sensitivityZ * 1.5f;

        // Damping
        if(velX < 11.5 && velX >= 0) {
            velX = (float) Math.pow(2, (velX - 8));
        } else if(velX > -11.5) {
            velX = (float) -Math.pow(2, (velX - 8));
        }

        if(velY >= -20 && velY <= 20) {
            velY = (velY * velY) / 40;
        }

        // Time
        final float time = (distanceToBoard / 10000.0f) / velZ; // Time = Distance / Speed

        // Landing Site x, y
        final float landX = (aimX / 10000.0f) + velX * time;   // Distance = Speed * time
        final float landY = (aimY / 10000.0f) + (velY * time) + (-4.905f * time * time); // Vertical Distance = Speed * Time + 0.5 * Acceleration * time * time

        // Animating dart flight to dartboard
        final Timer t = new Timer();

        // Stops other methods resetting or changing the dart while in flight
        inFlight = true;

        final float THROW_FPS = 30;     // Frames per second drawn to screen for dart throw

        // Send to server
        if(gameClass.getCompetitionType() == 5) {
            ServerComms.sendToServer("" + ((Float.isInfinite(landX)) ? 0 : landX));
            ServerComms.sendToServer("" + ((Float.isNaN(landY)) ? -1000 : landY));
            ServerComms.sendToServer("" + aimX);
            ServerComms.sendToServer("" + aimY);
        }

        t.scheduleAtFixedRate(new TimerTask() {

            int translations = (int) Math.ceil(time * THROW_FPS);
            int translationCount = 1;

            @Override
            public void run() {

                // Calculate the vertical position of the dart at its current point during in its flight
                float yPos = (velY * (THROW_FPS / 1000) * translationCount) + (-4.905f * ((THROW_FPS / 1000) * translationCount) * ((THROW_FPS / 1000) * translationCount));

                if (translationCount < translations) {
                    // Translate the dart to the calculate position where it would be at that point in flight
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimX + ((landX - aimX / 10000.0f) * 10000.0f) / translations * translationCount, aimY + (yPos * 10000.0f), (distanceToBoard - 500.0f) / translations * translationCount);
                } else {
                    // Translate the dart to the exact position that it was calculated to land
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation((landX * 10000.0f), (landY * 10000.0f), (distanceToBoard - 500.0f) / translations * translationCount);
                }

                // Correct the rotation
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                // Check whether the animation should be ended
                if(translationCount == translations  || yPos <= -1.73f) {

                    t.cancel();
                    t.purge();

                    inFlight = false;

                    if(!gameClass.gameStarted && gameClass.getGameMode() <= 3 && gameClass.getGameMode() > 0) {
                        gameClass.firstThrow(landX, landY);
                    } else {
                        gameClass.newThrow(landX, landY);
                    }

                    // Camera zoom in on dartboard
                    if(gameClass.scoreSystem.dartsThrown == 0 || (gameClass.scoreSystem.getScore()[gameClass.scoreSystem.currentPlayer] == 0 && gameClass.getGameMode() == 1)) {

                        viewLock = true;

                        setViewPosition(new Random().nextInt(3) + 3);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setViewPosition(1);
                                viewLock = false;
                            }
                        }, 2000);

                    }
                }

                translationCount++;

            }
        }, 0, 1000 / (int) THROW_FPS);
    }

    /**
     * Sets the position of the viewpoint to a different predefined location.
     * Positions:
     * 1 - Thrower position, behind dart
     * 2 - Observer position, to the side of the thrower
     * 3, 4, 5 - Close ups of dartboard
     *
     * @param position receives an integer, which sets the predefined position to be
     */
    private void setViewPosition(int position) {

        switch(position) {
            case 1:
                // Normal viewport position, as though looking directly at dartboard
                perspectiveCamera.position.set(0.0f, 0.0f, -2500.0f);
                perspectiveCamera.up.set(0.0f, 1.0f, 0.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            case 2:
                // Side on standing view, waiting for turn view
                perspectiveCamera.position.set(-10000.0f, 20.0f, -5000.0f);
                perspectiveCamera.lookAt(2000.0f, 0.0f, 18700.0f);
                break;
            case 3:
                // Close up view of dartboard #1
                perspectiveCamera.position.set(-3000.0f, 0.0f, 14700.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            case 4:
                // Close up view of dartboard #2
                perspectiveCamera.position.set(3000.0f, -1000.0f, 12700.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            case 5:
                // Close up view of dartboard #3
                perspectiveCamera.position.set(0.0f, 3000.0f, 14700.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            default:
                // Prints error message
                System.out.println("ERROR: In setViewPosition(int position) method call - position parameter invalid");
                break;
        }

        perspectiveCamera.update();
    }

    private void resetDartPositions() {
        for(int i = 0; i < 3; i++) {
            dartModelInstances[i].transform.setToTranslation(4000.0f + 100.0f * i, -10000.0f, -1000.0f);
            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 89);
            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
            dartModelInstances[i+3].transform.setToTranslation(-4000.0f - 100.0f * i, -10000.0f, -1000.0f);
            dartModelInstances[i+3].transform.rotate(1.0f, 0.0f, 0.0f, 89);
            dartModelInstances[i+3].transform.rotate(0.0f, 1.0f, 0.0f, 45);
        }
        dartsReset = true;
    }

    private void drawUI_501() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (200 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), (int) (250 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect((int) (520 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (470 * scaleConstant), 0, (int) (520 * scaleConstant), (int) (100 * scaleConstant), (int) (520 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        // Back button
        shapeRenderer.rect(0, (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (75 * scaleConstant), lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle((int) (100 * scaleConstant), (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (1280 * scaleConstant), (int) (156 * scaleConstant), (int) (1280 * scaleConstant), lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect((int) (660 * scaleConstant), (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        GlyphLayout scoreLayout = new GlyphLayout(descriptionFont, text[139]);
        descriptionFont.draw(spriteBatch, text[139], (int) (9 * scaleConstant), (int) (90 * scaleConstant));
        descriptionFont.draw(spriteBatch, scoreLayout, (int) ((715) * scaleConstant) - scoreLayout.width, (int) (90 * scaleConstant));

        // Draw user names
        switch (gameClass.scoreSystem.currentPlayer) {

            case 0:
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                GlyphLayout playerGlyph = new GlyphLayout(playerFont, gameClass.playerNames[1]);
                playerFont.draw(spriteBatch, playerGlyph, (int) ((635) * scaleConstant) - playerGlyph.width, (int) (150 * scaleConstant));
                break;

            case 1:
                playerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                GlyphLayout playerGlyph1 = new GlyphLayout(currentPlayerFont, gameClass.playerNames[1]);
                currentPlayerFont.draw(spriteBatch, playerGlyph1, (int) ((635) * scaleConstant) - playerGlyph1.width, (int) (150 * scaleConstant));
                break;

        }

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[0], (int) ((75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[0]).length())) * scaleConstant), (int) (70 * scaleConstant));
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1], (int) ((570 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[1]).length())) * scaleConstant), (int) (70 * scaleConstant));

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.firstPlayer && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][0][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], (int) (10 * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][1][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (int) ((710 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", (int) (48 * scaleConstant), (int) (1268 * scaleConstant));

        spriteBatch.end();

    }

    private void drawUI_RTC() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (200 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), (int) (250 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect((int) (520 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (470 * scaleConstant), 0, (int) (520 * scaleConstant), (int) (100 * scaleConstant), (int) (520 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        // Back button
        shapeRenderer.rect(0, (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (75 * scaleConstant), lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle((int) (100 * scaleConstant), (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (1280 * scaleConstant), (int) (156 * scaleConstant), (int) (1280 * scaleConstant), lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect((int) (660 * scaleConstant), (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        GlyphLayout scoreLayout = new GlyphLayout(descriptionFont, text[154]);
        descriptionFont.draw(spriteBatch, text[154], (int) (9 * scaleConstant), (int) (90 * scaleConstant));
        descriptionFont.draw(spriteBatch, scoreLayout, (int) ((715) * scaleConstant) - scoreLayout.width, (int) (90 * scaleConstant));

        // Draw user names
        switch (gameClass.scoreSystem.currentPlayer) {

            case 0:
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                GlyphLayout playerGlyph = new GlyphLayout(playerFont, gameClass.playerNames[1]);
                playerFont.draw(spriteBatch, playerGlyph, (int) ((635) * scaleConstant) - playerGlyph.width, (int) (150 * scaleConstant));
                break;

            case 1:
                playerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                GlyphLayout playerGlyph1 = new GlyphLayout(currentPlayerFont, gameClass.playerNames[1]);
                currentPlayerFont.draw(spriteBatch, playerGlyph1, (int) ((635) * scaleConstant) - playerGlyph1.width, (int) (150 * scaleConstant));
                break;

        }

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[0], (int) ((75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[0]).length())) * scaleConstant), (int) (70 * scaleConstant));
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1], (int) ((570 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[1]).length())) * scaleConstant), (int) (70 * scaleConstant));

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.firstPlayer && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][0][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], (int) (10 * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][1][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (int) ((710 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", (int) (48 * scaleConstant), (int) (1268 * scaleConstant));

        spriteBatch.end();

    }

    private void drawUI_Cricket() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (200 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), (int) (250 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect((int) (520 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (470 * scaleConstant), 0, (int) (520 * scaleConstant), (int) (100 * scaleConstant), (int) (520 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        // Back button
        shapeRenderer.rect(0, (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (75 * scaleConstant), lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle((int) (100 * scaleConstant), (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (1280 * scaleConstant), (int) (156 * scaleConstant), (int) (1280 * scaleConstant), lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect((int) (75 * scaleConstant), (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect((int) (585 * scaleConstant), (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        // Cricket Scoreboard
        shapeRenderer.rect(0, (int) (100 * scaleConstant), (int) (75 * scaleConstant), (int) (200 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect((int) (645 * scaleConstant), (int) (100 * scaleConstant), (int) (75 * scaleConstant), (int) (200 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.rect((int) (40 * scaleConstant), (int) (103 * scaleConstant), (int) (32 * scaleConstant), (int) (194 * scaleConstant), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f));
        shapeRenderer.rect((int) (647 * scaleConstant), (int) (103 * scaleConstant), (int) (32 * scaleConstant), (int) (194 * scaleConstant), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f));

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        GlyphLayout scoreLayout = new GlyphLayout(descriptionFont, text[139]);
        descriptionFont.draw(spriteBatch, text[139], (int) (9 * scaleConstant), (int) (90 * scaleConstant));
        descriptionFont.draw(spriteBatch, scoreLayout, (int) ((715) * scaleConstant) - scoreLayout.width, (int) (90 * scaleConstant));

        // Draw user names
        switch (gameClass.scoreSystem.currentPlayer) {

            case 0:
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (150 * scaleConstant), (int) (150 * scaleConstant));
                GlyphLayout playerGlyph = new GlyphLayout(playerFont, gameClass.playerNames[1]);
                playerFont.draw(spriteBatch, playerGlyph, (int) ((570) * scaleConstant) - playerGlyph.width, (int) (150 * scaleConstant));
                break;

            case 1:
                playerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (150 * scaleConstant), (int) (150 * scaleConstant));
                GlyphLayout playerGlyph1 = new GlyphLayout(currentPlayerFont, gameClass.playerNames[1]);
                currentPlayerFont.draw(spriteBatch, playerGlyph1, (int) ((570) * scaleConstant) - playerGlyph1.width, (int) (150 * scaleConstant));
                break;

        }

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[0], (int) ((75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[0]).length())) * scaleConstant), (int) (70 * scaleConstant));
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1], (int) ((570 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[1]).length())) * scaleConstant), (int) (70 * scaleConstant));

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.firstPlayer && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][0][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], (int) (85 * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][1][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (int) ((635 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        // Cricket Scoreboard Text
        String[] tempOpeningsTexts = {"20", "19", "18", "17", "16", "15", "BU"};

        for(int i = 0; i < 7; i++) {

            for(int j = 0; j < 2; j++) {

                if(gameClass.scoreSystem.getInnings()[j][i] >= 3 && gameClass.scoreSystem.getInnings()[1-j][i] < 3) {
                    scoreFontCricketOpen.draw(spriteBatch, tempOpeningsTexts[i], (int) ((10 + 680 * j) * scaleConstant), (int) ((287 - 27 * i) * scaleConstant));
                } else if(gameClass.scoreSystem.getInnings()[j][i] >= 3 && gameClass.scoreSystem.getInnings()[1-j][i] >= 3) {
                    scoreFontCricketClosed.draw(spriteBatch, tempOpeningsTexts[i], (int) ((10 + 680 * j) * scaleConstant), (int) ((287 - 27 * i) * scaleConstant));
                } else{
                    scoreFontCricket.draw(spriteBatch, tempOpeningsTexts[i], (int) ((10 + 680 * j) * scaleConstant), (int) ((287 - 27 * i) * scaleConstant));
                }

                if(gameClass.scoreSystem.getInnings()[j][i] >= 1) {
                    scoreFontCricketMarks.draw(spriteBatch, "/", (int) ((50 + 607 * j) * scaleConstant), (int) ((287 - 27 * i) * scaleConstant));
                }

                if(gameClass.scoreSystem.getInnings()[j][i] >= 2) {
                    scoreFontCricketMarks.draw(spriteBatch, "\\", (int) ((50 + 607 * j) * scaleConstant), (int) ((287 - 27 * i) * scaleConstant));
                }

                if(gameClass.scoreSystem.getInnings()[j][i] >= 3) {
                    scoreFontCricketMarks.draw(spriteBatch, "0", (int) ((50 + 607 * j) * scaleConstant), (int) ((287 - 27 * i) * scaleConstant));
                }

            }

        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", (int) (48 * scaleConstant), (int) (1268 * scaleConstant));

        spriteBatch.end();

    }

    private void drawUI_UKCricket() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle((int) (200 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), (int) (250 * scaleConstant), 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect((int) (570 * scaleConstant), 0, (int) (200 * scaleConstant), (int) (100 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        // Back button
        shapeRenderer.rect(0, (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (75 * scaleConstant), lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle((int) (100 * scaleConstant), (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (1280 * scaleConstant), (int) (156 * scaleConstant), (int) (1280 * scaleConstant), lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, (int) (100 * scaleConstant), (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        GlyphLayout scoreLayout = new GlyphLayout(descriptionFont, text[155]);
        descriptionFont.draw(spriteBatch, text[139], (int) (9 * scaleConstant), (int) (90 * scaleConstant));
        descriptionFont.draw(spriteBatch, scoreLayout, (int) ((715) * scaleConstant) - scoreLayout.width, (int) (90 * scaleConstant));

        // Draw user names
        if(gameClass.scoreSystem.getPlayerBatting() == 0) {
            // Draw user names
            switch (gameClass.scoreSystem.currentPlayer) {

                case 0:
                    currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                    GlyphLayout playerGlyph = new GlyphLayout(playerFont, gameClass.playerNames[1]);
                    playerFont.draw(spriteBatch, playerGlyph, (int) ((635) * scaleConstant) - playerGlyph.width, (int) (150 * scaleConstant));
                    break;

                case 1:
                    playerFont.draw(spriteBatch, gameClass.playerNames[0], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                    GlyphLayout playerGlyph1 = new GlyphLayout(currentPlayerFont, gameClass.playerNames[1]);
                    currentPlayerFont.draw(spriteBatch, playerGlyph1, (int) ((635) * scaleConstant) - playerGlyph1.width, (int) (150 * scaleConstant));
                    break;

            }
        } else{

            // Draw user names
            switch (gameClass.scoreSystem.currentPlayer) {

                case 0:
                    playerFont.draw(spriteBatch, gameClass.playerNames[1], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                    GlyphLayout playerGlyph = new GlyphLayout(currentPlayerFont, gameClass.playerNames[0]);
                    currentPlayerFont.draw(spriteBatch, playerGlyph, (int) ((635) * scaleConstant) - playerGlyph.width, (int) (150 * scaleConstant));
                    break;

                case 1:
                    currentPlayerFont.draw(spriteBatch, gameClass.playerNames[1], (int) (100 * scaleConstant), (int) (150 * scaleConstant));
                    GlyphLayout playerGlyph1 = new GlyphLayout(playerFont, gameClass.playerNames[0]);
                    playerFont.draw(spriteBatch, playerGlyph1, (int) ((635) * scaleConstant) - playerGlyph1.width, (int) (150 * scaleConstant));
                    break;

            }
        }
        scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1 - gameClass.scoreSystem.getPlayerBatting()], (int) ((540 - String.valueOf(gameClass.scoreSystem.getScore()[1 - gameClass.scoreSystem.getPlayerBatting()]).length() * 6) * scaleConstant), (int) (35 * scaleConstant));

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[gameClass.scoreSystem.getPlayerBatting()], (int) ((75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[gameClass.scoreSystem.getPlayerBatting()]).length())) * scaleConstant), (int) (70 * scaleConstant));
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getWickets(), (int) ((614) * scaleConstant), (int) (60 * scaleConstant));

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.getPlayerBatting() && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn - ((gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.getPlayerBatting()) ? 0 : gameClass.scoreSystem.getPlayerBatting());

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][gameClass.scoreSystem.getPlayerBatting()][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][gameClass.scoreSystem.getPlayerBatting()][i], (int) (10 * scaleConstant), (int) ((140 + 45 * i) * scaleConstant));
            }
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", (int) (48 * scaleConstant), (int) (1268 * scaleConstant));

        spriteBatch.end();

    }

    private void drawUI_Practice() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Back button
        shapeRenderer.rect(0, (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (75 * scaleConstant), lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle((int) (100 * scaleConstant), (int) (1205 * scaleConstant), (int) (100 * scaleConstant), (int) (1280 * scaleConstant), (int) (156 * scaleConstant), (int) (1280 * scaleConstant), lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, 0, (int) (60 * scaleConstant), (int) (150 * scaleConstant), darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw dart score
        for(int i = 0; i < 3; i++) {
            scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[0][0][i], (int) (10 * scaleConstant), (int) ((40 + 45 * i) * scaleConstant));
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", (int) (48 * scaleConstant), (int) (1268 * scaleConstant));

        spriteBatch.end();
    }

}
