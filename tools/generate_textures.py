#!/usr/bin/env python3
"""
ModereX Custom Texture Generator
Generates 32x32 Minecraft-style textures for GUI items
"""

from PIL import Image, ImageDraw, ImageFilter
import os
import json
import hashlib

# Output directory
OUTPUT_DIR = "../app/src/main/resources/resourcepack"
TEXTURES_DIR = f"{OUTPUT_DIR}/assets/moderex/textures/item"
MODELS_DIR = f"{OUTPUT_DIR}/assets/moderex/models/item"
MINECRAFT_MODELS_DIR = f"{OUTPUT_DIR}/assets/minecraft/models/item"

# Minecraft color palette (approximation)
COLORS = {
    'black': (25, 25, 25),
    'dark_gray': (63, 63, 63),
    'gray': (127, 127, 127),
    'light_gray': (191, 191, 191),
    'white': (255, 255, 255),
    'red': (176, 46, 38),
    'dark_red': (114, 28, 24),
    'orange': (216, 127, 51),
    'yellow': (229, 199, 48),
    'lime': (128, 199, 31),
    'green': (84, 138, 57),
    'dark_green': (55, 90, 37),
    'cyan': (22, 156, 156),
    'light_blue': (58, 179, 218),
    'blue': (55, 93, 198),
    'dark_blue': (37, 49, 146),
    'purple': (137, 50, 184),
    'magenta': (189, 68, 179),
    'pink': (243, 139, 170),
    'brown': (131, 84, 50),
    'gold': (249, 185, 56),
    'diamond': (92, 219, 213),
}

def add_noise(img, intensity=15):
    """Add subtle noise to make texture look more Minecraft-like"""
    import random
    pixels = img.load()
    width, height = img.size
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            if a > 0:
                noise = random.randint(-intensity, intensity)
                r = max(0, min(255, r + noise))
                g = max(0, min(255, g + noise))
                b = max(0, min(255, b + noise))
                pixels[x, y] = (r, g, b, a)
    return img

def add_shading(img, light_dir='top_left'):
    """Add simple shading to give depth"""
    pixels = img.load()
    width, height = img.size
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            if a > 0:
                # Simple gradient based on position
                if light_dir == 'top_left':
                    factor = 1.0 - (x + y) / (width + height) * 0.3
                else:
                    factor = 0.7 + (x + y) / (width + height) * 0.3
                r = int(min(255, r * factor))
                g = int(min(255, g * factor))
                b = int(min(255, b * factor))
                pixels[x, y] = (r, g, b, a)
    return img

def create_base_icon(size=32):
    """Create a base transparent image"""
    return Image.new('RGBA', (size, size), (0, 0, 0, 0))

def draw_rounded_rect(draw, xy, radius, fill, outline=None):
    """Draw a rounded rectangle"""
    x1, y1, x2, y2 = xy
    draw.rectangle([x1 + radius, y1, x2 - radius, y2], fill=fill)
    draw.rectangle([x1, y1 + radius, x2, y2 - radius], fill=fill)
    draw.ellipse([x1, y1, x1 + radius * 2, y1 + radius * 2], fill=fill)
    draw.ellipse([x2 - radius * 2, y1, x2, y1 + radius * 2], fill=fill)
    draw.ellipse([x1, y2 - radius * 2, x1 + radius * 2, y2], fill=fill)
    draw.ellipse([x2 - radius * 2, y2 - radius * 2, x2, y2], fill=fill)

# ==================== ICON GENERATORS ====================

def gen_shield_icon(color_name='blue'):
    """Shield icon for protection/moderation"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    color = COLORS.get(color_name, COLORS['blue'])
    dark_color = tuple(max(0, c - 40) for c in color)

    # Shield shape
    points = [(16, 2), (28, 6), (28, 18), (16, 30), (4, 18), (4, 6)]
    draw.polygon(points, fill=color)

    # Inner highlight
    points_inner = [(16, 5), (25, 8), (25, 16), (16, 26), (7, 16), (7, 8)]
    draw.polygon(points_inner, fill=dark_color)

    # Center emblem (M for ModereX)
    draw.line([(10, 20), (10, 12), (16, 16), (22, 12), (22, 20)], fill=COLORS['gold'], width=2)

    return add_noise(img, 10)

def gen_sword_icon(color_name='gray'):
    """Sword icon for combat/anticheat"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    blade = COLORS.get(color_name, COLORS['gray'])
    handle = COLORS['brown']
    guard = COLORS['gold']

    # Blade
    draw.polygon([(14, 2), (18, 2), (20, 20), (12, 20)], fill=blade)
    draw.polygon([(15, 3), (17, 3), (18, 18), (14, 18)], fill=COLORS['white'])  # highlight

    # Guard
    draw.rectangle([8, 20, 24, 23], fill=guard)

    # Handle
    draw.rectangle([14, 23, 18, 30], fill=handle)

    return add_noise(img, 8)

