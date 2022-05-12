package ca.rttv.malum.rite;

import ca.rttv.malum.network.packet.s2c.play.MalumParticleS2CPacket;
import ca.rttv.malum.util.spirit.SpiritType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.Random;
import java.util.stream.StreamSupport;

public class EldritchSacredRite extends Rite {
    public EldritchSacredRite(Item... items) {
        super(items);
    }

    @Override
    public void onTick(BlockState state, ServerWorld world, BlockPos pos, Random random, long tick) {
        if (tick % 60 != 0) {
            return;
        }

        StreamSupport.stream(BlockPos.iterateOutwards(pos, 4, 0, 4).spliterator(), false).filter(possiblePos -> world.getBlockState(possiblePos).getBlock() instanceof Fertilizable).forEach(possiblePos -> {
            if (world.random.nextFloat() <= 0.06f) {
                BlockState state2 = world.getBlockState(possiblePos);

                for (int i = 0; i < 5 + world.random.nextInt(3); i++) {
                    state2.randomTick(world, possiblePos, world.random);
                }

                BlockPos particlePos = state2.isOpaque() ? possiblePos : possiblePos.down();
                world.getPlayers(players -> players.getWorld().isChunkLoaded(new ChunkPos(particlePos).x, new ChunkPos(particlePos).z)).forEach(players -> players.networkHandler.sendPacket(new MalumParticleS2CPacket<ClientPlayNetworkHandler>(SpiritType.SACRED_SPIRIT.color.getRGB(), particlePos.getX() + 0.5d, particlePos.getY() + 0.5d, particlePos.getZ() + 0.5d)));
            }
        });
    }

    @Override
    public void onCorruptTick(BlockState state, ServerWorld world, BlockPos pos, Random random, long tick) {
        if (tick % 60 != 0) {
            return;
        }

        world.getEntitiesByClass(AnimalEntity.class, new Box(pos.add(-4, -4, -4), pos.add(4, 4, 4)), Entity::isLiving).stream().filter(entity -> entity.getBreedingAge() != 0).limit(30).forEach(entity -> {
            if (entity.canEat() && world.random.nextFloat() <= 0.05f) {
                entity.setLoveTicks(600);
                world.getPlayers(players -> players.getWorld().isChunkLoaded(entity.getChunkPos().x, entity.getChunkPos().z)).forEach(players -> players.networkHandler.sendPacket(new MalumParticleS2CPacket<ClientPlayNetworkHandler>(SpiritType.SACRED_SPIRIT.color.getRGB(), entity.getX(), entity.getY(), entity.getZ())));
            }
        });
    }
}
