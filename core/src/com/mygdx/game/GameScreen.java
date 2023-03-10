package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

class GameScreen implements Screen {

    //screen
    private Camera camera;
    private Viewport viewport;

    //graphics
    private SpriteBatch batch;
    private TextureAtlas textureAtlas, textureAtlas1;
    private Texture explosionTexture,fallingMeteorSpecialPowerUpTextureRegion;
    private TextureRegion[] backgrounds;
    private TextureRegion playerShipTextureRegion,playerShieldTextureRegion,enemyShipTextureRegion,
            enemyShieldTextureRegion,playerLaserTextureRegion,enemyLaserTextureRegion,
            additionalLaserIconTextureRegion,laserFireRateIncreaseTextureRegion,playerMovementSpeedIncreaseTextureRegion,
            playerShieldRestoreTextureRegion,
            fallingMeteorTextureRegion,fallingMeteorDamagedTextureRegion;

    //timing
    private float[] backgroundOffsets = {0,0,0,0};
    private float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 4f;
    private float difficultyAddition = 0.2f, difficultyAdditionThreshold = 45f, difficultyAdditionTimer = 0;
    private float difficultySpawnMultiplier = 2, difficultySpawnThreshold = 120f, difficultySpawnTimer = 0;
    private boolean gameStartFirstSpawn = true;
    private float enemySpawnTimer = 0;
    private float gameTime = 0;

    //world parameters
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;

    //game objects
    private PlayerShip playerShip;
    private FallingObjects fallingObjects;
    private LinkedList <FallingObjects.FallingMeteors> fallingMeteorsLinkedList;
    private LinkedList <FallingObjects.AdditionalLaserPowerUp> additionalLaserPowerUp;
//    private FallingObjects.PlayerMovementSpeedIncrease playerMovementSpeedIncrease;
    private LinkedList <FallingObjects.LaserFireRatePowerUp> laserFireRatePowerUp;
    private LinkedList <FallingObjects.PlayerShieldRecovery> playerShieldRecoveryPowerUp;

    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;

    private int score = 0;

    //Heads-Up Display
    BitmapFont font, powerUpFont, tempDisplayFont1, tempDisplayFont2, tempDisplayFont3, tempDisplayFont4, tempDisplayFont5;
    float hudVerticalMargin, hudLeftX,hudRightX,hudCentreX,hudRow1Y,hudRow2Y,hudSectionWidth;
    boolean tempScoreDisplayCheck, tempFireRateDisplayCheck, tempLaserDisplayCheck;
    float temporaryScoreDisplayTimer, fireRateBlinkEffectTimer = 0, tempFireRateDisplayTimer,
            laserBlinkEffectTimer = 0, tempLaserDisplayTimer;
    String temporaryDisplayMessage = "+500";
    FreeTypeFontGenerator tempFontGenerator;
    FreeTypeFontGenerator.FreeTypeFontParameter tempFontParameter;

    GameScreen() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //set up the texture atlas
        textureAtlas = new TextureAtlas("images.atlas");
        textureAtlas1 = new TextureAtlas("additionalImages.atlas");

        //setting up the background
        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundMaxScrollingSpeed = (float)(WORLD_HEIGHT) / 4;

        //initialize texture regions
        playerShipTextureRegion = textureAtlas.findRegion("playerShip3_red");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyBlack4");
        playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion.flip(false,true);
        playerLaserTextureRegion = textureAtlas.findRegion("laserRed03");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserGreen05");

        //additional stuff by yours truly
        fallingMeteorTextureRegion = textureAtlas1.findRegion("meteor");
        fallingMeteorDamagedTextureRegion = textureAtlas1.findRegion("meteorDamaged");
        laserFireRateIncreaseTextureRegion = textureAtlas1.findRegion("star_bronze");
        additionalLaserIconTextureRegion = textureAtlas1.findRegion("things_bronze");
        playerShieldRestoreTextureRegion = textureAtlas1.findRegion("powerupRed_shield");
        playerMovementSpeedIncreaseTextureRegion = textureAtlas1.findRegion("powerupRed_bolt");
        fallingMeteorSpecialPowerUpTextureRegion = new Texture("meteorGrey_big3.png");
        fallingMeteorsLinkedList = new LinkedList<>();
        additionalLaserPowerUp = new LinkedList<>();
//        playerMovementSpeedIncrease = new LinkedList<>();
        laserFireRatePowerUp = new LinkedList<>();
        playerShieldRecoveryPowerUp = new LinkedList<>();
        fallingObjects = new FallingObjects();


