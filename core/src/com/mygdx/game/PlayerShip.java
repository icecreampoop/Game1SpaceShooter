package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship{

    private float playerPowerLevel = 0;

    public PlayerShip(float movementSpeed, int shield, float xPosition, float yPosition, float width, float height, float laserWidth, float laserHeight, float laserMovementSpeed, float timeBetweenShots, TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        super(movementSpeed, shield, xPosition, yPosition, width, height, laserWidth, laserHeight, laserMovementSpeed, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);
    }

    public void increaseNumberOfLasers() {
        playerPowerLevel++;
    }

    public float getPlayerPowerLevel() {
        return playerPowerLevel;
    }

    public void restoreShieldToTen() {
        shield = 10;
    }

    public void increaseFireRate() {
        if (timeBetweenShots > 0.05f) {
            timeBetweenShots -= 0.1f;
        }
    }

    public void increaseMovementSpeedByTen() {
        if (movementSpeed < 88f) {
            movementSpeed += 10;
        }
    }

    @Override
    public Laser[] fireLasers() {
        if (playerPowerLevel == 0) {
            Laser[] laser = new Laser[2];
            laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.255f, boundingBox.y + boundingBox.height * 0.5f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
            laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.745f, boundingBox.y + boundingBox.height * 0.5f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);

            timeSinceLastShot = 0;

            return laser;
        } else if (playerPowerLevel == 1) {
            Laser[] laser = new Laser[3];
            laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.1f, boundingBox.y + boundingBox.height * 0.35f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
            laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.9f, boundingBox.y + boundingBox.height * 0.35f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
            laser[2] = new Laser(boundingBox.x + boundingBox.width * 0.5f, boundingBox.y + boundingBox.height * 1.05f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);

            timeSinceLastShot = 0;

            return laser;
        } else {
            Laser[] laser = new Laser[4];
            laser[0] = new Laser(boundingBox.x + boundingBox.width * 0.03f, boundingBox.y + boundingBox.height * 0.2f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
            laser[1] = new Laser(boundingBox.x + boundingBox.width * 0.97f, boundingBox.y + boundingBox.height * 0.2f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
            laser[2] = new Laser(boundingBox.x + boundingBox.width * 0.35f, boundingBox.y + boundingBox.height * 0.75f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);
            laser[3] = new Laser(boundingBox.x + boundingBox.width * 0.65f, boundingBox.y + boundingBox.height * 0.75f, laserWidth, laserHeight, laserMovementSpeed, laserTextureRegion);

            timeSinceLastShot = 0;

            return laser;
        }
    }
}