def gen_eye_icon():
    """Eye icon for watchlist"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Eye white
    draw.ellipse([4, 10, 28, 22], fill=COLORS['white'])

    # Iris
    draw.ellipse([11, 11, 21, 21], fill=COLORS['light_blue'])

    # Pupil
    draw.ellipse([14, 13, 18, 19], fill=COLORS['black'])

    # Highlight
    draw.ellipse([15, 14, 17, 16], fill=COLORS['white'])

    # Eyelids
    draw.arc([4, 6, 28, 26], 180, 360, fill=COLORS['dark_gray'], width=2)
    draw.arc([4, 6, 28, 26], 0, 180, fill=COLORS['dark_gray'], width=2)

    return add_noise(img, 5)

def gen_bell_icon():
    """Bell icon for notifications"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    color = COLORS['gold']

    # Bell body
    draw.ellipse([8, 8, 24, 24], fill=color)
    draw.rectangle([10, 16, 22, 26], fill=color)

    # Bell rim
    draw.rectangle([6, 24, 26, 28], fill=color)

    # Clapper
    draw.ellipse([14, 26, 18, 30], fill=COLORS['dark_gray'])

    # Top
    draw.rectangle([14, 4, 18, 10], fill=color)
    draw.ellipse([13, 2, 19, 6], fill=color)

    return add_noise(add_shading(img), 8)

def gen_gavel_icon():
    """Gavel icon for punishments"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    wood = COLORS['brown']
    metal = COLORS['gray']

    # Handle
    draw.polygon([(4, 28), (8, 24), (22, 10), (18, 6)], fill=wood)

    # Head
    draw.ellipse([16, 2, 30, 14], fill=wood)
    draw.ellipse([18, 4, 28, 12], fill=tuple(c + 20 for c in wood))

    # Metal bands
    draw.arc([17, 3, 29, 13], 0, 360, fill=metal, width=1)

    return add_noise(img, 10)

def gen_scroll_icon():
    """Scroll icon for logs/history"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    paper = COLORS['white']
    roll = (235, 220, 180)

    # Main scroll body
    draw.rectangle([6, 6, 26, 26], fill=paper)

    # Top roll
    draw.ellipse([4, 2, 28, 10], fill=roll)
    draw.rectangle([6, 4, 26, 8], fill=paper)

    # Bottom roll
    draw.ellipse([4, 22, 28, 30], fill=roll)
    draw.rectangle([6, 24, 26, 28], fill=paper)

    # Text lines
    for y in [10, 14, 18]:
        draw.line([(10, y), (22, y)], fill=COLORS['dark_gray'], width=1)

    return add_noise(img, 5)

def gen_gear_icon():
    """Gear icon for settings"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    color = COLORS['gray']

    # Outer gear teeth
    for i in range(8):
        import math
        angle = i * 45 * math.pi / 180
        x1 = 16 + int(10 * math.cos(angle))
        y1 = 16 + int(10 * math.sin(angle))
        x2 = 16 + int(14 * math.cos(angle))
        y2 = 16 + int(14 * math.sin(angle))
        draw.line([(x1, y1), (x2, y2)], fill=color, width=4)

    # Center circle
    draw.ellipse([8, 8, 24, 24], fill=color)
    draw.ellipse([12, 12, 20, 20], fill=COLORS['dark_gray'])

    return add_noise(img, 8)

def gen_lightning_icon():
    """Lightning bolt for speed/timer checks"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    color = COLORS['yellow']

    # Lightning bolt shape
    points = [(18, 2), (8, 16), (14, 16), (10, 30), (24, 14), (18, 14), (22, 2)]
    draw.polygon(points, fill=color)

    # Inner highlight
    points_inner = [(17, 5), (11, 15), (15, 15), (12, 26), (21, 15), (17, 15)]
    draw.polygon(points_inner, fill=COLORS['white'])

    return add_noise(img, 8)