        explosionTexture = new Texture("explosion.png");

        //set up game objects
        playerShip = new PlayerShip(48,5,WORLD_WIDTH/2,WORLD_HEIGHT/4,5,5,0.6f,4,150,
                0.8f,playerShipTextureRegion,playerShieldTextureRegion,playerLaserTextureRegion);
        enemyShipList = new LinkedList<>();

        playerLaserList = new LinkedList<>();
        enemyLaserList = new LinkedList<>();

        explosionList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();

        //prepare temp font
        tempFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        tempFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tempFontParameter.size = 55;
        tempFontParameter.borderWidth = 3.6f;
        tempFontParameter.color = Color.WHITE;
        tempFontParameter.borderColor = Color.GRAY;

        tempDisplayFont1 = tempFontGenerator.generateFont(tempFontParameter);
        tempDisplayFont2 = tempFontGenerator.generateFont(tempFontParameter);
        tempDisplayFont3 = tempFontGenerator.generateFont(tempFontParameter);
        tempDisplayFont4 = tempFontGenerator.generateFont(tempFontParameter);
        tempDisplayFont5 = tempFontGenerator.generateFont(tempFontParameter);
        Color color = tempDisplayFont1.getColor();

        //scale the font to fit world
        tempDisplayFont1.getData().setScale(0.08f);
        tempDisplayFont2.getData().setScale(0.08f);
        tempDisplayFont3.getData().setScale(0.08f);
        tempDisplayFont4.getData().setScale(0.08f);
        tempDisplayFont5.getData().setScale(0.08f);

