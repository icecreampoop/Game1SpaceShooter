package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class FallingObjects {
    float additionalLaserPowerUpFrequency = SpaceShooterTestGame1.random.nextInt(45,90);
    float timeSinceAdditionalLaserPowerUp = 0;
    float fallingSpeed = 40f;
    float timeSinceLastMeteor = 0;
    float meteorFrequency = SpaceShooterTestGame1.random.nextFloat(0,5);
    float fallingMeteorHP = 6;
    TextureRegion bigMeteor;
    Texture powerUpMeteor;
    Rectangle boundingBox;
    boolean firstTimeAdditionalLaserPowerUpGuaranteedSpawn = true, powerUpTransitionDone = false;

    static class FallingMeteors {
        TextureRegion damagedMeteor;
        float width = 12,height = 12;

        FallingObjects outerMeteor = new FallingObjects();

        public FallingMeteors(TextureRegion bigMeteor,TextureRegion damagedMeteor,float xPosition, float yPosition) {
            this.outerMeteor.bigMeteor = bigMeteor;
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
                batch.draw(outerMeteor.bigMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
            } else if (outerMeteor.fallingMeteorHP > 0) {
                //small meteor
                batch.draw(damagedMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
            }
        }
    }

    static class AdditionalLaserPowerUp {
        float width = 12, height = 12;
        TextureRegion laserPowerUpIcon;
        FallingObjects outerMeteor = new FallingObjects();

        public AdditionalLaserPowerUp (Texture bigMeteor, TextureRegion laserPowerUpIcon, float xPosition, float yPosition) {
            this.outerMeteor.powerUpMeteor = bigMeteor;
            this.laserPowerUpIcon = laserPowerUpIcon;
            this.outerMeteor.boundingBox = new Rectangle(xPosition-width/2,yPosition-height/2,width,height);
        }

        public void checkIfMeteorDamaged() {
            if (outerMeteor.fallingMeteorHP < 3) {
                if (!outerMeteor.powerUpTransitionDone) {
                    outerMeteor.boundingBox.x = outerMeteor.boundingBox.x + outerMeteor.boundingBox.width / 4;
                    outerMeteor.boundingBox.y = outerMeteor.boundingBox.y + outerMeteor.boundingBox.height / 4;
                    outerMeteor.boundingBox.width = 6;
                    outerMeteor.boundingBox.height = 6;
                    outerMeteor.powerUpTransitionDone = true;
                }
            }
        }

        public void draw(Batch batch) {
            if (outerMeteor.fallingMeteorHP > 2) {
                //meteor shell with inner power up
                batch.draw(outerMeteor.powerUpMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
                batch.draw(laserPowerUpIcon,outerMeteor.boundingBox.x + outerMeteor.boundingBox.width/4,outerMeteor.boundingBox.y + outerMeteor.boundingBox.height/4,6,6);
            } else {
                //inner power up
                batch.draw(laserPowerUpIcon,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
            }
        }
    }

//    static class PlayerMovementSpeedIncrease {
//        float width, height = 8;
//        FallingObjects outerMeteor = new FallingObjects();
//
//        public void draw(Batch batch) {
//            if (outerMeteor.fallingMeteorHP > 2) {
//                //big meteor
//                batch.draw(outerMeteor.bigMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
//            } else if (outerMeteor.fallingMeteorHP > 0) {
//                //small meteor
//                batch.draw(damagedMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
//            }
//        }
//    }

//    static class LaserFireRatePowerUp {
//        float width, height = 8;
//        FallingObjects outerMeteor = new FallingObjects();
//
//        public void draw(Batch batch) {
//            if (outerMeteor.fallingMeteorHP > 2) {
//                //big meteor
//                batch.draw(outerMeteor.bigMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
//            } else if (outerMeteor.fallingMeteorHP > 0) {
//                //small meteor
//                batch.draw(damagedMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
//            }
//        }
//    }
//
//    static class RestorePlayerShieldToFull {
//        float width, height = 8;
//        FallingObjects outerMeteor = new FallingObjects();
//
//        public void draw(Batch batch) {
//            if (outerMeteor.fallingMeteorHP > 2) {
//                //big meteor
//                batch.draw(outerMeteor.bigMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
//            } else if (outerMeteor.fallingMeteorHP > 0) {
//                //small meteor
//                batch.draw(damagedMeteor,outerMeteor.boundingBox.x,outerMeteor.boundingBox.y,outerMeteor.boundingBox.width,outerMeteor.boundingBox.height);
//            }
//        }
//    }
}
