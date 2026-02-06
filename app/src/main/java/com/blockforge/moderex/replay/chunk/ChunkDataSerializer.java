package com.blockforge.moderex.replay.chunk;

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Serializes and deserializes chunk terrain data using a palette-based binary format.
 * Each chunk section (16x16x16) has its own palette of block state strings.
 * Air-only sections are skipped to save space.
 *
 * Binary format:
 *   Header: "MXCHUNK" (7 bytes magic) + version (byte) + chunkCount (int)
 *   Per chunk column:
 *     chunkX (int), chunkZ (int), sectionCount (byte)
 *     Per non-air section:
 *       sectionY (byte, -4 to 19 for overworld)
 *       paletteSize (short)
 *       Per palette entry: blockStateString (UTF)
 *       blockData: 4096 palette indices (byte if palette<=256, short otherwise)
 */
public class ChunkDataSerializer {

    private static final byte[] MAGIC = "MXCHUNK".getBytes();
    private static final byte FORMAT_VERSION = 1;
    private static final int SECTION_SIZE = 16;
    private static final int BLOCKS_PER_SECTION = SECTION_SIZE * SECTION_SIZE * SECTION_SIZE; // 4096

    // Minecraft 1.21.1 world height: -64 to 319 = 384 blocks = 24 sections
    private static final int MIN_SECTION_Y = -4;  // -64 / 16
    private static final int MAX_SECTION_Y = 19;  // 319 / 16

    /**
     * Serialize a list of chunk snapshots to a gzipped binary file.
     */
    public static void serialize(List<ChunkSnapshot> snapshots, Path outputFile) throws IOException {
        try (GZIPOutputStream gzip = new GZIPOutputStream(Files.newOutputStream(outputFile));
             DataOutputStream dos = new DataOutputStream(gzip)) {

            // Write header
            dos.write(MAGIC);
            dos.writeByte(FORMAT_VERSION);
            dos.writeInt(snapshots.size());

            for (ChunkSnapshot snapshot : snapshots) {
                serializeChunkColumn(dos, snapshot);
            }
        }
    }

    /**
     * Serialize a single chunk column.
     */
    private static void serializeChunkColumn(DataOutputStream dos, ChunkSnapshot snapshot) throws IOException {
        dos.writeInt(snapshot.getX());
        dos.writeInt(snapshot.getZ());

        // First pass: count non-air sections
        List<Integer> nonAirSections = new ArrayList<>();
        for (int sectionY = MIN_SECTION_Y; sectionY <= MAX_SECTION_Y; sectionY++) {
            if (!isSectionEmpty(snapshot, sectionY)) {
                nonAirSections.add(sectionY);
            }
        }

        dos.writeByte(nonAirSections.size());

        // Second pass: serialize each non-air section
        for (int sectionY : nonAirSections) {
            serializeSection(dos, snapshot, sectionY);
        }
    }