        color.a = 0.8f;
        tempDisplayFont2.setColor(color);
        color.a = 0.6f;
        tempDisplayFont3.setColor(color);
        color.a = 0.4f;
        tempDisplayFont4.setColor(color);
        color.a = 0.2f;
        tempDisplayFont5.setColor(color);


    }

    private void prepareHUD() {
        //Create a BitmapFont from our font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1,1,1,0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        font = fontGenerator.generateFont(fontParameter);

        fontParameter.color.set(Color.TEAL);
        powerUpFont = fontGenerator.generateFont(fontParameter);

        //scale the font to fit world
        font.getData().setScale(0.08f);
        powerUpFont.getData().setScale(0.08f);

        //calculate hud margins, etc.
        hudVerticalMargin = font.getCapHeight()/2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        //scrolling background
        renderBackground(deltaTime);

        detectInput(deltaTime);
        playerShip.update(deltaTime);

        //falling objects like power ups or meteors
        updateAndRenderFallingObjects(deltaTime);

        spawnEnemyShips(deltaTime);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip,deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);
        }

        //player ship
        playerShip.draw(batch);

        //lasers
        renderLasers(deltaTime);

        //detect collisions between lasers and ships
        detectCollisions();

        //explosions
        updateAndRenderExplosions(deltaTime);

        //hud rendering
        updateAndRenderHUD(deltaTime);

        //score display when meteor is destroyed (and other stuff)
        temporaryDisplay(deltaTime,temporaryDisplayMessage, tempScoreDisplayCheck,playerShip.boundingBox.x - playerShip.boundingBox.width/2,
                playerShip.boundingBox.y + playerShip.boundingBox.height/5);


        batch.end();
    }

    private void updateAndRenderFallingObjects(float deltaTime) {
        if (gameTime > 40) {
            fallingObjects.timeSinceLastMeteor += deltaTime;
            if (fallingObjects.timeSinceLastMeteor > fallingObjects.meteorFrequency) {
                //code for drawing&rendering meteor in a linked list
                fallingMeteorsLinkedList.add(new FallingObjects.FallingMeteors(fallingMeteorTextureRegion,fallingMeteorDamagedTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));

                fallingObjects.timeSinceLastMeteor -= fallingObjects.meteorFrequency;
                fallingObjects.meteorFrequency = SpaceShooterTestGame1.random.nextFloat(0,8);
            }

            ListIterator<FallingObjects.FallingMeteors> fallingMeteorsListIterator = fallingMeteorsLinkedList.listIterator();
            while (fallingMeteorsListIterator.hasNext()) {
                FallingObjects.FallingMeteors meteors = fallingMeteorsListIterator.next();
                meteors.outerMeteor.boundingBox.y -= meteors.outerMeteor.fallingSpeed*deltaTime;
                if (meteors.outerMeteor.boundingBox.y + meteors.outerMeteor.boundingBox.height < 0) {
                    fallingMeteorsListIterator.remove();
                }
                meteors.draw(batch);
            }
        }

        if (gameTime > 180) {
            fallingObjects.timeSinceAdditionalLaserPowerUp += deltaTime;
            if (fallingObjects.firstTimeAdditionalLaserPowerUpGuaranteedSpawn) {
                fallingObjects.firstTimeAdditionalLaserPowerUpGuaranteedSpawn = false;
                additionalLaserPowerUp.add(new FallingObjects.AdditionalLaserPowerUp(fallingMeteorSpecialPowerUpTextureRegion,additionalLaserIconTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));
            }

            if (fallingObjects.timeSinceAdditionalLaserPowerUp > fallingObjects.additionalLaserPowerUpFrequency && playerShip.getPlayerPowerLevel() < 2) {
                additionalLaserPowerUp.add(new FallingObjects.AdditionalLaserPowerUp(fallingMeteorSpecialPowerUpTextureRegion,additionalLaserIconTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));
                fallingObjects.timeSinceAdditionalLaserPowerUp -= fallingObjects.additionalLaserPowerUpFrequency;
                fallingObjects.additionalLaserPowerUpFrequency = SpaceShooterTestGame1.random.nextInt(180,210);
            }

            ListIterator<FallingObjects.AdditionalLaserPowerUp> additionalLaserPowerUpListIterator = additionalLaserPowerUp.listIterator();
            while (additionalLaserPowerUpListIterator.hasNext()) {
                FallingObjects.AdditionalLaserPowerUp aLPU = additionalLaserPowerUpListIterator.next();
                aLPU.outerMeteor.boundingBox.y -= aLPU.outerMeteor.fallingSpeed/1.5f*deltaTime;
                if (aLPU.outerMeteor.boundingBox.y + aLPU.outerMeteor.boundingBox.height < 0) {
                    additionalLaserPowerUpListIterator.remove();
                }
                aLPU.draw(batch);
            }
        }

        if (gameTime > 30) {
            fallingObjects.timeSinceLaserFireRatePowerUp += deltaTime;
            if (fallingObjects.firstTimeLaserFireRatePowerUpGuaranteedSpawn) {
                fallingObjects.firstTimeLaserFireRatePowerUpGuaranteedSpawn = false;
                laserFireRatePowerUp.add(new FallingObjects.LaserFireRatePowerUp(fallingMeteorSpecialPowerUpTextureRegion,laserFireRateIncreaseTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));
            }

            if (fallingObjects.timeSinceLaserFireRatePowerUp > fallingObjects.laserFireRatePowerUpFrequency && playerShip.timeBetweenShots > 0.1f) {
                laserFireRatePowerUp.add(new FallingObjects.LaserFireRatePowerUp(fallingMeteorSpecialPowerUpTextureRegion,laserFireRateIncreaseTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));
                fallingObjects.timeSinceLaserFireRatePowerUp -= fallingObjects.laserFireRatePowerUpFrequency;
                fallingObjects.laserFireRatePowerUpFrequency = SpaceShooterTestGame1.random.nextInt(60,75);
            }

            ListIterator<FallingObjects.LaserFireRatePowerUp> laserFireRatePowerUpListIterator = laserFireRatePowerUp.listIterator();
            while (laserFireRatePowerUpListIterator.hasNext()) {
                FallingObjects.LaserFireRatePowerUp lFRPU = laserFireRatePowerUpListIterator.next();
                lFRPU.outerMeteor.boundingBox.y -= lFRPU.outerMeteor.fallingSpeed/1.5f*deltaTime;
                if (lFRPU.outerMeteor.boundingBox.y + lFRPU.outerMeteor.boundingBox.height < 0) {
                    laserFireRatePowerUpListIterator.remove();
                }
                lFRPU.draw(batch);
            }
        }
        if (gameTime > 120) {
            fallingObjects.timeSinceShieldRecoveryPowerUp += deltaTime;
            if (fallingObjects.firstTimeShieldRecoveryPowerUpGuaranteedSpawn) {
                fallingObjects.firstTimeShieldRecoveryPowerUpGuaranteedSpawn = false;
                playerShieldRecoveryPowerUp.add(new FallingObjects.PlayerShieldRecovery(playerShieldRestoreTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));
            }

            if (fallingObjects.timeSinceShieldRecoveryPowerUp > fallingObjects.playerShieldRecoveryPowerUpFrequency) {
                playerShieldRecoveryPowerUp.add(new FallingObjects.PlayerShieldRecovery(playerShieldRestoreTextureRegion,
                        SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,WORLD_HEIGHT - 5));
                fallingObjects.timeSinceShieldRecoveryPowerUp -= fallingObjects.playerShieldRecoveryPowerUpFrequency;
                fallingObjects.playerShieldRecoveryPowerUpFrequency = SpaceShooterTestGame1.random.nextInt(60,90);
            }

            ListIterator<FallingObjects.PlayerShieldRecovery> playerShieldRecoveryListIterator = playerShieldRecoveryPowerUp.listIterator();
            while (playerShieldRecoveryListIterator.hasNext()) {
                FallingObjects.PlayerShieldRecovery pSR = playerShieldRecoveryListIterator.next();
                pSR.outerMeteor.boundingBox.y -= pSR.outerMeteor.fallingSpeed/1.5f*deltaTime;
                if (pSR.outerMeteor.boundingBox.y + pSR.outerMeteor.boundingBox.height < 0) {
                    playerShieldRecoveryListIterator.remove();
                }
                pSR.draw(batch);
            }
        }
//        if () { make it tag to a unique enemy instead, make the unique enemy faster than default ones
//            playerShip.increaseMovementSpeedByTen();
//        }
    }

    private void updateAndRenderHUD(float deltaTime) {
        //render top row labels
        font.draw(batch,"Score",hudLeftX,hudRow1Y,hudSectionWidth, Align.left,false);
        font.draw(batch,"Time",hudCentreX,hudRow1Y,hudSectionWidth,Align.center,false);
        font.draw(batch,"Shield", hudRightX,hudRow1Y,hudSectionWidth,Align.right,false);

        //render second row values
        gameTime+=deltaTime;
        font.draw(batch,String.format(Locale.getDefault(),"%06d",score),hudLeftX,hudRow2Y,hudSectionWidth,Align.left,false);
        font.draw(batch,String.format(Locale.getDefault(),"%02d",Math.round(gameTime)),hudCentreX,hudRow2Y,hudSectionWidth,Align.center,false);
        font.draw(batch,String.format(Locale.getDefault(),"%02d",playerShip.shield),hudRightX,hudRow2Y,hudSectionWidth,Align.right,false);
    }

    private void spawnEnemyShips(float deltaTime) {
        enemySpawnTimer += deltaTime;
        difficultyAdditionTimer += deltaTime;
        difficultySpawnTimer += deltaTime;

        if (enemyShipList.isEmpty() && gameStartFirstSpawn){
            gameStartFirstSpawn = false;
            for (int x = 0; x < difficultySpawnMultiplier; x++) enemyShipList.add(new EnemyShip(25, 1, SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                    WORLD_HEIGHT - 5, 10, 10, 0.5f, 3, 35, 1f,
                    enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
        }

        if (enemySpawnTimer > timeBetweenEnemySpawns) {
            for (int x = 0; x < difficultySpawnMultiplier; x++) enemyShipList.add(new EnemyShip(25, 1, SpaceShooterTestGame1.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                    WORLD_HEIGHT - 5, 10, 10, 0.5f, 3, 35, 1f,
                    enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }

        if (difficultyAdditionTimer > difficultyAdditionThreshold && timeBetweenEnemySpawns > 2) {
            timeBetweenEnemySpawns -= difficultyAddition;
            difficultyAdditionTimer -= difficultyAdditionThreshold;
        }

        if (difficultySpawnTimer > difficultySpawnThreshold && difficultySpawnMultiplier < 4) {
            difficultySpawnMultiplier++;
            difficultySpawnTimer -= difficultySpawnThreshold;
        }
    }

    private void detectInput(float deltaTime) {

        float leftLimit,rightLimit,upLimit,downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float)WORLD_HEIGHT/2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        //touch input (also mouse)
        if (Gdx.input.isTouched()){
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchPixels,yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(playerShip.boundingBox.x + playerShip.boundingBox.width/2, playerShip.boundingBox.y + playerShip.boundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference/touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference/touchDistance * playerShip.movementSpeed * deltaTime;

                if (xMove > 0) xMove = Math.min(xMove,rightLimit);
                else xMove = Math.max(xMove,leftLimit);

                if (yMove > 0) yMove = Math.min(yMove,upLimit);
                else yMove = Math.max(yMove,downLimit);

                playerShip.translate(xMove,yMove);
            }

        } else {
            //keyboard input
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
                playerShip.translate(Math.min(playerShip.movementSpeed * deltaTime, rightLimit), 0f);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
                playerShip.translate(0f, Math.min(playerShip.movementSpeed * deltaTime, upLimit));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
                playerShip.translate(Math.max(-playerShip.movementSpeed * deltaTime, leftLimit), 0f);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
                playerShip.translate(0f, Math.max(-playerShip.movementSpeed * deltaTime, downLimit));
            }
        }

    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTime) {
        //strategy: determine the max distance the ship can move

        float leftLimit,rightLimit,upLimit,downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float)WORLD_HEIGHT/2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0) xMove = Math.min(xMove,rightLimit);
        else xMove = Math.max(xMove,leftLimit);

        if (yMove > 0) yMove = Math.min(yMove,upLimit);
        else yMove = Math.max(yMove,downLimit);

        enemyShip.translate(xMove,yMove);
    }

    private void temporaryDisplay(float deltaTime,String temporaryDisplayMessage,boolean scoreDisplayCheck, float xCoordinate, float yCoordinate) {
        //caveman method of fading effect for meteor points
        if (scoreDisplayCheck && gameTime - temporaryScoreDisplayTimer < 0.2f){
            tempDisplayFont1.draw(batch,temporaryDisplayMessage,xCoordinate,yCoordinate);
        } else if (scoreDisplayCheck && gameTime - temporaryScoreDisplayTimer < 0.4f) {
            tempDisplayFont2.draw(batch,temporaryDisplayMessage,xCoordinate,yCoordinate);
        } else if (scoreDisplayCheck && gameTime - temporaryScoreDisplayTimer < 0.6f) {
            tempDisplayFont3.draw(batch,temporaryDisplayMessage,xCoordinate,yCoordinate);
        } else if (scoreDisplayCheck && gameTime - temporaryScoreDisplayTimer < 0.8f) {
            tempDisplayFont4.draw(batch,temporaryDisplayMessage,xCoordinate,yCoordinate);
        } else if (scoreDisplayCheck && gameTime - temporaryScoreDisplayTimer < 1f) {
            tempDisplayFont5.draw(batch,temporaryDisplayMessage,xCoordinate,yCoordinate);
        } else if (scoreDisplayCheck && gameTime - temporaryScoreDisplayTimer > 1f){
            tempScoreDisplayCheck = false;
            temporaryScoreDisplayTimer = 0;
        }

        //fire rate power up display effect
        if (tempFireRateDisplayCheck){
            fireRateBlinkEffectTimer += deltaTime;
            if (fireRateBlinkEffectTimer > 0 && fireRateBlinkEffectTimer < 0.7f) {
                powerUpFont.draw(batch, "F i r e-R a t e", WORLD_WIDTH / 2, WORLD_HEIGHT / 20 * 11, 0, Align.center, false);
                powerUpFont.draw(batch, "U P", WORLD_WIDTH / 2, WORLD_HEIGHT / 20 * 10, 0, Align.center, false);
            } else if (fireRateBlinkEffectTimer > 1.3f) {
                fireRateBlinkEffectTimer = 0;
            }

            if (tempFireRateDisplayCheck && gameTime - tempFireRateDisplayTimer > 5) {
                tempFireRateDisplayCheck = false;
            }
        }

        //laser power up display effect
        if (tempLaserDisplayCheck){
            laserBlinkEffectTimer += deltaTime;
            if (laserBlinkEffectTimer > 0 && laserBlinkEffectTimer < 0.7f) {
                powerUpFont.draw(batch, "X T R A L A S E R", WORLD_WIDTH / 2, WORLD_HEIGHT / 30 * 16, 0, Align.center, false);
            } else if (laserBlinkEffectTimer > 1.3f) {
                laserBlinkEffectTimer = 0;
            }

            if (tempLaserDisplayCheck && gameTime - tempLaserDisplayTimer > 5) {
                tempLaserDisplayCheck = false;
            }
        }

    }

    private void detectCollisions(){
        //collision with shield recovery power up
        ListIterator<FallingObjects.PlayerShieldRecovery> playerShieldRecoveryListIterator = playerShieldRecoveryPowerUp.listIterator();
        while (playerShieldRecoveryListIterator.hasNext()) {
            FallingObjects.PlayerShieldRecovery pSR = playerShieldRecoveryListIterator.next();
            if (playerShip.intersects(pSR.outerMeteor.boundingBox)) {
                playerShip.restoreShieldByTwo();
                playerShieldRecoveryListIterator.remove();
            }
        }

        //collision with power-up meteors (fire rate)
        ListIterator<FallingObjects.LaserFireRatePowerUp> laserFireRatePowerUpListIterator = laserFireRatePowerUp.listIterator();
        while (laserFireRatePowerUpListIterator.hasNext()) {
            FallingObjects.LaserFireRatePowerUp lFRPU = laserFireRatePowerUpListIterator.next();
            if (playerShip.intersects(lFRPU.outerMeteor.boundingBox) && lFRPU.outerMeteor.fallingMeteorHP > 2) {
                if (playerShip.hitAndCheckDestroyedByMeteors()){
                    explosionList.add(new Explosion(explosionTexture, new Rectangle(playerShip.boundingBox),5f));
                    //TODO add game over screen, enlarge explosions maybe (by adding boundingBox the size of mars
                }
                laserFireRatePowerUpListIterator.remove();
            } else if (playerShip.intersects(lFRPU.outerMeteor.boundingBox) && lFRPU.outerMeteor.fallingMeteorHP < 3) {
                playerShip.increaseFireRate();
                tempFireRateDisplayCheck = true;
                tempFireRateDisplayTimer = gameTime;
                laserFireRatePowerUpListIterator.remove();
            }

            ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
            while(laserListIterator.hasNext()) {
                Laser laser = laserListIterator.next();
                if (lFRPU.outerMeteor.boundingBox.overlaps(laser.boundingBox) && lFRPU.outerMeteor.fallingMeteorHP > 2) {
                    lFRPU.outerMeteor.fallingMeteorHP--;
                    lFRPU.checkIfMeteorDamaged();
                    laserListIterator.remove();
                    break;
                }
            }
        }

        //collision with power-up meteors (additional laser)
        ListIterator<FallingObjects.AdditionalLaserPowerUp> additionalLaserPowerUpListIterator = additionalLaserPowerUp.listIterator();
        while (additionalLaserPowerUpListIterator.hasNext()) {
            FallingObjects.AdditionalLaserPowerUp aLPU = additionalLaserPowerUpListIterator.next();
            if (playerShip.intersects(aLPU.outerMeteor.boundingBox) && aLPU.outerMeteor.fallingMeteorHP > 2) {
                if (playerShip.hitAndCheckDestroyedByMeteors()){
                    explosionList.add(new Explosion(explosionTexture, new Rectangle(playerShip.boundingBox),5f));
                    //TODO add game over screen, enlarge explosions maybe (by adding boundingBox the size of mars
                }
                additionalLaserPowerUpListIterator.remove();
            } else if (playerShip.intersects(aLPU.outerMeteor.boundingBox) && aLPU.outerMeteor.fallingMeteorHP < 3) {
                playerShip.increaseNumberOfLasers();
                tempLaserDisplayCheck = true;
                tempLaserDisplayTimer = gameTime;
                additionalLaserPowerUpListIterator.remove();
            }

            ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
            while(laserListIterator.hasNext()) {
                Laser laser = laserListIterator.next();
                if (aLPU.outerMeteor.boundingBox.overlaps(laser.boundingBox) && aLPU.outerMeteor.fallingMeteorHP > 2) {
                    aLPU.outerMeteor.fallingMeteorHP--;
                    aLPU.checkIfMeteorDamaged();
                    laserListIterator.remove();
                    break;
                }
            }
        }

        //collision for meteor hitting player ship
        ListIterator<FallingObjects.FallingMeteors> fallingMeteorsListIterator = fallingMeteorsLinkedList.listIterator();
        while(fallingMeteorsListIterator.hasNext()) {
            FallingObjects.FallingMeteors meteors = fallingMeteorsListIterator.next();
            meteors.checkIfMeteorDamaged();
            if (playerShip.intersects(meteors.outerMeteor.boundingBox)) {
                //contact with player ship
                if (playerShip.hitAndCheckDestroyedByMeteors()){
                    explosionList.add(new Explosion(explosionTexture, new Rectangle(playerShip.boundingBox),5f));
                    //TODO add game over screen, enlarge explosions maybe (by adding boundingBox the size of mars
                }
                fallingMeteorsListIterator.remove();
            }

            ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
            while(laserListIterator.hasNext()) {
                Laser laser = laserListIterator.next();
                if (meteors.outerMeteor.boundingBox.overlaps(laser.boundingBox)) {
                    meteors.outerMeteor.fallingMeteorHP--;

                    if(meteors.hitAndCheckDestroyed()){
                        fallingMeteorsListIterator.remove();
                        tempScoreDisplayCheck = true;
                        temporaryScoreDisplayTimer = gameTime;
                        score += 500;
                    }
                    laserListIterator.remove();
                    break;
                    }
            }
        }

        //for each player laser, check whether it intersects an enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    //contact with enemy ship
                    if(enemyShip.hitAndCheckDestroyed()){
                        enemyShipListIterator.remove();
                        explosionList.add(new Explosion(explosionTexture, new Rectangle(enemyShip.boundingBox),0.7f));
                        score += 100;
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        //for each enemy laser, check whether it intersects the player ship
        laserListIterator = enemyLaserList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                //contact with player ship
                if (playerShip.hitAndCheckDestroyed()){
                    explosionList.add(new Explosion(explosionTexture, new Rectangle(playerShip.boundingBox),5f));
                    playerShip.shield = 5;
                    //TODO add game over screen, enlarge explosions maybe (by adding boundingBox the size of mars
                }
                laserListIterator.remove();
            }
        }

    }

    private void updateAndRenderExplosions(float deltaTime){
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }

    private void renderLasers(float deltaTime) {
        //create new lasers
        //player lasers
        if (playerShip.canFireLaser()){
            Laser[] lasers = playerShip.fireLasers();
            for (Laser laser : lasers) {
                playerLaserList.add(laser);
            }
        }
        //enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                for (Laser laser : lasers) {
                    enemyLaserList.add(laser);
                }
            }
        }

        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while(iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed*deltaTime;
            if (laser.boundingBox.y>WORLD_HEIGHT) {
                iterator.remove();
            }
        }

        iterator = enemyLaserList.listIterator();
        while(iterator.hasNext()){
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed*deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime) {

        //update position of background images
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed/16+gameTime/1200;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed/8+gameTime/648;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed/4+gameTime/342;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed/2+gameTime/180;

        //draw each background layer
        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            if (backgroundOffsets[layer] > WORLD_HEIGHT) {
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer],0, -backgroundOffsets[layer],WORLD_WIDTH,WORLD_HEIGHT);
            batch.draw(backgrounds[layer],0, -backgroundOffsets[layer]+WORLD_HEIGHT,WORLD_WIDTH,WORLD_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {

    }
}
