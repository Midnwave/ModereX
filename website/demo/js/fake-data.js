/**
 * ModereX Panel - Data Generation Module
 * Provides sample datasets for the interactive panel preview
 */

// ─── Utility Helpers ─────────────────────────────────────────────────────────

function rand(min, max) {
  return Math.floor(Math.random() * (max - min)) + min;
}

function randomFloat(min, max) {
  return Math.random() * (max - min) + min;
}

function randomChoice(arr) {
  return arr[rand(0, arr.length)];
}

function weightedChoice(items, weights) {
  const total = weights.reduce((a, b) => a + b, 0);
  let r = Math.random() * total;
  for (let i = 0; i < items.length; i++) {
    r -= weights[i];
    if (r <= 0) return items[i];
  }
  return items[items.length - 1];
}

function randomTimestamp(daysAgo = 30) {
  return Date.now() - Math.random() * daysAgo * 24 * 60 * 60 * 1000;
}

function recentTimestamp(hoursAgo = 6) {
  return Date.now() - Math.random() * hoursAgo * 60 * 60 * 1000;
}

function generateRandomToken() {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  return Array.from({ length: 50 }, () => chars[rand(0, chars.length)]).join('');
}

function randomUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
  });
}

function randomIP() {
  // Realistic private/public ranges
  const ranges = [
    () => `192.168.${rand(0, 256)}.${rand(1, 255)}`,
    () => `10.${rand(0, 256)}.${rand(0, 256)}.${rand(1, 255)}`,
    () => `${rand(24, 223)}.${rand(1, 255)}.${rand(1, 255)}.${rand(1, 255)}`,
    () => `${rand(50, 200)}.${rand(10, 250)}.${rand(1, 255)}.${rand(1, 255)}`
  ];
  return randomChoice(ranges)();
}

function parseDuration(dur) {
  if (!dur || dur === 'perm') return 0;
  const match = dur.match(/^(\d+)([hdwmy])$/);
  if (!match) return 0;
  const value = parseInt(match[1]);
  const units = { h: 3600000, d: 86400000, w: 604800000, m: 2592000000, y: 31536000000 };
  return value * (units[match[2]] || 0);
}

