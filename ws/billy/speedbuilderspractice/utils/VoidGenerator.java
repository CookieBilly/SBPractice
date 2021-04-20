 

package ws.billy.speedbuilderspractice.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class VoidGenerator extends ChunkGenerator
{
    public ChunkGenerator.ChunkData generateChunkData(final World world, final Random random, final int n, final int n2, final ChunkGenerator.BiomeGrid biomeGrid) {
        final ChunkGenerator.ChunkData chunkData = this.createChunkData(world);
        if (0 >= n << 4 && 0 < n + 1 << 4 && 0 >= n2 << 4 && 0 < n2 + 1 << 4) {
            chunkData.setBlock(0, 0, 0, Material.BEDROCK);
        }
        return chunkData;
    }
    
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.5, 1.0, 0.5);
    }
}
