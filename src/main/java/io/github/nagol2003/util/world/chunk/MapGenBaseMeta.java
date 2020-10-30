package io.github.nagol2003.util.world.chunk;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class MapGenBaseMeta {
	
	/**
	 * The number of Chunks to gen-check in any given direction.
	 */
	protected int range = 8;

	/**
	 * The RNG used by the MapGen classes.
	 */
	protected Random rand = new Random();

	/**
	 * This world object.
	 */
	protected World worldObj;

	public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
		this.worldObj = world;
		this.rand.setSeed(world.getSeed());
		final long r0 = this.rand.nextLong();
		final long r1 = this.rand.nextLong();

		for (int x0 = chunkX - this.range; x0 <= chunkX + this.range; ++x0) {
			for (int y0 = chunkZ - this.range; y0 <= chunkZ + this.range; ++y0) {
				final long randX = x0 * r0;
				final long randZ = y0 * r1;
				this.rand.setSeed(randX ^ randZ ^ world.getSeed());
				this.recursiveGenerate(world, x0, y0, chunkX, chunkZ, primer);
			}
		}
	}

	/**
	 * Recursively called by generate() (generate) and optionally by itself.
	 */
	protected void recursiveGenerate(World world, int xChunkCoord, int zChunkCoord, int origXChunkCoord, int origZChunkCoord, ChunkPrimer primer) {
	}

}
