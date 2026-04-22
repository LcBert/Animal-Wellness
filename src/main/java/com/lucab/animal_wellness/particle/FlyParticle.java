package com.lucab.animal_wellness.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlyParticle extends TextureSheetParticle {
    private final double xCenter;
    private final double yCenter;
    private final double zCenter;
    private final double radius;
    private final double radiusY;
    private final float baseQuadSize;

    public FlyParticle(ClientLevel level, double x, double y, double z,
                       SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.xCenter = x;
        this.yCenter = y;
        this.zCenter = z;
        this.radius = 0.5;
        this.radiusY = 0.15;

        double angle = this.random.nextDouble() * Math.PI * 2;

        double startX = xCenter + Math.cos(angle) * radius;
        double startZ = zCenter + Math.sin(angle) * radius;

        this.setPos(startX, y, startZ);

        this.xo = startX;
        this.yo = y;
        this.zo = startZ;

        this.lifetime = 60;
        this.gravity = 0.0F;
        this.quadSize *= 0.25f;
        this.baseQuadSize = this.quadSize;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        if (this.age % 5 == 0) {
            this.xd += (this.random.nextDouble() - 0.5) * 0.08;
            this.yd += (this.random.nextDouble() - 0.5) * 0.04;
            this.zd += (this.random.nextDouble() - 0.5) * 0.08;
        }

        double dx = this.x - xCenter;
        double dz = this.z - zCenter;
        double distSqXZ = dx * dx + dz * dz;

        if (distSqXZ > radius * radius) {
            this.xd += (xCenter - this.x) * 0.05;
            this.zd += (zCenter - this.z) * 0.05;
        }

        double dy = this.y - yCenter;
        if (Math.abs(dy) > radiusY) {
            this.yd += (yCenter - this.y) * 0.1;
        }

        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.95;
        this.yd *= 0.95;
        this.zd *= 0.95;

        float startShrinkingAt = 0.8f;
        float agePct = (float) this.age / (float) this.lifetime;

        if (agePct > startShrinkingAt) {
            float shrinkFactor = (agePct - startShrinkingAt) / (1.0f - startShrinkingAt);
            this.quadSize = this.baseQuadSize * (1.0f - shrinkFactor);
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                                 double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed) {
            return new FlyParticle(level, x, y, z, spriteSet, xSpeed, ySpeed, zSpeed);
        }
    }
}
