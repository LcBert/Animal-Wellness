package com.lucab.animal_wellness.particle;

import com.lucab.animal_wellness.AnimalWellness;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = AnimalWellness.MODID, value = Dist.CLIENT)
public class RegisterParticles {
    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AnimalWellness.FLY_PARTICLE.get(), FlyParticle.Provider::new);
    }
}
