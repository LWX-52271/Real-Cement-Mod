// SoundRegistry.java
package lwx.cementmod;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SoundRegistry {
    public static final SoundEvent CRUSHER_OPERATING = createSoundEvent("crusher_operating");
    public static final SoundEvent CONCRETE_POUR = createSoundEvent("concrete_pour");
    public static final SoundEvent REINFORCED_COLLAPSE = createSoundEvent("reinforced_collapse");

    private static SoundEvent createSoundEvent(String name) {
        ResourceLocation location = new ResourceLocation(CementMod.MODID, name);
        return new SoundEvent(location).setRegistryName(location);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                CRUSHER_OPERATING,
                CONCRETE_POUR,
                REINFORCED_COLLAPSE
        );
    }
}