# ModereX Web Panel - Color Scheme Documentation

## CSS Variables (Root Level)

### Background Colors
| Variable | Value | Purpose |
|----------|-------|---------|
| `--bg0` | `#030508` | Darkest background |
| `--bg1` | `#080c14` | Primary dark background |
| `--bg2` | `#0c1220` | Secondary dark background |
| `--surface` | `#101828` | Surface/card background |
| `--surface2` | `#0a1018` | Alternative surface |

### Border Colors
| Variable | Value | Purpose |
|----------|-------|---------|
| `--border` | `#1a2744` | Default border color |
| `--border-light` | `#243352` | Lighter border (hover states) |

### Text Colors
| Variable | Value | Purpose |
|----------|-------|---------|
| `--text` | `#f0f4ff` | Primary text color |
| `--text-secondary` | `#a8b8d8` | Secondary/subdued text |
| `--muted` | `#6b7fa8` | Muted text |
| `--muted2` | `#4a5c7a` | More muted text |

### Primary Theme Colors (Blue)
| Variable | Value | Purpose |
|----------|-------|---------|
| `--primary-rgb` | `45, 122, 237` | RGB values for opacity variants |
| `--primary` | `#2d7aed` | Primary action color |
| `--primary-light-rgb` | `90, 156, 255` | RGB values for lighter primary opacity variants |
| `--primary-light` | `#5a9cff` | Lighter primary |
| `--primary-dark` | `#1a5cc0` | Darker primary |
| `--primary-glow` | `rgba(var(--primary-rgb), 0.35)` | Glow effect |

### Accent Colors (Purple)
| Variable | Value | Purpose |
|----------|-------|---------|
| `--accent` | `#7c5cff` | Accent color |
| `--accent-light` | `#9d82ff` | Lighter accent |

### Status Colors
| Variable | Value | Purpose |
|----------|-------|---------|
| `--ok` | `#10b981` | Success/online state |
| `--ok-glow` | `rgba(16, 185, 129, 0.30)` | Success glow |
| `--warn` | `#f59e0b` | Warning state |
| `--warn-glow` | `rgba(245, 158, 11, 0.30)` | Warning glow |
| `--bad` | `#ef4444` | Error/danger state |
| `--bad-glow` | `rgba(239, 68, 68, 0.30)` | Error glow |

### Special Effects
| Variable | Value | Purpose |
|----------|-------|---------|
| `--watchlist-glow` | Complex | Orange glow for watchlist alerts |
| `--line-glow` | `rgba(90, 156, 255, 0.22)` | Blue glow for lines |

### Shadows
| Variable | Value | Purpose |
|----------|-------|---------|
| `--shadow-sm` | `0 4px 12px rgba(0, 0, 0, 0.25)` | Small shadow |
| `--shadow` | `0 12px 40px rgba(0, 0, 0, 0.45)` | Medium shadow |
| `--shadow-lg` | `0 24px 60px rgba(0, 0, 0, 0.55)` | Large shadow |

### Glass Effects
| Variable | Value | Purpose |
|----------|-------|---------|
| `--glass` | `rgba(16, 24, 40, 0.85)` | Glass background |
| `--glass2` | `rgba(8, 12, 20, 0.75)` | Alternative glass |

---

## Hardcoded Colors to Fix

The following hardcoded `rgba(45, 122, 237, ...)` instances need to be converted to use CSS variables for proper theming:

### High Priority (Visible UI Elements)

1. **Body Background Gradient** (line 94)
   - `rgba(45, 122, 237, 0.18)` - Should use `var(--primary)` with opacity

2. **Aurora Background** (line 137)
   - `rgba(45, 122, 237, 0.28)` - Decorative, use variable

3. **Sidebar Gradient Overlay** (line 213)
   - `rgba(45, 122, 237, 0.12)` - Use variable

4. **Logo Image Shadow** (line 247)
   - `rgba(45, 122, 237, 0.45)` - Use primary-glow variant

5. **Active Sidebar Item** (lines 350-351)
   - Background and border - Use variables

6. **Chip Info State** (lines 719-720)
   - Border and background - Use variables

7. **Button Primary States** (multiple locations)
   - Various hover/focus states - Use variables

### Color Opacity Patterns

When the primary color is used with opacity, follow these patterns:

| Opacity | Purpose | Suggested Variable |
|---------|---------|-------------------|
| 0.05-0.08 | Very subtle backgrounds | `--primary-subtle` |
| 0.10-0.15 | Subtle backgrounds/borders | `--primary-light-bg` |
| 0.20-0.25 | Medium backgrounds | `--primary-medium-bg` |
| 0.30-0.40 | Strong backgrounds/borders | `--primary-strong-bg` |
| 0.45-0.60 | Glows and shadows | Use `--primary-glow` |

### Proposed New Variables

```css
:root {
  /* Primary with varying opacity for theming */
  --primary-rgb: 45, 122, 237;  /* Base RGB for opacity variations */

  /* Or explicit opacity variants */
  --primary-5: rgba(var(--primary-rgb), 0.05);
  --primary-8: rgba(var(--primary-rgb), 0.08);
  --primary-10: rgba(var(--primary-rgb), 0.10);
  --primary-12: rgba(var(--primary-rgb), 0.12);
  --primary-15: rgba(var(--primary-rgb), 0.15);
  --primary-18: rgba(var(--primary-rgb), 0.18);
  --primary-20: rgba(var(--primary-rgb), 0.20);
  --primary-25: rgba(var(--primary-rgb), 0.25);
  --primary-28: rgba(var(--primary-rgb), 0.28);
  --primary-30: rgba(var(--primary-rgb), 0.30);
  --primary-35: rgba(var(--primary-rgb), 0.35);
  --primary-40: rgba(var(--primary-rgb), 0.40);
  --primary-45: rgba(var(--primary-rgb), 0.45);
  --primary-50: rgba(var(--primary-rgb), 0.50);
  --primary-60: rgba(var(--primary-rgb), 0.60);
}
```

---

## Shadow vs Darker Color Guidelines

When converting hardcoded colors:

1. **True shadows** (depth/elevation) - Keep as `box-shadow` with black/transparent
2. **Colored backgrounds** - Use the themed color with appropriate opacity
3. **Borders** - Use themed color, typically 25-50% opacity
4. **Glows** - Use themed color with 30-60% opacity

### Pattern Recognition

- If it's `box-shadow: 0 Xpx Ypx rgba(...)` with black - it's a true shadow
- If it's `background: rgba(45, 122, 237, X)` - it's a themed background
- If it's `border-color: rgba(45, 122, 237, X)` - it's a themed border

---

## Implementation Notes

To make the panel fully themeable:

1. Add `--primary-rgb` variable as `45, 122, 237`
2. Replace all `rgba(45, 122, 237, X)` with `rgba(var(--primary-rgb), X)`
3. This allows changing theme by only modifying `--primary-rgb`

Alternative approach:
1. Create explicit opacity variants as CSS variables
2. Replace hardcoded values with the appropriate variable
3. More verbose but clearer intent