def gen_checkmark_icon():
    """Checkmark for enabled/success"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Green background circle
    draw.ellipse([2, 2, 30, 30], fill=COLORS['green'])
    draw.ellipse([4, 4, 28, 28], fill=COLORS['lime'])

    # White checkmark
    draw.line([(8, 16), (14, 22), (24, 10)], fill=COLORS['white'], width=3)

    return add_noise(img, 6)

def gen_x_icon():
    """X mark for disabled/error"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Red background circle
    draw.ellipse([2, 2, 30, 30], fill=COLORS['dark_red'])
    draw.ellipse([4, 4, 28, 28], fill=COLORS['red'])

    # White X
    draw.line([(10, 10), (22, 22)], fill=COLORS['white'], width=3)
    draw.line([(22, 10), (10, 22)], fill=COLORS['white'], width=3)

    return add_noise(img, 6)

def gen_warning_icon():
    """Warning triangle"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Yellow triangle
    points = [(16, 2), (30, 28), (2, 28)]
    draw.polygon(points, fill=COLORS['yellow'])

    # Orange border
    draw.polygon(points, outline=COLORS['orange'], width=2)

    # Exclamation mark
    draw.rectangle([14, 10, 18, 20], fill=COLORS['black'])
    draw.ellipse([14, 22, 18, 26], fill=COLORS['black'])

    return add_noise(img, 6)

def gen_user_icon():
    """User/player icon"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Head
    draw.ellipse([10, 4, 22, 16], fill=COLORS['light_gray'])

    # Body
    draw.ellipse([6, 16, 26, 32], fill=COLORS['light_blue'])

    # Face features
    draw.ellipse([12, 8, 14, 10], fill=COLORS['dark_gray'])  # left eye
    draw.ellipse([18, 8, 20, 10], fill=COLORS['dark_gray'])  # right eye

    return add_noise(img, 6)

def gen_staff_icon():
    """Staff member icon with crown"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Crown
    draw.polygon([(8, 8), (10, 4), (12, 8), (16, 2), (20, 8), (22, 4), (24, 8), (24, 12), (8, 12)], fill=COLORS['gold'])

    # Head
    draw.ellipse([10, 12, 22, 24], fill=COLORS['light_gray'])

    # Body (darker to show authority)
    draw.ellipse([6, 22, 26, 34], fill=COLORS['purple'])

    return add_noise(img, 6)

def gen_combat_icon():
    """Crossed swords for combat category"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    blade = COLORS['light_gray']
    handle = COLORS['brown']

    # First sword (left to right)
    draw.line([(4, 4), (28, 28)], fill=blade, width=3)
    draw.ellipse([2, 2, 8, 8], fill=handle)

    # Second sword (right to left)
    draw.line([(28, 4), (4, 28)], fill=blade, width=3)
    draw.ellipse([24, 2, 30, 8], fill=handle)

    return add_noise(img, 8)

def gen_movement_icon():
    """Running figure for movement category"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    color = COLORS['cyan']

    # Head
    draw.ellipse([12, 2, 20, 10], fill=color)

    # Body
    draw.line([(16, 10), (16, 20)], fill=color, width=3)

    # Arms (running pose)
    draw.line([(16, 12), (8, 18)], fill=color, width=2)
    draw.line([(16, 12), (24, 8)], fill=color, width=2)

    # Legs (running pose)
    draw.line([(16, 20), (8, 28)], fill=color, width=2)
    draw.line([(16, 20), (26, 26)], fill=color, width=2)

    return add_noise(img, 6)

def gen_packet_icon():
    """Network packet icon for packet checks"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Envelope shape
    draw.rectangle([4, 8, 28, 24], fill=COLORS['light_gray'])
    draw.polygon([(4, 8), (16, 18), (28, 8)], fill=COLORS['white'])
    draw.line([(4, 8), (16, 18), (28, 8)], fill=COLORS['gray'], width=1)

    # Binary dots
    for i, (x, y) in enumerate([(8, 26), (12, 26), (16, 26), (20, 26), (24, 26)]):
        col = COLORS['green'] if i % 2 == 0 else COLORS['red']
        draw.ellipse([x-1, y-1, x+1, y+1], fill=col)

    return add_noise(img, 5)