function shuffle(arr) {
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = rand(0, i + 1);
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

// ─── Username Pool (170+ realistic names) ────────────────────────────────────

const USERNAMES = [
  // Classic / OG style
  'Notch', 'Herobrine', 'Steve', 'Alex', 'Philza', 'Technoblade',
  'Skeppy', 'BadBoyHalo', 'Sapnap', 'Punz', 'Purpled', 'Ranboo',
  'Tubbo', 'Quackity', 'Foolish', 'HBomb94', 'Antfrost', 'Awesamdude',

  // PvP / Competitive
  'xXDragonSlayerXx', 'PvPGod2024', 'L0rdOfBlades', 'CriticalHitz',
  'BladeMaster99', 'ComboKing_', 'NoDebuffPvP', 'StrafeKing',
  'W_Tapper', 'RodGod_MC', 'PearlClutch', 'FireAspectII',
  'SharpIV', 'PotGod', 'EasyClaps_', 'GGNoRe',
  'CleanCombo', 'Jitterclick', 'ButterflyPvP', 'DragClickKing',

  // Cute / Aesthetic
  'ItzLily', 'StardustGirl', 'MoonlightMC', 'CherryBlossom_',
  'PixelDaisy', 'FairyDust_', 'SunshineVibes', 'CloudySkies_',
  'RainbowCraft', 'ButterflyWings', 'PetalStorm', 'CottonCandy_MC',
  'SparkleQueen', 'CozyVibes_', 'SoftPetals', 'GlitterFrost',
  'LavenderMist', 'HoneyBee_MC', 'SnowflakeStar', 'AuroraBorealis_',

  // Edgy / Dark
  'DarkShadow_X', 'xVoidWalker', 'SilentReaper', 'NightmareKing',
  'GhostRider_MC', 'DemonSlayer99', 'ShadowStrike', 'VenomFang',
  'DeathWhisper', 'BloodMoon_MC', 'PhantomBlade', 'SoulHunter_',
  'DarkEnergy_X', 'ObsidianKnight', 'WarpedSoul', 'NetherbornX',

  // Builder / Creative
  'MasterBuilder', 'ArchitectMC', 'PixelArtist_', 'RedstoneGenius',
  'BlockByBlock', 'CraftingTable_', 'BuilderJoe', 'CreativeKing_',
  'TerrainEditor', 'StructureBlock', 'BlueprintMC', 'DesignCraft',

  // Standard / Normal names
  'CoolKid_MC', 'JackTheGamer', 'SkyWarrior', 'TheRealMike',
  'GamerGuy123', 'MinecraftPro', 'DiamondMiner42', 'IronGolem_',
  'EnderDragon_', 'CreeperKiller', 'ZombieSlayer7', 'WitchHunter_',
  'VillagerTrader', 'PiglinBarterMC', 'NetherExplorer', 'EndBuster',
  'OverworldKing', 'CaveSpelunker', 'DeepslateDigger', 'CopperGolem_',

  // Number / Year suffixed
  'Player_2024', 'xGamer2023', 'MC_Veteran_09', 'OGplayer2012',
  'NewPlayer_25', 'Rookie2024', 'VeteranMC_15', 'SinceAlpha_',
  'BetaTester01', 'Day1Player', 'Season4Main', 'Grinder247',

  // YouTube / Streamer style
  'iTzNova_YT', 'FrostByte_TTV', 'AceGaming_', 'zFluxYT',
  'EpicNinja_Live', 'StreamerBTW', 'ContentKing_YT', 'ClipMaster_',
  'HighlightReel', 'MontageGod_', 'TrickShotter_', 'ProClips_YT',

  // Food / Animal themed
  'CookieCrafter', 'PizzaLord_MC', 'TacoMaster99', 'WaffleKing_',
  'DonutDestroyer', 'BaconBits_MC', 'CakeBoss_', 'SushiRoll_MC',
  'WolfPack_Alpha', 'AxolotlLover', 'ParrotPirate', 'FoxCrafter_',
  'BeeKeeper_MC', 'CatLady_MC', 'TurtleMaster', 'PandaHugger_',

  // Meme / Funny
  'BigBrainTime', 'PotatoFarmer42', 'SleepyBoi_', 'WhyAmIHere_MC',
  'JustVibing_', 'TouchGrass_', 'NoLifeGamer', 'AFK_Andy',
  'LagMaster3000', 'Ping999_', 'RubberBanding', '404PlayerNotFound',
  'StackOverflow_', 'CtrlZ_', 'AltF4Master', 'F3PlusT',

  // Region / Culture
  'SakuraMC_JP', 'KiwiCrafter_NZ', 'MapleSyrup_CA', 'OutbackMiner',
  'FjordBuilder', 'AlpineExplorer', 'DesertNomad_', 'TropicalMC',
  'ArcticFox_MC', 'CoralReefDiver', 'VolcanoMiner', 'JungleExplorer_',

  // Misc realistic
  'xKryptonite', 'EchoValley_', 'NeonLights_MC', 'QuantumLeap_',
  'SolarFlare_X', 'CometTail_MC', 'NebulaCraft', 'GalaxySurfer_',
  'ThunderStrike', 'LightningBolt_', 'StormChaser_MC', 'TornadoAlley',
  'TsunamiWave', 'EarthQuake_MC', 'MagmaBlock_', 'FrostWalker_',
  'SilkTouch_', 'FortuneIII_MC', 'Looting3', 'Efficiency5',
  'Unbreaking3', 'Mending_MC', 'InfinityBow', 'PowerV',
  'xBlazeRunner', 'IronPickaxe_', 'GoldenApple_MC', 'TotemPop_',
  'ShulkerBox_', 'ElytrafIyer', 'TridentKing_', 'CrossbowMC'
];

// ─── Staff Members ──────────────────────────────────────────────────────────

const STAFF_NAMES = [
  'xKryptonite',      // Owner
  'SilentReaper',     // Admin
  'MasterBuilder',    // Admin
  'StardustGirl',     // Sr. Moderator
  'BladeMaster99',    // Sr. Moderator
  'ItzLily',          // Moderator
  'CoolKid_MC',       // Moderator
  'SkyWarrior',       // Moderator
  'RedstoneGenius',   // Jr. Moderator
  'AxolotlLover',     // Helper
  'AutoMod'           // System
];

const STAFF_RANKS = {
  'xKryptonite': 'Owner',
  'SilentReaper': 'Admin',
  'MasterBuilder': 'Admin',
  'StardustGirl': 'Sr. Mod',
  'BladeMaster99': 'Sr. Mod',
  'ItzLily': 'Moderator',
  'CoolKid_MC': 'Moderator',
  'SkyWarrior': 'Moderator',
  'RedstoneGenius': 'Jr. Mod',
  'AxolotlLover': 'Helper',
  'AutoMod': 'System'
};

// ─── Punishment Reasons ─────────────────────────────────────────────────────

const BAN_REASONS = [
  'Hacking - KillAura detected',
  'Hacking - Fly/NoClip detected',
  'Hacking - Speed hack detected',
  'Hacking - Anti-KB detected',
  'Hacking - Reach hack detected',
  'Hacking - X-Ray resource pack',
  'Hacking - AutoClicker (20+ CPS)',
  'Hacking - Scaffold detected',
  'Hacking - Timer/FastBreak',
  'Griefing - Destroyed spawn area',
  'Griefing - Lava cast on player builds',
  'Griefing - TNT destruction in claims',
  'Severe toxicity / harassment',
  'Doxxing / sharing personal information',
  'DDoS threats',
  'Ban evasion (alt account)',
  'Chargeback on store purchase',
  'Exploiting duplication glitch',
  'Exploiting server vulnerability',
  'Impersonating staff member',
  'Real-world trading',
  'Advertising (repeat offender)',
  'Inappropriate skin/cape',
  'Botting / automated actions'
];

const MUTE_REASONS = [
  'Spam - Repeated messages',
  'Spam - Character flooding',
  'Advertising other servers',
  'Excessive profanity',
  'Toxicity in chat',
  'Disrespecting staff',
  'Sharing server IPs',
  'Encouraging rule-breaking',
  'Caps abuse (repeat)',
  'Non-English in global chat (warned)',
  'Bypassing chat filter',
  'Inappropriate topic discussion',
  'Minimodding excessively',
  'Provocative behavior in chat',
  'Begging for items/ranks repeatedly'
];

const WARN_REASONS = [
  'Mild spam in chat',
  'Light toxicity - first offense',
  'Caps lock abuse',
  'Minor advertising (mentioned server name)',
  'Inappropriate language',
  'Disrespectful behavior',
  'AFK farming (first warning)',
  'Building too close to another player',
  'Chat filter bypass attempt',
  'Misuse of /helpop',
  'Trolling new players',
  'Nickname policy violation',
  'Spawn camping',
  'Team griefing',
  'Ignoring staff instructions'
];

const KICK_REASONS = [
  'Refusing to follow staff instructions',
  'AFK in PvP arena',
  'Client modification warning',
  'Connection issues - please rejoin',
  'Server restart preparation',
  'Suspicious activity investigation',
  'Lag machine detected nearby',
  'Excessive entity spawning',
  'Unstable connection (packet spam)',
  'Verification required',
  'Account security concern',
  'Combat logging repeatedly'
];

// ─── Chat Messages Pool (800+ templates) ────────────────────────────────────

const CHAT_MESSAGES = [
  // Greetings / General
  'hey', 'hey everyone', 'hello', 'hi', 'sup', 'yo', 'whats up', 'good morning',
  'good night everyone', 'gn', 'gm', 'im back', 'brb', 'afk', 'back',
  'hey guys', 'anyone on?', 'whos online', 'how is everyone',
  'just got on', 'finally home', 'just woke up lol',
  'ok im heading off for the night', 'see yall tomorrow', 'cya later',
  'bye everyone', 'gotta go eat dinner', 'school tomorrow rip',

  // Gameplay - General
  'gg', 'gg wp', 'good game', 'nice', 'ez', 'close fight', 'good try',
  'that was intense', 'wow that was close', 'lets goooo', 'finally',
  'how do i get to the nether?', 'where is the shop?', 'wheres spawn?',
  'how do i set a home?', 'how do i claim land?', 'can someone tp me to spawn',
  'what are the server rules?', 'is pvp on?', 'is there keep inventory?',
  'what version is the server?', 'how do i join a faction?',
  'is there a discord?', 'whats the discord link?',

  // Trading / Economy
  'anyone want to trade?', 'selling diamonds', 'buying iron',
  'who has blaze rods?', 'ill trade you 32 iron for 16 gold',
  'how much for a diamond pickaxe?', 'selling enchanted books',
  'anyone have mending books?', 'looking for fortune 3 book',
  'selling netherite scrap', 'buying emeralds 2 for 1 diamond',
  'does anyone have a silk touch pick i can borrow?',
  'trading god apple for netherite ingot', 'how much is an elytra worth?',
  'selling shulker boxes', 'buying totems of undying',
  'anyone selling a trident?', 'who has an efficiency 5 pick?',
  'trading 64 diamonds for a netherite helmet',
  'ill pay 10 diamonds for someone to help me build',

  // Compliments / Reactions
  'nice base!', 'your house looks sick', 'that build is insane',
  'how did you build that', 'thats so cool', 'teach me how to build like that',
  'your skin is cool', 'nice armor', 'where did you get that cape?',
  'that farm is massive', 'respect for that grind',
  'thats a lot of diamonds', 'how long did that take you',
  'you guys are so good at pvp', 'that was a nice combo',
  'clean hit', 'nice shot with the bow', 'cracked at the game',

  // Help / Questions
  'can someone help me?', 'i need help', 'how do you craft this?',
  'what does this enchantment do?', 'how do i enchant stuff?',
  'where do i find diamonds?', 'best level for diamonds?',
  'y level -59 right?', 'whats the best food in the game?',
  'how do i make a brewing stand?', 'how do you make potions?',
  'i cant find my base', 'im lost lol', 'which way is north?',
  'how do i use the map?', 'is there a wiki?',
  'what biome is this?', 'how do you get a wither skull?',
  'how many blaze rods do i need?', 'how do i find a stronghold?',
  'where do endermen spawn the most?', 'how do i get sponges?',
  'whats the fastest way to level up?', 'how do xp farms work?',
  'can you explain how redstone repeaters work?',

  // Casual / Social
  'lol', 'lmao', 'haha', 'lolol', 'thats funny', 'bruh',
  'bro what', 'wait really?', 'no way', 'are you serious',
  'thats crazy', 'i didnt know that', 'oh cool', 'thanks',
  'ty', 'thank you', 'np', 'no problem', 'youre welcome',
  'sure', 'ok', 'alright', 'sounds good', 'bet', 'for sure',
  'oh nice', 'sweet', 'lets go', 'pog', 'W', 'massive W',

  // Frustration (clean)
  'i just died', 'i lost all my stuff', 'noooo', 'rip',
  'i fell in lava with all my diamonds', 'creeper blew up my house',
  'a skeleton shot me off a cliff', 'enderman took my block',
  'i forgot to set a home', 'the nether is so dangerous',
  'keep dying to this wither skeleton', 'phantoms are so annoying',
  'drowned keep killing me', 'cant find any diamonds',
  'been mining for an hour and nothing', 'this is taking forever',
  'the lag is real', 'my frames are dropping', 'anyone else lagging?',
  'i keep rubber banding', 'my ping is so high today',

  // PvP
  'wanna 1v1?', 'anyone wanna fight?', 'meet me at arena',
  'good fight', 'gf', 'rematch?', 'lets go again',
  'that was a good combo', 'nice strafe', 'you almost had me',
  'im at half heart lol', 'i had no pots', 'out of golden apples',
  'nice pearl clutch', 'that rod combo tho', 'clean w tap',
  'how do you click so fast', 'what mouse do you use',
  'i need to practice pvp more', 'anyone want to practice combos',

  // Building
  'what should i build?', 'building a castle rn', 'working on my base',
  'almost done with my house', 'does anyone have wood?',
  'i need like 3 stacks of stone', 'anyone have glass?',
  'what blocks look good together?', 'should i use deepslate or blackstone?',
  'building an aquarium', 'making a villager trading hall',
  'my farm is finally working', 'just finished my storage room',
  'redstone is so confusing', 'anyone good at redstone?',
  'making an automatic sugarcane farm', 'this sorting system is huge',

  // Events / Activities
  'when is the next event?', 'is there a build competition?',
  'anyone want to do the parkour?', 'race to the end?',
  'team up for the boss fight?', 'need 2 more for the dungeon',
  'who wants to raid the end city?', 'exploring the deep dark',
  'going to the warden room', 'lets go to the nether fortress',
  'anyone want to go treasure hunting?', 'found a shipwreck',
  'there is a village nearby', 'found a desert temple',
  'woodland mansion coords?', 'ocean monument near spawn',

  // Server specific
  'love this server', 'best server ive played on', 'how long has this server been up?',
  'the staff here are great', 'thanks for helping admin',
  'this plugin is really cool', 'the anticheat is good',
  'how do i report someone?', 'is there a report command?',
  'can a mod help me real quick?', 'need staff assistance',
  'someone is griefing near me', 'there is a hacker in pvp',
  'can someone check this player?', 'pretty sure they are using xray',

  // Random Minecraft knowledge
  'did you know you can crawl with trapdoors', 'you can use boats to go down fast',
  'foxes can hold items', 'bees die after they sting you', 'parrots dance to music',
  'the warden cant see you if you sneak', 'endermen cant teleport to half slabs',
  'TNT duping still works', 'you can beat the game without mining',
  'netherite floats in lava', 'gold tools are actually the fastest',
  'copper oxidizes over time', 'frogs eat magma cubes',

  // Party / Group play
  'party me', 'inv me', 'add me to the party', 'im in your team',
  'on my way', 'omw', 'wait for me', 'tp to me', 'tpa', 'accept my tp',
  'im at coords 500 64 -200', 'meet at the portal', 'come to my base',
  'follow me', 'this way', 'over here', 'im in the cave',
  'im at the farm', 'at spawn', 'near the shop',

  // Time-related
  'its night time again', 'everyone sleep please', 'someone sleep',
  'skip the night', 'one person isnt sleeping', 'who isnt in bed',
  'its raining again', 'thunderstorm incoming', 'clear weather pls',

  // Economy / Ranks
  'how do i get money?', 'whats the best way to earn coins?',
  'how much money do you have?', 'i just bought a rank',
  'is VIP worth it?', 'what perks does MVP get?',
  'the shop prices are fair', 'selling to the server shop',
  'how do i access the auction house?', 'checking the ah',
  'listed it on auction', 'outbid on the auction',

  // Miscellaneous
  'what texture pack do you use?', 'anyone use optifine?',
  'my shader is lagging', 'sodium is better than optifine',
  'using fabric or forge?', 'what mods are allowed?',
  'is litematica allowed?', 'can i use minimaps?',
  'whats the render distance?', 'the server has 20 tps nice',
  'mob spawns seem low', 'too many mobs in this area',
  'the end is reset right?', 'when does the world border expand?',

  // More casual conversation
  'what time is it for you guys?', 'im in EST', 'its midnight here lol',
  'playing until 3am again', 'should probably go to sleep',
  'one more hour then i gotta go', 'weekend grind session',
  'been playing all day', 'just started playing a week ago',
  'ive been on this server for like 2 years', 'OG player here',
  'i remember when the server had 5 people on',
  'the server has grown so much', 'server feels alive today',

  // Quick responses
  'yeah', 'yep', 'nope', 'nah', 'idk', 'maybe', 'probably',
  'true', 'facts', 'agreed', 'same', 'mood', 'real',
  'why', 'how', 'when', 'what', 'where',
  'oh', 'ah', 'hmm', 'interesting', 'wait what',

  // Longer messages
  'i just spent 3 hours building this farm and a creeper blew it up',
  'does anyone know how to make a zero tick sugarcane farm on this version',
  'i think the best strategy is to go to the nether first and get blaze rods',
  'my base is at the mushroom island so no mobs spawn which is really nice',
  'the server should add a player shop area where we can set up our own stores',
  'can someone help me move my stuff to my new base its a lot of items',
  'just found a spawner in a dungeon does anyone want to help me build an xp farm',
  'i have been looking for a jungle biome for hours and i still cant find one',
  'the fishing rod pvp technique is actually really overpowered once you learn it'
];

// ─── AutoMod Rule Names ─────────────────────────────────────────────────────

const AUTOMOD_RULES = [
  { name: 'Anti-Spam', desc: 'Repeated/rapid messages', severity: 'medium' },
  { name: 'Anti-Advertising', desc: 'Server IP or link sharing', severity: 'high' },
  { name: 'Toxicity Filter', desc: 'Profanity and slurs', severity: 'high' },
  { name: 'Caps Lock Filter', desc: 'Excessive capitalization', severity: 'low' },
  { name: 'Anti-Flood', desc: 'Character spam/flooding', severity: 'medium' },
  { name: 'Link Filter', desc: 'Unapproved URLs', severity: 'medium' },
  { name: 'Unicode Filter', desc: 'Suspicious unicode characters', severity: 'low' },
  { name: 'Repeat Message Filter', desc: 'Identical messages within timeframe', severity: 'low' },
  { name: 'Anti-Swear', desc: 'Blocked word detection', severity: 'medium' },
  { name: 'Anti-Mention Spam', desc: 'Mass player mentions', severity: 'medium' },
  { name: 'Command Spam Filter', desc: 'Rapid command execution', severity: 'medium' },
  { name: 'Chat Cooldown', desc: 'Message rate limiting', severity: 'low' },
  { name: 'Anti-Bot', desc: 'Automated message patterns', severity: 'high' },
  { name: 'Join Flood Protection', desc: 'Rapid join/leave cycling', severity: 'high' }
];

const AUTOMOD_TRIGGERED_MESSAGES = [
  'join my server play.something.net',
  'FREE RANKS AT WWW DOT SOMETHING DOT COM',
  'AAAAAAAAAAAAAAAAAAA',
  'hahahahahahahahahahahahahahahaha',
  'follow me on twitch at twitch.tv/something',
  '!!!!!!!!!!!!!!!!!!!!!!!!',
  'BUY CHEAP ITEMS AT MINESHOP DOT NET',
  'THIS SERVER IS SO BAD GO PLAY ON ANOTHER ONE',
  'lol lol lol lol lol lol lol lol',
  'selling accounts cheap dm me on discord',
  'CHECK OUT MY YOUTUBE CHANNEL',
  'EVERYONE REPORT THIS PLAYER HE IS HACKING',
  'i hate this stupid game',
  'join hypixel its way better',
  'L L L L L L L L L L',
  'get good kid you are garbage',
  'im going to lag the server',
  'CAPS CAPS CAPS EVERYTHING IS CAPS',
  'anyone want free stuff go to website dot com',
  'this admin is so unfair ban me i dont care'
];

// ─── Activity Log Action Templates ─────────────────────────────────────────

const ACTIVITY_TEMPLATES = [
  { action: 'Banned {target}', kind: 'punishment', detail: 'ban' },
  { action: 'Temp-banned {target}', kind: 'punishment', detail: 'tempban' },
  { action: 'Unbanned {target}', kind: 'punishment', detail: 'unban' },
  { action: 'Muted {target}', kind: 'punishment', detail: 'mute' },
  { action: 'Temp-muted {target}', kind: 'punishment', detail: 'tempmute' },
  { action: 'Unmuted {target}', kind: 'punishment', detail: 'unmute' },
  { action: 'Warned {target}', kind: 'punishment', detail: 'warn' },
  { action: 'Kicked {target}', kind: 'punishment', detail: 'kick' },
  { action: 'Cleared chat for {target}', kind: 'moderation', detail: 'clearchat' },
  { action: 'Froze {target}', kind: 'moderation', detail: 'freeze' },
  { action: 'Unfroze {target}', kind: 'moderation', detail: 'unfreeze' },
  { action: 'Vanished', kind: 'staff', detail: 'vanish' },
  { action: 'Teleported to {target}', kind: 'staff', detail: 'tp' },
  { action: 'Checked {target} inventory', kind: 'moderation', detail: 'invsee' },
  { action: 'Reviewed evidence for {target}', kind: 'moderation', detail: 'evidence' },
  { action: 'Added {target} to watchlist', kind: 'moderation', detail: 'watchlist' },
  { action: 'Removed {target} from watchlist', kind: 'moderation', detail: 'watchlist' },
  { action: 'Viewed chat logs for {target}', kind: 'moderation', detail: 'chatlog' },
  { action: 'Rollback applied for {target}', kind: 'moderation', detail: 'rollback' },
  { action: 'Edited punishment for {target}', kind: 'punishment', detail: 'edit' },
  { action: 'Escalated case for {target}', kind: 'moderation', detail: 'escalate' },
  { action: 'Toggled lockdown mode', kind: 'admin', detail: 'lockdown' },
  { action: 'Reloaded AutoMod configuration', kind: 'admin', detail: 'reload' },
  { action: 'Updated AutoMod rule', kind: 'admin', detail: 'automod' },
  { action: 'Exported punishment data', kind: 'admin', detail: 'export' }
];

// ─── Generate Players ───────────────────────────────────────────────────────

const usedNames = new Set();
const playerPool = shuffle(USERNAMES);

function pickUniqueName() {
  for (const name of playerPool) {
    if (!usedNames.has(name)) {
      usedNames.add(name);
      return name;
    }
  }
  // Fallback: generate a random one
  const n = 'Player_' + rand(1000, 9999);
  usedNames.add(n);
  return n;
}

// Decide statuses: ~12 online, ~5 afk, rest offline for a realistic 30-50 concurrent feel
const STATUS_POOL = [];
for (let i = 0; i < 14; i++) STATUS_POOL.push('online');
for (let i = 0; i < 6; i++) STATUS_POOL.push('afk');
for (let i = 0; i < 150; i++) STATUS_POOL.push('offline');

const PLAYERS = [];
const PLAYER_MAP = {};

for (let i = 0; i < 170; i++) {
  const name = pickUniqueName();
  const uuid = randomUUID();
  const status = i < STATUS_POOL.length ? STATUS_POOL[i] : 'offline';
  const isBedrock = Math.random() < 0.18;
  const joinedDaysAgo = rand(1, 365);

  // Generate realistic IP history (1-5 IPs)
  const primaryIP = randomIP();
  const ipCount = weightedChoice([1, 2, 3, 4, 5], [40, 30, 15, 10, 5]);
  const ips = [primaryIP];
  for (let j = 1; j < ipCount; j++) ips.push(randomIP());

  // Nickname history (0-3 past names)
  const nickCount = weightedChoice([0, 1, 2, 3], [50, 30, 15, 5]);
  const nicks = [];
  for (let j = 0; j < nickCount; j++) {
    nicks.push(USERNAMES[rand(0, USERNAMES.length)] + '_old');
  }

  const player = {
    id: uuid,
    name: isBedrock ? '.' + name : name,
    uuid: uuid,
    ip: primaryIP,
    platform: isBedrock ? 'Bedrock' : 'Java',
    status: status,
    lastSeen: status === 'offline'
      ? randomTimestamp(rand(1, 30))
      : Date.now() - rand(0, status === 'afk' ? 1800000 : 600000),
    firstJoin: Date.now() - joinedDaysAgo * 86400000,
    flags: weightedChoice([0, 0, 0, 1, 1, 2, 3, 4, 5, 6], [30, 20, 15, 12, 8, 6, 4, 3, 1, 1]),
    nicks: nicks,
    ips: ips,
    activePunishments: [],
    playtime: rand(1, 500) * 3600000, // 1-500 hours in ms
    server: randomChoice(['survival', 'survival', 'skyblock', 'factions', 'creative', 'hub'])
  };

  PLAYERS.push(player);
  PLAYER_MAP[uuid] = player;
}

// Sort so online players appear first in listings
PLAYERS.sort((a, b) => {
  const order = { online: 0, afk: 1, offline: 2 };
  return (order[a.status] || 2) - (order[b.status] || 2);
});

// ─── Generate Punishments (200+) ────────────────────────────────────────────

const DURATIONS_BAN = ['1h', '3h', '6h', '12h', '1d', '3d', '7d', '14d', '30d', 'perm'];
const DURATIONS_MUTE = ['15m', '30m', '1h', '3h', '6h', '12h', '1d', '3d', '7d', '14d', '30d'];

const PUNISHMENTS = [];
let caseCounter = 1;

// Build punishment distribution: more warns/mutes, fewer bans
const punishmentDistribution = [];
for (let i = 0; i < 45; i++) punishmentDistribution.push('BAN');
for (let i = 0; i < 55; i++) punishmentDistribution.push('MUTE');
for (let i = 0; i < 65; i++) punishmentDistribution.push('WARN');
for (let i = 0; i < 45; i++) punishmentDistribution.push('KICK');

for (let i = 0; i < 210; i++) {
  const type = punishmentDistribution[i] || randomChoice(['BAN', 'MUTE', 'WARN', 'KICK']);
  const player = PLAYERS[rand(0, PLAYERS.length)];
  const staff = randomChoice(STAFF_NAMES);
  const createdAt = randomTimestamp(30);

  let reason, duration;
  switch (type) {
    case 'BAN':
      reason = randomChoice(BAN_REASONS);
      duration = randomChoice(DURATIONS_BAN);
      break;
    case 'MUTE':
      reason = randomChoice(MUTE_REASONS);
      duration = randomChoice(DURATIONS_MUTE);
      break;
    case 'WARN':
      reason = randomChoice(WARN_REASONS);
      duration = null;
      break;
    case 'KICK':
      reason = randomChoice(KICK_REASONS);
      duration = null;
      break;
  }

  // Determine if still active
  let active = false;
  let expiresAt = null;
  let revokedAt = null;
  let revokedBy = null;

  if (type === 'BAN' || type === 'MUTE') {
    if (duration === 'perm') {
      expiresAt = -1;
      active = Math.random() > 0.35; // 65% of perms still active
    } else if (duration) {
      expiresAt = createdAt + parseDuration(duration);
      active = expiresAt > Date.now();
    }

    // Some were manually revoked
    if (active && Math.random() < 0.15) {
      active = false;
      revokedAt = createdAt + rand(3600000, 7 * 86400000);
      revokedBy = randomChoice(STAFF_NAMES.filter(s => s !== 'AutoMod'));
    }
  }

  const caseId = 'MX-' + String(caseCounter++).padStart(5, '0');

  const punishment = {
    id: caseId,
    playerId: player.id,
    playerName: player.name,
    type: type,
    reason: reason,
    staff: staff,
    duration: duration,
    createdAt: createdAt,
    expiresAt: expiresAt,
    active: active,
    revoked: !!revokedAt,
    revokedAt: revokedAt,
    revokedBy: revokedBy,
    server: randomChoice(['survival', 'skyblock', 'factions', 'global']),
    evidence: Math.random() < 0.3 ? `screenshot_${rand(1000, 9999)}.png` : null,
    notes: Math.random() < 0.2
      ? randomChoice([
          'Player admitted to using hacks in appeal',
          'Multiple reports from trusted players',
          'Caught on recording by staff member',
          'Pattern of behavior, escalated from previous warning',
          'Alt account confirmed via IP match',
          'Reviewed chat logs before issuing',
          'Player was uncooperative during freeze',
          'Evidence submitted via Discord ticket',
          'Automatic detection - confirmed by staff review'
        ])
      : null
  };

  if (active && (type === 'BAN' || type === 'MUTE')) {
    player.activePunishments.push(caseId);
  }

  PUNISHMENTS.push(punishment);
}

// Sort by date, newest first
PUNISHMENTS.sort((a, b) => b.createdAt - a.createdAt);

// ─── Generate Chat Messages (3000+) ────────────────────────────────────────

const CHAT_LOG = [];

// Generate messages spread over 7 days with realistic density
// More messages during "peak hours" simulation
for (let i = 0; i < 3200; i++) {
  const player = PLAYERS[rand(0, PLAYERS.length)];
  const message = CHAT_MESSAGES[rand(0, CHAT_MESSAGES.length)];

  // Cluster timestamps to create realistic activity patterns
  let timestamp;
  if (i < 400) {
    // Recent messages (last 6 hours) - high density
    timestamp = recentTimestamp(6);
  } else if (i < 1200) {
    // Today's messages
    timestamp = recentTimestamp(24);
  } else if (i < 2200) {
    // Last 3 days
    timestamp = randomTimestamp(3);
  } else {
    // Last 7 days
    timestamp = randomTimestamp(7);
  }

  CHAT_LOG.push({
    id: 'msg-' + String(i + 1).padStart(6, '0'),
    t: timestamp,
    player: player.name,
    playerId: player.id,
    message: message,
    server: player.server || 'survival',
    channel: weightedChoice(
      ['global', 'global', 'local', 'party', 'staff', 'private'],
      [45, 20, 15, 10, 5, 5]
    )
  });
}

// Sort newest first
CHAT_LOG.sort((a, b) => b.t - a.t);

// ─── Generate AutoMod Events (120+) ────────────────────────────────────────

const AUTOMOD_EVENTS = [];

for (let i = 0; i < 130; i++) {
  const player = PLAYERS[rand(0, PLAYERS.length)];
  const rule = AUTOMOD_RULES[rand(0, AUTOMOD_RULES.length)];
  const triggered = AUTOMOD_TRIGGERED_MESSAGES[rand(0, AUTOMOD_TRIGGERED_MESSAGES.length)];

  // Decide action taken
  const actionTaken = weightedChoice(
    ['blocked', 'warned', 'muted', 'flagged', 'logged'],
    [40, 25, 15, 10, 10]
  );

  AUTOMOD_EVENTS.push({
    id: 'am-' + String(i + 1).padStart(5, '0'),
    t: randomTimestamp(14),
    player: player.name,
    playerId: player.id,
    rule: rule.name,
    ruleDesc: rule.desc,
    severity: rule.severity,
    message: triggered,
    action: actionTaken,
    server: player.server || 'survival'
  });
}

AUTOMOD_EVENTS.sort((a, b) => b.t - a.t);

// ─── Generate Activity Log (500+) ──────────────────────────────────────────

const ACTIVITY = [];

for (let i = 0; i < 520; i++) {
  const template = ACTIVITY_TEMPLATES[rand(0, ACTIVITY_TEMPLATES.length)];
  const actor = STAFF_NAMES[rand(0, STAFF_NAMES.length)];
  const target = PLAYERS[rand(0, PLAYERS.length)];

  // Don't have AutoMod do non-automod things
  const finalActor = (template.kind === 'admin' && actor === 'AutoMod')
    ? STAFF_NAMES[rand(0, STAFF_NAMES.length - 1)]
    : actor;

  const actionStr = template.action.replace('{target}', target.name);

  // Spread activity over 30 days with more recent entries
  let timestamp;
  if (i < 100) {
    timestamp = recentTimestamp(24);
  } else if (i < 250) {
    timestamp = randomTimestamp(7);
  } else {
    timestamp = randomTimestamp(30);
  }

  ACTIVITY.push({
    id: 'act-' + String(i + 1).padStart(5, '0'),
    t: timestamp,
    actor: finalActor,
    actorRank: STAFF_RANKS[finalActor] || 'Staff',
    action: actionStr,
    target: target.name,
    targetId: target.id,
    kind: template.kind,
    detail: template.detail,
    server: randomChoice(['survival', 'skyblock', 'factions', 'global'])
  });
}

ACTIVITY.sort((a, b) => b.t - a.t);

// ─── Generate Watchlist (12 players) ────────────────────────────────────────

const WATCHLIST_REASONS = [
  'Suspected alt of banned player xXCheater_123Xx',
  'Multiple reports of kill aura, investigating',
  'Unusually fast mining speed, possible X-Ray',
  'Been joining and leaving rapidly, possible bot',
  'Trading large amounts of items, possible duping',
  'Chat behavior escalating, one more offense = mute',
  'New account with suspicious play patterns',
  'IP matches previously banned player',
  'Reported by 3 different players for harassment',
  'Inconsistent ping spikes during PvP encounters',
  'Found near multiple grief sites, needs monitoring',
  'Claims to have exploits, monitoring behavior',
  'Association with known rule-breakers',
  'Excessive /report usage, may be abusing system',
  'Possible VPN detected, verifying account legitimacy'
];

const watchlistCandidates = PLAYERS.filter(p => p.flags >= 2).slice(0, 20);
const WATCHLIST = [];

for (let i = 0; i < Math.min(12, watchlistCandidates.length); i++) {
  const player = watchlistCandidates[i];
  const addedBy = STAFF_NAMES[rand(0, STAFF_NAMES.length - 1)]; // Not AutoMod

  WATCHLIST.push({
    id: 'wl-' + String(i + 1).padStart(3, '0'),
    playerId: player.id,
    playerName: player.name,
    reason: WATCHLIST_REASONS[i] || randomChoice(WATCHLIST_REASONS),
    addedBy: addedBy,
    addedAt: randomTimestamp(14),
    priority: weightedChoice(['high', 'medium', 'low'], [20, 50, 30]),
    notes: Math.random() < 0.5
      ? randomChoice([
          'Check during peak hours when player is most active',
          'Coordinate with other staff for simultaneous observation',
          'Review recent chat logs before taking action',
          'Player has been cooperative so far, continue monitoring',
          'May need to escalate if behavior continues',
          'Check IP history for alt accounts'
        ])
      : null,
    lastChecked: randomTimestamp(3),
    lastCheckedBy: randomChoice(STAFF_NAMES.filter(s => s !== 'AutoMod'))
  });
}

WATCHLIST.sort((a, b) => {
  const prio = { high: 0, medium: 1, low: 2 };
  return (prio[a.priority] || 1) - (prio[b.priority] || 1);
});

// ─── Server Stats (for dashboard) ──────────────────────────────────────────

const SERVER_STATS = {
  name: 'CraftHaven Network',
  onlinePlayers: PLAYERS.filter(p => p.status === 'online').length,
  afkPlayers: PLAYERS.filter(p => p.status === 'afk').length,
  maxPlayers: 200,
  tps: randomFloat(19.2, 20.0).toFixed(1),
  uptime: rand(3, 45) * 86400000, // 3-45 days
  totalUniquePlayers: PLAYERS.length + rand(800, 2000),
  activeBans: PUNISHMENTS.filter(p => p.type === 'BAN' && p.active).length,
  activeMutes: PUNISHMENTS.filter(p => p.type === 'MUTE' && p.active).length,
  warningsToday: PUNISHMENTS.filter(p => p.type === 'WARN' && p.createdAt > Date.now() - 86400000).length,
  automodBlocksToday: AUTOMOD_EVENTS.filter(e => e.t > Date.now() - 86400000).length,
  version: '1.20.4',
  software: 'Paper'
};

// ─── Export ─────────────────────────────────────────────────────────────────

window.DEMO_DATA = {
  players: PLAYERS,
  playerMap: PLAYER_MAP,
  punishments: PUNISHMENTS,
  chatMessages: CHAT_LOG,
  automodEvents: AUTOMOD_EVENTS,
  activity: ACTIVITY,
  watchlist: WATCHLIST,
  serverStats: SERVER_STATS,
  staffNames: STAFF_NAMES,
  staffRanks: STAFF_RANKS,
  generateRandomToken: generateRandomToken
};

console.log('[ModereX] Data loaded:', {
  players: PLAYERS.length,
  punishments: PUNISHMENTS.length,
  chatMessages: CHAT_LOG.length,
  automodEvents: AUTOMOD_EVENTS.length,
  activityLog: ACTIVITY.length,
  watchlist: WATCHLIST.length,
  online: SERVER_STATS.onlinePlayers,
  afk: SERVER_STATS.afkPlayers
});
