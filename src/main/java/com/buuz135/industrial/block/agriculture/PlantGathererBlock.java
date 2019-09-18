package com.buuz135.industrial.block.agriculture;

import com.buuz135.industrial.block.IndustrialBlock;
import com.buuz135.industrial.block.agriculture.tile.PlantGathererTile;
import com.hrznstudio.titanium.api.IFactory;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

public class PlantGathererBlock extends IndustrialBlock<PlantGathererTile> {

    public PlantGathererBlock() {
        super("plant_gatherer", Properties.from(Blocks.STONE), PlantGathererTile.class);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public IFactory<PlantGathererTile> getTileEntityFactory() {
        return PlantGathererTile::new;
    }
}
