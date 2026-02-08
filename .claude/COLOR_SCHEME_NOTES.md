# ModereX Web Panel - Color Scheme Documentation

## HSL-Based Theming System

The panel uses a **hue-based theming system** where changing a single CSS variable (`--theme-h`) shifts the entire color scheme - backgrounds, borders, text, and primary colors all adapt together.

### Theme Hue Presets
| Color | Hue Value |
|-------|-----------|
| Blue (default) | `215` |
| Green | `145` |
| Teal | `175` |
| Purple | `265` |
| Red | `0` |
| Orange | `25` |

To change the theme, set `--theme-h` in CSS or via JavaScript:
```javascript
document.documentElement.style.setProperty('--theme-h', 145); // Green theme
```

---

## CSS Variables (Root Level)

### Theme Control
| Variable | Default | Purpose |
|----------|---------|---------|
| `--theme-h` | `215` | Master hue - controls entire color scheme |

### Derived Background Colors (from theme hue)
| Variable | Formula | Purpose |
|----------|---------|---------|
| `--bg0` | `hsl(--theme-h, 25%, 2%)` | Darkest background |
| `--bg1` | `hsl(--theme-h, 30%, 5%)` | Primary dark background |
| `--bg2` | `hsl(--theme-h, 38%, 8%)` | Secondary dark background |
| `--surface` | `hsl(--theme-h, 42%, 11%)` | Surface/card background |
| `--surface2` | `hsl(--theme-h, 32%, 6%)` | Alternative surface |

### Derived Border Colors
| Variable | Formula | Purpose |
|----------|---------|---------|
| `--border` | `hsl(--theme-h, 42%, 18%)` | Default border color |
| `--border-light` | `hsl(--theme-h, 38%, 23%)` | Lighter border (hover states) |

### Derived Text Colors
| Variable | Formula | Purpose |
|----------|---------|---------|
| `--text` | `hsl(--theme-h, 60%, 97%)` | Primary text color |
| `--text-secondary` | `hsl(--theme-h, 30%, 75%)` | Secondary/subdued text |
| `--muted` | `hsl(--theme-h, 25%, 54%)` | Muted text |
| `--muted2` | `hsl(--theme-h, 24%, 38%)` | More muted text |

### Primary Color (derived from theme hue)
| Variable | Formula/Value | Purpose |
|----------|---------------|---------|
| `--primary` | `hsl(--theme-h, 83%, 55%)` | Primary action color |
| `--primary-light` | `hsl(--theme-h, 100%, 68%)` | Lighter primary |
| `--primary-dark` | `hsl(--theme-h, 78%, 43%)` | Darker primary |
| `--primary-rgb` | Calculated | RGB for opacity variants |
| `--primary-light-rgb` | Calculated | RGB for light variant opacity |
| `--primary-glow` | `rgba(--primary-rgb, 0.35)` | Glow effect |

### Accent Colors (Purple - independent of theme)
| Variable | Value | Purpose |
|----------|-------|---------|
| `--accent-rgb` | `124, 92, 255` | RGB values for opacity variants |
| `--accent` | `#7c5cff` | Accent color |
| `--accent-light` | `#9d82ff` | Lighter accent |

### Status Colors (Fixed - not theme-dependent)
| Variable | Value | Purpose |
|----------|-------|---------|
| `--ok-rgb` | `16, 185, 129` | RGB for success opacity variants |
| `--ok` | `#10b981` | Success/online state |
| `--ok-glow` | `rgba(--ok-rgb, 0.30)` | Success glow |
| `--warn-rgb` | `245, 158, 11` | RGB for warning opacity variants |
| `--warn` | `#f59e0b` | Warning state |
| `--warn-glow` | `rgba(--warn-rgb, 0.30)` | Warning glow |
| `--bad-rgb` | `239, 68, 68` | RGB for error opacity variants |
| `--bad` | `#ef4444` | Error/danger state |
| `--bad-glow` | `rgba(--bad-rgb, 0.30)` | Error glow |

### Glass Effects (derived from theme)
| Variable | Formula | Purpose |
|----------|---------|---------|
| `--glass` | `hsla(--theme-h, 42%, 11%, 0.85)` | Glass background |
| `--glass2` | `hsla(--theme-h, 32%, 6%, 0.75)` | Alternative glass |

### Shadows (neutral - not theme-dependent)
| Variable | Value | Purpose |
|----------|-------|---------|
| `--shadow-sm` | `0 4px 12px rgba(0, 0, 0, 0.25)` | Small shadow |
| `--shadow` | `0 12px 40px rgba(0, 0, 0, 0.45)` | Medium shadow |
| `--shadow-lg` | `0 24px 60px rgba(0, 0, 0, 0.55)` | Large shadow |

---

## How Theme Changes Work

When a user selects a theme color (via color picker or preset), the JavaScript:

1. Converts the hex color to HSL
2. Sets `--theme-h` to the hue value
3. Sets `--primary-rgb` and `--primary-light-rgb` for opacity variants
4. All HSL-based CSS variables automatically recalculate

### Example: Switching to Green Theme

```javascript
// User picks #10b981 (green)
setThemeColor('#10b981');

// This sets:
// --theme-h: 160
// --primary: hsl(160, 83%, 55%)
// --bg1: hsl(160, 30%, 5%)  // Now green-tinted dark
// --border: hsl(160, 42%, 18%)  // Now green-tinted
// --muted: hsl(160, 25%, 54%)  // Now green-tinted
// etc.
```

---

## Color Opacity Patterns

For elements needing the primary color with opacity, use `rgba(var(--primary-rgb), X)`:

| Opacity | Purpose |
|---------|---------|
| 0.05-0.08 | Very subtle backgrounds |
| 0.10-0.15 | Subtle backgrounds/borders |
| 0.20-0.25 | Medium backgrounds |
| 0.30-0.40 | Strong backgrounds/borders |
| 0.45-0.60 | Glows and shadows |

---

## Implementation Notes

### Files Modified
- `styles.css` - All CSS variables now use HSL with `--theme-h`
- `app.js` - `setThemeColor()` and `applyThemeFromState()` set `--theme-h`

### Best Practices
1. Never hardcode hex colors for themed elements
2. Use `var(--bg1)`, `var(--border)`, etc. instead of hex values
3. For opacity variants, use `rgba(var(--primary-rgb), 0.XX)`
4. Status colors (ok, warn, bad) remain fixed for semantic meaning
