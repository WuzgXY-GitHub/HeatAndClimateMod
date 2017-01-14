package defeatedcrow.hac.main.worldgen;

import java.util.Random;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.main.MainInit;
import defeatedcrow.hac.main.config.WorldGenConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenOres implements IWorldGenerator {

	private static int sedPar = WorldGenConfig.depositGen[0];
	private static int kiesPar = WorldGenConfig.depositGen[1];
	private static int vinePar = WorldGenConfig.depositGen[2];
	private static int lavaPar = WorldGenConfig.depositGen[3];
	private static int vugsPar = WorldGenConfig.depositGen[4];

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {

		int genDim1 = world.provider.getDimension();
		if (chunkX > 3000 || chunkZ > 3000)
			// あまり遠いと生成しない
			return;

		int chunk2X = chunkX << 4;
		int chunk2Z = chunkZ << 4;
		int count = 4;

		if ((genDim1 != 1 && genDim1 != -1)) {
			int[] genY = {
					5, 30, 70, 120
			};
			for (int i = 0; i < count; i++) {
				/* 計4回のチャンス */
				int posX = chunk2X + random.nextInt(16);
				int posY = genY[i] + random.nextInt(20 + 10 * i);
				int posZ = chunk2Z + random.nextInt(16);
				BlockPos pos = new BlockPos(posX, posY, posZ);
				Biome biome = world.getBiomeForCoordsBody(pos);

				if (posY > 140) {
					if (random.nextInt(100) < sedPar * 2) {
						generateSediment(world, random, pos);
					}
				} else if (posY > 70) {
					if (random.nextInt(100) < sedPar) {
						if (posY > 90 && BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MOUNTAIN)) {
							generateSediment(world, random, pos);
						} else if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY)) {
							generateSandSediment(world, random, pos);
						}
					}
				} else if (posY < 60 && posY > 30) {
					if (BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MOUNTAIN)
							|| BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.OCEAN)) {
						if (random.nextInt(100) < kiesPar) {
							generateKieslager(world, random, pos);
						}
					} else if (random.nextInt(100) < vinePar) {
						generateQuartzVine(world, random, pos);
					}
				} else if (posY < 30 && posY > 19 && random.nextInt(100) < vugsPar) {
					generateVugs(world, random, pos);
				} else if (posY <= 19 && random.nextInt(100) < lavaPar) {
					generateUnderlava(world, random, pos);
				}
			}
		}

	}

	static boolean isPlaceable(Block block) {
		if (block == Blocks.STONE)
			return true;
		if (block == Blocks.GRAVEL)
			return true;
		if (block == Blocks.DIRT)
			return true;
		if (block == Blocks.SANDSTONE)
			return true;
		if (block == Blocks.SAND)
			return true;

		return false;
	}

	/*
	 * 堆積岩。山岳の高高度に生成する。
	 * これを集めるだけでも生きてはいけるが、銅や亜鉛はここにはない。
	 */
	public void generateSediment(World world, Random rand, BlockPos pos) {
		int h = rand.nextInt(6) + 1; // 2-7
		int r = h + 1;
		BlockSet[] gen = new BlockSet[h];
		for (int i = 0; i < h; i++) {
			if (i == 0) {
				gen[i] = new BlockSet(MainInit.ores, 0);
			} else if (i == h - 1) {
				gen[i] = rand.nextInt(2) == 0 ? new BlockSet(MainInit.ores, 1) : new BlockSet(Blocks.COAL_ORE, 0);
			} else {
				if (i <= h / 2) {
					gen[i] = rand.nextInt(2) == 0 ? new BlockSet(Blocks.COAL_ORE, 0) : new BlockSet(MainInit.ores, 0);
				} else {
					gen[i] = rand.nextInt(2) == 0 ? new BlockSet(MainInit.ores, 1) : new BlockSet(Blocks.STONE, 3);
				}
			}
		}

		BlockPos min = new BlockPos(pos.add(-r, -h + 2, -r));
		BlockPos max = new BlockPos(pos.add(r, 1, r));
		Iterable<BlockPos> itr = pos.getAllInBox(min, max);
		for (BlockPos p1 : itr) {
			Block block = world.getBlockState(p1).getBlock();
			double d1 = Math.sqrt(pos.distanceSq(p1.getX(), pos.getY(), p1.getZ()));
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block) && d1 < r) {
				int height = max.getY() - p1.getY();
				BlockSet add = gen[height];
				if (add.block == Blocks.STONE) {
					int j = rand.nextInt(50);
					if (j == 0) {
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(3), 2);
					} else if (j < 10) {
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(2), 2);
					} else {
						world.setBlockState(p1, add.getState(), 2);
					}
				} else {
					world.setBlockState(p1, add.getState(), 2);
				}
			}
		}

		// DCLogger.debugLog("Ore gen! Sediment:" + pos.getX() + "," + pos.getY() + "," + pos.getZ()
		// + ", size: " + h);
	}

	/*
	 * 堆積岩。砂漠かサバンナにのみ生成。
	 * 料理に必要な岩塩などがある。
	 */
	public void generateSandSediment(World world, Random rand, BlockPos pos) {
		int h = rand.nextInt(5) + 1; // 2-6
		int r = h + 1;
		BlockSet[] gen = new BlockSet[h];
		for (int i = 0; i < h; i++) {
			if (i == 0) {
				gen[i] = new BlockSet(MainInit.ores_2, 0);
			} else {
				if (i <= h / 2) {
					gen[i] = rand.nextBoolean() ? new BlockSet(MainInit.ores_2, 1) : new BlockSet(MainInit.ores_2, 0);
				} else {
					gen[i] = rand.nextBoolean() ? new BlockSet(MainInit.ores_2, 1) : new BlockSet(MainInit.ores_2, 2);
				}
			}
		}

		BlockPos min = new BlockPos(pos.add(-r, -h + 2, -r));
		BlockPos max = new BlockPos(pos.add(r, 1, r));
		Iterable<BlockPos> itr = pos.getAllInBox(min, max);
		for (BlockPos p1 : itr) {
			Block block = world.getBlockState(p1).getBlock();
			double d1 = Math.sqrt(pos.distanceSq(p1.getX(), pos.getY(), p1.getZ()));
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block) && d1 < r) {
				int height = max.getY() - p1.getY();
				BlockSet add = gen[height];
				world.setBlockState(p1, add.getState(), 2);
			}
		}
	}

	/*
	 * キースラガー。山岳と海底に生成。
	 * 本来は玄武岩など塩基性寄りのはずなんだが...
	 */
	public void generateKieslager(World world, Random rand, BlockPos pos) {
		int h = rand.nextInt(4) + 3; // 3-6
		int r = h + rand.nextInt(3);
		BlockSet[] gen = new BlockSet[h];
		for (int i = 0; i < h; i++) {
			if (i == 0 || i == h - 1) {
				gen[i] = new BlockSet(Blocks.STONE, 5); // andesite
			} else {
				// 亜鉛 - 銅 - 鉄
				if (i >= h / 2) {
					gen[i] = rand.nextInt(3) > 0 ? new BlockSet(MainInit.ores, 6) : new BlockSet(MainInit.ores, 4);
				} else {
					gen[i] = rand.nextInt(2) == 0 ? new BlockSet(MainInit.ores, 8) : new BlockSet(MainInit.ores, 4);
				}
			}
		}

		// 柱状に生成する
		BlockPos min = new BlockPos(pos.add(0, -h + 2, -r));
		BlockPos max = new BlockPos(pos.add(h, 1, r));
		if (rand.nextInt(2) == 0) {
			min = new BlockPos(pos.add(-r, -h + 2, 0));
			max = new BlockPos(pos.add(r, 1, h));
		}
		Iterable<BlockPos> itr = pos.getAllInBox(min, max);
		for (BlockPos p1 : itr) {
			Block block = world.getBlockState(p1).getBlock();
			double d1 = Math.sqrt(pos.distanceSq(p1.getX(), pos.getY(), p1.getZ()));
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block) && d1 < r) {
				int height = max.getY() - p1.getY();
				BlockSet add = gen[height];
				int j = rand.nextInt(30);
				// ニッケル、錫の生成チャンス
				if (j == 0) {
					world.setBlockState(p1, MainInit.ores.getStateFromMeta(7), 2);
				} else if (j == 1) {
					world.setBlockState(p1, MainInit.ores_2.getStateFromMeta(4), 2);
				} else if (j > 20) {
					world.setBlockState(p1, Blocks.STONE.getStateFromMeta(5), 2);
				} else {
					world.setBlockState(p1, add.getState(), 2);
				}
			}
		}

		// DCLogger.debugLog("Ore gen! Kieslager:" + pos.getX() + "," + pos.getY() + "," +
		// pos.getZ() + ", size: " + h);
	}

	/*
	 * 石英脈。キースラガーが生成しないバイオームに出現。
	 * サンドイッチ状の層構造とライン状の鉱脈を伴う。
	 * 出現率は低く、生成も固有鉱石はない。山がないときの救済用。
	 */
	public void generateQuartzVine(World world, Random rand, BlockPos pos) {
		int h = rand.nextInt(5) + 4; // 4-8
		int r = h + 1;
		BlockSet[] gen = new BlockSet[h];
		for (int i = 0; i < h; i++) {
			if (i == 0 || i == h - 1) {
				gen[i] = new BlockSet(Blocks.STONE, 1); // granite
			} else if (i == 1 || i == h - 2) {
				gen[i] = new BlockSet(MainInit.ores, 9);
			} else {
				// 優先度は 亜鉛>鉄>銀>金
				int j = rand.nextInt(8);
				switch (j) {
				case 0:
					gen[i] = new BlockSet(MainInit.ores, 10);
					break;
				case 1:
					gen[i] = new BlockSet(MainInit.ores, 11);
					break;
				case 2:
					gen[i] = new BlockSet(MainInit.ores, 12);
					break;
				case 3:
				case 4:
				case 5:
					gen[i] = new BlockSet(MainInit.ores, 8);
					break;
				default:
					gen[i] = new BlockSet(MainInit.ores, 15);
					break;
				}
			}
		}

		// 円柱状に生成
		BlockPos min = new BlockPos(pos.add(-r, -h + 2, -r));
		BlockPos max = new BlockPos(pos.add(r, 1, r));
		Iterable<BlockPos> itr = pos.getAllInBox(min, max);
		for (BlockPos p1 : itr) {
			double d1 = Math.sqrt(pos.distanceSq(p1.getX(), pos.getY(), p1.getZ()));
			Block block = world.getBlockState(p1).getBlock();
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block) && d1 < r) {
				int height = max.getY() - p1.getY();
				BlockSet add = gen[height];
				world.setBlockState(p1, add.getState(), 2);
			}
		}

		// 直線状の鉱脈を伴う
		for (int k = 0; k < 2; k++) {
			BlockPos line = new BlockPos(pos.add(rand.nextInt(5) - 2, rand.nextInt(5) - 2, rand.nextInt(5) - 2));
			BlockPos min2 = new BlockPos(line.north(5));
			BlockPos max2 = new BlockPos(line.south(5));
			if (rand.nextInt(2) == 0) {
				min2 = new BlockPos(line.north(5));
				max2 = new BlockPos(line.south(5));
				Iterable<BlockPos> itr2 = pos.getAllInBox(min2, max2);
				for (BlockPos p2 : itr2) {
					Block block = world.getBlockState(p2).getBlock();
					if (p2.getY() > 1 && p2.getY() < world.getActualHeight() && isPlaceable(block)) {
						int m = k == 0 ? 8 : 5;
						world.setBlockState(p2, MainInit.ores.getStateFromMeta(m), 2);
					}
				}
			}
		}

		// DCLogger.debugLog("Ore gen! Vine:" + pos.getX() + "," + pos.getY() + "," + pos.getZ() +
		// ", size: " + h);
	}

	/*
	 * マグマ底床。マグマ帯の周囲に生成する。
	 * 非常に探しづらいが、ニッケルの入手手段のひとつになる。
	 */
	public void generateUnderlava(World world, Random rand, BlockPos pos) {
		int h = rand.nextInt(2) + 2; // 2-3
		int r = h + 1;

		// 円柱状に生成
		BlockPos min = new BlockPos(pos.add(-r, -h + 2, -r));
		BlockPos max = new BlockPos(pos.add(r, 1, r));
		Iterable<BlockPos> itr = pos.getAllInBox(min, max);
		for (BlockPos p1 : itr) {
			double d1 = Math.sqrt(p1.distanceSq(pos));
			int d = r + 1 - MathHelper.floor_double(d1);
			if (d1 > d) {
				continue;
			}
			Block block = world.getBlockState(p1).getBlock();
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block)) {
				int j = rand.nextInt(5);
				if (j == 0) {
					world.setBlockState(p1, MainInit.ores.getStateFromMeta(7), 2);
				} else if (j < 3) {
					world.setBlockState(p1, MainInit.ores.getStateFromMeta(5), 2);
				} else {
					world.setBlockState(p1, MainInit.ores_2.getStateFromMeta(3), 2);
				}
			}
		}

		// DCLogger.debugLog("Ore gen! Underlava:" + pos.getX() + "," + pos.getY() + "," +
		// pos.getZ() + ", size: " + h);
	}

	/*
	 * 晶洞。マグマの上に生成する。
	 * 球状に生成し、中央に空洞を持つ。
	 * なかなか探しにくいが、引き当てるとバニラ宝石類が大量に手に入る。
	 */
	public void generateVugs(World world, Random rand, BlockPos pos) {
		int h = rand.nextInt(3) + 4; // 4-6
		if (rand.nextInt(10) == 0) {
			h = 8; // 希にあたりがある
		}
		// 球状に生成
		BlockPos min = new BlockPos(pos.add(-h, -h, -h));
		BlockPos max = new BlockPos(pos.add(h, h, h));
		Iterable<BlockPos> itr = pos.getAllInBox(min, max);
		for (BlockPos p1 : itr) {
			double d1 = Math.sqrt(p1.distanceSq(pos));
			int r = h + 1 - MathHelper.floor_double(d1);
			if (r < -0.0D) {
				continue;
			}
			Block block = world.getBlockState(p1).getBlock();
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block)) {
				if (r < 2.0D) {
					world.setBlockState(p1, Blocks.STONE.getStateFromMeta(1), 2);
				} else if (r < 4.0D) {
					world.setBlockState(p1, MainInit.ores.getStateFromMeta(9), 2);
				} else if (r < 5.0D) {
					int j = rand.nextInt(15);
					switch (j) {
					case 0:
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(12), 2);
						break;
					case 1:
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(13), 2);
						break;
					case 2:
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(14), 2);
						break;
					case 3:
						world.setBlockState(p1, MainInit.ores_2.getStateFromMeta(5), 2);
						break;
					case 4:
					case 5:
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(10), 2);
						break;
					case 6:
					case 7:
						world.setBlockToAir(p1);
						break;
					default:
						world.setBlockState(p1, MainInit.ores.getStateFromMeta(9), 2);
					}
				} else {
					world.setBlockToAir(p1);
				}
			}
		}

		// DCLogger.debugLog("Ore gen! Vugs:" + pos.getX() + "," + pos.getY() + "," + pos.getZ() +
		// ", size: " + h);
	}

	// 以下はバニラ改変
	public void generateStoneLayer(World world, Random rand, int chunkX, int chunkZ) {
		// 花崗岩帯をY40位に追加
		DCLogger.debugLog("stone");
		int h2 = 40 + rand.nextInt(5);
		int r2 = 3 + rand.nextInt(2);
		BlockPos min2 = new BlockPos(new BlockPos(chunkX, h2, chunkZ));
		BlockPos max2 = new BlockPos(new BlockPos(chunkX + 15, h2 + r2, chunkZ + 15));
		Iterable<BlockPos> itr2 = BlockPos.getAllInBox(min2, max2);
		for (BlockPos p1 : itr2) {
			Block block = world.getBlockState(p1).getBlock();
			if (p1.getY() > 1 && p1.getY() < world.getActualHeight() && isPlaceable(block)) {
				BlockSet add = new BlockSet(Blocks.STONE, 1);
				world.setBlockState(p1, add.getState(), 2);
			}
		}
	}

}