def gen_world_icon():
    """Globe for world interaction category"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Globe
    draw.ellipse([4, 4, 28, 28], fill=COLORS['light_blue'])

    # Continents (simplified)
    draw.ellipse([8, 8, 16, 16], fill=COLORS['green'])
    draw.ellipse([16, 14, 24, 22], fill=COLORS['green'])
    draw.ellipse([10, 18, 18, 26], fill=COLORS['green'])

    # Grid lines
    draw.arc([4, 4, 28, 28], 0, 360, fill=COLORS['dark_gray'], width=1)
    draw.line([(16, 4), (16, 28)], fill=COLORS['dark_gray'], width=1)
    draw.line([(4, 16), (28, 16)], fill=COLORS['dark_gray'], width=1)

    return add_noise(img, 5)

def gen_misc_icon():
    """Puzzle piece for misc category"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)
    color = COLORS['purple']

    # Main piece
    draw.rectangle([6, 10, 26, 26], fill=color)

    # Top tab
    draw.ellipse([12, 2, 20, 14], fill=color)

    # Right notch (cut out)
    draw.ellipse([22, 12, 30, 20], fill=(0, 0, 0, 0))

    return add_noise(img, 8)

def gen_arrow_right():
    """Right arrow for navigation"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Arrow
    draw.polygon([(8, 8), (24, 16), (8, 24), (12, 16)], fill=COLORS['lime'])

    return add_noise(img, 5)

def gen_arrow_left():
    """Left arrow for navigation"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Arrow
    draw.polygon([(24, 8), (8, 16), (24, 24), (20, 16)], fill=COLORS['lime'])

    return add_noise(img, 5)

def gen_preset_icon():
    """Preset/template icon"""
    img = create_base_icon()
    draw = ImageDraw.Draw(img)

    # Stack of papers
    draw.rectangle([8, 6, 26, 24], fill=COLORS['light_gray'])
    draw.rectangle([6, 8, 24, 26], fill=COLORS['white'])
    draw.rectangle([4, 10, 22, 28], fill=COLORS['white'])

    # Checkmarks on top paper
    draw.line([(8, 14), (10, 16), (14, 12)], fill=COLORS['green'], width=2)
    draw.line([(8, 20), (10, 22), (14, 18)], fill=COLORS['green'], width=2)

    return add_noise(img, 5)

# ==================== MAIN GENERATION ====================

ICONS = {
    # Core icons
    'moderex_logo': (gen_shield_icon, {'color_name': 'purple'}),
    'shield_blue': (gen_shield_icon, {'color_name': 'blue'}),
    'shield_green': (gen_shield_icon, {'color_name': 'green'}),
    'shield_red': (gen_shield_icon, {'color_name': 'red'}),

    # Action icons
    'sword': (gen_sword_icon, {}),
    'gavel': (gen_gavel_icon, {}),
    'bell': (gen_bell_icon, {}),
    'scroll': (gen_scroll_icon, {}),
    'gear': (gen_gear_icon, {}),
    'eye': (gen_eye_icon, {}),

    # Status icons
    'checkmark': (gen_checkmark_icon, {}),
    'x_mark': (gen_x_icon, {}),
    'warning': (gen_warning_icon, {}),
    'lightning': (gen_lightning_icon, {}),

    # User icons
    'user': (gen_user_icon, {}),
    'staff': (gen_staff_icon, {}),

    # Category icons
    'combat': (gen_combat_icon, {}),
    'movement': (gen_movement_icon, {}),
    'packet': (gen_packet_icon, {}),
    'world': (gen_world_icon, {}),
    'misc': (gen_misc_icon, {}),

    # Navigation
    'arrow_right': (gen_arrow_right, {}),
    'arrow_left': (gen_arrow_left, {}),
    'preset': (gen_preset_icon, {}),
}

# Custom model data mapping (starting from 100000 to avoid conflicts)
CUSTOM_MODEL_DATA_START = 100000

