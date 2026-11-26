package io.github.zhinjio.basilscatgenetics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.animal.feline.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasilsCatGenetics implements ModInitializer {
	public static final String MOD_ID = "basils_cat_genetics";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CatGenes.init();
		// Every cat gets genes the first time it is loaded or spawned.
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (entity instanceof Cat cat) {
				Cats.genes(cat);
			}
		});
		LOG.info("Basil's Cat Genetics loaded");
	}
}
