package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class FallingObjects {
    float fallingSpeed = 40f;
    float timeSinceLastMeteor = 0;
    float meteorFrequency = SpaceShooterTestGame1.random.nextFloat(0,5);
    float fallingMeteorHP = 6;
    Rectangle boundingBox;

    static class FallingMeteors {
        TextureRegion bigMeteor;
        TextureRegion damagedMeteor;
        float width = 12,height = 12;

        FallingObjects outerMeteor = new FallingObjects();

        public FallingMeteors(TextureRegion bigMeteor,TextureRegion damagedMeteor,float xPosition, float yPosition) {
            this.bigMeteor = bigMeteor;
            this.damagedMeteor = damagedMeteor;
            this.outerMeteor.boundingBox = new Rectangle(xPosition-width/2,yPosition-height/2,width,height);
        }

        public boolean hitAndCheckDestroyed() {
            return outerMeteor.fallingMeteorHP <= 0;
        }

        public void checkIfMeteorDamaged() {
            if (outerMeteor.fallingMeteorHP <= 2 && outerMeteor.fallingMeteorHP > 0) {
                outerMeteor.boundingBox.width = 7.5f;
                outerMeteor.boundingBox.height = 7.5f;
            }
        }


        public void draw(Batch batch) {
            if (outerMeteor.fallingMeteorHP > 2) {
                //big meteor
                batch.draw(bigMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
            } else if (outerMeteor.fallingMeteorHP > 0) {
                //small meteor
                batch.draw(damagedMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
            }
        }
    }

    static class AdditionalLaserPowerUp {
        float width, height;
    }

    static class PlayerMovementSpeedIncrease {
        float width, height;
    }

    static class LaserFireRatePowerUp {
        float width, height;
    }

    static class RestorePlayerShieldToFull {
        float width, height;
    }
}
