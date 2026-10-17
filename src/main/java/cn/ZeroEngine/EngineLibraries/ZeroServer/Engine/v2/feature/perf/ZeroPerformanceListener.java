package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.perf;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.ZeroSF;

import java.util.concurrent.atomic.AtomicInteger;

public class ZeroPerformanceListener implements Listener {

    private final ZeroPerformanceManager manager;
    private final AtomicInteger chunkLoadCount = new AtomicInteger(0);
    private final AtomicInteger chunkUnloadCount = new AtomicInteger(0);
    private final AtomicInteger spawnCount = new AtomicInteger(0);
    private long lastReportTick = 0;

    public ZeroPerformanceListener(ZeroPerformanceManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        chunkLoadCount.incrementAndGet();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        chunkUnloadCount.incrementAndGet();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        spawnCount.incrementAndGet();

        World world = event.getLocation().getWorld();
        if (world == null) return;

        int loaded = world.getLoadedChunks().length;
        int threshold = 5000;
        if (loaded > threshold && spawnCount.get() % 100 == 0) {
            ZeroSF sf = ZeroSF.sf();
            sf.warn("[Perf] High chunk count in %s: %d chunks", world.getName(), loaded);
        }
    }

    public int getChunkLoadCount() { return chunkLoadCount.get(); }
    public int getChunkUnloadCount() { return chunkUnloadCount.get(); }
    public int getSpawnCount() { return spawnCount.get(); }

    public void resetCounters() {
        chunkLoadCount.set(0);
        chunkUnloadCount.set(0);
        spawnCount.set(0);
    }
}