    /**
     * Check if a section is entirely air.
     */
    private static boolean isSectionEmpty(ChunkSnapshot snapshot, int sectionY) {
        int baseY = sectionY * SECTION_SIZE;
        for (int y = 0; y < SECTION_SIZE; y++) {
            int worldY = baseY + y;
            if (worldY < (MIN_SECTION_Y * SECTION_SIZE) || worldY > ((MAX_SECTION_Y + 1) * SECTION_SIZE - 1)) {
                continue;
            }
            for (int x = 0; x < SECTION_SIZE; x++) {
                for (int z = 0; z < SECTION_SIZE; z++) {
                    Material mat = snapshot.getBlockType(x, worldY, z);
                    if (mat != Material.AIR && mat != Material.VOID_AIR && mat != Material.CAVE_AIR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Serialize a single section (16x16x16) with palette compression.
     */
    private static void serializeSection(DataOutputStream dos, ChunkSnapshot snapshot, int sectionY) throws IOException {
        dos.writeByte(sectionY);

        int baseY = sectionY * SECTION_SIZE;

        // Build palette
        Map<String, Integer> paletteMap = new LinkedHashMap<>();
        String[] blockStates = new String[BLOCKS_PER_SECTION];
        int index = 0;

        for (int y = 0; y < SECTION_SIZE; y++) {
            int worldY = baseY + y;
            for (int z = 0; z < SECTION_SIZE; z++) {
                for (int x = 0; x < SECTION_SIZE; x++) {
                    String blockState;
                    if (worldY < (MIN_SECTION_Y * SECTION_SIZE) || worldY > ((MAX_SECTION_Y + 1) * SECTION_SIZE - 1)) {
                        blockState = "minecraft:air";
                    } else {
                        BlockData blockData = snapshot.getBlockData(x, worldY, z);
                        blockState = blockData.getAsString(true); // Include block state properties
                    }

                    blockStates[index] = blockState;
                    paletteMap.putIfAbsent(blockState, paletteMap.size());
                    index++;
                }
            }
        }

        // Write palette
        int paletteSize = paletteMap.size();
        dos.writeShort(paletteSize);
        for (String state : paletteMap.keySet()) {
            dos.writeUTF(state);
        }

        // Write block indices
        boolean useShort = paletteSize > 256;
        for (int i = 0; i < BLOCKS_PER_SECTION; i++) {
            int paletteIndex = paletteMap.get(blockStates[i]);
            if (useShort) {
                dos.writeShort(paletteIndex);
            } else {
                dos.writeByte(paletteIndex);
            }
        }
    }

    /**
     * Deserialize chunk data from a gzipped binary file.
     * Returns a list of ChunkColumnData objects.
     */
    public static List<ChunkColumnData> deserialize(Path inputFile) throws IOException {
        try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(inputFile));
             DataInputStream dis = new DataInputStream(gzip)) {

            // Read and verify header
            byte[] magic = new byte[MAGIC.length];
            dis.readFully(magic);
            if (!Arrays.equals(magic, MAGIC)) {
                throw new IOException("Invalid chunk data file: bad magic bytes");
            }

            byte version = dis.readByte();
            if (version != FORMAT_VERSION) {
                throw new IOException("Unsupported chunk data version: " + version);
            }

            int chunkCount = dis.readInt();
            List<ChunkColumnData> columns = new ArrayList<>(chunkCount);

            for (int i = 0; i < chunkCount; i++) {
                columns.add(deserializeChunkColumn(dis));
            }

            return columns;
        }
    }

    /**
     * Deserialize a single chunk column.
     */
    private static ChunkColumnData deserializeChunkColumn(DataInputStream dis) throws IOException {
        int chunkX = dis.readInt();
        int chunkZ = dis.readInt();
        int sectionCount = dis.readByte() & 0xFF;

        List<SectionData> sections = new ArrayList<>(sectionCount);
        for (int i = 0; i < sectionCount; i++) {
            sections.add(deserializeSection(dis));
        }

        return new ChunkColumnData(chunkX, chunkZ, sections);
    }

    /**
     * Deserialize a single section.
     */
    private static SectionData deserializeSection(DataInputStream dis) throws IOException {
        int sectionY = dis.readByte();

        // Read palette
        int paletteSize = dis.readShort() & 0xFFFF;
        String[] palette = new String[paletteSize];
        for (int i = 0; i < paletteSize; i++) {
            palette[i] = dis.readUTF();
        }

        // Read block indices
        boolean useShort = paletteSize > 256;
        int[] blocks = new int[BLOCKS_PER_SECTION];
        for (int i = 0; i < BLOCKS_PER_SECTION; i++) {
            blocks[i] = useShort ? (dis.readShort() & 0xFFFF) : (dis.readByte() & 0xFF);
        }

        return new SectionData(sectionY, palette, blocks);
    }

    /**
     * Read raw bytes of a chunk data file for WebSocket transmission.
     * Returns the raw gzipped bytes as a byte array.
     */
    public static byte[] readRawBytes(Path chunkFile) throws IOException {
        return Files.readAllBytes(chunkFile);
    }

    // ========== Data Classes ==========

    /**
     * Represents a full chunk column (16x384x16 area).
     */
    public static class ChunkColumnData {
        public final int chunkX;
        public final int chunkZ;
        public final List<SectionData> sections;

        public ChunkColumnData(int chunkX, int chunkZ, List<SectionData> sections) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.sections = sections;
        }
    }

    /**
     * Represents a single 16x16x16 section within a chunk.
     */
    public static class SectionData {
        public final int sectionY;
        public final String[] palette;
        public final int[] blocks; // 4096 palette indices (y*256 + z*16 + x order)

        public SectionData(int sectionY, String[] palette, int[] blocks) {
            this.sectionY = sectionY;
            this.palette = palette;
            this.blocks = blocks;
        }

        /**
         * Get block state at local coordinates within the section.
         */
        public String getBlockState(int x, int y, int z) {
            int index = y * 256 + z * 16 + x;
            return palette[blocks[index]];
        }
    }
}