def create_directories():
    """Create necessary directories"""
    os.makedirs(TEXTURES_DIR, exist_ok=True)
    os.makedirs(MODELS_DIR, exist_ok=True)
    os.makedirs(MINECRAFT_MODELS_DIR, exist_ok=True)

def generate_pack_mcmeta():
    """Generate pack.mcmeta for different MC versions"""
    # Pack format versions:
    # 15 = 1.20-1.20.1
    # 18 = 1.20.2
    # 22 = 1.20.3-1.20.4
    # 32 = 1.20.5-1.20.6
    # 34 = 1.21-1.21.1
    # 42 = 1.21.2+

    pack_meta = {
        "pack": {
            "pack_format": 34,  # 1.21-1.21.1
            "supported_formats": [15, 42],  # 1.20 to 1.21.2+
            "description": "ModereX Custom GUI Textures"
        }
    }

    with open(f"{OUTPUT_DIR}/pack.mcmeta", 'w') as f:
        json.dump(pack_meta, f, indent=2)

    print("Generated pack.mcmeta")

def generate_model_json(icon_name, cmd):
    """Generate model JSON for an icon"""
    model = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": f"moderex:item/{icon_name}"
        }
    }

    with open(f"{MODELS_DIR}/{icon_name}.json", 'w') as f:
        json.dump(model, f, indent=2)

def generate_paper_override():
    """Generate paper item model with custom model data overrides"""
    overrides = []

    for i, icon_name in enumerate(ICONS.keys()):
        cmd = CUSTOM_MODEL_DATA_START + i
        overrides.append({
            "predicate": {"custom_model_data": cmd},
            "model": f"moderex:item/{icon_name}"
        })

    model = {
        "parent": "minecraft:item/generated",
        "textures": {
            "layer0": "minecraft:item/paper"
        },
        "overrides": overrides
    }

    with open(f"{MINECRAFT_MODELS_DIR}/paper.json", 'w') as f:
        json.dump(model, f, indent=2)

    print(f"Generated paper.json with {len(overrides)} overrides")

def generate_java_constants():
    """Generate Java constants file for custom model data"""
    java_content = """package com.blockforge.moderex.resourcepack;

/**
 * Custom model data constants for ModereX GUI textures.
 * Auto-generated by generate_textures.py
 */
public final class GuiTextures {
    private GuiTextures() {}

"""

    for i, icon_name in enumerate(ICONS.keys()):
        cmd = CUSTOM_MODEL_DATA_START + i
        const_name = icon_name.upper()
        java_content += f"    public static final int {const_name} = {cmd};\n"

    java_content += "}\n"

    # Write to Java source
    java_dir = "../app/src/main/java/com/blockforge/moderex/resourcepack"
    os.makedirs(java_dir, exist_ok=True)

    with open(f"{java_dir}/GuiTextures.java", 'w') as f:
        f.write(java_content)

    print("Generated GuiTextures.java")

def main():
    print("ModereX Texture Generator")
    print("=" * 40)

    create_directories()

    # Generate textures
    print("\nGenerating textures...")
    for icon_name, (generator, kwargs) in ICONS.items():
        img = generator(**kwargs)
        img.save(f"{TEXTURES_DIR}/{icon_name}.png")
        print(f"  Generated {icon_name}.png")

    # Generate models
    print("\nGenerating models...")
    for i, icon_name in enumerate(ICONS.keys()):
        cmd = CUSTOM_MODEL_DATA_START + i
        generate_model_json(icon_name, cmd)

    # Generate pack.mcmeta
    generate_pack_mcmeta()

    # Generate paper override model
    generate_paper_override()

    # Generate Java constants
    generate_java_constants()

    print("\n" + "=" * 40)
    print("Done! Generated:")
    print(f"  - {len(ICONS)} textures")
    print(f"  - {len(ICONS)} model files")
    print(f"  - pack.mcmeta")
    print(f"  - paper.json (overrides)")
    print(f"  - GuiTextures.java")
    print(f"\nCustom model data range: {CUSTOM_MODEL_DATA_START} - {CUSTOM_MODEL_DATA_START + len(ICONS) - 1}")

if __name__ == "__main__":
    main()
